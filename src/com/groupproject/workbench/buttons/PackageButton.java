package com.groupproject.workbench.buttons;



import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import com.groupproject.workbench.Activator;
import com.groupproject.workbench.preferences.PreferenceConstants;

public class PackageButton extends SquareButton{
	
	public int packageId;
	public String packageValue; 
	public Composite myComposite; 
	public Color myActiveColor; 
	
	
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

			@Override
			public void widgetSelected(SelectionEvent e) {
				//System.out.println("Selected: " + packageValue);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
			
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
			backgroundColor = myActiveColor;
			backgroundColor2 = new Color(myComposite.getDisplay(),200,200,200);
		}
		
	}
	@Override
	public void checkSubclass()
	{
		
	}
	
	private void getColor()
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String color = store.getString(PreferenceConstants.P_COLOR_ONE);
		String[] values = color.split(",");
		if(values.length > 2)
		{
			myActiveColor = new Color(myComposite.getDisplay(), new RGB(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])));

		}
		else
		{
			myActiveColor = new Color(myComposite.getDisplay(), 255,200,200);
		}

	}

}
