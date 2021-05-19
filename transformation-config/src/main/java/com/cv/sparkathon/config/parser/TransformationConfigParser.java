package com.cv.sparkathon.config.parser;

import com.cv.sparkathon.config.model.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.commons.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


public class TransformationConfigParser {
    private static final Logger LOG = LoggerFactory.getLogger(TransformationConfigParser.class);
    public static final String FILE_URL_SCHEME = "file:";

    public TransformationConfig parse(String transformationConfigFilePath) {
        ImmutableList.Builder<Configuration> configBuilder = ImmutableList.builder();
        configBuilder.add(new SystemConfiguration());
        if (transformationConfigFilePath != null
                && !"".equals(transformationConfigFilePath)) {
            configBuilder.add(getResourceConfig(transformationConfigFilePath));
            LOG.info("loading base configuration from args[0]");
        }
        Configuration rootConfig = new CompositeConfiguration(configBuilder.build());
        List<String> steps = rootConfig.getList("transform.steps");
        int stepNumber = 1;
        Map<StepInfo, StepConfig> stepConfigMap = new LinkedHashMap<>();
        for (String stepName : steps) {
            Configuration stepConfig = rootConfig.subset(stepName);
            Set<Target> targets = getStepTargets(rootConfig.getString("write"), stepConfig.getString("write"));
            Preconditions.checkArgument(!targets.isEmpty(), "Targets configured for the step can not be empty.");
            Source source = getSource(rootConfig, stepConfig);
            StepInfo stepInfo = new StepInfo(stepName,
                    stepNumber,
                    source,
                    targets);
            stepNumber = stepNumber + 1;
            StepConfig stepConfig1 = getStepConfig(stepName, source, targets, rootConfig);
            stepConfigMap.put(stepInfo, stepConfig1);
        }
        return new TransformationConfig(stepConfigMap);
    }

    private Source getSource(Configuration rootConfig, Configuration stepConfig) {
        String sourceInStepConfig = stepConfig.getString("source");
        String sourceInRootConfig = rootConfig.getString("source");
        String source = sourceInStepConfig != null ? sourceInStepConfig : sourceInRootConfig;
        Preconditions.checkArgument(source != null, "Source configuration can not be null, either " +
                "it should be present in root config or at transformation step config");
        return Source.fromString(source);
    }

    private StepConfig getStepConfig(String stepName, Source source, Set<Target> targets, Configuration rootConfig) {
        Configuration sourceConfig = getTransformationConfig(rootConfig.subset(stepName).subset(source.getName()),
                rootConfig.subset(stepName),
                rootConfig.subset(source.getName()),
                rootConfig);
        Configuration stepTransformationConfig = getTransformationConfig(rootConfig.subset(stepName).subset("transform"),
                rootConfig.subset(stepName),
                rootConfig.subset("transform"),
                rootConfig);
        Set<TargetConfig> targetConfigs = new LinkedHashSet<>();
        for (Target target : targets) {
            Configuration targetConfig = getTransformationConfig(
                    rootConfig.subset(stepName).subset(target.getName()),
                    rootConfig.subset(stepName),
                    rootConfig.subset(target.getName()),
                    rootConfig
            );
            targetConfigs.add(new TargetConfig(target, targetConfig));
        }
        return new StepConfig(new SourceConfig(source, sourceConfig),
                stepTransformationConfig,
                targetConfigs);
    }

    private static Configuration getTransformationConfig(Configuration... configuration) {
        ImmutableList.Builder<Configuration> builder = ImmutableList.builder();
        Arrays.stream(configuration).sequential().forEach(builder::add);
        return new CompositeConfiguration(builder.build());
    }


    private Set<Target> getStepTargets(String rootConfigWrite, String stepConfigWrite) {
        String write = stepConfigWrite != null ? stepConfigWrite : rootConfigWrite;
        Preconditions.checkArgument(write != null, "Write configuration can not be null, either " +
                "it should be present in root config or at transformation step config");
        return Arrays.stream(write.split(",")).sequential()
                .map(Target::fromString)
                .collect(Collectors.toSet());
    }

    public static MapConfiguration getResourceConfig(final String configPath) {
        try {
            return getConfig(getResourceAsStream(configPath));
        } catch (IOException e) {
            throw new ConfigurationRuntimeException(e);
        }
    }

    public static MapConfiguration getConfig(final InputStream is) throws IOException {
        Properties p = new Properties();
        try {
            p.load(is);
        } finally {
            is.close();
        }
        return new MapConfiguration(Maps.fromProperties(p));
    }

    public static InputStream getResourceAsStream(String resourcePath) {
        if (resourcePath.startsWith(FILE_URL_SCHEME)) {
            try {
                return new URL(resourcePath).openStream();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            final InputStream in
                    = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);

            return in == null ? TransformationConfigParser.class.getResourceAsStream(resourcePath) : in;
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String configPath = args[0];
            LOG.info("loading base configuration from args[0]");
        }
    }
}
