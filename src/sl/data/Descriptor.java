package sl.data;

public class Descriptor {
	private String key;
	private String value;
	public Descriptor(String k, String v) {
		key = k;
		value = v;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
