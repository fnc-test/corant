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
package org.corant.suites.query.mongodb.cdi;

import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.StringUtils.EMPTY;
import static org.corant.shared.util.StringUtils.asDefaultString;
import static org.corant.shared.util.StringUtils.isBlank;
import static org.corant.suites.cdi.Instances.findNamed;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.suites.query.mongodb.AbstractMgNamedQueryService;
import org.corant.suites.query.mongodb.MgNamedQuerier;
import org.corant.suites.query.shared.AbstractNamedQuerierResolver;
import org.corant.suites.query.shared.NamedQueryService;
import org.corant.suites.query.shared.NamedQueryServiceManager;
import org.corant.suites.query.shared.mapping.Query.QueryType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.mongodb.client.MongoDatabase;

/**
 * corant-suites-query-sql
 *
 * @author bingo 下午6:04:28
 *
 */
@Priority(1)
@ApplicationScoped
@Alternative
public class MgNamedQueryServiceManager implements NamedQueryServiceManager {

  static final Map<String, NamedQueryService> services = new ConcurrentHashMap<>(); // FIXME scope

  @Inject
  protected Logger logger;

  @Inject
  protected AbstractNamedQuerierResolver<MgNamedQuerier> resolver;

  @Inject
  @ConfigProperty(name = "query.mongodb.max-select-size", defaultValue = "128")
  protected Integer maxSelectSize;

  @Inject
  @ConfigProperty(name = "query.mongodb.limit", defaultValue = "16")
  protected Integer limit;

  @Inject
  @ConfigProperty(name = "query.mongodb.default-qualifier-value")
  protected Optional<String> defaultQualifierValue;

  @Override
  public NamedQueryService get(Object qualifier) {
    String key = resolveQualifier(qualifier);
    return services.computeIfAbsent(key, k -> {
      final String databaseName = isBlank(k) ? defaultQualifierValue.orElse(EMPTY) : k;
      logger.fine(() -> String.format(
          "Create default mongodb named query service, the data base is [%s].", databaseName));
      return new DefaultMgNamedQueryService(databaseName, this);
    });
  }

  @Override
  public QueryType getType() {
    return QueryType.MG;
  }

  @Produces
  @MgQuery
  NamedQueryService produce(InjectionPoint ip) {
    final Annotated annotated = ip.getAnnotated();
    final MgQuery sc = shouldNotNull(annotated.getAnnotation(MgQuery.class));
    return get(sc);
  }

  String resolveQualifier(Object qualifier) {
    if (qualifier instanceof MgQuery) {
      return ((MgQuery) qualifier).value();
    } else {
      return asDefaultString(qualifier);
    }
  }

  /**
   * corant-suites-query-mongodb
   *
   * @author bingo 下午3:41:34
   *
   */
  public static class DefaultMgNamedQueryService extends AbstractMgNamedQueryService {

    protected final MongoDatabase dataBase;
    protected final int defaultMaxSelectSize;
    protected final int defaultLimit;
    protected final AbstractNamedQuerierResolver<MgNamedQuerier> resolver;

    /**
     * @param dataBase
     * @param manager
     */
    public DefaultMgNamedQueryService(String dataBase, MgNamedQueryServiceManager manager) {
      this.dataBase =
          findNamed(MongoDatabase.class, dataBase).orElseThrow(() -> new CorantRuntimeException(
              "Can't build default mongo named query, the data base named %s not found.",
              dataBase));
      resolver = manager.resolver;
      defaultMaxSelectSize = manager.maxSelectSize;
      defaultLimit = manager.limit < 1 ? DEFAULT_LIMIT : manager.limit;
    }

    /**
     * @param dataBase
     * @param defaultMaxSelectSize
     * @param defaultLimit
     * @param resolver
     */
    protected DefaultMgNamedQueryService(MongoDatabase dataBase, int defaultMaxSelectSize,
        int defaultLimit, AbstractNamedQuerierResolver<MgNamedQuerier> resolver) {
      super();
      this.dataBase = dataBase;
      this.defaultMaxSelectSize = defaultMaxSelectSize;
      this.defaultLimit = defaultLimit;
      this.resolver = resolver;
    }

    @Override
    protected MongoDatabase getDataBase() {
      return dataBase;
    }

    @Override
    protected int getDefaultLimit() {
      return defaultLimit;
    }

    @Override
    protected int getDefaultMaxSelectSize() {
      return defaultMaxSelectSize;
    }

    @Override
    protected AbstractNamedQuerierResolver<MgNamedQuerier> getQuerierResolver() {
      return resolver;
    }

  }
}
