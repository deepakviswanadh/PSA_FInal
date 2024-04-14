package org.example.Visualizer;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GraphVisualizer {
    public static void visualizeGraph(ListenableGraph<String, DefaultEdge> g, String title) {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            Map<String, Object> vertexMap = new HashMap<>();
            Set<String> vertices = g.vertexSet();
            Map<String, Point> vertexPositions = generateSeparatedCircularLayout(600, 400, 200, vertices); // Adjust parameters as needed
            for (String vertex : vertices) {
                Point position = vertexPositions.get(vertex);
                Object vertexObject = graph.insertVertex(parent, null, vertex, position.x, position.y, 80, 30);
                vertexMap.put(vertex, vertexObject);
            }
            for (DefaultEdge edge : g.edgeSet()) {
                String source = g.getEdgeSource(edge);
                String target = g.getEdgeTarget(edge);
                Object sourceVertex = vertexMap.get(source);
                Object targetVertex = vertexMap.get(target);
                graph.insertEdge(parent, null, "", sourceVertex, targetVertex);
            }
        } finally {
            graph.getModel().endUpdate();
        }

        JFrame frame = new JFrame(title);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        frame.getContentPane().add(graphComponent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 800));
        frame.pack();
        frame.setVisible(true);
    }


    public static Map<String, Point> generateSeparatedCircularLayout(int centerX, int centerY, int radius, Set<String> vertices) {
        Map<String, Point> vertexPositions = new HashMap<>();
        double angleStep = 2 * Math.PI / vertices.size();
        double angle = 0;
        int circleIndex = 0; // Index for each circle
        int circles = (int) Math.ceil(Math.sqrt(vertices.size())); // Number of circles needed to accommodate all vertices
        int nodesPerCircle = (int) Math.ceil(vertices.size() / (double) circles); // Number of nodes per circle
        int nodesInCurrentCircle = 0;
        int currentRadius = radius;
        for (String vertex : vertices) {
            int x = (int) (centerX + currentRadius * Math.cos(angle));
            int y = (int) (centerY + currentRadius * Math.sin(angle));
            vertexPositions.put(vertex, new Point(x, y));
            angle += angleStep;
            nodesInCurrentCircle++;
            if (nodesInCurrentCircle >= nodesPerCircle) {
                // Move to the next circle
                circleIndex++;
                nodesInCurrentCircle = 0;
                currentRadius += 300; // Increase the distance between circles
                angleStep = 2 * Math.PI / Math.min(vertices.size() - (circleIndex * nodesPerCircle), nodesPerCircle);
                angle = circleIndex * (2 * Math.PI / circles);
            }
        }
        return vertexPositions;
    }

}