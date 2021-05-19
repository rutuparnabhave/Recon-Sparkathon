package com.cv.sparkathon.config.model;

import org.apache.commons.configuration.Configuration;

public class SourceConfig {
    private Source source;
    private Configuration sourceConfiguration;

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
}
