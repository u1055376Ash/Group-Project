package com.groupproject.workbench.buttons;

import java.net.MalformedURLException;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.groupproject.workbench.BenchInstance;
import com.groupproject.workbench.preferences.PreferenceConstants;
import com.groupproject.workbench.utility.ObjectBenchUtility;

/*
 * Object Bench Button - This class represents an instance of a class in the object bench view. 
 */
public class ObjectBenchButton extends ClassButton {
	BenchInstance myInstance; 			//The instance of an object associated with this button. 
	Color defaultColor; 
	
	/*
	 * Constructors
	 */
	public ObjectBenchButton(Composite parent, int style, String cn, int id,
			String pn) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException {
		super(parent, style, cn, id, pn);
		myInstance = new BenchInstance(cn,pn);
		defaultColor = this.backgroundColor;
	}
	
	public ObjectBenchButton(Composite parent, int style, String cn, int id,
			String pn, Object instance) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException {
		super(parent, style, cn, id, pn);
		myInstance = new BenchInstance(cn,pn,instance);
		defaultColor = this.backgroundColor;
	}
	
	/*
	 * Set Instance - Sets the instance this button is related to. 
	 */
	public void setInstance(BenchInstance o)
	{
		myInstance = o; 
		
	}
	
	/*
	 * Set Instance - Creates and sets a new instance based on class name and package. 
	 */
	public void setInstance(String cN, String pN) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		myInstance = new BenchInstance(cN,pN);
	}

	/*
	 * Get Instance - Returns the instance. 
	 */
	public BenchInstance getInstance()
	{
		return myInstance; 
	}
	
	/*
	 * Set Selected sets the selected stated of the button. 
	 */
	public void setSelected(final boolean selected)
	{
		Display display = this.getDisplay();
		if(display.isDisposed())return;
		final ObjectBenchButton bn = this;
	    display.asyncExec(new Runnable() {
	        @Override
	        public void run() {
	    		if(bn.isDisposed())
	    		{
	    			return;
	    		}
	    		if(selected == true)
	    		{
	    			bn.selectedColor = ObjectBenchUtility.getColorFromString(PreferenceConstants.P_COLOR_SELECTED);
	    			bn.setBackground(ObjectBenchUtility.getColorFromString(PreferenceConstants.P_COLOR_SELECTED));
	    			//this.backgroundColor = selected ? ObjectBenchUtility.getColorFromString(PreferenceConstants.P_COLOR_SELECTED):defaultColor;
	    		}
	    		else
	    		{
	    			bn.setBackground(defaultColor);
	    		}
	        }
	      });


	}

}
