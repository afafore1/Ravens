package Raven;

import java.util.ArrayList;
import java.util.HashMap;

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
	private String _name;
	private HashMap<String, RavensFigure> _figures;
	private HashMap<String, RavensFigure> _answers = new HashMap<String,RavensFigure>();
	private boolean _isVerbal;
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
		_name = problem.getName();
		_figures = problem.getFigures();
		_isVerbal = problem.hasVerbal();

		if (_name.contains("Basic Problem C")) // only solving for Basic problem
												// B
		{
			GetFigures();
			if (_isVerbal) {
				Algorithm.Brain brain = new Algorithm.Brain(_figures);
				System.out.println("---------------------------\n Calling AI for "+problem.getName());
				//System.out.println("Answer is "+ai.FindAnswer()); // return 
				return brain.Think();
//				System.out.println("---------------------------\n AI call done");
//				HashMap<String, RavensObject> aObject = _ravensFigure_a.getObjects();
//				HashMap<String, RavensObject> bObject = _ravensFigure_b.getObjects();
//				HashMap<String, RavensObject> cObject = _ravensFigure_c.getObjects();
//
//				System.out.println("\n"+problem.getName());
//				return CompareObjects(aObject, bObject, cObject);
			}
		}

		return -1;
	}

	private int CompareObjects(HashMap<String, RavensObject> aObject, HashMap<String, RavensObject> bObject, HashMap<String, RavensObject> cObject)
	{
		ArrayList<HashMap<String, String>> attr_a = new ArrayList<HashMap<String,String>>();
		ArrayList<HashMap<String, String>> attr_b = new ArrayList<HashMap<String,String>>();
		ArrayList<HashMap<String, String>> attr_c = new ArrayList<HashMap<String,String>>();
		
		attr_a = SetAttr(aObject);
		attr_b = SetAttr(bObject);
		attr_c = SetAttr(cObject);
		System.out.println(attr_a);
		System.out.println(attr_b);
		System.out.println(attr_c);
		return GetExpectedResult(attr_a, attr_b, attr_c);
	}
	
	private ArrayList<HashMap<String, String>> SetAttr(HashMap<String, RavensObject> rObject)
	{
		ArrayList<HashMap<String, String>> attr = new ArrayList<HashMap<String,String>>();
		for(String s : rObject.keySet())
		{
			RavensObject srObject = rObject.get(s);
			attr.add(srObject.getAttributes());
		}
		return attr;
	}

	private int GetExpectedResult(ArrayList<HashMap<String, String>> attr_a, ArrayList<HashMap<String, String>> attr_b, ArrayList<HashMap<String, String>> attr_c)
	{
		int ans = -1;
		int bSize = attr_b.size();
		int cSize = attr_c.size();
		int b = 0;
		int c = 0;
		for(int i = 0; i < attr_a.size(); i++)
		{
			HashMap<String, String> aHash = attr_a.get(i);
			HashMap<String, String>  bHash = new HashMap<>();
			HashMap<String, String>  cHash = new HashMap<>();
			if(bSize == attr_a.size())
			{
				bHash = attr_b.get(i);
			}
			else
			{
				if(b < bSize)
				{
					bHash = attr_b.get(b);
					b++;
				}
			}
			if(cSize == attr_a.size())
			{
				cHash = attr_c.get(i);
			}else
			{
				if(c < bSize)
				{
					cHash = attr_c.get(c);
					c++;
				}
			}
			if(bSize < attr_a.size())
			{
				return Sort(aHash, bHash, cHash);
			}
			ans = Sort(aHash, bHash, cHash);
		}
		return ans;
	}
	
	private int Sort(HashMap<String, String> aHash, HashMap<String, String> bHash, HashMap<String, String> cHash)
	{
		HashMap<String, String> same = new HashMap<>();
		HashMap<String, String> diff = new HashMap<>();
		for(String akey : aHash.keySet())
		{
			if(bHash.containsKey(akey) && cHash.containsKey(akey))
			{
				if(aHash.get(akey).equals(bHash.get(akey)))
				{
					same.put(akey, cHash.get(akey));
				}
				else
				{
					String aDiff = aHash.get(akey);
					String bDiff = bHash.get(akey);
					String cDiff = cHash.get(akey);
					diff.put(akey, GetRightProp(akey, aDiff,bDiff,cDiff));
				}
			}
		}
		System.out.println("Same: "+same);
		System.out.println("Diff: "+diff);
		
		return GetResult(same, diff);
	}
	
	private String GetRightProp(String key, String a, String b, String c)
	{
		String rst = "";
		if(a.equals(c))
		{
			if(b.isEmpty()) return a;
			return b;
		}else if(a.equals(b))
		{
			return c;
		}else if(b.equals(c))
		{
			return a;
		}
		else
		{
			if(key.equals("alignment"))
			{
				String [] aArr = a.split("-");
				String [] bArr = b.split("-");
				String [] cArr = c.split("-");
				String [] dArr = new String[cArr.length];
				for(int i = 0; i < aArr.length; i++)
				{
					if(aArr[i].equals(bArr[i]))
					{
						dArr [i]=cArr[i];
					}
					else
					{
						dArr[i]=bArr[i];
					}
				}
				rst = dArr[0]+"-"+dArr[1];
			}else if(key.equals("inside") || key.equals("above"))
			{
				for(RavensFigure rf : _answers.values())
				{
					HashMap<String, RavensObject> obj = rf.getObjects();
					for(String s : obj.keySet())
					{
						RavensObject sObject = obj.get(s);
						HashMap<String, String> attr = sObject.getAttributes();
						if(attr.containsKey(key))
						{
							rst = attr.get(key);
						}
						break;
					}
				}
			}else if(key.equals("angle")) // we know angle is a num
			{
				int aAng = Integer.parseInt(a);
				int bAng = Integer.parseInt(b);
				int cAng = Integer.parseInt(c);
				rst = String.valueOf(cAng - (bAng - aAng));
			}
		}
		
		return rst;
	}
	
	private int GetResult(HashMap<String, String> same, HashMap<String, String> diff)
	{
		int ans = -1;
		ArrayList<RavensFigure> pFigures = new ArrayList<>();
		for(RavensFigure rf : _answers.values())
		{
			HashMap<String, RavensObject> object = rf.getObjects();
			for(String s : object.keySet())
			{
				RavensObject sObject = object.get(s);
				HashMap<String, String> attr = sObject.getAttributes();
				if(isContainMap(same, attr))
				{
					pFigures.add(rf);
					System.out.println("This guys is potential "+rf.getName());
					if(diff.isEmpty())
					{
						ans = Integer.parseInt(rf.getName());
					}
				}
			}
		}
		
		if(ans == -1)
		{
			ans = EvaluatePotential(pFigures, same, diff);
		}
		System.out.println("MyAnswer "+ans);
		return ans;
	}
	
	private int EvaluatePotential(ArrayList<RavensFigure> potentials, HashMap<String, String> same, HashMap<String, String> diff)
	{		
		same.putAll(diff);
		System.out.println("new same "+same);
		diff = new HashMap<>();
		for(RavensFigure rf : potentials)
		{
			HashMap<String, RavensObject> object = rf.getObjects();
			for(String s : object.keySet())
			{
				RavensObject sObject = object.get(s);
				HashMap<String, String> attr = sObject.getAttributes();
				if(isContainMap(same, attr))
				{
					return Integer.parseInt(rf.getName());
				}
			}
		}
		return -1;
	}
	
	private boolean isContainMap(HashMap<String, String> same, HashMap<String, String> attr)
	{
		boolean containsAll = false;
		for(String key : same.keySet())
		{
			if(attr.containsKey(key))
			{
				if(same.get(key).equals(attr.get(key)))
				{
					containsAll = true;
				}
				else
				{
					return false;
				}
			}
		}
		return containsAll;
	}
	
	private void GetFigures() {
		// get first two and compare attributes .. third one we evaluate for..
		for (String name : _figures.keySet()) {
			RavensFigure rf = _figures.get(name);
			if (rf.getName().equals("A")) {
				_ravensFigure_a = rf;
			} else if (rf.getName().equals("B")) {
				_ravensFigure_b = rf;
			} else if (rf.getName().equals("C")) {
				_ravensFigure_c = rf;
			}
			else
			{
				_answers.put(name, rf);
			}
		}
	}
}