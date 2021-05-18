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
 * @author bingo 下午6:21:39
 *
 */
public class StringCharacterConverter extends AbstractConverter<String, Character> {

  /**
   * @see AbstractConverter#AbstractConverter()
   */
  public StringCharacterConverter() {}

  /**
   * @see AbstractConverter#AbstractConverter(boolean)
   */
  public StringCharacterConverter(boolean throwException) {
    super(throwException);
  }

  /**
   * @see AbstractConverter#AbstractConverter(Object)
   */
  public StringCharacterConverter(Character defaultValue) {
    super(defaultValue);
  }

  /**
   * @see AbstractConverter#AbstractConverter(Object,boolean)
   */
  public StringCharacterConverter(Character defaultValue, boolean throwException) {
    super(defaultValue, throwException);
  }

  @Override
  protected Character convert(String value, Map<String, ?> hints) throws Exception {
    if (value.isEmpty()) {
      return getDefaultValue();
    }
    if (value.length() == 1) {
      return value.charAt(0);
    }
    throw new ConversionException("Can not convert from '%s' to Character", value);
  }

}
