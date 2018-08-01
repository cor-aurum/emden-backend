package de.recondita.emden.startup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.quartz.SchedulerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.recondita.emden.data.DataFieldSetup;
import de.recondita.emden.data.Settings;
import de.recondita.emden.data.input.SourceSetup;
import de.recondita.emden.data.input.csv.CSVCrawler;
import de.recondita.emden.data.input.sql.DatabaseHolder;
import de.recondita.emden.data.input.sql.JDBCCrawler;
import de.recondita.emden.data.input.sql.SQLDataField;

/**
 * Parses the Config, given as XML
 * 
 * @author felix
 *
 */
public class ConfigParser {

	/**
	 * Constructor
	 * 
	 * @param configXml
	 *            Config as XML
	 */
	public ConfigParser(File configXml) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configXml);
			doc.getDocumentElement().normalize();
			XPath xpath = XPathFactory.newInstance().newXPath();
			parseDatafields(
					(NodeList) xpath.compile("/emden/datafields/datafield").evaluate(doc, XPathConstants.NODESET));
			parseSources(((NodeList) xpath.compile("/emden/sources").evaluate(doc, XPathConstants.NODESET)).item(0)
					.getChildNodes());

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
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
			case "sqlsource":
				parseSQL(sources.item(i).getChildNodes(), sources.item(i).getAttributes());
				break;
			}
		}
	}

	private void parseSQL(NodeList sql, NamedNodeMap namedNodeMap) throws SchedulerException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			String id = namedNodeMap.getNamedItem("id").getNodeValue();
			SourceSetup.setSource(id);
			String cron = Settings.getInstance().getProperty("default.cron");
			try {
				cron = ((Element) ((NodeList) xpath.compile("schedule").evaluate(sql, XPathConstants.NODESET)).item(0))
						.getAttribute("cron");
			} catch (Exception e) {
				// Fall Back to default
			}
			NodeList databases = (NodeList) xpath.compile("database").evaluate(sql, XPathConstants.NODESET);
			HashMap<String, DatabaseHolder> map = new HashMap<>();
			for (int i = 0; i < databases.getLength(); i++) {
				String jdbc = ((Element) databases.item(i)).getAttribute("jdbc");
				String driver = ((Element) databases.item(i)).getAttribute("driver");
				String url = ((Element) databases.item(i)).getAttribute("url");
				String user = "";
				try {
					user = ((Element) databases.item(i)).getAttribute("user");
				} catch (Exception e) {
				}
				String password = "";
				try {
					password = ((Element) databases.item(i)).getAttribute("password");
				} catch (Exception e) {
				}
				map.put(((Element) databases.item(i)).getAttribute("id"),
						new DatabaseHolder(jdbc, driver, url, user, password));
			}

			NodeList datafields = (NodeList) xpath.compile("datafields/datafield").evaluate(sql,
					XPathConstants.NODESET);
			ArrayList<SQLDataField> sqllist = new ArrayList<>();
			for (int i = 0; i < datafields.getLength(); i++) {
				String sqlstatement = ((Element) datafields.item(i)).getAttribute("sql");
				String[] df = ((Element) datafields.item(i)).getAttribute("name").split("[,;]");
				DatabaseHolder holder = map.get(((Element) datafields.item(i)).getAttribute("database"));
				sqllist.add(new SQLDataField(holder, df, sqlstatement));
			}
			new Cron<JDBCCrawler>(cron, new JDBCCrawler(id, sqllist.toArray(new SQLDataField[sqllist.size()])));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	private void parseCSV(NodeList csv, NamedNodeMap namedNodeMap) throws SchedulerException {
		String path = namedNodeMap.getNamedItem("path").getNodeValue();
		String id = namedNodeMap.getNamedItem("id").getNodeValue();
		SourceSetup.setSource(id);
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
				!firstRowHeader.toLowerCase().startsWith("f"), rowsForDatafields, id));
	}
}
