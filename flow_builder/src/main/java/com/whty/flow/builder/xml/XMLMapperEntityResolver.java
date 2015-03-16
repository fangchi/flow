package com.whty.flow.builder.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.whty.util.classname.Resources;

/**
 * XML DTD定义解析规则类
 * @since 0.0.1
 */
public class XMLMapperEntityResolver implements EntityResolver {

	private static final Map<String, String> doctypeMap = new HashMap<String, String>();

	private static final String FLOWS_CONFIG_DOCTYPE = "-//flows.whty.flow.com.cn//DTD Config 1.0//EN"
			.toUpperCase(Locale.ENGLISH);
	private static final String FLOWS_CONFIG_URL = "http://whty.flow.com.cn/dtd/flows.dtd"
			.toUpperCase(Locale.ENGLISH);
	private static final String FLOWS_CONFIG_DTD = "com/whty/flow/builder/xml/flows.dtd";
	
	private static final String FLOWTEMPLATES_CONFIG_DOCTYPE = "-//flowtemps.whty.flow.com.cn//DTD Config 1.0//EN"
			.toUpperCase(Locale.ENGLISH);
	private static final String FLOWTEMPLATES_CONFIG_URL = "http://whty.flow.com.cn/dtd/flowtemps.dtd"
			.toUpperCase(Locale.ENGLISH);
	private static final String FLOWTEMPLATES_CONFIG_DTD = "com/whty/flow/builder/xml/flowtemps.dtd";


	static {
		doctypeMap.put(FLOWS_CONFIG_URL, FLOWS_CONFIG_DTD);
		doctypeMap.put(FLOWS_CONFIG_DOCTYPE, FLOWS_CONFIG_DTD);
		
		doctypeMap.put(FLOWTEMPLATES_CONFIG_URL, FLOWTEMPLATES_CONFIG_DTD);
		doctypeMap.put(FLOWTEMPLATES_CONFIG_DOCTYPE, FLOWTEMPLATES_CONFIG_DTD);
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		if (publicId != null)
			publicId = publicId.toUpperCase(Locale.ENGLISH);
		if (systemId != null)
			systemId = systemId.toUpperCase(Locale.ENGLISH);

		InputSource source = null;
		try {
			String path = doctypeMap.get(publicId);
			source = getInputSource(path, source);
			if (source == null) {
				path = doctypeMap.get(systemId);
				source = getInputSource(path, source);
			}
		} catch (Exception e) {
			throw new SAXException(e.toString());
		}
		return source;
	}

	private InputSource getInputSource(String path, InputSource source) {
		if (path != null) {
			InputStream in;
			try {
				in = Resources.getResourceAsStream(path);
				source = new InputSource(in);
			} catch (IOException e) {
			}
		}
		return source;
	}

}
