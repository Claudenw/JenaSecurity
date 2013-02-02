package org.xenei.jena.security;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.JA;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.assembler.assemblers.AssemblerGroup;
import com.hp.hpl.jena.assembler.assemblers.ModelAssembler;
import com.hp.hpl.jena.assembler.exceptions.AssemblerException;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.util.MappingRegistry;
import com.hp.hpl.jena.tdb.sys.SystemTDB;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;



public class SecuredAssembler extends ModelAssembler {
	private static boolean initialized;
	
	public static final String URI = "http://xenei.org/jena/security/Assembler#";
	public static final Property EVALUATOR_FACTORY =  
			ResourceFactory.createProperty( URI + "evaluatorFactory" );
    public static final Property SECURED_MODEL = ResourceFactory.createProperty( URI + "Model" ); 
    public static final Property BASE_MODEL = ResourceFactory.createProperty( URI + "baseModel" ); 
	
	static { init() ; }
    
    static synchronized public void init()
    {
        if ( initialized )
            return ;
        MappingRegistry.addPrefixMapping("sec", URI) ;
        registerWith(Assembler.general) ;
        initialized = true ;
    }
    
    static void registerWith(AssemblerGroup g)
    {
        // Wire in the extension assemblers (extensions relative to the Jena assembler framework)
        // Domain and range for properties.
        // Separated and use ja:imports
        assemblerClass(g, SECURED_MODEL, new SecuredAssembler()) ;
       
    }
	
    public static void assemblerClass(AssemblerGroup group, Resource r, Assembler a)
    {
        if ( group == null )
            group = Assembler.general ;
        group.implementWith(r, a) ;
    }
	
	@Override
	public Model open(Assembler a, Resource root, Mode mode) {

		List<Statement> lst = root.listProperties().toList();
		Resource rootModel = getUniqueResource( root, BASE_MODEL );
		if (rootModel == null)
		{
			throw new AssemblerException( root, String.format( "No %s provided for %s", BASE_MODEL, root ));
		}
		Model baseModel = a.openModel(rootModel, mode); 
		
	
		Literal modelName = getUniqueLiteral( root, JA.modelName );
		if (modelName == null)
		{
			throw new AssemblerException( root, String.format( "No %s provided for %s", JA.modelName, root ));
		}
		

		Literal factoryName = getUniqueLiteral( root, EVALUATOR_FACTORY );
		if (factoryName == null)
		{
			throw new AssemblerException( root, String.format( "No %s provided for %s", EVALUATOR_FACTORY, root ));
		}
		SecurityEvaluator securityEvaluator = null;
		try
		{
			Class<?> factoryClass = Class.forName( factoryName.getString() );
			Method method = factoryClass.getMethod("getInstance" );
			if ( ! SecurityEvaluator.class.isAssignableFrom(method.getReturnType()))
			{
				throw new AssemblerException( root, String.format( "%s (found at %s for %s) getInstance() must return an instance of SecurityEvaluator", factoryName, EVALUATOR_FACTORY, root ));
			}
			if ( ! Modifier.isStatic( method.getModifiers()))
			{
				throw new AssemblerException( root, String.format( "%s (found at %s for %s) getInstance() must be a static method", factoryName, EVALUATOR_FACTORY, root ));			
			}
			securityEvaluator = (SecurityEvaluator) method.invoke( null );
		}
		catch (SecurityException e)
		{
			throw new AssemblerException( root, String.format( "Error finding factory class %s:  %s", factoryName, e.getMessage() ), e);			
		}
		catch (IllegalArgumentException e)
		{
			throw new AssemblerException( root, String.format( "Error finding factory class %s:  %s", factoryName, e.getMessage() ), e);			
		}
		catch (ClassNotFoundException e)
		{
			throw new AssemblerException( root, String.format( "Class %s (found at %s for %s) could not be loaded", factoryName, EVALUATOR_FACTORY, root ));
		}
		catch (NoSuchMethodException e)
		{
			throw new AssemblerException( root, String.format( "%s (found at %s for %s) must implement a static getInstance() that returns an instance of SecurityEvaluator", factoryName, EVALUATOR_FACTORY, root ));
		}
		catch (IllegalAccessException e)
		{
			throw new AssemblerException( root, String.format( "Error finding factory class %s:  %s", factoryName, e.getMessage() ), e);			
		}
		catch (InvocationTargetException e)
		{
			throw new AssemblerException( root, String.format( "Error finding factory class %s:  %s", factoryName, e.getMessage() ), e);			
		}

		
		return Factory.getInstance(securityEvaluator, modelName.asLiteral().getString(), baseModel);
			
	}

	@Override
	protected Model openEmptyModel(Assembler a, Resource root, Mode mode) {
		return open(a, root, mode);
	}


}
