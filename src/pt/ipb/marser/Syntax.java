/*
 * $Id: Syntax.java 3 2004-08-03 10:42:11Z rlopes $
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

import pt.ipb.snmp.SnmpConstants;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class Syntax implements Serializable {
  String description = null;

  byte type = SnmpConstants.UNKNOWN;

  Enum enum = null;

  Constraint constraint = null;

  Sequence sequence = null;

  Syntax sequenceType = null;

  /**
   * Builds an empty LeafSyntax object.
   */
  public Syntax() {
  }

  public Syntax(Syntax s, String descr) {
    this(descr);
    this.sequenceType = s;
  }

  public Syntax(byte type, String descr) {
    this(descr);
    setSnmpType(type);
  }

  /**
   * Builds a LeafSyntax object with the provided description.
   */
  public Syntax(String descr) {
    setDescription(descr);
  }

  /**
   * Getter for property enum.
   * 
   * @return Value of property enum.
   */
  public Enum getEnum() {
    return enum;
  }

  /**
   * Setter for property enum.
   * 
   * @param enum
   *          New value of property enum.
   */
  public void setEnum(Enum enum) {
    this.enum = enum;
  }

  /**
   * set a constraint of the form 1..21, SIZE(1..20), -1
   */
  public void setConstraint(Constraint c) {
    this.constraint = c;
  }

  public Constraint getConstraint() {
    return constraint;
  }

  /**
   * Getter for property sequence.
   * 
   * @return Value of property sequence.
   */
  public pt.ipb.marser.Sequence getSequence() {
    return sequence;
  }

  /**
   * Setter for property sequence.
   * 
   * @param sequence
   *          New value of property sequence.
   */
  public void setSequence(pt.ipb.marser.Sequence sequence) {
    this.sequence = sequence;
  }

  /**
   * Set the SYNTAX description, for example: "OCTET STRING (SIZE (0..255))"
   */
  public void setDescription(String s) {
    this.description = s;
  }

  /**
   * Get the SYNTAX description, for example: "OCTET STRING (SIZE (0..255))"
   */
  public String getDescription() {
    return description;
  }

  /**
   * Checks to see if this LeafSyntax corresponds to a base SNMP type.
   * 
   * @see pt.ipb.snmp.SnmpConstants
   */
  public boolean isSnmpType() {
    if (checkSnmpType() != SnmpConstants.UNKNOWN) {
      return true;
    }
    return false;
  }

  /**
   * Get the correspondent base type.
   * 
   * @see pt.ipb.snmp.SnmpConstants
   */
  byte checkSnmpType() {
    if (getDescription().equals(SnmpConstants.SMI_INTEGER)) {
      return SnmpConstants.INTEGER;
    } else if (getDescription().equals(SnmpConstants.SMI_OCTETSTRING)) {
      return SnmpConstants.OCTETSTRING;
    } else if (getDescription().equals(SnmpConstants.SMI_OID)) {
      return SnmpConstants.OID;
    } else if (getDescription().equals(SnmpConstants.SMI_INTEGER32)) {
      return SnmpConstants.INTEGER32;
    } else if (getDescription().equals(SnmpConstants.SMI_IPADDRESS)) {
      return SnmpConstants.IPADDRESS;
    } else if (getDescription().equals(SnmpConstants.SMI_COUNTER64)) {
      return SnmpConstants.COUNTER64;
    } else if (getDescription().equals(SnmpConstants.SMI_COUNTER32)) {
      return SnmpConstants.COUNTER32;
    } else if (getDescription().equals(SnmpConstants.SMI_GAUGE32)) {
      return SnmpConstants.GAUGE32;
    } else if (getDescription().equals(SnmpConstants.SMI_UNSIGNED32)) {
      return SnmpConstants.UNSIGNED32;
    } else if (getDescription().equals(SnmpConstants.SMI_NULL)) {
      return SnmpConstants.NULL;
    } else if (getDescription().equals(SnmpConstants.SMI_OPAQUE)) {
      return SnmpConstants.OPAQUE;
    } else if (getDescription().equals(SnmpConstants.SMI_TIMETICKS)) {
      return SnmpConstants.TIMETICKS;
    }
    return SnmpConstants.UNKNOWN;
  }

  /**
   * Checks to see if this LeafSyntax maps to a base SNMP type. Check if the
   * description starts with any of the SMI_ string.
   * 
   * @see pt.ipb.snmp.SnmpConstants
   */
  public boolean mapsToSnmpType() {
    if (checkSnmpMapping() != SnmpConstants.UNKNOWN) {
      return true;
    }
    return false;
  }

  /**
   * Checks if the description maps directly to Snmp type. It checks if the
   * description starts with any of the Snmp types.
   */
  byte checkSnmpMapping() {
    if (getDescription().startsWith(SnmpConstants.SMI_INTEGER)) {
      return SnmpConstants.INTEGER;
    } else if (getDescription().startsWith(SnmpConstants.SMI_OCTETSTRING)) {
      return SnmpConstants.OCTETSTRING;
    } else if (getDescription().startsWith(SnmpConstants.SMI_OID)) {
      return SnmpConstants.OID;
    } else if (getDescription().startsWith(SnmpConstants.SMI_INTEGER32)) {
      return SnmpConstants.INTEGER32;
    } else if (getDescription().startsWith(SnmpConstants.SMI_IPADDRESS)) {
      return SnmpConstants.IPADDRESS;
    } else if (getDescription().startsWith(SnmpConstants.SMI_COUNTER32)) {
      return SnmpConstants.COUNTER32;
    } else if (getDescription().startsWith(SnmpConstants.SMI_COUNTER64)) {
      return SnmpConstants.COUNTER64;
    } else if (getDescription().startsWith(SnmpConstants.SMI_GAUGE32)) {
      return SnmpConstants.GAUGE32;
    } else if (getDescription().startsWith(SnmpConstants.SMI_OPAQUE)) {
      return SnmpConstants.OPAQUE;
    } else if (getDescription().startsWith(SnmpConstants.SMI_NULL)) {
      return SnmpConstants.NULL;
    } else if (getDescription().startsWith(SnmpConstants.SMI_TIMETICKS)) {
      return SnmpConstants.TIMETICKS;
    } else if (getDescription().startsWith(SnmpConstants.SMI_UNSIGNED32)) {
      return SnmpConstants.UNSIGNED32;
    }
    return SnmpConstants.UNKNOWN;
  }

  /**
   * Sets the SNMP type defined in SnmpConstants.
   * 
   * @see pt.ipb.snmp.SnmpConstants
   */
  public void setSnmpType(byte t) {
    this.type = t;
  }

  /**
   * Returns the SNMP type defined in SnmpConstants.
   */
  public byte getSnmpType() {
    if (type != SnmpConstants.UNKNOWN)
      return type;

    if (isSnmpType()) {
      return checkSnmpType();
    } else if (mapsToSnmpType()) {
      return checkSnmpMapping();
    }
    return SnmpConstants.UNKNOWN;
  }

  /**
   * Returns the SNMP type string defined in SnmpConstants.
   */
  public String getSnmpTypeStr() {
    return SnmpConstants.type2string(type);
  }

  public String toString() {
    return getDescription();
  }

}