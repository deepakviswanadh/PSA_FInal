package org.example.Utils.Rest;

import org.example.GraphNode.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class CopyClone {
    public static List<GraphNode> deepCopyGraphList(List<GraphNode> originalList) {
        List<GraphNode> copiedList = new ArrayList<>();
        for (GraphNode originalNode : originalList) {
            List<String> dependenciesCopy = new ArrayList<>(originalNode.getAdjacencyList());
            GraphNode copiedNode = new GraphNode(originalNode.getName(), dependenciesCopy);
            copiedList.add(copiedNode);
        }
        return copiedList;
    }
}
