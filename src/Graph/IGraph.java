package Graph;

import java.util.ArrayList;

import Raven.RavensFigure;
import Raven.RavensObject;

public interface IGraph {

	void AddEdge(int id, IVertex source, IVertex destination, double weight);

	ArrayList<IVertex> GetVertices();

	ArrayList<IEdge> GetEdges();

	IVertex GetVertex(int id);

	void AddVertex(int id, String label, RavensFigure ravensFigure);
	
	boolean isConnected(IVertex a, IVertex b);

}