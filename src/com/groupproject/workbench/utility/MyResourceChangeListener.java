package com.groupproject.workbench.utility;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

import com.groupproject.workbench.JavaModelHelper;

public class MyResourceChangeListener implements IResourceChangeListener{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
        IResource res = event.getResource();
        switch (event.getType()) {
           case IResourceChangeEvent.PRE_CLOSE:
            //  System.out.print("Project ");
             // System.out.print(res.getFullPath());
         //    System.out.println(" is about to close.");
              break;
           case IResourceChangeEvent.PRE_DELETE:
           //   System.out.print("Project ");
           //   System.out.print(res.getFullPath());
          //    System.out.println(" is about to be deleted.");
              break;
           case IResourceChangeEvent.POST_CHANGE:
          //    System.out.println("Resources have changed.");
              try {
				JavaModelHelper.rebuild();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
            //  event.getDelta().accept(new DeltaPrinter());
              break;
           case IResourceChangeEvent.PRE_BUILD:
          //    System.out.println("Build about to run.");
            //  event.getDelta().accept(new DeltaPrinter());
              break;
           case IResourceChangeEvent.POST_BUILD:
          //    System.out.println("Build complete.");

              //event.getDelta().accept(new DeltaPrinter());
              break;
        }
		
	}
	
	

}
