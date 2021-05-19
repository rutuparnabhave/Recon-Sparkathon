package com.cv.sparkathon.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SparkSqlUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SparkSqlUtils.class);

    public static SQLExecutionSession runSqlsWithConfiguration(
            final String source,
            final SparkSession spark,
            final String sqlTemplate,
            final org.apache.commons.configuration.Configuration paramConfig) {
        Map<String, String> replacedParameters = Maps.newHashMap();
        String generatedSql =
                IDPatternReplacer.replaceIDs(
                        sqlTemplate,
                        key -> {
                            String paramValue = paramConfig.getString(key);
                            if (paramValue != null)
                                replacedParameters.put(key, paramValue);
                            else
                                throw new ConfigurationRuntimeException("there is no config entry for " + key);
                            return paramValue;
                        });

        try {
            LOG.info("running SQL [source:{}] [params:{}]", source, ImmutableMap.copyOf(replacedParameters));
            Dataset<Row> result = spark.sql(generatedSql);
            if (LOG.isDebugEnabled()) {
                LOG.debug("running sql: \n{}", Stream.of(generatedSql.split("\n")).collect(Collectors.joining("\n    ", "    ", "")));
                result.toJSON().collectAsList()
                        .forEach(json -> LOG.debug("transformed result row [source:{}] - {}", source, json));
            }

            return new SQLExecutionSession(result, generatedSql);
        } catch (Exception e) {
            LOG.error("error executing SQL [source:" + source + "] [params:" + ImmutableMap.copyOf(replacedParameters) + "]\n" + generatedSql + "\n" + e.getMessage(), e);
            throw new ConfigurationRuntimeException("error executing SQL\n" + generatedSql, e);
        }
    }

    public static class SQLExecutionSession {
        private final Dataset<Row> result;
        private final String generatedSQL;

        public SQLExecutionSession(Dataset<Row> result, String generatedSQL) {
            this.result = result;
            this.generatedSQL = generatedSQL;
        }

        public Dataset<Row> getResult() {
            return result;
        }

        public String getGeneratedSQL() {
            return generatedSQL;
        }
    }
}
