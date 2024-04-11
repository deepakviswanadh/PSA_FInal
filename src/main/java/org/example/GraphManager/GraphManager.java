package org.example.GraphManager;

import org.example.GraphNode.GraphNode;

import java.util.*;

public class GraphManager {
    private static GraphManager instance;
    private List<GraphNode> graphList;

    private GraphManager() {
        graphList = new ArrayList<>();
    }

    public static synchronized GraphManager getInstance() {
        if (instance == null) {
            instance = new GraphManager();
        }
        return instance;
    }

    public void addNode(GraphNode node) {
        graphList.add(node);
    }

    public List<GraphNode> getGraphList() {
        return graphList;
    }


    public void clearGraphList() {
        graphList.clear();
    }
}
