package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.Analysis.DependencyTree.DependencyTree;
import org.example.Analysis.LeastDependent.LeastDependent;
import org.example.Analysis.MaxCompatibility.MaxCompatibility;
import org.example.Analysis.NodeReduction.NodeReduction;
import org.example.Analysis.StronglyConnected.StronglyConnected;
import org.example.Analysis.StronglyConnected.TarjanAlgorithm;
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
import static org.example.Utils.Rest.ConvertToVisualizer.convertToJGraphTGraph;
import static org.example.Utils.TortoiseHare.DetectCycles.detectAndPrintCycles;

public class Main {

    private static final ObjectMapper objectMapper = new ObjectMapper();
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
                List<String> adjacencyList = objectMapper.convertValue(entry.getValue(), new TypeReference<List<String>>() {});
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
            ListenableGraph<String, DefaultEdge> graph = convertToJGraphTGraph(graphManager, jsonNode, objectMapper,true);
            performOps(graph, graphManager,jsonNode);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }


    public static void performOps(ListenableGraph<String, DefaultEdge> graph, GraphManager graphManager, JsonNode jsonNode) {
        GraphVisualizer.visualizeGraph(graph, "OG Graph");
//        if (detectAndPrintCycles(graph)) {
//            System.out.println("Cycle is detected in the graph. Cannot perform" +
//                    "Dependency Analysis");
//            return;
//        }
//        List<String> topologicalOrder = performTopologicalSortAndVisualize(graph,graphManager,jsonNode);
//        System.out.println("Regular flow || Topo sort:");
//        Iterator<String> topoIterator = topologicalOrder.iterator();
//        for (String vertex : graph.vertexSet()) {
//            System.out.print(vertex);
//            if (topoIterator.hasNext()) {
//                System.out.print(" || " + topoIterator.next());
//            }
//            System.out.println();
//        }
//
//        //dependency analysis
//
//        MaxCompatibility.resolveDependencies(graphManager);
////        DependencyTree dependencyTree = new DependencyTree();
////        dependencyTree.analyzeDependencyResolution(graphManager);
//
//
//
//        //strongly connected analysis
//
//        StronglyConnected stronglyConnected = new StronglyConnected();
//        stronglyConnected.deriveSCComponents(graphManager);
//
//
//        //least connected analysis
//
//        LeastDependent leastDependent = new LeastDependent();
//        leastDependent.assessLeastDependent(graphManager);

        List<GraphNode> compressedGraph = NodeReduction.compressGraph(graphManager);

        // Output the compressed graph for verification
        for (GraphNode node : compressedGraph) {
            System.out.println("Node [" + node.getName() + "] -> " + node.getAdjacencyList());
        }
    }
}
