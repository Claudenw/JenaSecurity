package org.xenei.jena.security.query.rewriter;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprFunction;
import com.hp.hpl.jena.sparql.expr.ExprFunctionN;
import com.hp.hpl.jena.sparql.expr.ExprFunctionOp;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.ExprVisitor;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase;
import com.hp.hpl.jena.sparql.function.FunctionBase0;
import com.hp.hpl.jena.sparql.function.FunctionEnv;
import com.hp.hpl.jena.sparql.graph.NodeTransform;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;

import java.util.List;
import java.util.Map;

import org.xenei.jena.security.SecuredItemImpl;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.SecurityEvaluator.SecNode;
import org.xenei.jena.security.SecurityEvaluator.SecTriple;

public class SecuredFunction extends ExprFunctionN
{
	private final SecurityEvaluator securityEvaluator;
	private final List<Node> variables;
	private final List<Triple> bgp;
	private final SecNode graphIRI;

	public SecuredFunction( final SecNode graphIRI,
			final SecurityEvaluator securityEvaluator,
			final List<Node> variables, final List<Triple> bgp )
	{
		super("<java:" + SecuredFunction.class.getName() + ">" );
		//, 
		//		new ElementTriplesBlock( BasicPattern.wrap(bgp) ),
		//		new OpBGP( BasicPattern.wrap(bgp) )
		//		);
		this.securityEvaluator = securityEvaluator;
		this.variables = variables;
		this.bgp = bgp;
		this.graphIRI = graphIRI;
	}
	
	private boolean checkAccess( Binding values )
	{
		for (final Triple t : bgp)
		{
			final SecTriple secT = createSecTriple(t, values);
			if (!securityEvaluator.evaluate(Action.Read, graphIRI, secT))
			{
				return false;
			}
		}
		return true;
	}

	private SecTriple createSecTriple( final Triple t, final Binding values )
	{
		int idx = variables.indexOf(t.getSubject());

		final SecNode s = SecuredItemImpl.convert(idx ==-1 ? t.getSubject()
				: values.get(Var.alloc( variables.get(idx))));

		idx = variables.indexOf(t.getPredicate());
		final SecNode p = SecuredItemImpl.convert(idx == -1 ? t
				.getPredicate() 
				: values.get(Var.alloc( variables.get(idx))));
		idx = variables.indexOf(t.getObject());
		final SecNode o = SecuredItemImpl.convert(idx == -1 ? t.getObject()
				: values.get(Var.alloc( variables.get(idx))));
		return new SecTriple(s, p, o);
	}


	@Override
	public Expr copySubstitute( Binding binding, boolean foldConstants )
	{
		return this;
	}

	@Override
	public Expr applyNodeTransform( NodeTransform transform )
	{
		return this;
	}

	@Override
	public void visit( ExprVisitor visitor )
	{
		visitor.visit( this );
	}

	@Override
	public Expr getArg( int i )
	{
		if (i<=variables.size())
		{
			return new ExprVar( variables.get(i-1));
		}
		return null;
	}

	@Override
	public int numArgs()
	{
		return variables.size();
	}
	
	//NodeValue s = evalSpecial(binding, env) ;

	@Override
	protected NodeValue eval( List<NodeValue> args )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Expr copy( ExprList newArgs )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected NodeValue evalSpecial( Binding binding, FunctionEnv env )
	{
		return NodeValue.booleanReturn( checkAccess( binding ));
	}


	
}
