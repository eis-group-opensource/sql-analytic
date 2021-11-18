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


package com.github.sql.analytic.schema;

import com.github.sql.analytic.expression.ExpressionVisitor;
import com.github.sql.analytic.expression.SQLExpression;
import com.github.sql.analytic.statement.select.ColumnReference;
import com.github.sql.analytic.statement.select.ColumnReferenceVisitor;
import com.github.sql.analytic.statement.select.SelectItemVisitor;
import com.github.sql.analytic.statement.select.SelectListItem;


/**
 * A column. It can have the table name it belongs to. 
 */

public class Column implements SQLExpression, ColumnReference,SelectListItem {
	private String columnName = "";
	private Table table;
	
	public Column() {
	}

	public Column(Table table, String columnName) {
		this.table = table;
		this.columnName = columnName;
	}
	
	public String getColumnName() {
		return columnName;
	}

	public Table getTable() {
		return table;
	}

	public Column setColumnName(String string) {
		columnName = string;
		return this;
	}

	public Column setTable(Table table) {
		this.table = table;
		return this;
	}
	
	/**
	 * @return the name of the column, prefixed with 'tableName' and '.' 
	 */
	public String getWholeColumnName() {
		
		String columnWholeName = null;
		String tableWholeName = table == null ? null : table.getWholeTableName();
		
		if (tableWholeName != null && tableWholeName.length() != 0) {
			columnWholeName = tableWholeName + "." + columnName;
		} else {
			columnWholeName = columnName;
		}
		
		return columnWholeName;

	}
	
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visit(this);
	}

	public void accept(ColumnReferenceVisitor columnReferenceVisitor) {
		columnReferenceVisitor.visit(this);
	}


	public String toString() {
		return getWholeColumnName();
	}

	public void accept(SelectItemVisitor selectItemVisitor) {
		selectItemVisitor.visit(this);
		
	}

	
}
