package com.cv.sparkathon.transform;

import org.apache.spark.sql.SparkSession;

public class TransformationMain {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("Missing transformation configuration file path");
        }
        String transformationConfigFile = args[0];
        try (SparkSession spark = SparkSession.builder()
                .config("spark.hadoop.fs.hdfs.impl.disable.cache", true)
                .enableHiveSupport()
                .getOrCreate()) {

            TransformationSteps.process(transformationConfigFile, spark);
        }
    }
}
