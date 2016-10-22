package Graph;

public class Edge implements IEdge {
	private final int _id;
	private final IVertex _source;
	private final IVertex _destination;
	private double _weight;
	
	public Edge(int id, IVertex source, IVertex destination, double weight)
	{
		_id = id;
		_source = source;
		_destination = destination;
		_weight = weight;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IEdge#GetSource()
	 */
	@Override
	public IVertex GetSource()
	{
		return _source;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IEdge#GetDestination()
	 */
	@Override
	public IVertex GetDestination()
	{
		return _destination;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IEdge#IncrementWeight(int)
	 */
	@Override
	public void SetWeight(int value)
	{
		_weight = value;
	}
	
	/* (non-Javadoc)
	 * @see Graph.IEdge#GetWeight()
	 */
	@Override
	public double GetWeight()
	{
		return _weight;
	}
}
