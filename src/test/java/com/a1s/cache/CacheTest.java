package com.a1s.cache;

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
public class CacheTest extends TestCase {
    @TestSubject
    Cache cache =   Cache.getInstance();

    @Test
    public void test() throws ParserConfigurationException, SAXException,   IOException {
     assertNotNull(cache.getCache("cache1"));
    }
}