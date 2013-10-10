/*
 * $Id: MibTC.java 3 2004-08-03 10:42:11Z rlopes $
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
public class MibTC extends Macro {

  String display = null;

  String status = null;

  String reference = null;

  String label = null;

  Syntax syntax = null;

  /**
   * Constructor
   */
  public MibTC() {
    super();
  }

  /**
   * Checks if the provided MibTC has the same label.
   * 
   * @return true if the provided MibTC has the same name.
   */
  public boolean equals(Object tc) {
    if (!(tc instanceof MibTC))
      return false;
    return label.equals(((MibTC) tc).getLabel());
  }

  /**
   * Set the TC syntax string, for example: INTEGER { localResourceLack(-1),
   * badDestination(-2) }.
   */
  public void setSyntax(Syntax s) {
    syntax = s;
  }

  /**
   * Get the Syntax correspondent to this TC.
   */
  public Syntax getSyntax() {
    return syntax;
  }

  /**
   * Get the name text for this node
   */
  public String getLabel() {
    return label;
  }

  /**
   * Set the name text for this TC
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Get the reference part for this TC
   * 
   * @return null if it is not defined for this node
   */
  public String getReference() {
    return reference;
  }

  /**
   * Set the reference part for this TC
   */
  public void setReference(String reference) {
    this.reference = reference;
  }

  /**
   * Get the status for this TC
   * 
   * @return null if it is not defined for this node
   */
  public String getStatus() {
    return status;
  }

  /**
   * Set the status for this TC
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Set display hint for this TC
   */
  public void setDisplayHint(String dh) {
    this.display = dh;
  }

  /**
   * Get the display hint for this TC
   */
  public String getDisplayHint() {
    return display;
  }

  public String toString() {
    return getLabel();
  }
}

