package org.example.Analysis.TopoSort;

import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.example.Visualizer.GraphVisualizer;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopoSort {
    public static void performTopologicalSortAndVisualize(GraphManager graphManager) {
        // Convert GraphManager to ListenableGraph
        ListenableGraph<String, DefaultEdge> graph = convertToJGraphTGraph(graphManager);

        // Perform topological sorting
        List<String> topologicalOrder = topologicalSort(graph);

        // Visualize the sorted graph
        GraphVisualizer.visualizeGraph(graph,"TopoSort");
    }

    private static ListenableGraph<String, DefaultEdge> convertToJGraphTGraph(GraphManager graphManager) {
        List<GraphNode> nodes = graphManager.getGraphList();
        ListenableGraph<String, DefaultEdge> g =
                new DefaultListenableGraph<>(new org.jgrapht.graph.DirectedAcyclicGraph<>(DefaultEdge.class));
        for (GraphNode node : nodes) {
            g.addVertex(node.getName());
        }
        for (GraphNode node : nodes) {
            String sourceNode = node.getName();
            for (String targetNode : node.getAdjacencyList()) {
                if (g.containsVertex(sourceNode) && g.containsVertex(targetNode)) {
                    g.addEdge(sourceNode, targetNode);
                }
            }
        }
        return g;
    }

    private static List<String> topologicalSort(ListenableGraph<String, DefaultEdge> graph) {
        List<String> topologicalOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String vertex : graph.vertexSet()) {
            if (!visited.contains(vertex)) {
                topologicalSortUtil(graph, vertex, visited, topologicalOrder);
            }
        }

        return topologicalOrder;
    }

    private static void topologicalSortUtil(ListenableGraph<String, DefaultEdge> graph,
                                            String vertex,
                                            Set<String> visited,
                                            List<String> topologicalOrder) {
        visited.add(vertex);

        for (DefaultEdge edge : graph.outgoingEdgesOf(vertex)) {
            String nextVertex = graph.getEdgeTarget(edge);
            if (!visited.contains(nextVertex)) {
                topologicalSortUtil(graph, nextVertex, visited, topologicalOrder);
            }
        }

        topologicalOrder.add(0, vertex); // Add to the beginning of the list for topological order
    }
}
