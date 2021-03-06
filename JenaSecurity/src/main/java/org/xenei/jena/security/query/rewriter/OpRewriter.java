package org.xenei.jena.security.query.rewriter;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVisitor;
import com.hp.hpl.jena.sparql.algebra.op.Op1;
import com.hp.hpl.jena.sparql.algebra.op.Op2;
import com.hp.hpl.jena.sparql.algebra.op.OpAssign;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpConditional;
import com.hp.hpl.jena.sparql.algebra.op.OpDatasetNames;
import com.hp.hpl.jena.sparql.algebra.op.OpDiff;
import com.hp.hpl.jena.sparql.algebra.op.OpDisjunction;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpExt;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpLabel;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpList;
import com.hp.hpl.jena.sparql.algebra.op.OpMinus;
import com.hp.hpl.jena.sparql.algebra.op.OpN;
import com.hp.hpl.jena.sparql.algebra.op.OpNull;
import com.hp.hpl.jena.sparql.algebra.op.OpOrder;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpProcedure;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpPropFunc;
import com.hp.hpl.jena.sparql.algebra.op.OpQuad;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadPattern;
import com.hp.hpl.jena.sparql.algebra.op.OpReduced;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpSlice;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpTopN;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Function;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xenei.jena.security.AccessDeniedException;
import org.xenei.jena.security.SecuredItemImpl;
import org.xenei.jena.security.SecurityEvaluator;
import org.xenei.jena.security.SecurityEvaluator.Action;
import org.xenei.jena.security.SecurityEvaluator.SecNode;
import org.xenei.jena.security.SecurityEvaluator.SecTriple;

/**
 * This class rewrites the query by examining each operation in the algebra
 * returned by the Jena SPARQL parser.
 * 
 * The operations are placed in the start sequence, end sequence or distributed
 * via the ops object.
 * 
 * Some operations (e.g. Slice which implements limit) are passed both to the
 * endpoints and then applied on the
 * result of the union of the endpoint operators. This is handled by placing
 * those operations on the revisitStack.
 * getResult() then pops the operations off the revisit stack and uses the
 * Revisitor class to apply the operation.
 * 
 * 
 * 
 * @author claude
 * 
 */
public class OpRewriter implements OpVisitor
{
	private static Logger LOG = LoggerFactory.getLogger(OpRewriter.class);
	private OpSequence result;
	private final SecNode graphIRI;
	private final SecurityEvaluator securityEvaluator;
	private final boolean silentFail;

	public OpRewriter( final SecurityEvaluator securityEvaluator,
			final SecNode graphIRI )
	{
		this.securityEvaluator = securityEvaluator;
		this.graphIRI = graphIRI;
		this.silentFail = false;
		reset();
	}

	public OpRewriter( final SecurityEvaluator securityEvaluator,
			final String graphIRI )
	{
		this(securityEvaluator, new SecNode(SecNode.Type.URI, graphIRI));
	}

	private void addOp( final Op op )
	{
		result.add(op);
	}

	public Op getResult()
	{
		if (result.size() == 0)
		{
			return OpNull.create();
		}
		if (result.size() == 1)
		{
			return result.get(0);
		}
		return result;
		
	}

	/**
	 * Record variables and create variable names for blank nodes.
	 * 
	 * @param n
	 * @param variables
	 */
	private Node processBGPNode( final Node n, final List<Node> variables )
	{
		if (n.isVariable() && !variables.contains(n))
		{
			variables.add(n);
		}
		return n;
	}

	public OpRewriter reset()
	{
		result = OpSequence.create();
		return this;
	}

	private Triple rewriteBGPTriple( final Triple t,
			final List<Node> variables )
	{
		final Node s = processBGPNode(t.getSubject(), variables);
		final Node p = processBGPNode(t.getPredicate(), variables);
		final Node o = processBGPNode(t.getObject(), variables);
		return new Triple(s, p, o);
	}

	/**
	 * Rewrites the subop of op1 and returns the result.
	 * 
	 * @param op1
	 * @return
	 */
	private Op rewriteOp1( final Op1 op1 )
	{
		final OpRewriter rewriter = new OpRewriter(securityEvaluator, graphIRI);
		op1.getSubOp().visit(rewriter);
		return rewriter.getResult();
	}

	/**
	 * rewrites the left and right parts of the op2 the left part is
	 * returned the right part is placed in the rewriter
	 * 
	 * @param op2
	 * @param rewriter
	 * @return
	 */
	private Op rewriteOp2( final Op2 op2, final OpRewriter rewriter )
	{
		op2.getLeft().visit(rewriter.reset());
		final Op left = rewriter.getResult();
		op2.getRight().visit(rewriter.reset());
		return left;
	}

	/**
	 * rewrite source to dest and returns dest
	 * 
	 * @param source
	 * @param dest
	 * @return
	 */
	private OpN rewriteOpN( final OpN source, final OpN dest )
	{
		final OpRewriter rewriter = new OpRewriter(securityEvaluator, graphIRI);
		for (final Op o : source.getElements())
		{
			o.visit(rewriter.reset());
			dest.add(rewriter.getResult());
		}
		return dest;
	}

	/**
	 * rewrites the subop of assign.
	 */
	@Override
	public void visit( final OpAssign opAssign )
	{
		addOp(OpAssign.assign(rewriteOp1(opAssign), opAssign.getVarExprList()));
	}

	@Override
	public void visit( final OpBGP opBGP )
	{
		if (!securityEvaluator.evaluate(Action.Read, graphIRI))
		{
			if (silentFail)
			{
				return;
			}
			else
			{
				throw new AccessDeniedException(graphIRI, Action.Read);
			}
		}

		if (securityEvaluator.evaluate(Action.Read, graphIRI, SecTriple.ANY))
		{
			addOp(opBGP);
		}
		else
		{

			final List<Triple> newBGP = new ArrayList<Triple>();
			final List<Node> variables = new ArrayList<Node>();
			// register all variables
			for (final Triple t : opBGP.getPattern().getList())
			{
				newBGP.add(rewriteBGPTriple(t, variables));
			}

			final SecuredFunction secFunc = new SecuredFunction(graphIRI,
					securityEvaluator, variables, newBGP);
			Op filter = OpFilter.filter(secFunc, new OpBGP(BasicPattern.wrap(newBGP)));
	
			addOp(filter);
			//addOp( new OpBGP(BasicPattern.wrap(newBGP)) );
		}
	}

	/**
	 * Rewrite left and right
	 */
	@Override
	public void visit( final OpConditional opCondition )
	{
		final OpRewriter rewriter = new OpRewriter(securityEvaluator, graphIRI);
		addOp(new OpConditional(rewriteOp2(opCondition, rewriter),
				rewriter.getResult()));
	}

	/**
	 * returns the dsNames
	 */
	@Override
	public void visit( final OpDatasetNames dsNames )
	{
		addOp(dsNames);
	}

	/**
	 * Rewrite left and right
	 */
	@Override
	public void visit( final OpDiff opDiff )
	{
		final OpRewriter rewriter = new OpRewriter(securityEvaluator, graphIRI);
		addOp(OpDiff.create(rewriteOp2(opDiff, rewriter), rewriter.getResult()));
	}

	/**
	 * Rewrite sequence elements
	 */
	@Override
	public void visit( final OpDisjunction opDisjunction )
	{
		addOp(rewriteOpN(opDisjunction, OpDisjunction.create()));
	}

	/**
	 * rewrites the subop of distinct
	 */
	@Override
	public void visit( final OpDistinct opDistinct )
	{
		addOp(new OpDistinct(rewriteOp1(opDistinct)));
	}

	/**
	 * Returns the Ext
	 */
	@Override
	public void visit( final OpExt opExt )
	{
		addOp(opExt);
	}

	/**
	 * rewrites the subop of extend.
	 */
	@Override
	public void visit( final OpExtend opExtend )
	{
		addOp(OpExtend.extend(rewriteOp1(opExtend), opExtend.getVarExprList()));
	}

	/**
	 * rewrites the subop of filter.
	 */
	@Override
	public void visit( final OpFilter opFilter )
	{
		addOp(OpFilter.filter(opFilter.getExprs(), rewriteOp1(opFilter)));
	}

	/**
	 * rewrites the subop of graph.
	 */
	@Override
	public void visit( final OpGraph opGraph )
	{
		final OpRewriter rewriter = new OpRewriter(securityEvaluator,
				SecuredItemImpl.convert(opGraph.getNode()));
		opGraph.getSubOp().visit(rewriter);
		addOp(new OpGraph(opGraph.getNode(), rewriter.getResult()));
	}

	/**
	 * rewrites the subop of group.
	 */
	@Override
	public void visit( final OpGroup opGroup )
	{
		addOp(new OpGroup(rewriteOp1(opGroup), opGroup.getGroupVars(),
				opGroup.getAggregators()));
	}

	/**
	 * Parses the joins and recursively calls the left and right parts
	 */
	@Override
	public void visit( final OpJoin opJoin )
	{
		final OpRewriter rewriter = new OpRewriter(securityEvaluator, graphIRI);
		addOp(OpJoin.create(rewriteOp2(opJoin, rewriter), rewriter.getResult()));
	}

	/**
	 * returns the label
	 */
	@Override
	public void visit( final OpLabel opLabel )
	{
		addOp(opLabel);
	}

	/**
	 * Parses the joins and recursively calls the left and right parts
	 */
	@Override
	public void visit( final OpLeftJoin opLeftJoin )
	{
		final OpRewriter rewriter = new OpRewriter(securityEvaluator, graphIRI);
		addOp(OpLeftJoin.create(rewriteOp2(opLeftJoin, rewriter),
				rewriter.getResult(), opLeftJoin.getExprs()));
	}

	/**
	 * rewrites the subop of list.
	 */
	@Override
	public void visit( final OpList opList )
	{
		addOp(new OpList(rewriteOp1(opList)));
	}

	/**
	 * Rewrite left and right
	 */
	@Override
	public void visit( final OpMinus opMinus )
	{
		final OpRewriter rewriter = new OpRewriter(securityEvaluator, graphIRI);
		addOp(OpMinus.create(rewriteOp2(opMinus, rewriter),
				rewriter.getResult()));
	}

	/**
	 * returns the null
	 */
	@Override
	public void visit( final OpNull opNull )
	{
		addOp(opNull);
	}

	/**
	 * rewrites the subop of order.
	 */
	@Override
	public void visit( final OpOrder opOrder )
	{
		addOp(new OpOrder(rewriteOp1(opOrder), opOrder.getConditions()));
	}

	/**
	 * Returns the path
	 */
	@Override
	public void visit( final OpPath opPath )
	{
		addOp(opPath);
	}

	/**
	 * rewrites the subop of proc.
	 */
	@Override
	public void visit( final OpProcedure opProc )
	{
		if (opProc.getProcId() != null)
		{
			addOp(new OpProcedure(opProc.getProcId(), opProc.getArgs(),
					rewriteOp1(opProc)));
		}
		else
		{
			addOp(new OpProcedure(opProc.getURI(), opProc.getArgs(),
					rewriteOp1(opProc)));
		}
	}

	/**
	 * rewrites the subop of project.
	 */
	@Override
	public void visit( final OpProject opProject )
	{
		addOp(new OpProject(rewriteOp1(opProject), opProject.getVars()));
	}

	/**
	 * rewrites the subop of propFunc.
	 */
	@Override
	public void visit( final OpPropFunc opPropFunc )
	{
		addOp(new OpPropFunc(opPropFunc.getProperty(),
				opPropFunc.getSubjectArgs(), opPropFunc.getObjectArgs(),
				rewriteOp1(opPropFunc)));
	}

	/**
	 * Returns the quad
	 */
	@Override
	public void visit( final OpQuad opQuad )
	{
		addOp(opQuad);
	}

	/**
	 * Returns the quadpattern
	 */
	@Override
	public void visit( final OpQuadPattern quadPattern )
	{
		addOp(quadPattern);
	}

	/**
	 * rewrites the subop of reduced.
	 */
	@Override
	public void visit( final OpReduced opReduced )
	{
		addOp(OpReduced.create(rewriteOp1(opReduced)));
	}

	/**
	 * Rewrite sequence elements
	 */
	@Override
	public void visit( final OpSequence opSequence )
	{
		addOp(rewriteOpN(opSequence, OpSequence.create()));
	}

	/**
	 * returns the service
	 */
	@Override
	public void visit( final OpService opService )
	{
		addOp(opService);
	}

	/**
	 * rewrites the subop of slice
	 * 
	 * This also handles the limit case
	 */
	@Override
	public void visit( final OpSlice opSlice )
	{
		addOp(opSlice);
	}

	/**
	 * returns the table
	 */
	@Override
	public void visit( final OpTable opTable )
	{
		addOp(opTable);
	}

	/**
	 * rewrites the subop of top.
	 */
	@Override
	public void visit( final OpTopN opTop )
	{
		addOp(new OpTopN(rewriteOp1(opTop), opTop.getLimit(),
				opTop.getConditions()));
	}

	/**
	 * Converts to BGP
	 */
	@Override
	public void visit( final OpTriple opTriple )
	{
		visit(opTriple.asBGP());
	}

	/**
	 * Rewrite left and right
	 */
	@Override
	public void visit( final OpUnion opUnion )
	{
		final OpRewriter rewriter = new OpRewriter(securityEvaluator, graphIRI);
		addOp(OpUnion.create(rewriteOp2(opUnion, rewriter),
				rewriter.getResult()));
	}
}
