package sl.data;

import java.util.ArrayList;
import java.util.List;

public class Prediction {
	private List<String> prediction = new ArrayList<String>();
	private List<String> text = new ArrayList<String>();
	private int iter;
	public Prediction() {
		iter = 0;
	}
	public void addPrediction(String d) {
		prediction.add(d);
	}
	
	public void addText(String t) {
		text.add(t);
	}
	
	public String next() {
		return prediction.get(iter++);
	}
	
	public boolean hasNext() {
		return iter < prediction.size();
	}
}
