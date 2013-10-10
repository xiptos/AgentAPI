/*
 * $Id: Authenticator.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Authenticates users for the HTTP engine.
 */
public class Authenticator {
  Properties prop = null;

  /**
   * the Argument are space separated MIB file names.
   */
  public Authenticator(Properties p) {
    this.prop = new Properties(p);

    String users = prop.getProperty("login.users");
    StringTokenizer strToken = new StringTokenizer(users);
    while (strToken.hasMoreTokens()) {
      String user = strToken.nextToken();
      if (user != null) {
        user.trim();
        String passwd = prop.getProperty(user + ".passwd");
        if (passwd != null) {
          passwd.trim();
          prop.setProperty("auth." + user, passwd);
        }
      }
    }
  }

  /**
   * Authenticates users.
   */
  public boolean authenticate(String userid, String passwd) {
    String myPass = prop.getProperty("auth." + userid);
    if (myPass == null)
      return false;
    if (myPass.equals(passwd))
      return true;
    return false;
  }

}