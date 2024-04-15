package org.example.Analysis.DependencyTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyResolver {

    public Map<String, String> resolveDependencies(Map<String, List<String>> dependencyGraph,
                                                   Map<String, String[]> packageVersions) {
        Map<String, String> selectedVersions = new HashMap<>();

        for (String packageName : dependencyGraph.keySet()) {
            selectLatestVersion(packageName, dependencyGraph, packageVersions, selectedVersions);
        }

        return selectedVersions;
    }

    private void selectLatestVersion(String packageName, Map<String, List<String>> dependencyGraph,
                                     Map<String, String[]> packageVersions, Map<String, String> selectedVersions) {
        if (selectedVersions.containsKey(packageName)) {
            return; // Version already selected for this package
        }

        List<String> dependencies = dependencyGraph.get(packageName);
        String[] versions = packageVersions.get(packageName);

        // Selecting the latest version
        String latestVersion = selectLatestVersion(versions);

        // Recursively select versions for dependencies
        for (String dependency : dependencies) {
            selectLatestVersion(dependency, dependencyGraph, packageVersions, selectedVersions);
        }

        // Add the package and its selected version
        selectedVersions.put(packageName, latestVersion);
    }

    private String selectLatestVersion(String[] versions) {
        String latestVersion = "0.0.0";
        for (String version : versions) {
            if (compareVersions(version, latestVersion) > 0) {
                latestVersion = version;
            }
        }
        return latestVersion;
    }

    // Compare two versions in format "x.y.z"
    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        for (int i = 0; i < 3; i++) {
            int part1 = Integer.parseInt(parts1[i]);
            int part2 = Integer.parseInt(parts2[i]);
            if (part1 != part2) {
                return Integer.compare(part1, part2);
            }
        }

        return 0; // Versions are equal
    }
}