package org.example.Analysis.MaxCompatibility;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.example.Utils.Rest.VersionGenerator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Random;

public class MaxCompatibility {

    public static void resolveDependencies(GraphManager graphManager) {

        List<String> packageNames = graphManager.getGraphList().stream()
                .map(GraphNode::getName)
                .collect(Collectors.toList());

        Map<String, String[]> packageVersions = VersionGenerator.generateVersions(packageNames);

        int numPackages = packageNames.size();
        double[] decisionVariables = new double[numPackages * 2];

        LinearConstraint[] constraints = new LinearConstraint[numPackages];
        double[] constants = new double[numPackages];
        Arrays.fill(constants, 1);

        for (int i = 0; i < numPackages; i++) {
            double[] coefficientsForRow = new double[numPackages * 2];
            Arrays.fill(coefficientsForRow, 0);
            for (int j = 0; j < 2; j++) {
                coefficientsForRow[i * 2 + j] = 1;
            }
            constraints[i] = new LinearConstraint(coefficientsForRow, Relationship.LEQ, constants[i]);
        }

        LinearConstraintSet constraintSet = new LinearConstraintSet(constraints);

        double[] weights = new double[numPackages * 2];
        Arrays.fill(weights, 1);

        LinearObjectiveFunction objective = new LinearObjectiveFunction(weights, 0);

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(objective, constraintSet, GoalType.MAXIMIZE);

        if (solution != null && solution.getValue() != Double.NEGATIVE_INFINITY) {
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
