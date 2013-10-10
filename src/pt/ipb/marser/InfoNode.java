/*
 * $Id: InfoNode.java 3 2004-08-03 10:42:11Z rlopes $
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
public class InfoNode extends MibNode {
  String status = null;

  String reference = null;

  public InfoNode() {
  }

  public InfoNode(String label, int subId) {
    super(label, subId);
  }

  public InfoNode(String label, int subId, boolean allowsChildren) {
    super(label, subId, allowsChildren);
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String l) {
    this.status = l;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String l) {
    this.reference = l;
  }

}