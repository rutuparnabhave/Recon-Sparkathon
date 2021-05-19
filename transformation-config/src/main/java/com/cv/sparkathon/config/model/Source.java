package com.cv.sparkathon.config.model;

public enum Source {
    SQL("sql"),
    HIVE("hive"),
    HDFS("hdfs"),
    KAFKA("kafka"),
    HBASE("hbase");

    String name;

    Source(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Source fromString(String value) {
        for(Source source : Source.values()) {
            if (source.getName().equals(value)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Unsupported source type: " + value);
    }
}
