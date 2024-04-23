package org.example.Utils.Rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;

import java.util.*;

public class ConvertToVisualizer {
    public static ListenableGraph<String, DefaultEdge> convertToJGraphTGraph(GraphManager graphManager, JsonNode jsonNode,
                                                                             ObjectMapper objectMapper, boolean adjustFlag) {
        ListenableGraph<String, DefaultEdge> g =
                new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));
        Set<String> visitedNodes = new HashSet<>();
        Queue<String> nodeQueue = new LinkedList<>();

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String nodeName = entry.getKey();
            List<String> adjacencyList = objectMapper.convertValue(entry.getValue(), new TypeReference<List<String>>() {});
            if(adjustFlag) {
                if (!graphManager.containsNode(nodeName)) {
                    graphManager.addNode(new GraphNode(nodeName, adjacencyList));
                }
            }
            if (!visitedNodes.contains(nodeName)) {
                visitedNodes.add(nodeName);
                nodeQueue.add(nodeName);
            }
            while (!nodeQueue.isEmpty()) {
                String currentNodeName = nodeQueue.poll();
                GraphNode currentNode = graphManager.getNode(currentNodeName);
                g.addVertex(currentNodeName);
                for (String neighborName : currentNode.getAdjacencyList()) {
                    g.addVertex(neighborName);
                    g.addEdge(currentNodeName, neighborName);
                    if(adjustFlag) {
                        if (!graphManager.containsNode(neighborName)) {
                            graphManager.addNode(new GraphNode(neighborName, Collections.emptyList()));
                        }
                    }
                    if (!visitedNodes.contains(neighborName)) {
                        visitedNodes.add(neighborName);
                        nodeQueue.add(neighborName);
                    }
                }
            }
        }
        return g;
    }
}
