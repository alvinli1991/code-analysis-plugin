package me.alvin.learn.domain.dag;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import me.alvin.learn.utils.GraphUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 4:08 PM
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Dag {

    @Getter
    private String id;

    /**
     * TODO 此变量可能无用，可以用actionIdMap代替
     */
    @JsonIgnore
    private Set<Action> actions;
    @Getter
    private Map<String, Action> actionIdMap;
    @JsonIgnore
    @Getter
    private Graph<Action, DefaultEdge> actionDag;

    /**
     * TODO 此变量可能无用，可以用stageIdMap代替
     */
    @JsonIgnore
    private Set<Stage> stages;
    @Getter
    private Map<String, Stage> stageIdMap;
    @JsonIgnore
    private Graph<Stage, DefaultEdge> stageRelation;

    public Dag(String id) {
        this.id = id;
        this.actionIdMap = new HashMap<>();
        this.stageIdMap = new HashMap<>();
        this.actionDag = new DirectedAcyclicGraph<>(DefaultEdge.class);
        this.stageRelation = new DirectedAcyclicGraph<>(DefaultEdge.class);
    }

    /**
     * 设置actions
     * 此处是完全替换，不是添加
     *
     * @param actions
     */
    public void setActions(Set<Action> actions) {
        if (CollectionUtils.isEmpty(actions)) {
            return;
        }
        this.actions = actions;
        this.actionIdMap = actions.stream()
                .collect(Collectors.toMap(Action::getId, Function.identity(), (existing, replacement) -> existing));
    }

    /**
     * 根据action id获取action元数据
     *
     * @param id
     * @return
     */
    public Optional<Action> getActionMeta(String id) {
        if (StringUtils.isBlank(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.actionIdMap.getOrDefault(id, null));
    }

    /**
     * 设置stages
     * 此处是完全替换，不是添加
     *
     * @param stages
     */
    public void setStages(Set<Stage> stages) {
        if (CollectionUtils.isEmpty(stages)) {
            return;
        }
        this.stages = stages;
        this.stageIdMap = stages.stream()
                .collect(Collectors.toMap(Stage::getId, Function.identity(), (existing, replacement) -> existing));
    }

    /**
     * 根据stage id获取stage元数据
     *
     * @param id
     * @return
     */
    public Optional<Stage> getStageMeta(String id) {
        if (StringUtils.isBlank(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.stageIdMap.getOrDefault(id, null));
    }

    /**
     * TODO 缺少检查
     * 1. 增加stage的关系
     * 2. 同时将from stage的叶子节点与to stage的初始节点连接
     *
     * @param from
     * @param to
     */
    public void addDependency(Stage from, Stage to) {
        this.stages.add(from);
        this.stages.add(to);
        this.stageIdMap.put(from.getId(), from);
        this.stageIdMap.put(to.getId(), to);

        GraphUtils.addEdge(this.stageRelation, from, to);
        GraphUtils.addEdge(this.actionDag, from.getVirtualOutAction(), to.getVirtualInAction());
    }


    /**
     * TODO 缺少检查
     * 增加action的关系
     *
     * @param from
     * @param to
     */
    public void addDependency(Action from, Action to) {
        this.actions.add(from);
        this.actions.add(to);
        this.actionIdMap.put(from.getId(), from);
        this.actionIdMap.put(to.getId(), to);

        GraphUtils.addEdge(this.actionDag, from, to);
    }
}
