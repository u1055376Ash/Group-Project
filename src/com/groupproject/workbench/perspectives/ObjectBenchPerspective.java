/**
 * 
 */
package com.groupproject.workbench.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Tsumiki
 * Object Bench Perspective - The class for the perspective. 
 */
public class ObjectBenchPerspective implements IPerspectiveFactory {

	
	private static final String CLASS_DIAGRAM_ID = "com.groupproject.workbench.views.classdiagram";
	
	private static final String OBJECT_BENCH_ID = "com.groupproject.workbench.views.objectbench";
	
	private static final String INSPECTOR_ID =  "com.groupproject.workbench.views.inspector";
	
	private static final String BOTTOM = "bottom";
	
	private static final String TOP = "top";
	
	private static final String LEFT = "left";
	
	private static final String RIGHT  = "right";
	
	private static IPageLayout theLayout;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout myLayout) {
		

		myLayout.setEditorAreaVisible(false);
		theLayout = myLayout;
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		myLayout.addView(OBJECT_BENCH_ID, IPageLayout.BOTTOM, 0.8f, myLayout.getEditorArea());
		myLayout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.2f, myLayout.getEditorArea());
		myLayout.addView(CLASS_DIAGRAM_ID, IPageLayout.LEFT,0.6f,myLayout.getEditorArea());
		myLayout.addView(INSPECTOR_ID, IPageLayout.LEFT, 0.3f, myLayout.getEditorArea());
	}
	
	/*
	 * Hide Editor - Hides the empty window. 
	 */
	public static void hideEditor()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setEditorAreaVisible(false);
		if(theLayout != null)
		{
			//System.out.println("Hiding");
			theLayout.setEditorAreaVisible(false);
		}
	}
	
	/*
	 * Show Window - Shows the empty window to be used for the code editor. 
	 */
	public static void showEditor()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setEditorAreaVisible(true);
		if(theLayout != null)
		{
			//System.out.println("Showing");
			theLayout.setEditorAreaVisible(true);
		}
	}

}
