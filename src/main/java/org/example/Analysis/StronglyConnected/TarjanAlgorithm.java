package org.example.Analysis.StronglyConnected;

import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;

import java.util.*;

public class TarjanAlgorithm {
    private int index;
    private Stack<GraphNode> stack;
    private List<List<GraphNode>> stronglyConnectedComponents;

    public List<List<GraphNode>> findStronglyConnectedComponents(GraphManager graphManager) {
        index = 0;
        stack = new Stack<>();
        stronglyConnectedComponents = new ArrayList<>();

        Map<GraphNode, Integer> indexMap = new HashMap<>();
        Map<GraphNode, Integer> lowLinkMap = new HashMap<>();
        Set<GraphNode> onStack = new HashSet<>();

        for (GraphNode node : graphManager.getGraphList()) {
            if (!indexMap.containsKey(node)) {
                strongConnect(node, indexMap, lowLinkMap, onStack, graphManager);
            }
        }

        return stronglyConnectedComponents;
    }

    private void strongConnect(GraphNode node, Map<GraphNode, Integer> indexMap, Map<GraphNode, Integer> lowLinkMap, Set<GraphNode> onStack, GraphManager graphManager) {
        indexMap.put(node, index);
        lowLinkMap.put(node, index);
        index++;
        stack.push(node);
        onStack.add(node);

        for (String neighborName : node.getAdjacencyList()) {
            GraphNode neighbor = graphManager.getNode(neighborName);
            if (neighbor == null) continue;
            if (!indexMap.containsKey(neighbor)) {
                strongConnect(neighbor, indexMap, lowLinkMap, onStack, graphManager);
                lowLinkMap.put(node, Math.min(lowLinkMap.get(node), lowLinkMap.get(neighbor)));
            } else if (onStack.contains(neighbor)) {
                lowLinkMap.put(node, Math.min(lowLinkMap.get(node), indexMap.get(neighbor)));
            }
        }

        if (lowLinkMap.get(node).equals(indexMap.get(node))) {
            List<GraphNode> component = new ArrayList<>();
            GraphNode curr;
            do {
                curr = stack.pop();
                onStack.remove(curr);
                component.add(curr);
            } while (!curr.equals(node));
            stronglyConnectedComponents.add(component);
        }
    }
}
