package org.example.Visualizer;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GraphVisualizer {
    public static void visualizeGraph(ListenableGraph<String, DefaultEdge> g) {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            Map<String, Object> vertexMap = new HashMap<>();
            for (String vertex : g.vertexSet()) {
                Object vertexObject = graph.insertVertex(parent, null, vertex, 20, 20, 80, 30);
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
        mxIGraphLayout layout = new mxCompactTreeLayout(graph);
        layout.execute(parent);
        JFrame frame = new JFrame("Graph Visualization");
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        frame.getContentPane().add(graphComponent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 600));
        frame.pack();
        frame.setVisible(true);
    }
}
