package controllers.managers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import models.Project;
import models.networkcomponents.Node;
import models.networkcomponents.WirelessNetwork;
import models.networkcomponents.WirelessNode;
import models.networkcomponents.events.AppEvent;
import models.networkcomponents.events.NodeEvent;
import models.networkcomponents.features.Area;
import models.networkcomponents.features.GraphicLabel;
import models.networkcomponents.features.Label;
import models.networkcomponents.protocols.ApplicationProtocol;
import models.networkcomponents.protocols.TransportProtocol;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import controllers.helpers.Helper;

/**
 * Provide methods for parsing xml to Objects of program
 * @author leecom
 *
 */
public class Parser {
	public static final int XML = 0, TXT = 1;
	
	
	static private void addConfigElement(String name, HashMap<String, HashMap<String, String>> data, String selectedData, Element eParent) {
		Element e = new Element(name + "s");
		Set<Entry<String, HashMap<String, String>>> set = data.entrySet();
		Iterator<Entry<String, HashMap<String, String>>> iterator = set.iterator();
		
		while (iterator.hasNext()) {			
			Entry<String, HashMap<String, String>> entry = (Entry<String, HashMap<String, String>>) iterator.next();
			String type = entry.getKey();
			
			Element params = new Element("params");
			
			Set<Entry<String, String>> paramSet = entry.getValue().entrySet();
			Iterator<Entry<String, String>> paramIterator = paramSet.iterator();
			
			while (paramIterator.hasNext()) {
				Entry<String, String> paramEntry = (Entry<String,String>) paramIterator.next();
				Element param = new Element("param");
				param.addAttribute(new Attribute("property", paramEntry.getKey()));
				param.addAttribute(new Attribute("value", paramEntry.getValue()));
				
				params.appendChild(param);
			}
			
			Element eData = new Element(name);
			eData.addAttribute(new Attribute("type", type));
			if (type.equals(selectedData))
				eData.addAttribute(new Attribute("default", "true"));
			
			eData.appendChild(params);
			e.appendChild(eData);
		}
		
		eParent.appendChild(e);
	}
	
	
	
	private static HashMap<String, HashMap<String, String>> loadConfigElement(Element e, StringBuilder builder) {
		Elements elements = e.getChildElements();
		HashMap<String, HashMap<String, String>> hashMap = new HashMap<>();
		builder.delete(0, builder.length());
		
		for (int i = 0; i < elements.size(); i++) {
			Element eData = elements.get(i);
			String type = eData.getAttributeValue("type");
			if (eData.getAttributeValue("default") != null && eData.getAttributeValue("default").equals("true"))
				builder.append(type);
			
			Elements params = eData.getFirstChildElement("params").getChildElements();
			HashMap<String, String> pHashMap = new HashMap<>();
			for (int j = 0; j < params.size(); j++) {
				Element param = params.get(j);				
				pHashMap.put(param.getAttributeValue("property"), param.getAttributeValue("value"));
			}
			
			hashMap.put(type, pHashMap);
		}
		
		return hashMap;
	}
	
	/**
	 * Parse xml file to Document Object
	 * @param path
	 * @return
	 * @throws ValidityException
	 * @throws ParsingException
	 * @throws IOException
	 */
	static public Document parse(String path) throws ValidityException, ParsingException, IOException {
		InputStream input;
		Document doc = null;
		
		input = new FileInputStream(path);
        Builder parser = new Builder();
		doc = parser.build(input);			

		return doc;
	}
	
	/**
	 * Save Document Object to xml file
	 * @param doc
	 * @param path
	 * @return
	 * @throws IOException
	 */
	static public boolean saveXml(Document doc, String path) throws IOException {		
		OutputStream output = new FileOutputStream(path);
        Serializer serializer = new Serializer(output, "ISO-8859-1");
        serializer.setIndent(4);
        serializer.write(doc);
        output.close();
        
        return true;
	}
	
	static public boolean saveTxt(String str, String path) throws FileNotFoundException {
		PrintStream out = null;
		try {
		    out = new PrintStream(new FileOutputStream(path));
		    out.print(str);
		}
		finally {
		    if (out != null) out.close();
		}	
		
		return true;
	}
}