package me.alvin.learn.domain.dag;

/**
 * 表示引用输入的地方
 * 1. 可能还有除if，for之外的语句
 * 2. 目前使用java stream的可能分析不到
 *
 *
 * @author: Li Xiang
 * Date: 2021/12/25
 * Time: 9:47 PM
 */
public enum InputReferenceType {

    /**
     * 未知，不在下面具体化的方式之列
     */
    UNKNOWN,
    /**
     * PsiMethodCallExpression
     * 被方法引用，此种情况比较好分析
     */
    METHOD_CALL,

    /**
     * PsiBinaryExpression
     * 二目运算
     */
    BINARY,

    /**
     * PsiConditionalExpression
     * 三目条件运算
     */
    CONDITIONAL,
    /**
     * PsiIfStatement
     * 在if的判断语句内，此种情况比较好分析
     */
    IF_STATEMENT,

    /**
     * PsiForeachStatement
     * 为for循环所用，不好分析
     */
    FOR_EACH_STATEMENT,
}
