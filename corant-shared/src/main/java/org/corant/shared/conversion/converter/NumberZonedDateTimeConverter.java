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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import org.corant.shared.conversion.ConversionException;
import org.corant.shared.conversion.ConverterHints;

/**
 * corant-shared
 *
 * @author bingo 下午5:35:33
 *
 */
public class NumberZonedDateTimeConverter extends AbstractTemporalConverter<Number, ZonedDateTime> {

  private static final long serialVersionUID = 7155121548134208922L;

  public NumberZonedDateTimeConverter() {
    super();
  }

  /**
   * @param throwException
   */
  public NumberZonedDateTimeConverter(boolean throwException) {
    super(throwException);
  }

  /**
   * @param defaultValue
   */
  public NumberZonedDateTimeConverter(ZonedDateTime defaultValue) {
    super(defaultValue);
  }

  /**
   * @param defaultValue
   * @param throwException
   */
  public NumberZonedDateTimeConverter(ZonedDateTime defaultValue, boolean throwException) {
    super(defaultValue, throwException);
  }

  @Override
  public boolean isPossibleDistortion() {
    return true;
  }

  @Override
  protected ZonedDateTime convert(Number value, Map<String, ?> hints) throws Exception {
    if (value == null) {
      return getDefaultValue();
    }
    Optional<ZoneId> ozoneId = resolveHintZoneId(hints);
    if (!ozoneId.isPresent()) {
      if (!isStrict(hints)) {
        warn(LocalDateTime.class, value);
        if (ChronoUnit.SECONDS
            .equals(ConverterHints.getHint(hints, ConverterHints.CVT_TEMPORAL_EPOCH_KEY))) {
          return ZonedDateTime.ofInstant(Instant.ofEpochSecond(value.longValue()),
              ZoneId.systemDefault());
        } else {
          return ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.longValue()),
              ZoneId.systemDefault());
        }
      }
    } else {
      if (ChronoUnit.SECONDS
          .equals(ConverterHints.getHint(hints, ConverterHints.CVT_TEMPORAL_EPOCH_KEY))) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(value.longValue()), ozoneId.get());
      } else {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.longValue()), ozoneId.get());
      }
    }
    throw new ConversionException("Can't not convert %s to ZonedDateTime", value);
  }
}
