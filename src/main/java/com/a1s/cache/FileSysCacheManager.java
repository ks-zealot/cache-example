package com.a1s.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
public class FileSysCacheManager implements CacheManager {
    private String path;
    private Logger log = LoggerFactory.getLogger(FileSysCacheManager.class);
    private AtomicBoolean isWriteInProgress = new AtomicBoolean(false);
    private List keys = new CopyOnWriteArrayList<>();
    public FileSysCacheManager(String stringProperty) {
        path = stringProperty;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void putObject(Object key, Serializable object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        FileOutputStream oos = null;
        log.debug("put object with key {}", key);
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            byte[] bytes = bos.toByteArray();
            oos =  new FileOutputStream(path + File.separator + key) ;
            isWriteInProgress.set(true);
            oos.write(bytes);
        } catch (IOException e) {
            log.error("error", e);
        } finally {
            try {
                if (bos != null)
                bos.close();
                if (oos != null)
                oos.close();
            } catch (IOException e) {
                log.error("error", e);
            }
            keys.add(key);
            isWriteInProgress.set(false);
        }
    }

    @Override
    public Object getObject(Object key) {
        ByteArrayInputStream bos = null;
        ObjectInput in = null;
        Object obj = null;
        FileInputStream oos = null;
        try {
            oos =  new FileInputStream(path + File.separator + key) ;
            byte[] bytes = Files.readAllBytes(Paths.get(path + File.separator + key));
            bos = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(bos);
            obj = in.readObject();
        } catch (IOException e) {
            log.error("error", e);
        } catch (ClassNotFoundException e) {
            log.error("error", e);
        } finally {
            try {
                if (bos != null)
                    bos.close();
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
               log.error("error", e);
            }
        }
        return obj;
    }

    @Override
    public void clear() {
        while (!isWriteInProgress.get()) {
            //wait until all stream finished
        }
        File f = new File(path);
        f.delete();
    }

    @Override
    public void delete(Object key) {
        log.debug("delete object with key {}", key);
        File file = new File(path + File.separator + key);
        file.delete();
        keys.remove(key);
    }

    @Override
    public boolean ifExist(Object key) {
      return  keys.contains(key);
    }


}
