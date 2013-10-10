/*
 * $Id: InvalidTypeException.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.marser;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class InvalidTypeException extends Exception {
  public static final byte USEOFZERO = 1;

  byte type;

  /**
   * Constructs a <code>InvalidTypeException</code> with <code>null</code>
   * as its error detail message.
   */
  public InvalidTypeException() {
    super();
  }

  /**
   * Constructs a <code>InvalidTypeException</code> with the specified detail
   * message. The error message string <code>s</code> can later be retrieved
   * by the <code>getMessage()</code> method of class
   * <code>java.lang.Throwable</code>.
   * 
   * @param s
   *          the detail message.
   */
  public InvalidTypeException(String s) {
    super(s);
  }

  public InvalidTypeException(byte t) {
    this(errorToString(t));
    this.type = t;
  }

  public byte getType() {
    return type;
  }

  public static String errorToString(byte t) {
    switch (t) {
    case USEOFZERO:
      return "Use of zero exception";
    }
    return "Unknown error type";
  }
}