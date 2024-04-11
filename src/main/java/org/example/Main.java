package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/java/org/example/dependencies_out.json";

        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Read JSON file into a JsonNode object
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));

            // Get the GraphManager instance
            GraphManager graphManager = GraphManager.getInstance();

            // Iterate over each key in the JSON object
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();

                // Get the key (node) and its adjacency list (value)
                String nodeName = entry.getKey();
                List<String> adjacencyList = objectMapper.convertValue(entry.getValue(), new TypeReference<List<String>>() {});

                // Create GraphNode instance
                GraphNode node = new GraphNode(nodeName, adjacencyList);

                // Add the node to the graph list
                graphManager.addNode(node);
            }

            // Print the graph nodes
            System.out.println("Graph Nodes:");
            for (GraphNode node : graphManager.getGraphList()) {
                System.out.println(node);
            }

        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }
}
