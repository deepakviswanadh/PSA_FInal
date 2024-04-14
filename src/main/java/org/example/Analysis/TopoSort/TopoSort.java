package org.example.Analysis.TopoSort;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.example.Visualizer.GraphVisualizer;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopoSort {
    public static List<String> performTopologicalSortAndVisualize(ListenableGraph<String, DefaultEdge> graph,GraphManager graphManager, JsonNode jsonNode) {

        // Perform topological sorting
        List<String> topologicalOrder = topologicalSort(graph, graphManager);

        // Visualize the topological order
        visualizeTopologicalOrder(topologicalOrder);

        return topologicalOrder;
    }

    private static List<String> topologicalSort(ListenableGraph<String, DefaultEdge> graph, GraphManager graphManager) {
        List<String> topologicalOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> nodesWithEmptyAdjacencyLists = new HashSet<>();

        // Find nodes with empty adjacency lists and start DFS from them
        for (String vertex : graph.vertexSet()) {
            if (graphManager.getNode(vertex).getAdjacencyList().isEmpty()) {
                nodesWithEmptyAdjacencyLists.add(vertex);
            }
        }

        for (String vertex : nodesWithEmptyAdjacencyLists) {
            topologicalSortUtil(graph, vertex, visited, topologicalOrder, graphManager);
        }

        // Find nodes with no incoming edges and start DFS from them
        for (String vertex : graph.vertexSet()) {
            if (!hasIncomingEdges(graph, vertex)) {
                topologicalSortUtil(graph, vertex, visited, topologicalOrder, graphManager);
            }
        }

        return topologicalOrder;
    }

    private static void topologicalSortUtil(ListenableGraph<String, DefaultEdge> graph,
                                            String vertex,
                                            Set<String> visited,
                                            List<String> topologicalOrder,
                                            GraphManager manager) {
        GraphNode node = manager.getNode(vertex);
        if (!visited.contains(vertex)) {
            visited.add(vertex);
            for (DefaultEdge edge : graph.outgoingEdgesOf(vertex)) {
                String nextVertex = graph.getEdgeTarget(edge);
                topologicalSortUtil(graph, nextVertex, visited, topologicalOrder, manager);
            }
            // Add to the end of the list for topological order
            topologicalOrder.add(vertex);
        }
    }

    private static boolean hasIncomingEdges(ListenableGraph<String, DefaultEdge> graph, String vertex) {
        for (String v : graph.vertexSet()) {
            if (graph.containsEdge(v, vertex)) {
                return true;
            }
        }
        return false;
    }

    private static void visualizeTopologicalOrder(List<String> topologicalOrder) {
        // Create a new graph for visualization
        ListenableGraph<String, DefaultEdge> orderedGraph =
                new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));

        // Add vertices in the order specified by topological sorting
        for (String vertex : topologicalOrder) {
            orderedGraph.addVertex(vertex);
        }

        // Visualize the ordered graph
        GraphVisualizer.visualizeGraphInVerticalLine(orderedGraph, "Topological Order");
    }
}
