package com.github.sql.analytic.odata.testdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.parser.CCJSqlParserManager;
import com.github.sql.analytic.statement.Cursor;
import com.github.sql.analytic.statement.SQLStatement;
import com.github.sql.analytic.statement.policy.CreatePolicy;

public class Loader {

	public static void execute(Connection connection) throws SQLException, IOException {

		loadFile(connection,"mywind/northwind.sql");
		loadFile(connection,"mywind/northwind-data.sql");

	}

	private static void loadFile(Connection connection, String url) throws IOException, SQLException {
		try(InputStream in = new Loader().getClass().getClassLoader().getResourceAsStream(url)){
			BufferedReader reader = new BufferedReader( new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				connection.createStatement().execute(line);
			}
		}
	}

	public static Map<String,Cursor> getCursors() throws IOException, JSQLParserException {

		Map<String,Cursor> cursors = new HashMap<>();
		try(InputStream in = new Loader().getClass().getClassLoader().getResourceAsStream("northwind.cursor")){
			BufferedReader reader = new BufferedReader( new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				CCJSqlParserManager parserManager = new CCJSqlParserManager();
				Cursor stmt =  (Cursor) parserManager.parse(new StringReader(line));
				cursors.put(stmt.getName(), stmt);				
			}
		}		
		return cursors;
	}

	


	public static List<CreatePolicy> getPolicyList() throws IOException, JSQLParserException {

		List<String> policyList = new ArrayList<>();
		try(InputStream in = new Loader().getClass().getClassLoader().getResourceAsStream("northwind.policy")){
			BufferedReader reader = new BufferedReader( new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				policyList.add(line);
			}
		}		

		return parsePolicyList(policyList);
	}

	public static List<CreatePolicy> parsePolicyList(List<String> policyList) throws JSQLParserException {
		List<CreatePolicy> list = new ArrayList<CreatePolicy>();	
		for(String policy : policyList){		
			CCJSqlParserManager parserManager = new CCJSqlParserManager();
			SQLStatement stmt =  parserManager.parse(new StringReader(policy));
			list.add((CreatePolicy) stmt);
		}
		return list;

	}
	
	public static void drop(Connection connection) throws IOException, SQLException {
		loadFile(connection,"mywind/northwind-drop.sql");		
	}


}
