package com.cv.sparkathon.utils;

import com.google.common.base.Joiner;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PatternReplacer implements Iterator<String> {
	private final String src;
	private final Matcher matcher;
	private final Function<String,String> replacer;
	private int pos = 0;


	public PatternReplacer(Pattern pattern, String src, Function<String, String> replacer) {
		this.src = src;
		this.matcher = pattern.matcher(src);
		this.replacer = replacer;
	}

	@Override
	public boolean hasNext() {
		return pos < src.length();
	}

	@Override
	public String next() {
		if (hasNext()) {
			String ret;
			if (matcher.find(pos)) {
				ret = src.substring(pos, matcher.start()) + replacer.apply(matcher.group(1));
				pos = matcher.end();
			} else {
				ret = src.substring(pos);
				pos = src.length();
			}
			return ret;
		} else {
			throw new NoSuchElementException();
		}
	}

	public static String replaceAll(Pattern pattern, String source, Function<String, String> replacer) {
		return Joiner.on("").join(
				StreamSupport.stream(
					Spliterators.spliteratorUnknownSize(
							new PatternReplacer(pattern, source, replacer),
							Spliterator.ORDERED
					),
					false).collect(Collectors.toList()));
	}
}
