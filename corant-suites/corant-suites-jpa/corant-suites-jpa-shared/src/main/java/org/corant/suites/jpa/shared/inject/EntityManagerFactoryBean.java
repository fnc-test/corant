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
package org.corant.suites.jpa.shared.inject;

import static org.corant.kernel.util.Instances.resolvableApply;
import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.StringUtils.defaultBlank;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.BeanManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.corant.kernel.service.PersistenceService;
import org.corant.kernel.util.AbstractBean;
import org.corant.kernel.util.Qualifiers;

/**
 * corant-suites-jpa-shared
 *
 * @author bingo 上午10:34:41
 *
 */
public class EntityManagerFactoryBean extends AbstractBean<EntityManagerFactory> {

  final PersistenceUnit pu;

  /**
   * @param beanManager
   * @param pu
   */
  public EntityManagerFactoryBean(BeanManager beanManager, PersistenceUnit pu) {
    super(beanManager);
    this.pu = shouldNotNull(pu);
    qualifiers.add(Any.Literal.INSTANCE);
    qualifiers.add(Default.Literal.INSTANCE);
    qualifiers.add(Qualifiers.resolveNamed(pu.unitName()));
    types.add(EntityManagerFactory.class);
  }

  @Override
  public EntityManagerFactory create(CreationalContext<EntityManagerFactory> creationalContext) {
    return resolvableApply(PersistenceService.class, b -> b.getEntityManagerFactory(pu));
  }

  @Override
  public void destroy(EntityManagerFactory instance,
      CreationalContext<EntityManagerFactory> creationalContext) {
    if (instance != null && instance.isOpen()) {
      instance.close();
      logger.info(
          () -> String.format("Destroyed entity manager factory that persistence pu named %s.",
              defaultBlank(pu.unitName(), "unnamed")));
    }
  }

  @Override
  public String getId() {
    return EntityManagerFactoryBean.class.getName() + "." + pu.unitName();
  }

  @Override
  public String getName() {
    return "EntityManagerFactoryBean." + pu.unitName();
  }

  @Override
  public boolean isAlternative() {
    return false;
  }

  @Override
  public boolean isNullable() {
    return false;
  }

}
