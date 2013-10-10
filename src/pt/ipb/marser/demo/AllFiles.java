/*
 * $Id: AllFiles.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.marser.demo;

import java.io.File;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class AllFiles {
  String dir;

  /** Creates a new instance of AllFiles */
  public AllFiles(String dir) {
    this.dir = dir;
  }

  public void start() {
    File d = new File(dir);
    File[] files = d.listFiles();
    for (int i = 0; i < files.length; i++) {
      File f = files[i];
      if (f.isFile()) {
        try {
          MibToText mtt = new MibToText(f.getAbsolutePath());
          System.out.println(f.getAbsolutePath() + " OK!");
        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("Failed in " + f.getAbsolutePath());
          System.exit(1);
        }
      }
    }
  }

  /**
   * @param args
   *          the command line arguments
   */
  public static void main(String[] args) {
    String dir = ".";
    if (args.length > 0)
      dir = args[0];
    AllFiles a = new AllFiles(dir);
    a.start();
  }

}