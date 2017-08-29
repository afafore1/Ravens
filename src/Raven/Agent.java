package Raven;


import graph.*;

import java.util.*;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures: public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
    private static int count = 0;
	private String name;
	private Map<String, RavensFigure> figureHashMap;
	private HashMap<String, Graph> answersMap = new LinkedHashMap<>();
	private HashMap<String, Graph> givenMap = new LinkedHashMap<>();
	private boolean isVerbal;
	// this is what we know
	private RavensFigure _ravensFigure_a;
	private RavensFigure _ravensFigure_b;
	private RavensFigure _ravensFigure_c;

	/**
	 * The default constructor for your Agent. Make sure to execute any
	 * processing necessary before your Agent starts solving problems here.
	 * 
	 * Do not add any variables to this signature; they will not be used by
	 * main().
	 * 
	 */
	public Agent() {

	}

	/**
	 * The primary method for solving incoming Raven's Progressive Matrices. For
	 * each problem, your Agent's Solve() method will be called. At the
	 * conclusion of Solve(), your Agent should return an int representing its
	 * answer to the question: 1, 2, 3, 4, 5, or 6. Strings of these ints are
	 * also the Names of the individual RavensFigures, obtained through
	 * RavensFigure.getName(). Return a negative number to skip a problem.
	 * 
	 * Make sure to return your answer *as an integer* at the end of Solve().
	 * Returning your answer as a string may cause your program to crash.
	 * 
	 * @param problem
	 *            the RavensProblem your agent should solve
	 * @return your Agent's answer to this problem
	 */
	public int Solve(RavensProblem problem) {
		name = problem.getName();
		figureHashMap = problem.getFigures();
		isVerbal = problem.hasVerbal();

		if (name.contains("Basic Problem B"))  {
            System.out.println("Solving problem "+ ++count);
            makeRepresentation();
			if (isVerbal) {

			}
		}

		return -1;
	}

	private void makeRepresentation() {
	    createMap();
	    createRelationship();
//	    for (String key : givenMap.keySet()) {
//	        Graph graph = givenMap.get(key);
//            System.out.println(key + " -> " + graph.getVertex(key).getProperties());
//        }
//
//        for (String key : answersMap.keySet()) {
//            Graph graph = answersMap.get(key);
//            System.out.println(key + " -> " + graph.getVertex(key).getProperties());
//        }
    }

    private void createRelationship() {
	    // create relationship between A and B
        Graph A = givenMap.get("A");
        Graph B = givenMap.get("B");
        createRelationshipInGraph(A);
        createRelationshipInGraph(B);
        Map<String, String> relationship = getRelationshipBetweenGraphs(A, B);
        System.out.println(relationship);
    }

    private Graph generateGraph(Graph A, Graph B, Graph C) {
	    Graph D = null; // we want to generate this based on A and B

        return D;
    }

    private Map<String, String> getRelationshipBetweenGraphs(Graph A, Graph B) {
	    Map<String, Vertex> aVertexMap = A.getVertices();
	    Map<String, Vertex> bVertexMap = B.getVertices();
	    Map<String, String> relationship = new LinkedHashMap<>();
	    int maxSize = aVertexMap.size() > bVertexMap.size() ? aVertexMap.size() : bVertexMap.size();
	    for (int i = 0; i < maxSize; i++) {
            Vertex x = getVertex(i, aVertexMap);
            Vertex y = getVertex(i, bVertexMap);
	        if (x == null) {
	            relationship.put(y.getLabel(), "deleted");
            } else if (y == null) {
	            relationship.put(x.getLabel(), "deleted");
            } else {
                getDifference(relationship, x, y);
            }
        }
        return relationship;
    }

    private Vertex getVertex(int index, Map<String, Vertex> vertexMap) {
	    if (vertexMap.size() <= index) return null;
	    return (new ArrayList<>(vertexMap.values()).get(index));
    }

    private void getDifference(Map<String, String> relationship, Vertex x, Vertex y) {
	    Map<String, String> xProperties = x.getProperties();
	    Map<String, String> yProperties = y.getProperties();
	    for (String key : xProperties.keySet()) {
	        if (yProperties.get(key) != null) {
                String xValue = xProperties.get(key);
                String yValue = yProperties.get(key);
                if (xValue.equals(yValue)) {
	                relationship.put(key, "unchanged");
                } else {
                    System.out.println(xValue + " - " + yValue);
                }
            }
        }
    }

    private void createRelationshipInGraph(Graph graph) {
	    Map<String, Vertex> vertexMap = graph.getVertices();
	    Set<String> keys = vertexMap.keySet();
	    for (String key : keys) {
	        Vertex vertex = vertexMap.get(key);
	        Map<String, String> properties = vertex.getProperties();
	        for (String k : properties.keySet()) {
	            if (keys.contains(properties.get(k))) { // there is a relationship between these
	                Edge edge = new Edge(vertexMap.get(properties.get(k)), vertex, k);
                    System.out.println(edge.getSource().getLabel() + " " + edge.getDestination().getLabel() + " " + edge.getRelationship());
                }
            }
        }
    }

    private void createMap() {
        for (String key : figureHashMap.keySet()) {
            Graph graph = new Graph();
            setProperties(figureHashMap.get(key), graph);
            if (key.equals("A") || key.equals("B") || key.equals("C")) {
                givenMap.put(key, graph);
            } else {
                answersMap.put(key, graph);
            }
        }
    }

    private void setProperties(RavensFigure ravensFigure, Graph graph) {
	    HashMap<String, RavensObject> ravensObjectHashMap = ravensFigure.getObjects();
	    for (String key : ravensObjectHashMap.keySet()) {
	        Vertex vertex = new Vertex(key);
	        RavensObject ravensObject = ravensObjectHashMap.get(key);
	        vertex.setProperties(ravensObject.getAttributes());
	        graph.addVertex(vertex.getLabel(), vertex);
        }
    }
}