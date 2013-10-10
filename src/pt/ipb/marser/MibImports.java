/*
 * $Id: MibImports.java 3 2004-08-03 10:42:11Z rlopes $
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
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class deals with all the IMPORTS of the MIB module.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibImports {
  Hashtable tableImports = null;

  Hashtable tableModules = null;

  String[] path;

  public MibImports(String[] path) {
    this.path = path;
    tableImports = new Hashtable();
    tableModules = new Hashtable();
  }

  public void addModule(MibModule mibM) {
    if (mibM != null)
      tableModules.put(mibM.getName(), mibM);
  }

  public MibModule getModule(String module) {
    return (MibModule) tableModules.get(module);
  }

  public void addImports(String module, String[] imps)
      throws FileNotFoundException, MibException {
    tableImports.put(module, imps);
    //String fn = new String(dir+java.io.File.separator+module);
    MibModule m = MibModule.load(module, path);
    addModule(m);
  }

  public String[] getImports(String module) {
    return (String[]) tableImports.get(module);
  }

  /**
   * All the module names in this IMPORTS.
   * 
   * @return String with the name of the Module in the IMPORTS clause
   */
  public Enumeration imports() {
    return tableImports.keys();
  }

  /**
   * All the imported modules in this IMPORTS.
   * 
   * @return MibModule in the IMPORTS clause
   */
  public Enumeration modules() {
    return tableModules.elements();
  }

  /**
   * Returns the module which has the provided string.
   */
  public String getModuleNameContaining(String imp) {
    String modName = null;
    for (Enumeration e = modules(); e.hasMoreElements();) {
      modName = (String) e.nextElement();
      String[] imps = (String[]) getImports(modName);
      for (int i = 0; i < imps.length; i++) {
        if (imp.equals(imps[i]))
          return modName;
      }
    }
    return null;
  }

}