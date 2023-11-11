package org.ramaswamy.jdk17;

import java.nio.charset.StandardCharsets;

import org.rocksdb.*;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        String dbPath = "/tmp/rocks-db";

        Options opts = new Options();
        opts.setCreateIfMissing(true);

        try (final RocksDB db = RocksDB.open(opts, dbPath)) {
            db.put("hello".getBytes(), "world".getBytes());

            String result = new String(db.get("hello".getBytes()), StandardCharsets.UTF_8);
            System.out.println("Got " + result.toString());
        } catch (final RocksDBException e) {
            System.err.println(e);
        }

        System.out.println("Hello World!");
    }
}
