/*
 * $Id: DateAndTime.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.StringTokenizer;

import pt.ipb.snmp.type.smi.OctetString;
import pt.ipb.snmp.type.smi.Var;

/**
 * The DateAndTime textual convention. It uses OctetString to represent
 * DateAndTime.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class DateAndTime extends TC {
  int y = 0;

  int m = 0;

  int d = 0;

  int h = 0;

  int M = 0;

  int sec = 0;

  int D = 0;

  char c = ' ';

  int hutc = 0;

  int mutc = 0;

  /**
   * Format of date&time: "yyyy-mm-dd,hh:mm:ss.DD,chh:mm".
   */
  public DateAndTime(int y, int m, int d, int h, int M, int s, int D, char c,
      int hutc, int mutc) {
    this.y = y;
    this.m = m;
    this.d = d;
    this.h = h;
    this.M = M;
    this.sec = s;
    this.D = D;
    this.c = c;
    this.hutc = hutc;
    this.mutc = mutc;
  }

  /**
   * This TC is an OctetString
   */
  public DateAndTime(OctetString o) throws NumberFormatException {
    byte[] bo = (byte[]) o.toJavaValue();
    int aux1 = bo[0] & 0xff;
    int aux2 = bo[1] & 0xff;
    this.y = aux1 * 256 + aux2;
    this.m = bo[2];
    this.d = bo[3];
    this.h = bo[4];
    this.M = bo[5];
    this.sec = bo[6];
    this.D = bo[7];
    this.c = (char) bo[8];
    if (this.c != '+' || this.c != '-' || this.c != ' ')
      this.c = ' ';
    if (bo.length > 9) {
      this.hutc = bo[9];
      this.mutc = bo[10];
    }
  }

  /**
   * Format of date&time: "yyyy-mm-dd,hh:mm:ss.D,chh:mm".
   */
  public DateAndTime(String s) throws NumberFormatException {
    try {
      StringTokenizer s_token = new StringTokenizer(s, ",");
      StringTokenizer date_token = new StringTokenizer(s_token.nextToken(), "-");
      StringTokenizer hour_token = new StringTokenizer(s_token.nextToken(), ":");
      String utc = s_token.nextToken();

      String my_s;
      my_s = date_token.nextToken().trim();
      y = Integer.parseInt(my_s);
      my_s = date_token.nextToken().trim();
      m = Integer.parseInt(my_s);
      my_s = date_token.nextToken().trim();
      d = Integer.parseInt(my_s);

      my_s = hour_token.nextToken().trim();
      h = Integer.parseInt(my_s);
      my_s = hour_token.nextToken().trim();
      M = Integer.parseInt(my_s);
      my_s = hour_token.nextToken().trim();
      StringTokenizer sec_token = new StringTokenizer(my_s, ".");
      my_s = sec_token.nextToken().trim();
      sec = Integer.parseInt(my_s);
      my_s = sec_token.nextToken().trim();
      D = Integer.parseInt(my_s);

      StringBuffer futc = new StringBuffer(utc);
      c = futc.charAt(0);
      futc.deleteCharAt(0);
      StringTokenizer utc_token = new StringTokenizer(futc.toString(), ":");
      my_s = utc_token.nextToken().trim();
      hutc = Integer.parseInt(my_s);
      my_s = utc_token.nextToken().trim();
      mutc = Integer.parseInt(my_s);

    } catch (Exception e) {
      e.printStackTrace();
      throw new NumberFormatException("Invalid DateAndTime format: " + s);
    }
  }

  public int getYear() {
    return y;
  }

  public int getMonth() {
    return m;
  }

  public int getDay() {
    return d;
  }

  public int getHour() {
    return h;
  }

  public int getMin() {
    return M;
  }

  public int getSecond() {
    return sec;
  }

  public int getDeci() {
    return D;
  }

  public char getOffset() {
    return c;
  }

  public int getUTCHour() {
    return hutc;
  }

  public int getUTCMin() {
    return mutc;
  }

  /**
   * This TC is an OctetString
   */
  public Var toVar() {
    byte o[] = new byte[11];
    int y1 = y / 256;
    int y2 = y % 256;
    o[0] = (byte) y1;
    o[1] = (byte) y2;
    o[2] = (byte) m;
    o[3] = (byte) d;
    o[4] = (byte) h;
    o[5] = (byte) M;
    o[6] = (byte) sec;
    o[7] = (byte) D;
    o[8] = (byte) c;
    o[9] = (byte) hutc;
    o[10] = (byte) mutc;
    return new OctetString(o);
  }

  public static String toString(OctetString o) {
    return new DateAndTime(o).toString();
  }

  public String toString() {
    String dstr = new String(y + "-" + m + "-" + d + "," + h + ":" + M + ":"
        + sec + "." + D + "," + c + hutc + ":" + mutc);
    return dstr;
  }

  public static void main(String arg[]) {
    System.out.println(new DateAndTime("2000-2-10,10:17:32.1,+0:0").toVar());
  }
}