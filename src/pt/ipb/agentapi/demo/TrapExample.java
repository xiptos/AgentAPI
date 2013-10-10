/*
 * $Id: TrapExample.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.demo;

import pt.ipb.agentapi.AbstractAgent;
import pt.ipb.agentapi.Table;
import pt.ipb.snmp.SnmpConstants;
import pt.ipb.snmp.SnmpProperties;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * An agent example which sends traps every 2 seconds.
 */
public class TrapExample extends AbstractAgent implements Runnable {

  public final static String SYS_UP_TIME_INSTANCE = ".1.3.6.1.2.1.1.3.0";

  public final static String SYS_CONTACT_INSTANCE = ".1.3.6.1.2.1.1.4.0";

  public final static String IFTABLE = ".1.3.6.1.2.1.2.2";

  public final static String SNMP_TARGET_MIB = ".1.3.6.1.6.3.12";

  public final static String SNMP_TARGET_OBJECTS = new String(SNMP_TARGET_MIB
      + ".1");

  public final static String SNMP_TARGET_ADDR_TABLE = new String(
      SNMP_TARGET_OBJECTS + ".2");

  public TrapExample() {
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
      TrapExample agent = new TrapExample();
      pt.ipb.agentapi.engine.EngineFactory.start(agent);
      agent.start();
    } catch (Exception e) {
      System.out.println("Caught an exception: " + e.getMessage());
    }
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to
   * create a thread, starting the thread causes the object's <code>run</code>
   * method to be called in that separately executing thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may take
   * any action whatsoever.
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    while (true) {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
      }
      java.util.Vector v = new java.util.Vector();
      // The first VarBind for SNMPv2-TRAPs is sysUpTime.0
      v.addElement(getOperation(new VarBind(SYS_UP_TIME_INSTANCE, null)));
      // The second VarBind is snmpTrapOID.0
      // For example, coldStart
      v.addElement(new VarBind(pt.ipb.agentapi.event.TrapEvent.SNMP_TRAP_OID,
          new OID(".1.3.6.1.6.3.1.1.5.1")));
      pt.ipb.agentapi.event.TrapEvent e = new pt.ipb.agentapi.event.TrapEvent(
          this);
      SnmpProperties props = new SnmpProperties();
      props.setCommunity("public");
      props.setVersion(SnmpConstants.SNMPv2c);
      e.setProperties(props);
      e.setTarget("localhost");
      e.setTargetPort(10162);
      e.setSpecific(1);
      e.setGeneric(2);
      e.setTimestamp(1000);
      e.setEnterpriseOid(".1.2.3.4");
      e.setVarBindVector(v);
      sendNotification(e);
    }
  }

  public void start() {
    Thread t = new Thread(this);
    t.start();
  }

}

