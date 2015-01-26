package com.groupproject.workbench.utility;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import com.groupproject.workbench.Activator;
import com.groupproject.workbench.BenchInstance;
import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.preferences.PreferenceConstants;
import com.groupproject.workbench.views.InspectorView;
import com.groupproject.workbench.views.ObjectBenchView;
/**
 * Object Bench Utility - This static class contains methods to assist in common operations. 
 * @author Tsumiki
 *
 */
public final class ObjectBenchUtility 
{
	private static ObjectBenchView objectBench;					//A link to the object bench accessible by all classes. 	
	private static InspectorView inspectorView; 				//A link to the inspector view. 
	private static String activePackage; 						//The active package
	private static BenchInstance activeInstance; 				//The active instance accessible by all classes. 
	
	/*
	 * Register Object Bench - Registers the Object Bench with this utility. 
	 */
	public static void registerObjectBench(ObjectBenchView ob)
	{
		objectBench = ob;
	}

	/*
	 * Register Inspector View - Registers the inspector view with this utility. 
	 */
	public static void registerInspectorView(InspectorView i)
	{
		inspectorView = i; 
	}
	
	/*
	 * Get Inspector View - Returns a reference to the inspector view. 
	 */
	public static InspectorView getInspectorView()
	{
		return inspectorView; 
	}
	
	/*
	 * Get Object Bench - Returns a reference to the Object Bench View. 
	 */
	public static ObjectBenchView getObjectBench()
	{
		return objectBench;
	}
	
	/*
	 * Get Active Package - Returns the name of the active package. 
	 */
	public static String getActivePackage()
	{
		return activePackage; 
	}
	
	/*
	 * Set Active Pacakage - Sets the active package. 
	 */
	public static void setActivePackage(String s)
	{
		activePackage = s;
	}
	
	/*
	 * Open Class In Editor - This method enables a class to be opened in the default class editor. 
	 */
	public static void openClassInEditor(String myPackage, String myClass)
	{
		//TODO - Look into adding our own editor with scope highlighting here. 
		File file;
		try {
			file = JavaModelHelper.getClassFile(myPackage, myClass);
			if(file.exists() && file.isFile())
			{
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try{
					//page.toggleZoom(page.getActivePartReference());
					
					IDE.openEditorOnFileStore(page, fileStore);
					page.setPartState(page.getActivePartReference(), IWorkbenchPage.STATE_MAXIMIZED);
					//page.getActivePart().getSite().getShell().setMaximized(true);
				}catch(PartInitException e2){
					e2.printStackTrace();
				}
			}
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}

	}
	
	/*
	 * Get Color From String - This method gets a color from preferences. 
	 */
	public static Color getColorFromString(String s)
	{
		
		//TODO - Refactor this to getColorFromPreferences(String s)
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String color = store.getString(s);
		String[] values = color.split(",");
		Color myActiveColor = null; 
		if(values.length > 2)
		{
			myActiveColor = new Color(Display.getCurrent(), new RGB(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])));

		}
		else
		{
			myActiveColor = new Color(Display.getCurrent(), 255,200,200);
		}
		
		return myActiveColor; 

	}
	/*
	 * Show Empty Packages - Gets whether to show empty packages from preferences.
	 */
	public static Boolean showEmptyPackages()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String bool = store.getString(PreferenceConstants.P_BOOLEAN_SHOWEMPTY);
		if(Boolean.parseBoolean(bool) == true)
		{
			return true;
		}
		else
		{
			return false; 
		} 
	}
	
	/*
	 * Set Active Instance - Sets the active instance...
	 */
	public static void setActiveInstance(BenchInstance i) throws JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		activeInstance = i; 
		if(inspectorView != null)
		{
			inspectorView.update();
		}
	}
	
	/*
	 * Get Active Instance - Returns the active instance 
	 */
	public static BenchInstance getActiveInstance()
	{
		return activeInstance;
	}
	
	/*
	 * Get Class From Type - Returns a class type from a string.
	 */
	public static Class<?> getClassFromType(String s)
	{
		//TODO - Extend this so user classes can be found. 
		if(s.equals("I"))
		{
			return int.class;
		}
		if(s.equals("QString"))
		{
			return String.class;
		}
		if(s.equals("QString;"))
		{
			return String.class;
		}
		if(s.equals("String"))
		{
			return String.class;
		}
		if(s.equals("B"))
		{
			return byte.class;
		}
		if(s.equals("S"))
		{
			return short.class;
		}
		if(s.equals("J"))
		{
			return long.class;
		}
		if(s.equals("F"))
		{
			return float.class;
		}
		if(s.equals("D"))
		{
			return double.class;
		}
		if(s.equals("Z"))
		{
			return boolean.class;
		}
		if(s.equals("C"))
		{
			return char.class;
		}
		return null; 
		
	}

	/*
	 * Get Control - Gets a control based on a string type. 
	 */
	public static Control getControl(Composite control, String s)
	{
		if(s.equals("I") || s.equals("int"))
		{
			Spinner spinner = new Spinner(control,SWT.BORDER);
			spinner.setMinimum(Integer.MIN_VALUE);
			spinner.setMaximum(Integer.MAX_VALUE);
			spinner.setSelection(0);
			spinner.setIncrement(1);
			spinner.setPageIncrement(100);
			spinner.setData("typeKey", "int");
			return spinner; 
		}
		if(s.equals("QString;") || s.equals("java.lang.String"))
		{
			Text text = new Text(control, SWT.BORDER);
			text.setData("typeKey","string");
			return text;
		}
		if(s.equals("B") || s.equals("byte"))
		{
			Spinner spinner = new Spinner(control,SWT.BORDER);
			spinner.setMinimum(Byte.MIN_VALUE);
			spinner.setMaximum(Byte.MAX_VALUE);
			spinner.setSelection(0);
			spinner.setIncrement(1);
			spinner.setPageIncrement(100);
			spinner.setData("typeKey", "byte");
			return spinner; 
		}
		if(s.equals("S") || s.equals("short"))
		{
			Spinner spinner = new Spinner(control,SWT.BORDER);
			spinner.setMinimum(Short.MIN_VALUE);
			spinner.setMaximum(Short.MAX_VALUE);
			spinner.setSelection(0);
			spinner.setIncrement(1);
			spinner.setPageIncrement(100);
			spinner.setData("typeKey", "short");
			return spinner; 
		}
		if(s.equals("J") || s.equals("long"))
		{
			Spinner spinner = new Spinner(control,SWT.BORDER);
			spinner.setMinimum((int) Long.MIN_VALUE);
			spinner.setMaximum((int) Long.MAX_VALUE);
			spinner.setSelection(0);
			spinner.setIncrement(1);
			spinner.setPageIncrement(100);
			spinner.setData("typeKey", "long");
			return spinner; 
		}
		if(s.equals("F") || s.equals("float"))
		{
			Spinner spinner = new Spinner(control,SWT.BORDER);
			spinner.setDigits(3);
			spinner.setMinimum(0);
			spinner.setMaximum((int) Float.MAX_VALUE);
			spinner.setSelection(0);
			spinner.setIncrement(1);
			spinner.setPageIncrement(10);
			spinner.setData("typeKey", "float");
			return spinner; 
		}
		if(s.equals("D") || s.equals("double"))
		{
			Spinner spinner = new Spinner(control,SWT.BORDER);
			spinner.setDigits(3);
			spinner.setMinimum(0);
			spinner.setMaximum((int) Double.MAX_VALUE);
			spinner.setSelection(0);
			spinner.setIncrement(1);
			spinner.setPageIncrement(10);
			spinner.setData("typeKey", "double");
			return spinner; 
		}
		if(s.equals("Z") || s.equals("boolean"))
		{
			Button b = new Button(control, SWT.CHECK);
			b.setData("typeKey", "boolean");
			return b;
		}
		if(s.equals("C") || s.equals("char"))
		{
			Text text = new Text(control, SWT.BORDER);
			text.setTextLimit(1);
			text.setData("typeKey", "char");
			return text;
		}
		return null; 
	}
}
