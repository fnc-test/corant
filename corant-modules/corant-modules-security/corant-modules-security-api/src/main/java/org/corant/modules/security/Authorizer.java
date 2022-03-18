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
package org.corant.modules.security;

import org.corant.shared.ubiquity.Sortable;

/**
 * corant-modules-security-api
 *
 * @author bingo 12:24:41
 *
 */
public interface Authorizer extends Sortable {

  /**
   * Check whether the caller has access privileges through context and pre-configured roles or
   * permissions information, if not, throw an authorization exception.
   *
   * @param context the caller context, in general the context is current SecurityContext.
   * @param roleOrPermit the necessary (pre-configured) roles or permissions, used to compute
   *        whether the caller has access privileges with the roles or permissions acquired by the
   *        caller context.
   * @throws AuthorizationException if the caller has not access permissions with the roles or
   *         permissions acquired by the context.
   */
  default void checkAccess(Object context, Object roleOrPermit) throws AuthorizationException {
    boolean success = false;
    try {
      preCheckAccess(context, roleOrPermit);
      if (!testAccess(context, roleOrPermit)) {
        throw new AuthorizationException((Object) SecurityMessageCodes.UNAUTHZ_ACCESS);
      }
      success = true;
    } finally {
      postCheckAccess(success);
    }
  }

  /**
   * A callback after access check.
   *
   * @param success true means pass the access check, false means access check failed
   */
  default void postCheckAccess(boolean success) {}

  /**
   * A callback before access check.
   *
   * @param context the caller context, in general the context is current SecurityContext.
   * @param roleOrPermit the necessary (pre-configured) roles or permissions, used to compute
   *        whether the caller has access privileges with the roles or permissions acquired by the
   *        caller context.
   */
  default void preCheckAccess(Object context, Object roleOrPermit) {}

  /**
   * Tests whether the current context has access.
   *
   * @param context the caller context, in general the context is current SecurityContext.
   * @param roleOrPermit the necessary (pre-configured) roles or permissions, used to compute
   *        whether the caller has access privileges with the roles or permissions acquired by the
   *        caller context.
   * @return whether have access permissions
   */
  boolean testAccess(Object context, Object roleOrPermit);

}
