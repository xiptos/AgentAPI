/*
 * $Id: SnmpURL.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.snmp;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Class to represent SNMP URLs.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class SnmpURL {
  public static final String SCHEME = "snmp";

  public static final String TIMEOUT = "timeout";

  public static final String RETRIES = "retries";

  public static final String OP = "op";

  public static final String VALUE = "value";

  public static final String AUTH = "auth";

  public static final String PRIV = "priv";

  public static final String PASS = "pass";

  public static final String CONTEXT = "context";

  public static final int DEF_PORT = 161;

  public static final int DEF_TIMEOUT = 5;

  public static final int DEF_RETRIES = 3;

  String scheme;

  String community;

  String wcommunity;

  String user;

  String authProtocol;

  String privProtocol;

  String authPass;

  String privPass;

  String host;

  int port;

  String oid;

  String instance;

  String op;

  String value;

  String version;

  String context;

  int retries;

  int timeout;

  boolean isAbsolute;

  public SnmpURL() {
    scheme = null;
    user = null;
    host = null;
    port = -1;
    oid = null;
  }

  public SnmpURL(String scheme, String host, int port) {
    this(scheme, null, host, port, null);
  }

  public SnmpURL(String scheme, String user, String host, int port, String oid) {
    this.scheme = scheme;
    this.user = user;
    this.host = host;
    this.port = port;
    this.oid = oid;
  }

  public SnmpURL(SnmpURL url, String str) throws MalformedURLException {
    SnmpURL l = new SnmpURL(str);
    if (l.isAbsolute()) {
      this.scheme = l.getScheme();

      this.community = l.getCommunity();
      ;
      this.wcommunity = l.getWriteCommunity();
      this.user = l.getUser();
      this.authProtocol = l.getAuthProtocol();
      this.privProtocol = l.getPrivProtocol();
      this.authPass = l.getAuthPass();
      this.privPass = l.getPrivPass();

      this.host = l.getHost();
      this.port = l.getPort();

    } else {
      this.scheme = url.getScheme();
      this.community = url.getCommunity();
      ;
      this.wcommunity = url.getWriteCommunity();
      this.user = url.getUser();
      this.authProtocol = url.getAuthProtocol();
      this.privProtocol = url.getPrivProtocol();
      this.authPass = url.getAuthPass();
      this.privPass = url.getPrivPass();

      this.host = url.getHost();
      this.port = url.getPort();
    }

    this.context = l.getContext();
    this.oid = l.getOID();
    this.instance = l.getInstance();

    this.op = l.getOperation();
    this.value = l.getValue();

    this.version = l.getVersion();
    this.context = l.getContext();

    this.retries = l.getRetries();
    this.timeout = l.getTimeout();
  }

  public SnmpURL(String str) throws MalformedURLException {
    try {
      parse(new URI(str));
    } catch (Exception e) {
      e.printStackTrace();
      throw new MalformedURLException(e.getMessage());
    }
  }

  /**
   * Builds an SnmpURL object. An SNMP URL may be of the form: snmp:// <user>@
   * <host>: <port>/ <oid>
   */
  public SnmpURL(URI uri) throws MalformedURLException {
    parse(uri);
  }

  void parse(URI uri) throws MalformedURLException {
    parseFragment(uri);
    parseQuery(uri);
    parseSecurity(uri);
    parseScheme(uri);
    parsePath(uri);
    parseHost(uri);
    parsePort(uri);
  }

  Properties parseProps(String mainStr, String sep)
      throws MalformedURLException {
    Properties props = new Properties();
    StringTokenizer strToken = new StringTokenizer(mainStr, sep);
    while (strToken.hasMoreTokens()) {
      String str = strToken.nextToken();
      StringTokenizer strToken2 = new StringTokenizer(str, "=");
      try {
        String key = strToken2.nextToken();
        String val = strToken2.nextToken();
        props.setProperty(key.toLowerCase(), val);
      } catch (NoSuchElementException e) {
        throw new MalformedURLException("Invalid property: " + str);
      }
    }
    return props;
  }

  void parseFragment(URI uri) throws MalformedURLException {
    if (uri.getFragment() != null) {
      Properties props = parseProps(uri.getFragment(), "&");

      if (props.getProperty(TIMEOUT) != null) {
        try {
          setTimeout(Integer.parseInt(props.getProperty(TIMEOUT)));
        } catch (NumberFormatException e) {
          throw new MalformedURLException("Invalid timeout: "
              + props.getProperty(TIMEOUT));
        }
      } else {
        timeout = DEF_TIMEOUT;
      }

      if (props.getProperty(RETRIES) != null) {
        try {
          setRetries(Integer.parseInt(props.getProperty(RETRIES)));
        } catch (NumberFormatException e) {
          throw new MalformedURLException("Invalid retries: "
              + props.getProperty(RETRIES));
        }
      } else {
        retries = DEF_RETRIES;
      }

    }
  }

  void parseQuery(URI uri) throws MalformedURLException {
    if (uri.getQuery() != null) {
      String query = uri.getQuery();
      // Operation part
      int i = 0;
      int l = query.indexOf('?');
      if (l == -1) {
        // we do not have the '?' or the version information
        l = query.length();
      }
      String opPart = query.substring(i, l);
      Properties props = parseProps(opPart, "&");
      if (props.getProperty(OP) != null) {
        setOperation(props.getProperty(OP));
      } else if (props.getProperty(VALUE) != null) {
        setValue(props.getProperty(VALUE));
      }

      // Version part
      i = query.indexOf('?');
      if (i == -1) {
        // we have no version part
        return;
      }
      i++; // Disregard the '?'
      l = query.indexOf('?', i);
      if (l == -1) {
        // we have no context information
        l = query.length();
      }
      setVersion(query.substring(i, l));

      // Context part
      i = query.indexOf('?', i);
      if (i == -1) {
        // we have no context part
        return;
      }
      i++;
      l = query.length();
      setContext(query.substring(i, l));
    }
  }

  void parseSecurity(URI uri) throws MalformedURLException {
    if (uri.getUserInfo() != null) {
      StringTokenizer strToken = new StringTokenizer(uri.getUserInfo(), ":");
      // User/Community part
      if (strToken.hasMoreTokens()) {
        setUser(strToken.nextToken());
        setCommunity(getUser());
      }
      // Auth part
      if (strToken.hasMoreTokens()) {
        String auth = strToken.nextToken();
        Properties props = parseProps(auth, ",");
        if (props.getProperty(AUTH) != null) {
          setAuthProtocol(props.getProperty(AUTH));
        }
        if (props.getProperty(PASS) != null) {
          setAuthPass(props.getProperty(PASS));
        }
      }
      // Priv part
      if (strToken.hasMoreTokens()) {
        String priv = strToken.nextToken();
        Properties props = parseProps(priv, ",");
        if (props.getProperty(PRIV) != null) {
          setPrivProtocol(props.getProperty(PRIV));
        }
        if (props.getProperty(PASS) != null) {
          setPrivPass(props.getProperty(PASS));
        }
      }
    }
  }

  void parseScheme(URI uri) throws MalformedURLException {
    if (uri.isAbsolute()) {
      if (uri.getScheme() == null) {
        setScheme(SCHEME);
        setAbsolute(true);

      } else if (!SCHEME.equals(uri.getScheme())) {
        throw new MalformedURLException("Invalid scheme: " + uri.getScheme());
      } else {
        setScheme(uri.getScheme());
      }
    } else {
      setAbsolute(false);
    }
  }

  void parsePath(URI uri) throws MalformedURLException {
    if (uri.getPath() != null) {
      StringTokenizer pathToken = new StringTokenizer(uri.getPath(), "/");
      String path1 = null;
      String path2 = null;
      if (pathToken.hasMoreTokens()) {
        setOID(pathToken.nextToken());
      }
      if (pathToken.hasMoreTokens()) {
        setInstance(pathToken.nextToken());
      }
    }
  }

  void parseHost(URI uri) {
    setHost(uri.getHost());
  }

  void parsePort(URI uri) throws MalformedURLException {
    if (uri.getPort() == -1) {
      setPort(DEF_PORT);
    } else {
      setPort(uri.getPort());
      if (getHost() == null) {
        setHost("localhost");
      }
    }
  }

  public boolean equals(Object o) {
    if (!(o instanceof SnmpURL)) {
      return false;
    }
    SnmpURL u = (SnmpURL) o;
    if (isEqual(scheme, u.getScheme()) && isEqual(community, u.getCommunity())
        && isEqual(wcommunity, u.getWriteCommunity())
        && isEqual(user, u.getUser())
        && isEqual(authProtocol, u.getAuthProtocol())
        && isEqual(privProtocol, u.getPrivProtocol())
        && isEqual(authPass, u.getAuthPass())
        && isEqual(privPass, u.getPrivPass()) && isEqual(host, u.getHost())
        && port == u.getPort() && isEqual(oid, u.getOID())
        && isEqual(instance, u.getInstance()) && isEqual(op, u.getOperation())
        && isEqual(value, u.getValue()) && isEqual(version, u.getVersion())
        && isEqual(context, u.getContext()) && retries == u.getRetries()
        && timeout == u.getTimeout()) {
      return true;
    }
    return false;
  }

  boolean isEqual(String s1, String s2) {
    if (s1 == null && s2 == null)
      return true;
    if (s1 != null)
      return s1.equals(s2);
    return false;
  }

  public String getScheme() {
    return scheme;
  }

  public String getCommunity() {
    return community;
  }

  public String getWriteCommunity() {
    return wcommunity;
  }

  public String getUser() {
    return user;
  }

  public String getAuthProtocol() {
    return authProtocol;
  }

  public String getPrivProtocol() {
    return privProtocol;
  }

  public String getAuthPass() {
    return authPass;
  }

  public String getPrivPass() {
    return privPass;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getOID() {
    return oid;
  }

  public String getInstance() {
    return instance;
  }

  public String getOperation() {
    return op;
  }

  public String getValue() {
    return value;
  }

  public String getVersion() {
    return version;
  }

  public String getContext() {
    return context;
  }

  public int getRetries() {
    return retries;
  }

  public int getTimeout() {
    return timeout;
  }

  public boolean isAbsolute() {
    return isAbsolute;
  }

  public void setScheme(String s) {
    this.scheme = s;
  }

  public void setCommunity(String s) {
    this.community = s;
  }

  public void setWriteCommunity(String s) {
    this.wcommunity = s;
  }

  public void setUser(String s) {
    this.user = s;
  }

  public void setAuthProtocol(String s) {
    this.authProtocol = s;
  }

  public void setPrivProtocol(String s) {
    this.privProtocol = s;
  }

  public void setAuthPass(String s) {
    this.authPass = s;
  }

  public void setPrivPass(String s) {
    this.privPass = s;
  }

  public void setHost(String s) {
    this.host = s;
  }

  public void setPort(int p) {
    this.port = p;
  }

  public void setOID(String s) {
    this.oid = s;
  }

  public void setInstance(String s) {
    this.instance = s;
  }

  public void setOperation(String s) {
    this.op = s;
  }

  public void setValue(String s) {
    this.value = s;
  }

  public void setVersion(String s) {
    this.version = s;
  }

  public void setContext(String s) {
    this.context = s;
  }

  public void setRetries(int p) {
    this.retries = p;
  }

  public void setTimeout(int p) {
    this.timeout = p;
  }

  public void setAbsolute(boolean b) {
    this.isAbsolute = b;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    if (scheme != null) {
      str.append(scheme);
      str.append(":");
    }
    if (user != null || host != null) {
      str.append("//");
    }
    if (user != null) {
      str.append(user);
      str.append("@");
    }
    if (host != null) {
      str.append(host);
    }
    if (port != -1) {
      str.append(":");
      str.append(port);
    }
    if (oid != null) {
      str.append("/");
      str.append(oid);
    }
    if (instance != null) {
      str.append("/");
      str.append(instance);
    }
    if (op != null) {
      str.append("?");
      str.append(OP + "=" + op);
    }
    if (value != null) {
      str.append("&");
      str.append(VALUE + "=" + value);
    }
    if (version != null) {
      if (op == null)
        str.append("?");
      str.append("?");
      str.append(version);
    }
    if (context != null) {
      if (version == null) {
        str.append("?");
        if (op == null)
          str.append("?");
      }

      str.append("?");
      str.append(context);
    }
    str.append("#");
    str.append(TIMEOUT + "=" + timeout);
    str.append("&");
    str.append(RETRIES + "=" + retries);
    return str.toString();
  }

  public static void main(String arg[]) {
    try {
      System.out.println(new SnmpURL(arg[0]));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

