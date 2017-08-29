package graph;

public class Edge {
	private String relationship;
	private final Vertex source;
	private final Vertex destination;
	private double weight;
	
	public Edge(Vertex source, Vertex destination, String relationship) {
		this.source = source;
		this.destination = destination;
		this.relationship = relationship;
	}

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
