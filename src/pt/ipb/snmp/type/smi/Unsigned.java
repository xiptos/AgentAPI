/*
 * $Id: Unsigned.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.math.BigInteger;

/**
 * The SNMP Unsigned32 data type.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class Unsigned extends Number {
  public static final long MAX_VALUE = new BigInteger("1").shiftLeft(32)
      .longValue() - 1;

  long i;

  public Unsigned(String s, int radix) {
    this(Long.valueOf(s, radix).longValue());
    type = Var.UNSIGNED32;
  }

  public Unsigned(String s) {
    this(new Long(s).longValue());
    type = Var.UNSIGNED32;
  }

  public Unsigned(long i) {
    if (i > MAX_VALUE)
      this.i = i % (MAX_VALUE + 1);
    else
      this.i = i;
    type = Var.UNSIGNED32;
  }

  /**
   * Returns this Unsigned as a java.lang.Long
   */
  public Object toJavaValue() {
    return new Long(i);
  }

  public String toString() {
    return Long.toString(i);
  }

  public static void main(String arg[]) {
    System.out.println(new Unsigned(arg[0]));
  }

}