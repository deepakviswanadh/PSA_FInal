package org.example.Analysis.LeastDependent;

import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;

import java.util.*;

public class KahnsAlgorithm {

    public static List<String> findLeastDependentPackages(GraphManager graphManager) {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, List<String>> graph = new HashMap<>();

        // Compute in-degree and create the graph
        for (GraphNode node : graphManager.getGraphList()) {
            String packageName = node.getName();
            inDegree.put(packageName, 0);
            graph.put(packageName, node.getAdjacencyList());
            for (String neighbor : node.getAdjacencyList()) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }

        // Initialize queue with nodes having in-degree of 0
        Queue<String> queue = new LinkedList<>();
        for (String packageName : inDegree.keySet()) {
            if (inDegree.get(packageName) == 0) {
                queue.offer(packageName);
            }
        }

        // Perform topological sorting
        List<String> leastDependentPackages = new ArrayList<>();
        while (!queue.isEmpty()) {
            String packageName = queue.poll();
            leastDependentPackages.add(packageName);
            for (String neighbor : graph.get(packageName)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        return leastDependentPackages;
    }
}
