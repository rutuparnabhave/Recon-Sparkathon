package com.cv.sparkathon.config.model;

import com.cv.sparkathon.config.util.ConfigUtil;
import org.apache.commons.configuration.Configuration;

public class TargetConfig {
    private final Target target;
    private final Configuration targetConfiguration;

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

    @Override
    public String toString() {
        return "TargetConfig{" +
                "target=" + target +
                ", targetConfiguration=" + ConfigUtil.dumpConfig(targetConfiguration) +
                '}';
    }
}
