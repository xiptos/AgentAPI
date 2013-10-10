/*
 * $Id: MibException.java 3 2004-08-03 10:42:11Z rlopes $
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
public class MibException extends Exception {
  public static final byte INVALID_SYNTAX = 1;

  int line = -1;

  int col = -1;

  String token = null;

  String fileName;

  byte type;

  /**
   * Constructs a <code>MibException</code> with <code>null</code> as its
   * error detail message.
   */
  public MibException() {
    super();
  }

  public MibException(String s, String fileName) {
    this(s);
    this.fileName = fileName;
  }

  public MibException(String s, String fileName, String token, int line, int col) {
    this(s);
    this.fileName = fileName;
    this.token = token;
    this.line = line;
    this.col = col;
  }

  /**
   * Constructs a <code>MibException</code> with the specified detail message.
   * The error message string <code>s</code> can later be retrieved by the
   * <code>getMessage()</code> method of class
   * <code>java.lang.Throwable</code>.
   * 
   * @param s
   *          the detail message.
   */
  public MibException(String s) {
    super(s);
  }

  public MibException(byte t) {
    this(errorToString(t));
    this.type = t;
  }

  public byte getType() {
    return type;
  }

  public String getToken() {
    return token;
  }

  public int getLine() {
    return line;
  }

  public int getCol() {
    return col;
  }

  public String getFileName() {
    return fileName;
  }

  public static String errorToString(byte t) {
    switch (t) {
    case INVALID_SYNTAX:
      return "Invalid Syntax";
    }
    return "Unknown error type";
  }
}