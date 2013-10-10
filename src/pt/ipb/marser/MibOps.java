/*
 * $Id: MibOps.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import pt.ipb.snmp.type.smi.OID;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibOps implements Serializable {
  // ACCESS
  public static final int NO_VAL = 0;

  public static final int NOT_ACCESSIBLE = 1;

  public static final int ACCESSIBLE_FOR_NOTIFY = 2;

  public static final int READ_ONLY = 3;

  public static final int READ_WRITE = 4;

  public static final int READ_CREATE = 5;

  public static final int WRITE_ONLY = 6;

  public static final String NO_VAL_STR = "access-undefined";

  public static final String NOT_ACCESSIBLE_STR = "not-accessible";

  public static final String ACCESSIBLE_FOR_NOTIFY_STR = "accessible-for-notify";

  public static final String READ_ONLY_STR = "read-only";

  public static final String READ_WRITE_STR = "read-write";

  public static final String READ_CREATE_STR = "read-create";

  public static final String WRITE_ONLY_STR = "write-only";

  // STATUS
  public static final int CURRENT = 10;

  public static final int DEPRECATED = 11;

  public static final int OBSOLETE = 12;

  ArrayList modules = null;

  String[] path = null;

  String fSep = System.getProperty("file.separator");

  public MibOps() {
    modules = new ArrayList();
  }

  public MibOps(String[] path) {
    this();
    setPath(path);
  }

  /**
   * The path where to retrieve the IMPORTS modules.
   */
  public void setPath(String[] path) {
    this.path = path;
  }

  /**
   * Loads space separated mib files. It appends the path defined in
   * setMibPath()
   */
  public void loadMibs(String mibs) throws FileNotFoundException, MibException {
    StringTokenizer strToken = new StringTokenizer(mibs, " ");
    while (strToken.hasMoreTokens()) {
      String mib = strToken.nextToken();
      loadMib(mib);
    }
  }

  /**
   * Loads a single MIB file. It appends the path defined in setMibPath()
   */
  public void loadMib(String mib) throws FileNotFoundException, MibException {
    MibModule mibModule = MibModule.load(mib, path);
    if (mibModule != null) {
      addMibModule(mibModule);
      MibImports imports = mibModule.getImports();
      if (imports != null) {
        for (Enumeration e = imports.modules(); e.hasMoreElements();) {
          MibModule m = (MibModule) e.nextElement();
          addMibModule(m);
        }
      }
    }
  }

  public MibNode getCloserNode(String node) {
    MibNode n = null;
    OID o = new OID(node);
    do {
      n = getMibNode(o);
      if (n != null)
        return n;
      o = o.subOID(0, o.length() - 1);
    } while (n == null);
    return null;
  }

  public MibNode getMibNode(OID oid) {
    for (Iterator i = modules.iterator(); i.hasNext();) {
      MibModule my = (MibModule) i.next();
      MibNode n = my.getNode(oid);
      if (n != null)
        return n;
    }
    return null;
  }

  public MibNode getMibNode(String node) {
    for (Iterator i = modules.iterator(); i.hasNext();) {
      MibModule my = (MibModule) i.next();
      MibNode n = my.getNode(node);
      if (n != null)
        return n;
    }
    return null;
  }

  public MibTC getMibTC(String label) {
    for (Iterator i = modules.iterator(); i.hasNext();) {
      MibModule my = (MibModule) i.next();
      MibTC tc = my.getMibTC(label);
      if (tc != null)
        return tc;
    }
    return null;
  }

  public synchronized void removeAll() {
    for (Iterator i = modules.iterator(); i.hasNext();) {
      MibModule module = (MibModule) i.next();
      i.remove();
    }
  }

  public synchronized void removeModuleContainingTC(MibTC tc) {
    for (Iterator i = modules.iterator(); i.hasNext();) {
      MibModule module = (MibModule) i.next();
      if (module.containsTC(tc)) {
        i.remove();
        return;
      }
    }
  }

  public synchronized void remove(String k) {
    removeMibModule(getMibModule(k));
  }

  public synchronized MibModule getMibModule(String k) {
    for (Iterator i = modules.iterator(); i.hasNext();) {
      MibModule module = (MibModule) i.next();
      if (module.getName().equals(k))
        return module;
    }
    return null;
  }

  public synchronized void removeMibModule(MibModule m) {
    modules.remove(m);
  }

  public synchronized void addMibModule(MibModule m) {
    if (modules.contains(m))
      return;
    modules.add(m);
  }

  public synchronized Enumeration modules() {
    return Collections.enumeration(modules);
  }

  public synchronized MibModule[] getMibModules() {
    MibModule[] mods = new MibModule[modules.size()];
    return (MibModule[]) modules.toArray(mods);
  }

  public String toString() {
    return "root";
  }

  public static String access2str(int a) {
    switch (a) {
    case NO_VAL:
      return NO_VAL_STR;
    case NOT_ACCESSIBLE:
      return NOT_ACCESSIBLE_STR;
    case ACCESSIBLE_FOR_NOTIFY:
      return ACCESSIBLE_FOR_NOTIFY_STR;
    case READ_ONLY:
      return READ_ONLY_STR;
    case READ_WRITE:
      return READ_WRITE_STR;
    case READ_CREATE:
      return READ_CREATE_STR;
    case WRITE_ONLY:
      return WRITE_ONLY_STR;
    }
    return NO_VAL_STR;
  }

  public static int access2int(String a) {
    if (NOT_ACCESSIBLE_STR.equals(a))
      return NOT_ACCESSIBLE;
    else if (ACCESSIBLE_FOR_NOTIFY_STR.equals(a))
      return ACCESSIBLE_FOR_NOTIFY;
    else if (READ_ONLY_STR.equals(a))
      return READ_ONLY;
    else if (READ_WRITE_STR.equals(a))
      return READ_WRITE;
    else if (READ_CREATE_STR.equals(a))
      return READ_CREATE;
    else if (WRITE_ONLY_STR.equals(a))
      return WRITE_ONLY;

    return NO_VAL;
  }
}