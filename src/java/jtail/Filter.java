package jtail;

import java.util.regex.Pattern;

/**
 * Filter
 * @author kazuhiko arase
 */
public class Filter {
	private final Pattern pattern;
	private final boolean exclude;
	public Filter(String regex) {
		if (regex.startsWith("!") ) {
			pattern = Pattern.compile(regex.substring(1) );
			exclude = true;
		} else {
			pattern = Pattern.compile(regex);
			exclude = false;
		}
	}
	public boolean accept(final String line) {
		return pattern.matcher(line).find() ^ exclude;
	}
}