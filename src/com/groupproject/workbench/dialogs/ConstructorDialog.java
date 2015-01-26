package com.groupproject.workbench.dialogs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.groupproject.workbench.utility.ObjectBenchUtility;

public class ConstructorDialog  extends Dialog{

	
	Constructor<?> constructor; 
	Composite container;
	List<Control> controls; 
	Object[] objects;
	public ConstructorDialog(Shell parentShell, Constructor<?> con) {
		super(parentShell);
		constructor = con; 
		// TODO Auto-generated constructor stub
	}
	

	
	protected Control createDialogArea(Composite parent){
		Composite area = (Composite) super.createDialogArea(parent);
		container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2,false);
		container.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true, true));
		container.setLayout(layout);
		controls = new ArrayList<Control>();
		parseParameters();
		
		return container;
	}
	
	void drawForm()
	{
		
	}
	
	void parseParameters()
	{
		for(Class<?> c:constructor.getParameterTypes())
		{
			//System.out.println(c.getName());
			
			Label lbl = new Label(container,SWT.NONE);
			lbl.setText(c.getName());
			Control control = ObjectBenchUtility.getControl(container, c.getName());
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
	
	@Override
	protected void okPressed()
	{
		objects = getParameterValues();
		super.okPressed();
	}
	
	 Object[] getParameterValues()
	{
		objects = new Object[controls.size()];
		for(int i = 0; i<controls.size();i++)
		{
			String s = (String)controls.get(i).getData("typeKey");
			if(s.equals("int"))
			{
				Spinner spin = (Spinner)controls.get(i);
				objects[i] = Integer.parseInt(spin.getText());
			}
			if(s.equals("double"))
			{
				Spinner spin = (Spinner)controls.get(i);
				objects[i] = Double.parseDouble(spin.getText());
			}
			if(s.equals("float"))
			{
				Spinner spin = (Spinner)controls.get(i);
				objects[i] = Float.parseFloat(spin.getText());
			}
			if(s.equals("byte"))
			{
				Spinner spin = (Spinner)controls.get(i);
				objects[i] = Byte.parseByte(spin.getText());
			}
			if(s.equals("long"))
			{
				Spinner spin = (Spinner)controls.get(i);
				objects[i] = Long.parseLong(spin.getText());
			}
			if(s.equals("char"))
			{
				Text t = (Text)controls.get(i);
				objects[i] = t.getText();
			}
			if(s.equals("string"))
			{
				Text t = (Text)controls.get(i);
				objects[i] = t.getText();
			}
			if(s.equals("boolean"))
			{
				Button b = (Button)controls.get(i);
				objects[i] = b.getSelection();
			}
		}
		
		return objects; 
	}
	
	public Object getInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		for(Object o:objects)
		{
			System.out.println(o.toString());
		}
		return constructor.newInstance(objects);
	}
	

}
