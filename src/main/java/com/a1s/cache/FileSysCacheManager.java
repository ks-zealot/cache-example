package com.a1s.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by y.lybarskiy on 09.11.2015.
 */
public class FileSysCacheManager implements CacheManager {
    private String path;
    private Logger log = LoggerFactory.getLogger(FileSysCacheManager.class);
    private List keys = new CopyOnWriteArrayList<>();
    private final Object lock = new Lock();
    private final AtomicBoolean onClear = new AtomicBoolean(false);
    private ThreadPoolExecutor writeService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private ScheduledExecutorService clearer = Executors.newSingleThreadScheduledExecutor();
    private static final class Lock {
    }

    public FileSysCacheManager(String path) {
        this.path = path;
        clearer.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
            clearAbandonedFiles();
            }
        },5, 5, TimeUnit.SECONDS);
    }


    @Override
    public void putObject(Object key, Serializable object) {
        while (onClear.get()) {
            try {
                log.debug("wait til clear finished");
                Thread.sleep(5);
            } catch (InterruptedException e) {
                log.error("error", e);
            }
        }
        log.debug("write object with key {}", key);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            byte[] bytes = bos.toByteArray();
            File file = new File(path + File.separator + key);
            Future future = writeService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        final FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
                        channel.write(ByteBuffer.wrap(bytes));
                        channel.close();
                    } catch (IOException ioe) {
                        log.debug("error", ioe);
                    }
                }
            });
            future.get();
        } catch (IOException e) {
            log.error("error", e);
        } catch (InterruptedException e) {
            log.error("error", e);
        } catch (ExecutionException e) {
            log.error("error", e);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                log.error("error", e);
            }
            keys.add(key);
        }
    }
    private void clearAbandonedFiles(){
        File[] files = new File(path).listFiles();
        List<String> keyNames = new ArrayList<>();
        for (Object o : keys) {
            keyNames.add(o.toString());
        }
        for (File file : files){
            if (!keyNames.contains(file.getName())){
                log.debug("abandoned file {}", file.getName());
                try {
                    Files.deleteIfExists(Paths.get(file.getPath()));
                } catch (IOException e) {
                    log.debug("error", e);
                }
            }
        }
    }

    @Override
    public Serializable getObject(Object key) {
        if (!keys.contains(key)){
            return null;
        }
        ByteArrayInputStream bos = null;
        ObjectInput in = null;
        Object obj = null;
        try {
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
                if (in != null)
                    in.close();
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                log.error("error", e);
            }
        }
        return (Serializable) obj;
    }

    @Override
    public void clear() {
        onClear.compareAndSet(false, true);
        synchronized (lock) {
            while (writeService.getActiveCount() != 0 || writeService.getQueue().size() != 0) {
                try {
                    log.debug("wait until all task finished");
                    lock.wait(5);
                } catch (InterruptedException e) {
                    log.error("error", e);
                }
            }
        }
        log.debug("start clear cache, on clear now true");
        try {
            File dir = new File(path);
            for (final File f : dir.listFiles()) {
                Path path = Paths.get(f.getPath());
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            log.debug("error", e);
        } finally {
            log.debug("clear task just finished");
            onClear.compareAndSet(true, false);
        }
    }

    @Override
    public boolean delete(Object key) {
//        try {

          return  keys.remove(key);
//            Path filePath = Paths.get(path + File.separator + key);
//            return Files.deleteIfExists(filePath);
//        } catch (IOException e) {
//            log.error("error", e);
//            return false;
//        }

    }

    @Override
    public boolean ifExist(Object key) {
        return keys.contains(key);
    }


}
