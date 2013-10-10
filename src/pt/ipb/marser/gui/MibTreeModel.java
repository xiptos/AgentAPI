/*
 * $Id: MibTreeModel.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.marser.gui;

import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import pt.ipb.marser.MibModule;
import pt.ipb.marser.MibNode;
import pt.ipb.marser.MibOps;
import pt.ipb.marser.MibTC;
import pt.ipb.marser.MibTrap;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibTreeModel implements TreeModel {
  MibOps ops = null;

  /** Listeners. */
  protected EventListenerList listenerList = new EventListenerList();

  public MibTreeModel() {
    super();
    updateMibOps(new MibOps());
  }

  public MibTreeModel(MibOps ops) {
    super();
    updateMibOps(ops);
  }

  public void updateMibOps(MibOps ops) {
    this.ops = ops;
    refresh();
  }

  /**
   * Returns the root of the tree. Returns null only if the tree has no nodes.
   * 
   * @return the root of the tree
   */
  public Object getRoot() {
    return ops;
  }

  /**
   * Returns the index of child in parent.
   */
  public int getIndexOfChild(Object parent, Object child) {
    Object childs[] = children(parent);
    if (childs == null)
      return -1;

    for (int i = 0; i < childs.length; i++) {
      if (childs[i] == child) {
        return i;
      }
    }
    return -1;
  }

  Object[] children(Object parent) {
    Object childs[] = null;
    if (parent instanceof MibOps) {
      childs = ((MibOps) parent).getMibModules();

    } else if (parent instanceof MibModule) {
      Vector v = new Vector();
      if (((MibModule) parent).getRoot() != null)
        v.addElement(((MibModule) parent).getRoot());

      Enumeration tcs = ((MibModule) parent).mibTCs();
      if (tcs.hasMoreElements())
        v.addElement(new MibArray("TCs", tcs));

      Enumeration traps = ((MibModule) parent).traps();
      if (traps.hasMoreElements())
        v.addElement(new MibArray("Traps", traps));

      childs = v.toArray();

    } else if (parent instanceof MibArray) {
      MibArray ma = (MibArray) parent;
      if (ma.toString().equals("TCs")) {
        MibTC[] tcs = new MibTC[ma.size()];
        childs = ma.toArray(tcs);
      } else if (ma.toString().equals("Traps")) {
        MibTrap[] tcs = new MibTrap[ma.size()];
        childs = ma.toArray(tcs);
      }

    } else if (parent instanceof MibNode) {
      Vector auxv = new Vector();
      for (Enumeration e = ((MibNode) parent).children(); e.hasMoreElements();) {
        auxv.addElement(e.nextElement());
      }
      if (((MibNode) parent).isTableEntry()) {
        for (Enumeration e = ((MibNode) parent).indexes(); e.hasMoreElements();) {
          String indexName = (String) e.nextElement();
          boolean exists = false;
          for (Enumeration e2 = auxv.elements(); e2.hasMoreElements();) {
            MibNode node = (MibNode) e2.nextElement();
            if (indexName.equals(node.getLabel())) {
              exists = true;
              break;
            }
          }
          if (!exists) {
            MibNode node = ((MibNode) parent).getModule().getNode(indexName);
            if (node == null)
              node = ((MibNode) parent).getModule().lookupImports(indexName);
            auxv.addElement(node);
          }
        }
        for (Enumeration e = ((MibNode) parent).impliedIndexes(); e
            .hasMoreElements();) {
          String indexName = (String) e.nextElement();
          boolean exists = false;
          for (Enumeration e2 = auxv.elements(); e.hasMoreElements();) {
            MibNode node = (MibNode) e.nextElement();
            if (indexName.equals(node.getLabel())) {
              exists = true;
              break;
            }
          }
          if (!exists) {
            auxv.addElement(((MibNode) parent).getModule().getNode(indexName));
          }
        }
        java.util.Collections.sort(auxv, new OIDComparator());
      }
      childs = auxv.toArray();

    }
    return childs;
  }

  final class OIDComparator implements java.util.Comparator {
    public int compare(Object o1, Object o2) {
      MibNode n1 = (MibNode) o1;
      MibNode n2 = (MibNode) o2;
      return n1.getOID().compareTo(n2.getOID());
    }
  }

  public Object getChild(Object parent, int index) {
    Object childs[] = children(parent);
    if (childs == null)
      return null;
    return childs[index];
  }

  public int getChildCount(Object parent) {
    Object childs[] = children(parent);
    int l = 0;
    if (childs != null)
      l = childs.length;
    return l;
  }

  public boolean isLeaf(Object n) {
    if (n instanceof MibTrap) {
      return true;
    } else if (n instanceof MibTC) {
      return true;
    } else if (n instanceof MibNode) {
      MibNode node = (MibNode) n;
      return node.isLeaf();
    } else {
      return false;
    }
  }

  /**
   * This sets the user object of the TreeNode identified by path and posts a
   * node changed. If you use custom user objects in the TreeModel you're going
   * to need to subclass this and set the user object of the changed node to
   * something meaningful.
   */
  public void valueForPathChanged(TreePath path, Object newValue) {
    System.out.println("Path changed: " + path);
  }

  //
  //  Events
  //

  /**
   * Adds a listener for the TreeModelEvent posted after the tree changes.
   * 
   * @see #removeTreeModelListener
   * @param l
   *          the listener to add
   */
  public void addTreeModelListener(TreeModelListener l) {
    listenerList.add(TreeModelListener.class, l);
  }

  /**
   * Removes a listener previously added with <B>addTreeModelListener() </B>.
   * 
   * @see #addTreeModelListener
   * @param l
   *          the listener to remove
   */
  public void removeTreeModelListener(TreeModelListener l) {
    listenerList.remove(TreeModelListener.class, l);
  }

  /*
   * Notify all listeners that have registered interest for notification on this
   * event type. The event instance is lazily created using the parameters
   * passed into the fire method.
   * 
   * @see EventListenerList
   */
  protected void fireTreeNodesChanged(Object source, TreePath path) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        // Lazily create the event:
        if (e == null)
          e = new TreeModelEvent(source, path);
        ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
      }
    }
  }

  /*
   * Notify all listeners that have registered interest for notification on this
   * event type. The event instance is lazily created using the parameters
   * passed into the fire method.
   * 
   * @see EventListenerList
   */
  protected void fireTreeNodesInserted(Object source, Object[] path,
      int[] childIndices, Object[] children) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        // Lazily create the event:
        if (e == null)
          e = new TreeModelEvent(source, path, childIndices, children);
        ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
      }
    }
  }

  /*
   * Notify all listeners that have registered interest for notification on this
   * event type. The event instance is lazily created using the parameters
   * passed into the fire method.
   * 
   * @see EventListenerList
   */
  protected void fireTreeNodesRemoved(Object source, Object[] path,
      int[] childIndices, Object[] children) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        // Lazily create the event:
        if (e == null)
          e = new TreeModelEvent(source, path, childIndices, children);
        ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
      }
    }
  }

  /*
   * Notify all listeners that have registered interest for notification on this
   * event type. The event instance is lazily created using the parameters
   * passed into the fire method.
   * 
   * @see EventListenerList
   */
  protected void fireTreeStructureChanged(Object source, TreePath path) {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        // Lazily create the event:
        if (e == null)
          e = new TreeModelEvent(source, path);
        ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
      }
    }
  }

  /**
   * Return an array of all the listeners of the given type that were added to
   * this model.
   * 
   * @return all of the objects recieving <em>listenerType</em> notifications
   *         from this model
   * 
   * @since 1.3
   */
  public EventListener[] getListeners(Class listenerType) {
    return listenerList.getListeners(listenerType);
  }

  public void refresh() {
    fireTreeStructureChanged(this, new TreePath(ops));
  }

  public MibOps getMibOps() {
    return ops;
  }
} // End of class GHTreeModel

