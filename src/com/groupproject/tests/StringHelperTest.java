package com.groupproject.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.groupproject.workbench.utility.StringHelper;

public class StringHelperTest {

	
	@Test
	public void testFixType() {
		
		String input = "I";
		String result = "Integer";
		String inputTwo = "D";
		String resultTwo = "Double";
		String inputThree = "[I";
		String resultThree = "I[]";
		
		assertTrue(StringHelper.fixType(input).equals(result));
		assertTrue(StringHelper.fixType(inputTwo).equals(resultTwo));
		assertTrue(StringHelper.fixType(inputThree).equals(resultThree));
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
