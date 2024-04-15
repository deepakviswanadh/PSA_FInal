package org.example.Analysis.StronglyConnected;

import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;

import java.util.List;

public class StronglyConnected {

    public void deriveSCComponents(GraphManager graphManager){
        TarjanAlgorithm tarjan = new TarjanAlgorithm();
        List<List<GraphNode>> stronglyConnectedComponents = tarjan.findStronglyConnectedComponents(graphManager);

        // Print the strongly connected components
        for (int i = 0; i < stronglyConnectedComponents.size(); i++) {
            List<GraphNode> component = stronglyConnectedComponents.get(i);

            // Exclude nodes with empty adjacency list
            component.removeIf(node -> node.getAdjacencyList().isEmpty());

            // Print component size
            int componentSize = component.size();

            if (componentSize > 0) {
                // Print component details
                System.out.println("Nodes:");
                for (GraphNode node : component) {
                    System.out.println("- " + node.getName());
                    System.out.println("  Adjacency List: " + node.getAdjacencyList());
                }
                System.out.println("------------------------");
            }

        }
    }
}
