package org.xenei.jena.server.security.model;

import com.hp.hpl.jena.rdf.model.Container;

/**
 * The interface for secured Container instances.
 * 
 * Use one of the SecuredContainer derived class Factory methods to create
 * instances
 */
public interface SecuredContainer extends Container, SecuredResource
{

}
