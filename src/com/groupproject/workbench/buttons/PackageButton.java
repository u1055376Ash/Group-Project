package com.groupproject.workbench.buttons;



import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import com.groupproject.workbench.preferences.PreferenceConstants;
import com.groupproject.workbench.utility.ObjectBenchUtility;

/*
 * Package Button - A button that represents that packages in the package viewer. 
 */
public class PackageButton extends SquareButton{
	
	public int packageId;					//The ID of the package NOTE - DEPRECIATED, MAYBE REMOVE?
	public String packageValue; 			//The name of the package
	public Composite myComposite; 			//The value of the composite holding this button. 

	
	
	public PackageButton(Composite parent, int style, String packageName, int id) {
		super(parent, style);
		myComposite = parent; 
		getColor();
		packageId = id;
		packageValue = packageName;
		setRoundedCorners(true);
		hoverColor = new Color(parent.getDisplay(), 255,50,50);
		hoverColor2 = new Color(parent.getDisplay(), 255,205,205);
		this.setTextDirection(SWT.LEFT_TO_RIGHT);
		addSelectionListener(new SelectionListener() {

			//If we want to add a behaviour when a package is selected. 
			@Override
			public void widgetSelected(SelectionEvent e) {}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
//Will re-factor this to be used throughout the initialisation. 
	}
	
	public PackageButton(Composite parent, int style, String packageName, int id, Boolean empty) {
		this(parent,style,packageName,id);
		setEmpty(empty);
	}
	
	public void setEmpty(Boolean b)
	{
		if(b)
		{
			backgroundColor = new Color(myComposite.getDisplay(), 255,255,255);
			backgroundColor2 = new Color(myComposite.getDisplay(),240,240,240);
		}
		else
		{
			getColor();
		}
		
	}
	@Override
	public void checkSubclass()
	{
		//DO NOT REMOVE
	}
	
	void getColor()
	{
		backgroundColor = ObjectBenchUtility.getColorFromString(PreferenceConstants.P_COLOR_ONE);
		backgroundColor2 = ObjectBenchUtility.getColorFromString(PreferenceConstants.P_COLOR_TWO);
	}
	

}
