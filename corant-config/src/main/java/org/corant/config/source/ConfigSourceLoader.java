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
package org.corant.config.source;

import static org.corant.shared.util.Classes.tryAsClass;
import static org.corant.shared.util.Sets.setOf;
import static org.corant.shared.util.Streams.streamOf;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.resource.URLResource;
import org.corant.shared.util.Functions;
import org.corant.shared.util.Objects;
import org.corant.shared.util.Resources;
import org.corant.shared.util.Services;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * corant-config
 *
 * @author bingo 上午10:52:25
 *
 */
public class ConfigSourceLoader {

  static final Logger logger = Logger.getLogger(ConfigSourceLoader.class.getName());

  public static List<ConfigSource> load(ClassLoader classLoader, int ordinal, Predicate<URL> filter,
      String... classPaths) throws IOException {
    Set<URI> loadedUrls = new LinkedHashSet<>();
    for (String path : classPaths) {
      streamOf(classLoader.getResources(path)).filter(filter).map(ConfigSourceLoader::toURI)
          .forEach(loadedUrls::add);
      if (Thread.currentThread().getContextClassLoader() != classLoader) {
        streamOf(Thread.currentThread().getContextClassLoader().getResources(path))
            .map(ConfigSourceLoader::toURI).forEach(loadedUrls::add);
      }
    }
    return loadedUrls.stream().map(uri -> load(toURL(uri), ordinal)).filter(Objects::isNotNull)
        .collect(Collectors.toList());
  }

  public static List<ConfigSource> load(int ordinal, Predicate<URL> filter, String... locations)
      throws IOException {
    List<ConfigSource> sources = new ArrayList<>();
    for (String path : setOf(locations)) {
      Resources.from(path).findFirst().flatMap(r -> load(filter, r, ordinal))
          .ifPresent(sources::add);
    }
    return sources;
  }

  static Optional<AbstractCorantConfigSource> load(Predicate<URL> filter, URLResource resource,
      int ordinal) {
    if (resource != null && filter.test(resource.getURL())) {
      String location = resource.getURL().getPath().toLowerCase(Locale.ROOT);
      if (location.endsWith(".properties")) {
        return Optional.of(new PropertiesConfigSource(resource, ordinal));
      } else if (location.endsWith(".yml") || location.endsWith(".yaml")) {
        if (tryAsClass("org.yaml.snakeyaml.Yaml") == null) {
          logger.warning(() -> String.format(
              "Can't not load config source [%s], the [class org.yaml.snakeyaml.Yaml] not not exists!",
              location));
        } else {
          return Optional.of(new YamlConfigSource(resource, ordinal));
        }
      } else if (location.endsWith(".json")) {
        if (Services.findRequired(JsonConfigSourceResolver.class).isEmpty()) {
          logger.warning(() -> String.format(
              "Can't not load config source [%s], the [JsonConfigSourceResolver] not not exists!",
              location));
        } else {
          return Optional.of(new JsonConfigSource(resource, ordinal));
        }
      } else if (location.endsWith(".xml")) {
        return Optional.of(new XmlConfigSource(resource, ordinal));
      }
    }
    return Optional.empty();
  }

  static AbstractCorantConfigSource load(URL resourceUrl, int ordinal) {
    return load(Functions.emptyPredicate(true), new URLResource(resourceUrl), ordinal).orElse(null);
  }

  static URI toURI(URL url) {
    try {
      return url.toURI();
    } catch (URISyntaxException e) {
      throw new CorantRuntimeException(e);
    }
  }

  static URL toURL(URI uri) {
    try {
      return uri.toURL();
    } catch (MalformedURLException e) {
      throw new CorantRuntimeException(e);
    }
  }
}
