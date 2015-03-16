package com.whty.util.xml.parsing;

/**
 * 起止符解析器
 * @since 0.0.1
 */
public class GenericTokenParser {

	//开闭TOKEN
	private final String openToken;
	private final String closeToken;
	
	//解析Handler
	private final TokenHandler handler;

	public GenericTokenParser(String openToken, String closeToken,
			TokenHandler handler) {
		this.openToken = openToken;
		this.closeToken = closeToken;
		this.handler = handler;
	}

	public String parse(String text) {
		StringBuilder builder = new StringBuilder();
		if (text != null) {
			String after = text;
			int start = after.indexOf(openToken);
			int end = after.indexOf(closeToken);
			while (start > -1) {
				if (end > start) {
					String before = after.substring(0, start);
					String content = after.substring(
							start + openToken.length(), end);
					String substitution = handler.handleToken(content);
					builder.append(before);
					builder.append(substitution);
					after = after.substring(end + closeToken.length());
				} else if (end > -1) {
					String before = after.substring(0, end);
					builder.append(before);
					builder.append(closeToken);
					after = after.substring(end + closeToken.length());
				} else {
					break;
				}
				start = after.indexOf(openToken);
				end = after.indexOf(closeToken);
			}
			builder.append(after);
		}
		return builder.toString();
	}

}
