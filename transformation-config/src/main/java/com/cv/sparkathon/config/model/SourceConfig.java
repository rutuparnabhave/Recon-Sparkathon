package com.cv.sparkathon.config.model;

import com.cv.sparkathon.config.util.ConfigUtil;
import org.apache.commons.configuration.Configuration;

public class SourceConfig {
    private final Source source;
    private final Configuration sourceConfiguration;

    public SourceConfig(Source source, Configuration sourceConfiguration) {
        this.source = source;
        this.sourceConfiguration = sourceConfiguration;
    }

    public Source getSource() {
        return source;
    }

    public Configuration getSourceConfiguration() {
        return sourceConfiguration;
    }

    @Override
    public String toString() {
        return "SourceConfig{" +
                "source=" + source +
                ", sourceConfiguration=" + ConfigUtil.dumpConfig(sourceConfiguration) +
                '}';
    }
}
