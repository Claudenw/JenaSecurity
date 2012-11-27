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

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.xenei.jena.server.security.ItemHolder;
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
	// the secured model that contains this node.
	private SecuredModel model;

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
	protected SecuredRDFNodeImpl( final SecurityEvaluator securityEvaluator,
			final String graphIRI,
			final ItemHolder<? extends RDFNode, ? extends SecuredRDFNode> holder )
	{
		super(securityEvaluator, graphIRI, holder);
		if (holder.getBaseItem().getModel() != null)
		{
			this.model = org.xenei.jena.server.security.model.impl.Factory
					.getInstance(this, holder.getBaseItem().getModel());
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public <T extends RDFNode> T as( final Class<T> view )
	{
		if (canAs(view))
		{
			if (view.equals(SecuredRDFNodeImpl.class))
			{
				return (T) this;
			}
			String classNm = SecuredRDFNodeImpl.class.getName();
			classNm = String.format("%s.Secured%s",
					classNm.substring(0, classNm.lastIndexOf(".")),
					view.getSimpleName());
			final T retval = holder.getBaseItem().as(view);
			try
			{
				final Class<?> c = Class.forName(classNm);
				Method getInstance = null;
				try
				{
					getInstance = c.getDeclaredMethod("getInstance",
							SecuredItemImpl.class, view);

					return (T) getInstance.invoke(null, this, retval);
				}
				catch (final NoSuchMethodException e)
				{
					getInstance = c.getDeclaredMethod("getInstance",
							SecurityEvaluator.class, String.class, view);
					return (T) getInstance.invoke(null,
							this.getSecurityEvaluator(), this.getModelIRI(),
							retval);
				}

			}
			catch (final ClassNotFoundException e)
			{
				return retval;
			}
			catch (final NoSuchMethodException e)
			{
				throw new RuntimeException(e);
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
		return holder.getBaseItem().canAs(view);
	}

	@Override
	public Model getModel()
	{
		return model;
	}

	@Override
	public RDFNode inModel( final Model m )
	{
		checkRead();
		if (model.equals(m))
		{
			return this;
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

}
