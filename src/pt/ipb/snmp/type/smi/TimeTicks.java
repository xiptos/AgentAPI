/*
 * $Id: TimeTicks.java 3 2004-08-03 10:42:11Z rlopes $
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

/**
 * The SNMP TimeTicks data type.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class TimeTicks extends Unsigned {

  public TimeTicks(String s, int radix) {
    super(s, radix);
    type = Var.TIMETICKS;
  }

  public TimeTicks(String s) {
    super(s);
    type = Var.TIMETICKS;
  }

  public TimeTicks(long i) {
    super(i);
    type = Var.TIMETICKS;
  }

  public static void main(String arg[]) {
    System.out.println(new TimeTicks(arg[0]));
  }

}