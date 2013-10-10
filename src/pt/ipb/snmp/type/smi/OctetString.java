/*
 * $Id: OctetString.java 3 2004-08-03 10:42:11Z rlopes $
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
 * The SNMP OctetString data type.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class OctetString extends Var {
  byte o[];

  /**
   * Constructs an OctetString object with a String which can have three
   * formats. Format: "#a0.#1.#10.#ff" or "12.4.100.35" or "034.023.012".
   */
  public OctetString(String s) throws NumberFormatException {
    type = Var.OCTETSTRING;
    setByteArray(parseString(s));
  }

  public OctetString(byte o[]) {
    type = Var.OCTETSTRING;
    setByteArray(o);
  }

  /**
   * To avoid complaints from sub classes...
   */
  public OctetString() {
  }

  protected byte[] parseString(String s) {
    StringTokenizer str = new StringTokenizer(s, ".");
    byte o[] = new byte[str.countTokens()];
    int i = 0;
    while (str.hasMoreTokens()) {
      String ns = str.nextToken();
      Integer b = Integer.decode(ns);
      //Byte b = Byte.decode(ns);
      o[i++] = b.byteValue();
    }
    return o;
  }

  public void setByteArray(byte[] ba) {
    this.o = ba;
  }

  public int length() {
    return o.length;
  }

  public OctetString subOctetString(int i) {
    return subOctetString(i, length());
  }

  public OctetString subOctetString(int i, int f) {
    if (i < 0 || i > length() || i > f)
      throw new IndexOutOfBoundsException("Index out of bounds : " + i + ", "
          + f);
    byte no[] = new byte[f - i];
    for (int t = 0; t < (f - i); t++) {
      if (t + i >= length())
        break;
      no[t] = o[t + i];
    }
    OctetString s = new OctetString(no);
    return s;
  }

  public int indexOf(OctetString os) {
    return indexOf(os, 0);
  }

  public int indexOf(OctetString os, int fromIndex) {
    return indexOf(o, length(), os.o, os.length(), fromIndex);
  }

  public static int indexOf(byte[] source, int sourceCount, byte[] target,
      int targetCount, int fromIndex) {
    if (fromIndex >= sourceCount) {
      return (targetCount == 0 ? sourceCount : -1);
    }
    if (fromIndex < 0) {
      fromIndex = 0;
    }
    if (targetCount == 0) {
      return fromIndex;
    }
    int targetOffset = 0;
    int sourceOffset = 0;
    byte first = target[targetOffset];
    int i = sourceOffset + fromIndex;
    int max = sourceOffset + (sourceCount - targetCount);

    startSearchForFirstChar: while (true) {
      /* Look for first character. */
      while (i <= max && source[i] != first) {
        i++;
      }
      if (i > max) {
        return -1;
      }

      /* Found first character, now look at the rest of v2 */
      int j = i + 1;
      int end = j + targetCount - 1;
      int k = targetOffset + 1;
      while (j < end) {
        if (source[j++] != target[k++]) {
          i++;
          /* Look for str's first char again. */
          continue startSearchForFirstChar;
        }
      }
      return i - sourceOffset; /* Found whole string. */
    }
  }

  /**
   * Returns this OctetString as a byte[]
   */
  public Object toJavaValue() {
    return o;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    for (int i = 0; i < o.length; i++) {
      int t = o[i] & 0xff;
      str.append(t);
      if (i < o.length - 1)
        str.append(".");
    }
    return str.toString();
  }

  public static void main(String arg[]) {
    byte i[] = { 19, 20, 10, 1 };
    System.out.println(new OctetString(i));
    System.out.println(new OctetString("#a0.#a.#f"));
    System.out.println(new OctetString("100.101.102.103.104"));
    System.out.println(new OctetString(arg[0]));
  }

}