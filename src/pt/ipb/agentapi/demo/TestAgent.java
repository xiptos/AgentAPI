/*
 * $Id: TestAgent.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
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

import pt.ipb.agentapi.AbstractAgent;
import pt.ipb.agentapi.Agent;
import pt.ipb.agentapi.Table;

public class TestAgent extends AbstractAgent {

  public final static String SYS_UP_TIME_INSTANCE = ".1.3.6.1.2.1.1.3.0";

  public final static String SYS_CONTACT_INSTANCE = ".1.3.6.1.2.1.1.4.0";

  public final static String IFTABLE = ".1.3.6.1.2.1.2.2";

  public final static String SNMP_TARGET_MIB = ".1.3.6.1.6.3.12";

  public final static String SNMP_TARGET_OBJECTS = new String(SNMP_TARGET_MIB
      + ".1");

  public final static String SNMP_TARGET_ADDR_TABLE = new String(
      SNMP_TARGET_OBJECTS + ".2");

  public TestAgent() {
    super();
  }

  public void setObjects() {
    SysUpTime a = new SysUpTime(SYS_UP_TIME_INSTANCE);
    addObject(a);

    SysContact b = new SysContact(SYS_CONTACT_INSTANCE);
    addObject(b);

    SnmpTargetAddrTable model = new SnmpTargetAddrTable("0.0.0.0.0.0.0.0.0.0.0");
    Table c = new Table(SNMP_TARGET_ADDR_TABLE, model);
    addObject(c);

    Table ct = new Table(IFTABLE, new IFTable());
    addObject(ct);

    ct = new Table(AtTable.ATTABLE, new AtTable());
    addObject(ct);

  }

  public static void main(String arg[]) {
    try {
      Agent agent = new TestAgent();
      pt.ipb.agentapi.engine.EngineFactory.start(agent);
    } catch (Exception e) {
      System.out.println("Caught an exception: " + e.getMessage());
    }
  }
}

