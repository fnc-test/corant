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
package org.corant.suites.query.jpql;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.corant.suites.lang.javascript.NashornScriptEngines;
import org.corant.suites.query.shared.FetchQueryResolver;
import org.corant.suites.query.shared.QueryParameter;
import org.corant.suites.query.shared.QueryResolver;
import org.corant.suites.query.shared.dynamic.AbstractDynamicQuerierBuilder;
import org.corant.suites.query.shared.mapping.Query;

/**
 * corant-suites-query
 *
 * @author bingo 下午7:46:22
 *
 */
public class JavascriptJpqlQuerierBuilder
    extends AbstractDynamicQuerierBuilder<Object[], String, JpqlNamedQuerier> {

  final Function<Object[], Object> execution;

  /**
   * @param query
   * @param queryResolver
   * @param fetchQueryResolver
   */
  protected JavascriptJpqlQuerierBuilder(Query query, QueryResolver queryResolver,
      FetchQueryResolver fetchQueryResolver) {
    super(query, queryResolver, fetchQueryResolver);
    execution = NashornScriptEngines.compileFunction(query.getScript().getCode(), "p", "up");
  }

  /**
   * Generate SQL script with placeholder, and converted the parameter to appropriate type.
   */
  @Override
  public DefaultJpqlNamedQuerier build(Object param) {
    QueryParameter queryParam = getQueryResolver().resolveParameter(getQuery(), param);
    List<Object> useParam = new ArrayList<>();
    Object script = getExecution().apply(new Object[] {queryParam, useParam});
    return new DefaultJpqlNamedQuerier(getQuery(), queryParam, getQueryResolver(),
        getFetchQueryResolver(), useParam.toArray(new Object[useParam.size()]), script.toString());
  }

  public Function<Object[], Object> getExecution() {
    return execution;
  }

}
