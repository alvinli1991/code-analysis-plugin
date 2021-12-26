package me.alvin.learn.action;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.JavaElementKind;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.Query;
import com.intellij.util.xml.DomManager;
import me.alvin.learn.domain.DagActionClassMeta;
import me.alvin.learn.domain.xml.DagGraph;
import me.alvin.learn.domain.xml.Unit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: Li Xiang
 * Date: 2021/12/9
 * Time: 10:42 AM
 */
public class XmlExtractLearnAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(XmlExtractLearnAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        final GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

        //获取所有Action类名
        DomManager domManager = DomManager.getDomManager(project);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        XmlFile xmlFile = (XmlFile) psiFile;
        DagGraph dagGraph = domManager.getFileElement(xmlFile, DagGraph.class).getRootElement();
        List<Unit> units = dagGraph.getUnits().getUnits();

        List<DagActionClassMeta> classMetas = units.stream().map(unit -> {
            DagActionClassMeta dagClassMeta = new DagActionClassMeta();
            dagClassMeta.setId(unit.getId().getValue());
            dagClassMeta.setFullClassName(unit.getClz().getValue());
            dagClassMeta.setDescription(unit.getDescription().getValue());
            return dagClassMeta;
        }).collect(Collectors.toList());

        //获取该类的psi
        DagActionClassMeta meta = classMetas.get(0);
        PsiClass theClass = JavaPsiFacade.getInstance(project).findClass(meta.getFullClassName(), allScope);
        //获取该类下所有的字段
        PsiField[] psiFields = theClass.getFields();

        List<PsiField> dependServicesFields = Stream.of(psiFields)
                .filter(psiField -> {
                    PsiType type = psiField.getType();
                    if ("org.slf4j.Logger".equals(type.getCanonicalText())) {
                        return false;
                    }

                    if (psiField.hasAnnotation("javax.annotation.Resource")) {
                        return true;
                    }
                    if (psiField.hasAnnotation("org.springframework.beans.factory.annotation.Autowired")) {
                        return true;
                    }
                    return true;
                }).collect(Collectors.toList());

        PsiField theField = dependServicesFields.get(1);
        ReferencesSearch.search(theField.getSourceElement());
        Query<PsiReference> fieldQuery = ReferencesSearch.search(theField);
        fieldQuery.findAll().stream()
                .forEach(ref -> {
                    PsiElement selfElement = ref.getElement();
                    LOG.info("self:" + selfElement.getText() + ":" + JavaElementKind.fromElement(selfElement));
                    LOG.info("parent:" + selfElement.getParent().getLastChild().getText() + ":" + JavaElementKind.fromElement(selfElement.getParent()));
                    LOG.info("parent's parent:" + selfElement.getParent().getParent().getText() + ":" + JavaElementKind.fromElement(selfElement.getParent().getParent()));
                });
        PsiMethod[] psiMethods = theClass.getMethods();
        PsiMethod psiMethod = psiMethods[2];
        //TODO 确定参数类型，进而确定参数的类

        //TODO 找到参数在函数体内的引用

        /**
         * TODO
         * 1. 过滤出仅是参数本身，及无进一步执行引用方法的引用。作为input
         *    1.找到以此引用为入参的方法，则为input的refMethods
         * 2. 配合类信息，过滤出有执行引用方法
         *    1. 只有一级调用（无链式调用），且有返回值的方法。作为input。
         *      1. 找到以此返回为入参的方法，则为input的refMethods
         *    2. 只有一级调用（无链式调用），且方法无返回值的。作为output
         *       1. 如果没入参直接作为output
         *       2. 找出此方法的入参、及其类型
         *    2. 链式调用
         *       1. 找到链式调用的最后一个方法
         *          1. 找到倒数第二个方法的返回类型
         *          2. 如果没入参直接作为output
         *          3. 找出此方法的入参、及其类型
         */
//
//        Query<PsiReference> query =  ReferencesSearch.search(psiMethods[1].getParameterList().getParameter(0).getSourceElement());
//        PsiReference pr = query.findFirst();
//        if(pr.getElement() instanceof PsiReturnStatement){
//
//        }

    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Project project = event.getProject();

        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        boolean isEnable = project != null && psiFile != null && (psiFile.getLanguage().is(XMLLanguage.INSTANCE));


        event.getPresentation().setEnabledAndVisible(isEnable);
        //TODO 所选的文件必须是xml
        //TODO 所选的xml文件的DagGraph节点的xsi:noNamespaceSchemaLocation属性必须包含lpp-dag.xsd
    }
}
