package org.example.Utils.TortoiseHare;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class DetectCycles {
    public static void detectAndPrintCycles(ListenableGraph<String, DefaultEdge> graph) {
        CycleDetector cycleDetector = new CycleDetector(graph);
        List<Stack<String>> cycles = cycleDetector.findCycles();
        if (!cycles.isEmpty()) {
            System.out.println("Cycles detected:");
            for (Stack<String> cycle : cycles) {
                System.out.println(cycle);
            }
        } else {
            System.out.println("No cycles detected.");
        }
    }
}