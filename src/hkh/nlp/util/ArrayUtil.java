package hkh.nlp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtil {

	public static String[] removeFirst(String[] strArr) {
		if (strArr.length == 0) {
			return null;
		}
		return Arrays.copyOfRange(strArr, 1, strArr.length);
	}
	
	public static String[] appendElement(String[] strArr, String str) {
		List<String> list = new ArrayList<String>();
		for (String s : strArr) {
			list.add(s);
		}
		list.add(str);
		return list.toArray(new String[0]);
	}
	
	public static void appendElement(ArrayList<String[]> ref, ArrayList<String[]> completed, String str) {
		for (int i=0; i<ref.size(); i++) {
			String[] elm = ref.get(i);
			elm = appendElement(elm, str);
			completed.add(elm);
		}
	}
}
