/*
 * $Id: TC.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.snmp.type.tc;

import pt.ipb.snmp.type.smi.Var;

/**
 * The base class for all the SNMP base types. From RFC2578:
 * 
 * <pre>
 * 
 *  
 *   
 *    -- the &quot;base types&quot; defined here are:
 *    --   3 built-in ASN.1 types: INTEGER, OCTET STRING, OBJECT IDENTIFIER
 *    --   8 application-defined types: Integer32, IpAddress, Counter32,
 *    --              Gauge32, Unsigned32, TimeTicks, Opaque, and Counter64
 *    
 *   
 *  
 * </pre>
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public abstract class TC implements java.io.Serializable {

  /**
   * Abstract method to return the textual convention as an SNMP type.
   */
  public abstract Var toVar();

  /**
   * Abstract method to return the TC String representation.
   */
  public abstract String toString();
}