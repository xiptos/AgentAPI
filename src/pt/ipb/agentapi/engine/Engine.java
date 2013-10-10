/*
 * $Id: Engine.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.engine;

import java.util.Properties;

import pt.ipb.agentapi.Agent;
import pt.ipb.agentapi.event.MessageListener;

/**
 * This class acts as a proxy SNMP/Method invocation. SNMP (Get, GetNext,
 * GetBulk, Set) ---> Agent SNMP (GetResp, GetNextResp, GetBulkResp, SetResp)
 * <--- MessageListener It uses virtually any stack through EngineFactory.
 */
public interface Engine {
  /**
   * Starts engine
   */
  public void open() throws Exception;

  /**
   * Closes the session.
   */
  public void close();

  /**
   * To register listening Agents
   */
  public void addAgent(Agent l);

  /**
   * To remove Agents
   */
  public void removeAgent(Agent l);

  /**
   * To set the Properties needed by the Engine implementation.
   */
  public void setProperties(Properties p);

  /**
   * Gets an inner engine class to receive messages from the agent. The messages
   * are then forward to the manager.
   */
  public MessageListener createAgentListener();

}