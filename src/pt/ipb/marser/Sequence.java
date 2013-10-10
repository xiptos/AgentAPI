/*
 * Sequence.java
 *
 * Created on 13 de Janeiro de 2003, 21:45
 * $Id: Sequence.java 3 2004-08-03 10:42:11Z rlopes $
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
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class Sequence {
  ArrayList list = new ArrayList();

  static public class Entry {
    String identifier;

    Syntax type;

    public Entry(String identifier, Syntax type) {
      this.identifier = identifier;
      this.type = type;
    }

    public String getIdentifier() {
      return identifier;
    }

    public Syntax getType() {
      return type;
    }

  }

  /** Creates a new instance of Sequence */
  public Sequence() {
  }

  /**
   * Set the enum list, for example: INTEGER { one(1), two(2), three(3) }.
   */
  public void setNamedTypeList(ArrayList list) {
    this.list = list;
  }

  public ArrayList getNamedTypeList() {
    return list;
  }

  public void addNamedTypeElement(String identifier, Syntax type) {
    Sequence.Entry entry = new Sequence.Entry(identifier, type);
    list.add(entry);
  }

  public void add(Sequence.Entry entry) {
    list.add(entry);
  }

  public Enumeration elements() {
    return Collections.enumeration(list);
  }

  public String toString() {
    StringBuffer str = new StringBuffer("SEQUENCE { \n");
    for (Iterator i = list.iterator(); i.hasNext();) {
      Sequence.Entry entry = (Sequence.Entry) i.next();
      str.append(entry.getIdentifier());
      str.append(entry.getType());
      str.append("\n");
      if (i.hasNext())
        str.append(", ");
    }
    str.append(" }\n");
    return str.toString();
  }

}