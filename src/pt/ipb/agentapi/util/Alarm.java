/*
 * $Id: Alarm.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.util;

/**
 * Alarm thread. It calls a method when a timeout occurs. Even during calling
 * the clock does not stop.
 */
public class Alarm implements Runnable {

  private volatile Thread alarmThread = null;

  Called called = null;

  long period = 0;

  /**
   * The called object.
   */
  public Alarm() {
  }

  public void addCalled(Called l) {
    called = CallMulticaster.add(called, l);
  }

  public void removeCalled(Called l) {
    called = CallMulticaster.remove(called, l);
  }

  public void processEvent() {
    if (called != null) {
      called.callback();
    }
  }

  /**
   * The period in seconds.
   */
  public synchronized void setPeriod(long n) {
    this.period = n;
  }

  public synchronized long getPeriod() {
    return this.period;
  }

  /**
   * Start counting.
   */
  public void start() {
    if (period == 0)
      return;
    alarmThread = new Thread(this);
    alarmThread.start();
  }

  /**
   * Stop alarm clock.
   */
  public void stop() {
    alarmThread = null;
  }

  public void run() {
    Thread myThread = Thread.currentThread();
    while (alarmThread == myThread) {
      doCall();
      try {
        Thread.sleep(getPeriod());
      } catch (Exception e) {
      }
    }
  }

  class CallingClass implements Runnable {
    public void run() {
      called.callback();
    }
  }

  void doCall() {
    Thread t = new Thread(new CallingClass());
    t.start();
  }

}