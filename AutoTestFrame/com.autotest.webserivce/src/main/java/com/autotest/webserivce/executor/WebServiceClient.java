package com.autotest.webserivce.executor;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import com.autotest.utils.StringUtils;

/**
 * WebService客户端，通过URL发送请求
 * 
 * @author veaZhao
 *
 */
public class WebServiceClient {
	private Client client = null;
	private String namespaceURI = null;

	/**
	 * @param wsdlUrl
	 * @param namespaceURI
	 */
	public WebServiceClient(String wsdlUrl, QName qName) {
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		client = dcf.createClient(wsdlUrl, qName);
	}

	public WebServiceClient(String wsdlUrl, String namespaceURI, String methodName) {
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		QName qName = new QName(namespaceURI, methodName);
		client = dcf.createClient(wsdlUrl, qName);
	}

	public WebServiceClient(String wsdlUrl, String namespaceURI) {
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		this.namespaceURI = namespaceURI;
		client = dcf.createClient(wsdlUrl);
	}

	public WebServiceClient(String wsdlUrl) {
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		client = dcf.createClient(wsdlUrl);
	}

	public Object[] invoke(String methodName, Object... parameters) throws Exception {
		Object[] tempParams = adapterParams(parameters);
		QName qName = null;
		if (StringUtils.isEmpty(this.namespaceURI)) {
			qName = new QName(methodName);
		} else {
			qName = new QName(this.namespaceURI, methodName);
		}
		Object[] result = client.invoke(qName, tempParams);
		return result;
	}

	public Object[] invoke(QName qName, Object... parameters) throws Exception {
		Object[] tempParams = adapterParams(parameters);
		Object[] result = client.invoke(qName, tempParams);
		return result;
	}

	public Object[] invoke(String namespaceURI, String methodName, Object... parameters) throws Exception {
		Object[] tempParams = adapterParams(parameters);
		QName qName = new QName(namespaceURI, methodName);
		Object[] result = client.invoke(qName, tempParams);
		return result;
	}

	private Object[] adapterParams(Object[] objects) {
		if (objects == null) {
			return null;
		}
		for (int i = 0; i < objects.length; i++) {
			if (objects[i].getClass() == Date.class) {
				GregorianCalendar nowGregorianCalendar = new GregorianCalendar();
				nowGregorianCalendar.setTime((Date) objects[i]);
				XMLGregorianCalendar xmlDatetime;
				try {
					xmlDatetime = DatatypeFactory.newInstance().newXMLGregorianCalendar(nowGregorianCalendar);
					objects[i] = xmlDatetime;
				} catch (DatatypeConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
		return objects;
	}
}
