package com.groupproject.workbench.dialogs;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.groupproject.workbench.utility.ObjectBenchUtility;

/*
 * Method Dialog - This class will provide a dialog to enter parameters when calling a method.
 */
public class MethodDialog extends Dialog {
	
	public Method myMethod; 				//A copy of the method used
	Composite container;					//The main view in this dialog
	List<Control> controls; 				//A list of controls
	Object[] objects;						//A list of objects
	
	/*
	 * Constructor
	 */
	public MethodDialog(Shell parentShell, Method method) {
		super(parentShell);
		myMethod = method; 
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent){
		Composite area = (Composite) super.createDialogArea(parent);
		container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2,false);
		container.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true, true));
		container.setLayout(layout);
		controls = new ArrayList<Control>();
		try {
			parseParameters();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return container;
	}
	
	/*
	 * This method parses the parameters in the constructor and creates a control relative to it.
	 */
	void parseParameters() throws ClassNotFoundException, MalformedURLException
	{
		for(Class<?> c:myMethod.getParameterTypes())
		{
			//System.out.println(c.getName());
			
			Label lbl = new Label(container,SWT.NONE);
			lbl.setText(c.getName());
			Control control = ObjectBenchUtility.getControl(container, c.getSimpleName()); //Gets a control from the utility. 
			controls.add(control);
			GridData gD = new GridData();
			gD.grabExcessHorizontalSpace = true; 
			gD.horizontalAlignment = GridData.FILL;
			
			if(control != null)
			{
				control.setLayoutData(gD);
			}

			
		}
		container.layout();
		
	}
	
	
	/*
	 * Get Parameter Values - This builds an Object collection for use in a constructor call through reflection. 
	 */
	 Object[] getParameterValues()
	{
		objects = new Object[controls.size()];
		for(int i = 0; i<controls.size();i++)
		{
			objects[i] = ObjectBenchUtility.getControlValue(controls.get(i));
		}
		return objects; 
	}
	 
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		objects = getParameterValues();
		super.okPressed();
	}
	
	/*
	 * Get Parameters - returns the parameters entered into the dialog. 
	 */
	public Object[] getParameters()
	{
		return objects; 
	}
	 
}
