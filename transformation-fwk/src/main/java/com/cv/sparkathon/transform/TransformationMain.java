package com.cv.sparkathon.transform;

import com.cv.sparkathon.config.model.TransformationConfig;
import com.cv.sparkathon.config.parser.TransformationConfigParser;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformationMain {
    private static final Logger LOG = LoggerFactory.getLogger(TransformationMain.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("Missing transformation configuration file path");
        }
        String transformationConfigFile = args[0];
        try (SparkSession spark = SparkSession.builder()
                .config("spark.hadoop.fs.hdfs.impl.disable.cache", true)
                .enableHiveSupport()
                .getOrCreate()) {
            TransformationConfig transformationConfig = new TransformationConfigParser().parse(transformationConfigFile);
            TransformationSteps.process(transformationConfig, spark);
        }
    }
}
