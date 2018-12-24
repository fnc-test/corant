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
package org.corant.suites.query.sqlquery.dialect;

/**
 * asosat-query
 *
 * @author bingo 下午1:59:06
 *
 */
public class CalciteDialect implements Dialect {

  @Override
  public String getLimitSql(String sql, int offset, int limit) {
    StringBuilder sbd = new StringBuilder(50 + sql.length());
    sbd.append(sql).append(" ");
    sbd.append(" OFFSET ").append(offset).append(" ROWS");
    if (limit > 0) {
      sbd.append(" FETCH NEXT ").append(limit).append(" ROWS ONLY");
    }
    return sbd.toString();
  }

  @Override
  public boolean supportsLimit() {
    return true;
  }

}
