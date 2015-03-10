package com.groupproject.workbench.dialogs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.groupproject.workbench.utility.ObjectBenchUtility;

/*
 * Constructor Dialog - This class controls the dialog when instantiating a class, this allows for the parameters of a constructor to be entered. 
 * 
 */
public class ConstructorDialog  extends Dialog{

	Constructor<?> constructor; 			//This is the constructor associated with this dialog. 
	Composite container;					//The main view in this dialog. 
	List<Control> controls; 				//A list of controls
	Object[] objects;						//A list of objects
	
	/*
	 * Constructor
	 */
	public ConstructorDialog(Shell parentShell, Constructor<?> con) {
		super(parentShell);
		constructor = con;
		//constructor.
		ObjectBenchUtility.setActivePackage(constructor.getDeclaringClass().getPackage().getName());
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent){
		Composite area = (Composite) super.createDialogArea(parent);
		ScrolledComposite sc = new ScrolledComposite(area, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		
		container = new Composite(sc, SWT.NONE);
		
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2,false);
	//	container.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true, true));
		container.setLayout(layout);
		container.setSize(400,300);
		sc.setContent(container);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(container.computeSize(300, 300));
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
		if(constructor.getParameterTypes().length < 1)
		{
			okPressed();
			return;
		}
		for(Class<?> c:constructor.getParameterTypes())
		{
			//System.out.println(c.getName());
			Label lbl = new Label(container,SWT.NONE);
			lbl.setText(c.getSimpleName());

			Control control = ObjectBenchUtility.getControl(container, c.getSimpleName()); //Gets a control from the utility. 
			controls.add(control);
			GridData gD = new GridData();
			gD.grabExcessHorizontalSpace = true; 
			gD.horizontalAlignment = GridData.FILL;
			
			if(control != null)
			{
				control.setLayoutData(gD);
//				if(c.isArray())
//				{
//					Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT,false);
//					control.setSize(size);
//				}
			}


		}
		container.layout();
		
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
	  * Get Instance - Creates an instance using the constructor and returns it. 
	  */
	public Object getInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
//		for(Object o:objects)
//		{
//			//System.out.println(o.toString());
//		}
//		for(int i = 0; i<objects.length;i++)
//		{
//			if(objects[i].getClass().equals(constructor.getParameterTypes()[i]))
//			{
//				System.out.println("MATCHED");
//			}
//			else
//			{
//				System.out.println("FUCKED");
//				Object o = constructor.getParameterTypes()[i].newInstance();
//				for(int x = 0; x < o.getClass().getFields().length; x++)
//				{
//					Field oldField = objects.getClass().getFields()[x];
//					Field newField = o.getClass().getFields()[x];
//					oldField.setAccessible(true);
//					newField.setAccessible(true);
//					newField.set(o, oldField.get(objects[i]));
//				}
//				if(o != null)
//				{
//					objects[i] = o;
//				}
//			}
//		}
		return constructor.newInstance(objects);
	}
	

}
