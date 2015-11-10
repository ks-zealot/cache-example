package com.a1s.cache;

import org.easymock.EasyMockRunner;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
@RunWith(EasyMockRunner.class)
public class InMemoryCacheManagerTest {
    @TestSubject
    InMemoryCacheManager cacheManager = new InMemoryCacheManager();
    @Test
    public void test() {
        cacheManager.putObject("1", new TestObject("1"));
        TestObject o  = (TestObject) cacheManager.getObject("1");
        assertEquals("1", o.getField());
        cacheManager.delete("1");
        assertNull(cacheManager.getObject("1"));
    }
    @Test
    public void testConcurrent() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(150);
        for (int i = 0; i< 150; i++) {
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    cacheManager.putObject("1", new TestObject("1"));
                    TestObject o  = (TestObject) cacheManager.getObject("1");
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
    }
}