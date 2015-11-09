package com.a1s.cache;

import org.easymock.EasyMockRunner;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
@RunWith(EasyMockRunner.class)
public class FileSysCacheManagerTest {
    @TestSubject
    FileSysCacheManager fileSysCacheManager = new FileSysCacheManager("tmp");
    @Test
    public void test() {
        File f = new File("tmp");
        f.mkdirs();
        File f1 = new File("tmp" + File.separator + "1");
        fileSysCacheManager.putObject("1", new TestObject("1"));
        assertTrue(f1.exists());
        TestObject o  = (TestObject) fileSysCacheManager.getObject("1");
        assertEquals("1", o.getField());
        fileSysCacheManager.delete("1");

        assertTrue(!f1.exists());

    }
}