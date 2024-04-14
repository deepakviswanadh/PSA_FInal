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
            Set<String> allStrings = new HashSet<>();
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String nodeName = entry.getKey();
                List<String> adjacencyList = objectMapper.convertValue(entry.getValue(), new TypeReference<List<String>>() {
                });
                GraphNode node = new GraphNode(nodeName, adjacencyList);
                graphManager.addNode(node);
                allStrings.add(nodeName);
                allStrings.addAll(adjacencyList);
            }
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
        for (GraphNode node : nodes) {
            g.addVertex(node.getName());
        }
        for (GraphNode node : nodes) {
            for (String targetNode : node.getAdjacencyList()) {
                if (!g.containsVertex(targetNode)) {
                    g.addVertex(targetNode);
                    graphManager.addNode(new GraphNode(targetNode, Collections.emptyList()));
                    ((ObjectNode) jsonNode).putArray(targetNode);
                }
            }
        }
        return g;
    }

    public static void performOps(ListenableGraph<String, DefaultEdge> graph, GraphManager graphManager) {
        GraphVisualizer.visualizeGraph(graph, "OG Graph");
//        if (detectAndPrintCycles(graph)) {
//            System.out.println("Cycle is detected in the graph. Cannot perform" +
//                    "Depedency Analysis");
//            return;
//        }
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
