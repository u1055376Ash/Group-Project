package com.groupproject.workbench.buttons;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.helpers.StringHelper;
import com.groupproject.workbench.preferences.PreferenceConstants;
import com.groupproject.workbench.utility.ObjectBenchUtility;

/*
 * Class Button - A representation of a class that can be used as a button. Inherits from Square Button. 
 */
public class ClassButton extends SquareButton{
	
	public int classId;						//The ID of the class. 
	public String className; 				//The name of the Class to be associated with. 
	public String packageName; 				//The name of the Package that the Class is associated with. 
	

	/*
	 * Constructors 
	 */
	public ClassButton(Composite parent, int style, String cn, int id,String pn) throws ClassNotFoundException {
		super(parent, style);
		classId = id;
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
		// DO NOT REMOVE!
	}
	
	/*
	 * Get Colour - Sets the colours based on colours specified in the preferences.
	 */
	public void getColor()
	{
		backgroundColor = ObjectBenchUtility.getColorFromString(PreferenceConstants.P_COLOR_THREE);
		backgroundColor2 = ObjectBenchUtility.getColorFromString(PreferenceConstants.P_COLOR_FOUR);
	}
	
	/*
	 * Get My Class - Returns an object representing the class referenced in this button. 
	 */
	public Class<?> getMyClass() throws ClassNotFoundException
	{

		Class<?> c  = JavaModelHelper.getClassFromLoader(StringHelper.getQualifiedName(className, packageName));
		return c;
	}
	
}
