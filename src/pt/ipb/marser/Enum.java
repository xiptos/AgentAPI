/*
 * $Id: Enum.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * This class represents an enumerated INTEGER.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class Enum {

  ArrayList list = new ArrayList();

  static public class Entry {
    String identifier;

    String i;

    public Entry(String identifier, String i) {
      this.identifier = identifier;
      this.i = i;
    }

    public String getIdentifier() {
      return identifier;
    }

    public String getNumber() {
      return i;
    }

  }

  /**
   * Constructor
   */
  public Enum() {
  }

  /**
   * Set the enum list, for example: INTEGER { one(1), two(2), three(3) }.
   */
  public void setEnumList(ArrayList list) {
    this.list = list;
  }

  public ArrayList getEnumlList() {
    return list;
  }

  public void addEnumElement(String identifier, String i)
      throws InvalidTypeException {
    if ("0".equals(i))
      throw new InvalidTypeException(InvalidTypeException.USEOFZERO);
    Enum.Entry entry = new Enum.Entry(identifier, i);
    list.add(entry);
  }

  public void add(Enum.Entry entry) {
    list.add(entry);
  }

  public Enumeration elements() {
    return Collections.enumeration(list);
  }

  public String toString() {
    StringBuffer str = new StringBuffer("{ ");
    for (Iterator i = list.iterator(); i.hasNext();) {
      Enum.Entry entry = (Enum.Entry) i.next();
      str.append(entry.getIdentifier());
      str.append(entry.getNumber());
      if (i.hasNext())
        str.append(", ");
    }
    str.append(" }\n");
    return str.toString();
  }
}

