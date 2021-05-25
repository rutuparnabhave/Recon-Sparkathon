package com.cv.sparkathon.writer;

import com.google.common.collect.ImmutableList;
import org.apache.commons.configuration.Configuration;
import org.apache.spark.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import java.util.List;

public class HiveWriter {

    private static final Logger LOG = LoggerFactory.getLogger(HiveWriter.class);

    public static void write(Dataset<Row> df, Configuration config) {
        long startTime = System.currentTimeMillis();
        SparkSession sparkSession = df.sparkSession();
        Configuration sparkConfig = config.subset("spark");
        sparkConfig.getKeys().forEachRemaining(conf -> {
            String key = conf.toString();
            String value = sparkConfig.getString(key);
            sparkSession.conf().set(key, value);
        });

        DataFrameWriter<Row> writer = df.write()
                .mode(SaveMode.valueOf(config.getString("saveMode", "Append")))
                .format(config.getString("format", "parquet"));

        Configuration formatOptions = config.subset("format.options");
        formatOptions.getKeys().forEachRemaining(option -> {
            String key = option.toString();
            String value = formatOptions.getString(key);
            writer.option(key, value);
        });

        String hiveTableName = config.getString("table");
//        boolean tableExists = sparkSession.catalog().tableExists(hiveTableName);
        //TODO: check the compatibility of dataset with table schema

        ImmutableList<String> partitionColumns = config.containsKey("partitionBy") ?
                ImmutableList.copyOf((List<String>) config.getList("partitionBy")) :
                ImmutableList.of();
        String partitions = "";
        if (!partitionColumns.isEmpty()) {
            writer.partitionBy(JavaConversions.asScalaBuffer(partitionColumns));
            partitions = partitionColumns.toString();
        }
        writer.saveAsTable(hiveTableName);
        LOG.info("Records were written to hive [table:{}] [paritions:{}] [duration:{}ms]",
                hiveTableName, partitions, (System.currentTimeMillis() - startTime));

    }
}
