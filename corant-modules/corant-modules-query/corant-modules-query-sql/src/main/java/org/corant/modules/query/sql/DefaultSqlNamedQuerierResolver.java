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
package org.corant.modules.query.sql;

import static java.util.Collections.emptySet;
import static org.corant.shared.util.Objects.forceCast;
import static org.corant.shared.util.Sets.setOf;
import static org.corant.shared.util.Strings.split;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.corant.modules.query.mapping.Query;
import org.corant.modules.query.shared.AbstractNamedQuerierResolver;
import org.corant.modules.query.shared.dynamic.DynamicQuerierBuilder;
import org.corant.shared.exception.NotSupportedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import net.jcip.annotations.GuardedBy;

/**
 * corant-modules-query-sql
 *
 * @author bingo 下午3:16:56
 *
 */
@ApplicationScoped
@SuppressWarnings({"rawtypes"})
public class DefaultSqlNamedQuerierResolver extends AbstractNamedQuerierResolver<SqlNamedQuerier> {

  protected final Map<String, DynamicQuerierBuilder> builders = new ConcurrentHashMap<>();

  @Inject
  protected Logger logger;

  @Inject
  @ConfigProperty(name = "corant.query.sql.mapping-file.paths")
  protected Optional<String> mappingFilePaths;

  @GuardedBy("QueryMappingService.rwl.writeLock")
  @Override
  public void beforeQueryMappingInitialize(Collection<Query> queries, long initializedVersion) {
    clearBuilders();
  }

  @Override
  public Set<String> getMappingFilePaths() {
    if (mappingFilePaths.isPresent()) {
      return setOf(split(mappingFilePaths.get(), ",", true, true));
    }
    return emptySet();
  }

  @Override
  public DefaultSqlNamedQuerier resolve(String name, Object param) {
    DynamicQuerierBuilder builder = builders.get(name);
    if (builder == null) {
      // Note: this.builders & QueryMappingService.queries may cause dead lock
      Query query = resolveQuery(name);
      builder = builders.computeIfAbsent(name, k -> createBuilder(query));
    }
    return forceCast(builder.build(param));
  }

  protected DynamicQuerierBuilder createBuilder(Query query) {
    // FIXME decide script engine
    switch (query.getScript().getType()) {
      case JS:
        return createJsBuilder(query);
      case CDI:
        return createJbBuilder(query);
      case JSE:
      case KT:
        throw new NotSupportedException("The query script type %s not support!",
            query.getScript().getType());
      default:
        return createFmBuilder(query);
    }
  }

  protected DynamicQuerierBuilder createFmBuilder(Query query) {
    return new FreemarkerSqlQuerierBuilder(query, getQueryHandler(), getFetchQueryHandler());
  }

  protected DynamicQuerierBuilder createJbBuilder(Query query) {
    return new JavaBeanSqlQuerierBuilder(query, getQueryHandler(), getFetchQueryHandler());
  }

  protected DynamicQuerierBuilder createJsBuilder(Query query) {
    return new JavaScriptSqlQuerierBuilder(query, getQueryHandler(), getFetchQueryHandler());
  }

  @PreDestroy
  protected void onPreDestroy() {
    clearBuilders();
  }

  void clearBuilders() {
    if (!builders.isEmpty()) {
      builders.clear();
      logger.fine(() -> "Clear default sql named querier resolver builders.");
    }
  }

}
