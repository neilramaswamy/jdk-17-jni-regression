package org.ramaswamy.jdk17;

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
        String dbPath = "/tmp/rocks-db";

        Options opts = new Options();
        opts.setCreateIfMissing(true);

        setJVMLogger(opts);
        opts.setInfoLogLevel(org.rocksdb.InfoLogLevel.DEBUG_LEVEL);

        try (final RocksDB db = RocksDB.open(opts, dbPath)) {
            db.put("hello".getBytes(), "world".getBytes());

            String result = new String(db.get("hello".getBytes()), StandardCharsets.UTF_8);
            System.out.println("Got " + result.toString());
        } catch (final RocksDBException e) {
            System.err.println(e);
        }

        logger.info("Log from Log4j");
        System.out.println("Hello World!");
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
