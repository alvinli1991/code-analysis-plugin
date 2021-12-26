package me.alvin.learn.domain.dag;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import me.alvin.learn.domain.clazz.MethodMeta;

import java.util.Objects;

/**
 * output被赋值、设置的地方
 *
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 4:13 PM
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OutputAssign {

    @Getter
    @Setter
    private OutputAssignType outputAssignType;

    /**
     * 设置输出的方法
     */
    @Getter
    @Setter
    private MethodMeta desAssignMethod;

    /**
     * 引入此输入的代码块
     */
    @Getter
    @Setter
    private CodeBlock outputCodeBlock;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OutputAssign that = (OutputAssign) o;
        return outputAssignType == that.outputAssignType && Objects.equals(desAssignMethod, that.desAssignMethod) && Objects.equals(outputCodeBlock, that.outputCodeBlock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputAssignType.name(), desAssignMethod, outputCodeBlock);
    }
}
