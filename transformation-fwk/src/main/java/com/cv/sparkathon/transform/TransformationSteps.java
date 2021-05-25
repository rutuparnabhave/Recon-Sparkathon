package com.cv.sparkathon.transform;

import com.cv.sparkathon.config.model.*;
import com.cv.sparkathon.config.parser.TransformationConfigParser;
import com.cv.sparkathon.reader.SQLHiveReader;
import com.cv.sparkathon.writer.HiveWriter;
import com.cv.sparkathon.writer.SparkMemWriter;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration.Configuration;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.lang.String.format;

public class TransformationSteps {
    private static final Logger LOG = LoggerFactory.getLogger(TransformationSteps.class);

    public static final ImmutableMap<String, BiConsumer<Dataset<Row>, Configuration>> DEFAULT_WRITERS =
            ImmutableMap.<String, BiConsumer<Dataset<Row>, Configuration>>builder()
                    .put("mem", SparkMemWriter::toMem)
                    .put("hive", HiveWriter::write)
                    .build();

    public static final ImmutableMap<String, BiFunction<SparkSession, Configuration, Dataset<Row>>> DEFAULT_READERS =
            ImmutableMap.<String, BiFunction<SparkSession, Configuration, Dataset<Row>>>builder()
                    .put("sql", SQLHiveReader::execute)
                    .put("hive", SQLHiveReader::execute)
                    .build();

    /**
     * Executing a set of transformations (${transform.steps}) specified by configuration file
     *
     * @param transformationConfigFile File path containing the configuration of the whole pipeline
     * @param spark                    spark session
     */
    public static void process(
            String transformationConfigFile,
            SparkSession spark) {
        TransformationConfig transformationConfig = new TransformationConfigParser().parse(transformationConfigFile);
        ValidationOutput validationOutput = transformationConfig.validate();
        if (!validationOutput.getErrors().isEmpty()) {
            throw new RuntimeException("Invalid transformation configuration provided, validation failed with the " +
                    "following error messages:\n" + validationOutput);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug(format("Executing the pipeline with the following transformation configuration:%n {}",
                    transformationConfig.toString()));
        }
        process(transformationConfig, spark);
    }

    /**
     * Executing a set of transformations (${transform.steps}) specified by root configuration
     *
     * @param transformationConfig configuration of the whole pipeline
     * @param spark                spark session
     */
    public static void process(
            TransformationConfig transformationConfig,
            SparkSession spark) {

        Map<StepInfo, StepConfig> transformationSteps = transformationConfig.getTransformationSteps();
        transformationSteps.forEach((stepInfo, stepConfig) -> {
            SourceConfig sourceConfig = stepConfig.getSourceConfig();
            BiFunction<SparkSession, Configuration, Dataset<Row>> reader = DEFAULT_READERS.get(sourceConfig.getSource().getName());
            if (reader != null) {
                Dataset<Row> readerOutput = reader.apply(spark, sourceConfig.getSourceConfiguration());

                stepConfig.getTargetConfigs().forEach(targetConfig -> {
                    BiConsumer<Dataset<Row>, Configuration> writer = DEFAULT_WRITERS.get(targetConfig.getTarget().getName());
                    if (writer != null) {
                        writer.accept(readerOutput, targetConfig.getTargetConfiguration());
                    } else {
                        String errorMessage = "Writer unsupported for the target: " + targetConfig.getTarget();
                        LOG.error(errorMessage);
                        throw new IllegalStateException(errorMessage);
                    }
                });
            } else {
                String errorMessage = "Reader unsupported for the source: " + stepInfo.getSource();
                LOG.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        });
    }
}
