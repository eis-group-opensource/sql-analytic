/* ================================================================
 * JSQLParser : java based sql parser 
 * ================================================================
 *
 * Project Info:  http://jsqlparser.sourceforge.net
 * Project Lead:  Leonardo Francalanci (leoonardoo@yahoo.it);
 *
 * (C) Copyright 2004, by Leonardo Francalanci
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.github.sql.analytic.statement.select;

import com.github.sql.analytic.expression.ExpressionVisitor;
import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.expression.operators.relational.ItemsList;
import com.github.sql.analytic.expression.operators.relational.ItemsListVisitor;



/**
 * A subselect followed by an optional alias.
 */

public class SubSelect implements FromItem, SQLExpression, ItemsList {
	private SelectBody selectBody;
	private String alias;
	private boolean expression;

	public void accept(FromItemVisitor fromItemVisitor) {
		fromItemVisitor.visit(this);
	}

	public SelectBody getSelectBody() {
		return selectBody;
	}

	public SubSelect setSelectBody(SelectBody body) {
		selectBody = body;
		return this;
	}

	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String string) {
		alias = string;		
	}

	public void accept(ItemsListVisitor itemsListVisitor) {
		itemsListVisitor.visit(this);
	}

	public String toString () {
		return "("+selectBody+")"+((alias!=null)?" AS "+alias:"");
	}

	public SubSelect setExpression(boolean expression) {
		this.expression = expression;
		return this;
	}

	public boolean isExpression() {
		return expression;
	}

	
}
