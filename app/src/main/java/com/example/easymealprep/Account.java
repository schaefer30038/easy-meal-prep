package com.example.easymealprep;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class Account {

	private Connection conn;
	private Statement stmt;
	private CallableStatement cstmt;

	private String accountName;
	private String userPassword;
	private String userName;
	private String userEmail;

	public Account(Connection connection) {
		conn = connection;
		try {
			stmt = conn.createStatement();
			cstmt = null;
			accountName = null;
			userPassword = null;
			userName = null;
			userEmail = null;
		} catch (SQLException e) {
			System.out.println("Error: Account " + e.getMessage());
		}
	}

	//  changed this.userPassword to Statics.currPassword
	// 	changed this.accountName to Statics.currUserAccount
	//  changed this.userEmail to Statics.currUserEmail
	//  changed this.userName to Statics.currName
	protected boolean loginAccount(String accountName, String password) {
		try {
			cstmt = conn.prepareCall("call loginAccount(?,?,?,?)");
			cstmt.setString(1, accountName);
			cstmt.setString(2, password);
			cstmt.registerOutParameter(3, Types.VARCHAR);
			cstmt.registerOutParameter(4, Types.VARCHAR);

			cstmt.executeUpdate();

			Statics.currName = cstmt.getString(3);
			Statics.currUserEmail = cstmt.getString(4);

			if (!Statics.currName.equals("NULL")) {
				Statics.currUserAccount = accountName;
				Statics.currPassword = password;
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Error: loginAccount " + e.getMessage());
		}
		return false;
	}

	protected boolean createAccount(String accountName, String password, String name, String email) {
		try {
			cstmt = conn.prepareCall("call createAccount(?,?,?,?,?)");
			cstmt.setString(1, accountName);
			cstmt.setString(2, password);
			cstmt.setString(3, name);
			cstmt.setString(4, email);
			cstmt.registerOutParameter(5, Types.VARCHAR);

			cstmt.executeUpdate();

			String status = cstmt.getString(5);
			if (status.equals("Success")) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println("Error: createAccount " + e.getMessage());
		}
		return false;
	}

	//  changed this.accountName to Statics.currUserAccount
	//	changed this.userPassword to Statics.currPassword
	protected boolean deleteAccount(String password) {
		if (Statics.currUserAccount == null || !Statics.currPassword.equals(password)) {
			return false;
		}

		try {
			stmt.execute("delete from Account where userAccount = \"" + Statics.currUserAccount + "\";");
			Statics.currUserAccount = null;
			Statics.currPassword = null;

			return true;
		} catch (SQLException e) {
			System.out.println("Error: deleteAccount + " + e.getMessage());
		}
		return false;
	}

	//	changed this.userPassword to Statics.currPassword
	// 	changed this.userName to Statics.currName
	// 	changed this.userEmail to Statics.currUserEmail
	// 	changed this.accountName to Statics.currUserAccount
	protected boolean updateAccount(String password, String username, String email) {

		try {
			cstmt = conn.prepareCall("call updateAccount(?,?,?,?,?)");
			cstmt.setString(1, Statics.currUserAccount);
			cstmt.setString(2, password);
			cstmt.setString(3, username);
			cstmt.setString(4, email);
			cstmt.registerOutParameter(5, Types.VARCHAR);
			cstmt.executeUpdate();

			String status = cstmt.getString(5);

			if (status.equals("Success")) {
				Statics.currPassword = password;
				Statics.currName = username;
				Statics.currUserEmail = email;
				return true;
			}

		} catch (SQLException e) {
			System.out.println("Error: updateAccount " + e.getMessage());
		}
		return false;
	}

	// 	changed this.accountName to Statics.currUserAccount
	protected ResultSet getFavorite() {
		String sql = "select f.foodID from Favorite f where f.userAccount = \"" + Statics.currUserAccount + "\";";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			System.out.println("Error: getFavorite " + e.getMessage());
		}
		return null;
	}

	// 	changed this.accountName to Statics.currUserAccount
	protected boolean setFavorite(int foodID) {
		try {
			cstmt = conn.prepareCall("call setFavorite(?,?,?);");
			cstmt.setString(1, Statics.currUserAccount);
			cstmt.setInt(2, foodID);
			cstmt.registerOutParameter(3, Types.VARCHAR);

			cstmt.executeUpdate();
			String status = cstmt.getString(3);

			if (status.equals("Success")) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Error: setFavorite " + e.getMessage());
		}
		return false;
	}

	protected boolean deleteFavorite(int foodID) {
		String sql = "delete from Favorite where foodID = \"" + foodID + "\";";
		try {
			stmt.execute(sql);
			return true;
		} catch (SQLException e) {
			System.out.println("Error: deleteFavorite " + e.getMessage());
		}
		return false;
	}

	protected boolean resetPassword(String userInput, String userPassword) {
		String sql = "";
		if (userInput.contains("@")){
			sql = sql + "update Account set userPassword = \"" + userPassword + "\" where userEmail = \"" + userInput + "\";";
		} else {
			sql = sql + "update Account set userPassword = \"" + userPassword + "\" where userAccount = \"" + userInput + "\";";
		}
		try {
			stmt.execute(sql);
			return true;
		} catch (SQLException e) {
			System.out.println("Error: resetPassword " + e.getMessage());
		}
		return false;
	}

	protected String getEmail(String userAccount) {
		String sql = "select a.userEmail from Account a where userAccount = \"" + userAccount + "\";";
		String result = null;
		try {
			ResultSet rs = stmt.executeQuery(sql);
			if (rs != null) {
				rs.next();
				result = rs.getString("userEmail");
			}
		} catch (SQLException e) {
			System.out.println("Error: getEmail " + e.getMessage());
		}
		return result;
	}

	protected String getUserAccount() {
		return Statics.currUserAccount;
	}

	protected String getUserEmail() {
		return Statics.currUserEmail;
	}

	protected String getUserPassword() {
		return Statics.currPassword;
	}

	protected String getUserName() {
		return Statics.currName;
	}
}
