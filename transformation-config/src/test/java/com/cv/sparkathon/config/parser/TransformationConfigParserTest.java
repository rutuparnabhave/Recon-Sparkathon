package com.cv.sparkathon.config.parser;


import com.cv.sparkathon.config.model.*;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

public class TransformationConfigParserTest {

    @Test
    public void testParse() {
        TransformationConfigParser parser = new TransformationConfigParser();
        TransformationConfig transformationConfig = parser.parse("/transformation-config.properties");
        assertNotNull(transformationConfig);

        assertEquals(2, transformationConfig.getTransformationSteps().size());
        StepConfig book_entry_interim_loader = transformationConfig.getTransformationSteps().get(new StepInfo("book_entry_interim_loader",
                1,
                Source.SQL,
                Sets.newHashSet(Target.KAFKA)));
        assertNotNull(book_entry_interim_loader);
        assertNotNull(book_entry_interim_loader.getSourceConfig());
        assertEquals("/conf/sql/book_entry_interim_loader_new.hql",
                book_entry_interim_loader.getSourceConfig().getSourceConfiguration().getString("path"));
        assertEquals("book_entry_final",
                book_entry_interim_loader.getSourceConfig().getSourceConfiguration().getString("params.SOURCE_TABLE"));
        assertEquals("NOTHING",
                book_entry_interim_loader.getSourceConfig().getSourceConfiguration().getString("params.NOTHING"));

        assertNotNull(book_entry_interim_loader.getStepTransformationConfig());
        assertEquals("com.cv.sparkathon.GenericSQLTransformationService",
                book_entry_interim_loader.getStepTransformationConfig().getString("class"));

        assertEquals(1, book_entry_interim_loader.getTargetConfigs().size());
        Optional<TargetConfig> kafkaTargetConfig = book_entry_interim_loader.getTargetConfigs()
                .stream()
                .filter(e -> Target.KAFKA.equals(e.getTarget()))
                .findFirst();
        assertTrue(kafkaTargetConfig.isPresent());
        assertEquals("book_entry_interim_loader.bootstrap.server",
                kafkaTargetConfig.get().getTargetConfiguration().getString("producer.bootstrap.servers"));
        assertEquals("10011",
                kafkaTargetConfig.get().getTargetConfiguration().getString("producer.batch.size"));

        StepConfig store_into_temp_table = transformationConfig.getTransformationSteps().get(new StepInfo("store_into_temp_table",
                2,
                Source.SQL,
                Sets.newHashSet(Target.SPARK_MEMORY)));
        assertNotNull(store_into_temp_table);

        assertNotNull(store_into_temp_table.getStepTransformationConfig());
        assertEquals("com.cv.sparkathon.PassThroughTransformationService",
                store_into_temp_table.getStepTransformationConfig().getString("class"));
    }
}