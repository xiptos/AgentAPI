/*
 * $Id: Str.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.snmp.type.tc;

import pt.ipb.snmp.type.smi.OctetString;
import pt.ipb.snmp.type.smi.Var;

/**
 * The SNMP DisplayString textual convention
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class Str extends TC {
  String str = null;

  public Str(String s) {
    str = s;
  }

  public Str(OctetString o) {
    str = new String((byte[]) o.toJavaValue());
  }

  /**
   * Returns this Str as an OctetString
   */
  public Var toVar() {
    return new OctetString(str.getBytes());
  }

  public static String toString(OctetString o) {
    return new Str(o).toString();
  }

  public String toString() {
    return str;
  }

}