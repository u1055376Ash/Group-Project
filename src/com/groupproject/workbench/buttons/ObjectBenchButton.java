package com.groupproject.workbench.buttons;

import org.eclipse.swt.widgets.Composite;

import com.groupproject.workbench.BenchInstance;

public class ObjectBenchButton extends ClassButton {
	BenchInstance myInstance; 
	
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
	
	public void setInstance(BenchInstance o)
	{
		myInstance = o; 
		
	}
	
	public void setInstance(String cN, String pN) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		myInstance = new BenchInstance(cN,pN);
	}
	
	public BenchInstance getInstance()
	{
		return myInstance; 
	}

}
