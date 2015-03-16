package com.whty.util.xml.parsing;

import java.util.Properties;

/**
 * 属性解析类
 * @since 0.0.1
 */
public class PropertyParser {

	public static String parse(String string, Properties variables) {
		VariableTokenHandler handler = new VariableTokenHandler(variables);
		GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
		return parser.parse(string);
	}

	/*
	 * 变量Token解析
	 */
	private static class VariableTokenHandler implements TokenHandler {
		private Properties variables;

		public VariableTokenHandler(Properties variables) {
			this.variables = variables;
		}

		public String handleToken(String content) {
			if (variables != null && variables.containsKey(content)) {
				return variables == null ? content : variables
						.getProperty(content);
			} else {
				return "${" + content + "}";
			}
		}
	}
}
