/*
 * $Id: TaskReaderFactory.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.macros;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import pt.ipb.snmp.SnmpProperties;

/**
 * A TaskReaderFactory instance can be used to create TaskReader objects. The
 * system property that determines which TaskReader implementation to create is
 * named "pt.ipb.agentapi.macros.TaskReader". This property names a concrete
 * subclass of the TaskReader interface. If the property is not defined, a
 * platform default is used.
 */
public final class TaskReaderFactory {

  /**
   * Private constructor.
   * 
   * <p>
   * This constructor prevents the class from being instantiated.
   * </p>
   */
  private TaskReaderFactory() {
  }

  public static TaskReader createTaskReader() throws TaskException {
    String className = System.getProperty("pt.ipb.agentapi.engine.TaskReader");
    if (className != null) {
      return createTaskReader(className);
    } else {
      throw new TaskException("No TaskReader class");
    }
  }

  public static TaskReader createTaskReader(String className)
      throws TaskException {
    try {
      return (TaskReader) (Class.forName(className).newInstance());
    } catch (ClassNotFoundException e1) {
      throw new TaskException("TaskReader class " + className + " not found",
          e1);
    } catch (IllegalAccessException e2) {
      throw new TaskException("TaskReader class " + className
          + " found but cannot be loaded", e2);
    } catch (InstantiationException e3) {
      throw new TaskException(
          "TaskReader class "
              + className
              + " loaded but cannot be instantiated (no empty public constructor?)",
          e3);
    } catch (ClassCastException e4) {
      throw new TaskException("TaskReader class " + className
          + " does not implement TaskReader", e4);
    }
  }

  public static TaskReader createSnmpTaskReader(String className,
      SnmpProperties props, String oid) throws TaskException {
    try {
      Class readerDefinition = Class.forName(className);
      Class[] argsClass = new Class[] { props.getClass(), oid.getClass() };
      Constructor readerConstructor = readerDefinition
          .getConstructor(argsClass);

      Object[] args = new Object[] { props, oid };
      return (TaskReader) readerConstructor.newInstance(args);

    } catch (InvocationTargetException e1) {
      throw new TaskException("TaskReader class " + className
          + " cannot be invoked.", e1);

    } catch (NoSuchMethodException e1) {
      throw new TaskException("TaskReader class " + className
          + " does not have the appropriate constructor.", e1);

    } catch (ClassNotFoundException e1) {
      throw new TaskException("TaskReader class " + className + " not found",
          e1);
    } catch (IllegalAccessException e2) {
      throw new TaskException("TaskReader class " + className
          + " found but cannot be loaded", e2);
    } catch (InstantiationException e3) {
      throw new TaskException(
          "TaskReader class "
              + className
              + " loaded but cannot be instantiated (no empty public constructor?)",
          e3);
    } catch (ClassCastException e4) {
      throw new TaskException("TaskReader class " + className
          + " does not implement TaskReader", e4);
    }
  }

  public static TaskReader createStreamTaskReader(String className,
      InputStream stream) throws TaskException {
    try {
      Class readerDefinition = Class.forName(className);
      Class[] argsClass = new Class[] { stream.getClass() };
      Constructor readerConstructor = readerDefinition
          .getConstructor(argsClass);

      Object[] args = new Object[] { stream };
      return (TaskReader) readerConstructor.newInstance(args);

    } catch (InvocationTargetException e1) {
      throw new TaskException("TaskReader class " + className
          + " cannot be invoked.", e1);

    } catch (NoSuchMethodException e1) {
      throw new TaskException("TaskReader class " + className
          + " does not have the appropriate constructor.", e1);

    } catch (ClassNotFoundException e1) {
      throw new TaskException("TaskReader class " + className + " not found",
          e1);
    } catch (IllegalAccessException e2) {
      throw new TaskException("TaskReader class " + className
          + " found but cannot be loaded", e2);
    } catch (InstantiationException e3) {
      throw new TaskException(
          "TaskReader class "
              + className
              + " loaded but cannot be instantiated (no empty public constructor?)",
          e3);
    } catch (ClassCastException e4) {
      throw new TaskException("TaskReader class " + className
          + " does not implement TaskReader", e4);
    }
  }
}