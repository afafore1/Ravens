package Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Raven.RavensFigure;

public interface IVertex {

	int GetId();
	
	String GetLabel();

	HashSet<Edge> GetEdges();

	boolean IsVisited();

	void MarkVisited();
	
	void ToggleKey();
	
	RavensFigure GetFigure();
	
	boolean IsKey();

	ArrayList<HashMap<String, String>> GetAttributes();
}