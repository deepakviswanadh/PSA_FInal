package org.example.Utils.Rest;

import java.util.*;

public class VersionGenerator {

    public static Map<String, String[]> generateVersions(List<String> packages){
        Map<String, String[]> packageVersions = new HashMap<>();
        Random random = new Random();
        for (String packageName : packages) {
            String[] versions = new String[2];
            for (int i = 0; i < 2; i++) {
                versions[i] = String.format("%d.%d.%d", random.nextInt(10), random.nextInt(10), random.nextInt(10));
            }
            packageVersions.put(packageName, versions);
        }
        return packageVersions;
    }
}
