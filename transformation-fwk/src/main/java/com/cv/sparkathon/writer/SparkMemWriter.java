package com.cv.sparkathon.writer;

import org.apache.commons.configuration.Configuration;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalog.Catalog;
import org.apache.spark.storage.StorageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkMemWriter {
    private static final Logger LOG = LoggerFactory.getLogger(SparkMemWriter.class);
    public static final String STORAGE_LEVEL = "storage_level";

    public static void toMem(Dataset<Row> df, Configuration config) {
        String memTable = config.getString("table");

        if (config.containsKey(STORAGE_LEVEL)) {
            Catalog catalog = df.sparkSession().catalog();
            if (catalog.tableExists(memTable) && catalog.isCached(memTable)) {
                LOG.info("Un caching table: {}", memTable);
                catalog.uncacheTable(memTable);
            } else {
                LOG.info("Un cache of table {} was required but it does not exist or is not yet cached", memTable);
            }
        }

        df.createOrReplaceTempView(memTable);
        LOG.info("temp table registered [table:{}]", memTable);
        if (config.containsKey(STORAGE_LEVEL)) {
            String storageLevel = config.getString(STORAGE_LEVEL);
            df.persist(StorageLevel.fromString(storageLevel));
            LOG.info("records were written to mem [table:{}] [storageLevel:{}]", memTable, storageLevel);
        }
    }
}
