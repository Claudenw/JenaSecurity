package org.xenei.jena.security.contract.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.Test;


import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;


public class ModelTestSuite extends ParentRunner<Test>
{
	private SecTestPackage pkg;
	
	public ModelTestSuite( Class<?> testClass ) throws Exception
	{
		super( Test.class );
		pkg = new SecTestPackage();
	}

	@Override
	protected List<Test> getChildren()
	{
		List<Test> lst = new ArrayList<Test>();
		Enumeration<Test> enm = pkg.tests();
		while (enm.hasMoreElements())
		{
			lst.add( enm.nextElement() );
		}
		return lst;
	}

	@Override
	protected Description describeChild( Test child )
	{
		return Description.createTestDescription( child.getClass(), child.toString() );
	}

	@Override
	protected void runChild( Test child, RunNotifier notifier )
	{
		Method setUp = null;
		try
		{
			setUp = child.getClass().getMethod("setUp" );
		}
		catch (SecurityException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RuntimeException( e1 );
		}
		catch (NoSuchMethodException e1)
		{
		}
		Method tearDown = null;
		try
		{
			tearDown = child.getClass().getMethod("tearDown" );
		}
		catch (SecurityException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RuntimeException( e1 );
		}
		catch (NoSuchMethodException e1)
		{
		}
		for (Method m : child.getClass().getMethods())
		{
			if (m.getName().startsWith( "test" ) && m.getParameterTypes().length == 0)
			{
				Description desc = Description.createTestDescription( child.getClass(), child.toString() );
				notifier.fireTestStarted( desc );
				try
				{
					if (setUp != null)
					{
						setUp.invoke(child);
					}
					m.invoke(child);
					if (tearDown != null)
					{
						tearDown.invoke( child );
					}
					notifier.fireTestFinished( desc );
				}
				catch (IllegalArgumentException e)
				{
					notifier.fireTestFailure( new Failure(desc, e));
				}
				catch (IllegalAccessException e)
				{
					notifier.fireTestFailure( new Failure(desc, e));
				}
				catch (InvocationTargetException e)
				{
					notifier.fireTestFailure( new Failure(desc, e.getTargetException()));
				}
				catch (RuntimeException e) {
					notifier.fireTestFailure( new Failure(desc, e));
					throw e;
				}
			}
		}
	}
}
