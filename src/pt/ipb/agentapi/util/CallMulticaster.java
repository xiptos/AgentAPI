/*
 * $Id: CallMulticaster.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventListener;

/**
 * Class to invoke all the Called classes.
 * 
 * <pre><code>
 * 
 *  
 *   
 *    public myObject extends Object {
 *        Called called = null;
 *   
 *        public void addCalled(Called l) {
 *   	   called = CallMulticaster.add(called, l);
 *        }
 *        public void removeCalled(Called l) {
 *     	   called = CallMulticaster.remove(called, l);
 *        }
 *        public void processCall() {
 *            if (called != null) {
 *                called.callback();
 *            }         
 *        }
 *    }
 *    
 *   
 *  
 * </code></pre>
 *  
 */
public class CallMulticaster implements Called {

  protected final EventListener a, b;

  /**
   * Creates an event multicaster instance which chains listener-a with
   * listener-b. Input parameters <code>a</code> and <code>b</code> should
   * not be <code>null</code>, though implementations may vary in choosing
   * whether or not to throw <code>NullPointerException</code> in that case.
   * 
   * @param a
   *          listener-a
   * @param b
   *          listener-b
   */
  protected CallMulticaster(EventListener a, EventListener b) {
    this.a = a;
    this.b = b;
  }

  /**
   * Removes a listener from this multicaster and returns the resulting
   * multicast listener.
   * 
   * @param oldl
   *          the listener to be removed
   */
  protected EventListener remove(EventListener oldl) {
    if (oldl == a)
      return b;
    if (oldl == b)
      return a;
    EventListener a2 = removeInternal(a, oldl);
    EventListener b2 = removeInternal(b, oldl);
    if (a2 == a && b2 == b) {
      return this; // it's not here
    }
    return addInternal(a2, b2);
  }

  /**
   * Adds message-listener-a with message-listener-b and returns the resulting
   * multicast listener.
   * 
   * @param a
   *          message-listener-a
   * @param b
   *          message-listener-b
   */
  public static Called add(Called a, Called b) {
    return (Called) addInternal(a, b);
  }

  /**
   * Removes the old message-listener from message-listener-l and returns the
   * resulting multicast listener.
   * 
   * @param l
   *          component-listener-l
   * @param oldl
   *          the component-listener being removed
   */
  public static Called remove(Called l, Called oldl) {
    return (Called) removeInternal(l, oldl);
  }

  public void callback() {
    ((Called) a).callback();
    ((Called) b).callback();
  }

  /**
   * Returns the resulting multicast listener from adding listener-a and
   * listener-b together. If listener-a is null, it returns listener-b; If
   * listener-b is null, it returns listener-a If neither are null, then it
   * creates and returns a new EventMulticaster instance which chains a with b.
   * 
   * @param a
   *          event listener-a
   * @param b
   *          event listener-b
   */
  protected static EventListener addInternal(EventListener a, EventListener b) {
    if (a == null)
      return b;
    if (b == null)
      return a;
    return new CallMulticaster(a, b);
  }

  /**
   * Returns the resulting multicast listener after removing the old listener
   * from listener-l. If listener-l equals the old listener OR listener-l is
   * null, returns null. Else if listener-l is an instance of TEventMulticaster,
   * then it removes the old listener from it. Else, returns listener l.
   * 
   * @param l
   *          the listener being removed from
   * @param oldl
   *          the listener being removed
   */
  protected static EventListener removeInternal(EventListener l,
      EventListener oldl) {
    if (l == oldl || l == null) {
      return null;
    } else if (l instanceof CallMulticaster) {
      return ((CallMulticaster) l).remove(oldl);
    } else {
      return l; // it's not here
    }
  }

  /*
   * Serialization support.
   */

  protected void saveInternal(ObjectOutputStream s, String k)
      throws IOException {
    if (a instanceof CallMulticaster) {
      ((CallMulticaster) a).saveInternal(s, k);
    } else if (a instanceof Serializable) {
      s.writeObject(k);
      s.writeObject(a);
    }

    if (b instanceof CallMulticaster) {
      ((CallMulticaster) b).saveInternal(s, k);
    } else if (b instanceof Serializable) {
      s.writeObject(k);
      s.writeObject(b);
    }
  }

  protected static void save(ObjectOutputStream s, String k, EventListener l)
      throws IOException {
    if (l == null) {
      return;
    } else if (l instanceof CallMulticaster) {
      ((CallMulticaster) l).saveInternal(s, k);
    } else if (l instanceof Serializable) {
      s.writeObject(k);
      s.writeObject(l);
    }
  }
}