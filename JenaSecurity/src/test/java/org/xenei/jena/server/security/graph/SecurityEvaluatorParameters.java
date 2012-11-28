package org.xenei.jena.server.security.graph;

import  org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.xenei.jena.server.security.MockSecurityEvaluator;

public class SecurityEvaluatorParameters extends Suite {
	

	private class TestClassRunnerForParameters extends
			BlockJUnit4ClassRunner {
		private final int fParameterSetNumber;

		private final List<Object[]> fParameterList;

		TestClassRunnerForParameters(Class<?> type,
				List<Object[]> parameterList, int i) throws InitializationError {
			super(type);
			fParameterList= parameterList;
			fParameterSetNumber= i;
		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(
					fParameterList.get(fParameterSetNumber));
		}

		@Override
		protected String getName() {
			return String.format("[%s]", fParameterSetNumber);
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s]", method.getName(),
					fParameterList.get(fParameterSetNumber)[0]);
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}
		
		@Override
		protected Annotation[] getRunnerAnnotations() {
			return new Annotation[0];
		}
	}
	private final ArrayList<Runner> runners= new ArrayList<Runner>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public SecurityEvaluatorParameters(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());
		List<Object[]> parametersList= new ArrayList<Object[]>();

		final boolean[] bSet = { true, false };

		for (final boolean create : bSet)
		{
			for (final boolean read : bSet)
			{
				for (final boolean update : bSet)
				{
					for (final boolean delete : bSet)
					{
						parametersList.add(new Object[] { new MockSecurityEvaluator(
								true, create, read, update, delete) });
					}
				}
			}
		}
				
		for (int i= 0; i < parametersList.size(); i++)
			runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(),
					parametersList, i));
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

}