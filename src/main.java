import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;

public class main {

	public static final int MAX_CLUSTERS = 2;
	public static final int CATEGORICAL_DISTANCE_THRESHHOLD = 2;

	public static ArrayList<Node> nodes = new ArrayList<>();

	public static double[] T = new double[MAX_CLUSTERS];

	public static ArrayList<Cluster> clusters;

	public static void init() {
		nodes.add(new Node(0, "Gray", new Value(true).setValue(1.0), new Value(false).setValue("red")));
		nodes.add(new Node(1, "John", new Value(true).setValue(2.0), new Value(false).setValue("blue")));
		nodes.add(new Node(2, "Tom", new Value(true).setValue(1.0), new Value(false).setValue("yellow")));
		nodes.add(new Node(3, "Jerry", new Value(true).setValue(3.0), new Value(false).setValue("blue")));
		nodes.add(new Node(4, "Hang", new Value(true).setValue(9.0), new Value(false).setValue("blue")));
		nodes.add(new Node(5, "Richard", new Value(true).setValue(4.0), new Value(false).setValue("red")));
		nodes.add(new Node(6, "Fabra", new Value(true).setValue(5.0), new Value(false).setValue("red")));

		clusters = new ArrayList<>();
		for (int i = 0; i < MAX_CLUSTERS; i++) {
			clusters.add(new Cluster(i, nodes.get(i)));
			T[i] = Integer.MAX_VALUE;
		}
	}

	public static void main(String[] args) {
		init();
		for (Node node : nodes) {

			// Step 3
			Cluster C_w_1 = min_dist_Mix(1, node);
			Cluster C_w_2 = min_dist_Mix(2, node);

			// Step 4
			T[C_w_1.index] = max_dist_Mix_from_centeroid(C_w_1);
			T[C_w_2.index] = max_dist_Mix_from_centeroid(C_w_2);

			// Step 5
			if (dist_Mix(node, C_w_1) > T[C_w_1.index]) {
				clusters.add(new Cluster(clusters.size() + 1, node));
				T[C_w_1.index] = Double.MAX_VALUE;
				continue;
			}
			if (dist_Mix(node, C_w_2) > T[C_w_2.index]) {
				clusters.add(new Cluster(clusters.size() + 1, node));
				T[C_w_2.index] = Double.MAX_VALUE;
				continue;
			}

		}
		for (Cluster cluster : clusters) {
			System.out.println("Cluster #"+cluster.index);
			for (Node d : cluster.nodes) {
				System.out.println("\t"+d.toString());
			}
		}
	}

	public static Cluster min_dist_Mix(int winnerIndex, Node instance) {
		int minDistClusterIndex = -1;
		double minDistValue = Double.MAX_VALUE;

		double distances[] = new double[clusters.size()];

		for (int i = 0; i < clusters.size(); i++) {
			distances[i] = dist_Mix(instance, clusters.get(i));
		}

		for (int i = 0; i < winnerIndex; i++) {
			for (int j = 0; j < distances.length; j++) {
				if (minDistValue > distances[j]) {
					minDistValue = distances[j];
					minDistClusterIndex = j;
				}
			}
			distances[minDistClusterIndex] = Double.MAX_VALUE;
		}

		if (minDistClusterIndex >= 0)
			return clusters.get(minDistClusterIndex);
		else
			return null;
	}

	public static double max_dist_Mix_from_centeroid(Cluster cluster) {
		double maxDistValue = Double.MIN_VALUE;

		for (Node node : cluster.nodes) {
			double distance = dist_Mix(node, cluster);
			maxDistValue = maxDistValue < distance ? distance : maxDistValue;
		}
		return maxDistValue;
	}

	public static double dist_Mix(Node instance, Cluster cluster) {
		return Math.sqrt(dist_Num(instance, cluster.center) + dist_Cat(instance, cluster.center));
	}

	private static double dist_Cat(Node instance, Node cluster) {
		double distance = 0;
		for (int i = 0; i < instance.getValues().length; i++) {
			if (!instance.getValue(i).isNumeric()) {
				if (!((String) instance.getValue(i).getValue()).equals((String) cluster.getValue(i).getValue())) {
					distance += w(i, instance, cluster);
				}
			}
		}
		return distance;
	}

	private static double w(int attributeIndex, Node instance_i, Node instance_j) {//considering unsupervised
		double _f=f(instance_i.getValues().length);
		return Math.min(Math.min(_f, CATEGORICAL_DISTANCE_THRESHHOLD),d_fr(attributeIndex, instance_i, instance_j));
	}

	private static double d_fr(int attributeIndex, Node instance_i, Node instance_j){
		return (Math.abs(fr(attributeIndex,instance_i)-fr(attributeIndex,instance_j))+mf(attributeIndex)) / Math.max(fr(attributeIndex,instance_i), fr(attributeIndex,instance_j));
	}
	
	private static int mf(int attributeIndex) {
		HashMap<String, Integer> occurness = new HashMap<>();
		
		for (Node node : nodes) {
			if(occurness.containsKey((String)node.getValue(attributeIndex).getValue())){
				int value =occurness.get((String)node.getValue(attributeIndex).getValue())+1;
				occurness.remove((String)node.getValue(attributeIndex).getValue());
				occurness.put((String)node.getValue(attributeIndex).getValue(), value);
			}
			else{
				occurness.put((String)node.getValue(attributeIndex).getValue(), 1);
			}
		}
		
		int min=Integer.MAX_VALUE;
		for (String d : occurness.keySet()) {
			if(min>occurness.get(d))
				min=occurness.get(d);
		}
		return min;
	}

	private static int fr(int attributeIndex, Node instance_i){
		String value = (String)instance_i.getValue(attributeIndex).getValue();
		int count=0;
		for (Node node : nodes) {
			if(value.equals((String)node.getValue(attributeIndex).getValue()))
				count++;
		}
		return count;
	}
	
	private static double f(double z){
		if(z<=3)
			return 1;
		else if(3<z && z<=10)
			return 1-0.05*(z-3);
		else
			return 0.65-0.01*(z-10);
	}
	
	private static double dist_Num(Node instance, Node cluster) {
		double distance = 0;
		for (int i = 0; i < instance.getValues().length; i++) {
			if (instance.getValue(i).isNumeric())
				distance += Math.pow((double) instance.getValue(i).getValue() - (double) cluster.getValue(i).getValue(),
						2);
		}
		return distance;
	}
}
