/*
 * $Id: Choice.java 3 2004-08-03 10:42:11Z rlopes $
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

/**
 * This class represents a CHOICE in a MIB tree.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class Choice extends Macro {

  ArrayList choiceList = new ArrayList();

  static public class Entry {
    String identifier;

    Syntax syntax;

    public Entry(String identifier, Syntax syntax) {
      this.identifier = identifier;
      this.syntax = syntax;
    }

    public String getIdentifier() {
      return identifier;
    }

    public Syntax getSyntax() {
      return syntax;
    }

  }

  /**
   * Constructor
   */
  public Choice() {
    super();
  }

  /**
   * Checks if the provided MibTC has the same label.
   * 
   * @return true if the provided MibTC has the same name.
   */
  public boolean equals(Object ch) {
    if (!(ch instanceof Choice))
      return false;
    return label.equals(((Choice) ch).getLabel());
  }

  /**
   * Set the choice list, for example: CHOICE { number INTEGER, empty NULL }.
   */
  public void setChoiceList(ArrayList list) {
    this.choiceList = list;
  }

  public ArrayList getChoiceList() {
    return choiceList;
  }

  public void addCoiceElement(String identifier, Syntax syntax) {
    Choice.Entry entry = new Choice.Entry(identifier, syntax);
    choiceList.add(entry);
  }

  public Enumeration elements() {
    return Collections.enumeration(choiceList);
  }

  public String toString() {
    return getLabel();
  }
}

