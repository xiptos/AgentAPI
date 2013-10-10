/*
 * $Id: MultiAgentExample.java 3 2004-08-03 10:42:11Z rlopes $
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

import pt.ipb.agentapi.MultiAgent;

/**
 * This agent is used as an example to show an agent made of several Agents. It
 * is used with MultiAgentFirst and MultiAgentSecond.
 * 
 * @see MultiAgentFirst
 * @see MultiAgentSecond
 */
public class MultiAgentExample {

  public static void main(String arg[]) {
    try {
      MultiAgent ma = new MultiAgent();

      MultiAgentFirst first = new MultiAgentFirst();
      MultiAgentSecond second = new MultiAgentSecond();

      ma.add(first);
      ma.add(second);

      pt.ipb.agentapi.engine.EngineFactory.start(ma);

      System.out.println("Ready");
    } catch (Exception e) {
      //e.printStackTrace();
      System.out.println("Caught an exception: " + e.getMessage());
    }
  }

  /*
   * public static void main(String[] args) { // The engine for this agent
   * Engine snmpEngine = null; // We have to define the properties to pass to
   * the engine // It is also possible to read them from a file. SnmpProperties
   * p = new SnmpProperties(); p.setCommunity("public");
   * p.setWriteCommunity("private"); p.setPort(10161);
   * p.setVersion(SnmpConstants.SNMPv2c);
   * 
   * try { // EngineFactory gets the engine class name from // the system
   * property pt.ipb.agentpi.engine.Engine // Defaults to
   * pt.ipb.agentapi.engine.snmp.JoeSnmpEngine snmpEngine =
   * EngineFactory.createEngine(); snmpEngine.setProperties(p);
   * snmpEngine.open();
   * 
   * pt.ipb.agentapi.event.MessageListener ml =
   * snmpEngine.createAgentListener();
   * 
   * MultiAgent ma = new MultiAgent();
   * 
   * TestAgent agent = new TestAgent(); TestAgent1 agent1 = new TestAgent1();
   * 
   * ma.add(agent); ma.add(agent1);
   * 
   * ma.addMessageListener(ml); snmpEngine.addAgent(ma);
   * 
   * System.out.println("Ready"); } catch (Exception e) { e.printStackTrace(); } }
   */
  /*
   * public static void main(String[] args) { // The engine for this agent
   * Engine[] engines = null; // We have to define the properties to pass to the
   * engine // It is also possible to read them from a file. SnmpProperties p =
   * new SnmpProperties(); p.setCommunity("public");
   * p.setWriteCommunity("private"); p.setPort(10161);
   * p.setVersion(SnmpConstants.SNMPv2c); // EngineFactory gets the engine class
   * name from // the system property pt.ipb.agentpi.engine.Engine // Defaults
   * to pt.ipb.agentapi.engine.snmp.JoeSnmpEngine MultiAgent ma = new
   * MultiAgent();
   * 
   * MultiAgentFirst first = new MultiAgentFirst(); MultiAgentSecond second =
   * new MultiAgentSecond();
   * 
   * ma.add(first); ma.add(second);
   * 
   * try { engines = EngineFactory.createEngines();
   * pt.ipb.agentapi.engine.EngineFactory.start(ma, engines, p);
   * System.out.println("Ready"); } catch (Exception e) {
   * System.out.println("Caught exception: "+e.getMessage()); } }
   */
}