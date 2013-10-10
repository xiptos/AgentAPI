/*
 * $Id: Bits.java 3 2004-08-03 10:42:11Z rlopes $
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
 * The Bits class. Although it is a pseudo-type, it is used as a textual
 * convention. It is based on an OctetString.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class Bits extends TC {
  boolean bits[];

  /**
   * Creates a Bit array with n bits all '0'.
   */
  public Bits(int n) {
    bits = new boolean[n];
    for (int i = 0; i < n; i++) {
      bits[i] = false;
    }
  }

  /**
   * Creates a Bits based on a byte array.
   */
  public Bits(byte[] c) {
    bits = new boolean[c.length * 8];
    for (int i = 0; i < c.length; i++) {
      byte b = c[i];
      for (int t = 0; t < 8; t++) {
        if ((b & 1) == 1) {
          bits[i * 8 + 7 - t] = true;
        } else {
          bits[i * 8 + 7 - t] = false;
        }
        b >>= 1;
      }
    }
  }

  /**
   * Creates a Bits object based on an ObjectString
   */
  public Bits(OctetString o) {
    this((byte[]) o.toJavaValue());
  }

  /**
   * The format, for simplicity, is a sequence of 0/1 characters. For example:
   * "0011001011001010100"
   */
  public Bits(String s) {
    bits = new boolean[s.length()];
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '1') {
        bits[i] = true;
      } else {
        bits[i] = false;
      }
    }
  }

  /**
   * Resets the bit at position i.
   */
  public void reset(int i) {
    bits[i] = false;
  }

  /**
   * Sets the bit at position i.
   */
  public void set(int i) {
    bits[i] = true;
  }

  /**
   * Checks if bit at position i is set.
   */
  public boolean isSet(int i) {
    return bits[i];
  }

  /**
   * Returns the Bits object as an OctetString.
   */
  public Var toVar() {
    int size = bits.length;
    int octets = size / 8;
    int rest = size % 8;
    if (rest > 0)
      octets++;

    int bit_number = 0;

    byte o[] = new byte[octets];
    for (int i = 0; i < octets; i++) {
      o[i] = 0;
      int bit = 128;
      for (int t = 0; t < 8; t++) {
        bit_number = i * 8 + t;
        if ((bit_number < size) && bits[bit_number]) {
          o[i] |= bit;
        }
        bit >>= 1;
      }
    }
    return new OctetString(o);
  }

  public static String toString(OctetString o) {
    return new Bits(o).toString();
  }

  /**
   * Returns the Bits object as a sequence of '0' and '1' string.
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    for (int i = 0; i < bits.length; i++) {
      if (bits[i]) {
        str.append("1");
      } else {
        str.append("0");
      }
    }
    return str.toString();
  }

  public static void main(String arg[]) {
    System.out
        .println(new Bits(
            "0101101010010100101010000001011101001010101110010101010000101010101001111111"));
    byte c[] = { 9, 0, -1, 0, 127 };
    System.out.println(new Bits(c));
    System.out.println(new Bits("00000001000000100000010000001000"));
    System.out.println(new Bits("00000001000000100000010000001000").toVar());
    System.out.println(new Bits("11111110111111011111101111110111"));
    System.out.println(new Bits("11111110111111011111101111110111").toVar());
  }
}