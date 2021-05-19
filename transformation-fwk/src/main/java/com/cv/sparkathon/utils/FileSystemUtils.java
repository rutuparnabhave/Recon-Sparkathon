package com.cv.sparkathon.utils;

import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.isNull;

public class FileSystemUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemUtils.class);

    private static final Map<String, byte[]> FILE_SYSTEM_CACHE = new HashMap<>();

    public static <T> T hdfsFileSystemIO(Configuration hadoopConf, String path, IOOperation<T> ioOperation) {
        org.apache.hadoop.fs.Path hadoopPath = hadoopPath(hadoopConf, path);
        //TODO: make this retry configurable
        int retryCount = 3;

        long sleep = 1000;
        for (int i = 0; i < retryCount; i++) {
            try (FileSystem fs = hadoopPath.getFileSystem(hadoopConf)) {
                if (hadoopConf.getBoolean("io.skip.checksum.errors", true))
                    fs.setVerifyChecksum(false);
                return ioOperation.io(fs, hadoopPath);
            } catch (IOException e) {
                LOG.warn(e.getMessage(), e);
                LOG.warn("attempt #{} of {} cannot access " + hadoopPath, i, retryCount);
            }
            try {
                Thread.sleep(sleep);
                sleep = sleep * 2;
            } catch (InterruptedException e) {
                LOG.warn("interrupted");
                break;
            }
        }
        return null;
    }

    public static <T> T readFromHdfsInputStream(Configuration hadoopConf, String path, Function<InputStream, T> convertInput) {

        T ret = hdfsFileSystemIO(hadoopConf, path, (fs, hadoopPath) -> {
            try (InputStream is = fs.open(hadoopPath)) {
                return convertInput.apply(is);
            }
        });

        if (ret != null)
            return ret;
        else {
            byte[] streamFromCache;
            if (!isNull(streamFromCache = FILE_SYSTEM_CACHE.get(path))) {
                LOG.debug(path + " loaded from file system cache");
                return convertInput.apply(new ByteArrayInputStream(streamFromCache));
            } else {
                throw new ConfigurationRuntimeException("cannot load from " + path);
            }
        }
    }

    private static org.apache.hadoop.fs.Path hadoopPath(Configuration hadoopConf, String path) {
        boolean withProtocol = path.matches("[a-z]+:/.*");
        if (hadoopConf.get("fs.defaultFS") != null && hadoopConf.get("fs.defaultFS").startsWith("hdfs:") && withProtocol)
            // strip protocol from path if fs.defaultFS is hdfs
            return new org.apache.hadoop.fs.Path(new org.apache.hadoop.fs.Path(path).toUri().getPath());
        else {
            return new org.apache.hadoop.fs.Path(
                    withProtocol ?
                            path : // it is a full path, no need to prefix
                            hadoopConf.get("fs.defaultFS") + path);
        }
    }

    public static String readFromHdfs(Configuration hadoopConf, String path) {
        return readFromHdfsInputStream(hadoopConf, path, is -> {
            try {
                return IOUtils.toString(is);
            } catch (IOException e) {
                throw new ConfigurationRuntimeException("cannot load sql from " + path, e);
            }
        });
    }

    public interface IOOperation<T> {
        T io(FileSystem fs, org.apache.hadoop.fs.Path path) throws IOException;
    }
}
