package org.ramaswamy.jdk17;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rocksdb.FlushOptions;
import org.rocksdb.InfoLogLevel;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class App {
    private static final Logger logger = LogManager.getLogger("app");

    // The number of trials to run. In each trial, we write and flush ITERS_PER_TRIAL
    // records with a NUM_BYTES_PER_RECORD key and value.
    private static final int NUM_TRIALS = 1;

    private static final int ITERS_PER_TRIAL = 1000;

    private static int NUM_BYTES_PER_RECORD = 1024;

    private static String DB_PATH = "/tmp/rocks-db";

    private static boolean SHOULD_LOG = true;

    public static void main(String[] args) {
        Options opts = new Options();
        opts.setCreateIfMissing(true);

        if (SHOULD_LOG) {
            opts.setInfoLogLevel(org.rocksdb.InfoLogLevel.DEBUG_LEVEL);
            setJVMLogger(opts);
        }

        for (int trial = 0; trial < NUM_TRIALS; trial++) {
            try (final RocksDB db = RocksDB.open(opts, DB_PATH)) {
                // Values we use in every trial
                Random rand = new Random();
                FlushOptions flushOptions = new FlushOptions();

                long trialFlushTime = 0L;

                for (int i = 0; i < ITERS_PER_TRIAL; i++) {
                    byte[] bytes = new byte[NUM_BYTES_PER_RECORD];
                    rand.nextBytes(bytes);
                    db.put(bytes, bytes);
                    
                    long startFlush = System.nanoTime();
                    db.flush(flushOptions);
                    trialFlushTime = System.nanoTime() - startFlush;
                }

                System.out.println(trialFlushTime);

                // Clean up resources
                try {
                    db.close();
                    RocksDB.destroyDB(DB_PATH, opts);
                } catch (RocksDBException e) {
                    System.out.println("Error cleaning up database: " + e);
                }
            } catch (final RocksDBException e) {
                System.err.println(e);
            }
        }
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

        jvmLogger.setInfoLogLevel(org.rocksdb.InfoLogLevel.DEBUG_LEVEL);
        options.setLogger(jvmLogger);
    }
}
