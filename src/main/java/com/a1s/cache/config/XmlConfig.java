package com.a1s.cache.config;


import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by y.lybarskiy on 10.11.2015.
 */
public class XmlConfig {
    private Document document = null;
    private static Logger log = LoggerFactory.getLogger(XmlConfig.class);
    private String location = null;
    public XmlConfig()   {
        XmlLocator locator = new XmlLocator();
        location = locator.locate();


    }
public void init() throws IOException, SAXException, ParserConfigurationException {
    if (location == null) {
        log.debug("could not find cache.xml, use default file");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(getClass().getResourceAsStream("resources/cache.xml"));
    } else {
        File f = new File(location);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(f);
    }
}
    public void getByName(String name) {
        Element root = document.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("cache");
        for (int x = 0, size = nodeList.getLength(); x < size; x++) {
            if (nodeList.item(x).getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
                CacheBean bean = new CacheBean();
                System.out.println(nodeList.item(x).getAttributes().getNamedItem("name").getNodeValue());
            }
        }
    }

    public int getChildCount(String parentTag, int parentIndex, String childTag) {
        NodeList list = document.getElementsByTagName(parentTag);
        Element parent = (Element) list.item(parentIndex);
        NodeList childList = parent.getElementsByTagName(childTag);
        return childList.getLength();
    }

    public String getChildValue(String parentTag, int parentIndex, String childTag,
                                int childIndex) {
        NodeList list = document.getElementsByTagName(parentTag);
        Element parent = (Element) list.item(parentIndex);
        NodeList childList = parent.getElementsByTagName(childTag);
        Element field = (Element) childList.item(childIndex);
        Node child = field.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    public String getChildAttribute(String parentTag, int parentIndex,
                                    String childTag, int childIndex,
                                    String attributeTag) {
        NodeList list = document.getElementsByTagName(parentTag);
        Element parent = (Element) list.item(parentIndex);
        NodeList childList = parent.getElementsByTagName(childTag);
        Element element = (Element) childList.item(childIndex);
        return element.getAttribute(attributeTag);
    }


    public CacheBean getCacheBean(String name) {
        int count = getChildCount("caches", 0, "cache");
        for (int i = 0; i < count; i++) {
            if (name.equals(getChildValue("cache", i, "name", 0))) {
                CacheBean bean = new CacheBean();
                bean.setDelay(Integer.parseInt(getChildValue("cache", i, "delay", 0)));
                bean.setPath(getChildValue("cache", i, "filesystemPath", 0));
                bean.setMaxSecondLevelSize(Integer.parseInt(getChildValue("cache", i, "maxSizeSecondLevel", 0)));
                bean.setMaxFirstLevelSize(Integer.parseInt(getChildValue("cache", i, "maxSizeFirstLevel", 0)));
                bean.setTtl(Long.parseLong(getChildValue("cache", i, "ttl", 0)));
                return bean;
            }
        }
        return null;
    }

}
