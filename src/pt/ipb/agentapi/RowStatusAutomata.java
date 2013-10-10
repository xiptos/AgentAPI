/*
 * $Id: RowStatusAutomata.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi;

import pt.ipb.agentapi.event.ControlEvent;
import pt.ipb.agentapi.event.ControlListener;
import pt.ipb.agentapi.event.EventListenerList;

/**
 * This classe implements a Finite State Machine for RowStatus behaviour.
 */
public class RowStatusAutomata {
  int state;

  ConceptualTableRow row = null;

  EventListenerList listenerList = new EventListenerList();

  /**
   * This classe implements a Finite State Machine for RowStatus behaviour.
   */
  public RowStatusAutomata(ConceptualTableRow row) {
    this.row = row;
    state = AbstractAgent.NOT_EXISTING;
  }

  /**
   * Sets the state to NOT_EXISTING.
   */
  public void reset() {
    setState(AbstractAgent.NOT_EXISTING);
  }

  /**
   * Sets the current state and propagates it to the RowStatus column.
   */
  public void setState(int s) {
    state = s;
    row.storeState();
  }

  /**
   * Returns the current state.
   */
  public int getState() {
    return state;
  }

  /**
   * Changes state. Given input and the current state, this transitions to the
   * next state.
   * 
   * @throws MessageException
   *           with the codes INCONSISTENT_VALUE - if there is some value
   *           inconsistent with the state of some other MIB object's value.
   *           WRONG_VALUE - if there is some wrong value in the row or if the
   *           status row does not supports given value (createAndWait or
   *           notInService).
   */
  public int input(int in) throws MessageException {
    byte ret;
    switch (state) {
    case AbstractAgent.NOT_EXISTING:
      switch (in) {
      case AbstractAgent.CREATE_AND_GO:
        input(AbstractAgent.CREATE_AND_WAIT);
        input(AbstractAgent.ACTIVE);
        break;
      case AbstractAgent.CREATE_AND_WAIT:
        // may change to state NOT_IN_SERVICE or NOT_READY
        create();
        setState(AbstractAgent.NOT_IN_SERVICE);
        ret = suspend();
        if (ret == AbstractAgent.INCONSISTENT_VALUE) {
          setState(AbstractAgent.NOT_READY);
        } else if (ret == AbstractAgent.WRONG_VALUE) {
          throw new MessageException(AbstractAgent.WRONG_VALUE);
        }
        break;
      case AbstractAgent.ACTIVE:
        throw new MessageException(AbstractAgent.INCONSISTENT_VALUE);
      case AbstractAgent.NOT_IN_SERVICE:
        throw new MessageException(AbstractAgent.INCONSISTENT_VALUE);
      case AbstractAgent.DESTROY:
        destroy();
        reset();
        break;
      default:
        setState(AbstractAgent.NOT_READY);
        input(AbstractAgent.NOT_IN_SERVICE);
        break;
      }
      break;

    case AbstractAgent.NOT_READY:
      switch (in) {
      case AbstractAgent.CREATE_AND_GO:
        throw new MessageException(AbstractAgent.INCONSISTENT_VALUE);
      case AbstractAgent.CREATE_AND_WAIT:
        throw new MessageException(AbstractAgent.INCONSISTENT_VALUE);
      case AbstractAgent.ACTIVE:
        input(AbstractAgent.NOT_IN_SERVICE);
        input(AbstractAgent.ACTIVE);
        break;
      case AbstractAgent.NOT_IN_SERVICE:
        setState(AbstractAgent.NOT_IN_SERVICE);
        ret = suspend();
        if (ret != AbstractAgent.NO_ERROR) {
          setState(AbstractAgent.NOT_READY);
        }
        break;
      case AbstractAgent.DESTROY:
        destroy();
        reset();
        break;
      default:
        input(AbstractAgent.NOT_IN_SERVICE);
        break;
      }
      break;

    case AbstractAgent.NOT_IN_SERVICE:
      switch (in) {
      case AbstractAgent.CREATE_AND_GO:
        throw new MessageException(AbstractAgent.INCONSISTENT_VALUE);
      case AbstractAgent.CREATE_AND_WAIT:
        throw new MessageException(AbstractAgent.INCONSISTENT_VALUE);
      case AbstractAgent.ACTIVE:
        setState(AbstractAgent.ACTIVE);
        ret = activate();
        if (ret == AbstractAgent.NOT_READY) {
          setState(AbstractAgent.NOT_READY);
        }
        break;
      case AbstractAgent.NOT_IN_SERVICE:
        setState(AbstractAgent.NOT_IN_SERVICE);
        ret = suspend();
        if (ret == AbstractAgent.NOT_READY) {
          setState(AbstractAgent.NOT_READY);
        }
        break;
      case AbstractAgent.DESTROY:
        destroy();
        reset();
        break;
      default:
        input(AbstractAgent.NOT_IN_SERVICE);
        break;
      }
      break;

    case AbstractAgent.ACTIVE:
      switch (in) {
      case AbstractAgent.CREATE_AND_GO:
        throw new MessageException(AbstractAgent.INCONSISTENT_VALUE);
      case AbstractAgent.CREATE_AND_WAIT:
        throw new MessageException(AbstractAgent.INCONSISTENT_VALUE);
      case AbstractAgent.ACTIVE:
        setState(AbstractAgent.ACTIVE);
        ret = activate();
        if (ret == AbstractAgent.NOT_READY) {
          setState(AbstractAgent.NOT_READY);
        }
        break;
      case AbstractAgent.NOT_IN_SERVICE:
        setState(AbstractAgent.NOT_IN_SERVICE);
        ret = suspend();
        if (ret != AbstractAgent.NO_ERROR) {
          setState(AbstractAgent.NOT_READY);
        }
        break;
      case AbstractAgent.DESTROY:
        suspend();
        destroy();
        reset();
        break;
      default:
        input(AbstractAgent.ACTIVE);
        break;
      }
      break;
    }
    return state;
  }

  public void addControlListener(ControlListener l) {
    listenerList.add(ControlListener.class, l);
  }

  public void removeControlListener(ControlListener l) {
    listenerList.remove(ControlListener.class, l);
  }

  protected void fireActivate(ControlEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ControlListener.class) {
        ((ControlListener) listeners[i + 1]).activate(e);
      }
    }
  }

  protected void fireSuspend(ControlEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ControlListener.class) {
        ((ControlListener) listeners[i + 1]).suspend(e);
      }
    }
  }

  protected void fireCreate(ControlEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ControlListener.class) {
        ((ControlListener) listeners[i + 1]).create(e);
      }
    }
  }

  protected void fireDestroy(ControlEvent e) throws MessageException {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ControlListener.class) {
        ((ControlListener) listeners[i + 1]).destroy(e);
      }
    }
  }

  /**
   * Called when is received an input of createAndWait or createAndGo.
   */
  void create() {
    fireCreate(new ControlEvent(row, AbstractAgent.CREATE_AND_WAIT));
  }

  /**
   * Called when is received an input of destroy.
   */
  void destroy() throws MessageException {
    fireDestroy(new ControlEvent(row, AbstractAgent.DESTROY));
  }

  /**
   * Called when is received an input of active.
   */
  byte activate() throws MessageException {

    byte ret = AbstractAgent.NO_ERROR;

    // Could return: INCONSISTENT_VALUE, WRONG_VALUE, NOT_READY, NO_ERROR
    byte rowValueStatus = row.validate();
    if (rowValueStatus == AbstractAgent.INCONSISTENT_VALUE) {
      ret = rowValueStatus;
    } else if (rowValueStatus == AbstractAgent.WRONG_VALUE) {
      ret = rowValueStatus;
    } else if (rowValueStatus == AbstractAgent.NOT_READY) {
      ret = rowValueStatus;
    }
    fireActivate(new ControlEvent(row, AbstractAgent.ACTIVE));
    return ret;
  }

  /**
   * Called when is received an input of notInService.
   */
  byte suspend() throws MessageException {

    byte ret = AbstractAgent.NO_ERROR;

    // Could return: INCONSISTENT_VALUE, WRONG_VALUE, NOT_READY, NO_ERROR
    byte rowValueStatus = row.validate();
    if (rowValueStatus == AbstractAgent.INCONSISTENT_VALUE) {
      ret = rowValueStatus;
    } else if (rowValueStatus == AbstractAgent.WRONG_VALUE) {
      ret = rowValueStatus;
    } else if (rowValueStatus == AbstractAgent.NOT_READY) {
      ret = rowValueStatus;
    }
    fireSuspend(new ControlEvent(row, AbstractAgent.NOT_IN_SERVICE));
    return ret;
  }

}