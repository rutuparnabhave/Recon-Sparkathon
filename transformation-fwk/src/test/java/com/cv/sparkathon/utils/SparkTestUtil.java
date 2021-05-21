package com.cv.sparkathon.utils;

import com.google.common.io.Files;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class SparkTestUtil {

    public static final File TMP_LOCATION_FILE = Files.createTempDir();
    public static final String TMP_LOCATION_PATH = TMP_LOCATION_FILE.toString();

    /**
     * Method to build spark session with hive support enabled.
     *
     * @return SparkSession
     */
    public static SparkSession getSparkSession() {
        initializeSparkWarehouse();
        initializeDerbyHome();

        //Replacing protocol
        String defaultFS = "file:///" + new Path(TMP_LOCATION_FILE.toURI().getPath()).toString();

        SparkSession sparkSession = SparkSession
                .builder()
                .config("spark.sql.orc.impl", "native")
                .config("spark.hadoop.fs.defaultFs", defaultFS)
                .config("spark.sql.warehouse.dir", defaultFS + "/spark-warehouse")
                .master("local[*]")
                .enableHiveSupport()
                .getOrCreate();
        sparkSession.sparkContext().conf().set("spark.sql.shuffle.partitions", "36");
        sparkSession.sparkContext().conf().set("spark.sql.crossJoin.enabled", "true");
        sparkSession.sparkContext().conf().set("hive.exec.dynamic.partition.mode", "nonstrict");

        return sparkSession;
    }

    private static void initializeSparkWarehouse() {
        File sparkWarehouseDir = Paths.get(TMP_LOCATION_PATH, "spark-warehouse").toFile();
        boolean created = sparkWarehouseDir.mkdirs();
        if (!created) {
            assertTrue(sparkWarehouseDir.exists());
        }
    }

    private static void initializeDerbyHome() {
        File derbySystemHome = Paths.get(TMP_LOCATION_PATH, "derby").toFile();
        boolean created = derbySystemHome.mkdirs();
        if (!created) {
            assertTrue(derbySystemHome.exists());
        }
        System.setProperty("derby.system.home", derbySystemHome.toString());
    }
}
