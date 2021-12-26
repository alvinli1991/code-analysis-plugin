package me.alvin.learn.utils.visitor;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiFieldImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Li Xiang
 * Date: 2021/12/29
 * Time: 11:30 AM
 */
public class IsReferenceExpressionFinalFieldVisitor extends JavaElementVisitor {
    private boolean myIsFinalField;
    private final Map<PsiVariable, Boolean> varIsConst = new HashMap<>();



    public boolean isMyIsFinalField() {
        return myIsFinalField;
    }

    @Override
    public void visitReferenceExpression(PsiReferenceExpression expression) {
        PsiExpression qualifierExpression = expression.getQualifierExpression();
        if (qualifierExpression != null && !(qualifierExpression instanceof PsiReferenceExpression)) {
            myIsFinalField = false;
            return;
        }
        PsiElement refElement = expression.resolve();
        if (!(refElement instanceof PsiVariable)) {
            myIsFinalField = false;
            return;
        }
        PsiVariable variable = (PsiVariable)refElement;
        Boolean isConst = varIsConst.get(variable);
        if (isConst != null) {
            myIsFinalField &= isConst.booleanValue();
            return;
        }
        if (variable instanceof PsiEnumConstant) {
            myIsFinalField = true;
            varIsConst.put(variable, Boolean.TRUE);
            return;
        }
        varIsConst.put(variable, Boolean.FALSE);
        if (variable.hasModifierProperty(PsiModifier.FINAL)){
            myIsFinalField = true;
            return;
        }
        PsiExpression initializer = PsiFieldImpl.getDetachedInitializer(variable);
        if (initializer == null){
            myIsFinalField = false;
            return;
        }
        initializer.accept(this);
        varIsConst.put(variable, myIsFinalField);
    }
}
