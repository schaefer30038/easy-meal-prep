package com.example.easymealprep;

import static org.junit.Assert.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//import com.sun.source.tree.AssertTree;

class ToolTest {

	private SQLConnection connect;
	private Tool testTool;
	
	@BeforeEach
	void setUp() throws Exception {
		connect = new SQLConnection();
		testTool = new Tool(connect.getConnection());
	}

	@AfterEach
	void tearDown() throws Exception {
		if (connect == null) {
			System.out.println("Print");
		}
		connect.closeConnection();
	}

	@Test
	void testCreateTool() {
		assertTrue(testTool.createTool("Test1"));
		assertFalse(testTool.createTool("Test1"));
		assertTrue(testTool.deleteTool());
	}
	
	@Test
	void testDeleteTool() {
		assertFalse(testTool.deleteTool());
		
		assertTrue(testTool.createTool("Test1"));
		assertTrue(testTool.deleteTool());

		assertTrue(testTool.createTool("Test1"));
		assertTrue(testTool.createToolFood(1, "Test1"));
		assertFalse(testTool.deleteTool());
		
		assertTrue(testTool.deleteToolFood(1, "Test1"));
		assertFalse(testTool.deleteTool());
	}
	@Test
	void testCreateToolFood() {
		assertTrue(testTool.createTool("Test1"));
		assertTrue(testTool.createTool("Test2"));
		assertTrue(testTool.createTool("Test3"));
		
		assertTrue(testTool.createToolFood(1, "Test1"));
		assertTrue(testTool.createToolFood(1, "Test2"));
		assertTrue(testTool.createToolFood(1, "Test3"));
		
		assertFalse(testTool.createToolFood(1, "Test1"));
		assertFalse(testTool.createToolFood(1, "Test2"));
		assertFalse(testTool.createToolFood(1, "Test3"));
		
		assertTrue(testTool.deleteToolFood(1, "Test1"));
		assertTrue(testTool.deleteToolFood(1, "Test2"));
		assertTrue(testTool.deleteToolFood(1, "Test3"));
		
	}
	@Test
	void testDeleteToolFood() {
		assertFalse(testTool.deleteToolFood(1, "Test1"));
		assertFalse(testTool.deleteToolFood(1, "Test2"));
		assertFalse(testTool.deleteToolFood(1, "Test3"));
		
	}
	
	@Test
	void testListTool() {
		assertTrue(testTool.createTool("Test Tool1"));
		assertTrue(testTool.createTool("Test Tool2"));
		assertTrue(testTool.createTool("Test Tool3"));
		assertTrue(testTool.createTool("Test Tool4"));
		
		ArrayList<String> tools = testTool.listTool();
		if (tools.size() < 4) {
			fail("Num of tools expected more than 3 but got " + tools.size());
		}
		
		boolean tool1 = false;
		boolean tool2 = false;
		boolean tool3 = false;
		boolean tool4 = false;
		
		for (int i = 0; i < tools.size(); i++) {
			if (tools.get(i).equals("Test Tool1")) {
				tool1 = true;
			}
			if (tools.get(i).equals("Test Tool2")) {
				tool2 = true;
			}
			if (tools.get(i).equals("Test Tool3")) {
				tool3 = true;
			}
			if (tools.get(i).equals("Test Tool4")) {
				tool4 = true;
			}
		}
		if (tool1 == false || tool2 == false || tool3 == false || tool4 == false) {
			fail("Test Tools are not correctly stored");
		}
		
		testTool.deleteTool();
	}
	
	@Test
	void testListToolFood() {
		assertTrue(testTool.createTool("Test Tool1"));
		assertTrue(testTool.createTool("Test Tool2"));
		assertTrue(testTool.createTool("Test Tool3"));
		assertTrue(testTool.createTool("Test Tool4"));
		
		assertTrue(testTool.createToolFood(1, "Test Tool1"));
		assertTrue(testTool.createToolFood(1, "Test Tool2"));
		assertTrue(testTool.createToolFood(1, "Test Tool3"));
		assertTrue(testTool.createToolFood(1, "Test Tool4"));
		
		boolean tool1 = false;
		boolean tool2 = false;
		boolean tool3 = false;
		boolean tool4 = false;
		
		ArrayList<String> result = testTool.listToolFood(1);
		
		assertEquals("Num of Tool Food", 4, result.size());
		
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).equals("Test Tool1")) {
				System.out.print(result.get(1));
				tool1 = true;
			}
			if (result.get(i).equals("Test Tool2")) {
				tool2 = true;
			}
			if (result.get(i).equals("Test Tool3")) {
				tool3 = true;
			}
			if (result.get(i).equals("Test Tool4")) {
				tool4 = true;
			}
		}
		
		if (tool1 == false || tool2 == false || tool3 == false || tool4 == false) {
			fail("Test Tools are not correctly stored");
		}

		assertTrue(testTool.deleteToolFood(1, "Test Tool1"));
		assertTrue(testTool.deleteToolFood(1, "Test Tool2"));
		assertTrue(testTool.deleteToolFood(1, "Test Tool3"));
		assertTrue(testTool.deleteToolFood(1, "Test Tool4"));
		testTool.deleteTool();
	}
	
	@Test
	void testUpdateToolFood() {
		testTool.deleteToolFood(1, "Test Tool1");
		testTool.deleteToolFood(1, "Test Tool2");
		testTool.deleteToolFood(1, "Test Tool3");
		testTool.deleteToolFood(1, "Test Tool4");

		assertTrue(testTool.createTool("Test Tool1"));
		assertTrue(testTool.createTool("Test Tool2"));
		assertTrue(testTool.createTool("Test Tool3"));
		assertTrue(testTool.createTool("Test Tool4"));
		
		assertTrue(testTool.createToolFood(1, "Test Tool1"));
		assertTrue(testTool.createToolFood(1, "Test Tool2"));
		
		boolean tool1 = false;
		boolean tool2 = false;
		boolean tool3 = false;
		boolean tool4 = false;
		
		ArrayList<String> tools = new ArrayList<>();
		tools.add("Test Tool3");
		tools.add("Test Tool4");
		
		assertTrue(testTool.updateToolFood(1, tools));
		
		ArrayList<String> result = testTool.listToolFood(1);
		
		if (result.size() != 2) {
			fail("Num tools of the food is not 2");
		}
		
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).equals("Test Tool1")) {
				tool1 = true;
			}
			if (result.get(i).equals("Test Tool2")) {
				tool2 = true;
			}
			if (result.get(i).equals("Test Tool3")) {
				tool3 = true;
			}
			if (result.get(i).equals("Test Tool4")) {
				tool4 = true;
			}
		}
		
		if (tool1 == true || tool2 == true) {
			fail("Test Tool1 and Test Tool2 should not be in the food");
		}
		
		if (tool3 == false || tool4 == false) {
			fail("Test Tool3 and Test Tool4 should be in the food");
		}
		
		assertTrue(testTool.deleteToolFood(1, "Test Tool3"));
		assertTrue(testTool.deleteToolFood(1, "Test Tool4"));
		testTool.deleteTool();
	}

}