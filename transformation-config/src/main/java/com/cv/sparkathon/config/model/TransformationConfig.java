package com.cv.sparkathon.config.model;


import com.cv.sparkathon.config.validator.Validator;
import com.cv.sparkathon.config.validator.ValidatorRegistry;
import com.google.common.base.Preconditions;
import org.apache.commons.configuration.Configuration;

import java.util.*;
import java.util.stream.Collectors;


public class TransformationConfig {

    private final Map<StepInfo, StepConfig> transformationSteps;

    public TransformationConfig(Map<StepInfo, StepConfig> transformationSteps) {
        Preconditions.checkArgument(!transformationSteps.isEmpty(), "Transformation steps can not be empty.");
        this.transformationSteps = transformationSteps;
    }

    public Map<StepInfo, StepConfig> getTransformationSteps() {
        return Collections.unmodifiableMap(transformationSteps);
    }

    public ValidationOutput validate() {
        ValidationOutput validationOutput = new ValidationOutput();
        transformationSteps
                .entrySet()
                .stream()
                .allMatch(stepInfoStepConfigEntry -> {
                    StepInfo stepInfo = stepInfoStepConfigEntry.getKey();
                    StepConfig stepConfig = stepInfoStepConfigEntry.getValue();
                    List<Validator<Configuration>> supportedSourceConfigValidators = ValidatorRegistry.
                            getInstance().
                            getSupportedSourceConfigValidators(stepInfo.getSource());
                    Map<Target, List<Validator<Configuration>>> supportedTargetValidatorsListMap = new HashMap<>();
                    for (Target target : stepInfo.getTargets()) {
                        List<Validator<Configuration>> validatorList = supportedTargetValidatorsListMap.get(target);
                        if (validatorList == null) {
                            validatorList = new ArrayList<>();
                        }
                        validatorList.addAll(ValidatorRegistry.
                                getInstance().
                                getSupportedTargetConfigValidators(target));
                        supportedTargetValidatorsListMap.put(target, validatorList);
                    }
                    Map<Target, Configuration> targetConfigurationMap = stepConfig
                            .getTargetConfigs()
                            .stream()
                            .collect(Collectors.toMap(TargetConfig::getTarget, TargetConfig::getTargetConfiguration));
                    return stepInfo.validate(validationOutput)
                            && stepConfig.validate(validationOutput)
                            && supportedSourceConfigValidators
                            .stream()
                            .allMatch(configurationValidator -> configurationValidator.validate(stepConfig.getSourceConfig().getSourceConfiguration(), validationOutput))
                            && stepInfo.getTargets().stream()
                            .allMatch(target -> supportedTargetValidatorsListMap
                                    .get(target)
                                    .stream()
                                    .allMatch(configurationValidator -> configurationValidator.validate(targetConfigurationMap.get(target), validationOutput)));
                });

        return validationOutput;
    }
}
