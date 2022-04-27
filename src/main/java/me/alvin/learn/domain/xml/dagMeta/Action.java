package me.alvin.learn.domain.xml.dagMeta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.alvin.learn.domain.StageType;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author: Li Xiang
 * Date: 2022/4/21
 * Time: 7:37 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "clz", "description", "dicts"})
public class Action {
    @JsonIgnore
    private Class<?> clazz;

    private String clz;

    private String id;

    private String description;

    @JsonIgnore
    private StageType stage;


    public Action() {
    }

    private Class<?> getClazz() {
        return clazz;
    }

    private void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getClz() {
        if(StringUtils.isNotBlank(clz)){
            return clz;
        }
        return this.clazz.getName();
    }

    public void setClz(String clz) {
        this.clz = clz;
    }

    public String getId() {
        if (StringUtils.isNotBlank(id)) {
            return id;
        }
        return StringUtils.uncapitalize(clazz.getSimpleName());
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StageType getStage() {
        return stage;
    }

    public void setStage(StageType stage) {
        this.stage = stage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(getClz(), action.getClz()) && Objects.equals(getId(), action.getId()) && Objects.equals(getDescription(), action.getDescription()) && getStage() == action.getStage();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClz(), getId(), getDescription(), getStage());
    }

    public static Builder builder() {
        return Builder.getInstance();
    }

    public static final class Builder {
        private Class<?> clazz;
        private String description;

        private StageType stage;

        private Builder() {
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder clazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder stage(StageType stage) {
            this.stage = stage;
            return this;
        }



        public Action build() {
            Action action = new Action();
            action.setDescription(description);
            action.clazz = this.clazz;
            action.setStage(stage);
            return action;
        }
    }


    @Override
    public String toString() {
        return "Action{" +
                "clazz=" + clazz +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", stage=" + stage +
                '}';
    }
}
