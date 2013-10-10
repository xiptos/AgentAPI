/*
 * BulkMessageEvent.java
 *
 * Created on 10 de Julho de 2002, 19:22
 * $Id: BulkMessageEvent.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.event;

import java.util.Vector;

/**
 * 
 * @author rlopes
 */
public class BulkMessageEvent extends MessageEvent {

  int maxRep = 0;

  int nonRep = 0;

  /** Creates a new instance of BulkMessageEvent */
  public BulkMessageEvent(Object source) {
    super(source);
  }

  public BulkMessageEvent(Object source, Vector v) {
    super(source, v);
  }

  /**
   * Getter for property maxRep.
   * 
   * @return Value of property maxRep.
   */
  public int getMaxRep() {
    return maxRep;
  }

  /**
   * Setter for property maxRep.
   * 
   * @param maxRep
   *          New value of property maxRep.
   */
  public void setMaxRep(int maxRep) {
    this.maxRep = maxRep;
  }

  /**
   * Getter for property nonRep.
   * 
   * @return Value of property nonRep.
   */
  public int getNonRep() {
    return nonRep;
  }

  /**
   * Setter for property nonRep.
   * 
   * @param nonRep
   *          New value of property nonRep.
   */
  public void setNonRep(int nonRep) {
    this.nonRep = nonRep;
  }

}