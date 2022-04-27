package me.alvin.learn.utils;


import me.alvin.learn.domain.StageType;
import me.alvin.learn.domain.annotation.ActionMeta;
import me.alvin.learn.domain.xml.dagMeta.*;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Li Xiang
 * Date: 2022/4/22
 * Time: 11:54 AM
 */
public class DagGeneratorUtils {

    public static void main(String[] args) {
        buildDagMeta("xxx");
    }

    public static Set<Class<?>> getClasses(String scanPackageName) {
        //现版本(0.9.11)有bug：此处必须把SubTypesScanner填上，否则会报错，
        Reflections reflections = new Reflections(scanPackageName, new SubTypesScanner(),
                new TypeAnnotationsScanner(),
                new FieldAnnotationsScanner());
        return reflections.getTypesAnnotatedWith(ActionMeta.class);
    }

    public static void buildDagMeta(String scanPackageName) {
        Set<Class<?>> classes = getClasses(scanPackageName);
        Map<StageType, Graph<Class<?>, DefaultEdge>> stageActionClassesRelation = new HashMap<>();
        Graph<StageType, DefaultEdge> stageRelation = new DirectedAcyclicGraph<>(DefaultEdge.class);
        //初始化关系信息
        Arrays.stream(StageType.values()).forEach(stageType -> {
            if (Objects.nonNull(stageType.getDepend())) {
                Graphs.addEdgeWithVertices(stageRelation, stageType.getDepend(), stageType);
            } else {
                stageRelation.addVertex(stageType);
            }
        });

        Set<Class<?>> inactiveActionClass = new HashSet<>();
        classes.forEach(clazz -> {
            ActionMeta actionMeta = clazz.getAnnotation(ActionMeta.class);
            if (!actionMeta.isActive()) {
                inactiveActionClass.add(clazz);
            }
            StageType actionStage = actionMeta.stage();
            stageActionClassesRelation.putIfAbsent(actionStage, new DirectedAcyclicGraph<>(DefaultEdge.class));
            stageActionClassesRelation.computeIfPresent(actionStage, (key, value) -> {
                if (actionMeta.depends().length != 0) {
                    Arrays.stream(actionMeta.depends()).forEach(dependClz -> Graphs.addEdgeWithVertices(value, dependClz, clazz));
                } else {
                    value.addVertex(clazz);
                }
                return value;
            });
        });
        //校验
        long initStageCount = stageRelation.vertexSet().stream().filter(stageType -> stageRelation.inDegreeOf(stageType) == 0).count();
        if (0 == initStageCount) {
            throw new IllegalStateException("no init stage");
        }
        if (initStageCount > 1) {
            throw new IllegalStateException("more than one init stage");
        }

        Arrays.stream(StageType.values()).forEach(stageType -> {
            if (!stageActionClassesRelation.containsKey(stageType)) {
                throw new IllegalStateException("stage type don't has actions :" + stageType);
            }
            Graph<Class<?>, DefaultEdge> actionClassesRelation = stageActionClassesRelation.get(stageType);
            long initActionClzCount = actionClassesRelation.vertexSet().stream().filter(clz -> actionClassesRelation.inDegreeOf(clz) == 0).count();
            if (0 == initActionClzCount) {
                throw new IllegalStateException("stage has no init acitons :" + stageType);
            }
        });

        //提取stage顺序
        List<StageType> stageDependList = new ArrayList<>();
        StageType firstStage = stageRelation.vertexSet().stream().filter(stageType -> stageRelation.inDegreeOf(stageType) == 0).findFirst().get();
        Iterator<StageType> breadthFirstIterator = new BreadthFirstIterator<>(stageRelation, firstStage);
        while (breadthFirstIterator.hasNext()) {
            stageDependList.add(breadthFirstIterator.next());
        }
        //提取stage的flow信息
        Dag.Builder dagBuilder = Dag.builder().xmlns("http://www.w3.org/2001/XMLSchema-instance")
                .schemaLocation("http://xxx.xsd")
                .id("test");
        Map<StageType, Stage> stageMap = new HashMap<>();
        Map<Class<?>, Action> clzActionMap = new HashMap<>();
        stageDependList.forEach(stage -> {
            Graph<Class<?>, DefaultEdge> stageActionRelation = stageActionClassesRelation.get(stage);
            List<Class<?>> initActionClasses = stageActionRelation.vertexSet().stream().filter(action -> stageActionRelation.inDegreeOf(action) == 0).collect(Collectors.toList());
            List<Flow> stageFlows = new ArrayList<>();
            initActionClasses.forEach(initAction -> {
                Iterator<Class<?>> actionIterator = new BreadthFirstIterator<>(stageActionRelation, initAction);
                while (actionIterator.hasNext()) {
                    Class<?> clz = actionIterator.next();
                    ActionMeta actionMeta = clz.getAnnotation(ActionMeta.class);

                    Action clzAction = Action.builder().stage(stage)
                            .clazz(clz)
                            .description(actionMeta.description())
                            .build();
                    clzActionMap.putIfAbsent(clz, clzAction);
                    dagBuilder.unit(clzAction);
                    if (Graphs.vertexHasPredecessors(stageActionRelation, clz)) {
                        stageFlows.addAll(Arrays.stream(actionMeta.depends())
                                .map(dependClass -> Flow.builder()
                                        .fromAction(clzActionMap.get(dependClass))
                                        .toAction(clzAction)
                                        .build()).collect(Collectors.toList()));
                    } else if (!Graphs.vertexHasSuccessors(stageActionRelation, clz)) {
                        stageFlows.add(Flow.builder()
                                .fromAction(clzAction)
                                .build());
                    }
                }

            });
            Stage dependStageMeta = null;
            if (Objects.nonNull(stage.getDepend())) {
                dependStageMeta = stageMap.get(stage.getDepend());
            }
            Stage stageMeta = Stage.builder()
                    .flows(stageFlows)
                    .stageType(stage)
                    .depend(StageDepend.builder()
                            .dependStage(dependStageMeta)
                            .build())
                    .build();
            stageMap.put(stage, stageMeta);
            dagBuilder.stage(stageMeta);
        });

        Dag dag = dagBuilder.build();
        System.out.println(JacksonXmlUtils.toXml(dag));

    }


}
