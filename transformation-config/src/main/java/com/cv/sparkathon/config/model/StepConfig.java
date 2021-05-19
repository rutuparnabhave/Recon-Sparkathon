package com.cv.sparkathon.config.model;

import org.apache.commons.configuration.Configuration;

import java.util.Set;

public class StepConfig {

    private SourceConfig sourceConfig;
    private Configuration stepTransformationConfig;
    private Set<TargetConfig> targetConfigs;

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
}
