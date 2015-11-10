package com.a1s.cache;

import org.easymock.EasyMockRunner;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

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

    }

    @Test
    public void testConcurrent() throws InterruptedException {
        File f = new File("tmp");
        f.mkdirs();
        CountDownLatch countDownLatch = new CountDownLatch(150);
        for (int i = 0; i< 150; i++) {
             new Thread(new Runnable() {
                 @Override
                 public void run()
                 {
                     fileSysCacheManager.putObject("key", new TestObject("key"));
                    countDownLatch.countDown();
                 }
             }).start();
        }
        countDownLatch.await();
        TestObject test = (TestObject) fileSysCacheManager.getObject("key");
        assertEquals("key", test.getField());

    }


    @Test
    public void testConcurrentGet() throws InterruptedException {
        File f = new File("tmp");
        f.mkdirs();
        CountDownLatch countDownLatch = new CountDownLatch(150);
        fileSysCacheManager.putObject("testkey", new TestObject("testkey"));

        for (int i = 0; i< 150; i++) {
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    fileSysCacheManager.getObject("testkey");
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();

    }
    @Test
    public void testConcurrentDelete() throws InterruptedException {
        File f = new File("tmp");
        f.mkdirs();
        CountDownLatch countDownLatch = new CountDownLatch(150);
        fileSysCacheManager.putObject("testdelete", new TestObject("testdelete"));
        for (int i = 0; i< 150; i++) {
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    fileSysCacheManager.delete("testdelete");
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();

    }


    @Test
    public void testDeleteSingle() throws InterruptedException {
        File f = new File("tmp");
        f.mkdirs();
        fileSysCacheManager.putObject("testdeletesingle", new TestObject("testdeletesingle"));

        fileSysCacheManager.delete("testdeletesingle");

    }


    @Test
    public void testClear() throws InterruptedException {
        File f = new File("tmp");
        f.mkdirs();
        CountDownLatch countDownLatch = new CountDownLatch(150);
        for (int i = 0; i< 150; i++) {
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    fileSysCacheManager.putObject("key", new TestObject("key"));
                    countDownLatch.countDown();
                }
            }).start();
        }
        fileSysCacheManager.clear();
        countDownLatch.await();

    }

    @Test
    public void testClear1() throws InterruptedException {
        File f = new File("tmp");
        f.mkdirs();
        fileSysCacheManager.clear();
    }
    @Test
    public void testClearAbandoned() throws InterruptedException {
        File f = new File("tmp");
        f.mkdirs();
        fileSysCacheManager.putObject("1", new TestObject("1"));
        fileSysCacheManager.delete("1");
        assertNull(fileSysCacheManager.getObject("1"));//not present in cache
        File f1 = new File("tmp" + File.separator + "1");
        assertTrue(f1.exists()); // but file exist
        Thread.sleep(5000);
        assertFalse(f1.exists()); // not anymore
        fileSysCacheManager.clear();
    }
}