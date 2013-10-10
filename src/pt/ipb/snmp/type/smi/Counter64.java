/*
 * $Id: Counter64.java 3 2004-08-03 10:42:11Z rlopes $
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
 * The SNMP Counter64 data type.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class Counter64 extends Counter {
  private final BigInteger one = new BigInteger("1");

  boolean msb = false;

  public Counter64(String s, int radix) {
    super(0);
    setNumber(s, radix);
    type = Var.COUNTER64;
  }

  public Counter64(String s) {
    super(0);
    setNumber(s);
    type = Var.COUNTER64;
  }

  public Counter64(long i) {
    super(i);
    type = Var.COUNTER64;
  }

  public void setNumber(String s) {
    setNumber(s, 10);
  }

  public void setNumber(String s, int radix) {
    BigInteger maxValue = one.shiftLeft(64);
    BigInteger b = new BigInteger(s, radix);

    if (b.compareTo(maxValue) == 1) { // it uses more than 64 bits
      b = b.mod(maxValue);
    }

    if (b.compareTo(one.shiftLeft(63)) == 1) { // it uses all the 64 bits
      b = b.subtract(one.shiftLeft(64));
      msb = true;
    }
    i = b.longValue();
    type = Var.COUNTER64;
  }

  public void inc() {
    BigInteger bi = (BigInteger) toJavaValue();
    setNumber(bi.add(new BigInteger("1")).toString());
  }

  /**
   * Returns this Counter64 as a BigInteger
   */
  public Object toJavaValue() {
    BigInteger r = new BigInteger(new String("" + i));
    if (msb)
      r = r.add(one.shiftLeft(64));
    return r;
  }

  public String toString() {
    return toJavaValue().toString();
  }

  public static void main(String arg[]) {
    System.out.println(new Counter64(arg[0]));
  }

}