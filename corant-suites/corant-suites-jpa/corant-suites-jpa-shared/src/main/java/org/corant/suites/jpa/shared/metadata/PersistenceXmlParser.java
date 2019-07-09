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

import static org.corant.shared.util.Assertions.shouldBeFalse;
import static org.corant.shared.util.Empties.isEmpty;
import static org.corant.shared.util.Empties.isNotEmpty;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.util.ClassPaths;
import org.corant.shared.util.FileUtils;
import org.corant.suites.jpa.shared.JPAConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * corant-suites-jpa-shared
 *
 * @author bingo 上午10:43:11
 *
 */
@ApplicationScoped
public class PersistenceXmlParser {

  protected static final Logger logger = Logger.getLogger(PersistenceXmlParser.class.getName());

  public static Set<PersistenceUnitInfoMetaData> parse(URL url) {
    Set<PersistenceUnitInfoMetaData> cfgs = new HashSet<>();
    doParse(url, cfgs);
    logger.info(() -> String.format("Parsed persistence pu from %s", url.toExternalForm()));
    return cfgs;
  }

  static void doParse(Element element, PersistenceUnitInfoMetaData puimd) {
    puimd.setPersistenceUnitTransactionType(
        parseTransactionType(element.getAttribute(JPAConfig.JCX_TRANS_TYP)));
    NodeList children = element.getChildNodes();
    int len = children.getLength();
    for (int i = 0; i < len; i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element subEle = (Element) children.item(i);
        String tag = subEle.getTagName();
        if (tag.equals(JPAConfig.JCX_NON_JTA_DS)) {
          puimd.setNonJtaDataSourceName(extractContent(subEle));
        } else if (tag.equals(JPAConfig.JCX_JTA_DS)) {
          puimd.setJtaDataSourceName(extractContent(subEle));
        } else if (tag.equals(JPAConfig.JCX_PROVIDER)) {
          puimd.setPersistenceProviderClassName(extractContent(subEle));
        } else if (tag.equals(JPAConfig.JCX_CLS)) {
          puimd.addManagedClassName(extractContent(subEle));
        } else if (tag.equals(JPAConfig.JCX_MAP_FILE)) {
          puimd.addMappingFileName(extractContent(subEle));
        } else if (tag.equals(JPAConfig.JCX_JAR_FILE)) {
          puimd.getJarFileUrls().add(extractUrlContent(subEle));
        } else if (tag.equals(JPAConfig.JCX_EX_UL_CLS)) {
          puimd.setExcludeUnlistedClasses(extractBooleanContent(subEle, true));
        } else if (tag.equals(JPAConfig.JCX_VAL_MOD)) {
          puimd.setValidationMode(ValidationMode.valueOf(extractContent(subEle)));
        } else if (tag.equals(JPAConfig.JCX_SHARE_CACHE_MOD)) {
          puimd.setSharedCacheMode(SharedCacheMode.valueOf(extractContent(subEle)));
        } else if (tag.equals(JPAConfig.JCX_PROS)) {
          NodeList props = subEle.getChildNodes();
          for (int j = 0; j < props.getLength(); j++) {
            if (props.item(j).getNodeType() == Node.ELEMENT_NODE) {
              Element propElement = (Element) props.item(j);
              if (!JPAConfig.JCX_PRO.equals(propElement.getTagName())) {
                continue;
              }
              String propName = propElement.getAttribute(JPAConfig.JCX_PRO_NME).trim();
              String propValue = propElement.getAttribute(JPAConfig.JCX_PRO_VAL).trim();
              if (isEmpty(propValue)) {
                propValue = extractContent(propElement, "");
              }
              puimd.putPropertity(propName, propValue);
            }
          }
        }
      }
    }
    puimd.resolvePersistenceProvider();
  }

  static void doParse(URL url, Set<PersistenceUnitInfoMetaData> cfgs) {
    final Document doc = loadDocument(url);
    final Element top = doc.getDocumentElement();
    final NodeList children = top.getChildNodes();
    final String version = doc.getDocumentElement().getAttribute("version");
    int len = children.getLength();
    for (int i = 0; i < len; i++) {
      if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
        final Element element = (Element) children.item(i);
        final String tag = element.getTagName();
        if (tag.equals(JPAConfig.JCX_TAG)) {
          final String puName = element.getAttribute(JPAConfig.JCX_NME);
          shouldBeFalse(cfgs.stream().anyMatch(p -> p.getPersistenceUnitName().equals(puName)),
              "Persistence pu name %s dup!", tag);
          PersistenceUnitInfoMetaData puimd = new PersistenceUnitInfoMetaData(puName);
          puimd.setVersion(version);
          puimd.setPersistenceUnitRootUrl(extractRootUrl(url));
          doParse(element, puimd);
          if (isNotEmpty(puimd.getManagedClassNames())) {
            cfgs.add(puimd);
          }
        }
      }
    }
  }

  static Document loadDocument(URL url) {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(false);
      URLConnection conn = url.openConnection();
      conn.setUseCaches(false);
      try (InputStream inputStream = conn.getInputStream()) {
        final InputSource inputSource = new InputSource(inputStream);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputSource);
        // validate(document); FIXME
        return document;
      } catch (IOException | ParserConfigurationException | SAXException e) {
        throw new CorantRuntimeException(e);
      }
    } catch (IOException e) {
      throw new CorantRuntimeException(e);
    }
  }

  static PersistenceUnitTransactionType parseTransactionType(String value) {
    if (isEmpty(value)) {
      return null;
    } else if (value.equalsIgnoreCase("JTA")) {
      return PersistenceUnitTransactionType.JTA;
    } else if (value.equalsIgnoreCase("RESOURCE_LOCAL")) {
      return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    } else {
      throw new CorantRuntimeException("Unknown persistence pu transaction type : %s", value);
    }
  }

  static void validate(Document document) throws SAXException {
    final String version = document.getDocumentElement().getAttribute("version");
    List<Exception> exs = PersistenceSchema.validate(document, version);
    if (!isEmpty(exs)) {
      throw new SAXException(
          String.join("\n", exs.stream().map(Exception::getMessage).toArray(String[]::new)));
    }
  }

  private static boolean extractBooleanContent(Element element, boolean defaultBool) {
    String content = extractContent(element);
    if (content != null && content.length() > 0) {
      return Boolean.valueOf(content);
    }
    return defaultBool;
  }

  private static String extractContent(Element element) {
    return extractContent(element, null);
  }

  private static String extractContent(Element element, String defaultStr) {
    if (element == null) {
      return defaultStr;
    }
    NodeList children = element.getChildNodes();
    StringBuilder result = new StringBuilder("");
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeType() == Node.TEXT_NODE
          || children.item(i).getNodeType() == Node.CDATA_SECTION_NODE) {
        result.append(children.item(i).getNodeValue());
      }
    }
    return result.toString().trim();
  }

  private static URL extractRootUrl(URL originalURL) {
    try {
      URL rootUrl = FileUtils.extractJarFileURL(originalURL);
      if (rootUrl == null) {
        String urlToString = originalURL.toExternalForm();
        if (!urlToString.contains(ClassPaths.META_INF)) {
          logger.info(
              () -> String.format("%s should be located inside META-INF directory", urlToString));
          return null;
        }
        if (urlToString.lastIndexOf(ClassPaths.META_INF) == urlToString.lastIndexOf('/')
            - (1 + ClassPaths.META_INF.length())) {
          logger.info(() -> String.format("%s is not located in the root of META-INF directory",
              urlToString));
        }
        String rootUrlStr = urlToString.substring(0, urlToString.lastIndexOf(ClassPaths.META_INF));
        if (rootUrlStr.endsWith("/")) {
          rootUrlStr = rootUrlStr.substring(0, rootUrlStr.length() - 1);
        }
        rootUrl = new URL(rootUrlStr);
      }
      return rootUrl;
    } catch (MalformedURLException e) {
      throw new CorantRuntimeException(e);
    }
  }

  private static URL extractUrlContent(Element element) {
    try {
      return new URL(extractContent(element, null));
    } catch (MalformedURLException e) {
      throw new CorantRuntimeException(e);
    }
  }

}
