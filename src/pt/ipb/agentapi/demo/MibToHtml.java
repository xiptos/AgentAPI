/*
 * $Id: MibToHtml.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
 * *
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

package pt.ipb.agentapi.demo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import pt.ipb.marser.MibException;
import pt.ipb.marser.MibModule;
import pt.ipb.marser.MibNode;
import pt.ipb.marser.MibOps;
import pt.ipb.marser.MibTC;
import pt.ipb.marser.MibTrap;

public class MibToHtml {

  static String POPUP_CODE = "<script language=\"JavaScript\">"
      + "function popupWin(url, content, width, height) {"
      + "popupWindow = window.open(url, 1, 'WIDTH='+width+', HEIGHT='+height);"
      + "popupWindow.document.write(content);" + "popupWindow.focus();" + "}"
      + "</script>";

  static String NODE = "node.png";

  static String TABLE = "table.png";

  static String INDEX = "index.png";

  static String IMP_INDEX = "imp_index.png";

  static String LEAF = "leaf_ro.png";

  static String LEAF_RO = "leaf_ro.png";

  static String LEAF_RW = "leaf_rw.png";

  static String LEAF_RC = "leaf_rc.png";

  static String LEAF_WO = "leaf_ro.png";

  static String ENTRY = "entry.png";

  static String TC = "tc.png";

  static String TRAP = "trap.png";

  static String NOTIF = "notif.png";

  static String GROUP = "group.png";

  static String COMPL = "compl.png";

  String imageDir = null;

  MibOps mibOps = null;

  public MibToHtml(String fileName, String[] path) throws MibException,
      IOException, FileNotFoundException {
    this(fileName, path, null);
  }

  /**
   * Parses a MIB file and retrieves the dependencies from dir
   */
  public MibToHtml(String fileName, String[] path, String imageDir)
      throws MibException, IOException, FileNotFoundException {
    this.imageDir = imageDir;

    mibOps = new MibOps(path);

    //Loading MIBs
    mibOps.loadMibs(fileName);
  }

  String children(int level, MibNode node) {
    StringBuffer str = new StringBuffer();
    for (int i = 0; i < level; i++) {
      str.append(" ");
    }
    if (node != null) {

      str.append(nodeToHtml(node));

      str.append("<dl>\n");
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        MibNode child = (MibNode) e.nextElement();
        str.append("<dd>\n");
        str.append(children(level + 2, child));
        str.append("</dd>\n");
      }
      str.append("</dl>\n");
    }
    return str.toString();
  }

  /**
   * Builds an HTML document based on the given fileName. The document contains
   * all the children of the parentNode.
   */
  public String getPage() {
    StringBuffer str = new StringBuffer();
    str.append(initToHtml());

    for (Enumeration e = mibOps.modules(); e.hasMoreElements();) {
      MibModule module = (MibModule) e.nextElement();
      str.append(sectionToHtml(module.getName(), "#ffcc99"));

      MibNode root = module.getRoot();
      str.append(sectionToHtml("MIB Tree", "#ffffcc"));
      str.append(children(2, root));

      str.append(sectionToHtml("Textual Conventions", "#ffffcc"));
      for (Enumeration e2 = module.mibTCs(); e2.hasMoreElements();) {
        MibTC tc = (MibTC) e2.nextElement();
        str.append(tcToHtml(tc));
      }
      str.append(sectionToHtml("Traps", "#ffffcc"));
      for (Enumeration e3 = module.traps(); e3.hasMoreElements();) {
        MibTrap trap = (MibTrap) e3.nextElement();
        str.append(trapToHtml(trap));
      }
      str.append(sectionToHtml("End " + module.getName(), "#ffcc99"));
    }
    str.append("</BODY>\n");
    str.append("</HTML>\n");
    return str.toString();
  }

  public String getModuleName() {
    MibModule[] module = mibOps.getMibModules();
    if (module != null)
      return module[0].getName();
    else
      return null;
  }

  String addSlashes(String s) {
    StringBuffer str = new StringBuffer(s);
    int i = str.indexOf("\"");
    while (i < str.length() && i != -1) {
      str.deleteCharAt(i);
      i = str.indexOf("\"", i);
    }

    i = str.indexOf("\n");
    while (i < str.length() && i != -1) {
      //str.deleteCharAt(i);
      str.insert(i, "<br>\\");
      i = str.indexOf("\n", i + 6);
    }

    i = str.indexOf("'");
    while (i < str.length() && i != -1) {
      str.insert(i, "\\");
      i = str.indexOf("'", i + 2);
    }
    return str.toString();
  }

  public static void main(String args[]) {
    try {
      MibToHtml mtt = new MibToHtml(args[0], new String[] { args[1] });
      System.out.println(mtt.getPage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * HTML functions
   */
  String sectionToHtml(String s, String color) {
    StringBuffer str = new StringBuffer();
    str
        .append("<table cellpadding=\"2\" cellspacing=\"2\" border=\"0\" width=\"100%\">");
    str.append("  <tbody>");
    str.append("    <tr>");
    str.append("      <td bgcolor=\"" + color + "\">");
    str.append(s);
    str.append("      </td>");
    str.append("    </tr>");
    str.append("  </tbody>");
    str.append("</table>");
    return str.toString();
  }

  String nodeToHtml(MibNode node) {
    StringBuffer str = new StringBuffer();

    StringBuffer content = new StringBuffer();
    content.append("<table border='0' width='100%' cellspacing='3'>");

    content.append("<tr>");
    content.append("<td width='100%' colspan='2' bgcolor='#669999'>");
    content.append("<p>" + node.getOID() + "</p>");
    content.append("</td>");
    content.append("</tr>");

    if (node.getStatus() != null) {
      content.append("<tr>");
      content.append("<td width='25%' valign='top' align='right'>");
      content.append("<p align='right'><b>Status:</b></p>");
      content.append("</td>");
      content.append("<td width='75%'>");
      content.append(node.getStatus());
      content.append("</td>");
      content.append("</tr>");
    }

    if (node.getSyntax() != null) {
      content.append("<tr>");
      content.append("<td width='25%' valign='top' align='right'>");
      content.append("<p align='right'><b>Syntax:</b></p>");
      content.append("</td>");
      content.append("<td width='75%'>");
      content.append(node.getSyntax().getDescription());
      content.append("</td>");
      content.append("</tr>");
    }

    if (node.getUnits() != null) {
      content.append("<tr>");
      content.append("<td width='25%' valign='top' align='right'>");
      content.append("<p align='right'><b>Units:</b></p>");
      content.append("</td>");
      content.append("<td width='75%'>");
      content.append(node.getUnits());
      content.append("</td>");
      content.append("</tr>");
    }

    if (node.getAccessStr() != null) {
      content.append("<tr>");
      content.append("<td width='25%' valign='top' align='right'>");
      content.append("<p align='right'><b>Access:</b></p>");
      content.append("</td>");
      content.append("<td width='75%'>");
      content.append(node.getAccessStr());
      content.append("</td>");
      content.append("</tr>");
    }

    if (node.getDefVal() != null) {
      content.append("<tr>");
      content.append("<td width='25%' valign='top' align='right'>");
      content.append("<p align='right'><b>DefVal:</b></p>");
      content.append("</td>");
      content.append("<td width='75%'>");
      content.append(node.getDefVal());
      content.append("</td>");
      content.append("</tr>");
    }

    if (node.getDescription() != null) {
      content.append("<tr>");
      content.append("<td width='25%' valign='top' align='right'>");
      content.append("<p align='right'><b>Description:</b></p>");
      content.append("</td>");
      content.append("<td width='75%'>");
      content.append(node.getDescription());
      content.append("</td>");
      content.append("</tr>");
    }
    content.append("</table>");

    content
        .append("<p><center><input type=button value=\'Close\' onClick=\'self.close()'></center></p>");

    //System.out.println("Content: "+content.toString());

    str.append("<a href=\"#\" onclick=\"popupWin('', ");
    str.append("'" + addSlashes(content.toString()) + " ', ");
    str.append("600, 400)\">");

    if (imageDir != null) {
      str.append("<img border='0' src='" + imageDir + "/" + getIconName(node)
          + "'>");
    }
    str.append(node.getLabel());
    str.append("</a>");
    return str.toString();
  }

  String getIconName(MibNode node) {
    String image = NODE;

    if (node.isTable()) {
      image = TABLE;

    } else if (node.isTableEntry()) {
      image = ENTRY;

    } else if (node.isImpliedIndex()) {
      image = IMP_INDEX;

    } else if (node.isIndex()) {
      image = INDEX;

    } else if (node.isLeaf()) {
      int access = node.getAccess();
      switch (access) {
      case MibOps.READ_ONLY:
        image = LEAF_RO;
        break;
      case MibOps.READ_WRITE:
        image = LEAF_RW;
        break;
      case MibOps.READ_CREATE:
        image = LEAF_RC;
        break;
      case MibOps.WRITE_ONLY:
        image = LEAF_WO;
        break;
      case MibOps.ACCESSIBLE_FOR_NOTIFY:
        image = NOTIF;
        break;
      default:
        image = LEAF;
      }
    }
    return image;
  }

  String tcToHtml(MibTC tc) {
    StringBuffer str = new StringBuffer();

    if (imageDir != null) {
      str.append("<img border='0' src='" + imageDir + "/" + TC + "'>");
    }
    str.append(tc.getLabel() + "<br>");
    return str.toString();
  }

  String trapToHtml(MibTrap trap) {
    StringBuffer str = new StringBuffer();

    if (imageDir != null) {
      str.append("<img border='0' src='" + imageDir + "/" + TRAP + "'>");
    }
    str.append(trap.getLabel() + "<br>");
    return str.toString();
  }

  String initToHtml() {
    StringBuffer str = new StringBuffer();
    str.append("<HTML>\n");
    str.append("<TITLE>MibParser</TITLE>\n");
    str.append(POPUP_CODE);
    str.append("<BODY>\n");
    str
        .append("<p>You will need to activate JavaScript to see the details of each node!</p>");
    str.append("<b>Legend:</b><br>");
    str.append("<img border='0' src='" + imageDir + "/" + NODE
        + "'> - MIB tree node; ");
    str.append("<img border='0' src='" + imageDir + "/" + TABLE
        + "'> - MIB table; ");
    str.append("<img border='0' src='" + imageDir + "/" + INDEX
        + "'> - MIB table index; ");
    str.append("<img border='0' src='" + imageDir + "/" + IMP_INDEX
        + "'> - MIB table implied index; ");
    str.append("<img border='0' src='" + imageDir + "/" + ENTRY
        + "'> - MIB table entry; ");
    str.append("<img border='0' src='" + imageDir + "/" + LEAF_RC
        + "'> - Read-create leaf node; ");
    str.append("<img border='0' src='" + imageDir + "/" + LEAF_RO
        + "'> - Read-only leaf node; ");
    str.append("<img border='0' src='" + imageDir + "/" + LEAF_RW
        + "'> - Read-write leaf node; ");
    str.append("<img border='0' src='" + imageDir + "/" + LEAF_WO
        + "'> - Write-only leaf node; ");
    str.append("<img border='0' src='" + imageDir + "/" + NOTIF
        + "'> - Notification node; ");
    str.append("<img border='0' src='" + imageDir + "/" + TC
        + "'> - Textual convention; ");
    str.append("<img border='0' src='" + imageDir + "/" + TRAP + "'> - Trap; ");
    str.append("<img border='0' src='" + imageDir + "/" + COMPL
        + "'> - Compliance statement; ");
    str.append("<img border='0' src='" + imageDir + "/" + GROUP
        + "'> - Group; ");
    return str.toString();
  }
}