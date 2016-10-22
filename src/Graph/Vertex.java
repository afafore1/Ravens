package Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Raven.RavensFigure;
import Raven.RavensObject;

public class Vertex implements IVertex {
	private String _label;
	private boolean _isVisited;
	private RavensFigure _ravensFigure;
	private HashSet<Edge> _edgeSet;
	private boolean _isKey;
	private int _id;
	
	public Vertex(int id, String label, RavensFigure ravensFigure)
	{
		_id = id;
		_label = label;
		_ravensFigure = ravensFigure;
		_isVisited = false;
		_isKey = false;
		_edgeSet = new HashSet<>();
	}
	
	
	public int GetId()
	{
		return _id;
	}
	
	public RavensFigure GetFigure()
	{
		return this._ravensFigure;
	}
	
	
	public ArrayList<HashMap<String, String>> GetAttributes()
	{
		ArrayList<HashMap<String, String>> attributes = new ArrayList<HashMap<String,String>>();
		for(String s : GetFigure().getObjects().keySet())
		{
			RavensObject ravensObject = GetFigure().getObjects().get(s);
			attributes.add(ravensObject.getAttributes());
		}
		return attributes;
	}
	
	public void ToggleKey()
	{
		this._isKey = !this._isKey;
	}
	
	public boolean IsKey()
	{
		return this._isKey;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IVertex#GetLabel()
	 */
	@Override
	public String GetLabel()
	{
		return _label;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IVertex#GetEdges()
	 */
	@Override
	public HashSet<Edge> GetEdges()
	{
		return _edgeSet;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IVertex#IsVisited()
	 */
	@Override
	public boolean IsVisited()
	{
		return _isVisited;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IVertex#ToggleVisited()
	 */
	@Override
	public void MarkVisited()
	{
		_isVisited = true;
	}
}
