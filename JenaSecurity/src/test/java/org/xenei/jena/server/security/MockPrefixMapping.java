package org.xenei.jena.server.security;

import com.hp.hpl.jena.shared.PrefixMapping;

import java.util.Collections;
import java.util.Map;

public class MockPrefixMapping implements PrefixMapping
{

	@Override
	public String expandPrefix( final String prefixed )
	{
		return prefixed;
	}

	@Override
	public Map<String, String> getNsPrefixMap()
	{
		return Collections.emptyMap();
	}

	@Override
	public String getNsPrefixURI( final String prefix )
	{
		return null;
	}

	@Override
	public String getNsURIPrefix( final String uri )
	{
		return null;
	}

	@Override
	public PrefixMapping lock()
	{
		return this;
	}

	@Override
	public String qnameFor( final String uri )
	{
		return null;
	}

	@Override
	public PrefixMapping removeNsPrefix( final String prefix )
	{
		return this;
	}

	@Override
	public boolean samePrefixMappingAs( final PrefixMapping other )
	{
		return false;
	}

	@Override
	public PrefixMapping setNsPrefix( final String prefix, final String uri )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public PrefixMapping setNsPrefixes( final Map<String, String> map )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public PrefixMapping setNsPrefixes( final PrefixMapping other )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String shortForm( final String uri )
	{
		return uri;
	}

	@Override
	public PrefixMapping withDefaultMappings( final PrefixMapping map )
	{
		throw new UnsupportedOperationException();
	};
}