/*
 * $Id: XMLTaskWriter.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
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
package pt.ipb.agentapi.macros;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;

public class XMLTaskWriter implements TaskWriter {

  OutputStream stream = null;

  PrintStream ostream = null;

  public XMLTaskWriter(OutputStream s) {
    this.stream = s;
    this.ostream = new PrintStream(stream);
  }

  public void write(Tasks tasks) throws IOException {
    StringReader s = new StringReader(tasks.toXML());
    int c;
    while ((c = s.read()) != -1) {
      if (c == '&') {
        ostream.print("&amp;");
        ostream.flush();
      } else {
        stream.write(c);
      }
    }

    s.close();
    stream.close();
  }

}