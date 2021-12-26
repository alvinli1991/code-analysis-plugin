package me.alvin.learn.action;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.util.mxCellRenderer;
import me.alvin.learn.domain.context.ClassFactory;
import me.alvin.learn.domain.dag.*;
import me.alvin.learn.domain.xml.DagGraph;
import me.alvin.learn.domain.xml.Depends;
import me.alvin.learn.domain.xml.Unit;
import me.alvin.learn.utils.JacksonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO 目前还未处理异常情况，例如某个内容不存在，后续再处理
 * TODO 此实现目前包含耗时的操作，需要重构
 *
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 4:47 PM
 */
public class XmlExtractDagAction extends AnAction {
    private static final Logger LOGGER = Logger.getInstance(XmlExtractDagAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        final GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

        //init
        ClassFactory.setObjectClass(JavaPsiFacade.getInstance(project).findClass("java.lang.Object", allScope));

        //读取dag xml file
        DomManager domManager = DomManager.getDomManager(project);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        XmlFile xmlFile = (XmlFile) psiFile;
        DomFileElement<DagGraph> xmlFileElement = domManager.getFileElement(xmlFile, DagGraph.class);
        if (Objects.isNull(xmlFileElement)) {
            return;
        }
        DagGraph dagGraph = xmlFileElement.getRootElement();

        Dag dagContext = new Dag(xmlFile.getName());

        //解析action元数据
        List<Unit> units = dagGraph.getUnits().getUnits();
        if (CollectionUtils.isEmpty(units)) {
            return;
        }

        Set<Action> actions = units.stream().map(unit -> {
            String fullClassName = unit.getClz().getValue();
            String description = unit.getDescription().getValue();
            String id = unit.getId().getValue();
            PsiClass theClass = JavaPsiFacade.getInstance(project).findClass(fullClassName, allScope);

            Action theAction = new Action(id);
            theAction.setDescription(description);
            theAction.setActionClassMeta(ClassFactory.getClassMeta(theClass).orElse(null));

            //解析input、output
            theAction.setInputs(extractInputs(theAction));
            theAction.setOutputs(extractOutputs(theAction));
            return theAction;
        }).collect(Collectors.toSet());
        dagContext.setActions(actions);
        if (CollectionUtils.isEmpty(actions)) {
            return;
        }


        //解析stage
        List<me.alvin.learn.domain.xml.Stage> xmlStages = dagGraph.getStages().getStages();
        if (CollectionUtils.isEmpty(xmlStages)) {
            return;
        }

        Set<Stage> stages = xmlStages.stream()
                .map(stage -> {
                    String stageId = stage.getStageId().getValue();
                    Stage theStage = new Stage(stageId);

                    // 解析action的关系
                    stage.getFlows().getFlows().forEach(flow -> {
                        Action fromAction = dagContext.getActionMeta(flow.getFrom().getValue()).orElse(null);
                        Action toAction = dagContext.getActionMeta(flow.getTo().getValue()).orElse(null);
                        if (Objects.nonNull(fromAction) && Objects.nonNull(toAction)) {
                            dagContext.addDependency(fromAction, toAction);
                            theStage.addActionRelation(fromAction, toAction);
                        }
                    });

                    return theStage;
                })
                .map(stage -> stage.finishBuild(dagContext.getActionDag()))
                .collect(Collectors.toSet());
        dagContext.setStages(stages);
        if (CollectionUtils.isEmpty(stages)) {
            return;
        }


        //解析stage的关系
        xmlStages.forEach(stage -> {
            Depends depends = stage.getDepends();
            if (Objects.nonNull(depends)) {
                Stage toStage = dagContext.getStageMeta(stage.getStageId().getValue()).orElse(null);
                List<Stage> fromStages = depends.getDepends().stream()
                        .map(depend -> dagContext.getStageMeta(depend.getDependId().getValue()).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (Objects.nonNull(toStage)) {
                    fromStages.forEach(fromStage -> dagContext.addDependency(fromStage, toStage));
                }
            }
        });

        LOGGER.info(JacksonUtils.toJson(dagContext));
        generateActionDagImg(dagContext.getActionDag());

    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Project project = event.getProject();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        boolean isEnable = project != null && psiFile != null && (psiFile.getLanguage().is(XMLLanguage.INSTANCE));
        event.getPresentation().setEnabledAndVisible(isEnable);
    }

    /**
     * TODO 解析action的inputs
     * 输入的出处：
     * 1. process方法的入参：
     *     1. 直接使用入参
     *     1. 调用入参变量有返回值的方法，例如recallContext内部的ads变量
     * 2. 注入的bean：例如通过autowired 注入adData；
     * 3. 静态常量：例如PlatinumExpConstant.PLATINUM_SORT_CUT_SWITCH
     *
     * 输入的使用：
     * 1. 作为某方法的入参，且该方法无返回
     * 2. 例如调用了new构造器，作为了其它bean的内部变量
     * 3. 作为某写方法的入参，且此方法有返回
     *
     * TODO 如何考虑输入的层次，是否需要考虑输入转换为内部变量后又转为其它输入
     *
     * @param action
     * @return
     */
    private Set<Input> extractInputs(Action action) {

        return null;
    }



    /**
     * TODO 解析action的outputs，
     *
     * 1. 调用process入参变量的写方法
     *     1. 直接调用
     *     2. 链式调用
     * 2. 调用save方法中第一个入参的写方法
     *
     * 输出的判断标准
     * 1. 某个方法没有返回
     * 2. 某个方法有返回值但是没有被变量接收或使用
     *
     * TODO 何为写方法?
     *
     * @param output
     * @return
     */
    private Set<Output> extractOutputs(Action output) {
        return null;
    }


    private void generateActionDagImg(Graph<Action, DefaultEdge> actionDag) {
        JGraphXAdapter<Action, DefaultEdge> graphAdapter =
                new JGraphXAdapter<Action, DefaultEdge>(actionDag);
        mxFastOrganicLayout layout = new mxFastOrganicLayout(graphAdapter);
        layout.setDisableEdgeStyle(false);
        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("/Users/alvinli/Downloads/graph.png");
        try {
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

}
