package org.xenei.jena.security;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;

public class CachedSecurityEvaluator implements InvocationHandler
{
	private final SecurityEvaluator wrapped;
	private final Principal origPrincipal;

	private static Method GET_PRINCIPAL;

	static
	{
		try
		{
			CachedSecurityEvaluator.GET_PRINCIPAL = SecurityEvaluator.class
					.getMethod("getPrincipal");
		}
		catch (final SecurityException e)
		{
			throw new RuntimeException(e);
		}
		catch (final NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static SecurityEvaluator getInstance(
			final SecurityEvaluator evaluator, final Principal runAs )
	{
		final Set<Class<?>> ifac = new LinkedHashSet<Class<?>>();
		if (evaluator.getClass().isInterface())
		{
			ifac.add(evaluator.getClass());
		}
		ifac.addAll(ClassUtils.getAllInterfaces(evaluator.getClass()));

		return (SecurityEvaluator) Proxy.newProxyInstance(
				SecuredItemImpl.class.getClassLoader(),
				ifac.toArray(new Class<?>[ifac.size()]),
				new CachedSecurityEvaluator(evaluator, runAs));
	}

	private CachedSecurityEvaluator( final SecurityEvaluator wrapped,
			final Principal runAs )
	{
		origPrincipal = runAs;
		this.wrapped = wrapped;
	}

	@Override
	public Object invoke( final Object proxy, final Method method,
			final Object[] args ) throws Throwable
	{
		// check for the special case methods
		if (CachedSecurityEvaluator.GET_PRINCIPAL.equals(method))
		{
			return origPrincipal;
		}

		// if we get here then the method is not being proxied so call the
		// original method
		// on the base item.
		return method.invoke(wrapped, args);

	}
}
