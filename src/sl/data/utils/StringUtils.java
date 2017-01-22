package sl.data.utils;

import java.util.List;
import java.util.regex.Pattern;

public class StringUtils {
	private static final Pattern integerPattern = Pattern.compile("^[-\\+]?[\\d]+$");
	
	private static final Pattern floatPattern = Pattern.compile("^[-\\+]?[\\d]+\\.[\\d]+$");
	
	public static boolean isInteger(String input) {
		return integerPattern.matcher(input).matches();
	}
	
	public static boolean isScientific(String input) {
		String linput = input.toLowerCase();
		if (linput.equals("nan")) return true;
		int pos = linput.indexOf("e");
		if (pos == -1) return false;
		String parts[] = linput.split("e");
		if (parts.length != 2) return false;
		return isFloat(parts[0]) && isInteger(parts[1]);
	}
	
	public static boolean isFloat(String input) {
		return floatPattern.matcher(input).matches();
	}
	
	public static boolean isNumber(String input) {
		return isInteger(input) || isFloat(input) || isScientific(input);
	}
	
	public static boolean isBoolean(String input) {
		String linput = input.toLowerCase();
		return linput.equals("true") || linput.equals("false");
	}
	
	public static boolean isQuantative(String input) {
		return isNumber(input) || isBoolean(input);
	}
	
	public static String toString(List<String> input, String sep) {
		if (input.isEmpty()) return "";
		String result = input.get(0);
		for(int i = 1; i < input.size(); i++) {
			result += sep + input.get(i);
		}
		return result;
	}
}
