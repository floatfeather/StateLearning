package sl.data;

import sl.Constant;

public class Project {
	private int projectID;
	private int bugID;
	
	public Project(int pID, int bID) {
		projectID = pID;
		bugID = bID;
	}
	
	public String getProjectPath() {
		return Constant.DATA_PATH + "/" + getProjectDirName();
	}
	
	private String getProjectDirName() {
		String name = ProjectInfo.getProjectName(projectID);
		if (name == null) {
			System.err.println("Unknown project ID " + Integer.toString(projectID));
			return null;
		}
		return name + "_" + Integer.toString(bugID);
	}
	
}
