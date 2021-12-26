package me.alvin.learn.domain.dag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import me.alvin.learn.utils.GraphUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO equals和hashCode待验证
 *
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 4:08 PM
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Stage {

    public static final String START_SUFFIX = "-start";
    public static final String END_SUFFIX = "-end";
    @Getter
    private final String id;

    @Getter
    private final Action virtualInAction;
    @Getter
    private final Action virtualOutAction;

    private final Set<Action> actions;
    private final Map<String, Action> actionIdMap;
    @JsonIgnore
    private final Graph<Action, DefaultEdge> actionRelation;

    public Stage(String id) {
        this.id = id;
        this.virtualInAction = new Action(id + START_SUFFIX, true);
        this.virtualOutAction = new Action(id + END_SUFFIX, true);

        this.actions = new HashSet<>();
        this.actionIdMap = new HashMap<>();
        this.actionRelation = new DirectedAcyclicGraph<>(DefaultEdge.class);

    }

    /**
     * TODO 缺少检查
     *
     * @param from
     * @param to
     * @return
     */
    public void addActionRelation(Action from, Action to) {
        actions.add(from);
        actions.add(to);
        this.actionIdMap.put(from.getId(), from);
        this.actionIdMap.put(to.getId(), to);

        GraphUtils.addEdge(this.actionRelation, from, to);
    }

    /**
     * 完成stage的构建
     * 将虚拟节点给关联起来
     */
    public Stage finishBuild(Graph<Action, DefaultEdge> outDag) {
        Set<Action> inActions = getInitialActions();
        inActions.forEach(inAction -> {
            GraphUtils.addEdge(this.actionRelation, virtualInAction, inAction);
            GraphUtils.addEdge(outDag, virtualInAction, inAction);
        });
        Set<Action> outActions = getLeafActions();
        outActions.forEach(outAction -> {
            GraphUtils.addEdge(this.actionRelation, outAction, virtualOutAction);
            GraphUtils.addEdge(outDag, outAction, virtualOutAction);
        });
        return this;
    }

    /**
     * 获取入action
     *
     * @return
     */
    public Set<Action> getInitialActions() {
        return this.actionRelation.vertexSet()
                .stream()
                .filter(action -> this.actionRelation.inDegreeOf(action) == 0)
                .collect(Collectors.toSet());
    }

    /**
     * 获取叶子action
     *
     * @return
     */
    public Set<Action> getLeafActions() {
        return this.actionRelation.vertexSet()
                .stream()
                .filter(action -> this.actionRelation.outDegreeOf(action) == 0)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stage stage = (Stage) o;
        return Objects.equals(id, stage.id) && Objects.equals(actionRelation, stage.actionRelation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, actionRelation);
    }
}
