/*
 * $Id: SnmpConstants.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.snmp;

/**
 * Some SNMP constants
 * 
 * @author rlopes
 * @version $Revision: 1.2 $
 */
public class SnmpConstants {
  public static final int SNMPv1 = 1;

  public static final int SNMPv2c = 2;

  public static final int SNMPv3 = 3;

  public static final int NOAUTH = -1;

  public static final int NOPRIV = -1;

  public static final int MD5 = 5;

  public static final int SHA = 6;

  public static final int DES = 7;

  public static final String S_SNMPv1 = "v1";

  public static final String S_SNMPv2c = "v2c";

  public static final String S_SNMPv3 = "v3";

  public static final String S_NOAUTH = "NoAuth";

  public static final String S_NOPRIV = "NoPriv";

  public static final String S_MD5 = "MD5";

  public static final String S_SHA = "SHA";

  public static final String S_DES = "DES";

  // SNMP types
  public static final byte UNKNOWN = -1;

  // built-in ASN.1 types
  public static final byte INTEGER = 1;

  public static final byte OCTETSTRING = 2;

  public static final byte OID = 3;

  // application-defined types for SMIv1
  public static final byte NETWORKADDRESS = 4;

  public static final byte IPADDRESS = 5;

  public static final byte COUNTER = 6;

  public static final byte GAUGE = 7;

  public static final byte TIMETICKS = 8;

  public static final byte OPAQUE = 9;

  public static final byte NULL = 10;

  // application-defined types for SMIv2
  public static final byte INTEGER32 = INTEGER;

  public static final byte UNSIGNED32 = 11;

  public static final byte COUNTER32 = 12;

  public static final byte GAUGE32 = 13;

  public static final byte COUNTER64 = 14;

  public static final byte BITS = 15;

  // built-in ASN.1 types as appear on SNMPv2-SMI
  public static final String SMI_INTEGER = "INTEGER";

  public static final String SMI_OCTETSTRING = "OCTET STRING";

  public static final String SMI_OID = "OBJECT IDENTIFIER";

  // application-defined ASN.1 types as appear on SMIv1
  public static final String SMI_NETWORKADDRESS = "NetworkAddress";

  public static final String SMI_IPADDRESS = "IpAddress";

  public static final String SMI_COUNTER = "Counter";

  public static final String SMI_GAUGE = "Gauge";

  public static final String SMI_TIMETICKS = "TimeTicks";

  public static final String SMI_OPAQUE = "Opaque";

  public static final String SMI_NULL = "Null";

  // application-defined ASN.1 types as appear on SNMPv2-SMI
  public static final String SMI_INTEGER32 = "Integer32";

  public static final String SMI_COUNTER32 = "Counter32";

  public static final String SMI_GAUGE32 = "Gauge32";

  public static final String SMI_UNSIGNED32 = "Unsigned32";

  public static final String SMI_COUNTER64 = "Counter64";

  // SNMP errors
  public final static byte NO_RESPONSE = -1;

  public final static byte NO_ERROR = 0;

  public final static byte TOO_BIG = 1;

  public final static byte NO_SUCH_NAME = 2;

  public final static byte BAD_VALUE = 3;

  public final static byte READ_ONLY = 4;

  public final static byte GEN_ERR = 5;

  public final static byte NO_ACCESS = 6;

  public final static byte WRONG_TYPE = 7;

  public final static byte WRONG_LENGTH = 8;

  public final static byte WRONG_ENCODING = 9;

  public final static byte WRONG_VALUE = 10;

  public final static byte NO_CREATION = 11;

  public final static byte INCONSISTENT_VALUE = 12;

  public final static byte RESOURCE_UNAVAILABLE = 13;

  public final static byte COMMIT_FAILED = 14;

  public final static byte UNDO_FAILED = 15;

  public final static byte AUTHORIZATION_ERROR = 16;

  public final static byte NOT_WRITABLE = 17;

  public final static byte INCONSISTENT_NAME = 18;

  public final static byte NO_SUCH_OBJECT = 19;

  public final static byte NO_SUCH_INSTANCE = 20;

  public final static byte END_OF_MIB_VIEW = 21;

  public static String error2string(byte error) {
    switch (error) {
    case NO_SUCH_OBJECT:
      return new String("No such object");
    case NO_SUCH_INSTANCE:
      return new String("No such instance");
    case GEN_ERR:
      return new String("General error");
    case NO_ERROR:
      return new String("No error");
    case TOO_BIG:
      return new String("Too big");
    case END_OF_MIB_VIEW:
      return new String("End of MIB view");
    case NO_ACCESS:
      return new String("No Access");
    case NOT_WRITABLE:
      return new String("Not writable");
    case WRONG_TYPE:
      return new String("Wrong type");
    case WRONG_LENGTH:
      return new String("Wrong length");
    case WRONG_ENCODING:
      return new String("Wrong encoding");
    case WRONG_VALUE:
      return new String("Wrong value");
    case NO_CREATION:
      return new String("No creation");
    case INCONSISTENT_NAME:
      return new String("Inconsistent name");
    case INCONSISTENT_VALUE:
      return new String("Inconsistent value");
    case RESOURCE_UNAVAILABLE:
      return new String("Resource unavailable");
    case COMMIT_FAILED:
      return new String("Commit failed");
    case UNDO_FAILED:
      return new String("Undo failed");
    }
    return null;
  }

  public static String type2string(byte type) {
    switch (type) {
    case INTEGER:
      return SMI_INTEGER;
    case OCTETSTRING:
      return SMI_OCTETSTRING;
    case OID:
      return SMI_OID;
    case IPADDRESS:
      return SMI_IPADDRESS;
    case COUNTER32:
      return SMI_COUNTER32;
    case GAUGE32:
      return SMI_GAUGE32;
    case UNSIGNED32:
      return SMI_UNSIGNED32;
    case TIMETICKS:
      return SMI_TIMETICKS;
    case OPAQUE:
      return SMI_OPAQUE;
    case COUNTER64:
      return SMI_COUNTER64;
    case NULL:
      return SMI_NULL;
    default:
      return "unknown";
    }
  }

  public static byte string2type(String u) {
    if (SMI_INTEGER.equals(u)) {
      return INTEGER;
    } else if (SMI_OCTETSTRING.equals(u)) {
      return OCTETSTRING;
    } else if (SMI_OID.equals(u)) {
      return OID;
    } else if (SMI_INTEGER32.equals(u)) {
      return INTEGER32;
    } else if (SMI_IPADDRESS.equals(u)) {
      return IPADDRESS;
    } else if (SMI_COUNTER32.equals(u)) {
      return COUNTER32;
    } else if (SMI_GAUGE32.equals(u)) {
      return GAUGE32;
    } else if (SMI_UNSIGNED32.equals(u)) {
      return UNSIGNED32;
    } else if (SMI_TIMETICKS.equals(u)) {
      return TIMETICKS;
    } else if (SMI_OPAQUE.equals(u)) {
      return OPAQUE;
    } else if (SMI_COUNTER64.equals(u)) {
      return COUNTER64;
    } else if (SMI_NULL.equals(u)) {
      return NULL;
    } else {
      return UNKNOWN;
    }
  }

  public static int version2int(String u) {
    if (S_SNMPv1.equals(u)) {
      return SNMPv1;
    } else if (S_SNMPv2c.equals(u)) {
      return SNMPv2c;
    } else if (S_SNMPv3.equals(u)) {
      return SNMPv3;
    }
    return -1;
  }

  public static String version2string(int version) {
    switch (version) {
    case SNMPv1:
      return S_SNMPv1;
    case SNMPv2c:
      return S_SNMPv2c;
    case SNMPv3:
      return S_SNMPv3;
    }
    return null;
  }

  public static int auth2int(String u) {
    if (S_MD5.equals(u)) {
      return MD5;
    } else if (S_SHA.equals(u)) {
      return SHA;
    }
    return -1;
  }

  public static String auth2string(int version) {
    switch (version) {
    case MD5:
      return S_MD5;
    case SHA:
      return S_SHA;
    }
    return null;
  }

  public static int priv2int(String u) {
    if (S_DES.equals(u)) {
      return DES;
    }
    return -1;
  }

  public static String priv2string(int version) {
    switch (version) {
    case DES:
      return S_DES;
    }
    return null;
  }
}