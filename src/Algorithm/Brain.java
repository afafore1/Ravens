package Algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import Graph.Graph;
import Graph.IEdge;
import Graph.IGraph;
import Graph.IVertex;
import Raven.RavensFigure;

public class Brain {
	private HashMap<String, RavensFigure> _figures;
	private int _edgeId;
	IGraph _graph;

	public Brain(HashMap<String, RavensFigure> figures)
	{
		_figures = figures;
		_graph = new Graph();
		_edgeId = 0;
	}
	
	public int Think() {
		InsertVertices();
		return Thinking();
	}
	
	private void InsertVertices() {
		int graphId = 0;
		for (String name : _figures.keySet()) {
			_graph.AddVertex(graphId, name, _figures.get(name));
			if (name.equals("A") || name.equals("B") || name.equals("C")) {
				_graph.GetVertex(graphId).ToggleKey();
			}
			graphId++;
		}
	}
	
	private int Thinking() {
		ArrayList<IVertex> keys = GetKeys();
		ArrayList<IVertex> answers = GetAnswers();
		String answerGroup = FindAnswerGrouping(keys, 2);
		ArrayList<HashMap<String, String>> answerProp = GetProposedProperties(answerGroup, answers, keys);
		GetAnswerProperties(answerProp, keys);
		System.out.println(answerProp);
		return -1;
	}
	
	private String FindAnswerGrouping(ArrayList<IVertex> keys, int matrixSize) //2*2, 3*3 ?
	{
		keys.get(0).MarkVisited(); // mark first one ? what if first is empty match?
		for(IVertex key : keys)
		{
			for(IVertex k : keys)
			{
				if(!_graph.isConnected(key, k) && !k.equals(key)) // if no edge exist already
				{
					if(IsSameAttributes(key, k))
					{
						//System.out.println(key.GetLabel() +" and "+k.GetLabel()+" has prop \n"+key.GetLabel()+" prop are "+key.GetAttributes()+"\n"+k.GetLabel()+" prop are "+k.GetAttributes());
						_graph.AddEdge(_edgeId, key, k, 5); // create edge between them
						_edgeId++;
						k.MarkVisited();
					}
				}
			}
		}
		
		for(IVertex key : keys)
		{
			if(!key.IsVisited()) return key.GetLabel(); // could be multiple ?
		}
//		for(IEdge edge : _graph.GetEdges())
//		{
//			System.out.println(edge.GetSource().GetLabel() +" and "+edge.GetDestination().GetLabel());
//		}
		return "ALL";
	}
	
	private ArrayList<HashMap<String, String>> GetProposedProperties(String answerGroup, ArrayList<IVertex> answers, ArrayList<IVertex> keys)
	{
		ArrayList<HashMap<String, String>> answerProp = new ArrayList<>();
		if(answerGroup.equals("ALL"))
		{
			answerProp = keys.get(0).GetAttributes();
		}
		else
		{
			for(IVertex key : keys)
			{
				if(key.GetLabel().equals(answerGroup))
				{
					answerProp = key.GetAttributes();
				}
			}
		}
		return answerProp;
	}
	
	//forgive me for this horrible solution :(
	private ArrayList<HashMap<String, String>> GetAnswerProperties(ArrayList<HashMap<String, String>> answerProp, ArrayList<IVertex> keys)
	{
		ArrayList<HashMap<String, String>> rightProperties = new ArrayList<>();
		for(IVertex key : keys)
		{
			for(HashMap<String, String> keyAttr : key.GetAttributes())
			{
				for(HashMap<String, String> ansAttr : answerProp)
				{
					for(String s : keyAttr.keySet())
					{
						for(String a : ansAttr.keySet())
						{
							
						}
					}
				}
			}
		}
		return rightProperties;
	}
	
	private boolean IsSameAttributes(IVertex key, IVertex k)
	{
		boolean isSame = false;
		for(HashMap<String, String> m : key.GetAttributes())
		{
			for(HashMap<String, String> s : k.GetAttributes())
			{
				if(m.size() == s.size())
				{
					isSame = true;
				}
				else
				{
					isSame = false;
				}
			}
		}
		return isSame;
	}
	
	private ArrayList<IVertex> GetKeys() {
		ArrayList<IVertex> keys = new ArrayList<>();
		for (IVertex vertex : _graph.GetVertices()) {
			if (vertex.IsKey()) {
				keys.add(vertex);
			}
		}
		return keys;
	}

	private ArrayList<IVertex> GetAnswers() {
		ArrayList<IVertex> answers = new ArrayList<>();
		for (IVertex vertex : _graph.GetVertices()) {
			if (!vertex.IsKey()) {
				answers.add(vertex);
			}
		}
		return answers;
	}
}
