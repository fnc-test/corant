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
package org.corant.suites.query.shared.cache;

import org.corant.suites.query.shared.Querier;
import org.corant.suites.query.shared.QueryCache;

/**
 * corant-suites-query-shared
 *
 * @author bingo 下午2:03:02
 *
 */
public class MemoryNMRUQueryCache implements QueryCache<Querier> {

  @Override
  public Object get(Querier key) {
    return null;
  }

  @Override
  public Object put(Querier key, Object value) {
    return null;
  }

}
