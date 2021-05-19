package com.cv.sparkathon.config.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationOutput {
    private List<String> infos = new ArrayList<>();
    private List<String> warns = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public ValidationOutput() {
    }

    public void addInfo(String info) {
        infos.add(info);
    }

    public void addWarn(String warn) {
        warns.add(warn);
    }

    public void addError(String error) {
        errors.add(error);
    }
}
