package org.ramaswamy.jdk17;

import java.util.Random;

import org.rocksdb.FlushOptions;
import org.rocksdb.InfoLogLevel;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class ReproStderr {
    // The number of times we write and flush (together) to RocksDB.
    private static final int NUM_ROCKSDB_OPS = 10;

    private static int NUM_BYTES_PER_RECORD = 1024;

    private static String DB_PATH = "/tmp/rocks-db-stderr";

    /**
     * Generates NUM_ROCKSDB_OPS records of size NUM_BYTES_PER_RECORD and,
     * one-by-one, writes and flushes them from RocksDB. We flush to create log
     * pressure.
     * 
     * Before this method exits, it prints the number of wall-clock E2E nanoseconds
     * it took to perform all such operations on RocksDB.
     * 
     * RocksDB logs are written to stderr.
     */
    public static void main(String[] args) {
        Options opts = new Options();
        opts.setCreateIfMissing(true);

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
            } catch (RocksDBException e) {
                System.out.println("Error cleaning up database: " + e);
            }
        } catch (final RocksDBException e) {
            System.err.println(e);
        }
    }
}
