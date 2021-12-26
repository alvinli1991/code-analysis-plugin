package me.alvin.learn.domain.clazz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.intellij.psi.PsiType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 2:13 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DataTypeMeta {

    public DataTypeMeta(PsiType psiType) {
        if (Objects.isNull(psiType)) {
            throw new IllegalArgumentException("input is null");
        }
        this.psiType = psiType;
        this.fullTypeName = psiType.getCanonicalText();
        this.typeName = psiType.getPresentableText();
    }

    //region meta
    /**
     * 包含包名的类型名
     * 基础类型无包名
     */
    private String fullTypeName;

    /**
     * 不含包名的类型名
     * 基础类型无包名
     */
    private String typeName;
    //endregion

    //region idea meta
    /**
     * idea解析出的类型引用
     */
    @JsonIgnore
    private PsiType psiType;
    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataTypeMeta that = (DataTypeMeta) o;
        return Objects.equals(fullTypeName, that.fullTypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullTypeName);
    }
}
