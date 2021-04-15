/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.corant.config;

import static org.corant.shared.util.Assertions.shouldNotNull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import org.corant.config.CorantConfigConversion.OrdinalConverter;
import org.corant.config.source.MicroprofileConfigSources;
import org.corant.config.source.SystemEnvironmentConfigSource;
import org.corant.config.source.SystemPropertiesConfigSource;
import org.corant.shared.util.Strings;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.eclipse.microprofile.config.spi.Converter;

/**
 * corant-config
 *
 * @author bingo 下午4:04:49
 *
 */
public class CorantConfigBuilder implements ConfigBuilder {

  static final Logger logger = Logger.getLogger(CorantConfigBuilder.class.getName());

  final List<ConfigSource> sources = new ArrayList<>();
  final List<OrdinalConverter> converters =
      new LinkedList<>(CorantConfigConversion.BUILT_IN_CONVERTERS);
  protected ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

  CorantConfigBuilder() {}

  @Override
  public ConfigBuilder addDefaultSources() {
    addSource(new SystemPropertiesConfigSource());
    addSource(new SystemEnvironmentConfigSource());
    MicroprofileConfigSources.get(classLoader, Strings.EMPTY).forEach(this::addSource);
    return this;
  }

  @Override
  public ConfigBuilder addDiscoveredConverters() {
    ServiceLoader.load(Converter.class, classLoader).forEach(this::addConverter);
    return this;
  }

  @Override
  public ConfigBuilder addDiscoveredSources() {
    ServiceLoader.load(ConfigSource.class, classLoader).forEach(this::addSource);
    ServiceLoader.load(ConfigSourceProvider.class, classLoader)
        .forEach(csp -> csp.getConfigSources(classLoader).forEach(this::addSource));
    return this;
  }

  @Override
  public Config build() {
    CorantConfigSources configSources = CorantConfigSources.of(sources, classLoader);
    CorantConfig config = new CorantConfig(new CorantConfigConversion(converters), configSources);
    return config;
  }

  @Override
  public ConfigBuilder forClassLoader(ClassLoader loader) {
    classLoader = loader;
    return this;
  }

  @Override
  public <T> ConfigBuilder withConverter(Class<T> type, int priority, Converter<T> converter) {
    converters.add(new OrdinalConverter(type, converter, priority));
    return this;
  }

  @Override
  public ConfigBuilder withConverters(Converter<?>... converters) {
    for (Converter<?> converter : converters) {
      addConverter(converter);
    }
    return this;
  }

  @Override
  public ConfigBuilder withSources(ConfigSource... sources) {
    for (ConfigSource source : sources) {
      addSource(source);
    }
    return this;
  }

  void addConverter(Converter<?> converter) {
    final Class<?> cls = converter.getClass();
    Class<?> type = (Class<?>) CorantConfigConversion.getTypeOfConverter(cls);
    shouldNotNull(type, "Converter %s must be a ParameterizedType.", cls);
    converters.add(new OrdinalConverter(type, converter, CorantConfigConversion.findPriority(cls)));
    logger.fine(() -> String.format(
        "Find config converter, name:[%s], target type:[%s], class loader:[%s].", cls.getName(),
        type.getName(), classLoader));
  }

  void addSource(ConfigSource source) {
    sources.add(shouldNotNull(source, "Config source can not null."));
    logger.fine(() -> String.format(
        "Find config source, ordinal:[%s], items:[%s], name:[%s], class loader:[%s], source class:[%s].",
        source.getOrdinal(), source.getProperties().size(), source.getName(),
        classLoader.getClass().getName(), source.getClass().getName()));
  }

}
