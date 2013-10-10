/*
 * $Id: MibTrap.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import pt.ipb.snmp.type.smi.OID;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibTrap extends Macro implements Serializable {

  String enterpriseLabel;

  OID enterprise = null;

  Vector vars = new Vector();

  String label = null;

  String reference = null;

  int number;

  /**
   * Constructor
   */
  public MibTrap() {
  }

  public MibTrap(String label) {
    setLabel(label);
  }

  public MibTrap(OID enterprise) {
    setEnterprise(enterprise);
  }

  public MibTrap(MibNode enterpriseNode) {
    setEnterprise(enterpriseNode.getOID());
  }

  /**
   * Get the name text for this node
   */
  public String getLabel() {
    return label;
  }

  /**
   * Set the name text for this trap
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Get the reference part for this trap
   * 
   * @return null if it is not defined for this node
   */
  public String getReference() {
    return reference;
  }

  /**
   * Set the reference part for this trap
   */
  public void setReference(String reference) {
    this.reference = reference;
  }

  /**
   * Get the enterprise for this trap
   * 
   * @return null if it is not defined for this node
   */
  public OID getEnterprise() {
    return enterprise;
  }

  public void setEnterpriseLabel(String s) {
    enterpriseLabel = s;
  }

  public String getEnterpriseLabel() {
    return enterpriseLabel;
  }

  /**
   * Set the enterprise for this trap
   */
  public void setEnterprise(String enterprise) {
    setEnterprise(new OID(enterprise));
  }

  /**
   * Set the enterprise for this trap
   */
  public void setEnterprise(OID enterprise) {
    this.enterprise = enterprise;
  }

  /**
   * Set the vars for this trap
   */
  public void addVar(String var) {
    vars.addElement(var);
  }

  /**
   * Get the vars for this trap
   */
  public Enumeration vars() {
    return vars.elements();
  }

  public void setNumber(int n) {
    this.number = n;
  }

  public void setNumber(String n) throws NumberFormatException {
    this.number = Integer.parseInt(n);
  }

  public int getNumber() {
    return number;
  }

  public String toString() {
    return getLabel();
  }

}

