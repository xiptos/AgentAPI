/*
 * $Id: EngineException.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
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

package pt.ipb.agentapi.engine;

public class EngineException extends Exception {
  /**
   * Create a new EngineException.
   * 
   * @param message
   *          The error or warning message.
   */
  public EngineException(String message) {
    super(message);
    this.exception = null;
  }

  /**
   * Create a new EngineException wrapping an existing exception.
   * 
   * <p>
   * The existing exception will be embedded in the new one, and its message
   * will become the default message for the EngineException.
   * </p>
   * 
   * @param e
   *          The exception to be wrapped in a EngineException.
   */
  public EngineException(Exception e) {
    super();
    this.exception = e;
  }

  /**
   * Create a new EngineException from an existing exception.
   * 
   * <p>
   * The existing exception will be embedded in the new one, but the new
   * exception will have its own message.
   * </p>
   * 
   * @param message
   *          The detail message.
   * @param e
   *          The exception to be wrapped in a EngineException.
   */
  public EngineException(String message, Exception e) {
    super(message);
    this.exception = e;
  }

  /**
   * Return a detail message for this exception.
   * 
   * <p>
   * If there is an embedded exception, and if the EngineException has no detail
   * message of its own, this method will return the detail message from the
   * embedded exception.
   * </p>
   * 
   * @return The error or warning message.
   */
  public String getMessage() {
    String message = super.getMessage();
    if (message == null && exception != null) {
      return exception.getMessage();
    } else {
      return message;
    }
  }

  /**
   * Return the embedded exception, if any.
   * 
   * @return The embedded exception, or null if there is none.
   */
  public Exception getException() {
    return exception;
  }

  /**
   * Override toString to pick up any embedded exception.
   * 
   * @return A string representation of this exception.
   */
  public String toString() {
    if (exception != null) {
      return exception.toString();
    } else {
      return super.toString();
    }
  }

  //////////////////////////////////////////////////////////////////////
  // Internal state.
  //////////////////////////////////////////////////////////////////////

  /**
   * @serial The embedded exception if tunnelling, or null.
   */
  private Exception exception;

}

