package sl.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import sl.Constant;
import sl.data.utils.IOUtils;

public class ResultProcessor {
	
	private HashMap<String, ArrayList<String>> getTestInfo() {
		File rootf = new File(Constant.RESULT_PATH);
		File projectf[] = rootf.listFiles();
		HashMap<String, ArrayList<String>> info = new HashMap<String, ArrayList<String>>();
		for(File pf : projectf) {
			File testf[] = pf.listFiles();
			if (testf == null) continue;
			for(File tf : testf) {
				ArrayList<String> tinfo = new ArrayList<String>();
				File pointf[] = tf.listFiles();
				if (pointf == null) continue;
				for(File ptf : pointf) {
					tinfo.add(ptf.getAbsolutePath() + Constant.RESULT_CSV);
				}
				info.put(pf.getName() + "/" + tf.getName(), tinfo);
			}
		}
		return info;
	}
	
	private ArrayList<String> buildExecutionPath(String fpath) {
		String parts[] = fpath.split("/");
		String faildTestPath = Constant.DATA_PATH + "/" + parts[0] + Constant.FAILED_TEST;
		String testname = parts[1];
		File f = new File(faildTestPath);
		ArrayList<String> result = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			String target = "@" + testname;
			boolean start = false;
			while((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				if (!start) {
					String temp[] = line.split("#");
					line = temp[0] + "_" + temp[2];
				} else {
					int pos = line.lastIndexOf("#");
					line = line.substring(0, pos);
					pos = line.lastIndexOf("#");
					line = line.substring(0, pos);
				}
				if (line.equals(target)) {
					start = true;
				} else if (start) {
					if (line.charAt(0) == '@') {
						break;
					}
					result.add(line);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private Prediction analyzeResult(String csvPath) {
		File f = new File(csvPath);
		Prediction pred = new Prediction();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			boolean isFirst = true;
			while((line = reader.readLine()) != null) {
				if (isFirst) {
					isFirst = false;
					continue;
				}
				String parts[] = line.split(",");
				pred.addPrediction(Double.valueOf(parts[parts.length - 1]));
				pred.addText(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pred;
	}
	
	private List<String> sortPredictionAndWriteOut(ArrayList<String> exePaths, HashMap<String, Prediction> results) {
		List<String> sortedResult = new ArrayList<String>();
		sortedResult.add("pointName, prediction");
		for(String exePoint : exePaths) {
			System.out.println("ep:" + exePoint);
			Prediction pred = results.get(exePoint);
			if (pred != null && pred.hasNext()) {
				sortedResult.add("\"" + exePoint + "\"," + pred.next());
			}
		}
		return sortedResult;
	}
	
	public void run() {
		HashMap<String, ArrayList<String>> iopairs = getTestInfo();
		for(Entry<String, ArrayList<String>> entry : iopairs.entrySet()) {
			System.out.println(entry.getKey());
			ArrayList<String> exePaths = buildExecutionPath(entry.getKey());
			if (exePaths.isEmpty()) continue;
			HashMap<String, Prediction> results = new HashMap<String, Prediction>();
			for(String iopair : entry.getValue()) {
				Prediction result = analyzeResult(iopair);
				String parts[] = iopair.split("/");
				results.put(parts[parts.length - 2], result);
			}
			List<String> contents = sortPredictionAndWriteOut(exePaths, results);
			System.out.println(contents.toString());
			IOUtils.writeToFile(contents, Constant.RESULT_PATH + "/" + entry.getKey() + Constant.PRED_CSV);
		}
	}
	
	public static void main(String args[]) {
		ResultProcessor processor = new ResultProcessor();
		processor.run();
	}
}
