package com.cv.sparkathon.config.model;

public enum Target {
    HIVE("hive"),
    HDFS("hdfs"),
    KAFKA("kafka"),
    HBASE("hbase"),
    SPARK_MEMORY("mem");

    String name;

    Target(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Target fromString(String value) {
        for (Target target : Target.values()) {
            if (target.getName().equals(value)) {
                return target;
            }
        }
        throw new IllegalArgumentException("Unsupported target type: " + value);
    }

}
