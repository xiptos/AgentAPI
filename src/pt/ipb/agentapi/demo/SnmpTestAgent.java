package pt.ipb.agentapi.demo;

import java.util.logging.LogManager;

import pt.ipb.agentapi.engine.Engine;
import pt.ipb.agentapi.engine.EngineFactory;
import pt.ipb.snmp.SnmpConstants;
import pt.ipb.snmp.SnmpProperties;

public class SnmpTestAgent {

  public static void main(String args[]) {
    System.setProperty("java.util.logging.config.file", "log.properties");
    LogManager logManager = LogManager.getLogManager();
    try {
      logManager.readConfiguration();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // The engine for this agent
    Engine snmpEngine = null;

    // We have to define the properties to pass to the engine
    // It is also possible to read them from a file.
    SnmpProperties p = new SnmpProperties();
    p.setCommunity("public");
    p.setWriteCommunity("private");
    p.setPort(10161);
    p.setVersion(SnmpConstants.SNMPv2c);

    TestAgent agent = new TestAgent();
    try {
      // EngineFactory gets the engine class name from
      // the system property pt.ipb.agentpi.engine.Engine
      // Defaults to pt.ipb.agentapi.engine.snmp.JoeSnmpEngine
      snmpEngine = EngineFactory.createEngine();
      snmpEngine.setProperties(p);
      snmpEngine.addAgent(agent);
      agent.addMessageListener(snmpEngine.createAgentListener());

      snmpEngine.open();

      System.out.println("Ready");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

