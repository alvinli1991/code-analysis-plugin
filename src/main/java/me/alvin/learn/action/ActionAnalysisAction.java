package me.alvin.learn.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import me.alvin.learn.domain.context.ClassFactory;
import me.alvin.learn.domain.dag.Action;
import me.alvin.learn.domain.dag.Input;
import me.alvin.learn.utils.ActionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.alvin.learn.utils.MyPsiUtil.EXCLUDE_FIELD_DATA_TYPE_PKG_PREFIX;

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

        //TODO holder
        Stream.of(psiFields)
                .filter(field -> {
                    PsiClass containingClass = field.getContainingClass();
                    if (containingClass != null) {
                        String containingClassName = containingClass.getQualifiedName();
                        if (StringUtils.isNotBlank(containingClassName)) {
                            for (String excludePrefix : EXCLUDE_FIELD_DATA_TYPE_PKG_PREFIX) {
                                if (containingClassName.startsWith(excludePrefix)) {
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                })
                .map(field -> {
                    ClassFieldInfoToMarkdownAction.FieldInfo.FieldInfoBuilder fieldBuilder = ClassFieldInfoToMarkdownAction.FieldInfo.builder();
                    fieldBuilder.fieldName(field.getName())
                            .dataType(field.getType().getPresentableText());
                    String comment = "";
                    //??????
                    PsiDocComment psiDoc = field.getDocComment();
                    if (null != psiDoc) {
                        comment = PsiTreeUtil.findChildrenOfType(psiDoc, PsiDocToken.class)
                                .stream()
                                .filter(docToken -> docToken.getTokenType() == JavaDocTokenType.DOC_COMMENT_DATA)
                                .map(PsiElement::getText)
                                .filter(StringUtils::isNotBlank)
                                .map(text -> text.replaceAll("\\n", ""))
                                .collect(Collectors.joining("\\"));
                    }
                    //????????????
                    if (StringUtils.isBlank(comment)) {
                        PsiComment psiComment = PsiTreeUtil.findChildOfType(field, PsiComment.class);
                        if (null != psiComment) {
                            comment = psiComment.getText();
                        }
                    }
                    //@FieldDoc??????
                    PsiModifierList modifierList = field.getModifierList();
                    if (null != modifierList) {
                        PsiAnnotation[] annos = modifierList.getAnnotations();
                        if (annos.length > 0) {
                            comment += Arrays.stream(annos).filter(anno -> "FieldDoc".equalsIgnoreCase(anno.getNameReferenceElement().getText()))
                                    .map(PsiAnnotation::getParameterList)
                                    .map(PsiAnnotationParameterList::getAttributes)
                                    .filter(Objects::nonNull)
                                    .flatMap(Arrays::stream)
                                    .filter(pair -> "description".equalsIgnoreCase(pair.getName()) || "example".equalsIgnoreCase(pair.getName()))
                                    .map(pair -> pair.getName() + ":" + pair.getValue().getText())
                                    .collect(Collectors.joining("&"));
                        }
                    }

                    fieldBuilder.comment(comment);
                    return fieldBuilder.build();
                }).collect(Collectors.toList());

        //TODO ????????????Action???input

        //TODO ???????????????input?????????


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

        //????????????????????????
        Set<Input> constantInputs = ActionUtils.parseConstantInputForActionClass(theClass, document, javaPsiFacade);

        //??????context????????????

        //??????bean??????


        return null;
    }
}
