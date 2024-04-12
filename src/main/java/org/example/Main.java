package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.example.GraphManager.GraphManager;
import org.example.GraphNode.GraphNode;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/java/org/example/dependencies_out.json";

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(new File(filePath));

            GraphManager graphManager = GraphManager.getInstance();

            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();

                String nodeName = entry.getKey();
                List<String> adjacencyList = objectMapper.convertValue(entry.getValue(), new TypeReference<List<String>>() {});

                GraphNode node = new GraphNode(nodeName, adjacencyList);

                graphManager.addNode(node);
            }
            visualizeGraph(graphManager);

        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }

    public static void visualizeGraph(GraphManager graphManager) {
        List<GraphNode> nodes = graphManager.getGraphList();

        ListenableGraph<String, DefaultEdge> g =
                new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));

        for (GraphNode node : nodes) {
            g.addVertex(node.getName());
        }

        for (GraphNode node : nodes) {
            String sourceNode = node.getName();
            for (String targetNode : node.getAdjacencyList()) {
                if (g.containsVertex(sourceNode) && g.containsVertex(targetNode)) {
                    g.addEdge(sourceNode, targetNode);
                }
            }
        }
        visualizeJGraphX(g);
    }


    public static void visualizeJGraphX(ListenableGraph<String, DefaultEdge> g) {
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
