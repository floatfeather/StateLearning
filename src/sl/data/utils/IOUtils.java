package sl.data.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class IOUtils {
	public static void writeToFile(List<String> contents, String path) {
		File f = new File(path);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			for(String content : contents) {
				writer.write(content + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
