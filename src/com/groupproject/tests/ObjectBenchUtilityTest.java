package com.groupproject.tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import com.groupproject.workbench.utility.ObjectBenchUtility;

public class ObjectBenchUtilityTest {

	@Test
	public void testSetActivePackage() {
		String activePackage = "com.test.one"; 
		ObjectBenchUtility.setActivePackage(activePackage);
		assertTrue(ObjectBenchUtility.getActivePackage().equals(activePackage)); 
	}

	@Test
	public void testGetClassFromType() {
		String testOne = "I";
		Class<?> resultOne = int.class;
		String testTwo = "String"; 
		Class<?> resultTwo = String.class;
		
		try {
			assertTrue(ObjectBenchUtility.getClassFromType(testOne).equals(resultOne));
			assertTrue(ObjectBenchUtility.getClassFromType(testTwo).equals(resultTwo));
		} catch (MalformedURLException e) {
			fail("Exception thrown, test failed.");
			e.printStackTrace();
		}
		
	}

	@Test
	public void testIsKnown() {
		String testOne = "I";
		String testTwo = "D"; 
		String testThree = "F";
		String testFour = "Random12Q+";
		
		assertTrue(ObjectBenchUtility.isKnown(testOne));
		assertTrue(ObjectBenchUtility.isKnown(testTwo));
		assertTrue(ObjectBenchUtility.isKnown(testThree));
		assertFalse(ObjectBenchUtility.isKnown(testFour));
		
	}

	@Test
	public void testGetControl() {
		
		String testOne = "I";
		String testTwo = "String";
		
		Spinner result = new Spinner(null,SWT.BORDER);
		result.setMinimum(Integer.MIN_VALUE);
		result.setMaximum(Integer.MAX_VALUE);
		result.setSelection(0);
		result.setIncrement(1);
		result.setPageIncrement(100);
		result.setData("typeKey", "int");
		
		Text resultTwo = new Text(null, SWT.BORDER);
		resultTwo.setData("typeKey","string");
		
		try {
			assertTrue(ObjectBenchUtility.getControl(null, testOne).equals(result));
			assertTrue(ObjectBenchUtility.getControl(null, testTwo).equals(resultTwo));
		} catch (Exception e) {
			fail("Exception thrown, test failed");
			e.printStackTrace();
		}
		
	}

}
