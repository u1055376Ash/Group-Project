package com.groupproject.workbench.utility;

import java.io.File;
import java.net.MalformedURLException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
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
import com.groupproject.workbench.helpers.StringHelper;
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
	public static void setActiveInstance(BenchInstance i) throws JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, NoSuchMethodException, ClassNotFoundException, MalformedURLException
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
	public static Class<?> getClassFromType(String s) throws ClassNotFoundException, MalformedURLException
	{
		//TODO - Extend this so user classes can be found. 
		System.out.println(":"+s+":");
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
		if(s.equals("QColor;"))
		{
			return java.awt.Color.class;
		}
		
		
		Class<?> c = JavaModelHelper.getClassFromLoader(s);
		if(c != null)
		{
			return c; 
		}
		return null; 
		
	}
	
	/*
	 * Is Known - Returns whether the field is known to the plugin natively 
	 */
	public static boolean isKnown(String s)
	{
		if(s.equals("I"))
		{
			return true;
		}
		if(s.equals("QString"))
		{
			return true;
		}
		if(s.equals("QString;"))
		{
			return true;
		}
		if(s.equals("String"))
		{
			return true;
		}
		if(s.equals("B"))
		{
			return true;
		}
		if(s.equals("S"))
		{
			return true;
		}
		if(s.equals("J"))
		{
			return true;
		}
		if(s.equals("F"))
		{
			return true;
		}
		if(s.equals("D"))
		{
			return true;
		}
		if(s.equals("Z"))
		{
			return true;
		}
		if(s.equals("C"))
		{
			return true;
		}
		if(s.equals("QColor;"))
		{
			return true;
		}
		
		return false; 
	}
	
	public static Class<?>[] getParameterTypes(String[] types) throws ClassNotFoundException, MalformedURLException
	{
		Class<?>[] classes = new Class<?>[types.length];
		for(int i = 0; i<types.length;i++)
		{
			classes[i] = isKnown(types[i]) ? getClassFromType(types[i]): getClassFromType(StringHelper.getQualifiedName(StringHelper.fixType(types[i]), activePackage));
		}
		
		return classes; 
	}

	/*
	 * Get Control - Gets a control based on a string type. 
	 */
	public static Control getControl(final Composite control, final String s)
	{
		if(s.equals("I") || s.equals("int") || s.equals("java.lang.Integer"))
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
		if(s.equals("QString;") || s.equals("java.lang.String") || s.equals("String"))
		{
			Text text = new Text(control, SWT.BORDER);
			text.setData("typeKey","string");
			return text;
		}
		if(s.equals("B") || s.equals("byte") || s.equals("java.lang.Byte"))
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
		if(s.equals("S") || s.equals("short") || s.equals("java.lang.Short"))
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
		if(s.equals("J") || s.equals("long") || s.equals("java.lang.Long"))
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
		if(s.equals("F") || s.equals("float") || s.equals("java.lang.Float"))
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
		if(s.equals("D") || s.equals("double") || s.equals("java.lang.Double"))
		{
			Spinner spinner = new Spinner(control,SWT.BORDER);
			spinner.setDigits(3);
			spinner.setMinimum(0);
			spinner.setMaximum((int) Double.MAX_VALUE);
			spinner.setSelection(0);
			spinner.setIncrement(100);
			spinner.setPageIncrement(1000);
			spinner.setData("typeKey", "double");
			return spinner; 
		}
		if(s.equals("Z") || s.equals("boolean") || s.equals("java.lang.Boolean"))
		{
			Button b = new Button(control, SWT.CHECK);
			b.setData("typeKey", "boolean");
			return b;
		}
		if(s.equals("C") || s.equals("char") || s.equals("java.lang.Char"))
		{
			Text text = new Text(control, SWT.BORDER);
			text.setTextLimit(1);
			text.setData("typeKey", "char");
			return text;
		}
		if(s.equals("QColor;") || s.equals("Color")|| s.equals("java.awt.Color"))
		{
			final Button colorButton = new Button(control, SWT.NONE);
			colorButton.setText("Color");
			colorButton.setData("typeKey", "color");
			colorButton.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetSelected(SelectionEvent e) {
			        ColorDialog dlg = new ColorDialog(control.getShell());
			        java.awt.Color color = null;
			        // Set the selected color in the dialog from
			        // user's selected color
			        //dlg.setRGB(colorLabel.getBackground().getRGB());

			        // Change the title bar text
			        dlg.setText("Choose a Color");

			        // Open the dialog and retrieve the selected color
			        RGB rgb = dlg.open();
			        if (rgb != null) {
			          // Dispose the old color, create the
			          // new one, and set into the label
			          color = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
			         // colorLabel.setBackground(color);
			        }
			       // colorButton.setData("typeKey", "color");
			        colorButton.setData("typeData", color);
					
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			return colorButton;
		}
		
		/*
		 * Failing this object isn't a primitive type (or known) we need to try and see if there are any instances on the object bench. 
		 */
		
		final BenchInstance[] instances = ObjectBenchUtility.getObjectBench().getInstancesOfType(s);
		if(instances != null && instances.length > 0)
		{
			final Combo comboBox = new Combo(control, SWT.READ_ONLY);
			comboBox.setEnabled(true);
			comboBox.setData("typeKey", "combo");
			comboBox.setData("classData", s);
			for(int i = 0; i< instances.length; i++)
			{
				comboBox.add(instances[i].className + " - " + i, i);

			}
			comboBox.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					int current = comboBox.getSelectionIndex(); 
					for(int i = 0; i < instances.length; i++)
					{
						if(current == i)
						{
							comboBox.setData("typeData", instances[i].myInstance);

						}
					}

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
				
			});
			
			return comboBox;
		}
		return null; 
	}
	
	/*
	 * Get Control Value - Gets the value stored by a control. 
	 */
	public static Object getControlValue(Control c)
	{
		String s = (String)c.getData("typeKey");
		Object o = null;
		if(s.equals("int"))
		{
			Spinner spin = (Spinner)c;
			o = Integer.parseInt(spin.getText());
		}
		if(s.equals("double"))
		{
			Spinner spin = (Spinner)c;
			o = Double.parseDouble(spin.getText());
		}
		if(s.equals("float"))
		{
			Spinner spin = (Spinner)c;
			o = Float.parseFloat(spin.getText());
		}
		if(s.equals("byte"))
		{
			Spinner spin = (Spinner)c;
			o = Byte.parseByte(spin.getText());
		}
		if(s.equals("long"))
		{
			Spinner spin = (Spinner)c;
			o = Long.parseLong(spin.getText());
		}
		if(s.equals("char"))
		{
			Text t = (Text)c;
			o = t.getText();
		}
		if(s.equals("string"))
		{
			Text t = (Text)c;
			o = t.getText();
		}
		if(s.equals("boolean"))
		{
			Button b = (Button)c;
			o = b.getSelection();
		}
		if(s.equals("color"))
		{
			Button b = (Button)c;
			o = (java.awt.Color)b.getData("typeData");
			
		}
		
		if(c.getData("typeData") != null)
		{
			o = c.getData("typeData");
			//return o;
		}
		
		return o;
	}
	
	/*
	 * Set Control Value - Sets the value stored by a control. 
	 */
	public static void setControlValue(Control c, Object o)
	{
		String s = (String)c.getData("typeKey");
		if(s == null)
		{
			return;
		}
		if(s.equals("int"))
		{
			Spinner spin = (Spinner)c;
			spin.setValues((int)o, 0, Integer.MAX_VALUE, 0, 1, 3);
		}
		if(s.equals("double"))
		{
			Spinner spin = (Spinner)c;
			spin.setValues((int)o, 0, Integer.MAX_VALUE, 4, 1, 3);
		}
		if(s.equals("float"))
		{
			Spinner spin = (Spinner)c;
			spin.setValues((int) Float.parseFloat(o.toString()), 0, Integer.MAX_VALUE, 3, 1, 3);
		}
		if(s.equals("byte"))
		{
			Spinner spin = (Spinner)c;
			spin.setValues((int)o, 0, Integer.MAX_VALUE, 0, 1, 3);
		}
		if(s.equals("long"))
		{
			Spinner spin = (Spinner)c;
			spin.setValues((int)o, 0, Byte.MAX_VALUE, 4, 1, 3);
		}
		if(s.equals("char"))
		{
			Text t = (Text)c;
			t.setText((String)o);
		}
		if(s.equals("string"))
		{
			Text t = (Text)c;
			t.setText(o != null ? (String)o:"");
		}
		if(s.equals("boolean"))
		{
			Button b = (Button)c;
			b.setSelection((boolean)o);
		}
		if(s.equals("combo"))
		{
			Combo combo = (Combo)c;
			
			final BenchInstance[] instances = ObjectBenchUtility.getObjectBench().getInstancesOfType(combo.getData("classData").toString());
			//int current = combo.getSelectionIndex(); 
			for(int i = 0; i < instances.length; i++)
			{
				if(instances[i].myInstance.equals(o))
				{
					combo.select(i);
					//combo.setData("typeData", instances[i].myInstance);
				}
			}
			
		}
	}
	
	/*
	 * Delete File - Deletes a file, usually used to remove classes 
	 */
	public static void DeleteFile(File file)
	{
		if(file == null)
		{
			return;
		}
	    // A File object to represent the filename
	    File f = file;

	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists())
	      throw new IllegalArgumentException(
	          "Delete: no such file or directory");

	    if (!f.canWrite())
	      throw new IllegalArgumentException("Delete: write protected");

	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	      String[] files = f.list();
	      if (files.length > 0)
	        throw new IllegalArgumentException(
	            "Delete: directory not empty: ");
	    }

	    // Attempt to delete it
	    boolean success = f.delete();

	    if (!success)
	      throw new IllegalArgumentException("Delete: deletion failed");
	 }
	
	/*
	 * Delete Folder - Deletes a folder, usually used to delete packages. 
	 */
	public static boolean deleteFolder(File file)
	{
		if(file == null)
		{
			return false;
		}
	    if(file.exists()){
	        File[] files = file.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteFolder(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(file.delete());
	 }
	
}
