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
package org.corant.suites.jpa.shared.metadata;

import static org.corant.config.Configurations.getGroupConfigNames;
import static org.corant.shared.util.Assertions.shouldBeNull;
import static org.corant.shared.util.Empties.isNotEmpty;
import static org.corant.shared.util.StreamUtils.streamOf;
import static org.corant.shared.util.StringUtils.defaultString;
import static org.corant.shared.util.StringUtils.defaultTrim;
import static org.corant.shared.util.StringUtils.isNotBlank;
import static org.corant.shared.util.StringUtils.split;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.suites.jpa.shared.JPAConfig;
import org.corant.suites.jpa.shared.JPAUtils;
import org.eclipse.microprofile.config.Config;

/**
 * corant-suites-jpa-shared
 *
 * @author bingo 下午4:06:33
 *
 */
public class PersistencePropertiesParser {

  private static Logger logger = Logger.getLogger(PersistencePropertiesParser.class.getName());

  public static Map<String, PersistenceUnitInfoMetaData> parse(Config config) {
    Map<String, PersistenceUnitInfoMetaData> map = new HashMap<>();
    Set<String> dfltCfgKeys = JPAConfig.defaultPropertyNames(config);
    String dfltPuNme = config
        .getOptionalValue(JPAConfig.JC_PREFIX + JPAConfig.JC_PU_NME.substring(1), String.class)
        .orElse(null);
    doParse(config, dfltPuNme, dfltCfgKeys, map);// defaults
    Map<String, List<String>> namedCfgKeys = getGroupConfigNames(config,
        s -> defaultString(s).startsWith(JPAConfig.JC_PREFIX) && !dfltCfgKeys.contains(s), 1);
    namedCfgKeys.forEach((k, v) -> {
      doParse(config, k, v, map);
      logger.info(() -> String.format("Parsed persistence pu %s from config file.", k));
    });
    return map;
  }

  protected static void doParse(Config config, String key, Collection<String> cfgNmes,
      Map<String, PersistenceUnitInfoMetaData> map) {
    final String name = defaultTrim(key);
    PersistenceUnitInfoMetaData puimd = new PersistenceUnitInfoMetaData(name);
    final String proPrefix = isNotBlank(name) ? JPAConfig.JC_PREFIX + name + JPAConfig.JC_PRO
        : JPAConfig.JC_PREFIX + JPAConfig.JC_PRO.substring(1);
    final int proPrefixLen = proPrefix.length();
    Set<String> proCfgNmes = new HashSet<>();
    cfgNmes.forEach(pn -> {
      if (pn.startsWith(proPrefix) && pn.length() > proPrefixLen) {
        // handle properties
        proCfgNmes.add(pn);
      } else if (pn.endsWith(JPAConfig.JC_TRANS_TYP)) {
        config.getOptionalValue(pn, String.class).ifPresent(s -> puimd
            .setPersistenceUnitTransactionType(PersistenceUnitTransactionType.valueOf(s)));
      } else if (pn.endsWith(JPAConfig.JC_NON_JTA_DS)) {
        config.getOptionalValue(pn, String.class).ifPresent(puimd::setNonJtaDataSourceName);
      } else if (pn.endsWith(JPAConfig.JC_JTA_DS)) {
        config.getOptionalValue(pn, String.class).ifPresent(puimd::setJtaDataSourceName);
      } else if (pn.endsWith(JPAConfig.JC_PROVIDER)) {
        config.getOptionalValue(pn, String.class).ifPresent(puimd::setPersistenceProviderClassName);
      } else if (pn.endsWith(JPAConfig.JC_CLS)) {
        config.getOptionalValue(pn, String.class)
            .ifPresent(s -> streamOf(split(s, ",")).forEach(puimd::addManagedClassName));
      } else if (pn.endsWith(JPAConfig.JC_MAP_FILE)) {
        config.getOptionalValue(pn, String.class)
            .ifPresent(s -> streamOf(split(s, ",")).forEach(puimd::addMappingFileName));
      } else if (pn.endsWith(JPAConfig.JC_JAR_FILE)) {
        config.getOptionalValue(pn, String.class).ifPresent(s -> streamOf(split(s, ","))
            .map(PersistencePropertiesParser::toUrl).forEach(puimd::addJarFileUrl));
      } else if (pn.endsWith(JPAConfig.JC_EX_UL_CLS)) {
        config.getOptionalValue(pn, Boolean.class).ifPresent(puimd::setExcludeUnlistedClasses);
      } else if (pn.endsWith(JPAConfig.JC_VAL_MOD)) {
        config.getOptionalValue(pn, String.class)
            .ifPresent(s -> puimd.setValidationMode(ValidationMode.valueOf(s)));
      } else if (pn.endsWith(JPAConfig.JC_SHARE_CACHE_MOD)) {
        config.getOptionalValue(pn, String.class)
            .ifPresent(s -> puimd.setSharedCacheMode(SharedCacheMode.valueOf(s)));
      } else if (pn.endsWith(JPAConfig.JC_CLS_PKG)) {
        config.getOptionalValue(pn, String.class).ifPresent(s -> {
          streamOf(split(s, ",", true, true)).forEach(p -> {
            JPAUtils.getPersistenceClasses(p).stream().map(Class::getName)
                .forEach(puimd::addManagedClassName);
          });
        });
      } else if (pn.endsWith(JPAConfig.JC_MAP_FILE_PATH)) {
        JPAUtils.getPersistenceMappingFiles(
            split(config.getOptionalValue(pn, String.class).orElse(JPAConfig.DFLT_ORM_XML_LOCATION),
                ",", true, true))
            .forEach(puimd::addMappingFileName);
      } else if (pn.endsWith(JPAConfig.JC_JAR_FILE)) {
        config.getOptionalValue(pn, String.class).ifPresent(s -> puimd.addJarFileUrl(toUrl(s)));
      }
    });
    if (isNotBlank(puimd.getPersistenceProviderClassName())) {
      if (isNotEmpty(puimd.getManagedClassNames())) {
        puimd.resolvePersistenceProvider();
        doParseProperties(config, proPrefix, proCfgNmes, puimd);
        shouldBeNull(map.put(name, puimd),
            "The jpa configuration error persistence pu name %s dup!", name);
      } else {
        logger.warning(
            () -> String.format("Can not find any managed classes for persistence pu %s", name));
      }
    }
  }

  private static void doParseProperties(Config config, String proPrefix, Set<String> proCfgNmes,
      PersistenceUnitInfoMetaData metaData) {
    if (!proCfgNmes.isEmpty()) {
      int len = proPrefix.length() + 1;
      for (String cfgNme : proCfgNmes) {
        config.getOptionalValue(cfgNme, String.class).ifPresent(s -> {
          String proName = cfgNme.substring(len);
          metaData.putPropertity(proName, s);
        });
      }
    }
  }

  private static URL toUrl(String urlstr) {
    try {
      return new URL(urlstr);
    } catch (MalformedURLException e) {
      throw new CorantRuntimeException(e);
    }
  }
}
