package com.cv.sparkathon.transform;

import org.junit.Test;

import static org.junit.Assert.*;

public class TransformationStepsTest {

    @Test
    public void process() {
        TransformationSteps.process("transformation-config-test.properties", null);
    }
}