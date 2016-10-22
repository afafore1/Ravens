package Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Graph.Graph;
import Graph.IEdge;
import Graph.IGraph;
import Graph.IVertex;
import Raven.RavensFigure;

public class Solve {
	private HashMap<String, RavensFigure> _figures;
	private int _edgeId;
	IGraph _graph;

	public Solve(HashMap<String, RavensFigure> figures) {
		_figures = figures;
		_graph = new Graph();
		_edgeId = 0;
	}

	public int FindAnswer() {
		InsertVertices();
		return AIResult();
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

	private int AIResult() {
		ArrayList<IVertex> keys = GetKeys();
		ArrayList<IVertex> answers = GetAnswers();
		CreateConnection(keys, answers);
		return GetAnswer();
	}

	private int GetAnswer() {
		double max = 0; // min in this case is 0
		IEdge maxEdge = null;
		HashMap<IEdge, Integer> matches = new HashMap<>();
		for (IEdge edge : _graph.GetEdges()) {
			if (edge.GetWeight() >= max) {
				max = edge.GetWeight();
				if (matches.containsKey(edge)) {
					matches.put(edge, matches.get(edge) + 1);
				} else {
					matches.put(edge, 1);
				}
			}
		}

		if (matches != null) {
			PrintMatches(matches);
			max = 0;
			for (IEdge e : matches.keySet()) {
				if (e.GetWeight() > max) {
					max = e.GetWeight();
					maxEdge = e;
				}
			}
			System.out.println("Max with weight " + maxEdge.GetWeight() + " source is " + maxEdge.GetSource().GetLabel()
					+ " and destination " + maxEdge.GetDestination().GetLabel() + "\n");
			return Integer.parseInt(maxEdge.GetDestination().GetLabel());
		}

		return -1; // this should not happen
	}

//	private void AdjustWeights() {
//
//	}

	private void PrintMatches(HashMap<IEdge, Integer> matches) {
		for (IEdge e : matches.keySet()) {
			System.out.println(
					e.GetWeight() + " between " + e.GetSource().GetLabel() + " and " + e.GetDestination().GetLabel()); // just
																														// debugging!!
		}
	}

	private void CreateConnection(ArrayList<IVertex> keys, ArrayList<IVertex> answers) {
		for (IVertex key : keys) {
			for (IVertex answer : answers) {
				double weightByAttribute = GetWeightByAttributes(key.GetAttributes(), answer.GetAttributes());
				System.out.println(
						"Weight between " + key.GetLabel() + " and " + answer.GetLabel() + " is " + weightByAttribute); // for
																														// debugging!
				_graph.AddEdge(_edgeId, key, answer, weightByAttribute);
				_edgeId++;
			}
		}
	}

	// if attributes are exactly the same, give it weight of 5, otherwise
	// increment by 1
	private double GetWeightByAttributes(ArrayList<HashMap<String, String>> key, ArrayList<HashMap<String, String>> ans) {
		double weight = 0;
		for (int i = 0; i < key.size(); i++) {
			for (int j = 0; j < ans.size(); j++) {
				HashMap<String, String> keyAttr = key.get(i);
				HashMap<String, String> ansAttr = ans.get(j);
				if (keyAttr.equals(ansAttr)) {
					weight = .75;
					return weight;
				} else {
					ArrayList<HashMap<String, String>> potentials = FigureOut(key);
					weight = FindMatchInAnswers(potentials, ansAttr);
				}
			}
		}
		return weight;
	}

	private double FindMatchInAnswers(ArrayList<HashMap<String, String>> potentials, HashMap<String, String> ansAttr) {
		double weight = 0;
		double maxWeight = 0;
		for (int i = 0; i < potentials.size(); i++) {
			HashMap<String, String> potentialAttr = potentials.get(i);
			if (potentialAttr.equals(ansAttr)) 
			{
				weight += .33;
			} 
			else 
			{
				for(String s : potentialAttr.keySet())
				{
					for(String j : ansAttr.keySet())
					{
						if(potentialAttr.get(s).equals(ansAttr.get(j)))
						{
							weight += .11;
						}
					}
				}
			}
			if(weight > maxWeight)
			{
				maxWeight = weight;
			}
		}
		return maxWeight;
	}

	private ArrayList<HashMap<String, String>> FigureOut(ArrayList<HashMap<String, String>> key) {
		ArrayList<HashMap<String, String>> potentials = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < key.size(); i++)
		{
			HashMap<String, String> keyMap = key.get(i);
			System.out.println("KeyMap"+keyMap);
		}
		return potentials;
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
