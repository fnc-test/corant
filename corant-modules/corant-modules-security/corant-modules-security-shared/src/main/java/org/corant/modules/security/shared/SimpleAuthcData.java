/*
 * Copyright (c) 2013-2021, Bingo.Chen (finesoft@gmail.com).
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
package org.corant.modules.security.shared;

import static org.corant.shared.util.Lists.newArrayList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.corant.modules.security.AuthenticationData;
import org.corant.modules.security.Principal;

/**
 * corant-modules-security-shared
 *
 * @author bingo 下午3:14:35
 *
 */
public class SimpleAuthcData implements AuthenticationData, AttributeSet {

  private static final long serialVersionUID = -187218108099683055L;

  protected Object credentials;

  protected Collection<Principal> principals;

  protected Map<String, ? extends Serializable> attributes = Collections.emptyMap();

  public SimpleAuthcData(Object credentials, Collection<? extends Principal> principals) {
    this(credentials, principals, null);
  }

  public SimpleAuthcData(Object credentials, Collection<? extends Principal> principals,
      Map<String, ? extends Serializable> attributes) {
    this.credentials = credentials;
    this.principals = Collections.unmodifiableCollection(newArrayList(principals));
    if (attributes != null) {
      this.attributes = Collections.unmodifiableMap(attributes);
    }
  }

  protected SimpleAuthcData() {}

  @Override
  public Map<String, ? extends Serializable> getAttributes() {
    return attributes;
  }

  @Override
  public Object getCredentials() {
    return credentials;
  }

  @Override
  public Collection<? extends Principal> getPrincipals() {
    return principals;
  }
}
