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
            System.out.println("Package name\t\t\tAvailable\t\tSelected");
            double[] selectedVersions = solution.getPoint();
            int startIndex = 0;
            for (int i = 0; i < numPackages; i++) {
                String packageName = packageNames.get(i);
                String[] versions = packageVersions.get(packageName);
                String availableVersions = Arrays.toString(versions);
                String selectedVersion = getSelectedVersion(selectedVersions, startIndex, versions);
                System.out.println(packageName + ":\t\t" + availableVersions + "\t\t" + selectedVersion);
                startIndex += 2;
            }
        } else {
            // No feasible solution found
            System.out.println("No feasible solution found.");
        }
    }

    private static String getSelectedVersion(double[] selectedVersions, int startIndex, String[] versions) {
        boolean isFirstVersionSelected = selectedVersions[startIndex] == 1;
        boolean isSecondVersionSelected = selectedVersions[startIndex + 1] == 1;

        if (isFirstVersionSelected) {
            return "Version selected: " + versions[0];
        } else if (isSecondVersionSelected) {
            return "Version selected: " + versions[1];
        } else {
            return "No version selected";
        }
    }
}
