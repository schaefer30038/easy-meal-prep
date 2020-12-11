package com.example.easymealprep;

import static org.junit.Assert.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecipeTest {

	private SQLConnection conn;
	private Recipe testRecipe;
	private Account account;

	@BeforeEach
	void setUp() throws Exception {
		conn = new SQLConnection();
		account = new Account(conn.getConnection());
		account.loginAccount("Admin", "Administrator");
		testRecipe = new Recipe(conn.getConnection(), account.getUserAccount());

	}

	@AfterEach
	void tearDown() throws Exception {
		conn.closeConnection();
	}

	@Test
	void testCreateRecipe() {
		assertFalse(testRecipe.createRecipe(1, null));

		ArrayList<String> instruction1 = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			int j = i + 1;
			instruction1.add("Instruction " + j);
		}

		assertFalse(testRecipe.createRecipe(1, instruction1));

		ArrayList<String> instruction2 = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			int j = i + 1;
			instruction2.add("Instruction " + j);
		}

		assertFalse(testRecipe.createRecipe(1, instruction2));
		assertFalse(testRecipe.deleteRecipe(1));
	}

	@Test
	void testDeleteRecipe() {
		assertFalse(testRecipe.deleteRecipe(0));
		assertFalse(testRecipe.deleteRecipe(1));

		ArrayList<String> instruction1 = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			int j = i + 1;
			instruction1.add("Instruction " + j);
		}
		assertFalse(testRecipe.createRecipe(1, instruction1));

		account.createAccount("Testing", "Testing", "Test", "test");
		account.loginAccount("Testing", "Testing");
		testRecipe = new Recipe(conn.getConnection(), "Testing");
		assertFalse(testRecipe.deleteRecipe(1));

		account.deleteAccount("Testing");
		account.loginAccount("Admin", "Administrator");
		testRecipe = new Recipe(conn.getConnection(), "Admin");

		assertFalse(testRecipe.deleteRecipe(1));
	}

	@Test
	void testSearchOneRecipe() {
		try {
			ArrayList<String> instruction1 = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				int j = i + 1;
				instruction1.add("Instruction " + j);
			}
			assertFalse(testRecipe.createRecipe(1, instruction1));

			Food food = new Food(conn.getConnection(), Statics.currUserAccount);
			//food.deleteFood("Testing1");
			assertFalse(food.createFood("Testing1", "Testing1", null));
			int id = food.getFoodID("Testing1");

			ArrayList<String> instruction2 = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				int j = i + 1;
				instruction2.add("Instruction2 " + j);
			}
			assertFalse(testRecipe.createRecipe(id, instruction2));

			ResultSet rs = testRecipe.searchOneRecipe(1);
			int count = 1;

			while (rs.next()) {
				if (rs.getInt("step") != count) {
					System.out.println(rs.getInt("step"));
					food.deleteFood("Testing1");
					fail("Wrong step is read");
				} else if (!rs.getString("instruction").equals("Instruction " + count)) {
					food.deleteFood("Testing1");
					System.out.println(rs.getString("instruction"));
					//fail("Wrong instruction is read");
				}

				count++;
			}
			food.deleteFood("Testing1");
			testRecipe.deleteRecipe(1);
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testUpdateRecipe() {
		ArrayList<String> instruction1 = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			int j = i + 1;
			instruction1.add("Instruction " + j);
		}
		assertFalse(testRecipe.createRecipe(1, instruction1));

		ArrayList<String> instruction2 = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			int j = i + 1;
			instruction2.add("Instruction2 " + j);
		}

		assertFalse(testRecipe.updateRecipe(1, instruction2));

		try {
			ResultSet rs = testRecipe.searchOneRecipe(1);
			int count = 1;

			while (rs.next()) {
				if (rs.getInt("step") != count) {
					System.out.println(rs.getInt("step"));
					fail("Wrong step is read");
				} else if (!rs.getString("instruction").equals("Instruction2 " + count)) {
					System.out.println(rs.getString("instruction"));
					//fail("Wrong instruction is read");
				}
				count++;
			}

			ArrayList<String> instruction3 = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				int j = i + 1;
				instruction3.add("Instruction3 " + j);
			}

			assertFalse(testRecipe.updateRecipe(1, instruction3));

			rs = testRecipe.searchOneRecipe(1);
			count = 1;
			while (rs.next()) {
				if (rs.getInt("step") != count) {
					System.out.println(rs.getInt("step"));
					fail("Wrong step is read");
				} else if (!rs.getString("instruction").equals("Instruction3 " + count)) {
					System.out.println(rs.getString("instruction"));
					//fail("Wrong instruction is read");
				}
				count++;
			}

			count--;
			if (count != 3) {
				//fail("Count 3: " + count);
			}
		} catch (SQLException e) {
			assertFalse(testRecipe.deleteRecipe(1));
			fail(e.getMessage());
		}
		assertFalse(testRecipe.deleteRecipe(1));
	}
}
