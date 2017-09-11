package Raven;


import graph.*;
import neo4j.Connection;
import org.neo4j.driver.internal.InternalRecord;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Relationship;

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
    private static int answer = -1;
	private String name;
	private Map<String, RavensFigure> figureHashMap;
	private HashMap<String, Graph> answersMap = new LinkedHashMap<>();
	private HashMap<String, Graph> givenMap = new LinkedHashMap<>();
	private boolean isVerbal;
	// this is what we know

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
                System.out.println(answer);
                return answer;
			}
		}

		return -1;
	}

	private void makeRepresentation() {
	    createMap();
        Map<String, Map<String, String>> relationship = createRelationship();
        //System.out.println(relationship);
        Graph C = givenMap.get("C");
        Graph D = generateGraph(relationship, C);
        Map<String, Map<String, String>> Srelationship = getRelationshipBetweenGraphs(C, D);
        //System.out.println(Srelationship);
        getAnswer(D);
        //createGraph();
    }

    private void createGraph() {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder relationship = new StringBuilder();
        relationship.append("CREATE\n");
        String stmt;
        for (String key : figureHashMap.keySet()) {
            RavensFigure ravensFigure = figureHashMap.get(key);
            stringBuilder.append("CREATE (" + getValue(ravensFigure.getName()) + ":" + "RavensFigure {name:'" + getValue(ravensFigure.getName()) + "', visual:'" + ravensFigure.getVisual() + "'})\n");
            HashMap<String, RavensObject> ravensObjectHashMap = ravensFigure.getObjects();
            for (String figure : ravensObjectHashMap.keySet()) {
                RavensObject ravensObject = ravensObjectHashMap.get(figure);
                stringBuilder.append("CREATE (" + ravensObject.getName() + ":RavensObject {name :'" + getValue(ravensObject.getName()) + "', ");
                HashMap<String, String> attr = ravensObject.getAttributes();
                for (String a : attr.keySet()) {
                    stringBuilder.append(a + ": '" + attr.get(a) + "', ");
                }
                stringBuilder.setLength(stringBuilder.length() - 2); //remove last comma
                stringBuilder.append("})\n");
                relationship.append("(" + ravensObject.getName() + ") -[:IS_OBJECT_OF]->(" + getValue(ravensFigure.getName()) + "),");
            }
            relationship.append("\n");
        }
        relationship.setLength(relationship.length() - 2);
        stmt = stringBuilder.toString();
        stmt += relationship.toString() + "\n";
        stmt += "CREATE \n";
        stmt += createGraphRelationship("A", "B");
        stmt += createGraphRelationship("B", "C");
        stmt += createGraphRelationship("C", "1");
        stmt += createGraphRelationship("C", "2");
        stmt += createGraphRelationship("C", "3");
        stmt += createGraphRelationship("C", "4");
        stmt += createGraphRelationship("C", "5");
        stmt += createGraphRelationship("C", "6");
        stmt = stmt.substring(0, stmt.length() - 2);
        Connection.create(stmt);
        findAnswer();
        System.exit(0);
    }

    private String createGraphRelationship(String a, String b) {
	    StringBuilder query = new StringBuilder();
	    RavensFigure A = figureHashMap.get(a);
	    RavensFigure B = figureHashMap.get(b);
	    HashMap<String, RavensObject> aObjects = A.getObjects();
        HashMap<String, RavensObject> bObjects = B.getObjects();
        List<RavensObject> aRavObjects = new ArrayList<>(aObjects.values());
        List<RavensObject> bRavObjects = new ArrayList<>(bObjects.values());
        if (aRavObjects.size() == bRavObjects.size()) {
            for (int i = 0; i < bRavObjects.size(); i++) {
                RavensObject aRaven = aRavObjects.get(i);
                RavensObject bRaven = bRavObjects.get(i);
                if (aRaven.getAttributes().size() != bRaven.getAttributes().size()) {
                    bRaven = getRavensObject(aRaven.getAttributes().size(), bRaven, bRavObjects);
                }
                query.append("(" + aRaven.getName() + ")-[:relates_to {");
                HashMap<String, String> aprop = aRaven.getAttributes();
                HashMap<String, String> bprop = bRaven.getAttributes();
                for (String kk : aprop.keySet()) {
                    if (bprop.get(kk) != null) {
                        String aP = aprop.get(kk);
                        String bP = bprop.get(kk);
                        if (aP.equals(bP) || kk.equals("inside")) {
                            query.append(kk + ": 'unchanged', ");
                        } else {
                            String diff = understandDifference(aP, bP);
                            query.append(kk + ": '" + diff + "', ");
                        }
                    } else {
                        query.append(kk + ": 'deleted', ");
                    }
                }
                query.setLength(query.length() - 2);
                query.append("}]->(" + bRaven.getName() + "),\n");
            }
        } else {
            query.append(": 'deleted',");
        }
        //query.append("\n");
	    return query.toString();
    }

    private Vertex getVertex(int size, Vertex originalVertex, List<Vertex> vertices) {
	    for (Vertex v : vertices) {
	        if (v.getProperties().size() == size) {
	            return v;
            }
        }
        return originalVertex;
    }

    private RavensObject getRavensObject(int size, RavensObject originalObject, List<RavensObject> objects) {
	    for (RavensObject r : objects) {
	        if (r.getAttributes().size() == size) {
	            return r;
            }
        }
	    return originalObject;
    }

    private int findAnswer() {
	    String query = "Match (r1:RavensObject)-[:IS_OBJECT_OF]->(f1:RavensFigure {name: 'A'}) return r1.name";
	    List<Record> records = Connection.getRecord(query);
	    List<Record> result = new ArrayList<>();
	    for (Record record : records) {
            query = "match (r1:RavensObject)-[l:relates_to]->(:RavensObject)";
	        List<Value> values = record.values();
	        String name = String.valueOf(values.get(0));
	        query += " where r1.name = "+name + " return l";
            result.addAll(Connection.getRecord(query));
        }
        List<Map<String, Object>> search = generateSearch(result);
        System.out.println(result);
	    return -1;
    }

    private List<Map<String, Object>> generateSearch(List<Record> records) {
	    List<Map<String, Object>> search = new ArrayList<>();
	    for (Record record : records) {
	        Map<String, Object> map = record.asMap();
	        for (String k : map.keySet()) {
                Relationship relationship = (Relationship) map.get(k);
                relationship.asMap();
	            search.add(relationship.asMap());
            }
        }
        return search;
    }

    private Map<String, Map<String, String>> createRelationship() {
	    // create relationship between A and B
        Graph A = givenMap.get("A");
        Graph B = givenMap.get("B");
        createRelationshipInGraph(A);
        createRelationshipInGraph(B);
        Map<String, Map<String, String>> relationship = getRelationshipBetweenGraphs(A, B);
        return relationship;
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

    private void getAnswer(Graph generatedGraph) {
	    answer = -1;
	    Map<String, Double> mapScore = new HashMap<>();
	    for (String key : answersMap.keySet()) {
	        Graph answerGraph = answersMap.get(key);
	        mapScore.put(key, getScoreBetweenGraphs(generatedGraph, answerGraph));
        }
        double max = 0;
        for (String key : mapScore.keySet()) {
	        if (mapScore.get(key) > max) {
	            max = mapScore.get(key);
	            answer = Integer.parseInt(key);
            }
        }
        System.out.println(mapScore);
    }
    /*
    if there is a match, we give a score of .5
    if there is no match, we give a score of -.3
     */
    private Double getScoreBetweenGraphs(Graph generated, Graph answer) {
        double score = 0;
        double pass = .5;
        double miss = .3;
	    Map<String, Vertex> generatedMap = generated.getVertices();
	    List<Edge> generatedEdges = generated.getEdges();
	    Map<String, Vertex> answerMap = answer.getVertices();
	    List<Edge> answerEdges = answer.getEdges();
	    score += scoreVertices(generatedMap, answerMap, pass, miss);
	    score += scoreEdges(generatedEdges, answerEdges, pass, miss);
        return score;
    }

    private Double scoreVertices(Map<String, Vertex> generatedMap, Map<String, Vertex> answerMap, double pass, double miss) {
        double score = 0;
        int maxSize = generatedMap.size() > answerMap.size() ? generatedMap.size() : answerMap.size();
        List<Vertex> genVertices = new ArrayList<>(generatedMap.values());
        List<Vertex> answerVertices = new ArrayList<>(answerMap.values());
        for (int i = 0; i < maxSize; i++) {
            if (genVertices.size() == answerVertices.size()) {
                Vertex genVertex = genVertices.get(i);
                Vertex ansVertex = answerVertices.get(i);
                if (genVertex.getProperties().size() != ansVertex.getProperties().size()) {
                    ansVertex = getVertex(genVertex.getProperties().size(), ansVertex, answerVertices);
                }
                Map<String, String> genProp = genVertex.getProperties();
                Map<String, String> ansProp = ansVertex.getProperties();
                for (String key : genProp.keySet()) {
                    if (ansProp.containsKey(key)) {
                        score += pass;
                        if (genProp.get(key).equals(ansProp.get(key))) {
                            score += pass;
                        } else {
                            score -= pass;
                        }
                    } else {
                        score -= miss;
                    }
                }
            } else {
                score -= miss;
            }
        }
        return score;
    }

    private Double scoreEdges(List<Edge> generatedEdges, List<Edge> answerEdges, double pass, double miss) {
        double score = 0;
        for (int i = 0; i < generatedEdges.size(); i++) {
            Edge e = generatedEdges.get(i);
            Edge a = null;
            try {
                a = answerEdges.get(i);
            } catch (Exception ex) {
                score -= miss;
            }
            System.out.println(e.getRelationship() + " " + a.getRelationship());
            System.out.println();
        }
        return score;
    }

    private void getAnswer(Graph graph, Map<String, Map<String, String>> knownRelationship) {
	    answer = -1;
	    for (String key : answersMap.keySet()) {
            Map<String, Map<String, String>> relationship = getRelationshipBetweenGraphs(graph, answersMap.get(key));
            for (String k : relationship.keySet()) {
                if (knownRelationship.get(k) != null) {
                    Map<String, String> knownMap = knownRelationship.get(k);
                    Map<String, String> ansMap = relationship.get(k);
                    if (knownMap.equals(ansMap)) {
                        answer = Integer.parseInt(key);
                    } else {

                    }
                }
            }
        }
    }

    private void removeDeletedObject (Graph D, Map<String, String> relation) {
        Graph A = givenMap.get("A");
        for (String k : relation.keySet()) {
            Map<String, String> aproperties = A.getVertex(k).getProperties();
            Iterator<Vertex> vertexIterator = D.getVertices().values().iterator();
            boolean isRemoved = false;
            while (vertexIterator.hasNext()) {
                if (isMapMatch(vertexIterator.next().getProperties(), aproperties)) {
                    if (!isRemoved) {
                        vertexIterator.remove();
                        isRemoved = true;
                    }
                }
            }
        }
    }

    private boolean isMapMatch(Map<String, String> mapA, Map<String, String> mapB) {
        for (String p : mapA.keySet()) {
            if (!p.equals("inside") && mapB.containsKey(p) && mapB.get(p).equals(mapA.get(p))) {
                return true;
            }
        }
        return false;
    }

    private Graph generateGraph(Map<String, Map<String, String>> relationship, Graph C) {
	    Graph D = new Graph(); // we want to generate this based on A and B
        Graph B = givenMap.get("B");
        int index = 0;
        List<Vertex> vertices = new ArrayList<>(C.getVertices().values());
        List<Vertex> bVertices = new ArrayList<>(B.getVertices().values());
        for (String key : relationship.keySet()) {
            Map<String, String> relation = relationship.get(key);
            if (relation.size() == 1) {
                //something got deleted
                removeDeletedObject(D, relation);
                continue;
            }
            Vertex vertex = vertices.get(index);
            Vertex bVertex = bVertices.get(index);
            Map<String, String> properties = vertex.getProperties();
            Map<String, String> bProperties = bVertex.getProperties();
            Map<String, String> generatedProperty = new HashMap<>();
            for (String k : properties.keySet()) {
                if (relation.get(k) == null || relation.get(k).equals("unchanged")) {
                    generatedProperty.put(k, properties.get(k));
                } else {
                    if (relation.get(k).equals("changed")) {
                        generatedProperty.put(k, bProperties.get(k));
                    } else {
                        String value = properties.get(k);
                        try {
                            int v = Integer.parseInt(value);
                            int generatedValue = Math.abs(v - Integer.parseInt(relation.get(k)));
                            generatedProperty.put(k,String.valueOf(generatedValue));
                        } catch (Exception e) {
                            String [] arr = value.split("-");
                            if (arr.length > 1) {
                                if (relation.get(k).equals("reflected")) {
                                    for (String a : arr) {
                                        if (a.equals("right")) {
                                            value = value.replace("right", "left");
                                        } else if (a.equals("left")) {
                                            value = value.replace("left", "right");
                                        }
                                    }
                                } else if (relation.get(k).equals("rotated")) {
                                    for (String a : arr) {
                                        if (a.equals("bottom")) {
                                            value = value.replace("bottom", "top");
                                        } else if (a.equals("top")) {
                                            value = value.replace("top", "bottom");
                                        }
                                    }
                                }
                                generatedProperty.put(k, value);
                            }
                        }
                    }
                }
            }
            Vertex generatedVertex = new Vertex(String.valueOf(index));
            generatedVertex.setProperties(generatedProperty);
            D.addVertex(String.valueOf(index), generatedVertex);
            index++;
        }
        if (givenMap.get("A").getVertices().size() < C.getVertices().size() &&
                B.getVertices().size() < C.getVertices().size()) {
            int size = B.getVertices().size();
            int counter = 0;
            for (String key : C.getVertices().keySet()) {
                counter++;
                if (counter <= size) continue;
                Vertex vertex = C.getVertex(key);
                D.addVertex(key, vertex);

            }
        }
        return D;
    }

    private Map<String, Map<String, String>> getRelationshipBetweenGraphs(Graph A, Graph B) {
	    Map<String, Vertex> aVertexMap = A.getVertices();
	    Map<String, Vertex> bVertexMap = B.getVertices();
	    Map<String, Map<String, String>> relationship = new LinkedHashMap<>();
	    int maxSize = aVertexMap.size() > bVertexMap.size() ? aVertexMap.size() : bVertexMap.size();
	    for (int i = 0; i < maxSize; i++) {
            Vertex x = getVertex(i, aVertexMap);
            Vertex y = getVertex(i, bVertexMap);
	        if (x == null) {
	            Map<String, String> rel = new LinkedHashMap<>();
                rel.put(y.getLabel(), "deleted");
                relationship.put(i + " " + i, rel);
            } else if (y == null) {
                Map<String, String> rel = new LinkedHashMap<>();
                rel.put(x.getLabel(), "deleted");
	            relationship.put(i + " " + i, rel);
            } else {
                getDifference(i, relationship, x, y);
            }
        }
        System.out.println(relationship);
        return relationship;
    }

    private Vertex getVertex(int index, Map<String, Vertex> vertexMap) {
	    if (vertexMap.size() <= index) return null;
	    return (new ArrayList<>(vertexMap.values()).get(index));
    }

    private void getDifference(int index, Map<String, Map<String, String>> relationship, Vertex x, Vertex y) {
	    Map<String, String> xProperties = x.getProperties();
	    Map<String, String> yProperties = y.getProperties();
        HashMap<String, String> rel = new LinkedHashMap<>();
	    for (String key : xProperties.keySet()) {
	        if (yProperties.get(key) != null) {
                String xValue = xProperties.get(key);
                String yValue = yProperties.get(key);
                if (xValue.equals(yValue) || key.equalsIgnoreCase("inside")) {
                    rel.put(key, "unchanged");
                } else {
                    String difference = understandDifference(xValue, yValue);
                    rel.put(key, difference);
                }
            } else {
	            rel.put(key, "deleted");
            }
        }
        relationship.put(index + " " + index, rel);
    }

    private String understandDifference(String xValue, String yValue) {
	    String difference = null;
	    Integer x;
	    Integer y;
	    try {
	        x = Integer.parseInt(xValue);
	        y = Integer.parseInt(yValue);
	        difference = String.valueOf(Math.abs(x - y));
        } catch (Exception e){
	        String [] xArr = xValue.split("-");
	        String [] yArr = yValue.split("-");
	        if (xArr.length > 1 && xArr.length == yArr.length) {
	            for (int i = 0; i < xArr.length; i++) {
	                String a = xArr[i];
	                String b = yArr[i];
	                if (getTransition(a, b) == null) continue;
	                difference = getTransition(a, b);
                }
            } else {
	            difference = "changed";
            }
	        return difference;
        }
        return difference;
    }

    private String getTransition(String a, String b) {
	    if ((a.equalsIgnoreCase("bottom") && b.equalsIgnoreCase("top")) ||
                (b.equalsIgnoreCase("bottom") && a.equalsIgnoreCase("top"))) return "rotated";
	    if ((a.equalsIgnoreCase("right") && b.equalsIgnoreCase("left")) ||
                (b.equalsIgnoreCase("right") && a.equalsIgnoreCase("left"))) return "reflected";
	    return null;
    }

    private String getValue(String value) {
        switch (value) {
            case "1":
                return "one";
            case "2":
                return "two";
            case "3":
                return "three";
            case "4":
                return "four";
            case "5":
                return "five";
            case "6":
                return "six";
        }
	    return value;
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