package com.example.easymealprep;

import static org.junit.Assert.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mysql.jdbc.Connection;

class TestAccount {

	private SQLConnection testConnection;
	private Account testAccount;

	@BeforeEach
	void setUp() throws Exception {
		testConnection = new SQLConnection();
		testAccount = new Account(testConnection.getConnection());
	}

	@AfterEach
	void tearDown() throws Exception {
		testConnection.closeConnection();
	}

	@Test
	void testGetConnection() {
		Connection conn = (Connection) testConnection.getConnection();
		if (conn == null) {
			fail("Connection is null");
		}
	}

	@Test
	void testLoginAccount() {
		assertFalse(testAccount.loginAccount("Test", "Test"));
		assertFalse(testAccount.loginAccount("Admin", "Test"));
		assertFalse(testAccount.loginAccount("Test", "Administrator"));
		assertTrue(testAccount.loginAccount("Admin", "Administrator"));
	}

	@Test
	void testCreateAccount() {
		assertFalse(testAccount.createAccount("Admin", "FAIL", "FAIL", "FAIL"));
		assertTrue(testAccount.createAccount("TestAccount", "TestPW", "TestName", "TestEmail"));
		assertTrue(testAccount.loginAccount("TestAccount", "TestPW"));
		assertTrue(testAccount.deleteAccount("TestPW"));
	}

	@Test
	void testDeleteAccount() {
		assertFalse(testAccount.deleteAccount(null));
		assertTrue(testAccount.createAccount("TestAccount", "TestPW", "TestName", "TestEmail"));
		testAccount.loginAccount("TestAccount", "TestPW");
		assertFalse(testAccount.deleteAccount("FAIL"));
		assertTrue(testAccount.deleteAccount("TestPW"));
//		assertTrue(testAccount.createAccount("Admin", "Administrator", "HJ", "admin@gmail.com"));

	}

	@Test
	void testUpdateAccount() {
		assertFalse(testAccount.updateAccount("FAIL", "FAIL", "FAIL"));
		assertTrue(testAccount.loginAccount("Admin", "Administrator"));
		assertTrue(testAccount.updateAccount("Admin", "HJ Update", "Admin@gmail.com"));
		assertTrue(testAccount.updateAccount("Administrator", "HJ", "admin@gmail.com"));

	}

	void testGetFavorite() {
		ResultSet rs = null;
		assertTrue(testAccount.createAccount("TestAccount", "TestPW", "TestName", "TestEmail"));
		assertTrue(testAccount.loginAccount("TestAccount", "TestPW"));
		rs = testAccount.getFavorite();
		if (rs == null) {
			fail("No ResultSet returned");
		}
		int foodID = -1;
		try {
			while (rs.next()) {
				foodID = rs.getInt(1);
			}

			if (foodID != -1) {
				fail("Unexpected foodID is found from the favorite");
			}
		} catch (SQLException e) {
			fail(e.getMessage());
		}
		assertTrue(testAccount.deleteAccount("TestPW"));

		assertTrue(testAccount.loginAccount("Admin", "Administrator"));
		assertTrue(testAccount.setFavorite(1));
		rs = testAccount.getFavorite();
		if (rs == null) {
			fail("No ResultSet returned");
		}
		try {
			while (rs.next()) {
				foodID = rs.getInt(1);
			}

			if (foodID != 1) {
				fail("Expected foodID is not found from the Favorite");
			}
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testSetFavorite() {
		assertTrue(testAccount.loginAccount("Admin", "Administrator"));
		assertFalse(testAccount.setFavorite(0)); // Non existing food
		assertFalse(testAccount.setFavorite(1)); // Existing Favorite

		assertTrue(testAccount.deleteFavorite(1));
		assertTrue(testAccount.setFavorite(1));

		ResultSet rs = testAccount.getFavorite();
		if (rs == null) {
			fail("No ResultSet Returned");
		}

		int foodID = -1;
		try {
			while (rs.next()) {
				foodID = rs.getInt(1);
			}

			if (foodID != 1) {
				fail("Expected foodID is not returned from the favorite");
			}
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testDeleteFavorite() {
		assertTrue(testAccount.loginAccount("Admin", "Administrator"));
		
		assertTrue(testAccount.deleteFavorite(0)); // Delete test favorites
		assertTrue(testAccount.deleteFavorite(1));

		ResultSet rs = testAccount.getFavorite();
		if (rs == null) {
			fail("No ResultSet Returned");
		}

		int foodID = -1;
		try {
			while (rs.next()) {
				foodID = rs.getInt(1);

				if (foodID == 0 || foodID == 1) {
					fail("Uexpected foodID is returned from the favorite");
				}
			}
			
			assertTrue(testAccount.setFavorite(1));
		} catch (SQLException e) {
			fail(e.getMessage());
		}
		
	}
}
