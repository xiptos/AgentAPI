/*
 * $Id: StringOID.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.snmp.type.smi;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Represents an OID. Can be of the form .1.2.3."one".3."two".
 */
public class StringOID extends OID {
  String originalOid = null;

  Vector keys = null;

  /**
   * Represents an OID. OID is like .1.2.3 and key is like "one.two". This OID
   * will be of the form .1.2.3."one".3."two"
   */
  public StringOID(String oid) {
    super(oid);
    this.originalOid = oid;
  }

  public StringOID() {
    super();
    this.originalOid = "";
  }

  public String getKey() {
    if (keys != null) {
      StringBuffer str = new StringBuffer();
      for (Enumeration e = keys.elements(); e.hasMoreElements();) {
        String key = (String) e.nextElement();
        str.append(key);
        if (e.hasMoreElements()) {
          str.append(".");
        }
      }
      return str.toString();
    }
    return null;
  }

  public void appendKey(String key) {
    if (keys == null) {
      keys = new Vector();
    }
    keys.addElement(key);
    append(str2oid(key));
  }

  String str2oid(String key) {
    StringBuffer buffer = new StringBuffer();

    if (key != null) {
      buffer.append(".");

      buffer.append(key.length());
      for (int i = 0; i < key.length(); i++) {
        buffer.append(".");
        buffer.append((int) key.charAt(i));
      }
    }
    return buffer.toString();
  }

  /**
   * Get the OID without the key part, or .1.2.3 in the above example.
   */
  public String getWithoutKey() {
    return originalOid;
  }

  /**
   * This is used to parse an OID composed by subOID.a.b.c.x."---".y."----" the
   * number of numbers between the subOID and the key is represented by num.
   */
  public static StringOID createStringOID(String subOID, int num, String oid)
      throws ParseException {
    return createStringOID(subOID, num, oid, -1);
  }

  public static StringOID createStringOID(String subOID, int num, String oid,
      int words) throws ParseException {
    int wc = 0;

    if (subOID.length() > oid.length())
      throw new ParseException("Invalid sub oid", 0);

    if (oid.length() == 0)
      throw new ParseException("Invalid key", 0);

    if (!oid.startsWith(subOID))
      throw new ParseException("Invalid sub oid", 0);

    String instance = oid.substring(subOID.length());
    StringTokenizer strToken = new StringTokenizer(instance, ".");
    if (strToken.countTokens() < num)
      throw new ParseException("Invalid number", 0);

    StringBuffer oidStr = new StringBuffer(subOID);
    for (int i = 0; i < num; i++) {
      oidStr.append(".");
      oidStr.append(strToken.nextToken());
    }
    StringOID strOID = new StringOID(oidStr.toString());

    while (strToken.hasMoreTokens()) {
      if (words >= 0 && wc > words)
        throw new ParseException("Wrong number of words", 0);
      int len = 0;
      try {
        len = Integer.parseInt(strToken.nextToken());
      } catch (NumberFormatException e) {
        throw new ParseException("Number format exception", 0);
      }

      StringBuffer str = new StringBuffer();
      for (int i = 0; i < len; i++) {
        try {
          str.append((char) Integer.parseInt(strToken.nextToken()));
        } catch (NoSuchElementException e2) {
          throw new ParseException(new String("Error parsing " + oid), i);
        } catch (NumberFormatException nfe2) {
          throw new ParseException(new String("Error parsing " + oid), i);
        }
      }

      strOID.appendKey(str.toString());
      wc++;
    }
    if (wc != words)
      throw new ParseException(new String("Wrong number of words in key"),
          words);

    return strOID;
  }

}