package me.alvin.learn.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import me.alvin.learn.domain.context.DataTypeFactory;
import me.alvin.learn.domain.context.FieldFactory;
import me.alvin.learn.domain.context.MethodFactory;
import me.alvin.learn.domain.dag.*;
import me.alvin.learn.utils.visitor.IsReferenceExpressionFinalFieldVisitor;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Li Xiang
 * Date: 2021/12/28
 * Time: 3:44 PM
 */
public class ActionUtils {

    //region 解析入口是常量的input信息
    /**
     * 解析action类中常量的input信息
     *
     * @param actionClass
     * @param document
     * @param javaPsiFacade
     * @return
     */
    public static Set<Input> parseConstantInputForActionClass(PsiClass actionClass, final Document document, JavaPsiFacade javaPsiFacade) {
        Set<PsiReferenceExpression> constantExpressions = MyPsiUtil.getAllOuterConstantExpressions(actionClass, javaPsiFacade);
        Map<Input.InputCore, Set<PsiReferenceExpression>> input2ExpressionMap = constantExpressions.stream()
                .map(expression -> Pair.of(parseInputCoreForConstant(expression), expression))
                .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toSet())));

        return input2ExpressionMap.entrySet().stream()
                .map(entry -> {
                    Set<InputReference> inputReferences = entry.getValue()
                            .stream()
                            .map(expression -> findInputReferenceForConstant(document, expression))
                            .flatMap(Collection::stream)
                            .collect(Collectors.toSet());
                    Set<CodeBlock> inputSrcCodeBlocks = entry.getValue()
                            .stream()
                            .map(expression -> extractSelfCodeBlock(document, expression))
                            .collect(Collectors.toSet());
                    Input input = new Input(entry.getKey());
                    input.setInputReferences(inputReferences);
                    input.setInputSrcCodeBlocks(inputSrcCodeBlocks);
                    return input;
                }).collect(Collectors.toSet());
    }

    /**
     * 从常量中解析出input的核心
     *
     * @param constantExpression
     * @return
     */
    public static Input.InputCore parseInputCoreForConstant(PsiReferenceExpression constantExpression) {
        Input.InputCore inputCore = new Input.InputCore();
        inputCore.setInputSrcType(InputSrcType.CLASS_FIELD);
        PsiField refPsiField = (PsiField) constantExpression.resolve();
        FieldFactory.getFieldMeta(refPsiField).ifPresent(inputCore::setSrcField);
        DataTypeFactory.getDataTypeMeta(refPsiField.getType()).ifPresent(inputCore::setSrcDataType);
        return inputCore;
    }


    /**
     * 找到使用常量输入的地方
     * <p>
     * TODO 目前未没考虑间接本地变量
     *
     * @param constantExpression
     * @return
     */
    public static Set<InputReference> findInputReferenceForConstant(Document document, PsiReferenceExpression constantExpression) {
        Set<InputReference> result = new HashSet<>();
        //
        PsiMethodCallExpression methodCallExpression = PsiTreeUtil.getParentOfType(constantExpression, PsiMethodCallExpression.class);
        if (Objects.nonNull(methodCallExpression)) {
            InputReference inputReference = new InputReference();
            inputReference.setInputReferenceType(InputReferenceType.METHOD_CALL);
            inputReference.setActionInner(true);

            PsiMethod originMethod = (PsiMethod) methodCallExpression.getMethodExpression().resolve();
            MethodFactory.getMethodMeta(originMethod).ifPresent(inputReference::setRefMethod);

            inputReference.setCodeBlock(extractSelfCodeBlock(document, methodCallExpression));

            result.add(inputReference);
        }

        PsiIfStatement ifStatement = PsiTreeUtil.getParentOfType(constantExpression, PsiIfStatement.class);
        if (Objects.nonNull(ifStatement)) {
            InputReference inputReference = new InputReference();
            inputReference.setInputReferenceType(InputReferenceType.IF_STATEMENT);
            inputReference.setActionInner(true);

            inputReference.setCodeBlock(extractSelfCodeBlock(document, ifStatement));

            result.add(inputReference);
        }

        PsiConditionalExpression conditionExpression = PsiTreeUtil.getParentOfType(constantExpression, PsiConditionalExpression.class);
        if (Objects.nonNull(conditionExpression)) {
            InputReference inputReference = new InputReference();
            inputReference.setInputReferenceType(InputReferenceType.CONDITIONAL);
            inputReference.setActionInner(true);

            inputReference.setCodeBlock(extractSelfCodeBlock(document, conditionExpression));

            result.add(inputReference);
        }

        PsiBinaryExpression binaryExpression = PsiTreeUtil.getParentOfType(constantExpression, PsiBinaryExpression.class);
        if (Objects.nonNull(binaryExpression)) {
            InputReference inputReference = new InputReference();
            inputReference.setInputReferenceType(InputReferenceType.BINARY);
            inputReference.setActionInner(true);

            inputReference.setCodeBlock(extractSelfCodeBlock(document, binaryExpression));

            result.add(inputReference);
        }
        return result;
    }
    //endregion

    /**
     * 提取element的代码块
     *
     * @param document
     * @param psiElement
     * @return
     */
    public static CodeBlock extractSelfCodeBlock(Document document, PsiElement psiElement) {
        int lineNumber = MyPsiUtil.getLineNumber(document, psiElement.getTextOffset());
        String code = MyPsiUtil.extractPsiElementCode(psiElement);
        return new CodeBlock(lineNumber, code);
    }




}
