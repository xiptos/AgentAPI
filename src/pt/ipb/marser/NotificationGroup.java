/*
 * $Id: NotificationGroup.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class NotificationGroup extends InfoNode {
  Vector notifications = new Vector();

  public NotificationGroup() {
  }

  public NotificationGroup(String label, int subId) {
    super(label, subId);
  }

  public void addNotification(String not) {
    notifications.addElement(not);
  }

  public Enumeration notifications() {
    return notifications.elements();
  }

}