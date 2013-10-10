/*
 * $Id: ResourceFactory.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class ResourceFactory {
  private static final String IMAGES = "pt/ipb/marser/gui/images";

  public static Font getMainMenuFont() {
    return new Font("Dialog", Font.BOLD, 10);
  }

  public static Font getMenuFont() {
    return new Font("Dialog", Font.PLAIN, 10);
  }

  public static Font getOpLabelFont() {
    return new Font("Dialog", Font.PLAIN, 10);
  }

  public static Font getDialogFont() {
    return new Font("Dialog", Font.PLAIN, 10);
  }

  public static Font getTableFont() {
    return new Font("Dialog", Font.PLAIN, 12);
  }

  public static ImageIcon getIcon(String name) {
    URL iconURL = ClassLoader
        .getSystemResource(new String(IMAGES + "/" + name));
    ImageIcon icon = null;
    if (iconURL != null)
      icon = new ImageIcon(iconURL);
    return icon;
  }

  public static Cursor getCursor(String name) {
    Toolkit t = Toolkit.getDefaultToolkit();
    ImageIcon i = getIcon(name);
    Dimension d = t.getBestCursorSize(i.getIconWidth(), i.getIconHeight());
    BufferedImage buf = new BufferedImage(d.width, d.height,
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = buf.createGraphics();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
    Rectangle rect = new Rectangle(0, 0, d.width, d.height);
    g.fill(rect);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    g.drawImage(i.getImage(), 0, 0, i.getImageObserver());
    Cursor c = t.createCustomCursor(buf, new Point(15, 15), name);
    return c;
  }

}