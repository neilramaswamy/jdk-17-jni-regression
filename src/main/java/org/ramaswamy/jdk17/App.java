package org.ramaswamy.jdk17;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rocksdb.InfoLogLevel;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class App {
    private static final Logger logger = LogManager.getLogger("app");

    public static void main(String[] args) {
        System.out.println("Java version is " + System.getProperty("java.vesion"));

        String dbPath = "/tmp/rocks-db";

        Options opts = new Options();
        opts.setCreateIfMissing(true);

        setJVMLogger(opts);
        opts.setInfoLogLevel(org.rocksdb.InfoLogLevel.FATAL_LEVEL);

        // Write a few million records into RocksDB to stress the logging
        try (final RocksDB db = RocksDB.open(opts, dbPath)) {
            // 10M iterations, 64 * 2 bytes each
            // 10M iterations, 120bytes each => 1.2 gigabytes

            for (int iters = 0; iters < 1; iters++) {
                for (int i = 0; i < 10000000; i++) {
                    db.put(longToBytes(i), longToBytes(i + 1));
                }

                for (int i = 10000000 - 1; i >= 0; i--) {
                    byte[] val = longToBytes(i);

                    db.get(val);
                    db.delete(val);
                }
            }
        } catch (final RocksDBException e) {
            System.err.println(e);
        }

        // Clean up resources
        try {
            RocksDB.destroyDB("/tmp/rocks-db", opts);
        } catch (RocksDBException e) {
            System.out.println("Error cleaning up database: " + e);
        }
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    /**
     * Forwards RocksDB logs to our Log4j logger at the appropriate log level. 
     * 
     * @param options the RocksDB options on which the new logger will be set
     */
    private static void setJVMLogger(Options options) {
        org.rocksdb.Logger jvmLogger = new org.rocksdb.Logger(options) {
            @Override
            protected void log(InfoLogLevel level, String message) {
                switch (level) {
                    case FATAL_LEVEL:
                    case ERROR_LEVEL:
                        logger.error(message);
                        break;
                    case WARN_LEVEL:
                    case INFO_LEVEL:
                        logger.info(message);
                        break;
                    case DEBUG_LEVEL:
                        logger.debug(message);
                        break;
                    default:
                        logger.trace(message);
                }
            }
        };

        options.setLogger(jvmLogger);
    }
}
