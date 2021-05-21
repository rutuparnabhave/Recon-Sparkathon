package com.cv.sparkathon.config.util;

import org.apache.commons.configuration.Configuration;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Stream;

public class ConfigUtil {

    public static Stream<String> dumpConfig(final Configuration config) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        config.getKeys().forEachRemaining(key -> writer.println(key.toString() + "=" + config.getString(key.toString())));
        return new BufferedReader(new StringReader(stringWriter.toString())).lines();
    }
}
