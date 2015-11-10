package com.a1s.cache;

import org.easymock.EasyMockRunner;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
@RunWith(EasyMockRunner.class)
public class FileSysCacheManagerTest {
    @TestSubject
    FileSysCacheManager fileSysCacheManager = new FileSysCacheManager("tmp");
    private final CountDownLatch countDownLatch  = new CountDownLatch(1);
    @Test
    public void test() {
        File f = new File("tmp");
        f.mkdirs();
        File f1 = new File("tmp" + File.separator + "1");
        fileSysCacheManager.putObject("1", new TestObject("1"));
        assertTrue(f1.exists());
        TestObject o = (TestObject) fileSysCacheManager.getObject("1");
        assertEquals("1", o.getField());
        fileSysCacheManager.delete("1");

        assertTrue(!f1.exists());

    }

    @Test
    public void testConcurrent() {
        File f = new File("tmp");
        f.mkdirs();
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fileSysCacheManager.putObject("key", new TestObject("key"));
                }
            }).start();
        }
    }
    @Test
    public void testConcurrent1() {
        File f = new File("tmp");
        f.mkdirs();
        fileSysCacheManager.putObject("key", new TestObject("key"));
        List<Thread> threads = new ArrayList();
        for (int i = 0; i < 150; i++) {
        Thread t =     new Thread(

             ) {
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fileSysCacheManager.delete("key" );
            } };
            threads.add(t);
        }countDownLatch.countDown();
        for (Thread t : threads){
            t.start();
        }
    }

    @Test
    public void test1() {
        File f = new File("tmp");
        f.mkdirs();
         fileSysCacheManager.putObject(new TestObject("key"), new TestObject("value"));

    }
}