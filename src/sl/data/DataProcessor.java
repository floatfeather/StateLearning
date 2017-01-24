package sl.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sl.Constant;
import sl.data.utils.IOUtils;
import sl.data.utils.StringUtils;

public class DataProcessor {
	private ArrayList<ArrayList<Descriptor>> readFiles(String filename) {
		File f = new File(filename);
		ArrayList<ArrayList<Descriptor>> results = new ArrayList<ArrayList<Descriptor>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			while((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				String words[] = line.split("\t");
				ArrayList<Descriptor> result = new ArrayList<Descriptor>();
				for(String word: words) {
					String parts[] = word.split(":");
					if (parts.length == 1) {
						result.add(new Descriptor(parts[0], ""));
					} else if (parts.length == 2) {
						result.add(new Descriptor(parts[0], parts[1]));
					} else {
						int pos = word.indexOf(":");
						String res = word.substring(pos + 1);
						result.add(new Descriptor(parts[0], res));
					}
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
		File f = new File(project.getProjectPath() + Constant.TRIVAL_PATH + Constant.FAILE_DIR);
		Map<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();
		File testDirs[] = f.listFiles();
		for(File testDir : testDirs) {
			if (testDir.getName().equals(".DS_Store")) continue;
			ArrayList<String> inner = new ArrayList<String>();
			File testFiles[] = testDir.listFiles();
			for(File testFile : testFiles) {
				inner.add(testFile.getName());
				File nf = new File(project.getProjectPath() + "/" + testDir.getName() + "/" + testFile.getName());
				nf.mkdirs();
			}
			results.put(testDir.getName(), inner);
		}
		return results;
	}
	
	private void fillTypeInfo(ArrayList<ArrayList<Descriptor>> contents, List<Boolean> typeInfo) {
		for(ArrayList<Descriptor> content : contents) {
			for(int i = 0; i < content.size(); i++) {
				if (StringUtils.isQuantative(content.get(i).getValue())) {
					typeInfo.set(i, true);
				}
			}
		}
	}
	
	private void fillQuantativeData(ArrayList<ArrayList<Descriptor>> contents, List<Boolean> typeInfo,
			HashMap<String, HashMap<String, Integer>> quantativeData) {
		for(ArrayList<Descriptor> content : contents) {
			for(int i = 0; i < content.size(); i++) {
				if (typeInfo.get(i)) {
					HashMap<String, Integer> q = quantativeData.get(content.get(i).getKey());
					if (q == null) {
						q = new HashMap<String, Integer>();
						quantativeData.put(content.get(i).getKey(), q);
					}
					if (!q.containsKey(content.get(i).getValue())) {
						q.put(content.get(i).getValue(), q.size());
					}
				}
			}
		}
	}
	
	private void fillQuantativeInfo(ArrayList<ArrayList<Descriptor>> desc1, ArrayList<ArrayList<Descriptor>> desc2,
			ArrayList<ArrayList<Descriptor>> desc3, HashMap<String, HashMap<String, Integer>> quantativeInfo) {
		List<Boolean> typeInfo = new ArrayList<Boolean>();
		for(int i = 0; i < desc1.get(0).size(); i++) {
			typeInfo.add(false);
		}
		fillTypeInfo(desc1, typeInfo);
		fillTypeInfo(desc2, typeInfo);
		fillTypeInfo(desc3, typeInfo);
		fillQuantativeData(desc1, typeInfo, quantativeInfo);
		fillQuantativeData(desc2, typeInfo, quantativeInfo);
		fillQuantativeData(desc3, typeInfo, quantativeInfo);
	}
	
	private List<String> getHeader(ArrayList<ArrayList<Descriptor>> contents, HashMap<String, HashMap<String, Integer>> quantativeData) {
		List<String> header = new ArrayList<String>();
		for(Descriptor content : contents.get(0)) {
			HashMap<String, Integer> q = quantativeData.get(content.getKey());
			if (q == null) {
				header.add(content.getKey());
			} else {
				String temp[] = new String[q.size()];
				for(Entry<String, Integer> item : q.entrySet()) {
					temp[item.getValue()] = item.getKey();
				}
				for(int i = 0; i < temp.length - 1; i++) {
					header.add(temp[i]);
				}
			}
		}
		return header;
	}
	
	private String descToString(ArrayList<Descriptor> descriptors, HashMap<String, HashMap<String, Integer>> quantativeInfo) {
		String result = "";
		for(Descriptor desc : descriptors) {
			if (!result.isEmpty()) result += ",";
			HashMap<String, Integer> q = quantativeInfo.get(desc.getKey());
			if (q == null) {
				if (StringUtils.isBoolean(desc.getValue())) {
					result += desc.getValue().equals("true") ? 1 : 0;
				} else {
					result += desc.getValue();
				}
			} else {
				if (q.size() == 1) {
					if (!result.isEmpty()) result = result.substring(0, result.length() - 1);
					continue;
				}
				int pos = q.get(desc.getValue());
				for(int i = 0; i < pos; i++) {
					result += "0,";
				}
				if (pos < q.size() - 1) result += "1";
				else result = result.substring(0, result.length() - 1);
				for(int i = 0; i < q.size() - pos - 2; i++) {
					result += ",0";
				}
			}
		}
		return result;
	}
	
	private void generateTestSet(ArrayList<ArrayList<Descriptor>> contents, List<String> header,
			HashMap<String, HashMap<String, Integer>> quantativeInfo, String path) {
		List<String> results = new ArrayList<String>();
		results.add(StringUtils.toString(header, ","));
		for(ArrayList<Descriptor> content : contents) {
			results.add(descToString(content, quantativeInfo));
		}
		IOUtils.writeToFile(results, path);
	}
	
	private void generateTrainingSet(ArrayList<ArrayList<Descriptor>> negContents, ArrayList<ArrayList<Descriptor>> posContents,
			List<String> header, HashMap<String, HashMap<String, Integer>> quantativeInfo, String path) {
		List<String> results = new ArrayList<String>();
		results.add(StringUtils.toString(header, ",") + ",label");
		for(ArrayList<Descriptor> content : negContents) {
			String res = descToString(content, quantativeInfo);
			res += ",1";
			results.add(res);
		}
		for(ArrayList<Descriptor> content : posContents) {
			String res = descToString(content, quantativeInfo);
			res += ",0";
			results.add(res);
		}
		IOUtils.writeToFile(results, path);
	}
	
	private void deleteFiles(String path) {
		File f = new File(path);
		if (f.isDirectory()) {
			File children[] = f.listFiles();
			for(File child : children) {
				deleteFiles(child.getAbsolutePath());
			}
		}
		if (!f.delete()) {
			System.out.println("Fail to delete file " + path);
		}
	}
	
	private boolean checkFormat(ArrayList<ArrayList<Descriptor>> contents) {
		if (contents.size() < 2) return true;
		ArrayList<Descriptor> desc1 = contents.get(0);
		ArrayList<Descriptor> desc2 = contents.get(1);
		for(int i = 0; i < desc1.size(); i++) {
			if (!desc1.get(i).getKey().equals(desc2.get(i).getKey())) return false;
		}
		return true;
	}
	
	public void run(Project project) {
		Map<String, ArrayList<String>> failedTestInfo = getFailedTestInfo(project);
		String prefix = project.getProjectPath() + "/" + Constant.TRIVAL_PATH;
		String failedPrefix = prefix + Constant.FAILE_DIR;
		String negPrefix = prefix + Constant.NEGATIVE_DIR;
		String posPrefix = prefix + Constant.POSITIVE_DIR;
		for(Entry<String, ArrayList<String>> failedTest : failedTestInfo.entrySet()) {
			String outputPrefix = project.getProjectPath() + "/" + failedTest.getKey();
			for(String test : failedTest.getValue()) {
				System.out.println(failedTest.getKey() + "/" + test);
//				if (!test.equals("org.jfree.chart.plot.Marker#?#Marker#?,Paint,Stroke,Paint,Stroke,float")) continue;
				String failedPath = failedPrefix + "/" + failedTest.getKey() + "/" + test;
				String negPath = negPrefix + "/" + test;
				String posPath = posPrefix + "/" + test;
				ArrayList<ArrayList<Descriptor>> failedContents = readFiles(failedPath);
				ArrayList<ArrayList<Descriptor>> negContents = readFiles(negPath);
				ArrayList<ArrayList<Descriptor>> posContents = readFiles(posPath);
				if (negContents.isEmpty() || posContents.isEmpty()) {
					deleteFiles(outputPrefix + "/" + test);
					continue;
				}
				if ((!checkFormat(negContents)) || (!checkFormat(posContents)) || (!checkFormat(failedContents))) {
					deleteFiles(outputPrefix);
					break;
				}
				HashMap<String, HashMap<String, Integer>> quantativeInfo = new HashMap<String, HashMap<String, Integer>>();
				fillQuantativeInfo(failedContents, negContents, posContents, quantativeInfo);
				List<String> header = getHeader(failedContents, quantativeInfo);
				String testPath = outputPrefix + "/" + test + "/test.csv";
				String trainPath = outputPrefix + "/" + test + "/train.csv";
				generateTestSet(failedContents, header, quantativeInfo, testPath);
				generateTrainingSet(negContents, posContents, header, quantativeInfo, trainPath);
			}
		}
	}
	
	public static void main(String args[]) {
		Project project = new Project(ProjectInfo.getProjectID("chart"), 20);
		DataProcessor processor = new DataProcessor();
		processor.run(project);
	}
}
