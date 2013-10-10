/*
 * $Id: MibNode.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

import pt.ipb.snmp.type.smi.OID;

/**
 * A node base class for representing MIB structure. This class is based on the
 * javax.swing.tree.MibNode.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibNode extends Macro implements Cloneable {

  int subId;

  /**
   * An enumeration that is always empty. This is used when an enumeration of a
   * leaf node's children is requested.
   */
  static public final Enumeration EMPTY_ENUMERATION = new Enumeration() {
    public boolean hasMoreElements() {
      return false;
    }

    public Object nextElement() {
      throw new NoSuchElementException("No more elements");
    }
  };

  /** this node's parent, or null if this node has no parent */
  protected MibNode parent;

  /** array of children, may be null if this node has no children */
  protected Vector children;

  /** true if the node is able to have children */
  protected boolean allowsChildren;

  public MibNode() {
    this(null, -1, true);
  }

  public MibNode(String label) {
    this(label, -1, true);
  }

  /**
   * Creates a tree node that has no parent and no children, but which allows
   * children.
   */
  public MibNode(String label, int subId) {
    this(label, subId, true);
  }

  public MibNode(String label, int subId, boolean allowsChildren) {
    super(label);
    parent = null;
    setSubId(subId);
    setAllowsChildren(allowsChildren);
  }

  //
  //  Primitives
  //

  /**
   * Removes <code>newChild</code> from its present parent (if it has a
   * parent), sets the child's parent to this node, and then adds the child to
   * this node's child array at index <code>childIndex</code>.
   * <code>newChild</code> must not be null and must not be an ancestor of
   * this node.
   *  
   */
  public void insert(MibNode newChild, int childIndex) {
    if (!allowsChildren) {
      throw new IllegalStateException("node does not allow children");
    } else if (newChild == null) {
      throw new IllegalArgumentException("new child is null");
    } else if (isNodeAncestor(newChild)) {
      throw new IllegalArgumentException("new child is an ancestor");
    }

    MibNode oldParent = (MibNode) newChild.getParent();

    if (oldParent != null) {
      oldParent.remove(newChild);
    }
    newChild.setParent(this);
    if (children == null) {
      children = new Vector();
    }
    children.insertElementAt(newChild, childIndex);
  }

  /**
   * Removes the child at the specified index from this node's children and sets
   * that node's parent to null.
   */
  public void remove(int childIndex) {
    MibNode child = (MibNode) getChildAt(childIndex);
    children.removeElementAt(childIndex);
    child.setParent(null);
  }

  /**
   * Sets this node's parent to <code>newParent</code> but does not change the
   * parent's child array. This method is called from <code>insert()</code>
   * and <code>remove()</code> to reassign a child's parent, it should not be
   * messaged from anywhere else.
   */
  public void setParent(MibNode newParent) {
    parent = newParent;
  }

  /**
   * Returns this node's parent or null if this node has no parent.
   */
  public MibNode getParent() {
    return parent;
  }

  /**
   * Returns the child at the specified index in this node's child array.
   */
  public MibNode getChildAt(int index) {
    if (children == null) {
      throw new ArrayIndexOutOfBoundsException("node has no children");
    }
    return (MibNode) children.elementAt(index);
  }

  public MibNode getChildNamed(String shortName) {
    if (children == null) {
      throw new ArrayIndexOutOfBoundsException("node has no children");
    }
    for (Enumeration e = children.elements(); e.hasMoreElements();) {
      MibNode c = (MibNode) e.nextElement();
      if (shortName.equals(c.getLabel())) {
        return c;
      }
    }
    return null;
  }

  /**
   * Returns the number of children of this node.
   */
  public int getChildCount() {
    if (children == null) {
      return 0;
    } else {
      return children.size();
    }
  }

  /**
   * Returns the index of the specified child in this node's child array. If the
   * specified node is not a child of this node, returns <code>-1</code>.
   */
  public int getIndex(MibNode aChild) {
    if (aChild == null) {
      throw new IllegalArgumentException("argument is null");
    }

    if (!isNodeChild(aChild)) {
      return -1;
    }
    return children.indexOf(aChild);
  }

  /**
   * Creates and returns a forward-order enumeration of this node's children.
   */
  public Enumeration children() {
    if (children == null) {
      return EMPTY_ENUMERATION;
    } else {
      return children.elements();
    }
  }

  /**
   * Determines whether or not this node is allowed to have children. If
   * <code>allows</code> is false, all of this node's children are removed.
   * <p>
   * Note: By default, a node allows children.
   */
  public void setAllowsChildren(boolean allows) {
    if (allows != allowsChildren) {
      allowsChildren = allows;
      if (!allowsChildren) {
        removeAllChildren();
      }
    }
  }

  /**
   * Returns true if this node is allowed to have children.
   */
  public boolean allowsChildren() {
    return allowsChildren;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public void setSubId(int subId) {
    this.subId = subId;
  }

  public int getSubId() {
    return subId;
  }

  /**
   * Removes the subtree rooted at this node from the tree, giving this node a
   * null parent. Does nothing if this node is the root of its tree.
   */
  public void removeFromParent() {
    MibNode parent = (MibNode) getParent();
    if (parent != null) {
      parent.remove(this);
    }
  }

  /**
   * Removes <code>aChild</code> from this node's child array, giving it a
   * null parent.
   */
  public void remove(MibNode aChild) {
    if (aChild == null) {
      throw new IllegalArgumentException("argument is null");
    }

    if (!isNodeChild(aChild)) {
      throw new IllegalArgumentException("argument is not a child");
    }
    remove(getIndex(aChild));
  }

  /**
   * Removes all of this node's children, setting their parents to null. If this
   * node has no children, this method does nothing.
   */
  public void removeAllChildren() {
    for (int i = getChildCount() - 1; i >= 0; i--) {
      remove(i);
    }
  }

  /**
   * Removes <code>newChild</code> from its parent and makes it a child of
   * this node by adding it to the end of this node's child array.
   */
  public void add(MibNode newChild) {
    if (newChild != null && newChild.getParent() == this)
      insert(newChild, getChildCount() - 1);
    else
      insert(newChild, getChildCount());
    java.util.Collections.sort(children, subIdComparator());
  }

  public java.util.Comparator subIdComparator() {
    return new MibNode.SubIdComparator();
  }

  final class SubIdComparator implements java.util.Comparator {
    public int compare(Object o1, Object o2) {
      MibNode n1 = (MibNode) o1;
      MibNode n2 = (MibNode) o2;
      return n1.getSubId() - n2.getSubId();
    }

  }

  //
  // MIB queries
  //

  public boolean isTable() {
    return false;
  }

  public boolean isTableEntry() {
    return false;
  }

  public boolean isTableColumn() {
    return false;
  }

  public boolean isScalar() {
    return false;
  }

  public boolean isIndex() {
    if (getParent() != null) {
      for (Enumeration e = getParent().indexes(); e.hasMoreElements();) {
        String node = (String) e.nextElement();
        if (getLabel().equals(node))
          return true;
      }
    }
    return false;
  }

  public boolean isImpliedIndex() {
    if (getParent() != null) {
      for (Enumeration e = getParent().impliedIndexes(); e.hasMoreElements();) {
        String node = (String) e.nextElement();
        if (getLabel().equals(node))
          return true;
      }
    }
    return false;
  }

  public Enumeration indexes() {
    return EMPTY_ENUMERATION;
  }

  public Enumeration impliedIndexes() {
    return EMPTY_ENUMERATION;
  }

  public String getUnits() {
    return "";
  }

  public String getDefVal() {
    return "";
  }

  public String getAccessStr() {
    return "";
  }

  public int getAccess() {
    return MibOps.NO_VAL;
  }

  public Syntax getSyntax() {
    return null;
  }

  public String getStatus() {
    return "";
  }

  public boolean isReadable() {
    return (getAccess() == MibOps.READ_ONLY || getAccess() == MibOps.READ_WRITE || getAccess() == MibOps.READ_CREATE);
  }

  //
  // OID queries
  //
  /**
   * Get the numbered OID as a String.
   */
  public String getNumberedOIDString() {
    return getOID().toString();
  }

  public String getOIDString() {
    StringBuffer str = new StringBuffer();
    MibNode[] path = getPath();
    for (int i = 0; i < path.length; i++) {
      MibNode p = path[i];
      str.append(".");
      str.append(p.getLabel());
    }
    //System.out.println(getLabel() + " - " + str.toString());
    return str.toString();
  }

  /**
   * Get the OID.
   */
  public OID getOID() {
    StringBuffer str = new StringBuffer();
    MibNode[] path = getPath();
    int oid[] = new int[path.length];
    for (int i = 0; i < path.length; i++) {
      MibNode p = path[i];
      oid[i] = p.getSubId();
    }
    try {
      return new OID(oid);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  //
  //  Tree Queries
  //

  /**
   * Returns true if <code>anotherNode</code> is an ancestor of this node --
   * if it is this node, this node's parent, or an ancestor of this node's
   * parent. (Note that a node is considered an ancestor of itself.) If
   * <code>anotherNode</code> is null, this method returns false.
   */
  public boolean isNodeAncestor(MibNode anotherNode) {
    if (anotherNode == null) {
      return false;
    }

    MibNode ancestor = this;

    do {
      if (ancestor == anotherNode) {
        return true;
      }
    } while ((ancestor = ancestor.getParent()) != null);

    return false;
  }

  /**
   * Returns true if <code>anotherNode</code> is a descendant of this node --
   * if it is this node, one of this node's children, or a descendant of one of
   * this node's children. Note that a node is considered a descendant of
   * itself. If <code>anotherNode</code> is null, returns false.
   */
  public boolean isNodeDescendant(MibNode anotherNode) {
    if (anotherNode == null)
      return false;

    return anotherNode.isNodeAncestor(this);
  }

  /**
   * Returns the nearest common ancestor to this node and <code>aNode</code>.
   * Returns null, if no such ancestor exists -- if this node and
   * <code>aNode</code> are in different trees or if <code>aNode</code> is
   * null. A node is considered an ancestor of itself.
   */
  public MibNode getSharedAncestor(MibNode aNode) {
    if (aNode == this) {
      return this;
    } else if (aNode == null) {
      return null;
    }

    int level1, level2, diff;
    MibNode node1, node2;

    level1 = getLevel();
    level2 = aNode.getLevel();

    if (level2 > level1) {
      diff = level2 - level1;
      node1 = aNode;
      node2 = this;
    } else {
      diff = level1 - level2;
      node1 = this;
      node2 = aNode;
    }

    // Go up the tree until the nodes are at the same level
    while (diff > 0) {
      node1 = node1.getParent();
      diff--;
    }

    // Move up the tree until we find a common ancestor. Since we know
    // that both nodes are at the same level, we won't cross paths
    // unknowingly (if there is a common ancestor, both nodes hit it in
    // the same iteration).

    do {
      if (node1 == node2) {
        return node1;
      }
      node1 = node1.getParent();
      node2 = node2.getParent();
      // only need to check one -- they're at the
      // same level so if one is null, the other is
    } while (node1 != null);

    if (node1 != null || node2 != null) {
      throw new Error("nodes should be null");
    }

    return null;
  }

  public MibNode getAncestorMatching(MibNode aNode) {
    MibNode mn1 = this;
    while (mn1 != null) {
      MibNode mn2 = aNode;
      while (mn2 != null) {
        if (mn1.getLabel().equals(mn2.getLabel()))
          return mn1;
        mn2 = mn2.getParent();
      }
      mn1 = mn1.getParent();
    }
    return null;
  }

  /**
   * Returns true if and only if <code>aNode</code> is in the same tree as
   * this node. Returns false if <code>aNode</code> is null.
   */
  public boolean isNodeRelated(MibNode aNode) {
    return (aNode != null) && (getRoot() == aNode.getRoot());
  }

  /**
   * Returns the depth of the tree rooted at this node -- the longest distance
   * from this node to a leaf. If this node has no children, returns 0.
   */
  public int getDepth() {
    Object last = null;
    Enumeration enum = breadthFirstEnumeration();

    while (enum.hasMoreElements()) {
      last = enum.nextElement();
    }

    if (last == null) {
      throw new Error("nodes should be null");
    }

    return ((MibNode) last).getLevel() - getLevel();
  }

  /**
   * Returns the number of levels above this node -- the distance from the root
   * to this node. If this node is the root, returns 0.
   */
  public int getLevel() {
    MibNode ancestor;
    int levels = 0;

    ancestor = this;
    while ((ancestor = ancestor.getParent()) != null) {
      levels++;
    }

    return levels;
  }

  /**
   * Returns the path from the root, to get to this node. The last element in
   * the path is this node.
   */
  public MibNode[] getPath() {
    return getPathToRoot(this, 0);
  }

  /**
   * Builds the parents of node up to and including the root node, where the
   * original node is the last element in the returned array. The length of the
   * returned array gives the node's depth in the tree.
   */
  protected MibNode[] getPathToRoot(MibNode aNode, int depth) {
    MibNode[] retNodes;

    // Check for null, in case someone passed in a null node, or
    // they passed in an element that isn't rooted at root.
    if (aNode == null) {
      if (depth == 0)
        return null;
      else
        retNodes = new MibNode[depth];
    } else {
      depth++;
      retNodes = getPathToRoot(aNode.getParent(), depth);
      retNodes[retNodes.length - depth] = aNode;
    }
    return retNodes;
  }

  /**
   * Returns the root of the tree that contains this node. The root is the
   * ancestor with a null parent.
   */
  public MibNode getRoot() {
    MibNode ancestor = this;
    MibNode previous;

    do {
      previous = ancestor;
      ancestor = ancestor.getParent();
    } while (ancestor != null);

    return previous;
  }

  /**
   * Returns true if this node is the root of the tree. The root is the only
   * node in the tree with a null parent; every tree has exactly one root.
   */
  public boolean isRoot() {
    return getParent() == null;
  }

  /**
   * Returns the node that follows this node in a preorder traversal of this
   * node's tree. Returns null if this node is the last node of the traversal.
   */
  public MibNode getNextNode() {
    if (getChildCount() == 0) {
      // No children, so look for nextSibling
      MibNode nextSibling = getNextSibling();

      if (nextSibling == null) {
        MibNode aNode = (MibNode) getParent();

        do {
          if (aNode == null) {
            return null;
          }

          nextSibling = aNode.getNextSibling();
          if (nextSibling != null) {
            return nextSibling;
          }

          aNode = (MibNode) aNode.getParent();
        } while (true);
      } else {
        return nextSibling;
      }
    } else {
      return (MibNode) getChildAt(0);
    }
  }

  /**
   * Returns the node that precedes this node in a preorder traversal of this
   * node's tree. Returns <code>null</code> if this node is the first node of
   * the traversal -- the root of the tree.
   */
  public MibNode getPreviousNode() {
    MibNode previousSibling;
    MibNode myParent = (MibNode) getParent();

    if (myParent == null) {
      return null;
    }

    previousSibling = getPreviousSibling();

    if (previousSibling != null) {
      if (previousSibling.getChildCount() == 0)
        return previousSibling;
      else
        return previousSibling.getLastLeaf();
    } else {
      return myParent;
    }
  }

  /**
   * Creates and returns an enumeration that traverses the subtree rooted at
   * this node in preorder. The first node returned by the enumeration's
   * <code>nextElement()</code> method is this node.
   * <P>
   * 
   * Modifying the tree by inserting, removing, or moving a node invalidates any
   * enumerations created before the modification.
   */
  public Enumeration preorderEnumeration() {
    return new PreorderEnumeration(this);
  }

  /**
   * Creates and returns an enumeration that traverses the subtree rooted at
   * this node in postorder. The first node returned by the enumeration's
   * <code>nextElement()</code> method is the leftmost leaf. This is the same
   * as a depth-first traversal.
   * <P>
   * 
   * Modifying the tree by inserting, removing, or moving a node invalidates any
   * enumerations created before the modification.
   */
  public Enumeration postorderEnumeration() {
    return new PostorderEnumeration(this);
  }

  /**
   * Creates and returns an enumeration that traverses the subtree rooted at
   * this node in breadth-first order. The first node returned by the
   * enumeration's <code>nextElement()</code> method is this node.
   * <P>
   * 
   * Modifying the tree by inserting, removing, or moving a node invalidates any
   * enumerations created before the modification.
   */
  public Enumeration breadthFirstEnumeration() {
    return new BreadthFirstEnumeration(this);
  }

  /**
   * Creates and returns an enumeration that traverses the subtree rooted at
   * this node in depth-first order. The first node returned by the
   * enumeration's <code>nextElement()</code> method is the leftmost leaf.
   * This is the same as a postorder traversal.
   * <P>
   * 
   * Modifying the tree by inserting, removing, or moving a node invalidates any
   * enumerations created before the modification.
   */
  public Enumeration depthFirstEnumeration() {
    return postorderEnumeration();
  }

  /**
   * Creates and returns an enumeration that follows the path from
   * <code>ancestor</code> to this node. The enumeration's
   * <code>nextElement()</code> method first returns <code>ancestor</code>,
   * then the child of <code>ancestor</code> that is an ancestor of this node,
   * and so on, and finally returns this node.
   */
  public Enumeration pathFromAncestorEnumeration(MibNode ancestor) {
    return new PathBetweenNodesEnumeration(ancestor, this);
  }

  //
  //  Child Queries
  //

  /**
   * Returns true if <code>aNode</code> is a child of this node. If
   * <code>aNode</code> is null, this method returns false.
   */
  public boolean isNodeChild(MibNode aNode) {
    boolean retval;

    if (aNode == null) {
      retval = false;
    } else {
      if (getChildCount() == 0) {
        retval = false;
      } else {
        retval = (aNode.getParent() == this);
      }
    }

    return retval;
  }

  /**
   * Returns this node's first child. If this node has no children, throws
   * NoSuchElementException.
   */
  public MibNode getFirstChild() {
    if (getChildCount() == 0) {
      throw new NoSuchElementException("node has no children");
    }
    return getChildAt(0);
  }

  /**
   * Returns this node's last child. If this node has no children, throws
   * NoSuchElementException.
   */
  public MibNode getLastChild() {
    if (getChildCount() == 0) {
      throw new NoSuchElementException("node has no children");
    }
    return getChildAt(getChildCount() - 1);
  }

  /**
   * Returns the child in this node's child array that immediately follows
   * <code>aChild</code>, which must be a child of this node. If
   * <code>aChild</code> is the last child, returns null.
   */
  public MibNode getChildAfter(MibNode aChild) {
    if (aChild == null) {
      throw new IllegalArgumentException("argument is null");
    }

    int index = getIndex(aChild); // linear search

    if (index == -1) {
      throw new IllegalArgumentException("node is not a child");
    }

    if (index < getChildCount() - 1) {
      return getChildAt(index + 1);
    } else {
      return null;
    }
  }

  /**
   * Returns the child in this node's child array that immediately precedes
   * <code>aChild</code>, which must be a child of this node. If
   * <code>aChild</code> is the first child, returns null.
   */
  public MibNode getChildBefore(MibNode aChild) {
    if (aChild == null) {
      throw new IllegalArgumentException("argument is null");
    }

    int index = getIndex(aChild); // linear search

    if (index == -1) {
      throw new IllegalArgumentException("argument is not a child");
    }

    if (index > 0) {
      return getChildAt(index - 1);
    } else {
      return null;
    }
  }

  //
  //  Sibling Queries
  //

  /**
   * Returns true if <code>anotherNode</code> is a sibling of (has the same
   * parent as) this node. A node is its own sibling. If
   * <code>anotherNode</code> is null, returns false.
   */
  public boolean isNodeSibling(MibNode anotherNode) {
    boolean retval;

    if (anotherNode == null) {
      retval = false;
    } else if (anotherNode == this) {
      retval = true;
    } else {
      MibNode myParent = getParent();
      retval = (myParent != null && myParent == anotherNode.getParent());

      if (retval && !((MibNode) getParent()).isNodeChild(anotherNode)) {
        throw new Error("sibling has different parent");
      }
    }

    return retval;
  }

  /**
   * Returns the number of siblings of this node. A node is its own sibling (if
   * it has no parent or no siblings, this method returns <code>1</code>).
   */
  public int getSiblingCount() {
    MibNode myParent = getParent();

    if (myParent == null) {
      return 1;
    } else {
      return myParent.getChildCount();
    }
  }

  /**
   * Returns the next sibling of this node in the parent's children array.
   * Returns null if this node has no parent or is the parent's last child.
   */
  public MibNode getNextSibling() {
    MibNode retval;

    MibNode myParent = (MibNode) getParent();

    if (myParent == null) {
      retval = null;
    } else {
      retval = (MibNode) myParent.getChildAfter(this);
    }

    if (retval != null && !isNodeSibling(retval)) {
      throw new Error("child of parent is not a sibling");
    }

    return retval;
  }

  /**
   * Returns the previous sibling of this node in the parent's children array.
   * Returns null if this node has no parent or is the parent's first child.
   */
  public MibNode getPreviousSibling() {
    MibNode retval;

    MibNode myParent = (MibNode) getParent();

    if (myParent == null) {
      retval = null;
    } else {
      retval = (MibNode) myParent.getChildBefore(this);
    }

    if (retval != null && !isNodeSibling(retval)) {
      throw new Error("child of parent is not a sibling");
    }

    return retval;
  }

  //
  //  Leaf Queries
  //

  /**
   * Returns true if this node has no children. To distinguish between nodes
   * that have no children and nodes that <i>cannot </i> have children (e.g. to
   * distinguish files from empty directories), use this method in conjunction
   * with <code>getAllowsChildren</code>
   */
  public boolean isLeaf() {
    return (getChildCount() == 0);
  }

  /**
   * Finds and returns the first leaf that is a descendant of this node --
   * either this node or its first child's first leaf. Returns this node if it
   * is a leaf.
   */
  public MibNode getFirstLeaf() {
    MibNode node = this;

    while (!node.isLeaf()) {
      node = (MibNode) node.getFirstChild();
    }

    return node;
  }

  /**
   * Finds and returns the last leaf that is a descendant of this node -- either
   * this node or its last child's last leaf. Returns this node if it is a leaf.
   */
  public MibNode getLastLeaf() {
    MibNode node = this;

    while (!node.isLeaf()) {
      node = (MibNode) node.getLastChild();
    }

    return node;
  }

  /**
   * Returns the leaf after this node or null if this node is the last leaf in
   * the tree.
   * <p>
   */
  public MibNode getNextLeaf() {
    MibNode nextSibling;
    MibNode myParent = (MibNode) getParent();

    if (myParent == null)
      return null;

    nextSibling = getNextSibling();

    if (nextSibling != null)
      return nextSibling.getFirstLeaf();

    return myParent.getNextLeaf();
  }

  /**
   * Returns the leaf before this node or null if this node is the first leaf in
   * the tree.
   */
  public MibNode getPreviousLeaf() {
    MibNode previousSibling;
    MibNode myParent = (MibNode) getParent();

    if (myParent == null)
      return null;

    previousSibling = getPreviousSibling();

    if (previousSibling != null)
      return previousSibling.getLastLeaf();

    return myParent.getPreviousLeaf();
  }

  /**
   * Returns the total number of leaves that are descendants of this node. If
   * this node is a leaf, returns <code>1</code>.
   */
  public int getLeafCount() {
    int count = 0;

    MibNode node;
    Enumeration enum = breadthFirstEnumeration();

    while (enum.hasMoreElements()) {
      node = (MibNode) enum.nextElement();
      if (node.isLeaf()) {
        count++;
      }
    }

    if (count < 1) {
      throw new Error("tree has zero leaves");
    }

    return count;
  }

  //
  //  Overrides
  //

  /**
   * Overridden to make clone public. Returns a copy of this node; the new node
   * has the same parent and children.
   */
  public Object clone() {
    MibNode newNode = null;

    try {
      newNode = (MibNode) super.clone();

    } catch (CloneNotSupportedException e) {
      // Won't happen because we implement Cloneable
      throw new Error(e.toString());
    }

    return newNode;
  }

  /**
   * Returns a shallow copy of this node; the new node has no parent or
   * children.
   */
  public Object shallowClone() {
    MibNode newNode = null;

    try {
      newNode = (MibNode) super.clone();

      // shallow copy -- the new node has no parent or children
      newNode.children = null;
      newNode.parent = null;

    } catch (CloneNotSupportedException e) {
      // Won't happen because we implement Cloneable
      throw new Error(e.toString());
    }

    return newNode;
  }

  /**
   * Returns a copy of this node and all the ancestors; the new nodes have no
   * children except themselves.
   */
  public Object clonePath() {
    MibNode newNode = null;
    MibNode newParent = null;
    MibNode result = null;

    newNode = (MibNode) shallowClone();
    result = newNode;

    MibNode parent = getParent();
    while (parent != null) {
      newParent = (MibNode) parent.shallowClone();
      newParent.add(newNode);
      newNode = newParent;
      parent = parent.getParent();
    }
    return result;
  }

  public String toString() {
    return getLabel();
  }

  public String toTagString() {
    return getLabel() + "(" + getOID() + ")";
  }

  final class PreorderEnumeration implements Enumeration {
    protected Stack stack;

    public PreorderEnumeration(MibNode rootNode) {
      super();
      Vector v = new Vector(1);
      v.addElement(rootNode);
      stack = new Stack();
      stack.push(v.elements());
    }

    public boolean hasMoreElements() {
      return (!stack.empty() && ((Enumeration) stack.peek()).hasMoreElements());
    }

    public Object nextElement() {
      Enumeration enumer = (Enumeration) stack.peek();
      MibNode node = (MibNode) enumer.nextElement();
      Enumeration children = node.children();

      if (!enumer.hasMoreElements()) {
        stack.pop();
      }
      if (children.hasMoreElements()) {
        stack.push(children);
      }
      return node;
    }

  }

  final class PostorderEnumeration implements Enumeration {
    protected MibNode root;

    protected Enumeration children;

    protected Enumeration subtree;

    public PostorderEnumeration(MibNode rootNode) {
      super();
      root = rootNode;
      children = root.children();
      subtree = EMPTY_ENUMERATION;
    }

    public boolean hasMoreElements() {
      return root != null;
    }

    public Object nextElement() {
      Object retval;

      if (subtree.hasMoreElements()) {
        retval = subtree.nextElement();
      } else if (children.hasMoreElements()) {
        subtree = new PostorderEnumeration((MibNode) children.nextElement());
        retval = subtree.nextElement();
      } else {
        retval = root;
        root = null;
      }

      return retval;
    }

  }

  final class BreadthFirstEnumeration implements Enumeration {
    protected Queue queue;

    public BreadthFirstEnumeration(MibNode rootNode) {
      super();
      Vector v = new Vector(1);
      v.addElement(rootNode);
      queue = new Queue();
      queue.enqueue(v.elements());
    }

    public boolean hasMoreElements() {
      return (!queue.isEmpty() && ((Enumeration) queue.firstObject())
          .hasMoreElements());
    }

    public Object nextElement() {
      Enumeration enumer = (Enumeration) queue.firstObject();
      MibNode node = (MibNode) enumer.nextElement();
      Enumeration children = node.children();

      if (!enumer.hasMoreElements()) {
        queue.dequeue();
      }
      if (children.hasMoreElements()) {
        queue.enqueue(children);
      }
      return node;
    }

    // A simple queue with a linked list data structure.
    final class Queue {
      QNode head; // null if empty

      QNode tail;

      final class QNode {
        public Object object;

        public QNode next; // null if end

        public QNode(Object object, QNode next) {
          this.object = object;
          this.next = next;
        }
      }

      public void enqueue(Object anObject) {
        if (head == null) {
          head = tail = new QNode(anObject, null);
        } else {
          tail.next = new QNode(anObject, null);
          tail = tail.next;
        }
      }

      public Object dequeue() {
        if (head == null) {
          throw new NoSuchElementException("No more elements");
        }

        Object retval = head.object;
        QNode oldHead = head;
        head = head.next;
        if (head == null) {
          tail = null;
        } else {
          oldHead.next = null;
        }
        return retval;
      }

      public Object firstObject() {
        if (head == null) {
          throw new NoSuchElementException("No more elements");
        }

        return head.object;
      }

      public boolean isEmpty() {
        return head == null;
      }

    } // End of class Queue

  } // End of class BreadthFirstEnumeration

  final class PathBetweenNodesEnumeration implements Enumeration {
    protected Stack stack;

    public PathBetweenNodesEnumeration(MibNode ancestor, MibNode descendant) {
      super();

      if (ancestor == null || descendant == null) {
        throw new IllegalArgumentException("argument is null");
      }

      MibNode current;

      stack = new Stack();
      stack.push(descendant);

      current = descendant;
      while (current != ancestor) {
        current = current.getParent();
        if (current == null && descendant != ancestor) {
          throw new IllegalArgumentException("node " + ancestor
              + " is not an ancestor of " + descendant);
        }
        stack.push(current);
      }
    }

    public boolean hasMoreElements() {
      return stack.size() > 0;
    }

    public Object nextElement() {
      try {
        return stack.pop();
      } catch (EmptyStackException e) {
        throw new NoSuchElementException("No more elements");
      }
    }

  } // End of class PathBetweenNodesEnumeration

} // End of class MibNode
