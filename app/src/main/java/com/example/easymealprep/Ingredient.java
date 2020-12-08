package com.example.easymealprep;

import java.sql.*;
import java.util.ArrayList;

public class Ingredient {

	private Connection conn;
	private Statement stmt;
	private CallableStatement cstmt;

	public Ingredient(Connection conn) {
		this.conn = conn;
		try {
			stmt = this.conn.createStatement();
			cstmt = null;
		} catch (SQLException e) {
			System.out.println("Error: Ingredient " + e.getMessage());
		}
	}

	boolean createIngredient(String ingredient) {
		try {
			cstmt = conn.prepareCall("call createIngredient(?, ?);");
			cstmt.setString(1, ingredient);
			cstmt.registerOutParameter(2, Types.VARCHAR);

			cstmt.execute();

			if (cstmt.getString(2).equals("Success")) {
				return true;
			}
		} catch (SQLException e) {
//			System.out.println("Error: createIngredient " + e.getMessage());
		}

		return false;
	}

	boolean createIngredientFood(int foodID, String ingredient) {
		try {
			cstmt = conn.prepareCall("call createIngredientFood(?, ?);");
			cstmt.setInt(1, foodID);
			cstmt.setString(2, ingredient);

			int result = cstmt.executeUpdate();
			if (result == 1) {
				return true;
			}
		} catch (SQLException e) {
//			System.out.println("Error: createIngredientFood " + e.getMessage());
		}
		return false;

	}

	boolean deleteIngredient() {
		try {
			String sql = "delete from Ingredient i where i.ingredientName <> all(select fi.ingredientName from FoodIngredient fi group by fi.ingredientName);";
			int result = stmt.executeUpdate(sql);
			if (result != 0) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Error: deleteIngredient " + e.getMessage());
		}
		return false;
	}

	boolean deleteIngredientFood(int foodID, String ingredient) {
		String sql = "delete from FoodIngredient where foodID = " + foodID + " and ingredientName = \"" + ingredient
				+ "\";";
		try {
			int result = stmt.executeUpdate(sql);

			if (result == 1) {
				return true;
			}
		} catch (SQLException e) {
		}
		return false;
	}

	ArrayList<String> listIngredientFood(int foodID) {
		try {
			ArrayList<String> result = new ArrayList<String>();
//			ArrayList[] result = new ArrayList[2];
//			result[0] = new ArrayList<String>();
//			result[1] = new ArrayList<String>();

			String query = "select * from FoodIngredient where foodID = " + foodID + ";";
			ResultSet rs = stmt.executeQuery(query);

			if (rs != null) {
				while (rs.next()) {
//					result[0].add(rs.getInt("foodID") + "");
//					result[1].add(rs.getString("ingredientName"));
					result.add(rs.getString("ingredientName"));
				}
			}
			return result;
		} catch (SQLException e) {
			System.out.println("Error: listIngredientFood " + e.getMessage());
		}
		return null;
	}

	ArrayList<String> listIngredient() {
		try {
			ArrayList<String> result = new ArrayList<>();
			result.add("Select Ingredients");
			String query = "select * from Ingredient;";
			ResultSet rs = stmt.executeQuery(query);

			if (rs != null) {
				while (rs.next()) {
					result.add(rs.getString("ingredientName"));
				}
			}
			return result;

		} catch (SQLException e) {

		}
		return null;
	}

	boolean updateIngredient(int foodID, ArrayList<String> ingredients) {
		try {
			String sql = "delete from FoodIngredient where foodID = " + foodID + ";";
			stmt.executeUpdate(sql);

			for (int i = 0; i < ingredients.size(); i++) {
				sql = "insert into FoodIngredient values (" + foodID + ", \"" + ingredients.get(i) + "\");";
				int result = stmt.executeUpdate(sql);
				if (result != 1) {
					System.out.println("Fail: " + result);
					System.out.println(ingredients.get(i));
					return false;
				}
			}
			return true;
		} catch (SQLException e) {
			System.out.println("Error: updateIngredient " + e.getMessage());
		}
		return false;
	}
}
