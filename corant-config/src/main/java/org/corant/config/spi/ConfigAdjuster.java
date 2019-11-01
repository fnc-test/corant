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
package org.corant.config.spi;

import static org.corant.shared.util.StreamUtils.streamOf;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.UnaryOperator;

/**
 * corant-config
 *
 * @author bingo 下午8:50:45
 *
 */
@FunctionalInterface
public interface ConfigAdjuster
    extends Sortable, UnaryOperator<Map<String, String>> {

  static ConfigAdjuster resolve(ClassLoader classLoader) {
    ConfigAdjuster adjuster = (m) -> m;
    streamOf(ServiceLoader.load(ConfigAdjuster.class, classLoader))
        .sorted(Sortable::compare).forEach(adjuster::andThen);
    return adjuster;
  }

}
