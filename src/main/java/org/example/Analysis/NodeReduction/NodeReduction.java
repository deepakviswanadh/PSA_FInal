package org.example.Analysis.NodeReduction;
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

    public static List<GraphNode> compressGraph(List<GraphNode> originalGraph) {
        List<GraphNode> compressedGraph = deepCopyGraphList(originalGraph).stream().limit(100).collect(Collectors.toList());
        Map<String, Set<String>> dependencyMap = new HashMap<>();
        Map<Set<String>, String> commonDependencies = new HashMap<>();
        for (GraphNode node : compressedGraph) {
            Set<String> deps = new HashSet<>(node.getAdjacencyList());
            dependencyMap.put(node.getName(), deps);

            if (!commonDependencies.containsKey(deps)) {
                commonDependencies.put(deps, node.getName());
            }
        }

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

            node.setAdjacencyList(new ArrayList<>(newDeps));
        }
        printAndVisualize(compressedGraph,dependencyMap,commonDependencies);
        return compressedGraph;
    }

    public static void printAndVisualize(List<GraphNode> compressedGraphNodes,
                                         Map<String, Set<String>> dependencyMap,  Map<Set<String>, String> commonDependencies){

        ListenableGraph<String, DefaultEdge> graph = new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));


        for (GraphNode node : compressedGraphNodes) {
            System.out.println("Node [" + node.getName() + "] -> " + node.getAdjacencyList());
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

            newDeps.forEach(dep -> {
                graph.addVertex(dep);
                graph.addEdge(node.getName(), dep);
            });
        }
        GraphVisualizer.visualizeGraph(graph, "Reduced Graph");
    }
}
