package org.example.Utils.TortoiseHare;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class DetectCycles {
    public static boolean detectAndPrintCycles(ListenableGraph<String, DefaultEdge> graph) {
        CycleDetector cycleDetector = new CycleDetector(graph);
        List<Stack<String>> cycles = cycleDetector.findCycles();
        if (!cycles.isEmpty()) {
            return true;
        }
        return false;
    }
}