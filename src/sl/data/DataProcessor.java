package sl.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import sl.Constant;

public class DataProcessor {
	private ArrayList<ArrayList<Descriptor>> readFiles(String filename) {
		File f = new File(filename);
		ArrayList<ArrayList<Descriptor>> results = new ArrayList<ArrayList<Descriptor>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			while((line = reader.readLine()) != null) {
				String words[] = line.split(" ");
				ArrayList<Descriptor> result = new ArrayList<Descriptor>();
				for(String word: words) {
					String parts[] = word.split(":");
					if (parts.length != 2) {
						System.err.println("Wrong format " + word);
						reader.close();
						return null;
					}
					result.add(new Descriptor(parts[0], parts[1]));
				}
				results.add(result);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	private Map<String, ArrayList<String>> getFailedTestInfo(Project project) {
		File f = new File(project.getProjectPath() + Constant.TRIVAL_PATH + Constant.FAILED_TEST);
		Map<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			ArrayList<String> tests = null;
			while((line = reader.readLine()) != null) {
				if (line.charAt(0) == '@') {
					tests = new ArrayList<String>();
					results.put(line.substring(1), tests);
				} else {
					tests.add(line);
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	public static void main(String args[]) {
	}
}
