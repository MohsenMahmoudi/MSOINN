import java.util.ArrayList;

public class Cluster {
	public ArrayList<Node> nodes;
	public Node center;
	public int index;
	public Cluster(int index,Node center) {
		this.index=index;
		nodes=new ArrayList<>();
		this.center=center;
	}
}
