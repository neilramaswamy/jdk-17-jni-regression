package org.ramaswamy.jdk17;

import org.rocksdb.FlushOptions;
import org.rocksdb.OptimisticTransactionDB;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.TtlDB;

public class Repro {
    private static String DB_PATH = "/tmp/rocks-db";

    public static void main(String[] args) {
        Options opts = new Options();
        opts.setCreateIfMissing(true);

        try {
            RocksDB db = OptimisticTransactionDB.open(opts, DB_PATH);
            db.put("asdf".getBytes(), "asdf".getBytes());
            db.flush(new FlushOptions());
        } catch (RocksDBException e) {
        }

        try {
            RocksDB db = TtlDB.open(opts, DB_PATH, 100, false);
            db.put("asdf".getBytes(), "asdf".getBytes());
            db.flush(new FlushOptions());
        } catch (RocksDBException e) {
        }
    }
}
