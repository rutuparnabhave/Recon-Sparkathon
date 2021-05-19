package com.cv.sparkathon.reader;

import com.cv.sparkathon.utils.ConfigUtils;
import com.cv.sparkathon.utils.FileSystemUtils;
import com.cv.sparkathon.utils.SparkSqlUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run SQL and writing results to given writer
 */
public class SQLHiveReader {
    private static Logger LOG = LoggerFactory.getLogger(SQLHiveReader.class);

    private static final String SQL_PREFIX = "sql";

    public static Dataset<Row> execute(final SparkSession spark, final Configuration transformationStepConfig) {
        if ("hive".equals(transformationStepConfig.getString("source"))) {
            //TODO: need to implement here "registerHiveUDFs(spark, transformationStepConfig)"
            return readDataset(spark, transformationStepConfig);
        } else if (SQL_PREFIX.equals(transformationStepConfig.getString("source"))) {
            return readDataset(spark, transformationStepConfig);
        } else {
            throw new IllegalArgumentException("no source defined in transformation");
        }
    }

    private static Dataset<Row> readDataset(SparkSession spark, Configuration transformationStepConfig) {
        String sqlPath = transformationStepConfig.getString(SQL_PREFIX + ".sqlPath");
        org.apache.hadoop.conf.Configuration hadoopConf = spark.sparkContext().hadoopConfiguration();
        String sqlTemplate = FileSystemUtils.readFromHdfs(hadoopConf, sqlPath);
        spark.sparkContext().setJobGroup(transformationStepConfig.getString("transform.step"), sqlPath, false);
        if (LOG.isDebugEnabled()) {
            LOG.debug("running transformation '{}' step source with configuration: {}", sqlPath, ConfigUtils.getMap(transformationStepConfig));
        }
        try {
            SparkSqlUtils.SQLExecutionSession executionSession = SparkSqlUtils.runSqlsWithConfiguration(
                    sqlPath,
                    spark,
                    sqlTemplate,
                    transformationStepConfig.subset(SQL_PREFIX + ".param"));
            return executionSession.getResult();
        } finally {
            spark.sparkContext().clearJobGroup();
        }
    }
}
