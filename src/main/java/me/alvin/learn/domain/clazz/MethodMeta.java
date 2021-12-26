package me.alvin.learn.domain.clazz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.intellij.psi.PsiMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.alvin.learn.domain.context.DataTypeFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 2:11 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MethodMeta {
    public MethodMeta(PsiMethod psiMethod) {
        if (Objects.isNull(psiMethod)) {
            throw new IllegalArgumentException("input is null");
        }
        this.psiMethod = psiMethod;
        this.returnType = DataTypeFactory.getDataTypeMeta(psiMethod.getReturnType()).orElse(null);
        this.name = psiMethod.getName();
        if (psiMethod.hasParameters()) {
            this.parameterList = Stream.of(psiMethod.getParameterList().getParameters())
                    .map(ParamMeta::new)
                    .collect(Collectors.toList());
        } else {
            this.parameterList = Collections.emptyList();
        }

    }

    //region meta
    /**
     * 返回类型
     */
    private DataTypeMeta returnType;

    /**
     * 入参列表
     * 顺序为声明的顺序
     */
    private List<ParamMeta> parameterList;

    /**
     * 方法名
     */
    private String name;
    //endregion

    //region idea meta

    /**
     * idea解析出的方法引用
     */
    @JsonIgnore
    private PsiMethod psiMethod;
    //endregion

    //region 辅助信息

    /**
     * TODO 函数是否有返回
     *
     * @return
     */
    public boolean hasReturn() {
        return false;
    }
    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodMeta that = (MethodMeta) o;
        return Objects.equals(returnType, that.returnType) && Objects.equals(parameterList, that.parameterList) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnType, parameterList, name);
    }
}
