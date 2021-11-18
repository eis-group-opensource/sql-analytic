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

import java.util.Iterator;
import java.util.List;

import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.StatementVisitor;



public class Select implements SQLStatement {
	private SelectBody selectBody;
	
	private List<WithItem> withItemsList;
	
	public void accept(StatementVisitor statementVisitor) {
		statementVisitor.visit(this);
	}

	public SelectBody getSelectBody() {
		return selectBody;
	}

	public Select setSelectBody(SelectBody body) {
		selectBody = body;
		return this;
	}
	
	
	public String toString() {
		StringBuffer retval = new StringBuffer();
		if (withItemsList != null && !withItemsList.isEmpty()) {
			retval.append("WITH ");
			for (Iterator<WithItem> iter = withItemsList.iterator(); iter.hasNext();) {
				WithItem withItem = iter.next();
				retval.append(withItem);
				if (iter.hasNext()){
					retval.append(",");
				}
				retval.append(" ");
			}
		}
		retval.append(selectBody);
		return retval.toString();
	}

	
	public List<WithItem> getWithItemsList() {
		return withItemsList;
	}

	
	public Select setWithItemsList(List<WithItem> withItemsList) {
		this.withItemsList = withItemsList;
		return this;
	}
}
