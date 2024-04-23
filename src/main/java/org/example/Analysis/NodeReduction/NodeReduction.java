package org.example.Analysis.NodeReduction;

import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.example.Visualizer.GraphVisualizer;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;

import java.util.*;
import java.util.stream.Collectors;

import static org.example.Utils.Rest.CopyClone.deepCopyGraphList;

public class NodeReduction {

    public static List<GraphNode> compressGraph(GraphManager graphManager) {
//        List<GraphNode> compressedGraph = deepCopyGraphList(originalGraph).stream().limit(10).collect(Collectors.toList());
        List<GraphNode> compressedGraph = deepCopyGraphList(graphManager.getGraphList());
        Map<String, Set<String>> dependencyMap = new HashMap<>();
        Map<Set<String>, String> commonDependencies = new HashMap<>();

        // Populate dependency map and identify common dependencies
        for (GraphNode node : compressedGraph) {
            Set<String> deps = new HashSet<>(node.getAdjacencyList());
            dependencyMap.put(node.getName(), deps);

            // This map helps identify if a set of dependencies already has a representative node
            if (!commonDependencies.containsKey(deps)) {
                commonDependencies.put(deps, node.getName());
            }
        }

        // Now adjust the graph structure to use common dependency nodes
        for (GraphNode node : compressedGraph) {
            Set<String> originalDeps = new HashSet<>(node.getAdjacencyList());
            Set<String> newDeps = new HashSet<>();

            for (String dep : originalDeps) {
                Set<String> depDeps = dependencyMap.get(dep);
                String representative = commonDependencies.get(depDeps);
                if (representative != null && !representative.equals(node.getName())) {
                    newDeps.add(representative);
                } else {
                    newDeps.add(dep);
                }
            }

            // Update node's adjacency list with compressed dependencies
            node.setAdjacencyList(new ArrayList<>(newDeps));
        }

        printAndVisualize(compressedGraph,dependencyMap,commonDependencies);
        return compressedGraph;
    }

    public static void printAndVisualize(List<GraphNode> compressedGraphNodes,
                                         Map<String, Set<String>> dependencyMap,  Map<Set<String>, String> commonDependencies){

        ListenableGraph<String, DefaultEdge> graph = new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));

        // Adjust the graph structure to use common dependency nodes
        for (GraphNode node : compressedGraphNodes) {
            graph.addVertex(node.getName());
            Set<String> originalDeps = new HashSet<>(node.getAdjacencyList());
            Set<String> newDeps = new HashSet<>();

            for (String dep : originalDeps) {
                Set<String> depDeps = dependencyMap.get(dep);
                String representative = commonDependencies.get(depDeps);
                if (representative != null && !representative.equals(node.getName()) && !newDeps.contains(representative)) {
                    newDeps.add(representative);
                } else {
                    newDeps.add(dep);
                }
            }

            // Update the JGraphT graph
            newDeps.forEach(dep -> {
                graph.addVertex(dep);
                graph.addEdge(node.getName(), dep);
            });
        }
        GraphVisualizer.visualizeGraph(graph, "Reduced Graph");
    }

}

