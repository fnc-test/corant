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
package org.corant.modules.jms.shared.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.corant.shared.util.Conversions.toObject;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

/**
 * corant-modules-jms-shared
 *
 * @author bingo 下午5:19:13
 *
 */
@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface MessageSerialization {

  SerializationSchema schema();

  /**
   * corant-modules-jms-shared
   *
   * @author bingo 下午5:29:27
   *
   */
  class MessageSerializationLiteral extends AnnotationLiteral<MessageSerialization>
      implements MessageSerialization {
    private static final long serialVersionUID = -4241417907420530257L;

    private final SerializationSchema schame;

    protected MessageSerializationLiteral(SerializationSchema schame) {
      this.schame = schame;
    }

    public static MessageSerializationLiteral of(Object obj) {
      if (obj instanceof MessageSerialization) {
        return new MessageSerializationLiteral(((MessageSerialization) obj).schema());
      } else {
        return new MessageSerializationLiteral(toObject(obj, SerializationSchema.class));
      }
    }

    @Override
    public SerializationSchema schema() {
      return schame;
    }

  }

  enum SerializationSchema {

    JSON_STRING(TextMessage.class), BINARY(BytesMessage.class), JAVA_BUILTIN(
        ObjectMessage.class), MAP(MapMessage.class), KRYO(BytesMessage.class);

    private final MessageSerialization qualifier;
    private final Class<? extends Message> messageClass;

    SerializationSchema(Class<? extends Message> messageClass) {
      qualifier = MessageSerializationLiteral.of(this);
      this.messageClass = messageClass;
    }

    public Class<? extends Message> messageClass() {
      return messageClass;
    }

    public MessageSerialization qualifier() {
      return qualifier;
    }
  }
}
