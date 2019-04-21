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
package org.corant.suites.jndi;

import static org.corant.Corant.instance;
import static org.corant.shared.util.ClassUtils.asClass;
import static org.corant.shared.util.Empties.isNotEmpty;
import java.lang.annotation.Annotation;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

/**
 * corant-suites-jndi
 *
 * @author bingo 下午1:03:02
 *
 */
public class DefaultObjectFactory implements ObjectFactory {

  private static final DefaultObjectFactory INST = new DefaultObjectFactory();

  public DefaultObjectFactory() {

  }

  protected static DefaultObjectFactory build(Object obj, Hashtable<?, ?> environment) {
    return INST;
  }

  @Override
  public Object getObjectInstance(Object obj, Name name, Context nameCtx,
      Hashtable<?, ?> environment) throws Exception {
    if (obj instanceof DefaultReference) {
      DefaultReference reference = (DefaultReference) obj;
      Class<?> theClass = asClass(reference.getClassName());
      if (isNotEmpty(reference.qualifiers)) {
        return instance().select(theClass)
            .select(reference.qualifiers.stream().toArray(Annotation[]::new)).get();
      }
      return instance().select(theClass).get();
    } else {
      throw new RuntimeException("Object " + obj + " is not a reference");
    }
  }
}
