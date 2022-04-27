package me.alvin.learn.domain.xml.dagMeta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import me.alvin.learn.domain.StageType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: Li Xiang
 * Date: 2022/4/21
 * Time: 7:38 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"depends", "flows"})
public class Stage {
    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JsonIgnore
    private StageType stageType;

    @JacksonXmlElementWrapper(localName = "flows")
    @JacksonXmlProperty(localName = "flow")
    private List<Flow> flows;

    @JacksonXmlElementWrapper(localName = "depends")
    @JacksonXmlProperty(localName = "depend")
    private List<StageDepend> depends;

    public Stage() {
    }

    public String getId() {
        if (StringUtils.isNotBlank(id)) {
            return id;
        }
        return stageType.getId();
    }

    public void setId(String id) {
        this.id = id;
    }

    public StageType getStageType() {
        return stageType;
    }

    public void setStageType(StageType stageType) {
        this.stageType = stageType;
    }

    public List<Flow> getFlows() {
        return CollectionUtils.isEmpty(flows) ? null : flows;
    }

    public void setFlows(List<Flow> flows) {
        this.flows = flows;
    }

    public List<StageDepend> getDepends() {
        return depends;
    }

    public void setDepends(List<StageDepend> depends) {
        this.depends = depends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stage stage = (Stage) o;
        return Objects.equals(getId(), stage.getId()) && Objects.equals(getFlows(), stage.getFlows()) && Objects.equals(getDepends(), stage.getDepends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFlows(), getDepends());
    }


    @Override
    public String toString() {
        return "Stage{" +
                "id='" + id + '\'' +
                ", flows=" + flows +
                ", depends=" + depends +
                '}';
    }

    public static Builder builder() {
        return Builder.getInstance();
    }

    public static final class Builder {
        private String id;

        private StageType stageType;
        private List<Flow> flows;
        private List<StageDepend> depends;

        private Builder() {
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder stageType(StageType stageType) {
            this.stageType = stageType;
            return this;
        }

        public Builder flows(List<Flow> flows) {
            this.flows = flows;
            return this;
        }

        public Builder flow(Flow flow) {
            if (null == this.flows) {
                this.flows = new ArrayList<>();
            }
            this.flows.add(flow);
            return this;
        }


        public Builder depends(List<StageDepend> depends) {
            this.depends = depends;
            return this;
        }

        public Builder depend(StageDepend depend) {
            if (null == this.depends) {
                this.depends = new ArrayList<>();
            }
            this.depends.add(depend);
            return this;
        }

        public Stage build() {
            Stage stage = new Stage();
            stage.setId(id);
            stage.setFlows(flows);
            stage.setDepends(depends);
            stage.setStageType(stageType);
            return stage;
        }
    }
}
