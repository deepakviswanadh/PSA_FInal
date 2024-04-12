package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/java/org/example/dependencies_out.json";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            GraphManager graphManager = GraphManager.getInstance();
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String nodeName = entry.getKey();
                List<String> adjacencyList = objectMapper.convertValue(entry.getValue(), new TypeReference<List<String>>() {});
                GraphNode node = new GraphNode(nodeName, adjacencyList);
                graphManager.addNode(node);
            }
            ListenableGraph<String, DefaultEdge> graph = convertToJGraphTGraph(graphManager);
            performOps(graph,graphManager);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }

    public static ListenableGraph<String, DefaultEdge> convertToJGraphTGraph(GraphManager graphManager) {
        List<GraphNode> nodes = graphManager.getGraphList();
        ListenableGraph<String, DefaultEdge> g =
                new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));
        for (GraphNode node : nodes) {
            g.addVertex(node.getName());
        }
        for (GraphNode node : nodes) {
            String sourceNode = node.getName();
            for (String targetNode : node.getAdjacencyList()) {
                //since not all adjacent nodes of source nodes have a source node from the json
                //and not all source nodes have adjacent nodes
                if (g.containsVertex(sourceNode) && g.containsVertex(targetNode)) {
                    g.addEdge(sourceNode, targetNode);
                }
            }
        }
        return g;
    }

    public static void performOps( ListenableGraph<String, DefaultEdge> graph,GraphManager graphManager )
    {
//        GraphVisualizer.visualizeGraph(graph);
//        performTopologicalSortAndVisualize(graphManager);
    }
}
