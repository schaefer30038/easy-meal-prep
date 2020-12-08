package com.example.easymealprep;

import java.sql.*;
import java.util.ArrayList;

public class Tool {

	private Connection conn;
	private Statement stmt;
	private CallableStatement cstmt;

	public Tool(Connection conn) {
		this.conn = conn;
		try {
			stmt = this.conn.createStatement();
			cstmt = null;
		} catch (SQLException e) {
			System.out.println("Error: CookingTool " + e.getMessage());
		}
	}

	protected boolean createTool(String toolName) {
		try {
			cstmt = conn.prepareCall("call createTool(?);");
			cstmt.setString(1, toolName);

			int result = cstmt.executeUpdate();

			if (result == 1) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println("Error: createTool " + e.getMessage());
		}
		return false;
	}

	protected boolean createToolFood(int foodID, String toolName) {
		try {
			cstmt = conn.prepareCall("call createToolFood(?,?);");
			cstmt.setString(1, toolName);
			cstmt.setInt(2, foodID);
			int result = cstmt.executeUpdate();
			if (result == 1) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println("Error: createToolFood " + e.getMessage());
		}
		return false;
	}

	protected ArrayList<String> listTool() {
		try {
			ArrayList<String> tools = new ArrayList<String>();
			tools.add("Select Tools");
			String sql = "select * from Tool;";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				tools.add(rs.getString("toolName"));
			}
			return tools;
		} catch (SQLException e) {
			System.out.println("Error: listTool " + e.getMessage());
		}
		return null;
	}

	protected ArrayList<String> listToolFood(int foodID) {
		try {
//			ArrayList[] result = new ArrayList[2];
			ArrayList<String> result = new ArrayList<String>();
//			result[1] = new ArrayList<String>();
//			result[0] = new ArrayList<String>();

			String sql = "select * from FoodTool where foodID = " + foodID + ";";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
//				result[0].add(rs.getString("foodID"));
//				result[1].add(rs.getString("toolName"));
				result.add(rs.getString("toolName"));
			}
			return result;
		} catch (SQLException e) {
			System.out.println("Error: listTool " + e.getMessage());
		}
		return null;
	}

	protected boolean updateToolFood(int foodID, ArrayList<String> tools) {
		try {
			String sql = "delete from FoodTool where foodID = " + foodID + ";";
			stmt.executeUpdate(sql);
			
			for (int i = 0; i < tools.size(); i++) {
				cstmt = conn.prepareCall("insert into FoodTool values (?, ?);");
				cstmt.setString(1, tools.get(i));
				cstmt.setInt(2, foodID);
				
				if (cstmt.executeUpdate() != 1) {
					return false;
				}
				
			}
			return true;
		} catch (SQLException e) {
			System.out.println("Error: updateToolFood " + e.getMessage());
		}
		return false;
	}

	protected boolean deleteToolFood(int foodID, String toolName) {
		String sql = "delete from FoodTool where foodID = " + foodID + " and toolName = \"" + toolName + "\";";
		try {
			int result = stmt.executeUpdate(sql);
			if (result == 1) {
				this.deleteTool();
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Error: deleteToolFood " + e.getMessage());
		}
		return false;
	}

	protected boolean deleteTool() {
		try {
			String sql = "delete from Tool t where t.toolName <> all(select ft.toolName from FoodTool ft group by ft.toolName);";
			int result = stmt.executeUpdate(sql);
			if (result != 0) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Error: deleteTool " + e.getMessage());
		}
		return false;
	}
}
