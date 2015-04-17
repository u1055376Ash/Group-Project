package com.groupproject.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.groupproject.workbench.utility.StringHelper;

public class StringHelperTest {

	
	@Test
	public void testFixType() {
		
		//Arrange
		String input = "I";
		String inputTwo = "D";
		String inputThree = "[I";

		
		//Act
		String result = StringHelper.fixType(input);
		String resultTwo = StringHelper.fixType(inputTwo);
		String resultThree = StringHelper.fixType(inputThree);
		
		//Assert
		assertEquals(result, "Integer");
		assertEquals(resultTwo, "Double");
		assertEquals(resultThree, "I[]");
		
	}

	
	@Test
	public void testStripExtension() {
		String input = "Test.java";
		String result = "Test";
		String inputTwo = "TestTwo.cc";
		String resultTwo = "TestTwo";
		assertTrue(StringHelper.stripExtension(input).equals(result));
		assertTrue(StringHelper.stripExtension(inputTwo).equals(resultTwo));
	}

	@Test
	public void testGetQualifiedName() {
		
		String className = "TestClass";
		String packageName = "com.testPackage";
		String result = "com.testPackage.TestClass";
		
		String classNameTwo = "TestClassTwo";
		String packageNameTwo  = "com.testPackageTwo";
		String resultTwo = "com.testPackageTwo.TestClassTwo";
		
		String classNameThree = "TestClassThree";
		String packageNameThree = "";
		String resultThree = "TestClassThree";
		
		assertTrue(StringHelper.getQualifiedName(className, packageName).equals(result));
		assertTrue(StringHelper.getQualifiedName(classNameTwo, packageNameTwo).equals(resultTwo));
		assertTrue(StringHelper.getQualifiedName(classNameThree,packageNameThree).equals(resultThree)); 
		assertTrue(!StringHelper.getQualifiedName(classNameTwo, packageName).equals(result)); 
		//fail("Not yet implemented");
	}

}
