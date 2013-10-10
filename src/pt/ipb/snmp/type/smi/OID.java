/*
 * $Id: OID.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A class which represents an OID.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class OID extends Var implements Comparable {
  String oid = null;

  public OID(byte ns[]) throws NumberFormatException {
    type = Var.OID;
    StringBuffer str = new StringBuffer();
    for (int i = 0; i < ns.length; i++) {
      str.append(".");
      int my = ns[i] & 0xff;
      str.append(ns[i]);
    }
    setOID(str.toString());
  }

  public OID(int ns[]) throws NumberFormatException {
    type = Var.OID;
    StringBuffer str = new StringBuffer();
    for (int i = 0; i < ns.length; i++) {
      str.append(".");
      str.append(ns[i]);
    }
    setOID(str.toString());
  }

  public OID(long ns[]) throws NumberFormatException {
    type = Var.OID;
    StringBuffer str = new StringBuffer();
    for (int i = 0; i < ns.length; i++) {
      str.append(".");
      str.append(ns[i]);
    }
    setOID(str.toString());
  }

  public OID(OID oid) {
    type = Var.OID;
    setOID(oid.toString());
  }

  public OID(String oid) {
    type = Var.OID;
    setOID(oid);
  }

  public OID() {
    this("");
    type = Var.OID;
  }

  public Object clone() {
    return new OID(this);
  }

  /**
   * Appends the given int to this OID.
   */
  public void append(int i) {
    append(Integer.toString(i));
  }

  /**
   * Appends the given OID to this.
   */
  public void append(OID oid) {
    append(oid.toString());
  }

  /**
   * Appends the given OID to this.
   */
  public void append(String oid) {
    if (oid.equals(""))
      return;

    if (oid.startsWith(".")) {
      this.oid = new String(this.oid + oid);
    } else {
      this.oid = new String(this.oid + "." + oid);
    }
  }

  public int indexOf(OID o) {
    return indexOf(o, 0);
  }

  /**
   * Returns the index in this OID of the given OID or -1 if not found.
   */
  public int indexOf(OID o, int fromIndex) {
    StringTokenizer myToken = new StringTokenizer(toString(), ".");
    StringTokenizer otherToken = new StringTokenizer(o.toString(), ".");

    int v1[] = new int[length()];
    int v2[] = new int[o.length()];

    int a = 0;
    try {
      while (myToken.hasMoreTokens()) {
        v1[a++] = Integer.parseInt(myToken.nextToken());
      }

      a = 0;
      while (otherToken.hasMoreTokens()) {
        v2[a++] = Integer.parseInt(otherToken.nextToken());
      }
    } catch (Exception e) {
      return -1;
    }

    int count = length();
    int otherCount = o.length();
    int max = count - o.length();
    if (fromIndex >= count) {
      if (count == 0 && fromIndex == 0 && otherCount == 0) {
        /* There is an empty OID at index 0 in an empty OID. */
        return 0;
      }
      /* Note: fromIndex might be near -1>>>1 */
      return -1;
    }
    if (fromIndex < 0) {
      fromIndex = 0;
    }
    if (otherCount == 0) {
      return fromIndex;
    }

    int first = v2[0];
    int i = fromIndex;

    startSearchForFirstInt: while (true) {

      /* Look for first integer. */
      while (i <= max && v1[i] != first) {
        i++;
      }
      if (i > max) {
        return -1;
      }

      /* Found first integer, now look at the rest of v2 */
      int j = i + 1;
      int end = j + otherCount - 1;
      int k = 1;
      while (j < end) {
        if (v1[j++] != v2[k++]) {
          i++;
          /* Look for OID's first int again. */
          continue startSearchForFirstInt;
        }
      }
      return i; /* Found whole string. */
    }
  }

  /**
   * Returns the length of this OID.
   */
  public int length() {
    StringTokenizer myToken = new StringTokenizer(toString(), ".");
    return myToken.countTokens();
  }

  /**
   * Eliminates zeros in both ends of the OID.
   */
  public void trim() {
    StringBuffer res = new StringBuffer();
    StringTokenizer myToken = new StringTokenizer(toString(), ".");
    Vector v = new Vector();

    while (myToken.hasMoreTokens()) {
      v.addElement(myToken.nextToken());
    }

    try {
      while (Integer.parseInt(((String) v.firstElement())) == 0) {
        v.removeElementAt(0);
      }
      while (Integer.parseInt(((String) v.lastElement())) == 0) {
        v.removeElementAt(v.size() - 1);
      }
      for (Enumeration e = v.elements(); e.hasMoreElements();) {
        res.append((String) e.nextElement());
        if (e.hasMoreElements())
          res.append(".");
      }
      this.oid = res.toString();
    } catch (java.util.NoSuchElementException e) {
      this.oid = "";
    } catch (Exception e) {
    }
  }

  /**
   * Applies a mask to this OID. The mask has n elements of the type
   * maskElement, followed by m elements of type maskElement2.
   */
  public OID applyMask(int n, int maskElement, int m, int maskElement2) {
    StringBuffer mask = new StringBuffer();
    for (int i = 0; i < n; i++) {
      mask.append(maskElement);
      if (i < n - 1)
        mask.append(".");
    }
    for (int i = 0; i < m; i++) {
      mask.append(".");
      mask.append(maskElement2);
    }
    return applyMask(new OID(mask.toString()));
  }

  /**
   * Applies a mask to this OID. if this OID.length() > mask.length() then the
   * mask applies only to the first mask.length() numbers. if this OID.length() <
   * mask.length() then the mask applies only to the first OID.length() numbers.
   */
  public OID applyMask(OID mask) {
    StringBuffer res = new StringBuffer();
    StringTokenizer myToken = new StringTokenizer(toString(), ".");
    StringTokenizer otherToken = new StringTokenizer(mask.toString(), ".");

    while (myToken.hasMoreTokens() && otherToken.hasMoreTokens()) {

      String myEl = myToken.nextToken();
      String otherEl = otherToken.nextToken();

      try {
        int myInt = Integer.parseInt(myEl);
        int otherInt = Integer.parseInt(otherEl);
        int result = myInt & otherInt;
        res.append(result);
        if (myToken.hasMoreTokens()) {
          res.append(".");
        }
      } catch (NumberFormatException nfe) {
        throw new ClassCastException("Invalid OID");
      }
    }

    while (myToken.hasMoreTokens()) {
      String myEl = myToken.nextToken();
      res.append(myEl);
      if (myToken.hasMoreTokens()) {
        res.append(".");
      }
    }

    return new OID(res.toString());

  }

  /**
   * Returns a sub oid from i to the end.
   */
  public OID subOID(int i) throws NoSuchElementException {
    return subOID(i, length());
  }

  /**
   * Returns a sub oid from i to the f.
   */
  public OID subOID(int i, int f) throws NoSuchElementException {
    if (i > f)
      throw new NoSuchElementException();

    StringBuffer aux = new StringBuffer();

    StringTokenizer myToken = new StringTokenizer(toString(), ".");
    int t = 0;
    while (t < i) {
      myToken.nextToken();
      t++;
    }

    while (myToken.hasMoreTokens() && (t < f)) {
      aux.append(myToken.nextToken());
      t++;
      if (myToken.hasMoreTokens() && (t < f))
        aux.append(".");
    }

    String auxStr = aux.toString();
    if (oid.startsWith(".") && i == 0)
      return new OID(new String("." + auxStr));
    else
      return new OID(auxStr);
  }

  /**
   * true if this OID ends with otherOid.
   */
  public boolean endsWith(OID otherOid) throws ClassCastException {
    Vector myVector = new Vector();
    Vector otherVector = new Vector();
    StringTokenizer myToken = new StringTokenizer(toString(), ".");
    StringTokenizer otherToken = new StringTokenizer(otherOid.toString(), ".");

    try {
      while (myToken.hasMoreTokens()) {
        myVector.addElement(new Integer(myToken.nextToken()));
      }

      while (otherToken.hasMoreTokens()) {
        otherVector.addElement(new Integer(otherToken.nextToken()));
      }

      if (otherVector.size() > myVector.size())
        return false;

      while (!otherVector.isEmpty()) {
        Integer i1 = (Integer) otherVector.lastElement();
        Integer i2 = (Integer) myVector.lastElement();
        if (!i1.equals(i2))
          return false;

        otherVector.removeElement(i1);
        myVector.removeElement(i2);
      }

    } catch (NumberFormatException nfe) {
      throw new ClassCastException("Invalid OID");
    }
    return true;

  }

  /**
   * true if this OID starts with otherOid.
   */
  public boolean startsWith(OID otherOid) throws ClassCastException {
    StringTokenizer myToken = new StringTokenizer(toString(), ".");
    StringTokenizer otherToken = new StringTokenizer(otherOid.toString(), ".");

    while (myToken.hasMoreTokens() && otherToken.hasMoreTokens()) {

      String myEl = myToken.nextToken();
      String otherEl = otherToken.nextToken();

      try {
        int myInt = Integer.parseInt(myEl);
        int otherInt = Integer.parseInt(otherEl);
        if (myInt != otherInt)
          return false;
      } catch (NumberFormatException nfe) {
        throw new ClassCastException("Invalid OID");
      }
    }
    return true;

  }

  public boolean equals(Object o) {
    if (compareTo(o) == 0)
      return true;
    return false;
  }

  /**
   * Compares two OIDs. returns: 1 if this is greater than o returns: 0 if this
   * is equal to o returns: -1 if this is less than o
   */
  public int compareTo(Object o) {
    OID otherOid = (OID) o;
    int result = 0;
    StringTokenizer myToken = new StringTokenizer(toString(), ".");
    StringTokenizer otherToken = new StringTokenizer(otherOid.toString(), ".");

    while (myToken.hasMoreTokens() && otherToken.hasMoreTokens()) {

      String myEl = myToken.nextToken().trim();
      String otherEl = otherToken.nextToken().trim();

      try {
        int myInt = Integer.parseInt(myEl);
        int otherInt = Integer.parseInt(otherEl);
        if (myInt > otherInt)
          return 1;
        if (myInt < otherInt)
          return -1;
      } catch (NumberFormatException nfe) {
        throw new ClassCastException("Invalid OID");
      }
    }
    if (result == 0) { // So far they are equal...
      if (myToken.hasMoreTokens())
        result += 1;
      if (otherToken.hasMoreTokens())
        result -= 1;
    }

    return result;
  }

  /**
   * Define the OID.
   */
  public void setOID(String oid) {
    this.oid = oid;
  }

  /**
   * Returns the OID as a long[].
   */
  public long[] toArray() throws NumberFormatException {
    StringTokenizer strToken = new StringTokenizer(oid, ".");
    long oidArray[] = new long[strToken.countTokens()];
    int i = 0;
    while (strToken.hasMoreTokens())
      oidArray[i++] = Long.parseLong(strToken.nextToken());
    return oidArray;
  }

  public Object toJavaValue() {
    return oid;
  }

  public String toString() {
    return oid;
  }

  public static void main(String arg[]) {
    OID o1 = new OID(arg[0]);
    OID o2 = new OID(arg[1]);
    System.out.println(o1.indexOf(o2));
  }

  /**
   * Returns a hash code value for the object. This method is supported for the
   * benefit of hashtables such as those provided by
   * <code>java.util.Hashtable</code>.
   * <p>
   * The general contract of <code>hashCode</code> is:
   * <ul>
   * <li>Whenever it is invoked on the same object more than once during an
   * execution of a Java application, the <tt>hashCode</tt> method must
   * consistently return the same integer, provided no information used in
   * <tt>equals</tt> comparisons on the object is modified. This integer need
   * not remain consistent from one execution of an application to another
   * execution of the same application.
   * <li>If two objects are equal according to the <tt>equals(Object)</tt>
   * method, then calling the <code>hashCode</code> method on each of the two
   * objects must produce the same integer result.
   * <li>It is <em>not</em> required that if two objects are unequal
   * according to the {@link java.lang.Object#equals(java.lang.Object)}method,
   * then calling the <tt>hashCode</tt> method on each of the two objects must
   * produce distinct integer results. However, the programmer should be aware
   * that producing distinct integer results for unequal objects may improve the
   * performance of hashtables.
   * </ul>
   * <p>
   * As much as is reasonably practical, the hashCode method defined by class
   * <tt>Object</tt> does return distinct integers for distinct objects. (This
   * is typically implemented by converting the internal address of the object
   * into an integer, but this implementation technique is not required by the
   * Java <font size="-2"> <sup>TM </sup> </font> programming language.)
   * 
   * @return a hash code value for this object.
   * @see java.lang.Object#equals(java.lang.Object)
   * @see java.util.Hashtable
   */
  public int hashCode() {
    return toString().hashCode();
  }

}