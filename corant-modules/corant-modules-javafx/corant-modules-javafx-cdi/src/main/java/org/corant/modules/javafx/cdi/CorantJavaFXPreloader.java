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
package org.corant.modules.javafx.cdi;

import org.corant.Corant;
import javafx.application.Preloader;
import javafx.stage.Stage;

/**
 * corant-modules-javafx-cdi
 *
 * @author bingo 下午11:51:55
 *
 */
public class CorantJavaFXPreloader extends Preloader {

  @Override
  public boolean handleErrorNotification(ErrorNotification info) {
    stopCorant();
    return super.handleErrorNotification(info);
  }

  @Override
  public void init() throws Exception {
    startCorant();
    super.init();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {}

  @Override
  public void stop() throws Exception {
    super.stop();
    stopCorant();
  }

  protected void startCorant() {
    if (Corant.current() == null) {
      synchronized (Corant.class) {
        if (Corant.current() == null) {
          Corant.startup(getParameters().getRaw().toArray(String[]::new));
        }
      }
    }
    if (!Corant.current().isRunning()) {
      Corant.current().start(null);
    }
  }

  protected void stopCorant() {
    if (Corant.current() != null) {
      synchronized (Corant.class) {
        if (Corant.current() != null) {
          Corant.shutdown();
        }
      }
    }
  }
}
