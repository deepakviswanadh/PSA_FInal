package org.example.Analysis.TopoSort;

import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.example.Visualizer.GraphVisualizer;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;

import java.util.ArrayList;
import java.util.List;

public class TopoSort {
    public static void performTopologicalSortAndVisualize(GraphManager graphManager) {
        // Convert GraphManager to ListenableGraph
        ListenableGraph<String, DefaultEdge> graph = convertToJGraphTGraph(graphManager);

        // Perform topological sorting
        List<String> topologicalOrder = topologicalSort(graph,graphManager);

        // Visualize the topological order
        visualizeTopologicalOrder(topologicalOrder);
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

    private static List<String> topologicalSort(ListenableGraph<String, DefaultEdge> graph, GraphManager graphManager) {
        List<String> topologicalOrder = new ArrayList<>();
        for (String vertex : graph.vertexSet()) {
            topologicalSortUtil(graph, vertex, topologicalOrder, graphManager);
        }

        return topologicalOrder;
    }

    private static void topologicalSortUtil(ListenableGraph<String, DefaultEdge> graph,
                                            String vertex,
                                            List<String> topologicalOrder,
                                            GraphManager manager) {
        GraphNode node = manager.getNode(vertex);
        if (!node.isVisited) {
            node.setVisited(true);
            for (DefaultEdge edge : graph.outgoingEdgesOf(vertex)) {
                String nextVertex = graph.getEdgeTarget(edge);
                topologicalSortUtil(graph, nextVertex, topologicalOrder, manager);
            }
            // Add to the beginning of the list for topological order
            topologicalOrder.add(0, vertex);
        }
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
        GraphVisualizer.visualizeGraph(orderedGraph, "Topological Order");
    }
}
