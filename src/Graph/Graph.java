package Graph;

import java.util.ArrayList;
import java.util.HashMap;

import Raven.RavensFigure;
import Raven.RavensObject;

public class Graph implements IGraph {
	private HashMap<Vertex, Edge> _G;
	private ArrayList<IVertex> _vertices;
	private ArrayList<IEdge> _edges;
	public Graph()
	{
		_G = new HashMap<>();
		_vertices = new ArrayList<>();
		_edges = new ArrayList<>();
	}
	
	
	/* (non-Javadoc)
	 * @see Graph.IGraph#AddVertex(int, java.lang.String)
	 */
	@Override
	public void AddVertex(int id, String label, RavensFigure ravensFigure)
	{
		IVertex vertex = new Vertex(id, label, ravensFigure);
		_vertices.add(vertex);
		//_G.put(vertex, arg1)
	}
	
	/* (non-Javadoc)
	 * @see Graph.IGraph#AddEdge(int, Graph.IVertex, Graph.IVertex)
	 */
	@Override
	public void AddEdge(int id, IVertex source, IVertex destination, double weight)
	{
		IEdge edge = new Edge(id, source, destination, weight);
		_edges.add(edge);
	}

	/* (non-Javadoc)
	 * @see Graph.IGraph#GetVertices()
	 */
	@Override
	public ArrayList<IVertex> GetVertices()
	{
		return _vertices;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IGraph#GetEdges()
	 */
	@Override
	public ArrayList<IEdge> GetEdges()
	{
		return _edges;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IGraph#GetVertex(int)
	 */
	@Override
	public IVertex GetVertex(int id)
	{
		for(IVertex v : _vertices)
		{
			if(v.GetId() == id)
			{
				return v;
			}
		}
		return null;
	}
	
	public boolean isConnected(IVertex a, IVertex b)
	{
		for(IEdge edge : _edges)
		{
			if(edge.GetSource().equals(a) && edge.GetDestination().equals(b) || edge.GetSource().equals(b) && edge.GetDestination().equals(a))
			{
				return true;
			}
		}
		return false;
	}
}
