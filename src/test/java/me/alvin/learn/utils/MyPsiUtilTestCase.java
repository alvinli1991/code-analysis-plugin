package me.alvin.learn.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import me.alvin.base.MyJavaCodePsiParseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author: Li Xiang
 * Date: 2021/12/27
 * Time: 4:24 PM
 */
public class MyPsiUtilTestCase extends MyJavaCodePsiParseTestCase {
    private static final Logger LOGGER = Logger.getInstance(MyPsiUtilTestCase.class);

    public void testGetAllOuterConstantExpressions() {
        PsiClass theClass = myFixture.findClass("me.alvin.test.TestActionForConstantInput");

        Set<PsiReferenceExpression> result = MyPsiUtil.getAllOuterConstantExpressions(theClass, javaPsiFacade);

        Set<String> expectNames = new HashSet<>();
        expectNames.add("TestConstant.KEY_1");
        expectNames.add("A");
        expectNames.add("TestConstant.MAP_CONSTANT");

        assertThat(result.size(), equalTo(6));
        assertThat(result.stream().map(PsiReferenceExpression::getText).collect(Collectors.toSet()), equalTo(expectNames));
    }

    public void testFindConstantExpressionDataType() {
        PsiClass theClass = myFixture.findClass("me.alvin.test.TestAction");
        Set<PsiReferenceExpression> result = MyPsiUtil.getAllOuterConstantExpressions(theClass, javaPsiFacade);
        PsiFile containingFile = theClass.getContainingFile();
        FileViewProvider fileViewProvider = containingFile.getViewProvider();
        Document document = fileViewProvider.getDocument();
        result.forEach(expression -> {
            System.out.println(expression.getText() + ":" + MyPsiUtil.getLineNumber(document, expression.getTextOffset()));

            PsiMethodCallExpression methodCallExpression = PsiTreeUtil.getParentOfType(expression, PsiMethodCallExpression.class);
            if (null != methodCallExpression) {
                System.out.println("parent:" + methodCallExpression.getText());

                PsiElement methodElement = methodCallExpression.getMethodExpression().resolve();
                System.out.println(methodElement.getText());
            }

            PsiMethodCallExpression topMostMethodCallExpression = PsiTreeUtil.getTopmostParentOfType(expression, PsiMethodCallExpression.class);
            if(null != topMostMethodCallExpression){
                System.out.println("topMostParent" + topMostMethodCallExpression.getText());
            }

            PsiIfStatement topMostIfParentType = PsiTreeUtil.getTopmostParentOfType(expression, PsiIfStatement.class);
            if (Objects.nonNull(topMostIfParentType)) {
                System.out.println("if:" + topMostIfParentType.getText());
                System.out.println("condition:" + topMostIfParentType.getCondition().getText());
            }
            System.out.println();
        });

//        result.stream().forEach();

//        result.stream()
//                .map(PsiReferenceExpression::resolve)
//                .forEach(psiElement -> {
//                    if (psiElement instanceof PsiField) {
//                        PsiField theField = (PsiField) psiElement;
//                        System.out.println(psiElement.getText() + ":" + theField.getType());
//                    }
//                });
    }
}