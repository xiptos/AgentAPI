/*
 * $Id: EngineServlet.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
 * *
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import pt.ipb.agentapi.Agent;

public class EngineServlet extends HttpServlet {
  Authenticator auth = null;

  Properties prop = null;

  Agent agent = null;
  
  // To register listening Agents
  public void setAgent(Agent l) {
    this.agent = l;
  }

  public void setProperties(Properties props) {
    this.prop = props;
  }

  /**
   * Called to initialize servlet properties.
   */
  public void init() throws ServletException {
    ServletConfig servletConfig = getServletConfig();
    prop = new Properties();
    for (Enumeration e = servletConfig.getInitParameterNames(); e
        .hasMoreElements();) {
      String name = (String) e.nextElement();
      String val = servletConfig.getInitParameter(name);
      prop.setProperty(name, val);
    }
    auth = new Authenticator(prop);
  }

  /**
   * Called when the servlet is to be removed.
   */
  public void destroy() {
  }

  /**
   * Handle the HTTP GET method.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    String userid = (String) session.getAttribute("userid");
    String passwd = null;
    if (userid != null)
      passwd = (String) session.getAttribute(userid);

    String title = "HTTP Engine for Agent API";

    // set content type and other response header fields first
    String contentType = prop.getProperty("content.type", "html");
    String wmlParameter = request.getParameter("wml");
    if (wmlParameter != null && wmlParameter.equals("true")) {
      contentType = "wml";
    }
    /*
     * if(contentType.equals("wml")) {
     * response.setContentType("text/vnd.wap.wml"); } else {
     * response.setContentType("text/html"); }
     */

    PrintWriter out = response.getWriter();

    // User must be authenticated
    if (auth.authenticate(userid, passwd)) { // OK. Proceed
      try {
        MibToXml mibToXml = (MibToXml) session.getAttribute("MibToXml");
        if (mibToXml == null) {
          mibToXml = new MibToXml(prop, agent);
          session.setAttribute("MibToXml", mibToXml);
        }
        StringBuffer str = new StringBuffer();
        String url = response.encodeURL(request.getRequestURI());
        str.append(startXml(url));
        str.append(xmlBanner(userid));
        str.append("\n");

        String page = request.getParameter("page");
        String up = request.getParameter("up");
        String oid = request.getParameter("oid");
        if (oid != null && oid.indexOf(',') != -1) {
          oid = oid.substring(0, oid.indexOf(','));
        }
        if (up != null && up.equals("true")) {
          str.append(mibToXml.getPage(page, true));
        } else if (oid != null) {
          str.append(mibToXml.getPage(page, false, oid));
        } else {
          str.append(mibToXml.getPage(page));
        }
        str.append(endXml());
        if (contentType.equals("wml")) {
          out.println(transform(response, "main_wml.xsl", str.toString()));
        } else {
          out.println(transform(response, "main.xsl", str.toString()));
        }

        String fich = prop.getProperty("smi.create.xml");
        if (fich != null) {
          java.io.PrintWriter writer = new java.io.PrintWriter(
              new java.io.FileWriter(fich));
          writer.println(str.toString());
          writer.flush();
        }

      } catch (Exception e) {
        e.printStackTrace();
        out.println(e.getMessage());
      }

    } else {
      //out.println("Incorrect password..." + userid + ":" + passwd);
      // Show login form
      if (contentType.equals("wml")) {
        out.println(transform(response, "login_wml.xsl", loginForm()));
      } else {
        out.println(transform(response, "login.xsl", loginForm()));
      }
    }

    out.close();
  }

  String loginForm() {
    StringBuffer str = new StringBuffer();
    str.append("<?xml version=\"1.0\"?>");
    str.append("<login>LOGIN</login>");
    return str.toString();
  }

  String startXml(String url) {
    return ("<?xml version=\"1.0\"?><doc url=\"" + url + "\">\n");
  }

  String endXml() {
    return ("</doc>");
  }

  String xmlBanner(String userid) {
    StringBuffer str = new StringBuffer();
    str.append("<banner user=\"" + userid + "\"/>");
    return str.toString();
  }

  public String getContentType(Templates templates) {
    Properties oprops = templates.getOutputProperties();
    String method = oprops.getProperty(OutputKeys.METHOD);
    if (method == null)
      method = "xml"; // the default.
    /*
     * Properties defoprops = null; if (method.equals("html")) defoprops =
     * OutputProperties.getDefaultMethodProperties("html"); else if
     * (method.equals("text")) defoprops =
     * OutputProperties.getDefaultMethodProperties("text"); else if
     * (method.equals("wml")) defoprops =
     * OutputProperties.getDefaultMethodProperties("wml"); else defoprops =
     * OutputProperties.getDefaultMethodProperties("xml");
     */

    String encoding = oprops.getProperty(OutputKeys.ENCODING);
    /*
     * if(encoding==null) encoding = defoprops.getProperty(OutputKeys.ENCODING);
     */
    String media = oprops.getProperty(OutputKeys.MEDIA_TYPE);
    /*
     * if(media==null) media = defoprops.getProperty(OutputKeys.MEDIA_TYPE);
     */
    return media + "; charset=" + encoding;
  }

  String transform(HttpServletResponse response, Properties parameters,
      String xslFile, String xmlStr) {
    try {
      //String path =
      // getServletConfig().getServletContext().getRealPath(xslFile);
      String path = new String(prop.getProperty("xsl.dir", ".")
          + java.io.File.separator + xslFile);
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer(new StreamSource(path));
      Templates templates = tFactory.newTemplates(new StreamSource(path));
      String contentType = getContentType(templates);
      if (contentType != null)
        response.setContentType(contentType);

      if (parameters != null) {
        for (Enumeration e = parameters.propertyNames(); e.hasMoreElements();) {
          String n = (String) e.nextElement();
          transformer.setParameter(n, parameters.getProperty(n));
        }
      }
      StringWriter writer = new StringWriter();
      transformer.transform(new StreamSource(new StringReader(xmlStr)),
          new StreamResult(writer));

      return writer.toString();

    } catch (TransformerConfigurationException e) {
      StackTraceElement[] t = e.getStackTrace();
      StringBuffer str = new StringBuffer();
      str.append(e.getMessage() + "<br>\n<br>\n");
      for (int i = 0; i < t.length; i++) {
        str.append(t[i].toString());
        str.append("<br>\n");
      }
      return str.toString();
    } catch (TransformerException e) {
      StackTraceElement[] t = e.getStackTrace();
      StringBuffer str = new StringBuffer();
      str.append(e.getMessage() + "<br>\n<br>\n");
      for (int i = 0; i < t.length; i++) {
        str.append(t[i].toString());
        str.append("<br>\n");
      }
      return str.toString();
    }
  }

  String transform(HttpServletResponse response, String xslFile, String xmlStr) {
    return transform(response, null, xslFile, xmlStr);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String action = request.getParameter("action");
    HttpSession session = request.getSession();
    if (action != null) {
      if (action.equals("login")) {
        String userid = request.getParameter("userid");
        String passwd = request.getParameter("passwd");
        if (userid != null && passwd != null) {
          session.setAttribute("userid", userid);
          session.setAttribute(userid, passwd);
        }
      } else if (action.equals("logout")) {
        String userid = request.getParameter("userid");
        session.removeAttribute("userid");
        if (userid != null) {
          session.removeAttribute(userid);
        }
        if (request.getQueryString() != null) {
          response.sendRedirect(request.getContextPath()
              + request.getServletPath());
          return;
        }
      } else if (action.equals("snmpop")) {
        MibToXml mibToXml = (MibToXml) session.getAttribute("MibToXml");
        try {
          if (mibToXml == null) {
            mibToXml = new MibToXml(prop, agent);
            session.setAttribute("MibToXml", mibToXml);
          }
          String oid = request.getParameter("oid");
          if (oid != null && oid.indexOf(',') != -1) {
            oid = oid.substring(0, oid.indexOf(','));
          }
          String value = request.getParameter("value");
          mibToXml.set(oid, value);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    doGet(request, response);
  }

  // Debug section
  void debugSession(HttpServletRequest request, PrintWriter out) {
    HttpSession session = request.getSession();
    out.println("Requested Session Id: " + request.getRequestedSessionId());
    out.println("Current Session Id: " + session.getId());
    out.println("Session Created Time: " + session.getCreationTime());
    out.println("Session Last Accessed Time: " + session.getLastAccessedTime());
    out.println("Session Max Inactive Interval Seconds: "
        + session.getMaxInactiveInterval());
    out.println();
    out.println("Session values: ");
    Enumeration names = session.getAttributeNames();
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      out.println("   " + name + " = " + session.getAttribute(name));
    }
    out.flush();
  }
}