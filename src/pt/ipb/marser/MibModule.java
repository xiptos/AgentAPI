/*
 * $Id: MibModule.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import pt.ipb.marser.smi.SMIParser;
import pt.ipb.snmp.type.smi.OID;

/**
 * This class represents a MIB module.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibModule implements Serializable {
  // MODULE-IDENTITY
  MibIdentity identity = null;

  MibImports imports = null;

  MibNode root = null;

  Vector tcVector = new Vector();

  Vector trapVector = new Vector();

  Vector exportVector = new Vector();

  String name = null;

  String[] path = null;

  String fileName = null;

  public MibModule(String fName) {
    this(new File(fName));
  }

  public MibModule(File file) {
    this.path = new String[1];
    if (file.getParent() != null)
      this.path[0] = file.getParent();
    else
      this.path[0] = ".";

    this.fileName = file.getName();
  }

  public MibModule(String fName, String[] path) {
    this.fileName = fName;
    // Do not include the fName parent directory to the Path. This is done
    // in MibParser
    this.path = path;
  }

  /**
   * Get the MibNode by OID.
   * 
   * @param OID
   *          oid.
   * @return The MibNode object.
   */
  public MibNode getNode(OID oid) {
    try {
      // the first occurence in the existing tree.
      if (root == null)
        return null;
      for (Enumeration e = root.breadthFirstEnumeration(); e.hasMoreElements();) {
        MibNode node = (MibNode) e.nextElement();
        if (oid.equals(node.getOID())) {
          return node;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get the MibNode by short name.
   * 
   * @param String
   *          name.
   * @return The MibNode object.
   */
  public MibNode getNode(String shortName) {
    try {
      // the first occurence in the existing tree.
      if (root != null) {
        for (Enumeration e = root.getRoot().breadthFirstEnumeration(); e
            .hasMoreElements();) {
          MibNode node = (MibNode) e.nextElement();
          if (shortName.equals(node.getLabel())) {
            return node;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Checks if the provided MibNode has the same name.
   * 
   * @param MibModule
   *          the module to check.
   * @return true if modules have the same name.
   */
  public boolean equals(Object o) {
    MibModule m = (MibModule) o;
    return getName().equals(m.getName());
  }

  public int hashCode() {
    return getName().hashCode();
  }

  /**
   * Checks if the provided TC exists in this module.
   */
  public boolean containsTC(MibTC tc) {
    return tcVector.contains(tc);
  }

  /**
   * Returns a clone of the provided MibTC if found in IMPORTS.
   * 
   * @param String
   *          the name of the textual convention.
   * @return the MibTC object or null if not found.
   */
  public MibTC lookupImportsTC(String label) {
    MibTC result = null;
    try {
      if (imports != null) {
        for (Enumeration e = imports.modules(); e.hasMoreElements();) {
          MibModule mod = imports.getModule((String) e.nextElement());
          result = mod.getMibTC(label);
          if (result == null)
            result = mod.lookupImportsTC(label);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Returns a clone of the provided MibNode if found in IMPORTS.
   * 
   * @param String
   *          the name of the node.
   * @return the MibNode object or null if not found.
   */
  public MibNode lookupImports(String shortName) {
    MibNode result = null;
    try {
      if (imports != null) {
        for (Enumeration e = imports.modules(); e.hasMoreElements();) {
          MibModule mod = (MibModule) e.nextElement();
          result = mod.getNode(shortName);
          if (result == null)
            result = mod.lookupImports(shortName);
          else
            return result;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Builds a MibModule object by loading an SMIv2 file.
   * 
   * @param String
   *          the file name.
   * @return the MibModule correspondent to the file name.
   * @throws MibException
   *           if the file could no be parsed.
   * @throws FileNotFoundException
   *           if the file could not be found.
   */
  public static MibModule load(String fName) throws MibException,
      FileNotFoundException {
    return load(fName, null);
  }

  /**
   * Builds a MibModule object by loading an SMIv2 file.
   * 
   * @param String
   *          the file name.
   * @param path
   *          to search imports for.
   * @return the MibModule correspondent to the file name.
   * @throws MibException
   *           if the file could no be parsed.
   * @throws FileNotFoundException
   *           if the file could not be found.
   */
  public static MibModule load(String fileName, String[] path)
      throws MibException, FileNotFoundException {
    File file = new File(fileName);
    String[] pth = path;
    if (pth == null) {
      pth = new String[1];
      pth[0] = file.getParent() == null ? "." : file.getParent();
    }
    for (int i = 0; i < pth.length; i++) {
      if (!file.exists()) {
        file = new File(pth[i] + File.separator + file.getName());
      }
    }
    if (!file.exists()) {
      throw new java.io.FileNotFoundException(fileName + " not found!");
    }

    try {

      SMIParser mibParser = new SMIParser(file.getAbsolutePath(), pth);
      mibParser.parse();
      return mibParser.getFirstMibModule();

    } catch (FileNotFoundException e) {
      throw e;
    } catch (MibException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new MibException("error: " + e.getMessage());
    }

  }

  /**
   * Get the file name associated with this object.
   * 
   * @return the file name.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * The path where to look for imports files
   * 
   * @return the directory string.
   */
  public String[] getPath() {
    return path;
  }

  /**
   * Set the name of this MibModule.
   * 
   * @param String
   *          name of this MibModule.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the name of this MibModule.
   * 
   * @return the name of this module.
   */
  public String getName() {
    if (name != null)
      return name;
    return fileName;
  }

  /**
   * Get the IDENTITY object associated with this MibModule.
   * 
   * @return the {@link MibIdentity MibIdentity}associated with this module.
   */
  public MibIdentity getIdentity() {
    return identity;
  }

  /**
   * Associate the IDENTITY object to this MibModule.
   * 
   * @param the
   *          {@link MibIdentity MibIdentity}associated with this module.
   */
  public void setIdentity(MibIdentity n) {
    this.identity = n;
  }

  /**
   * Get the IMPORTS object associated with this MibModule.
   * 
   * @return the {@link MibImports MibImports}associated with this module.
   */
  public MibImports getImports() {
    return imports;
  }

  /**
   * Get all known MibTC, including the ones in MibImports.
   * 
   * @return vector of MibTC objects
   */
  public Vector getAllKnownMibTC() {
    Vector v = new Vector();
    v.addAll(tcVector);
    if (imports != null) {
      for (Enumeration e = imports.modules(); e.hasMoreElements();) {
        MibModule mod = imports.getModule((String) e.nextElement());
        v.addAll(mod.getAllKnownMibTC());
      }
    }
    return v;
  }

  /**
   * Add a MibTC to this MibModule.
   * 
   * @param MibTC
   *          the textual convention to include in this module.
   */
  public void addMibTC(MibTC tc) {
    tcVector.add(tc);
  }

  public Enumeration mibTCs() {
    return tcVector.elements();
  }

  public MibTC getMibTC(String label) {
    for (Enumeration e = mibTCs(); e.hasMoreElements();) {
      MibTC tc = (MibTC) e.nextElement();
      if (label.equals(tc.getLabel())) {
        return tc;
      }
    }
    return null;
  }

  /**
   * Set the MibTrap vector associated with this module.
   * 
   * @param Vector
   *          the vector of MibTrap objects.
   */
  public void addMibTrap(MibTrap t) {
    this.trapVector.addElement(t);
  }

  public Enumeration traps() {
    return trapVector.elements();
  }

  /**
   * Set the MibImports object associated with this module.
   * 
   * @param IMPORTS
   *          object.
   */
  public void setImports(MibImports n) {
    this.imports = n;
  }

  /**
   * Set the EXPORTS list objects associated with this module.
   * 
   * @param EXPORTS
   *          objects.
   */
  public void setExports(java.util.Collection c) {
    if (c != null) {
      this.exportVector = new Vector(c);
    }
  }

  public Enumeration exports() {
    return this.exportVector.elements();
  }

  /**
   * Get the root node.
   * 
   * @return the root node.
   */
  public MibNode getRoot() {
    return root;
  }

  /**
   * Set the root node.
   * 
   * @param MibNode
   *          the root node.
   */
  public void setRoot(MibNode n) {
    this.root = n;
  }

  /**
   * The name of this module.
   * 
   * @return name of this module.
   */
  public String toString() {
    if (getName() == null)
      return "No name yet...";
    return getName();
  }

  public static void main(String arg[]) {
    try {
      MibModule.load(arg[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}