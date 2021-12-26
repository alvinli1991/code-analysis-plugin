package me.alvin.learn.domain.dag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.intellij.psi.PsiReferenceExpression;
import lombok.Getter;
import lombok.Setter;
import me.alvin.learn.domain.clazz.DataTypeMeta;
import me.alvin.learn.domain.clazz.FieldMeta;
import me.alvin.learn.domain.clazz.MethodMeta;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 表示action输入的类
 * 记录输入的
 * 1.来源
 * 2.数据类型
 * 3.被使用的地方
 *
 *
 *
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 4:08 PM
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Input {
    //region input的核心

    /**
     * input的核心字段
     */
    @Getter
    @Setter
    private InputCore inputCore;

    //endregion

    //region 描述input的来源和引用
    /**
     * 是否有被变量存储
     */
    @Getter
    @Setter
    private boolean hasVarHolder;

    /**
     * 被存储到的变量名，如果hasVarHolder为false，此值为空
     */
    @Getter
    @Setter
    private Set<String> varNameHolders;

    /**
     * TODO 待确认放在此处是否合适
     * 输入源类型
     */
    @Getter
    @Setter
    private Set<InputSrcType> inputSrcTypes;

    /**
     * 引入此输入的代码块
     */
    @Getter
    @Setter
    private Set<CodeBlock> inputSrcCodeBlocks;

    /**
     * 引入输入的表达式
     */
    @Getter
    @Setter
    @JsonIgnore
    private Set<PsiReferenceExpression> inputSrcExpressions;

    /**
     * 使用此输入的引用
     */
    @Getter
    @Setter
    private Set<InputReference> inputReferences;
    //endregion
    /**
     * 关联的子输入
     * 例如for循环语句，将输入分拆为内部内容后再引用
     */
    @Getter
    @Setter
    private Set<Input> relateChildInputs;

    public Input(InputCore inputCore) {
        this.inputCore = inputCore;
        this.varNameHolders = new HashSet<>();
        this.inputSrcExpressions = new HashSet<>();
        this.inputSrcCodeBlocks = new HashSet<>();
        this.inputReferences = new HashSet<>();
        this.relateChildInputs = new HashSet<>();
    }

    public boolean addCodeBlock(CodeBlock codeBlock) {
        return this.getInputSrcCodeBlocks().add(codeBlock);
    }

    public boolean addInputRef(InputReference inputReference) {
        return this.getInputReferences().add(inputReference);
    }

    public boolean addChildInput(Input input) {
        return getRelateChildInputs().add(input);
    }

    /**
     * 输入的核心及什么可以完备的表示输入
     * 输入的本源
     * 1. 可以是一个字段
     * 2. 是一个方法的计算值
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class InputCore{
        /**
         * TODO 待确认放在此处是否合适
         * 输入源类型
         */
        @Getter
        @Setter
        private InputSrcType inputSrcType;

        /**
         * 输入的数据类型
         */
        @Getter
        @Setter
        private DataTypeMeta srcDataType;

        /**
         * 输入内容所属成员变量，如果存在且可以找到的话
         * 1. 当inputType为{@link InputSrcType#CLASS_METHOD}时，可能有值
         * 2. 当inputType为{@link InputSrcType#CLASS_FIELD}时，一定有值
         *
         */
        @Getter
        @Setter
        private FieldMeta srcField;

        /**
         * 引入输入的方法，当inputType为{@link InputSrcType#CLASS_METHOD}时有值
         * 获取一个内容
         */
        @Getter
        @Setter
        private MethodMeta srcInputMethod;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            InputCore inputCore = (InputCore) o;
            return inputSrcType == inputCore.inputSrcType && Objects.equals(srcDataType, inputCore.srcDataType) && Objects.equals(srcField, inputCore.srcField) && Objects.equals(srcInputMethod, inputCore.srcInputMethod);
        }

        @Override
        public int hashCode() {
            return Objects.hash(inputSrcType, srcDataType, srcField, srcInputMethod);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Input input = (Input) o;
        return Objects.equals(getInputCore(), input.getInputCore());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInputCore());
    }
}
