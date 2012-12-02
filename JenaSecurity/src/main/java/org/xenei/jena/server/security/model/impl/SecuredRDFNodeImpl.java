/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xenei.jena.server.security.model.impl;

import com.hp.hpl.jena.enhanced.UnsupportedPolymorphismException;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.xenei.jena.server.security.ItemHolder;
import org.xenei.jena.server.security.SecuredItem;
import org.xenei.jena.server.security.SecuredItemImpl;
import org.xenei.jena.server.security.SecurityEvaluator;
import org.xenei.jena.server.security.model.SecuredModel;
import org.xenei.jena.server.security.model.SecuredRDFNode;
import org.xenei.jena.server.security.model.SecuredUnsupportedPolymorphismException;

/**
 * Implementation of SecuredRDFNode to be used by a SecuredItemInvoker proxy.
 */
public abstract class SecuredRDFNodeImpl extends SecuredItemImpl implements
		SecuredRDFNode
{
	// the item holder that contains this SecuredRDFNode
	private ItemHolder<? extends RDFNode, ? extends SecuredRDFNode> holder;
	// the secured securedModel that contains this node.
	private SecuredModel securedModel;

	/**
	 * Constructor
	 * 
	 * @param securityEvaluator
	 *            The security evaluator to use
	 * @param graphIRI
	 *            the graphIRI to validate against.
	 * @param holder
	 *            the item holder that will contain this SecuredRDFNode.
	 */
	protected SecuredRDFNodeImpl( SecuredModel securedModel,
			final ItemHolder<? extends RDFNode, ? extends SecuredRDFNode> holder)
	{
		super(securedModel, holder);
		if (holder.getBaseItem().getModel()==null)
		{
			throw new IllegalArgumentException( String.format( "Holder base item (%s) must have a model", holder.getBaseItem().getClass()) );
		}
		this.securedModel = securedModel;
		this.holder = holder;
	}

	private <T extends RDFNode> Method getConstructor( Class<T> view )
	{
		String classNm = SecuredRDFNodeImpl.class.getName();
		classNm = String.format("%s.Secured%sImpl",
				classNm.substring(0, classNm.lastIndexOf(".")),
				view.getSimpleName());
		try
		{
			final Class<?> c = Class.forName(classNm);
			return c.getDeclaredMethod("getInstance",
						SecuredModel.class, view);
		}
		catch (final ClassNotFoundException e)
		{
			return null;
		}
		catch (SecurityException e)
		{
			return null;
		}
		catch (NoSuchMethodException e)
		{
			return null;
		}
	}
	
	@SuppressWarnings( "unchecked" )
	@Override
	public <T extends RDFNode> T as( final Class<T> view )
	{
		checkRead();
		// see if the base Item can as
		if (holder.getBaseItem().canAs(view))
		{
			if (view.equals(SecuredRDFNodeImpl.class) || view.equals(RDFNode.class))
			{
				return (T) this;
			}
			Method m = getConstructor( view );
			if (m  == null) {
				throw new SecuredUnsupportedPolymorphismException(this, view);
			}
			try {
			return (T) m.invoke(null, securedModel, holder.getBaseItem().as(view));
			}
			catch (UnsupportedPolymorphismException e)
			{
				throw new SecuredUnsupportedPolymorphismException(this, view);
			}
			catch (final IllegalArgumentException e)
			{
				throw new RuntimeException(e);
			}
			catch (final IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
			catch (final InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			throw new SecuredUnsupportedPolymorphismException(this, view);
		}
	}

	@Override
	public Node asNode()
	{
		checkRead();
		return holder.getBaseItem().asNode();
	}

	@Override
	public <T extends RDFNode> boolean canAs( final Class<T> view )
	{
		checkRead();
		// see if the base Item can as
		if (holder.getBaseItem().canAs(view))
		{
			return getConstructor( view ) != null;
		}
		return false;
	}

	@Override
	public SecuredModel getModel()
	{
		return securedModel;
	}

	@Override
	public RDFNode inModel( final Model m )
	{
		checkRead();
		if (securedModel.equals(m))
		{
			return this;
		}
		if (m instanceof SecuredModel)
		{
			return getInstance( (SecuredModel)m, holder.getBaseItem().inModel(m) );
		}
		return holder.getBaseItem().inModel(m);
	}

	@Override
	public boolean isAnon()
	{
		return holder.getBaseItem().isAnon();
	}

	@Override
	public boolean isLiteral()
	{
		return holder.getBaseItem().isLiteral();
	}

	@Override
	public boolean isResource()
	{
		return holder.getBaseItem().isResource();
	}

	@Override
	public boolean isURIResource()
	{
		return holder.getBaseItem().isURIResource();
	}

	public static SecuredRDFNode getInstance( final SecuredModel securedModel,
			final RDFNode rdfNode )
	{
		if (rdfNode instanceof Literal)
		{
			return SecuredLiteralImpl.getInstance(securedModel, (Literal) rdfNode);
		}
		else
		{
			return SecuredResourceImpl
					.getInstance(securedModel, (Resource) rdfNode);
		}
	}

}
