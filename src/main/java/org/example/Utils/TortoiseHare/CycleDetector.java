package org.example.Utils.TortoiseHare;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class CycleDetector {
    private ListenableGraph<String, DefaultEdge> graph;
    private Set<String> visited;
    private Set<String> onStack;
    private List<Stack<String>> cycles;

    public CycleDetector(ListenableGraph<String, DefaultEdge> graph) {
        this.graph = graph;
        visited = new HashSet<>();
        onStack = new HashSet<>();
        cycles = new ArrayList<>();
    }

    public List<Stack<String>> findCycles() {
        for (String node : graph.vertexSet()) {
            if (!visited.contains(node)) {
                dfs(node, new Stack<>());
            }
        }
        return cycles;
    }

    private void dfs(String node, Stack<String> path) {
        visited.add(node);
        onStack.add(node);
        path.push(node);

        for (DefaultEdge edge : graph.outgoingEdgesOf(node)) {
            String neighbor = graph.getEdgeTarget(edge);
            if (!visited.contains(neighbor)) {
                dfs(neighbor, path);
            } else if (onStack.contains(neighbor)) {
                // Found a cycle
                Stack<String> cycle = new Stack<>();
                while (!path.isEmpty()) {
                    String vertex = path.pop();
                    cycle.push(vertex);
                    if (vertex.equals(neighbor)) {
                        break;
                    }
                }
                cycles.add(cycle);
            }
        }

        onStack.remove(node);
        path.pop();
    }
}
