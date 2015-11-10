package com.a1s.cache;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
@RunWith(EasyMockRunner.class)
public class CacheManagerImplTest {
    @TestSubject
    CacheManagerImpl cache = new CacheManagerImpl();
    @Mock
    InMemoryCacheManager manager;
    @Mock
    FileSysCacheManager fsManager;
    @Test
    public void test() throws ConfigurationException {
        cache.setMaxSizeFirstLevel(10);
        cache.setMaxSizeSecondLevel(10);
        manager.putObject(anyObject(), anyObject());
        expectLastCall().times(10);
        fsManager.putObject(anyObject(), anyObject());
        expectLastCall().times(10);
        expect(manager.getObject("21")).andReturn(null);
        expect(fsManager.getObject("21")).andReturn(null);
        replay(manager, fsManager);
        for (int i = 0;  i < 20; i++){
            TestObject obj = new TestObject("" + i);
            cache.putObject("" + i, obj);
        }
        //cache full now
        cache.putObject("21", new TestObject("21"));
        assertNull(cache.getObject("21"));
        verify(fsManager,manager);
    }
    @Test
    public void testExpired() throws ConfigurationException, InterruptedException {
        cache.setMaxSizeFirstLevel(10);
        cache.setMaxSizeSecondLevel(10);
        cache.setTtl(1000);
        cache.setDelay(1);
        cache.init();
        TestObject obj = new TestObject("1");
        manager.putObject("1", obj);
        expectLastCall();
        expect(manager.delete("1")).andReturn(true).anyTimes();
        expect(manager.ifExist("1")).andReturn(true).anyTimes();
        replay(manager);
        cache.putObject("1", obj);
        Thread.sleep(2000);
        verify(manager);
        //////
    }


}