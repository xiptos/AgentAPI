/*
 * $Id: ComplianceObject.java 3 2004-08-03 10:42:11Z rlopes $
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
public class ComplianceObject {

  String obj = null;

  Syntax syntax = null;

  Syntax writeSyntax = null;

  String access = null;

  String description = null;

  public ComplianceObject(String obj) {
    setObj(obj);
  }

  /**
   * Getter for property access.
   * 
   * @return Value of property access.
   */
  public java.lang.String getAccess() {
    return access;
  }

  /**
   * Setter for property access.
   * 
   * @param access
   *          New value of property access.
   */
  public void setAccess(java.lang.String access) {
    this.access = access;
  }

  /**
   * Getter for property syntax.
   * 
   * @return Value of property syntax.
   */
  public pt.ipb.marser.Syntax getSyntax() {
    return syntax;
  }

  /**
   * Setter for property syntax.
   * 
   * @param syntax
   *          New value of property syntax.
   */
  public void setSyntax(pt.ipb.marser.Syntax syntax) {
    this.syntax = syntax;
  }

  /**
   * Getter for property writeSyntax.
   * 
   * @return Value of property writeSyntax.
   */
  public pt.ipb.marser.Syntax getWriteSyntax() {
    return writeSyntax;
  }

  /**
   * Setter for property writeSyntax.
   * 
   * @param writeSyntax
   *          New value of property writeSyntax.
   */
  public void setWriteSyntax(pt.ipb.marser.Syntax writeSyntax) {
    this.writeSyntax = writeSyntax;
  }

  /**
   * Getter for property obj.
   * 
   * @return Value of property obj.
   */
  public java.lang.String getObj() {
    return obj;
  }

  /**
   * Setter for property obj.
   * 
   * @param obj
   *          New value of property obj.
   */
  public void setObj(java.lang.String obj) {
    this.obj = obj;
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

}

