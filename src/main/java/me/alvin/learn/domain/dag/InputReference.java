package me.alvin.learn.domain.dag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import me.alvin.learn.domain.clazz.MethodMeta;

import java.util.Objects;

/**
 * 对action输入的引用
 * 1. 目前仅解析action内部引用了input的地方
 *
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 4:13 PM
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InputReference {
    /**
     * 如果是被方法引用，请参考refMethod字段
     * 如果是其它，请直接参考codeBlock
     */
    @Getter
    @Setter
    private InputReferenceType InputReferenceType;

    /**
     * 引用input的代码块
     */
    @Getter
    @Setter
    private CodeBlock codeBlock;

    /**
     * 如果是方法引用，及{@link InputReferenceType#METHOD_CALL}，存储方法信息
     */
    @Getter
    @Setter
    @JsonIgnore
    private MethodMeta refMethod;

    /**
     * 此引用是否在action内部
     * 如果此引用是action以外类的方法，此值为false
     */
    @Getter
    @Setter
    private boolean actionInner;

    /**
     * 在action内的引用层级，例如直接引用是1层，作为某方法的入参后在此方法内部则层数加1
     */
    @Getter
    @Setter
    private int innerRefLevel;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InputReference that = (InputReference) o;
        return innerRefLevel == that.innerRefLevel && InputReferenceType == that.InputReferenceType && Objects.equals(codeBlock, that.codeBlock) && Objects.equals(refMethod, that.refMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(InputReferenceType.name(), codeBlock, refMethod, innerRefLevel);
    }
}
