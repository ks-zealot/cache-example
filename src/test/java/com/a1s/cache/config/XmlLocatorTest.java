package com.a1s.cache.config;

import org.easymock.EasyMockRunner;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;
@RunWith(EasyMockRunner.class)
public class XmlLocatorTest {
@TestSubject
    XmlLocator locator = new XmlLocator();
    @Test
    public void test(){
        File f = new File("." + File.separator + "cache.xml");
        assertEquals(f.getAbsoluteFile().toString(), locator.locate());
    }
}