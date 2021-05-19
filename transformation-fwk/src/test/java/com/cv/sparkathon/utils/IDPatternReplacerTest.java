package com.cv.sparkathon.utils;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class IDPatternReplacerTest {
    @Test
    public void testSimple() {
        Map<String, String> params = ImmutableMap.<String, String>builder()
                .put("a:a", "ZA")
                .put("a", "A")
                .put("b", "B").build();

        Assert.assertEquals("A", IDPatternReplacer.replaceAll(IDPatternReplacer.ID_PATTERN, "${a}", params::get));
        Assert.assertEquals("aA", IDPatternReplacer.replaceAll(IDPatternReplacer.ID_PATTERN, "a${a}", params::get));
        Assert.assertEquals("aAa", IDPatternReplacer.replaceAll(IDPatternReplacer.ID_PATTERN, "a${a}a", params::get));
        Assert.assertEquals("aAaA", IDPatternReplacer.replaceAll(IDPatternReplacer.ID_PATTERN, "a${a}a${a}", params::get));
        Assert.assertEquals("aAaAa", IDPatternReplacer.replaceAll(IDPatternReplacer.ID_PATTERN, "a${a}a${a}a", params::get));
        Assert.assertEquals("aAaBa", IDPatternReplacer.replaceAll(IDPatternReplacer.ID_PATTERN, "a${a}a${b}a", params::get));
        Assert.assertEquals("aA\naBa", IDPatternReplacer.replaceAll(IDPatternReplacer.ID_PATTERN, "a${a}\na${b}a", params::get));
        Assert.assertEquals("aA\naB${a:a}a", IDPatternReplacer.replaceAll(IDPatternReplacer.ID_PATTERN, "a${a}\na${b}${a:a}a", params::get));
    }
}
