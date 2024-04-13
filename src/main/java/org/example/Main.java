package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.example.Visualizer.GraphVisualizer;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.example.Analysis.TopoSort.TopoSort.performTopologicalSortAndVisualize;
import static org.example.Utils.TortoiseHare.DetectCycles.detectAndPrintCycles;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/java/org/example/dependencies_out.json";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            GraphManager graphManager = GraphManager.getInstance();

            // Collect all unique strings from the JSON
            Set<String> allStrings = new HashSet<>();

            // Collect all nodes first
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String nodeName = entry.getKey();
                List<String> adjacencyList = objectMapper.convertValue(entry.getValue(), new TypeReference<List<String>>() {});
                GraphNode node = new GraphNode(nodeName, adjacencyList);
                graphManager.addNode(node);

                // Add node name and its adjacency list to the set of all strings
                allStrings.add(nodeName);
                allStrings.addAll(adjacencyList);
            }

            // Add missing strings as properties with empty arrays
            for (String str : allStrings) {
                if (!jsonNode.has(str)) {
                    ((ObjectNode) jsonNode).putArray(str);
                }
            }

            ListenableGraph<String, DefaultEdge> graph = convertToJGraphTGraph(graphManager, jsonNode);
            performOps(graph, graphManager);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }

    public static ListenableGraph<String, DefaultEdge> convertToJGraphTGraph(GraphManager graphManager, JsonNode jsonNode) {
        List<GraphNode> nodes = new ArrayList<>(graphManager.getGraphList()); // Make a copy of the list
        ListenableGraph<String, DefaultEdge> g =
                new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));

        // Add all vertices first
        for (GraphNode node : nodes) {
            g.addVertex(node.getName());
        }

        // Then add edges
        for (GraphNode node : nodes) {
            String sourceNode = node.getName();
            for (String targetNode : node.getAdjacencyList()) {
                // Check if the target node is present in the graph
                if (!g.containsVertex(targetNode)) {
                    // If not present in the graph, create a node for it and add it to the graph and JSON
                    g.addVertex(targetNode);
                    graphManager.addNode(new GraphNode(targetNode, Collections.emptyList()));
                    ((ObjectNode) jsonNode).putArray(targetNode);
                    System.out.println("Node added for edge: " + targetNode);
                }

                // Check if both source and target vertices are present in the graph
                if (g.containsVertex(sourceNode) && g.containsVertex(targetNode)) {
                    if (g.addEdge(sourceNode, targetNode) != null) {
                        System.out.println("Edge added: " + sourceNode + " -> " + targetNode);
                    } else {
                        System.out.println("Failed to add edge: " + sourceNode + " -> " + targetNode);
                    }
                } else {
                    System.out.println("One or both vertices not found for edge: " + sourceNode + " -> " + targetNode);
                }
            }
        }
        return g;
    }

    public static void performOps(ListenableGraph<String, DefaultEdge> graph, GraphManager graphManager) {
        GraphVisualizer.visualizeGraph(graph, "OG Graph");
//        detectAndPrintCycles(graph);
//        List<String> topologicalOrder = performTopologicalSortAndVisualize(graphManager);
//        System.out.println("Regular flow || Topo sort:");
//        Iterator<String> topoIterator = topologicalOrder.iterator();
//        for (String vertex : graph.vertexSet()) {
//            System.out.print(vertex);
//            if (topoIterator.hasNext()) {
//                System.out.print(" || " + topoIterator.next());
//            }
//            System.out.println();
//        }
    }
}
