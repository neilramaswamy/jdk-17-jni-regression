package org.ramaswamy.jdk17;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class Repro {
    private static String DB_PATH = "/tmp/rocks-db";

    public static void main(String[] args) {
        Options opts = new Options();
        opts.setCreateIfMissing(true);

        try (final RocksDB db = RocksDB.open(opts, DB_PATH)) {
            db.put("foo".getBytes(), "bar".getBytes());
            db.close();
        } catch (final RocksDBException e) {
            System.err.println(e);
        }

        try (final RocksDB db = RocksDB.open(opts, DB_PATH)) {
            db.getColumnFamilyMetaData();
            db.getColumnFamilyMetaData();

            db.close();
            opts.close();
        } catch (final RocksDBException e) {
            System.err.println(e);
        }
    }
}
