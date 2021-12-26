package me.alvin.learn.utils;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

/**
 * @author: Li Xiang
 * Date: 2021/12/25
 * Time: 8:51 PM
 */
public class GraphUtils {
    /**
     * 向图中添加节点及其关联
     * @param graph
     * @param from
     * @param to
     * @param <T>
     */
    public static <T> void addEdge(Graph<T, DefaultEdge> graph, T from, T to) {
        graph.addVertex(from);
        graph.addVertex(to);
        graph.addEdge(from, to);
    }
}
