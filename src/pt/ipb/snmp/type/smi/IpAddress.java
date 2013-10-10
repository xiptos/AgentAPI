/*
 * $Id: IpAddress.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.StringTokenizer;

/**
 * The SNMP IpAddress data type.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class IpAddress extends OctetString {

  /**
   * Format: "#a0.#0d.#1f.#01" or "192.168.0.10"
   */
  public IpAddress(String s) throws NumberFormatException {
    type = Var.IPADDRESS;
    StringTokenizer strToken = new StringTokenizer(s, ".");
    if (strToken.countTokens() != 4)
      throw new NumberFormatException();

    setByteArray(parseString(s));
  }

  public IpAddress(byte ip[]) throws NumberFormatException {
    if (ip.length != 4)
      throw new NumberFormatException();
    setByteArray(ip);
  }

  public IpAddress(OctetString o) {
    this((byte[]) o.toJavaValue());
  }

  public static void main(String arg[]) {
    //System.out.println(new IpAddress(arg[0]));
    System.out.println(new IpAddress(arg[0]));
  }

}