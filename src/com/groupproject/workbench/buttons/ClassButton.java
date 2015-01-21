package com.groupproject.workbench.buttons;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import com.groupproject.workbench.BenchInstance;
import com.groupproject.workbench.preferences.PreferenceConstants;
import com.groupproject.workbench.utility.ObjectBenchUtility;

public class ClassButton extends SquareButton{
	
	public int classId;
	public String className; 
	public String packageName; 
	
	BenchInstance myInstance; 
	ICompilationUnit myClass; 
	
	public ClassButton(Composite parent, int style, String cn, int id,String pn) {
		super(parent, style);
		classId = id;
		myInstance = new BenchInstance(cn,pn);
		className = cn; 
		packageName = pn;
		
		getColor();
		//setText(packageValue);
		//Image image = ImageDescriptor.createFromURL(getClass().getResource("/img/classButton.png")).createImage();
		setRoundedCorners(true);
		backgroundColor = new Color(parent.getDisplay(), 150,255,100);
		backgroundColor2 = new Color(parent.getDisplay(),255,255,255);
		hoverColor = new Color(parent.getDisplay(), 255,50,50);
		hoverColor2 = new Color(parent.getDisplay(), 255,205,205);
		this.setTextDirection(SWT.LEFT_TO_RIGHT);
		
		addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//System.out.println("Selected: " + packageValue);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
			
		});
		

	}
	
	@Override
	public void checkSubclass()
	{
		
	}
	
	public void getColor()
	{
		backgroundColor = ObjectBenchUtility.getColorFromString(PreferenceConstants.P_COLOR_THREE);
		backgroundColor2 = ObjectBenchUtility.getColorFromString(PreferenceConstants.P_COLOR_FOUR);
	}
	
	public void setClass(ICompilationUnit newClass)
	{
		myClass = newClass; 
		
	}
	
	public Class<?> getMyClass() throws ClassNotFoundException
	{


		String cleanName = myInstance.className.substring(0, myInstance.className.lastIndexOf('.'));
		Class<?> c  = Class.forName(myInstance.packageName + "." + cleanName);
		return c;
	}
	
	public void setInstance(BenchInstance o)
	{
		myInstance = o; 
		
	}
	
	public void setInstance(String cN, String pN)
	{
		myInstance = new BenchInstance(cN,pN);
	}
	
	public BenchInstance getInstance()
	{
		return myInstance; 
	}
}
