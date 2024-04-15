package org.example.GraphNode;

import java.util.List;
import java.util.Objects;

public class GraphNode {
    private String name;
    private List<String> adjacencyList;

    public boolean isVisited;


    public GraphNode(String name, List<String> adjacencyList) {
        this.name = name;
        this.adjacencyList = adjacencyList;
        this.isVisited=false;
    }
    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAdjacencyList() {
        return adjacencyList;
    }

    public void setAdjacencyList(List<String> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    @Override
    public String toString() {
        return "GraphNode{" +
                "name='" + name + '\'' +
                ", adjacencyList=" + adjacencyList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return Objects.equals(name, graphNode.name) &&
                Objects.equals(adjacencyList, graphNode.adjacencyList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, adjacencyList);
    }
}
