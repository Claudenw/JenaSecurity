JenaSecurity is a SecurityEvaluator interface and a set of dynamic proxies that apply that interface to Jena Graphs, Models, and associated methods and classes.

The SecurityEvaluator class must be implemented.  This class provides the interface to the authentication results (e.g. getPrincipal())) and the authorization system.

Create a SecuredGraph by calling Factory.getInstance( SecurityEvaluator, String, Graph );
Create a SecuredModel by calling Factory.getInstance( SecurityEvaluator, String, Model ) or ModelFactory.createModelForGraph( SecuredGraph );


