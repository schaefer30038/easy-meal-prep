package com.example.easymealprep;

import static org.junit.Assert.*;
import java.sql.*;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IngredientTest {
	private SQLConnection conn;
	private Ingredient testIngredient;
	Statement stmt;

	@BeforeEach
	void setUp() throws Exception {
		conn = new SQLConnection();
		testIngredient = new Ingredient(conn.getConnection());
		stmt = conn.getConnection().createStatement();
	}

	@AfterEach
	void tearDown() throws Exception {
		conn.closeConnection();
	}

	@Test
	void testCreateIngredient() {
		// Test inserting new ingredient
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient2"));

		String query = "delete from Ingredient where ingredientName = \"Test Ingredient2\";";
		try {
			int result = stmt.executeUpdate(query);
			assertEquals("Delete new Ingredient: ", 1, result);
		} catch (SQLException e) {
			fail("testCreateIngredient: " + e.getMessage());
		}

		// Test inserting existing ingredient
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient2"));
		assertEquals("Insert new ingredient: ", false, testIngredient.createIngredient("Test Ingredient2"));
		query = "delete from Ingredient where ingredientName = \"Test Ingredient2\";";
		try {
			int result = stmt.executeUpdate(query);
			assertEquals("Delete new Ingredient: ", 1, result);
		} catch (SQLException e) {
			fail("testCreateIngredient: " + e.getMessage());
		}
	}

	@Test
	void testCreateIngredientFood() {
		// Valid
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient"));
		try {
			String query = "delete from Ingredient where ingredientName = \"Test Ingredient\";";
			int result = stmt.executeUpdate(query);
			assertEquals("Delete new Ingredient: ", 1, result);
		} catch (SQLException e) {
			fail("testCreateIngredient: " + e.getMessage());
		}

		// Inserting non-existing ingredient
		assertEquals("Insert non existing ingredient: ", false,
				testIngredient.createIngredientFood(1, "Test Ingredient"));

		// Inserting non-existing food
		assertEquals("Insert non existing ingredient: ", false,
				testIngredient.createIngredientFood(1, "Test Ingredient"));

		// Inserting existing foodIngredient
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient"));
		assertEquals("Insert IngredientFood: ", false, testIngredient.createIngredientFood(1, "Test Ingredient"));
		try {
			String query = "delete from Ingredient where ingredientName = \"Test Ingredient\";";
			int result = stmt.executeUpdate(query);
			assertEquals("Delete new Ingredient: ", 1, result);
		} catch (SQLException e) {
			fail("testCreateIngredient: " + e.getMessage());
		}
	}

	@Test
	void testDeleteIngredient() {
		// Delete unused ingredient
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient"));
		assertEquals("Delete ingredient: ", true, testIngredient.deleteIngredient());

		// No deletion when all ingredients are in use
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient"));
		assertEquals("Delete no ingredient: ", false, testIngredient.deleteIngredient());
		try {
			String query = "delete from Ingredient where ingredientName = \"Test Ingredient\";";
			int result = stmt.executeUpdate(query);
			assertEquals("Delete new Ingredient: ", 1, result);
		} catch (SQLException e) {
			fail("testCreateIngredient: " + e.getMessage());
		}
	}

	@Test
	void testDeleteIngredientFood() {
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient1"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient2"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient1"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient2"));

		// Invalid ingredients
		assertEquals("Delete IngredientFood: ", false, testIngredient.deleteIngredientFood(1, "Test Ingredient3"));

		// Invalid food
		assertEquals("Delete IngredientFood: ", false, testIngredient.deleteIngredientFood(0, "Test Ingredient1"));

		// Invalid for deleting twice
		assertEquals("Delete IngredientFood: ", true, testIngredient.deleteIngredientFood(1, "Test Ingredient1"));
		assertEquals("Delete IngredientFood: ", false, testIngredient.deleteIngredientFood(1, "Test Ingredient1"));

		// Valid
		assertEquals("Delete IngredientFood: ", true, testIngredient.deleteIngredientFood(1, "Test Ingredient2"));

		try {
			String query = "delete from Ingredient where ingredientName = \"Test Ingredient1\";";
			int result = stmt.executeUpdate(query);
			assertEquals("Delete new Ingredient: ", 1, result);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient2\";";
			result = stmt.executeUpdate(query);
			assertEquals("Delete new Ingredient: ", 1, result);
		} catch (SQLException e) {
			fail("testCreateIngredient: " + e.getMessage());
		}
	}

	@Test
	void testListIngredientFood() {
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient1"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient2"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient3"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient4"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient1"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient2"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient3"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient4"));

		ArrayList<String> result = testIngredient.listIngredientFood(1);
		assertEquals("Return Size1: ", 4, result.size());
		assertEquals("Return Size2: ", 4, result.size());

		if (!result.get(0).equals("Test Ingredient1")) {
			fail("Wrong ingredient returned");
		}
		if (!result.get(1).equals("Test Ingredient2")) {
			fail("Wrong ingredient returned");
		}
		if (!result.get(2).equals("Test Ingredient3")) {
			fail("Wrong ingredient returned");
		}
		if (!result.get(3).equals("Test Ingredient4")) {
			fail("Wrong ingredient returned");
		}

		try {
			String query = "delete from Ingredient where ingredientName = \"Test Ingredient1\";";
			int result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient1: ", 1, result1);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient2\";";
			result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient2: ", 1, result1);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient3\";";
			result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient3: ", 1, result1);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient4\";";
			result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient4: ", 1, result1);
		} catch (SQLException e) {
			fail("testCreateIngredient: " + e.getMessage());
		}
	}

	@Test
	void testListIngredient() {
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient1"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient2"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient3"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient4"));

		ArrayList<String> result = testIngredient.listIngredient();
		if (result.size() < 4) {
			fail("Num of ingredients is less than 4");
		}

		boolean ingredient1 = false;
		boolean ingredient2 = false;
		boolean ingredient3 = false;
		boolean ingredient4 = false;

		for (int i = 0; i < result.size(); i++) {

			if (result.get(i).equals("Test Ingredient1")) {
				if (ingredient1 == false) {
					ingredient1 = true;
				} else {
					fail("More than two Test ingredient1");
				}
			}
			if (result.get(i).equals("Test Ingredient2")) {
				if (ingredient2 == false) {
					ingredient2 = true;
				} else {
					fail("More than two Test ingredient2");
				}
			}
			if (result.get(i).equals("Test Ingredient3")) {
				if (ingredient3 == false) {
					ingredient3 = true;
				} else {
					fail("More than two Test ingredient3");
				}
			}
			if (result.get(i).equals("Test Ingredient4")) {
				if (ingredient4 == false) {
					ingredient4 = true;
				} else {
					fail("More than two Test ingredient4");
				}
			}
		}

		try {
			String query = "delete from Ingredient where ingredientName = \"Test Ingredient1\";";
			int result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient1: ", 1, result1);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient2\";";
			result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient2: ", 1, result1);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient3\";";
			result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient3: ", 1, result1);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient4\";";
			result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient4: ", 1, result1);
		} catch (SQLException e) {
			fail("testCreateIngredient: " + e.getMessage());
		}
	}

	@Test
	void testUpdateIgredient() {
		ArrayList<String> test = new ArrayList<>();
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient1"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient2"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient3"));
		assertEquals("Insert new ingredient: ", true, testIngredient.createIngredient("Test Ingredient4"));

		test.add("Test Ingredient3");
		test.add("Test Ingredient4");

		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient1"));
		assertEquals("Insert IngredientFood: ", true, testIngredient.createIngredientFood(1, "Test Ingredient2"));

		assertEquals("Update IngredientFood: ", true, testIngredient.updateIngredient(1, test));

		ArrayList<String> result = testIngredient.listIngredientFood(1);
		if (result.size() != 2) {
			fail("Expected 2 but got " + result.size());
		}

		for (int i = 0; i < result.size(); i++) {
			if (!result.get(i).equals("Test Ingredient3") && !result.get(i).equals("Test Ingredient4")) {
				fail("Wrong ingredient: " + result.get(i));
			}
		}

		try {
			String query = "delete from Ingredient where ingredientName = \"Test Ingredient1\";";
			int result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient1: ", 1, result1);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient2\";";
			result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient2: ", 1, result1);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient3\";";
			result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient3: ", 1, result1);

			query = "delete from Ingredient where ingredientName = \"Test Ingredient4\";";
			result1 = stmt.executeUpdate(query);
			assertEquals("Delete Ingredient4: ", 1, result1);
		} catch (SQLException e) {
			fail("testCreateIngredient: " + e.getMessage());
		}
	}
}
