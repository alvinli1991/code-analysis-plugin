package me.alvin.learn.domain.dag;

import lombok.Getter;
import lombok.Setter;
import me.alvin.learn.domain.clazz.ClassMeta;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 4:08 PM
 */
public class Action {

    @Getter
    private String id;

    @Getter
    @Setter
    private String description;

    /**
     * 此action是否是虚拟节点
     */
    @Getter
    @Setter
    private boolean stageVirtual;

    /**
     * 设置action对应的class元数据
     */
    @Getter
    @Setter
    private ClassMeta actionClassMeta;

    @Getter
    @Setter
    private Set<Input> inputs;

    @Getter
    @Setter
    private Set<Output> outputs;

    public Action(String id, boolean stageVirtual) {
        this.id = id;
        this.stageVirtual = stageVirtual;
        this.inputs = new HashSet<>();
        this.outputs = new HashSet<>();
    }

    public Action(String id) {
        this(id, false);
    }

    public void addInput(Input input) {
        if (Objects.nonNull(input)) {
            this.inputs.add(input);
        }
    }

    public void addOutput(Output output) {
        if (Objects.nonNull(output)) {
            this.outputs.add(output);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Action action = (Action) o;
        return stageVirtual == action.stageVirtual && Objects.equals(id, action.id) && Objects.equals(actionClassMeta, action.actionClassMeta) && Objects.equals(inputs, action.inputs) && Objects.equals(outputs, action.outputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stageVirtual, actionClassMeta, inputs, outputs);
    }

    @Override
    public String toString() {
        return id;
    }
}
