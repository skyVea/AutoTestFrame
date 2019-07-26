package com.autotest.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xerces.internal.dom.DeferredAttrImpl;

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
	 * @param name
	 *            节点名称
	 * @param node
	 *            上级节点
	 * @param clazz
	 *            节点类型
	 * @return 获取指定类型的节点
	 */
	public static <T extends Node> List<T> getChildNodes(String name, Node node, Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		NodeList nodelist = node.getChildNodes();
		for (int i = 0; i < nodelist.getLength(); i++) {
			if (nodelist.item(i).getNodeName().equalsIgnoreCase(name) && clazz.isInstance(nodelist.item(i))) {
				list.add((T) nodelist.item(i));
			}
		}
		return list;
	}

	/**
	 * @param name
	 *            节点名称
	 * @param node
	 *            上级节点
	 * @return 获取Element类型的节点
	 */
	public static List<Element> getChildElementNodes(String name, Node node) {
		List<Element> list = getChildNodes(name, node, Element.class);
		return list;
	}

	/**
	 * @param name
	 *            节点名称
	 * @param node
	 *            上级节点
	 * @return 获取Element类型的唯一Name的节点
	 */
	public static Element getUniqueChildElementNode(String name, Node node) {
		List<Element> list = getChildNodes(name, node, Element.class);
		return list.get(0);
	}

	/**
	 * @param node
	 *            上级节点
	 * @return 获取属性值
	 */
	public static Map<String, String> getAttrs(Element node) {
		NamedNodeMap namedNodeMap = node.getAttributes();
		Map<String, String> attrmap = new HashMap<String, String>();
		List<DeferredAttrImpl> list = new ArrayList<DeferredAttrImpl>();
		Field[] fields = com.sun.org.apache.xerces.internal.dom.NamedNodeMapImpl.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			if (fields[i].getName().equals("nodes")) {
				try {
					if (fields[i].get(namedNodeMap).getClass() == ArrayList.class) {
						list = (List<DeferredAttrImpl>) fields[i].get(namedNodeMap);
						break;
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			DeferredAttrImpl tempnode = list.get(i);
			attrmap.put(tempnode.getName(), tempnode.getValue());
		}

		return attrmap;
	}

	/**
	 * @param name
	 *            节点名称
	 * @param node
	 *            上级节点
	 * @return 获取Text内容
	 */
	public static String getText(String name, Node node) {
		Element element = getUniqueChildElementNode(name, node);
		List<com.sun.org.apache.xerces.internal.dom.DeferredTextImpl> list = getChildNodes("#text", element,
				com.sun.org.apache.xerces.internal.dom.DeferredTextImpl.class);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0).getNodeValue();
	}

	/**
	 * @param node
	 *            当前节点
	 * @return 获取Text内容
	 */
	public static String getText(Element node) {
		List<com.sun.org.apache.xerces.internal.dom.DeferredTextImpl> list = getChildNodes("#text", node,
				com.sun.org.apache.xerces.internal.dom.DeferredTextImpl.class);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0).getNodeValue();
	}

	/**
	 * @param name
	 *            节点名称
	 * @param node
	 *            上级节点
	 * @return 获取Text内容
	 */
	public static String getCDATAText(String name, Node node) {
		Element element = getUniqueChildElementNode(name, node);
		List<com.sun.org.apache.xerces.internal.dom.DeferredCDATASectionImpl> list = getChildNodes("#cdata-section",
				element, com.sun.org.apache.xerces.internal.dom.DeferredCDATASectionImpl.class);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0).getNodeValue();
	}

	/**
	 * @param name节点名称
	 * @return 获取CDATAText内容
	 */
	public static String getCDATAText(Element node) {
		List<com.sun.org.apache.xerces.internal.dom.DeferredCDATASectionImpl> list = getChildNodes("#cdata-section",
				node, com.sun.org.apache.xerces.internal.dom.DeferredCDATASectionImpl.class);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0).getNodeValue();
	}

	/**
	 * @param filePath
	 * @return 返回Document节点
	 */
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
