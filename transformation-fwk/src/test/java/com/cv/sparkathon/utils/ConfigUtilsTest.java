package com.cv.sparkathon.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration.*;
import org.apache.commons.configuration.tree.NodeCombiner;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class ConfigUtilsTest {
    @Test
    public void testHierarchical() {
        HierarchicalConfiguration conf1 = ConfigurationUtils.convertToHierarchical(new MapConfiguration(
                ImmutableMap.of("a", "${b}1", "c", "${b}1---")
        ));
        HierarchicalConfiguration conf3 = ConfigurationUtils.convertToHierarchical(new MapConfiguration(
                Collections.singletonMap("a", "${b}2")
        ));
        HierarchicalConfiguration conf2 = ConfigurationUtils.convertToHierarchical(new MapConfiguration(
                ImmutableMap.of("b", "2", "x", "b")
        ));
        HierarchicalConfiguration conf4 = ConfigurationUtils.convertToHierarchical(new MapConfiguration(
                ImmutableMap.of("r.a", "33", "r.x", "44")
        ));
        HierarchicalConfiguration result = new HierarchicalConfiguration();
//		result.setRootNode(cn);
        result.addNodes("r", conf3.getRootNode().getChildren());
        result.addNodes("r", conf1.getRootNode().getChildren());
        result.addNodes(null, conf2.getRootNode().getChildren());
        result.addNodes(null, conf4.getRootNode().getChildren());

        Assert.assertEquals("22", result.getString("r.a"));
        Assert.assertEquals("21---", result.getString("r.c"));
        Assert.assertEquals("44", result.getString("r.x"));
        Assert.assertEquals("2", result.getString("b"));
    }

    @Test
    public void testComposite() {
        List a = new CompositeConfiguration(ImmutableList.of(
                new MapConfiguration(ImmutableMap.of("a", "a,s")),
                new MapConfiguration(ImmutableMap.of("a", "x,b")),
                new MapConfiguration(ImmutableMap.of("a", "c,d"))
        )).getList("a");
        Assert.assertThat(ImmutableList.copyOf((List<String>) a), Is.is(ImmutableList.of("a", "s")));
    }
}
