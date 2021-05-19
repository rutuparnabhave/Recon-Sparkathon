package com.cv.sparkathon.config.validator;

import com.cv.sparkathon.config.model.Source;
import com.cv.sparkathon.config.model.Target;
import org.apache.commons.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidatorRegistry {
    List<Validator<Configuration>> validators;

    private static final ValidatorRegistry VALIDATOR_REGISTRY = new ValidatorRegistry();

    private ValidatorRegistry() {
        this.validators = initialize();
    }

    private List<Validator<Configuration>> initialize() {
        List<Validator<Configuration>> validators = new ArrayList<>();
        validators.add(new KafkaReaderValidator());
        validators.add(new KafkaWriterValidator());
        return validators;
    }

    public static ValidatorRegistry getInstance() {
        return VALIDATOR_REGISTRY;
    }

    public List<Validator<Configuration>> getSupportedSourceConfigValidators(Source source) {
        return validators
                .stream()
                .filter(configurationValidator -> configurationValidator.isSupported(source))
                .collect(Collectors.toList());
    }

    public List<Validator<Configuration>> getSupportedTargetConfigValidators(Target target) {
        return validators
                .stream()
                .filter(configurationValidator -> configurationValidator.isSupported(target))
                .collect(Collectors.toList());
    }
}
