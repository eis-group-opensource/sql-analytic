package com.github.sql.analytic.odata;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;

import com.github.sql.analytic.expression.BinaryExpression;
import com.github.sql.analytic.expression.Function;
import com.github.sql.analytic.expression.InverseExpression;
import com.github.sql.analytic.expression.LongValue;
import com.github.sql.analytic.expression.Parenthesis;
import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.expression.StringValue;
import com.github.sql.analytic.expression.operators.arithmetic.Addition;
import com.github.sql.analytic.expression.operators.arithmetic.Division;
import com.github.sql.analytic.expression.operators.arithmetic.Multiplication;
import com.github.sql.analytic.expression.operators.arithmetic.Subtraction;
import com.github.sql.analytic.expression.operators.conditional.AndExpression;
import com.github.sql.analytic.expression.operators.conditional.OrExpression;
import com.github.sql.analytic.expression.operators.relational.EqualsTo;
import com.github.sql.analytic.expression.operators.relational.ExpressionList;
import com.github.sql.analytic.expression.operators.relational.GreaterThan;
import com.github.sql.analytic.expression.operators.relational.GreaterThanEquals;
import com.github.sql.analytic.expression.operators.relational.LikeExpression;
import com.github.sql.analytic.expression.operators.relational.MinorThan;
import com.github.sql.analytic.expression.operators.relational.MinorThanEquals;
import com.github.sql.analytic.expression.operators.relational.NotEqualsTo;
import com.github.sql.analytic.expression.operators.string.Concat;
import com.github.sql.analytic.schema.Column;
import com.github.sql.analytic.schema.Table;

public class FilterExpressionVisitor  implements ExpressionVisitor<SQLExpression> {

	private static final StringValue MATCH_ANY = new StringValue("'%'");
	private String alias;

	public FilterExpressionVisitor(String string) {
		this.alias = string;
	}

	@Override
	public SQLExpression visitBinaryOperator(BinaryOperatorKind operator, SQLExpression left, SQLExpression right)
			throws ExpressionVisitException, ODataApplicationException {
		switch (operator){
		case  MUL: return new Multiplication().setLeftExpression(left).setRightExpression(right);
		case  DIV: return new Division().setLeftExpression(left).setRightExpression(right);
		case  MOD: return new Function().setName("MOD").setParameters(new ExpressionList(Arrays.asList(left,right)));
		case  ADD: return new Addition().setLeftExpression(left).setRightExpression(right);
		case  SUB: return new Subtraction().setLeftExpression(left).setRightExpression(right);
		case  GT:  return new GreaterThan().setLeftExpression(left).setRightExpression(right);
		case  GE:  return new GreaterThanEquals().setLeftExpression(left).setRightExpression(right);
		case  LT:  return new MinorThan().setLeftExpression(left).setRightExpression(right);
		case  LE:  return new MinorThanEquals().setLeftExpression(left).setRightExpression(right);
		case  EQ:  return new EqualsTo().setLeftExpression(left).setRightExpression(right);
		case  NE:  return new NotEqualsTo().setLeftExpression(left).setRightExpression(right);
		case  AND: return new AndExpression().setLeftExpression(left).setRightExpression(right);
		case  OR:  return new OrExpression().setLeftExpression(left).setRightExpression(right);
		default:
			break;

		}
		throw new ODataApplicationException(operator + " operator is not implemented", 
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);

	}

	@Override
	public SQLExpression visitUnaryOperator(UnaryOperatorKind operator, SQLExpression operand)
			throws ExpressionVisitException, ODataApplicationException {

		switch (operator) {
		case MINUS:	return new InverseExpression().setExpression(operand);
		case NOT: return new Parenthesis().setExpression(operand).setNot();

		default:
			break;
		}

		throw new ODataApplicationException(operator + " operator is not implemented", 
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
	}

	@Override
	public SQLExpression visitMethodCall(MethodKind methodCall, List<SQLExpression> parameters)
			throws ExpressionVisitException, ODataApplicationException {

		Function function = new Function().setName(methodCall.name()).
				setParameters(new ExpressionList(parameters));

		switch (methodCall) {

		case CONTAINS: {
			BinaryExpression concat = new Concat().setLeftExpression(MATCH_ANY).setRightExpression(parameters.get(1));
			return new LikeExpression().
					setLeftExpression(parameters.get(0)).
					setRightExpression( new Concat().setLeftExpression(concat).setRightExpression(MATCH_ANY));
		}
		case STARTSWITH: {				
			return new LikeExpression().
					setLeftExpression(parameters.get(0)).
					setRightExpression( new Concat().setLeftExpression(parameters.get(1)).setRightExpression(MATCH_ANY));
		}
		case ENDSWITH:{
			return new LikeExpression().
					setLeftExpression(parameters.get(0)).
					setRightExpression( new Concat().setLeftExpression(MATCH_ANY).setRightExpression(parameters.get(1)));
		}
		case TOLOWER: return function.setName("LOWER");
		case TOUPPER: return function.setName("UPPER");
		case INDEXOF: return function.setName("INSTR");
		case TRIM: return function;
		case NOW: function.setName("CURRENT_TIMESTAMP");
		case LENGTH: return function;
		case SUBSTRING: return function;
		case CONCAT: return function;
		case YEAR: return function;
		case MONTH: return function;
		case DAY: return function;
		case HOUR: return function;
		case MINUTE: return function;
		case SECOND: return function;
		case ROUND: return function;
		case FLOOR: return function;
		case CEILING: return function;

		default:
			break;
		}
		throw new ODataApplicationException(methodCall + " function is not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);		
	}

	@Override
	public SQLExpression visitLambdaExpression(String lambdaFunction, String lambdaVariable, Expression expression)
			throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Lambda functions are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);		
	}

	@Override
	public SQLExpression visitLiteral(Literal literal) throws ExpressionVisitException, ODataApplicationException {
		String literalAsString = literal.getText();
		if(literal.getType() instanceof EdmString) {			
			return new StringValue(literal.getText());
		} else {	        
			try {
				return new LongValue(literalAsString);
			} catch(NumberFormatException e) {
				throw new ODataApplicationException("Only Edm.Int32 and Edm.String literals are implemented",
						HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
			}
		}
	}


	@Override
	public SQLExpression visitAlias(String aliasName) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Alias is not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);	
	}

	@Override
	public SQLExpression visitTypeLiteral(EdmType type) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Type literals are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
	}

	@Override
	public SQLExpression visitLambdaReference(String variableName)
			throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Lambda functions are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);	

	}

	@Override
	public SQLExpression visitEnum(EdmEnumType type, List<String> enumValues) throws ODataApplicationException {
		throw new ODataApplicationException("Enums are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);	

	}

	@Override
	public SQLExpression visitBinaryOperator(BinaryOperatorKind binaryOperatorKind, SQLExpression sqlExpression, List<SQLExpression> list) throws ODataApplicationException {
		throw new ODataApplicationException("Binary operation are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
	}

	@Override
	public SQLExpression visitMember(Member member) throws ExpressionVisitException, ODataApplicationException {

		final List<UriResource> uriResourceParts = member.getResourcePath().getUriResourceParts();

		if(uriResourceParts.size() == 1 && uriResourceParts.get(0) instanceof UriResourcePrimitiveProperty) {

			UriResourcePrimitiveProperty uriResourceProperty = (UriResourcePrimitiveProperty) uriResourceParts.get(0);
			String name = uriResourceProperty.getProperty().getName();	       
			Table table = new Table().setName(alias);

			return new Column(alias != null ? table : null , name);
		} else {

			throw new ODataApplicationException("Only primitive properties are implemented in filter  expressions", 
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
		}
	}



}
