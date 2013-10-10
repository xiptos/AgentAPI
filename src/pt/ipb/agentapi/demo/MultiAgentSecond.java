/*
 * $Id: MultiAgentSecond.java 3 2004-08-03 10:42:11Z rlopes $
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
import pt.ipb.agentapi.Agent;
import pt.ipb.agentapi.Table;

/**
 * This agent is used as an example to the MultiAgentExample toghether with
 * MultiAgentFirst.
 * 
 * @see MultiAgentFirst
 * @see MultiAgentExample
 */
public class MultiAgentSecond extends AbstractAgent {

  public final static String ATTABLE = ".1.3.6.1.2.1.3.1";

  public MultiAgentSecond() {
    super();
  }

  public void setObjects() {
    Table ct = new Table(ATTABLE, new AtTable());
    addObject(ct);
  }

  public static void main(String arg[]) {
    try {
      Agent agent = new MultiAgentSecond();
      pt.ipb.agentapi.engine.EngineFactory.start(agent);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

