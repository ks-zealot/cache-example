package com.a1s.cache.config;

import junit.framework.TestCase;
import org.easymock.EasyMockRunner;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by y.lybarskiy on 10.11.2015.
 */
@RunWith(EasyMockRunner.class)
public class XmlConfigTest extends TestCase {
@TestSubject
private XmlConfig xmlConfig = new XmlConfig();
    @Test
    public void test() throws ParserConfigurationException, SAXException, IOException {
        xmlConfig.init();
    CacheBean bean = xmlConfig.getCacheBean("cache1");
        assertEquals(bean.getDelay(), 10);
        assertEquals(bean.getPath(), "tmp");
        assertEquals(bean.getTtl(), 100);
        assertEquals(bean.getMaxFirstLevelSize(), 100);
        assertEquals(bean.getMaxSecondLevelSize(), 10000);
}
}