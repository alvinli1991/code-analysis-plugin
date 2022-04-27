package me.alvin.learn.domain.xml.dagMeta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author: Li Xiang
 * Date: 2022/4/21
 * Time: 8:23 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StageDepend {

    @JsonIgnore
    private Stage dependStage;

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    public StageDepend() {
    }

    @JacksonXmlProperty(isAttribute = true)
    public String getId() {
        if (StringUtils.isNotBlank(id)) {
            return id;
        }
        if (Objects.nonNull(dependStage)) {
            return dependStage.getId();
        }
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Stage getDependStage() {
        return dependStage;
    }

    private void setDependStage(Stage dependStage) {
        this.dependStage = dependStage;
    }

    public static Builder builder() {
        return Builder.getInstance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StageDepend that = (StageDepend) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static final class Builder {
        private Stage dependStage;

        private Builder() {
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder dependStage(Stage dependStage) {
            this.dependStage = dependStage;
            return this;
        }

        public StageDepend build() {
            StageDepend stageDependS = new StageDepend();
            stageDependS.setDependStage(dependStage);
            return stageDependS;
        }
    }

    @Override
    public String toString() {
        return "StageDepend{" +
                "id='" + getId() + '\'' +
                '}';
    }
}
