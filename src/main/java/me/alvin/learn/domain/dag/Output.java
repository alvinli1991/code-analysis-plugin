package me.alvin.learn.domain.dag;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import me.alvin.learn.domain.clazz.DataTypeMeta;
import me.alvin.learn.domain.clazz.FieldMeta;

import java.util.Objects;
import java.util.Set;

/**
 * 表示action输出的类
 * 记录输出的
 * 1. 目的地
 * 2. 数据类型
 * 3. 输出的方式
 *
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 4:09 PM
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Output {
    /**
     * 输出的数据类型
     */
    @Getter
    @Setter
    private DataTypeMeta desDataType;

    /**
     * 输出内容归属字段，如果存在且可以找到的话
     */
    @Getter
    @Setter
    private FieldMeta desField;

    /**
     * output被赋值、设置的地方
     */
    @Getter
    @Setter
    private Set<OutputAssign> outputAssigns;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Output output = (Output) o;
        return Objects.equals(desDataType, output.desDataType) && Objects.equals(desField, output.desField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(desDataType, desField);
    }
}
