package org.example.Analysis.DependencyTree;

import org.example.Analysis.DependencyTree.DependencyResolver;
import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.example.Utils.Rest.VersionGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyTree {

    public void analyzeDependencyResolution(GraphManager graphManager) {
        DependencyResolver resolver = new DependencyResolver();
        List<GraphNode> graphList = graphManager.getGraphList();
        Map<String, List<String>> dependencyGraph = buildDependencyGraph(graphList);
        Map<String, String[]> packageVersions = VersionGenerator.generateVersions(graphList
                .stream().map(GraphNode::getName).collect(Collectors.toList()));
        Map<String, String> selectedVersions = resolver.resolveDependencies(dependencyGraph, packageVersions);

        // Print package name, available versions, and chosen version
        for (String packageName : packageVersions.keySet()) {
            System.out.println("Package Name: " + packageName);
            System.out.println("Available Versions: " + Arrays.toString(packageVersions.get(packageName)));
            System.out.println("Chosen Version: " + selectedVersions.get(packageName));
            System.out.println();
        }
    }

    private Map<String, List<String>> buildDependencyGraph(List<GraphNode> graphList) {
        Map<String, List<String>> dependencyGraph = new HashMap<>();
        for (GraphNode node : graphList) {
            dependencyGraph.put(node.getName(), node.getAdjacencyList());
        }
        return dependencyGraph;
    }
}
