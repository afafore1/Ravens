package graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import Raven.RavensFigure;

public class Graph {
	private Map<String, Vertex> _vertices;
	private ArrayList<Edge> _edges;
	public Graph()
	{
		_vertices = new LinkedHashMap<>();
		_edges = new ArrayList<>();
	}
	
	
	/* (non-Javadoc)
	 * @see graph.IGraph#AddVertex(int, java.lang.String)
	 */
	
	public void AddVertex(String label)
	{
		Vertex vertex = new Vertex(label);
		_vertices.put(label, vertex);
	}

	public void addVertex(String label, Vertex vertex) {
	    _vertices.put(label, vertex);
    }

	public Vertex getVertex(String label) {
	    return _vertices.get(label);
    }
	
	/* (non-Javadoc)
	 * @see graph.IGraph#AddEdge(int, graph.Vertex, graph.Vertex)
	 */

	public Map<String, Vertex> getVertices() {
	    return this._vertices;
    }

    public ArrayList<Edge> getEdges() {
	    return this._edges;
    }

	public void AddEdge(Vertex source, Vertex destination, String relationship)
	{
		Edge edge = new Edge(source, destination, relationship);
		_edges.add(edge);
	}
	
	/* (non-Javadoc)
	 * @see graph.IGraph#GetEdges()
	 */
	
	public Edge GetEdge(Vertex source, Vertex dest)
	{
		for (Edge e : _edges) {
		    if (e.getSource().equals(source) && e.getDestination().equals(dest)) {
		        return e;
            }
        }
        return null;
	}
	
	/* (non-Javadoc)
	 * @see graph.IGraph#GetVertex(int)
	 */
	

	public boolean isConnected(Vertex a, Vertex b)
	{
		for(Edge edge : _edges)
		{
			if(edge.getSource().equals(a) && edge.getDestination().equals(b) || edge.getSource().equals(b) && edge.getDestination().equals(a))
			{
				return true;
			}
		}
		return false;
	}
}
