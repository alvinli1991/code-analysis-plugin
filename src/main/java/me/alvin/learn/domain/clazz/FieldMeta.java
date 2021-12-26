package me.alvin.learn.domain.clazz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.intellij.psi.PsiField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.alvin.learn.domain.context.DataTypeFactory;

import java.util.Objects;

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
public class FieldMeta {

    public FieldMeta(PsiField psiField) {
        if (Objects.isNull(psiField)) {
            throw new IllegalArgumentException("input is null");
        }
        this.psiField = psiField;
        this.type = DataTypeFactory.getDataTypeMeta(psiField.getType()).orElse(null);
        this.name = psiField.getName();
        this.referenceName = psiField.getContainingClass().getQualifiedName() + "#" + this.name;
    }

    //region meta
    /**
     * 字段类型
     */
    private DataTypeMeta type;

    /**
     * 字段名
     */
    private String name;

    /**
     * 存储引用名，包含全类名
     */
    private String referenceName;

    //endregion

    //region idea meta
    /**
     * idea解析出的字段引用
     */
    @JsonIgnore
    private PsiField psiField;
    //endregion


    //region 辅助信息

    /**
     * TODO 是否是常量
     *
     * @return
     */
    public boolean isFinal() {
        return false;
    }
    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldMeta fieldMeta = (FieldMeta) o;
        return Objects.equals(getType(), fieldMeta.getType()) && Objects.equals(getReferenceName(), fieldMeta.getReferenceName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getReferenceName());
    }
}
