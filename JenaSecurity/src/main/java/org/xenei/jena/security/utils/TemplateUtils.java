/*-- $Id: TemplateUtils.java,v 1.3 2011/01/23 20:46:45 claudenw Exp $ --

     A set of static methods to retrieve template class information from 
     templated classes. 
     Copyright (C) 2010 by Xenei.com

     This program is free software; you may redistribute and/or modify it under
     the terms and conditions of the Academic Free License, as published by the
     Open Source Initiative at http://www.opensource.org/licenses/academic.php.

     This program is distributed in the hope it will be useful but WITHOUT ANY
     WARRANTY, and without any implied warranty of MERCHANTABILITY or FITNESS
     FOR A PARTICULAR PURPOSE. See the Academic Free License for more details.

     You should have received a copy of the Academic Free License along with
     this program, see the file LICENSE.txt; if not, use the URL shown above.

     $Revision: 1.3 $
     $State: Exp $
 */

package org.xenei.jena.security.utils;

import java.lang.reflect.ParameterizedType;

/**
 * A set of methods to retrieve class template information from templated
 * classes.
 * 
 */
public class TemplateUtils
{

	/**
	 * Get the first class for the first template of the parent class.
	 * 
	 * @param parent
	 *            A templated class
	 * @return The first class specified in the template.
	 */
	public static Class<?> getTemplateClass( final Class<?> parent )
	{
		return TemplateUtils.getTemplateClass(parent, 0);
	}

	/**
	 * The the idxth class for the first template of the parent class.
	 * 
	 * @param parent
	 *            a templated class
	 * @param idx
	 *            the index (zero based) of the template class to retrieve
	 * @return the class specified in the template
	 */
	public static Class<?> getTemplateClass( final Class<?> parent,
			final int idx )
	{
		parent.getGenericSuperclass();
		return ((Class<?>) ((ParameterizedType) parent.getGenericSuperclass())
				.getActualTypeArguments()[idx]);
	}

	/**
	 * determine if the parent is a templated class
	 * 
	 * @param parent
	 * @return
	 */
	public static boolean isTemplatedClass( final Class<?> parent )
	{
		return parent.getGenericSuperclass() instanceof ParameterizedType;
	}
}
