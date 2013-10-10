/*
 * Macro.java
 *
 * $Id: Macro.java 3 2004-08-03 10:42:11Z rlopes $
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
public class Macro implements java.io.Serializable {
  String description;

  String label;

  MibModule module = null;

  /** Creates a new instance of Type */
  public Macro() {
  }

  public Macro(String label) {
    setLabel(label);
  }

  /**
   * Getter for property description.
   * 
   * @return Value of property description.
   */
  public java.lang.String getDescription() {
    return description;
  }

  /**
   * Setter for property description.
   * 
   * @param description
   *          New value of property description.
   */
  public void setDescription(java.lang.String description) {
    this.description = description;
  }

  /**
   * Getter for property label.
   * 
   * @return Value of property label.
   */
  public java.lang.String getLabel() {
    return label;
  }

  /**
   * Setter for property label.
   * 
   * @param label
   *          New value of property label.
   */
  public void setLabel(java.lang.String label) {
    this.label = label;
  }

  public MibModule getModule() {
    return module;
  }

  public void setModule(MibModule module) {
    this.module = module;
  }

}