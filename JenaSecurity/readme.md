REQUIRES Jena 2.7.4 with patch from JENA-370 applied  https://issues.apache.org/jira/browse/JENA-370

JenaSecurity is a SecurityEvaluator interface and a set of dynamic proxies that apply that interface to Jena Graphs, Models, and associated methods and classes.

The SecurityEvaluator class must be implemented.  This class provides the interface to the authentication results (e.g. getPrincipal())) and the authorization system.

Create a SecuredGraph by calling Factory.getInstance( SecurityEvaluator, String, Graph );
Create a SecuredModel by calling Factory.getInstance( SecurityEvaluator, String, Model ) or ModelFactory.createModelForGraph( SecuredGraph );

NOTE: when creating a model by wrapping a secured graph (e.g. ModelFactory.createModelForGraph( SecuredGraph );) the resulting Model does not 
have the same security requirements that the standard secured model does. 

For instance when creating a list on a secured model calling model.createList( RDFNode[] ); The standard secured model verifies that the user
has the right to update the triples and allows or denies the entire operation accordingly.  The wrapped secured graph does not have visibility
to the createList() command and can only operate on the instructions issued by the model.createList() implementation.  In the standard implementation
the model requests the graph to delete one triple and then insert another.  Thus the user must have delete and add permissions, not the update permission.

There are several other cases where the difference in the layer can trip up the security system.  In all known cases the result is a tighter 
security definition than was requested.  For simplicity sake we recommend that the wrapped secured graph only be used in cases where access to the
graph as a whole is granted/denied.  In these cases the user either has all CRUD capabilities or none.
 


