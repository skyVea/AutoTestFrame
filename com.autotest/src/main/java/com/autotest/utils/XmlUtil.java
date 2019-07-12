package com.autotest.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 解析-xml文件
 * 
 * @author veaZhao
 *
 */
public class XmlUtil {

	/**
	 * @param element
	 *            元素节点
	 * @return 返回某个元素下的Attributes
	 */
	public static NamedNodeMap getAttribute(Element element) {
		NamedNodeMap namedNodeMap = element.getAttributes();
		return namedNodeMap;
	}

	public static List<Element> getSubElementNodeByName(Element element, String nodeName) {
		List<Element> list = new ArrayList<Element>();
		NodeList nodelist = element.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++) {
			if (nodelist.item(i) instanceof Element && nodelist.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
				list.add((Element) nodelist.item(i));
			}
		}
		return list;
	}

	public static List<Element> getSubElementNodes(Element element) {
		List<Element> list = new ArrayList<Element>();
		NodeList nodelist = element.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++) {
			if (nodelist.item(i) instanceof Element) {
				list.add((Element) nodelist.item(i));
			}
		}
		return list;
	}

	public static List<Element> getRootElementNodes(Document document) {
		List<Element> list = new ArrayList<Element>();
		NodeList nodelist = document.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++) {
			if (nodelist.item(i) instanceof Element) {
				list.add((Element) nodelist.item(i));
			}
		}
		return list;
	}

	public static Node getRootNode(String code) {
		return null;
	}

	private void getChildNodes(Element elem) {
		NodeList list = elem.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node instanceof Element) {
				Element e1 = (Element) node;
				getChildNodes(e1);
			}
		}
	}

	public static Document readXML(String filePath) {
		File file = new File(filePath);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
