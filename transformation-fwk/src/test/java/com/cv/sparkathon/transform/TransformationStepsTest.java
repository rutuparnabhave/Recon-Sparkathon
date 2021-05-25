package com.cv.sparkathon.transform;

import com.cv.sparkathon.utils.SparkTestUtil;
import org.apache.spark.sql.SparkSession;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TransformationStepsTest {

    private static SparkSession sparkSession;

    @BeforeClass
    public static void setup() {
        sparkSession = SparkTestUtil.getSparkSession();

    }

    @Test
    @Ignore
    public void process() {
        TransformationSteps.process("transformation-config-test.properties", sparkSession);
    }
}