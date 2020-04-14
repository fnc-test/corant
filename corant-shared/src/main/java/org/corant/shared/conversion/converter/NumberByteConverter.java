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

import java.util.Map;
import org.corant.shared.conversion.ConversionException;

/**
 * corant-shared
 *
 * @author bingo 下午5:35:33
 *
 */
public class NumberByteConverter extends AbstractConverter<Number, Byte> {

  private static final long serialVersionUID = -2907903153705190681L;

  public NumberByteConverter() {
    super();
  }

  /**
   * @param throwException
   */
  public NumberByteConverter(boolean throwException) {
    super(throwException);
  }

  /**
   * @param defaultValue
   */
  public NumberByteConverter(Byte defaultValue) {
    super(defaultValue);
  }

  /**
   * @param defaultValue
   * @param throwException
   */
  public NumberByteConverter(Byte defaultValue, boolean throwException) {
    super(defaultValue, throwException);
  }

  @Override
  protected Byte convert(Number value, Map<String, ?> hints) throws Exception {
    if (value instanceof Byte) {
      return (Byte) value;
    } else if (value == null) {
      return getDefaultValue();
    }
    final long longValue = value.longValue();
    if (longValue > Byte.MAX_VALUE) {
      throw new ConversionException("Can not convert, the source value is to big for byte!");
    }
    if (longValue < Byte.MIN_VALUE) {
      throw new ConversionException("Can not convert, the source value is to small for byte!");
    }
    return Byte.valueOf(value.byteValue());
  }

}
