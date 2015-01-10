package com.groupproject.workbench.utility;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;

import com.groupproject.workbench.Activator;
import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.preferences.PreferenceConstants;
import com.groupproject.workbench.views.ObjectBenchView;

public final class ObjectBenchUtility 
{
	private static ObjectBenchView objectBench;
	
	private static String activeProject; 
	private static String activePackage; 
	
	public static void registerObjectBench(ObjectBenchView ob)
	{
		objectBench = ob;
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
	
	public static Color getColorFromString()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String color = store.getString(PreferenceConstants.P_COLOR_ONE);
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

}
