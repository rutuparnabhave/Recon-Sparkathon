package com.cv.sparkathon.utils;

import java.util.function.Function;
import java.util.regex.Pattern;

public class IDPatternReplacer {
	public static final Pattern ID_PATTERN = Pattern.compile("\\$\\{([\\w\\.\\$]+)\\}", Pattern.MULTILINE);

	public static String replaceIDs(String source, Function<String, String> replacer) {
		return replaceAll(ID_PATTERN, source, replacer);
	}

	public static String replaceAll(Pattern pattern, String source, Function<String, String> replacer) {
		return PatternReplacer.replaceAll(pattern, source, replacer);
	}
}
