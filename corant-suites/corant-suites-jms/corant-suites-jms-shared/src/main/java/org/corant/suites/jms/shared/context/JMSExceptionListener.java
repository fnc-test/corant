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
package org.corant.suites.jms.shared.context;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import org.corant.config.spi.Sortable;
import org.corant.shared.exception.CorantRuntimeException;

/**
 * corant-suites-jms-shared
 *
 * @author bingo 下午3:47:27
 *
 */
public interface JMSExceptionListener extends ExceptionListener, Sortable {

  boolean canHandle(Object ob);

  default void tryConfig(Connection conn) {
    try {
      conn.setExceptionListener(this);
    } catch (Exception e) {
      throw new CorantRuntimeException(e);
    }
  }
}
