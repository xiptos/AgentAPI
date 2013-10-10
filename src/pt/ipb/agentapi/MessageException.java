/*
 * $Id: MessageException.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
 * *
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

package pt.ipb.agentapi;

public class MessageException extends Exception {

  byte error = 0;

  /**
   * Constructs a <code>MessageException</code> with <code>null</code> as
   * its error detail message.
   */
  public MessageException() {
    super();
  }

  /**
   * Constructs a <code>MessageException</code> with <code>null</code> as
   * its error detail message.
   */
  public MessageException(byte error) {
    super(getExceptionString(error));
    this.error = error;
  }

  /**
   * Constructs a <code>MessageException</code> with the specified detail
   * message. The error message string <code>s</code> can later be retrieved
   * by the <code>getMessage()</code> method of class
   * <code>java.lang.Throwable</code>.
   * 
   * @param s
   *          the detail message.
   */
  public MessageException(String s) {
    super(s);
  }

  public static String getExceptionString(byte error) {
    switch (error) {
    case AbstractAgent.NO_SUCH_OBJECT:
      return new String("No such object");
    case AbstractAgent.NO_SUCH_INSTANCE:
      return new String("No such instance");
    case AbstractAgent.GEN_ERR:
      return new String("General error");
    case AbstractAgent.NO_ERROR:
      return new String("No error");
    case AbstractAgent.TOO_BIG:
      return new String("Too big");
    case AbstractAgent.END_OF_MIB_VIEW:
      return new String("End of MIB view");
    case AbstractAgent.NO_ACCESS:
      return new String("No Access");
    case AbstractAgent.NOT_WRITABLE:
      return new String("Not writable");
    case AbstractAgent.WRONG_TYPE:
      return new String("Wrong type");
    case AbstractAgent.WRONG_LENGTH:
      return new String("Wrong length");
    case AbstractAgent.WRONG_ENCODING:
      return new String("Wrong encoding");
    case AbstractAgent.WRONG_VALUE:
      return new String("Wrong value");
    case AbstractAgent.NO_CREATION:
      return new String("No creation");
    case AbstractAgent.INCONSISTENT_NAME:
      return new String("Inconsistent name");
    case AbstractAgent.INCONSISTENT_VALUE:
      return new String("Inconsistent value");
    case AbstractAgent.RESOURCE_UNAVAILABLE:
      return new String("Resource unavailable");
    case AbstractAgent.COMMIT_FAILED:
      return new String("Commit failed");
    case AbstractAgent.UNDO_FAILED:
      return new String("Undo failed");
    }
    return null;

  }

  public byte getError() {
    return error;
  }
}