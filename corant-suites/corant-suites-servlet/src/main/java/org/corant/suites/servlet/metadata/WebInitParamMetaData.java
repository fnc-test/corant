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
package org.corant.suites.servlet.metadata;

import java.util.Arrays;
import javax.servlet.annotation.WebInitParam;

/**
 * corant-suites-servlet
 *
 * @author bingo 上午10:14:43
 *
 */
public class WebInitParamMetaData {

  protected String name;
  protected String value;
  protected String description;

  /**
   * @param name
   * @param value
   * @param description
   */
  public WebInitParamMetaData(String name, String value, String description) {
    super();
    this.name = name;
    this.value = value;
    this.description = description;
  }

  public WebInitParamMetaData(WebInitParam anno) {
    if (anno != null) {
      name = anno.name();
      value = anno.value();
      description = anno.description();
    }
  }

  protected WebInitParamMetaData() {}

  public static WebInitParamMetaData[] of(WebInitParam... annos) {
    return Arrays.stream(annos).map(WebInitParamMetaData::new).toArray(WebInitParamMetaData[]::new);
  }

  /**
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }


}
