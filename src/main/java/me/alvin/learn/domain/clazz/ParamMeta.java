package me.alvin.learn.domain.clazz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.intellij.psi.PsiParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.alvin.learn.domain.context.DataTypeFactory;

import java.util.Objects;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 2:31 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ParamMeta {

    public ParamMeta(PsiParameter psiParameter) {
        if (Objects.isNull(psiParameter)) {
            throw new IllegalArgumentException("input is null");
        }
        this.psiParameter = psiParameter;
        this.type = DataTypeFactory.getDataTypeMeta(psiParameter.getType()).orElse(null);
        this.name = psiParameter.getName();
    }

    //region meta
    /**
     * 返回类型
     */
    private DataTypeMeta type;
    /**
     * 参数名
     */
    private String name;
    //endregion

    //region idea meta
    /**
     * idea解析出的参数引用
     */
    @JsonIgnore
    private PsiParameter psiParameter;
    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamMeta paramMeta = (ParamMeta) o;
        return Objects.equals(type, paramMeta.type) && Objects.equals(name, paramMeta.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }
}
