package com.devteam.util;

import java.io.PrintWriter;
import java.io.StringWriter;


public class StringUtils {

	public static boolean isEmpty(String... str) {
		for (String s : str) {
			if (s == null || "".equals(s.trim())) {
				return true;
			}
		}
		return false;
	}


	public static boolean hasSpecialChar(String... str) {
		for (String s : str) {
			if (s.contains("%") || s.contains("_") || s.contains("[") || s.contains("#") || s.contains("*")) {
				return true;
			}
		}
		return false;
	}

	public static String substring(String str, int start, int end) {
		if (str == null || "".equals(str)) {
			return "";
		}
		if (start < 0 || end < 0) {
			return str;
		}
		if (end > str.length()) {
			end = str.length();
		}
		if (start >= end) {
			return "";
		}
		return str.substring(start, end);
	}

	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		try (PrintWriter pw = new PrintWriter(sw)) {
			throwable.printStackTrace(pw);
			return sw.toString();
		}
	}
}
