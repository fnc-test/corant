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
package org.corant.suites.webserver.tomcat;

import org.apache.catalina.startup.Tomcat;
import org.corant.suites.webserver.shared.WebServerConfigurator;

/**
 * corant-suites-webserver-tomcat
 *
 * @author bingo 上午10:09:52
 *
 */
@FunctionalInterface
public interface TomcatWebServerConfigurator extends WebServerConfigurator {

  void configure(Tomcat tomcat);

}
