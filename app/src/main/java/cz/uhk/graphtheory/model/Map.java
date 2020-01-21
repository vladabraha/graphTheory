package cz.uhk.graphtheory.model;


import java.util.ArrayList;


public class Map {
    private ArrayList<Edge> edges;
    private ArrayList<Coordinate> nodes;
    private ArrayList<Edge> redEdgesList = new ArrayList<>();
    private ArrayList<Coordinate> redNodes = new ArrayList<>();


    public Map(ArrayList<Edge> edges, ArrayList<Coordinate> nodes) {
        this.edges = edges;
        this.nodes = nodes;
    }

    public Map(ArrayList<Edge> edges, ArrayList<Coordinate> nodes, ArrayList<Edge> redEdgesList) {
        this.edges = edges;
        this.nodes = nodes;
        this.redEdgesList = redEdgesList;
    }

    public Map(ArrayList<Edge> edges, ArrayList<Coordinate> nodes, ArrayList<Edge> redEdgesList, ArrayList<Coordinate> redNodes) {
        this.edges = edges;
        this.nodes = nodes;
        this.redEdgesList = redEdgesList;
        this.redNodes = redNodes;
    }



    public Map(Map map) {
        ArrayList<Edge> customLinesCopied = new ArrayList<>();
        ArrayList<Coordinate> nodesCopied = new ArrayList<>();
        ArrayList<Edge> redLineListCopied = new ArrayList<>();
        ArrayList<Coordinate> redNodesCopied = new ArrayList<>();

        for (Edge edge : map.edges) {
            Coordinate from = new Coordinate(edge.getFrom().x, edge.getFrom().y);
            Coordinate to = new Coordinate(edge.getTo().x, edge.getTo().y);
            customLinesCopied.add(new Edge(from, to));
        }

        for (Coordinate coordinate : map.nodes) {
            nodesCopied.add(new Coordinate(coordinate.x, coordinate.y));
        }

        if (map.getRedNodes() != null){
            for (Coordinate coordinate : map.redNodes) {
                redNodesCopied.add(new Coordinate(coordinate.x, coordinate.y));
            }
        }

        for (Edge edge : map.redEdgesList) {
            Coordinate from = new Coordinate(edge.getFrom().x, edge.getFrom().y);
            Coordinate to = new Coordinate(edge.getTo().x, edge.getTo().y);
            redLineListCopied.add(new Edge(from, to));
        }

        this.edges = customLinesCopied;
        this.nodes = nodesCopied;
        this.redEdgesList = redLineListCopied;
        this.redNodes = redNodesCopied;
    }

    public ArrayList<Edge> getRedEdgesList() {
        return redEdgesList;
    }

    public void setRedEdgesList(ArrayList<Edge> redEdgesList) {
        this.redEdgesList = redEdgesList;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public ArrayList<Coordinate> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Coordinate> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Coordinate> getRedNodes() {
        return redNodes;
    }

    public void setRedNodes(ArrayList<Coordinate> redNodes) {
        this.redNodes = redNodes;
    }
}
