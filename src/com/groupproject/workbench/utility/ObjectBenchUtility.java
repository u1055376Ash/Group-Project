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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;

import com.groupproject.workbench.Activator;
import com.groupproject.workbench.BenchInstance;
import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.preferences.PreferenceConstants;
import com.groupproject.workbench.views.InspectorView;
import com.groupproject.workbench.views.ObjectBenchView;

public final class ObjectBenchUtility 
{
	private static ObjectBenchView objectBench;
	private static InspectorView inspectorView; 
	
	private static String activeProject; 
	private static String activePackage; 
	private static Object activeObject; 
	
	private static BenchInstance activeInstance; 
	
	public static void registerObjectBench(ObjectBenchView ob)
	{
		objectBench = ob;
	}
	
	public static void registerInspectorView(InspectorView i)
	{
		inspectorView = i; 
	}
	
	public static InspectorView getInspectorView()
	{
		return inspectorView; 
	}
	
	public static ObjectBenchView getObjectBench()
	{
		return objectBench;
	}
	
	public static String getActivePackage()
	{
		return activePackage; 
	}
	public static void setActivePackage(String s)
	{
		activePackage = s;
	}
	
	public static void openClassInEditor(String myPackage, String myClass)
	{
		File file;
		try {
			file = JavaModelHelper.getClassFile(myPackage, myClass);
			if(file.exists() && file.isFile())
			{
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try{
					//page.toggleZoom(page.getActivePartReference());
					
					IEditorPart part = IDE.openEditorOnFileStore(page, fileStore);
					page.setPartState(page.getActivePartReference(), IWorkbenchPage.STATE_MAXIMIZED);
					//page.getActivePart().getSite().getShell().setMaximized(true);
				}catch(PartInitException e2){
					e2.printStackTrace();
				}
			}
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public static Color getColorFromString(String s)
	{
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
	
	
	public static void setActiveInstance(BenchInstance i) throws JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		activeInstance = i; 
		if(inspectorView != null)
		{
			inspectorView.update();
		}
	}
	
	public static BenchInstance getActiveInstance()
	{
		return activeInstance;
	}
	
	public static Class<?> getClassFromType(String s)
	{
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
