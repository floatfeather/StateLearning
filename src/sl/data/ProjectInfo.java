package sl.data;

import java.util.HashMap;
import java.util.Map;

public class ProjectInfo {
	private static Map<Integer, String> idToName = new HashMap<Integer, String>();
	private static Map<String, Integer> nameToID = new HashMap<String, Integer>();
	
	static {
		idToName.put(0, "lang");
		idToName.put(1, "chart");
		nameToID.put("lang", 0);
		nameToID.put("chart", 1);
	};
	
	public static int getProjectID(String projectName) {
		return nameToID.get(projectName);
	}
	
	public static String getProjectName(int id) {
		return idToName.get(id);
	}
}
