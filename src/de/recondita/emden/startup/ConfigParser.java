package de.recondita.emden.startup;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.quartz.SchedulerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.recondita.emden.data.DataFieldSetup;
import de.recondita.emden.data.Settings;
import de.recondita.emden.data.input.CSVCrawler;

public class ConfigParser {
	public ConfigParser(File configXml) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configXml);
			doc.getDocumentElement().normalize();
			NodeList conf = (NodeList) doc.getChildNodes().item(0);
			for (int i = 0; i < conf.getLength(); i++) {
				switch (conf.item(i).getNodeName()) {
				case "datafields":
					parseDatafields(conf.item(i).getChildNodes());
					break;
				case "sources":
					parseSources(conf.item(i).getChildNodes());
					break;
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			e.printStackTrace();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	private void parseDatafields(NodeList datafield) throws InvalidXMLException {
		if (datafield == null) {
			throw new InvalidXMLException("No datafields provided");
		}
		for (int i = 0; i < datafield.getLength(); i++) {
			if (datafield.item(i).getNodeType() == Node.ELEMENT_NODE)
				DataFieldSetup.setDatafield(((Element) datafield.item(i)).getAttribute("name"));
		}
	}

	private void parseSources(NodeList sources) throws InvalidXMLException, SchedulerException {
		if (sources == null) {
			throw new InvalidXMLException("No sources provided");
		}
		for (int i = 0; i < sources.getLength(); i++) {
			switch (sources.item(i).getNodeName()) {
			case "csvsource":
				parseCSV(sources.item(i).getChildNodes(), sources.item(i).getAttributes());
				break;
			}
		}
	}

	private void parseCSV(NodeList csv, NamedNodeMap namedNodeMap) throws SchedulerException {
		String path = namedNodeMap.getNamedItem("path").getNodeValue();
		String firstRowHeader = namedNodeMap.getNamedItem("firstRowHeader").getNodeValue();
		String separator = namedNodeMap.getNamedItem("separator").getNodeValue();
		String cron = Settings.getInstance().getProperty("default.cron");
		if (DataFieldSetup.getDatafields().length < 1)
			System.out.println(
					"----------------------------------------------------------------------------------------------ERROR");
		int[] rowsForDatafields = new int[DataFieldSetup.getDatafields().length];
		for (int i = 0; i < rowsForDatafields.length; i++) {
			rowsForDatafields[i] = -1;
		}
		for (int i = 0; i < csv.getLength(); i++) {
			switch (csv.item(i).getNodeName()) {
			case "schedule":
				cron = ((Element) csv.item(i)).getAttribute("cron");
				break;
			case "datafields":
				for (int j = 0; j < csv.item(i).getChildNodes().getLength(); j++) {
					if (csv.item(i).getChildNodes().item(j) instanceof Element) {
						int k = 0;
						while (!DataFieldSetup.getDatafields()[k]
								.equals(((Element) (csv.item(i).getChildNodes().item(j))).getAttribute("name")))
							k++;
						rowsForDatafields[k] = Integer
								.parseInt(((Element) (csv.item(i).getChildNodes().item(j))).getAttribute("col")) - 1;
					}
				}
				break;
			}
		}

		new Cron<CSVCrawler>(cron, new CSVCrawler(new File(path), separator,
				!firstRowHeader.toLowerCase().startsWith("f"), rowsForDatafields));
		System.out.println("Pfad: " + path);
		System.out.println("FirstRowHeader: " + firstRowHeader);
	}
}
