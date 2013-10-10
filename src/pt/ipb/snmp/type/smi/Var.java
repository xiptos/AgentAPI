/*
 * $Id: Var.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.snmp.type.smi;

import pt.ipb.snmp.SnmpConstants;

/**
 * The base class for all the SNMP base types. From RFC2578:
 * 
 * <pre>
 * 
 *  
 *   
 *    
 *     -- the &quot;base types&quot; defined here are:
 *     --   3 built-in ASN.1 types: INTEGER, OCTET STRING, OBJECT IDENTIFIER
 *     --   8 application-defined types: Integer32, IpAddress, Counter32,
 *     --              Gauge32, Unsigned32, TimeTicks, Opaque, and Counter64
 *     
 *    
 *   
 *  
 * </pre>
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public abstract class Var implements java.io.Serializable, Cloneable {

  // built-in ASN.1 types
  public static final byte INTEGER = SnmpConstants.INTEGER;

  public static final byte OCTETSTRING = SnmpConstants.OCTETSTRING;

  public static final byte OID = SnmpConstants.OID;

  // application-defined types for SMIv1
  public static final byte IPADDRESS = SnmpConstants.IPADDRESS;

  public static final byte COUNTER = SnmpConstants.COUNTER;

  public static final byte GAUGE = SnmpConstants.GAUGE;

  public static final byte OPAQUE = SnmpConstants.OPAQUE;

  public static final byte TIMETICKS = SnmpConstants.TIMETICKS;

  public static final byte NULL = SnmpConstants.NULL;

  // application-defined types for SMIv2
  public static final byte INTEGER32 = SnmpConstants.INTEGER32;

  public static final byte COUNTER32 = SnmpConstants.COUNTER32;

  public static final byte GAUGE32 = SnmpConstants.GAUGE32;

  public static final byte UNSIGNED32 = SnmpConstants.UNSIGNED32;

  public static final byte COUNTER64 = SnmpConstants.COUNTER64;

  protected byte type = 0;

  protected Var() {
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public static Var createVar(String value, byte type)
      throws NumberFormatException {
    Var var = null;

    switch (type) {

    case INTEGER:
      var = new Int(value);
      break;

    case OCTETSTRING:
      var = new OctetString(value);
      break;

    case OID:
      var = new OID(value);
      break;

    case IPADDRESS:
      var = new IpAddress(value);
      break;

    case COUNTER32:
      var = new Counter(value);
      break;
    case GAUGE32:
      var = new Gauge(value);
      break;

    case UNSIGNED32:
      var = new Unsigned(value);
      break;

    case TIMETICKS:
      var = new TimeTicks(value);
      break;

    case OPAQUE:
      var = new Opaque(value);
      break;

    case COUNTER64:
      var = new Counter64(value);
      break;

    case NULL:
      var = new Null();
      break;

    default:
      throw new NumberFormatException("Invalid type");
    }
    return var;
  }

  /**
   * Method to return the type of this Var object.
   */
  public byte getType() {
    return type;
  }

  public String getTypeStr() {
    return SnmpConstants.type2string(type);
  }

  /**
   * Abstract method to return the value as a native Java type representation.
   */
  public abstract Object toJavaValue();

  /**
   * Abstract method to return the Var String representation.
   */
  public abstract String toString();
}