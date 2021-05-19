package com.cv.sparkathon.config.validator;

import com.cv.sparkathon.config.model.Source;
import com.cv.sparkathon.config.model.Target;
import com.cv.sparkathon.config.model.ValidationOutput;
import org.apache.commons.configuration.Configuration;

public class KafkaWriterValidator implements Validator<Configuration> {

    @Override
    public boolean isSupported(Source source) {
        return false;
    }

    @Override
    public boolean isSupported(Target target) {
        return target != null
                && target.equals(Target.KAFKA);
    }

    @Override
    public boolean validate(Configuration configuration, ValidationOutput validationOutput) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
