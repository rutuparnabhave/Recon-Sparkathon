package com.cv.sparkathon.utils;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration.Configuration;

import java.util.Map;

public abstract class ConfigUtils {
    public static Map<String, String> getMap(Configuration compositeConfiguration) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        compositeConfiguration.getKeys().forEachRemaining(k -> builder.put(k.toString(), compositeConfiguration.getString(k.toString())));
        return builder.build();
    }
}
