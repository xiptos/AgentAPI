/*
 * $Id: HTTPEngine.java 19 2004-08-11 13:17:11Z rlopes $
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

package pt.ipb.agentapi.engine.http;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.NCSARequestLog;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.util.MultiException;

import pt.ipb.agentapi.Agent;
import pt.ipb.agentapi.engine.Engine;
import pt.ipb.agentapi.event.MessageListener;

/**
 * This class is a wrapper to Jetty web server. Simply instanciates Server and
 * creates a Servlet.
 */
public class HTTPEngine implements Engine {
  Properties prop = null;

  HttpServer server = null;

  Agent agent = null;

  /**
   * This class provides the HTTP engine for AgentAPI. Some Properties:
   * 
   * <pre>
   * 
   *  
   *   
   *    
   *     mib.dir=/tmp/mibs/ 
   *     mib.files=DISMAN-SCHEDULE-MIB              
   *     mib.debug=false  
   *     mib.compiled=true 
   *     mib.root.node=internet 
   *     
   *     http.servlet.class=pt.ipb.agentapi.engine.http.SchedAgent
   *     
   *     http.server.port=8080
   *     http.log.dir=logs
   *     
   *    
   *   
   *  
   * </pre>
   */
  public HTTPEngine() {
  }

  public void setProperties(Properties prop) {
    this.prop = prop;
  }

  public void open() throws IOException, ClassNotFoundException,
      InstantiationException, IllegalAccessException, MultiException {
    if (server != null)
      return;

    int port = 8080;
    try {
      port = Integer.parseInt(prop.getProperty("http.server.port"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    String className = prop.getProperty("http.servlet.class", "pt.ipb.agentapi.engine.http.EngineServlet");
    String mountPoint = prop.getProperty("http.servlet.mount", "/eng");
    String contextURI = prop.getProperty("http.servlet.context", "/");
    String resourceBase = prop.getProperty("http.servlet.resourceBase","web");
    
    System.out.println("Starting HTTPEngine on http://localhost:"+port+contextURI+mountPoint);
    
    // Make server
    server= new HttpServer();
    System.out.println("  Adding Listener to server at port "+ port);
    server.addListener(":"+ port);
    System.out.println("  Getting "+contextURI+" context from server");
    HttpContext context= server.getContext(contextURI);
    
    ServletHandler handler= new ServletHandler();
    System.out.println("  Adding Servlet to handler at http://localhost:"+ port+contextURI+mountPoint);
    ServletHolder holder = handler.addServlet(mountPoint, className);
    System.out.println("  Adding Handler to "+contextURI);
    context.addHandler(handler);

    System.out.println("  Adding Resource to "+contextURI);
    context.setResourceBase(resourceBase);
    context.addHandler(new ResourceHandler());

    // Logger
    String logFile = prop.getProperty("http.log.file");
    if (logFile != null) {
      NCSARequestLog log = new NCSARequestLog();
      log.setFilename(logFile);
      log.setRetainDays(90);
      log.setAppend(true);
      //log.setMultiDay(false);
      server.setRequestLog(log);
    }

    holder.putAll(prop);

/*    for (Enumeration en = prop.propertyNames(); en.hasMoreElements();) {
      String key = (String) en.nextElement();
      holder.setInitParameter(key, prop.getProperty(key));
    }
*/    
    // Start handlers and listener
    server.start();

    try {
      EngineServlet s = (EngineServlet) holder.getServlet();
      s.setProperties(prop);
      s.setAgent(agent);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Done!");
  }

  public void close() {
    System.out.println("Closing...");
    try {
      if (server != null)
        server.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
    server = null;
    System.out.println("Done!");
  }

  public void addAgent(Agent a) {
    this.agent = a;
  }

  public void removeAgent(Agent a) {
  }

  public MessageListener createAgentListener() {
    return null;
  }

  protected void finalize() throws Throwable {
    //close();
    super.finalize();
  }

  public static void main(String arg[]) {
    try {
      Properties prop = new Properties();
      prop.load(new FileInputStream(arg[0]));
      HTTPEngine e = new HTTPEngine();
      e.setProperties(prop);
      e.open();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}