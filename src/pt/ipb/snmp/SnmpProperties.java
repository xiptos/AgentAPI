/*
 * $Id: SnmpProperties.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.Properties;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class SnmpProperties extends Properties {
  public static final String VERSION = "snmp.version";

  public static final String WCOMMUNITY = "snmp.write_community";

  public static final String COMMUNITY = "snmp.community";

  public static final String USER = "snmp.user";

  public static final String PRIVPASS = "snmp.priv_pass";

  public static final String AUTHPASS = "snmp.auth_pass";

  public static final String PRIVPROTO = "snmp.priv_proto";

  public static final String AUTHPROTO = "snmp.auth_proto";

  public static final String PORT = "snmp.port";

  public static final String HOST = "snmp.host";

  public static final String RETRIES = "snmp.retries";

  public static final String TIMEOUT = "snmp.timeout";

  public static final String ENGINEID = "snmp.engineID";

  public static final String CONTEXT = "snmp.context";

  public static final int DEFAULT_PORT = 161;

  public static final int DEFAULT_RETRIES = 3;

  public static final int DEFAULT_TIMEOUT = 5;

  public static final String DEFAULT_HOST = "localhost";

  public SnmpProperties() {
    defaults = buildDefaults();
  }

  public SnmpProperties(Properties d) {
    defaults = buildDefaults();
    addProperties(d);
  }

  public SnmpProperties(SnmpURL url) {
    defaults = buildDefaults();
    putAll(url);
  }

  public void addProperties(Properties p) {
    putAll(p);
  }

  protected Properties buildDefaults() {
    Properties p = new Properties();
    p.setProperty(VERSION, SnmpConstants.S_SNMPv3);
    p.setProperty(WCOMMUNITY, "private");
    p.setProperty(COMMUNITY, "public");
    p.setProperty(USER, "");
    p.setProperty(PRIVPASS, "");
    p.setProperty(AUTHPASS, "");
    p.setProperty(PRIVPROTO, SnmpConstants.S_NOPRIV);
    p.setProperty(AUTHPROTO, SnmpConstants.S_NOAUTH);
    p.setProperty(PORT, Integer.toString(DEFAULT_PORT));
    p.setProperty(HOST, DEFAULT_HOST);
    p.setProperty(RETRIES, Integer.toString(DEFAULT_RETRIES));
    p.setProperty(TIMEOUT, Integer.toString(DEFAULT_TIMEOUT));
    return p;
  }

  public void putAll(SnmpURL url) {
    setVersion(SnmpConstants.version2int(url.getVersion()));
    if (url.getWriteCommunity() != null)
      setWriteCommunity(url.getWriteCommunity());
    if (url.getCommunity() != null)
      setCommunity(url.getCommunity());
    if (url.getUser() != null)
      setUser(url.getUser());
    if (url.getPrivPass() != null)
      setPrivPass(url.getPrivPass());
    if (url.getAuthPass() != null)
      setAuthPass(url.getAuthPass());
    setPrivProto(SnmpConstants.priv2int(url.getPrivProtocol()));
    setAuthProto(SnmpConstants.auth2int(url.getAuthProtocol()));
    setPort(url.getPort());
    if (url.getHost() != null)
      setHost(url.getHost());
    if (url.getContext() != null)
      setContext(url.getContext());
    setTimeout(url.getTimeout());
    setRetries(url.getRetries());
  }

  public int getVersion() {
    try {
      int version = SnmpConstants.SNMPv3;
      String s = getProperty(VERSION);
      if (s.equals(SnmpConstants.S_SNMPv1))
        return SnmpConstants.SNMPv1;
      else if (s.equals(SnmpConstants.S_SNMPv2c))
        return SnmpConstants.SNMPv2c;
      else if (s.equals(SnmpConstants.S_SNMPv3))
        return SnmpConstants.SNMPv3;
    } catch (Exception e) {
    }
    return -1;
  }

  public void setVersion(int i) {
    switch (i) {
    case SnmpConstants.SNMPv3:
      setProperty(VERSION, SnmpConstants.S_SNMPv3);
      break;
    case SnmpConstants.SNMPv1:
      setProperty(VERSION, SnmpConstants.S_SNMPv1);
      break;
    default:
      setProperty(VERSION, SnmpConstants.S_SNMPv2c);
      break;
    }
  }

  public void setContext(String context) {
    setProperty(CONTEXT, context);
  }

  public String getContext() {
    return getProperty(CONTEXT);
  }

  public void setPort(int i) {
    setProperty(PORT, Integer.toString(i));
  }

  public int getPort() {
    try {
      return Integer.parseInt(getProperty(PORT));
    } catch (Exception e) {
    }
    return DEFAULT_PORT;
  }

  public void setRetries(int i) {
    setProperty(RETRIES, Integer.toString(i));
  }

  public int getRetries() {
    try {
      return Integer.parseInt(getProperty(RETRIES));
    } catch (Exception e) {
    }
    return DEFAULT_RETRIES;
  }

  public void setTimeout(int i) {
    setProperty(TIMEOUT, Integer.toString(i));
  }

  public int getTimeout() {
    try {
      return Integer.parseInt(getProperty(TIMEOUT));
    } catch (Exception e) {
    }
    return DEFAULT_TIMEOUT;
  }

  public String getHost() {
    return getProperty(HOST);
  }

  public void setHost(String u) {
    setProperty(HOST, u);
  }

  public String getWriteCommunity() {
    return getProperty(WCOMMUNITY);
  }

  public void setWriteCommunity(String u) {
    setProperty(WCOMMUNITY, u);
  }

  public String getEngineID() {
    return getProperty(ENGINEID);
  }

  public void setEngineID(String u) {
    setProperty(ENGINEID, u);
  }

  public String getCommunity() {
    return getProperty(COMMUNITY);
  }

  public void setCommunity(String u) {
    setProperty(COMMUNITY, u);
  }

  public String getUser() {
    return getProperty(USER);
  }

  public void setUser(String u) {
    setProperty(USER, u);
  }

  public int getAuthProto() {
    String ap = getProperty(AUTHPROTO);
    if (ap.equals(SnmpConstants.S_MD5)) {
      return SnmpConstants.MD5;
    } else if (ap.equals(SnmpConstants.S_SHA)) {
      return SnmpConstants.SHA;
    }
    return SnmpConstants.NOAUTH;
  }

  public void setAuthProto(int i) {
    switch (i) {
    case SnmpConstants.MD5:
      setProperty(AUTHPROTO, SnmpConstants.S_MD5);
      break;
    case SnmpConstants.SHA:
      setProperty(AUTHPROTO, SnmpConstants.S_SHA);
      break;
    default:
      setProperty(AUTHPROTO, SnmpConstants.S_NOAUTH);
      break;
    }
  }

  public int getPrivProto() {
    String ap = getProperty(PRIVPROTO);
    if (ap.equals(SnmpConstants.S_DES)) {
      return SnmpConstants.DES;
    }
    return SnmpConstants.NOPRIV;
  }

  public void setPrivProto(int i) {
    switch (i) {
    case SnmpConstants.DES:
      setProperty(PRIVPROTO, SnmpConstants.S_DES);
      break;
    default:
      setProperty(PRIVPROTO, SnmpConstants.S_NOPRIV);
      break;
    }
  }

  public void setAuthPass(String ap) {
    setProperty(AUTHPASS, ap);
  }

  public String getAuthPass() {
    return getProperty(AUTHPASS);
  }

  public void setPrivPass(String pp) {
    setProperty(PRIVPASS, pp);
  }

  public String getPrivPass() {
    return getProperty(PRIVPASS);
  }

  public SnmpURL toSnmpURL() {
    SnmpURL snmpURL = new SnmpURL();
    snmpURL.setScheme(SnmpURL.SCHEME);
    snmpURL.setUser(getUser());
    snmpURL.setCommunity(getCommunity());
    snmpURL.setWriteCommunity(getWriteCommunity());
    snmpURL.setAuthProtocol(SnmpConstants.auth2string(getAuthProto()));
    snmpURL.setPrivProtocol(SnmpConstants.priv2string(getPrivProto()));
    snmpURL.setAuthPass(getAuthPass());
    snmpURL.setPrivPass(getPrivPass());
    snmpURL.setHost(getHost());
    snmpURL.setPort(getPort());
    snmpURL.setTimeout(getTimeout());
    snmpURL.setRetries(getRetries());
    return snmpURL;
  }
}

