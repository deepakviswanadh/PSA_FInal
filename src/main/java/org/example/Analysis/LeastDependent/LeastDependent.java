package org.example.Analysis.LeastDependent;
import org.example.GraphManager.GraphManager;
import java.util.List;
import static org.example.Analysis.LeastDependent.KahnsAlgorithm.findLeastDependentPackages;

public class LeastDependent {

    public void assessLeastDependent(GraphManager graphManager){
        // Add nodes and their adjacency lists to the graphManager

        List<String> leastDependentPackages = findLeastDependentPackages(graphManager);

        // Print packages with the fewest dependencies
        System.out.println("Packages with the fewest dependencies:");
        for (String packageName : leastDependentPackages) {
            System.out.println("- " + packageName);
        }
    }
}
