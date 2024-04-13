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
            Map<String, Point> vertexPositions = generateCircularLayout(600, 400, 200, vertices); // Adjust parameters as needed
            for (String vertex : vertices) {
                Point position = vertexPositions.get(vertex);
                Object vertexObject = graph.insertVertex(parent, null, vertex, position.x, position.y, 80, 30);
                vertexMap.put(vertex, vertexObject);
                // Insert adjacent vertices
                System.out.println("Vertex: " + vertex);
                System.out.println("Adjacents: " + g.edgesOf(vertex));
                for (DefaultEdge edge : g.edgesOf(vertex)) {
                    String adjacentVertex = g.getEdgeTarget(edge);
                    if (adjacentVertex.equals(vertex)) {
                        adjacentVertex = g.getEdgeSource(edge);
                    }
                    System.out.println("Adjacent vertex: " + adjacentVertex);
                    if (!vertexMap.containsKey(adjacentVertex)) {
                        // Add adjacent vertices that have not been added yet
                        Point adjacentPosition = new Point(position.x + 100, position.y); // Adjust position as needed
                        System.out.println("Adding adjacent vertex: " + adjacentVertex);
                        Object adjacentVertexObject = graph.insertVertex(parent, null, adjacentVertex, adjacentPosition.x, adjacentPosition.y, 80, 30);
                        vertexMap.put(adjacentVertex, adjacentVertexObject);
                    }
                }
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

        // Set very large spacing between nodes
        mxIGraphLayout layout = new mxCompactTreeLayout(graph) {
            @Override
            public mxRectangle getVertexBounds(Object vertex) {
                mxRectangle bounds = super.getVertexBounds(vertex);
                bounds.setWidth(bounds.getWidth() + 200); // Increase width to create space
                return bounds;
            }
        };

        layout.execute(parent);

        JFrame frame = new JFrame(title);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        frame.getContentPane().add(graphComponent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 800));
        frame.pack();
        frame.setVisible(true);
    }



    public static Map<String, Point> generateCircularLayout(int centerX, int centerY, int radius, Set<String> vertices) {
        Map<String, Point> vertexPositions = new HashMap<>();
        double angleStep = 2 * Math.PI / vertices.size();
        double angle = 0;
        for (String vertex : vertices) {
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            vertexPositions.put(vertex, new Point(x, y));
            angle += angleStep;
        }
        return vertexPositions;
    }
}