package org.ramaswamy.jdk17;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rocksdb.FlushOptions;
import org.rocksdb.InfoLogLevel;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class Repro {
    private static final Logger logger = LogManager.getLogger("app");

    // The number of times we write and flush (together) to RocksDB.
    private static final int NUM_ROCKSDB_OPS = 10;

    private static int NUM_BYTES_PER_RECORD = 1024;

    private static String DB_PATH = "/tmp/rocks-db";

    private static String LOG_ARG_PREFIX = "--log=";

    private static boolean getLogLevel(String[] args) {
        for (String arg : args) {
            if (arg.startsWith(LOG_ARG_PREFIX)) {
                String directive = arg.substring(LOG_ARG_PREFIX.length());
                return Boolean.parseBoolean(directive);
            }
        }

        throw new RuntimeException("Must set log level via " + LOG_ARG_PREFIX + "<boolean> to run.");
    }

    /**
     * Generates NUM_ROCKSDB_OPS records of size NUM_BYTES_PER_RECORD and,
     * one-by-one, writes and flushes them from RocksDB. We flush to create log
     * pressure.
     * 
     * Before this method exits, it prints the number of wall-clock E2E nanoseconds
     * it took to perform all such operations on RocksDB. Because RocksDB logging is
     * done to the JVM synchronously, regressions in native thread attachment
     * mechanism should be reflected in this time.
     * 
     * Callers must pass a --log=<boolean> argument to specify whether they want
     * RocksDB to log. If logging is enabled, the Log4J file (whose location is
     * specified in the log4j2.xml file) will NOT be deleted automatically when this
     * method exits. Instead, it is kept around so that callers might inspect its
     * size; as a result, it is the caller's responsibility to delete it between
     * calls to this method.
     */
    public static void main(String[] args) {
        // ----------------------------------
        // Log level setup
        // ----------------------------------
        boolean shouldLog = getLogLevel(args);

        Options opts = new Options();
        opts.setCreateIfMissing(true);

        org.rocksdb.Logger logger = null;

        if (shouldLog) {
            opts.setInfoLogLevel(org.rocksdb.InfoLogLevel.DEBUG_LEVEL);
            logger = setJVMLogger(opts);
        }

        // ----------------------------------------
        // Calling RocksDB ops to generate logs
        // ----------------------------------------
        try (final RocksDB db = RocksDB.open(opts, DB_PATH)) {
            Random rand = new Random();
            FlushOptions flushOptions = new FlushOptions();

            long startTime = System.nanoTime();

            for (int i = 0; i < NUM_ROCKSDB_OPS; i++) {
                byte[] bytes = new byte[NUM_BYTES_PER_RECORD];
                rand.nextBytes(bytes);
                db.put(bytes, bytes);

                db.flush(flushOptions);
            }

            System.out.println(System.nanoTime() - startTime);

            // Clean up
            try {
                db.close();
                RocksDB.destroyDB(DB_PATH, opts);

                opts.close();

                if (logger != null) {
                    System.out.println("[REPRO] is owning logger handle " + logger.isOwningHandle());
                    System.out.println("[REPRO] closing logger");
                    logger.close();
                    System.out.println("[REPRO] Logger closed\n");
                }
            } catch (RocksDBException e) {
                System.out.println("Error cleaning up database: " + e);
            }
        } catch (final RocksDBException e) {
            System.err.println(e);
        }

        System.out.println("[REPRO] java exiting");
    }

    /**
     * Forwards RocksDB logs to our Log4j logger at the appropriate log level.
     * 
     * @param options the RocksDB options on which the new logger will be set
     */
    private static org.rocksdb.Logger setJVMLogger(Options options) {
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

        jvmLogger.setInfoLogLevel(org.rocksdb.InfoLogLevel.DEBUG_LEVEL);
        options.setLogger(jvmLogger);

        return jvmLogger;
    }
}
