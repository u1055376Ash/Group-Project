package com.groupproject.workbench.buttons;

import org.eclipse.swt.widgets.Composite;
import com.groupproject.workbench.BenchInstance;

public class ObjectBenchButton extends ClassButton {
	BenchInstance myInstance; 			//The instance of an object associated with this button. 
	
	
	/*
	 * Constructors
	 */
	public ObjectBenchButton(Composite parent, int style, String cn, int id,
			String pn) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		super(parent, style, cn, id, pn);
		myInstance = new BenchInstance(cn,pn);
	}
	
	public ObjectBenchButton(Composite parent, int style, String cn, int id,
			String pn, Object instance) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		super(parent, style, cn, id, pn);
		myInstance = new BenchInstance(cn,pn,instance);
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
	public void setInstance(String cN, String pN) throws ClassNotFoundException, InstantiationException, IllegalAccessException
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

}
