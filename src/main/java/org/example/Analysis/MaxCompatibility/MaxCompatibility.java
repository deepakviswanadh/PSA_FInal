package org.example.Analysis.MaxCompatibility;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.example.Utils.Rest.VersionGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class MaxCompatibility {

    public static void resolveDependencies(GraphManager graphManager) {

        // Take only the first 50 packages
        List<String> packageNames = graphManager.getGraphList().stream()
                .limit(50)
                .map(GraphNode::getName)
                .collect(Collectors.toList());

        Map<String, String[]> packageVersions = VersionGenerator.generateVersions(packageNames);

        // Print details for each package
        for (String packageName : packageNames) {
            System.out.println("Package Name: " + packageName);

            // Print available versions
            System.out.println("Available Versions:");
            String[] versions = packageVersions.get(packageName);
            System.out.println(Arrays.toString(versions));

            // Define binary decision variables representing package selection
            int numPackages = packageNames.size();
            double[] decisionVariables = new double[numPackages * 2]; // 2 versions per package

            // Define constraints (each package can only be selected once)
            LinearConstraint[] constraints = new LinearConstraint[numPackages];
            double[] constants = new double[numPackages];
            Arrays.fill(constants, 1);

            for (int i = 0; i < numPackages; i++) {
                double[] coefficientsForRow = new double[numPackages * 2];
                Arrays.fill(coefficientsForRow, 0);
                for (int j = 0; j < 2; j++) {
                    coefficientsForRow[i * 2 + j] = 1; // Each package can only be selected once
                }
                constraints[i] = new LinearConstraint(coefficientsForRow, Relationship.LEQ, constants[i]);
            }

            LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);

            // Define weights for versions (just for demonstration)
            double[] weights = new double[numPackages * 2]; // Assuming each package has 2 versions
            Arrays.fill(weights, 1); // Equal weights for simplicity

            // Define an objective function (maximize the weighted sum of selected versions)
            LinearObjectiveFunction objective = new LinearObjectiveFunction(weights, 0); // No constant term

            // Solve the linear programming problem
            SimplexSolver solver = new SimplexSolver();
            PointValuePair solution = solver.optimize(objective, constraintSet, GoalType.MAXIMIZE);

            // Check if a feasible solution is found
            if (solution != null && solution.getValue() != Double.NEGATIVE_INFINITY) {
                // Print the solution
                double[] selectedVersions = solution.getPoint();
                int startIndex = packageNames.indexOf(packageName) * 2;
                boolean isFirstVersionSelected = selectedVersions[startIndex] == 1;
                boolean isSecondVersionSelected = selectedVersions[startIndex + 1] == 1;

                // Print selected version and reason for selection
                System.out.println("Selected Version:");
                if (isFirstVersionSelected && isSecondVersionSelected) {
                    System.out.println("Both versions selected - maximizing compatibility");
                } else if (isFirstVersionSelected) {
                    System.out.println(versions[0] + " - maximizing compatibility");
                } else if (isSecondVersionSelected) {
                    System.out.println(versions[1] + " - maximizing compatibility");
                } else {
                    System.out.println("No version selected");
                }
            } else {
                // No feasible solution found
                System.out.println("No feasible solution found for package " + packageName);
            }
            System.out.println(); // Add a blank line for readability
        }
    }
}
