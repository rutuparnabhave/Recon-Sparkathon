package com.cv.sparkathon.config.model;

import java.util.Objects;
import java.util.Set;

public class StepInfo {
    private String stepName;
    private Integer stepNumber;
    private Source source;
    private Set<Target> targets;

    public StepInfo(String stepName,
                    Integer stepNumber,
                    Source source,
                    Set<Target> targets) {
        this.stepName = stepName;
        this.stepNumber = stepNumber;
        this.source = source;
        this.targets = targets;
    }

    public boolean validate(ValidationOutput validationOutput) {
        return !"".equals(stepName) && stepNumber > 0;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Set<Target> getTargets() {
        return targets;
    }

    public void setTargets(Set<Target> targets) {
        this.targets = targets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StepInfo stepInfo = (StepInfo) o;
        return stepName.equals(stepInfo.stepName) &&
                stepNumber.equals(stepInfo.stepNumber) &&
                source == stepInfo.source &&
                targets.equals(stepInfo.targets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepName, stepNumber, source, targets);
    }
}
