package de.recondita.emden.startup;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.recondita.emden.data.DataFieldSetup;

public class ConfigParser {
	public ConfigParser(File configXml) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configXml);
			doc.getDocumentElement().normalize();
			NodeList conf = (NodeList)doc.getChildNodes().item(0);
			NodeList datafield = null;
			for (int i = 0; i < conf.getLength(); i++) {
				switch (conf.item(i).getNodeName()) {
				case "datafields":
					datafield = conf.item(i).getChildNodes();
					break;
				}
			}
			if (datafield == null) {
				throw new InvalidXMLException("No datafields provided");
			}
			for (int i = 0; i < datafield.getLength(); i++) {
				if (datafield.item(i).getNodeType() == Node.ELEMENT_NODE)
					DataFieldSetup.setDatafield(((Element)datafield.item(i)).getAttribute("name"));
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			e.printStackTrace();
		}

	}
}
