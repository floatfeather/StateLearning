package sl.data;

import java.util.ArrayList;
import java.util.List;

public class Prediction {
	private List<Double> prediction = new ArrayList<Double>();
	private List<String> text = new ArrayList<String>();
	private int iter;
	public Prediction() {
		iter = 0;
	}
	public void addPrediction(double d) {
		prediction.add(d);
	}
	
	public void addText(String t) {
		text.add(t);
	}
	
	public double next() {
		return prediction.get(iter++);
	}
	
	public boolean hasNext() {
		return iter < prediction.size();
	}
}
