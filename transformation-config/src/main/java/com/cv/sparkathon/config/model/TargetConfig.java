package com.cv.sparkathon.config.model;

import org.apache.commons.configuration.Configuration;

public class TargetConfig {
    private Target target;
    private Configuration targetConfiguration;

    public TargetConfig(Target target, Configuration targetConfiguration) {
        this.target = target;
        this.targetConfiguration = targetConfiguration;
    }

    public Target getTarget() {
        return target;
    }

    public Configuration getTargetConfiguration() {
        return targetConfiguration;
    }
}
