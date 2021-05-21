package com.cv.sparkathon.config.model;

import com.cv.sparkathon.config.util.ConfigUtil;
import com.google.common.base.Joiner;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

import java.util.Set;
import java.util.StringJoiner;

public class StepConfig {

    private final SourceConfig sourceConfig;
    private final Configuration stepTransformationConfig;
    private final Set<TargetConfig> targetConfigs;

    public StepConfig(SourceConfig sourceConfig, Configuration stepTransformationConfig, Set<TargetConfig> targetConfigs) {
        this.sourceConfig = sourceConfig;
        this.stepTransformationConfig = stepTransformationConfig;
        this.targetConfigs = targetConfigs;
    }

    public SourceConfig getSourceConfig() {
        return sourceConfig;
    }

    public Configuration getStepTransformationConfig() {
        return stepTransformationConfig;
    }

    public Set<TargetConfig> getTargetConfigs() {
        return targetConfigs;
    }

    public boolean validate(ValidationOutput validationOutput) {
        return sourceConfig != null
                && !sourceConfig.getSourceConfiguration().isEmpty()
                && stepTransformationConfig != null
                && !stepTransformationConfig.isEmpty()
                && targetConfigs != null
                && !targetConfigs.isEmpty();
    }

    @Override
    public String toString() {
        return "StepConfig{" +
                "sourceConfig=" + sourceConfig +
                ", stepTransformationConfig=" + ConfigUtil.dumpConfig(stepTransformationConfig) +
                ", targetConfigs=" + Joiner.on(",").join(targetConfigs) +
                '}';
    }
}
