package Algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
		answerProp = GetAnswerProperties(answerProp, keys);
		if(!answerProp.isEmpty())
		{
			System.out.println("Answer is "+GetAnswer(answers, answerProp));
			return GetAnswer(answers, answerProp);
		}
		System.out.println("This is proposal "+answerProp);
		return -1;
	}
	
	private int GetAnswer(ArrayList<IVertex> answers, ArrayList<HashMap<String, String>> answerProp)
	{
			for(IVertex ans : answers)
			{
				if(ans.GetAttributes().equals(answerProp))
				{
					return Integer.parseInt(ans.GetLabel());
				}
			}
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
	
	private ArrayList<HashMap<String, String>> GetAnswerProperties(ArrayList<HashMap<String, String>> answerProp, ArrayList<IVertex> keys)
	{
		ArrayList<HashMap<String, String>> rightProperties = new ArrayList<>();
		HashSet<HashMap<String, String>> differencesInKeys = new HashSet<>();
		HashMap<String, Integer> diffInKeys = new HashMap<>();
		for(int k = 0; k < keys.size(); k++)
		{
			IVertex key = keys.get(k);
			for(int j = k+1 <= keys.size() ? k+1 : 1; j < keys.size(); j++)
			{
				IVertex kKey = keys.get(j);
				if(!key.GetLabel().equals(kKey.GetLabel()))
				{
					// compare their attributes
					for(int i = 0; i < key.GetAttributes().size(); i++)
					{
						HashMap<String, String> keyAttr = key.GetAttributes().get(i);
						if(answerProp.size() > i && kKey.GetAttributes().size() > i)
						{
							HashMap<String, String> ansAttr = answerProp.get(i);
							HashMap<String, String> kKeyAttr = kKey.GetAttributes().get(i);
							HashMap<String, String> diff = Diff(keyAttr, kKeyAttr, ansAttr);
							if(!diff.isEmpty())
							{
								differencesInKeys.add(diff);
								if(diffInKeys.containsKey(key.GetLabel()))
								{
									diffInKeys.put(key.GetLabel(), diffInKeys.get(key.GetLabel())+1);
								}
								else
								{
									diffInKeys.put(key.GetLabel(), 1);
								}
								if(diffInKeys.containsValue(kKey.GetLabel()))
								{
									diffInKeys.put(kKey.GetLabel(), diffInKeys.get(kKey.GetLabel()+1));
								}
								else
								{
									diffInKeys.put(kKey.GetLabel(), 1);
								}
								System.out.println(key.GetLabel()+" is different from "+kKey.GetLabel()+" with diff "+diff);
							}							
						}
					}
				}
			}
		}
		System.out.println(diffInKeys);
		rightProperties = keys.get(0).GetAttributes();
		if(differencesInKeys.isEmpty())
		{
			//rightProperties = keys.get(0).GetAttributes();
			System.out.println("Right properties are "+rightProperties);
		}
		else
		{
			rightProperties = FixWrongProperties(diffInKeys, differencesInKeys, rightProperties, keys);
			System.out.println("fix these "+differencesInKeys);
		}
		return rightProperties;
	}
	
	private ArrayList<HashMap<String, String>> FixWrongProperties(HashMap<String, Integer> diffInKeys, HashSet<HashMap<String, String>> differencesInKeys,
			ArrayList<HashMap<String, String>> rightProperties, ArrayList<IVertex> keys)
	{
		ArrayList<HashMap<String, String>> rightPropertiesCopy = new ArrayList<>();
		String keyToTakeFrom = GetHighestInKey(diffInKeys);
		if(keyToTakeFrom != null)
		{
			for(IVertex k : keys)
			{
				if(k.GetLabel().equals(keyToTakeFrom))
				{
					rightPropertiesCopy = FixWrongProperties(k.GetAttributes(), differencesInKeys, rightProperties);
				}
			}
		}
		else // figure this out
		{
			for(HashMap<String, String> diffs : differencesInKeys)
			{
				for(String s : diffs.keySet())
				{
					rightPropertiesCopy = FixWrongProperties(s, keys, rightProperties);
				}
			}
		}
		
		return rightPropertiesCopy;
	}
	
	private ArrayList<HashMap<String, String>> FixWrongProperties(String fixCase, ArrayList<IVertex> keys, ArrayList<HashMap<String, String>> rightProperties)
	{
		ArrayList<HashMap<String, String>> rightPropertiesCopy = new ArrayList<>();
		String fix = "";
		switch(fixCase)
		{
		case "angle":
			fix = String.valueOf(FixAngle(keys));
			rightPropertiesCopy =  InsertInRightProperties(rightProperties, "angle", fix);
			break;
		case "shape":
			
			break;
			default:
				break;
		}
		return rightPropertiesCopy;
	}
	
	private String FixCommon(ArrayList<IVertex> keys)
	{
		
	}
	
	private Integer FixAngle(ArrayList<IVertex> keys)
	{
		int aAngle = 0;
		int bAngle = 0;
		int cAngle = 0;
		
		for(IVertex k : keys)
		{
			String value = GetValue("angle", k.GetAttributes());
			if(value != null)
			{
				if(k.GetLabel().equals("A"))
				{
					aAngle = Integer.parseInt(value);
				}
				else if(k.GetLabel().equals("B"))
				{
					bAngle = Integer.parseInt(value);
				}
				else
				{
					cAngle = Integer.parseInt(value);
				}
			}
			
		}
		// we assume there is more similarities between a and b, c and ?
		int diff = 0;
		if(aAngle > bAngle)
		{
			diff = aAngle - bAngle;
		}
		else if(bAngle > aAngle)
		{
			diff = bAngle - aAngle;
		}
		
		int ansProp = cAngle - diff;
		if(ansProp >= 0) return ansProp;
		System.out.println(aAngle +" "+bAngle+" "+cAngle);
		return null;		
	}
	
	private String GetValue(String val, ArrayList<HashMap<String, String>> keyList)
	{
		for(HashMap<String, String> key : keyList)
		{
			for(String s : key.keySet())
			{
				if(s.equals(val))
				{
					return key.get(s);
				}
			}
		}
		return null;
	}
	
	private ArrayList<HashMap<String, String>> FixWrongProperties(ArrayList<HashMap<String, String>> keyAttr, HashSet<HashMap<String, String>> differencesInKeys,
			ArrayList<HashMap<String, String>> rightProperties)
	{
		ArrayList<HashMap<String, String>> rightPropertiesCopy = new ArrayList<>();
		for(HashMap<String, String> kAttr : keyAttr)
		{
			for(HashMap<String, String> dInKey : differencesInKeys)
			{
				for(String dKey : dInKey.keySet())
				{
					if(kAttr.containsKey(dKey))
					{
						// get it's value
						String valueNeeded = kAttr.get(dKey);
						rightPropertiesCopy = InsertInRightProperties(rightProperties, dKey, valueNeeded);
						System.out.println(rightPropertiesCopy);
					}
				}
			}
		}
		return rightPropertiesCopy;
	}
	
	private ArrayList<HashMap<String, String>> InsertInRightProperties(ArrayList<HashMap<String, String>> rightProperties, String key, String valueNeeded)
	{
		//System.out.println("bruh right "+rightProperties);
		ArrayList<HashMap<String, String>> inserted = new ArrayList<>();
		for(HashMap<String , String> prop : rightProperties)
		{
			for(String k : prop.keySet())
			{
				if(k.equals(key))
				{
					System.out.println("this is key "+k+" value "+valueNeeded);
					prop.put(key, valueNeeded);
				}
			}
		}
		inserted = rightProperties; 
		return inserted;
	}
	
	private String GetHighestInKey(HashMap<String, Integer> diffInKeys)
	{
		// two values same ? return odd person if they are max
		
		int max = 0;
		// bad way to do things here.. but quick hack!!
		int aValue = 0;
		int bValue = 0;
		int cValue = 0;
		for(String k : diffInKeys.keySet())
		{
			if(k.equals("A")) aValue = diffInKeys.get(k);
			if(k.equals("B")) bValue = diffInKeys.get(k);
			if(k.equals("C")) cValue = diffInKeys.get(k);
			if(diffInKeys.get(k) > max)
			{
				max = diffInKeys.get(k);
			}
			
			
		}
		
		if(aValue == bValue && cValue == max)
		{
			return "C";
		}else if(bValue == cValue && aValue == max)
		{
			return "A";
		}else if(aValue == cValue && bValue == max)
		{
			return "B";
		}
		
		return null;
	}
	
	// return all properties that are different
	private HashMap<String, String> Diff(HashMap<String, String> keyAttr, HashMap<String, String> kKeyAttr, HashMap<String, String> ansAttr)
	{
		HashMap<String, String> diff = new HashMap<>();
		for(String aK : keyAttr.keySet())
		{
			if(ansAttr.containsKey(aK))
			{
				for(String bK : kKeyAttr.keySet())
				{
					if(aK.equals(bK))
					{
						// check across all keys and see if there is any difference					
						if(!keyAttr.get(aK).equals(kKeyAttr.get(bK)))
						{
							diff.put(bK, "diff");
						}
					}
				}
			}
		}
		return diff;
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
