package me.alvin.learn.action;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.PsiTreeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.alvin.learn.ui.toolWindow.ClassFieldInfoMdWindowFactory;
import net.steppschuh.markdowngenerator.table.Table;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.alvin.learn.utils.MyPsiUtil.EXCLUDE_FIELD_DATA_TYPE_PKG_PREFIX;

/**
 * @author: Li Xiang
 * Date: 2022/1/6
 * Time: 5:27 PM
 */
public class ClassFieldInfoToMarkdownAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (null == project) {
            return;
        }
        List<PsiClass> clzs = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
            return Stream.of(((PsiJavaFile) psiFile).getClasses())
                    .collect(Collectors.toList());
        });

        List<FieldInfo> fieldInfos = clzs.stream()
                .flatMap(clz -> Arrays.stream(clz.getAllFields()))
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
                    FieldInfo.FieldInfoBuilder fieldBuilder = FieldInfo.builder();
                    fieldBuilder.fieldName(field.getName())
                            .dataType(field.getType().getPresentableText());
                    String comment = "";
                    //星型
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
                    //普通注释
                    if (StringUtils.isBlank(comment)) {
                        PsiComment psiComment = PsiTreeUtil.findChildOfType(field, PsiComment.class);
                        if (null != psiComment) {
                            comment = psiComment.getText();
                        }
                    }
                    //@FieldDoc注解
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

        Table.Builder tableBuilder = new Table.Builder().withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow("field", "type", "comment");
        fieldInfos.forEach(fieldInfo -> {
            tableBuilder.addRow(fieldInfo.getFieldName(), fieldInfo.getDataType(), fieldInfo.getComment());
        });
        String mdContent = tableBuilder.build().toString();

        ClassFieldInfoMdWindowFactory.ProjectService projectService = project.getService(ClassFieldInfoMdWindowFactory.ProjectService.class);
        projectService.getFieldInfoWindow().showToolWindow();
        projectService.getFieldInfoWindow().refreshFieldInfoMd(mdContent);

//        JBTextArea myTextArea = new JBTextArea(mdContent);
//        JComponent myPanel = new JPanel();
//        myPanel.add(myTextArea);
//        JBPopupFactory.getInstance().createComponentPopupBuilder(myPanel, myTextArea).createPopup().showInBestPositionFor(event.getData(PlatformDataKeys.EDITOR));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Project project = event.getProject();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        boolean isEnable = project != null && psiFile != null && (psiFile.getLanguage().is(JavaLanguage.INSTANCE));
        event.getPresentation().setEnabledAndVisible(isEnable);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class FieldInfo {
        private String fieldName;
        private String dataType;
        private String comment;
    }
}
