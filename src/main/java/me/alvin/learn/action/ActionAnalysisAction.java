package me.alvin.learn.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import me.alvin.learn.domain.context.ClassFactory;
import me.alvin.learn.domain.dag.Action;
import me.alvin.learn.domain.dag.Input;
import me.alvin.learn.utils.ActionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author: Li Xiang
 * Date: 2021/12/28
 * Time: 11:13 AM
 */
public class ActionAnalysisAction extends AnAction {
    private static final Logger LOGGER = Logger.getInstance(ActionAnalysisAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        final GlobalSearchScope allScope = GlobalSearchScope.allScope(project);
        Action theAction = new Action("test");
        PsiClass theClass = JavaPsiFacade.getInstance(project).findClass("me.alvin.test.TestAction", allScope);
        theAction.setActionClassMeta(ClassFactory.getClassMeta(theClass).orElse(null));

        PsiField[] psiFields = theClass.getFields();
        PsiMethod[] psiMethods = theClass.getMethods();


        //TODO 解析出此Action的input

        //TODO 解析出这些input的引用


    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(false);
    }

    public Set<Input> extractInputFromAction(Action action, Project project) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);

        PsiClass theClass = action.getActionClassMeta().getPsiClass();
        PsiFile containingFile = theClass.getContainingFile();
        FileViewProvider fileViewProvider = containingFile.getViewProvider();
        Document document = fileViewProvider.getDocument();

        //静态常量入参入口
        Set<Input> constantInputs = ActionUtils.parseConstantInputForActionClass(theClass, document, javaPsiFacade);

        //函数context入参入口

        //注入bean入口


        return null;
    }
}
