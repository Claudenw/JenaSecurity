package org.xenei.jena.security.model;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.ResourceRequiredException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.MockSecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.SecurityEvaluatorParameters;
import org.xenei.jena.security.model.impl.SecuredLiteralImpl;

@RunWith( value = SecurityEvaluatorParameters.class )
public class SecuredLiteralTest extends SecuredRDFNodeTest
{

	public SecuredLiteralTest( final MockSecurityEvaluator securityEvaluator )
	{
		super(securityEvaluator);
	}

	private SecuredLiteral getSecuredLiteral()
	{
		return (SecuredLiteral) getSecuredRDFNode();
	}

	@Test
	public void sameValueAs()
	{
		try
		{
			getSecuredLiteral().sameValueAs(
					ResourceFactory.createPlainLiteral("Junk"));
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Override
	@Before
	public void setup()
	{
		super.setup();
		Literal l = ResourceFactory.createTypedLiteral("literal");
		setSecuredRDFNode(SecuredLiteralImpl.getInstance(securedModel, l), l);
	}

	@Test
	public void testAsLiteral()
	{
		getSecuredLiteral().asLiteral();
	}

	@Test
	public void testAsResource()
	{
		try
		{
			getSecuredLiteral().asResource();
			Assert.fail("Should have thrown ResoruceRequiredException");
		}
		catch (final ResourceRequiredException e)
		{
			// expected
		}
	}

	@Test
	public void testGetBoolean()
	{
		try
		{
			getSecuredLiteral().getBoolean();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
		catch (final DatatypeFormatException e)
		{
			// expected
		}
	}

	@Test
	public void testGetByte()
	{
		try
		{
			getSecuredLiteral().getByte();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
		catch (final DatatypeFormatException e)
		{
			// expected
		}
	}

	@Test
	public void testGetChar()
	{
		try
		{
			getSecuredLiteral().getChar();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
		catch (final DatatypeFormatException e)
		{
			// expected
		}
	}

	@Test
	public void testGetDatatype()
	{
		try
		{
			getSecuredLiteral().getDatatype();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetDatatypeURI()
	{
		try
		{
			getSecuredLiteral().getDatatypeURI();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetDouble()
	{
		try
		{
			getSecuredLiteral().getDouble();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
		catch (final DatatypeFormatException e)
		{
			// expected
		}

	}

	@Test
	public void testGetFloat()
	{
		try
		{
			getSecuredLiteral().getFloat();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
		catch (final DatatypeFormatException e)
		{
			// expected
		}
	}

	@Test
	public void testGetInt()
	{
		try
		{
			getSecuredLiteral().getInt();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
		catch (final DatatypeFormatException e)
		{
			// expected
		}
	}

	@Test
	public void testGetLanguage()
	{
		try
		{
			getSecuredLiteral().getLanguage();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetLexicalForm()
	{
		try
		{
			getSecuredLiteral().getLexicalForm();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testGetLong()
	{
		try
		{
			getSecuredLiteral().getLong();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
		catch (final DatatypeFormatException e)
		{
			// expected
		}
	}

	@Test
	public void testGetShort()
	{
		try
		{
			getSecuredLiteral().getShort();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
		catch (final DatatypeFormatException e)
		{
			// expected
		}
	}

	@Test
	public void testGetString()
	{
		try
		{
			getSecuredLiteral().getString();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
		catch (final DatatypeFormatException e)
		{
			// expected
		}
	}

	@Test
	public void testGetValue()
	{
		try
		{
			getSecuredLiteral().getValue();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}

	@Test
	public void testIsWellFormedXML()
	{
		try
		{
			getSecuredLiteral().isWellFormedXML();
			if (!securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail("Should have thrown AccessDenied Exception");
			}
		}
		catch (final AccessDeniedException e)
		{
			if (securityEvaluator.evaluate(Action.Read))
			{
				Assert.fail(String
						.format("Should not have thrown AccessDenied Exception: %s - %s",
								e, e.getTriple()));
			}
		}
	}
}
