/*
 * $Id: EventListenerList.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EventListener;

/**
 * Based on javax.swing.event.EventListenerList.
 */
public class EventListenerList implements Serializable {
  private final static Object[] NULL_ARRAY = new Object[0];

  protected transient Object[] listenerList = NULL_ARRAY;

  public Object[] getListenerList() {
    return listenerList;
  }

  public EventListener[] getListeners(Class t) {
    Object[] lList = listenerList;
    int n = getListenerCount(lList, t);
    EventListener[] result = (EventListener[]) Array.newInstance(t, n);
    int j = 0;
    for (int i = lList.length - 2; i >= 0; i -= 2) {
      if (lList[i] == t) {
        result[j++] = (EventListener) lList[i + 1];
      }
    }
    return result;
  }

  public int getListenerCount() {
    return listenerList.length / 2;
  }

  public int getListenerCount(Class t) {
    Object[] lList = listenerList;
    return getListenerCount(lList, t);
  }

  private int getListenerCount(Object[] list, Class t) {
    int count = 0;
    for (int i = 0; i < list.length; i += 2) {
      if (t == (Class) list[i])
        count++;
    }
    return count;
  }

  public synchronized void add(Class t, EventListener l) {
    if (l == null) {
      return;
    }
    if (!t.isInstance(l)) {
      throw new IllegalArgumentException("Listener " + l + " is not of type "
          + t);
    }
    if (listenerList == NULL_ARRAY) {
      listenerList = new Object[] { t, l };
    } else {
      // Otherwise copy the array and add the new listener
      int i = listenerList.length;
      Object[] tmp = new Object[i + 2];
      System.arraycopy(listenerList, 0, tmp, 0, i);

      tmp[i] = t;
      tmp[i + 1] = l;

      listenerList = tmp;
    }
  }

  public synchronized void remove(Class t, EventListener l) {
    if (l == null) {
      return;
    }
    if (!t.isInstance(l)) {
      throw new IllegalArgumentException("Listener " + l + " is not of type "
          + t);
    }
    // Is l on the list?
    int index = -1;
    for (int i = listenerList.length - 2; i >= 0; i -= 2) {
      if ((listenerList[i] == t) && (listenerList[i + 1].equals(l) == true)) {
        index = i;
        break;
      }
    }

    // If so, remove it
    if (index != -1) {
      Object[] tmp = new Object[listenerList.length - 2];
      // Copy the list up to index
      System.arraycopy(listenerList, 0, tmp, 0, index);
      // Copy from two past the index, up to
      // the end of tmp (which is two elements
      // shorter than the old list)
      if (index < tmp.length)
        System.arraycopy(listenerList, index + 2, tmp, index, tmp.length
            - index);
      // set the listener array to the new array or null
      listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
    }
  }

  // Serialization support.
  private void writeObject(ObjectOutputStream s) throws IOException {
    Object[] lList = listenerList;
    s.defaultWriteObject();
    // Save the non-null event listeners:
    for (int i = 0; i < lList.length; i += 2) {
      Class t = (Class) lList[i];
      EventListener l = (EventListener) lList[i + 1];
      if ((l != null) && (l instanceof Serializable)) {
        s.writeObject(t.getName());
        s.writeObject(l);
      }
    }

    s.writeObject(null);
  }

  private void readObject(ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    listenerList = NULL_ARRAY;
    s.defaultReadObject();
    Object listenerTypeOrNull;

    while (null != (listenerTypeOrNull = s.readObject())) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      EventListener l = (EventListener) s.readObject();
      add(Class.forName((String) listenerTypeOrNull, true, cl), l);
    }
  }

  public String toString() {
    Object[] lList = listenerList;
    String s = "EventListenerList: ";
    s += lList.length / 2 + " listeners: ";
    for (int i = 0; i <= lList.length - 2; i += 2) {
      s += " type " + ((Class) lList[i]).getName();
      s += " listener " + lList[i + 1];
    }
    return s;
  }
}