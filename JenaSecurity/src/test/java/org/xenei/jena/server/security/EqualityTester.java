package org.xenei.jena.server.security;

import junit.framework.Assert;

public class EqualityTester
{

	public static void testEquality( final String label, final Object o1,
			final Object o2 )
	{
		Assert.assertEquals(label, o1, o2);
		Assert.assertEquals(label + " inverse", o2, o1);
		Assert.assertEquals(label + " hashCode", o1.hashCode(), o2.hashCode());
	}

	public static void testInequality( final String label, final Object o1,
			final Object o2 )
	{
		if ((o1 == null) && (o2 == null))
		{
			Assert.fail(label + ": both arguments are null");
		}
		if ((o1 == null) || (o2 == null))
		{
			return;
		}
		Assert.assertFalse(label, o1.equals(o2));
		Assert.assertFalse(label, o2.equals(o1));
	}

}
