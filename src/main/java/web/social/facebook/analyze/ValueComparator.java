package web.social.facebook.analyze;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator<T extends Number> implements Comparator<String> {
	Map<String, T> base;

	public ValueComparator(Map<String, T> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(String a, String b) {
		if (base.get(a).doubleValue() > base.get(b).doubleValue()) {
			return -1;
		} else {
			return 1;
		}
	}
}
