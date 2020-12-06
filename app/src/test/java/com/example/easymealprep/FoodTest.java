package com.example.easymealprep;

import static org.junit.Assert.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mysql.jdbc.Connection;

class FoodTest {

	private Food testFood;
	private SQLConnection connect;
	private Account account;

	@BeforeEach
	void setUp() throws Exception {
		connect = new SQLConnection();
		account = new Account(connect.getConnection());
		account.loginAccount("Admin", "Administrator");
		testFood = new Food(connect.getConnection(), account.getUserAccount());
	}

	@AfterEach
	void tearDown() throws Exception {
		connect.closeConnection();
	}

	@Test
	void testCreateFood() {
		assertFalse(testFood.createFood("Test Food", "Test Description", null));

		assertTrue(testFood.createFood("TESTFood", "TESTDescription", null));
		assertFalse(testFood.createFood("TESTFood", "TESTDescription", null));

		assertTrue(testFood.deleteFood("TESTFood"));

	}

	@Test
	void testDeleteFood() {
		assertTrue(testFood.createFood("Testing1", "Testing", null));
		assertTrue(testFood.deleteFood("Testing1"));
		assertTrue(testFood.createFood("Testing1", "Testing", null));
		assertTrue(testFood.deleteFood("Testing1"));
	}

	@Test
	void testSearchFood() {
		int count = 0;
		try {
			assertTrue(account.createAccount("TestingID", "TestingPW", "Test", "Test"));

			ResultSet rs = testFood.searchFood("Test Food");

			assert (rs != null);

			while (rs.next()) {
				count++;
			}
			if (count != 1) {
				fail("Only one food should exist with the name Test Food");
			}
			assertTrue(testFood.createFood("Testing1", "Test Description", null));

			assertTrue(account.loginAccount("TestingID", "TestingPW"));
			testFood = new Food(connect.getConnection(), account.getUserAccount());

			assertTrue(testFood.createFood("Testing1", "Test Description", null));

			rs = testFood.searchFood("Testing1");

			assert (rs != null);

			count = 0;

			while (rs.next()) {
				count++;
			}
			if (count != 2) {
				System.out.println(count);
				fail("Only two food should exist with the name Test Food");
			}
			assertTrue(testFood.deleteFood("Testing1"));
			assertTrue(account.deleteAccount("TestingPW"));
			assertTrue(account.loginAccount("Admin", "Administrator"));
			testFood = new Food(connect.getConnection(), "Admin");
			assertTrue(testFood.deleteFood("Testing1"));

		} catch (SQLException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testListUserFood() {
		account.createAccount("Testing1","Testing1", "Test" , "Test");
		account.loginAccount("Testing1","Testing1");
		testFood = new Food(connect.getConnection(), "Testing1");
		
		testFood.createFood("Testing1", "Testing1", null);
		testFood.createFood("Testing2", "Testing2", null);
		testFood.createFood("Testing3", "Testing3", null);
		testFood.createFood("Testing4", "Testing4", null);
		
		ResultSet rs = testFood.listUserFood();
		assert (rs != null);
		
		try {
			int count = 0;
			while (rs.next()) {
				if (!rs.getString("foodName").equals("Testing1") && !rs.getString("foodName").equals("Testing2")
						&& !rs.getString("foodName").equals("Testing3") && !rs.getString("foodName").equals("Testing4")) {
					System.out.println(rs.getString("foodName"));
					fail("Wrong food is returned");
				}
				count++;
			}
			
			if (count != 4) {
				fail("Inaccurate number of foods is returned: " + count);
			}
			
			testFood.deleteFood("Testing1");
			testFood.deleteFood("Testing2");
			testFood.deleteFood("Testing3");
			testFood.deleteFood("Testing4");
			
			account.deleteAccount("Testing1");
			
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	void testListAllFood() {
		account.createAccount("Testing1","Testing1", "Test" , "Test");
		account.loginAccount("Testing1","Testing1");
		testFood = new Food(connect.getConnection(), "Testing1");
		testFood.createFood("Testing1", "Testing1", null);
		testFood.createFood("Testing2", "Testing2", null);
		testFood.createFood("Testing3", "Testing3", null);
		testFood.createFood("Testing4", "Testing4", null);
		

		ResultSet rs = testFood.listUserFood();
		assert (rs != null);
		int count = 0;
		int expect = 4;
		
		try {
			while (rs.next()) {
				if (!rs.getString("foodName").equals("Testing1") && !rs.getString("foodName").equals("Testing2")
						&& !rs.getString("foodName").equals("Testing3") && !rs.getString("foodName").equals("Testing4")) {
					System.out.println(rs.getString("foodName"));
					fail("Wrong food is returned");
				}
				count++;
			}
			
			if (count < expect) {
				fail("Inaccurate number of foods is returned: " + count + " < " + expect);
			}
			
			testFood.deleteFood("Testing1");
			testFood.deleteFood("Testing2");
			testFood.deleteFood("Testing3");
			testFood.deleteFood("Testing4");
			
			account.deleteAccount("Testing1");
			
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	void testUpdateFood() {
		assertTrue(account.createAccount("Testing", "Testing", "AAA", "AAA"));
		assertTrue(account.loginAccount("Testing", "Testing"));
		testFood = new Food(connect.getConnection(), account.getUserAccount());
		
		assertFalse(testFood.updateFood(1, "Test Food", "Test Description", null));
		
		assertTrue(account.deleteAccount("Testing"));
		assertTrue(account.loginAccount("Admin", "Administrator"));
		testFood = new Food(connect.getConnection(), account.getUserAccount());
		
		assertFalse(testFood.updateFood(0, "Testing1", "Testeing1", null));
		assertFalse(testFood.updateFood(0, "A", "A", null));
		assertTrue(testFood.updateFood(1, "Testing", "Testing", null));
		assertTrue(testFood.updateFood(1, "Test Food", "Test Description", null));
		
	}
	
	@Test
	void testSearchOneFood() {
		
	}
}
