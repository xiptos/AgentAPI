/*
 * $Id: EngineFactory.java 3 2004-08-03 10:42:11Z rlopes $
 * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 *
 */

package pt.ipb.agentapi.engine;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * An EngineFactory instance can be used to create Engine objects. The system
 * property that determines which Engine implementation to create is named
 * "agentapi.engine". This property names one or several concrete subclasses of
 * the Engine interface separated by commas ','. If the property is not defined,
 * a platform default is be used. The configuration file(s) for the engines are
 * explicited with the system property "agentapi.engine.config.file", also
 * separated by commas.
 */
public final class EngineFactory {
  public static final String ENGINE_PROP = "agentapi.engine";

  public static final String CONFIG_PROP = "agentapi.engine.config.file";

  public static final String DEFAULT_ENGINE = "pt.ipb.agentapi.engine.snmp.JoeSnmpEngine";

  /**
   * Private constructor.
   * 
   * <p>
   * This constructor prevents the class from being instantiated.
   * </p>
   */
  private EngineFactory() {
  }

  public static Engine createEngine() throws EngineException {
    String className = System.getProperty(ENGINE_PROP, DEFAULT_ENGINE);
    StringTokenizer strToken = new StringTokenizer(className, ",");
    return createEngine(strToken.nextToken());
  }

  public static Engine createEngine(String className) throws EngineException {
    try {
      return (Engine) (Class.forName(className).newInstance());
    } catch (ClassNotFoundException e1) {
      throw new EngineException("Engine class " + className + " not found", e1);
    } catch (IllegalAccessException e2) {
      throw new EngineException("Engine class " + className
          + " found but cannot be loaded", e2);
    } catch (InstantiationException e3) {
      throw new EngineException(
          "Engine class "
              + className
              + " loaded but cannot be instantiated (no empty public constructor?)",
          e3);
    } catch (ClassCastException e4) {
      throw new EngineException("Engine class " + className
          + " does not implement Engine", e4);
    }
  }

  public static Engine[] createEngines() throws EngineException {
    String classNames = System.getProperty(ENGINE_PROP, DEFAULT_ENGINE);
    return createEngines(classNames);
  }

  public static Engine[] createEngines(String classNames)
      throws EngineException {
    StringTokenizer strToken = new StringTokenizer(classNames, ",");
    Engine[] engines = new Engine[strToken.countTokens()];
    int i = 0;
    while (strToken.hasMoreTokens()) {
      String className = strToken.nextToken();
      engines[i++] = createEngine(className);
    }
    return engines;
  }

  public static Engine[] start(pt.ipb.agentapi.Agent agent)
      throws EngineException {
    Engine[] engines = createEngines();
    return start(agent, engines);
  }

  public static Engine[] start(pt.ipb.agentapi.Agent agent, Engine[] engines)
      throws EngineException {
    String propNames = System.getProperty(CONFIG_PROP, "");
    Properties props = new Properties();
    StringTokenizer strToken = new StringTokenizer(propNames, ",");
    while (strToken.hasMoreTokens()) {
      String fName = strToken.nextToken();
      try {
        props.load(new java.io.FileInputStream(fName));
      } catch (java.io.FileNotFoundException ex) {
        throw new EngineException(ex);
      } catch (java.io.IOException ex) {
        throw new EngineException(ex);
      }
    }
    return start(agent, engines, props);
  }

  public static Engine[] start(pt.ipb.agentapi.Agent agent, Engine[] engines,
      Properties props) throws EngineException {
    for (int i = 0; i < engines.length; i++) {
      Engine engine = engines[i];

      engine.setProperties(props);
      engine.addAgent(agent);
      agent.addMessageListener(engine.createAgentListener());
      try {
        engine.open();
      } catch (Exception e) {
        //e.printStackTrace();
        throw new EngineException(e);
      }
    }
    return engines;
  }

  public static void stop(Engine[] engines) {
    for (int i = 0; i < engines.length; i++) {
      Engine engine = engines[i];
      engine.notify();
      engine.close();
    }
  }
}