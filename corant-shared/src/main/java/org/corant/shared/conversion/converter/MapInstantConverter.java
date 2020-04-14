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
package org.corant.shared.conversion.converter;

import java.time.Instant;
import java.util.Map;
import org.corant.shared.conversion.ConversionException;

/**
 * corant-shared
 *
 * @author bingo 上午10:47:31
 *
 */
@SuppressWarnings("rawtypes")
public class MapInstantConverter extends AbstractTemporalConverter<Map, Instant> {

  private static final long serialVersionUID = -5931968882747573116L;

  public MapInstantConverter() {
    super();
  }

  /**
   * @param throwException
   */
  public MapInstantConverter(boolean throwException) {
    super(throwException);
  }

  /**
   * @param defaultValue
   */
  public MapInstantConverter(Instant defaultValue) {
    super(defaultValue);
  }

  /**
   * @param defaultValue
   * @param throwException
   */
  public MapInstantConverter(Instant defaultValue, boolean throwException) {
    super(defaultValue, throwException);
  }

  @Override
  protected Instant convert(Map value, Map<String, ?> hints) throws Exception {
    if (value == null) {
      return getDefaultValue();
    }
    if (value.containsKey("epochSecond") && value.containsKey("nano")) {
      return Instant.ofEpochSecond(resolveLong(value.get("epochSecond")),
          resolveLong(value.get("nano")));
    } else if (value.containsKey("epochSecond")) {
      return Instant.ofEpochSecond(resolveLong(value.get("epochSecond")));
    }
    throw new ConversionException("Can't found the value of 'epochSecond'");
  }

}
