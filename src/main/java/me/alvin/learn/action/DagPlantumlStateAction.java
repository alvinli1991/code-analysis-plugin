package me.alvin.learn.action;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import me.alvin.learn.domain.plantuml.state.domain.State;
import me.alvin.learn.domain.plantuml.state.domain.StateRelation;
import me.alvin.learn.domain.plantuml.state.domain.StateUml;
import me.alvin.learn.domain.xml.DagGraph;
import me.alvin.learn.domain.xml.Depend;
import me.alvin.learn.domain.xml.Stage;
import me.alvin.learn.domain.xml.Unit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: Li Xiang
 * Date: 2022/4/27
 * Time: 1:01 PM
 */
public class DagPlantumlStateAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(DagPlantumlStateAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        final GlobalSearchScope allScope = GlobalSearchScope.allScope(project);
        DomManager domManager = DomManager.getDomManager(project);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        XmlFile xmlFile = (XmlFile) psiFile;
        DomFileElement<DagGraph> dagFile = domManager.getFileElement(xmlFile, DagGraph.class);
        if (Objects.isNull(dagFile)) {
            return;
        }
        DagGraph dagGraph = dagFile.getRootElement();
        List<Unit> units = dagGraph.getUnits().getUnits();
        List<Stage> stages = dagGraph.getStages().getStages();
        if (CollectionUtils.isEmpty(units) || CollectionUtils.isEmpty(stages)) {
            return;
        }

        Map<String, State> actionStates = units.stream()
                .map(unit -> State.builder().name(unit.getId().getValue())
                        .desc(unit.getDescription().getValue())
                        .build())
                .collect(Collectors.toMap(State::getName
                        , Function.identity()
                        , (existing, replacement) -> existing));
        StateUml.StateUmlBuilder stateUmlBuilder = StateUml.builder();

        Map<String, State> stageStates = stages.stream()
                .map(stage -> {
                    State.StateBuilder stageStateBuilder = State.builder();
                    stageStateBuilder.name(stage.getStageId().getValue());
                    Set<String> stageActionStore = new HashSet<>();

                    Optional.ofNullable(stage.getFlows().getFlows())
                            .orElse(Collections.emptyList())
                            .forEach(flow -> {
                                String from = flow.getFrom().getValue();
                                String to = flow.getTo().getValue();
                                if (StringUtils.isNotBlank(from)) {
                                    if (actionStates.containsKey(from) && !stageActionStore.contains(from)) {
                                        stageStateBuilder.child(actionStates.get(from));
                                        stageActionStore.add(from);
                                    }
                                }
                                if (StringUtils.isNotBlank(to)) {
                                    if (actionStates.containsKey(to) && !stageActionStore.contains(to)) {
                                        stageStateBuilder.child(actionStates.get(to));
                                        stageActionStore.add(to);
                                    }
                                }
                                if (StringUtils.isNoneBlank(from, to)) {
                                    if (actionStates.containsKey(from) && actionStates.containsKey(to)) {
                                        StateRelation actionRelation = StateRelation.builder()
                                                .from(actionStates.get(from))
                                                .to(actionStates.get(to))
                                                .build();
                                        stateUmlBuilder.stateRelation(actionRelation);
                                    }
                                }
                            });
                    stateUmlBuilder.state(stageStateBuilder.build());
                    return stageStateBuilder.build();
                })
                .collect(Collectors.toMap(State::getName
                        , Function.identity()
                        , (existing, replacement) -> existing));

        stages.forEach(stage -> {
            List<Depend> stageDepends = stage.getDepends().getDepends();
            if (CollectionUtils.isNotEmpty(stageDepends)) {
                stageDepends.forEach(stageDepend -> {
                    String dependStage = stageDepend.getDependId().getValue();
                    if (stageStates.containsKey(dependStage)) {
                        StateRelation stageRelation = StateRelation.builder()
                                .from(stageStates.get(dependStage))
                                .to(stageStates.get(stage.getStageId().getValue()))
                                .build();
                        stateUmlBuilder.stateRelation(stageRelation);
                    }
                });
            }
        });

        String statePlantUml = stateUmlBuilder.build().toPlantuml();
        ApplicationManager.getApplication().runWriteAction(() -> {
            PsiDirectory xmlFileDirectory = xmlFile.getContainingDirectory();
            String fileName = dagGraph.getDagId().getValue() + ".puml";
            VirtualFile virtualFile = VfsUtil.findRelativeFile(xmlFileDirectory.getVirtualFile(), fileName);
            if (Objects.isNull(virtualFile)) {
                PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
                PsiFile statePlantUmlFile = psiFileFactory.createFileFromText(fileName
                        , PlainTextFileType.INSTANCE, statePlantUml);

                xmlFileDirectory.add(statePlantUmlFile);
            } else {
                Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                document.setText(statePlantUml);
            }
        });
    }

    private static VirtualFile getOrCreatePath(VirtualFile rootDir, String desPath) {
        try {
            return VfsUtil.createDirectoryIfMissing(rootDir, desPath);
        } catch (IOException e) {
            LOG.error(e);
        }
        return null;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Project project = event.getProject();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        boolean isEnable = project != null && psiFile != null && (psiFile.getLanguage().is(XMLLanguage.INSTANCE));
        if(isEnable){
            //??????????????????xml??????
            XmlFile xmlFile = (XmlFile) psiFile;
            DomManager domManager = DomManager.getDomManager(project);
            DomFileElement<DagGraph> dagFile = domManager.getFileElement(xmlFile, DagGraph.class);
            if (Objects.isNull(dagFile)) {
                isEnable = false;
            }
        }
        event.getPresentation().setEnabledAndVisible(isEnable);
    }
}
