package me.alvin.learn.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import me.alvin.learn.utils.visitor.IsReferenceExpressionFinalFieldVisitor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: Li Xiang
 * Date: 2021/12/27
 * Time: 3:23 PM
 */
public class MyPsiUtil {
    private static final Logger LOGGER = Logger.getInstance(MyPsiUtil.class);
    private static final Set<String> EXCLUDE_FIELD_DATA_TYPE_PKG_PREFIX = new HashSet<>();

    static{
        EXCLUDE_FIELD_DATA_TYPE_PKG_PREFIX.add("java");
        EXCLUDE_FIELD_DATA_TYPE_PKG_PREFIX.add("org.slf4j");
    }

    /**
     * 找到一个类中所有的外部常量表达式
     *
     * @param clz
     * @param javaPsiFacade
     * @return
     */
    public static Set<PsiReferenceExpression> getAllOuterConstantExpressions(PsiClass clz, JavaPsiFacade javaPsiFacade) {
        return PsiTreeUtil.findChildrenOfType(clz, PsiReferenceExpression.class)
                .stream()
                .filter(MyPsiUtil::isFinalFieldExpression)
                .filter(expression ->{
                    PsiElement psiElement = expression.resolve();
                    if(!(psiElement instanceof PsiField)){
                        return false;
                    }
                    PsiField srcField = (PsiField) psiElement;
                    PsiClass containingClass = srcField.getContainingClass();
                    if(Objects.isNull(containingClass)){
                        return false;
                    }
                    String containingClassQualifiedName = containingClass.getQualifiedName();
                    if(StringUtils.isBlank(containingClassQualifiedName)){
                        return false;
                    }
                    for(String excludePrefix:EXCLUDE_FIELD_DATA_TYPE_PKG_PREFIX){
                        if(containingClassQualifiedName.startsWith(excludePrefix)){
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toSet());
    }


    /**
     * 获取代码行数
     *
     * @param document
     * @param offset
     * @return
     */
    public static int getLineNumber(Document document, int offset) {
        if (Objects.isNull(document)) {
            return -1;
        }
        return document.getLineNumber(offset) + 1;
    }

    /**
     * 获取自身的代码文本
     * 1. 如果是if语句，只取if条件
     *
     * @param psiElement
     * @return
     */
    public static String extractPsiElementCode(PsiElement psiElement) {
        if (Objects.isNull(psiElement)) {
            return StringUtils.EMPTY;
        }
        if (psiElement instanceof PsiIfStatement) {
            PsiIfStatement ifElement = (PsiIfStatement) psiElement;
            PsiExpression condition = ifElement.getCondition();
            if (Objects.nonNull(condition)) {
                return "if(" + condition.getText() + ")";
            } else {
                return StringUtils.EMPTY;
            }
        }
        return psiElement.getText();
    }

    public static boolean isFinalFieldExpression(@NotNull PsiExpression expression){
        IsReferenceExpressionFinalFieldVisitor visitor = new IsReferenceExpressionFinalFieldVisitor();
        expression.accept(visitor);
        return visitor.isMyIsFinalField();
    }

}
