package com.cv.sparkathon.config.validator;

import com.cv.sparkathon.config.model.Source;
import com.cv.sparkathon.config.model.Target;
import com.cv.sparkathon.config.model.ValidationOutput;

public interface Validator<Configuration> {
    boolean isSupported(Source source);
    boolean isSupported(Target target);
    boolean validate(Configuration configuration, ValidationOutput validationOutput);
}
