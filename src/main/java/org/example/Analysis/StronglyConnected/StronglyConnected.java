package org.example.Analysis.StronglyConnected;

import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;

import java.util.List;

public class StronglyConnected {

    public void deriveSCComponents(GraphManager graphManager){
        TarjanAlgorithm tarjan = new TarjanAlgorithm();
        List<List<GraphNode>> stronglyConnectedComponents = tarjan.findStronglyConnectedComponents(graphManager);

        for (int i = 0; i < stronglyConnectedComponents.size(); i++) {
            List<GraphNode> component = stronglyConnectedComponents.get(i);
            component.removeIf(node -> node.getAdjacencyList().isEmpty());

            int componentSize = component.size();

            if (componentSize > 0) {
                System.out.print("Strongly connected component " + ": ");
                for (int j = 0; j < component.size(); j++) {
                    GraphNode node = component.get(j);
                    System.out.print(node.getName() + (j < component.size() - 1 ? " -> " : ""));
                }
                System.out.println(); // Newline after each component
                for (GraphNode node : component) {
                    System.out.println("  " + node.getName() + " Adjacency List: " + node.getAdjacencyList());
                }
                System.out.println("------------------------");
            }

        }
    }

}
