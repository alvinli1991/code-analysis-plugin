package me.alvin.learn.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: Li Xiang
 * Date: 2021/12/7
 * Time: 10:45 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DagActionClassMeta {
    private String id;
    private String fullClassName;
    private String description;
    private boolean async;
    private ActionType actionType;
    private List<DependService> dependServices;
    private List<Input> inputs;
    private List<Output> outputs;

    @JsonIgnore
    private PsiClass psiClass;
    @JsonIgnore
    private List<PsiField> dependServiceFields;
}
