package me.alvin.learn.domain.clazz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.alvin.learn.domain.context.ClassFactory;
import me.alvin.learn.domain.context.FieldFactory;
import me.alvin.learn.domain.context.MethodFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 2:10 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClassMeta {

    public ClassMeta(PsiClass psiClass) {
        if (Objects.isNull(psiClass)) {
            throw new IllegalArgumentException("input is null");
        }
        this.psiClass = psiClass;
        this.qualifiedName = psiClass.getQualifiedName();
        this.className = psiClass.getName();

        this.allMethodMetaMap = Stream.of(psiClass.getAllMethods())
                .filter(method -> !ClassFactory.getObjectClass().equals(method.getContainingClass()))
                .map(MethodFactory::getMethodMeta)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(MethodMeta::getPsiMethod, Function.identity(), (existing, replacement) -> existing));
        this.allFieldMetaMap = Stream.of(psiClass.getAllFields())
                .map(FieldFactory::getFieldMeta)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(FieldMeta::getPsiField, Function.identity(), (existing, replacement) -> existing));

        this.selfMethodMetaMap = Stream.of(psiClass.getMethods())
                .map(MethodFactory::getMethodMeta)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(MethodMeta::getPsiMethod, Function.identity(), (existing, replacement) -> existing));
        this.selfFieldMetaMap = Stream.of(psiClass.getFields())
                .map(FieldFactory::getFieldMeta)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(FieldMeta::getPsiField, Function.identity(), (existing, replacement) -> existing));

    }

    //region meta
    /**
     * class全名
     */
    private String qualifiedName;

    /**
     * 仅class名
     */
    private String className;

    /**
     * 类自身的方法映射
     */
    private Map<PsiMethod, MethodMeta> selfMethodMetaMap;

    /**
     * 类自身的字段映射
     */
    private Map<PsiField, FieldMeta> selfFieldMetaMap;

    /**
     * 包含父类的方法映射
     */
    private Map<PsiMethod, MethodMeta> allMethodMetaMap;

    /**
     * 包含父类的字段映射
     */
    private Map<PsiField, FieldMeta> allFieldMetaMap;
    //endregion


    //region idea meta
    /**
     * idea解析出的类引用
     */
    @JsonIgnore
    private PsiClass psiClass;
    //endregion

    public Optional<MethodMeta> getMethodMeta(PsiMethod psiMethod) {
        return Optional.ofNullable(allMethodMetaMap.getOrDefault(psiMethod, null));
    }

    public Optional<FieldMeta> getFieldMeta(PsiField psiField) {
        return Optional.ofNullable(allFieldMetaMap.getOrDefault(psiField, null));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassMeta classMeta = (ClassMeta) o;
        return Objects.equals(qualifiedName, classMeta.qualifiedName) && Objects.equals(allMethodMetaMap, classMeta.allMethodMetaMap) && Objects.equals(allFieldMetaMap, classMeta.allFieldMetaMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, allMethodMetaMap, allFieldMetaMap);
    }
}
