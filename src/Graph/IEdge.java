package Graph;

public interface IEdge {

	IVertex GetSource();

	IVertex GetDestination();

	void SetWeight(int value);

	double GetWeight();

}