package com.groupproject.workbench.utility;

import java.lang.reflect.Array;
import java.net.MalformedURLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/*
 * Array Control - A new type of SWT control designed to dynamically create and update arrays. 
 */
public class ArrayControl extends Composite {

	int length;						//The length of the array.
	Object array; 					//The instance of the array.
	Spinner spinner; 				//A spinner to set length.
	Class<?> myClass; 				//A reference to the array class. 
	Label sizeLabel; 				//A label for size. 
	
	/*
	 * Constructors
	 */
	public ArrayControl(Composite parent, int style, Class<?> c, int initialSize) throws ClassNotFoundException, MalformedURLException {
		super(parent, style);
		
		if(c.isArray())
		{
			length = initialSize; 
			myClass = c;
			createInitialArrayInstance();
			createSizeLabel();
			createSizeSpinner();
			createFields();
			setLayout(new GridLayout());
			this.layout();
		}
	}
	

	public ArrayControl(Composite parent, int style, Class<?> c) throws ClassNotFoundException, MalformedURLException
	{
		super(parent,style);
		length = 1;
		System.out.println("My Intake: " + c.getName());
		myClass = Array.newInstance(c,1).getClass();
		createInitialArrayInstance(c);
		createSizeLabel();
		createSizeSpinner();
		createFields();
		setLayout(new GridLayout());
		this.layout();
	}
	
	/*
	 * Get Length - Gets the length of the array associated with this object. 
	 */
	public int getLength()
	{
		return length; 
	}
	
	/*
	 * Set Length - Sets the length of the array associated with this object. 
	 */
	public void setLength(int i)
	{
		length = i; 
	}
	
	/*
	 * Set Array Object - Sets the actual array object associated with this object. 
	 */
	public void setArrayObject(Object o)
	{
		if(o == null)return;
		if(o.getClass().isArray())
		{
			array = o;
			if(!isDisposed())
			{
				clear();
				try {
					refresh();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Get Array - Returns the array object. 
	 */
	public Object getArray()
	{
		return array; 
	}
	
	/*
	 * Set Class - Sets the class to be referenced.
	 */
	public void setClass(Class<?> c)
	{
		if(c == null)return;
		myClass = c;
	}
	
	/*
	 * Get my Array Class - Returns the array class.
	 */
	public Class<?> getMyArrayClass()
	{
		return myClass; 
	}
	
	/*
	 * Get Array Component Type - Gets the type of element stored in thre array. 
	 */
	public Class<?> getArrayComponentType()
	{
		return (myClass != null) ? myClass.getComponentType():null;
	}
	
	/*
	 * Clear - Clears the control. 
	 */
	void clear()
	{
		for(Control c:this.getChildren())
		{
//			if(c.equals(spinner) || c.equals(sizeLabel))
//			{
//				continue;
//			}
			if(!c.isDisposed())
			{
				c.dispose();
			}
		}
	}
	
	/*
	 * Refresh - Refreshes the control. 
	 */
	void refresh() throws ClassNotFoundException, MalformedURLException
	{
		createSizeLabel();
		createSizeSpinner();
		createFields();
		this.layout(true,true);
		this.getParent().layout(true,true);
		this.getParent().getShell().layout(true, true);
		Point newSize = getParent().getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		getParent().getShell().setSize(newSize);
		
	}
	
	/*
	 * Create Size Label - Creates the size label. 
	 */
	void createSizeLabel()
	{
		sizeLabel = new Label(this, SWT.NONE);
		sizeLabel.setText("Size");
	}
	
	/*
	 * Create Initial Array Instance - Creates initial array instance. 
	 */
	void createInitialArrayInstance(Class<?> arrayType)
	{
		array = Array.newInstance(arrayType,length);
	}
	
	void createInitialArrayInstance()
	{
		array = Array.newInstance(myClass.getComponentType(), length);
	}
	
	/*
	 * Create Size Spinner - Creates the size spinner used to define the size of an array. 
	 */
	void createSizeSpinner()
	{
		spinner = new Spinner(this,SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setMaximum(Integer.MAX_VALUE);
		spinner.setIncrement(1);
		spinner.setPageIncrement(100);
		spinner.setSelection(length);
		spinner.setData("typeKey", "int");
		spinner.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(spinner.getSelection() != length && spinner.getSelection() > 0)
				{
					length = spinner.getSelection();
					Object o = Array.newInstance(array.getClass().getComponentType(),length);
					try
					{
						int oldLength = Array.getLength(array) > Array.getLength(o) ? Array.getLength(o):Array.getLength(array);
						System.arraycopy(array, 0, o, 0, oldLength);
						array = o; 
						clear();
						refresh();
					}
					catch(Exception e1)
					{
						e1.printStackTrace();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
	}
	
	/*
	 * Create Fields - Creates the fields to handle elements in an array. 
	 */
	void createFields() throws ClassNotFoundException, MalformedURLException
	{
		Label previousLabel = sizeLabel;
		Label number = null; 
		System.out.println("Array Length: " + length);
		for(int x = 0; x < length; x++)
		{
			final int index = x; 
			number = new Label(this, SWT.NONE);
		//	FormData lData = new FormData(20, SWT.DEFAULT);
			//lData.top = new FormAttachment (previousLabel,25,SWT.TOP);
			//lData.left = x == 0 ? new FormAttachment(previousLabel, 10, SWT.LEFT):new FormAttachment(previousLabel,0,SWT.LEFT);
			GridData lData = new GridData();
			lData.grabExcessHorizontalSpace = true; 
			lData.horizontalAlignment = GridData.FILL;
			
			number.setText("[" + x + "]");
			number.setLayoutData(lData);
			//System.out.println("My Class: " + myClass.getName());
			final Control c = ObjectBenchUtility.getControl(this, myClass.getComponentType().getSimpleName());
//			if(c == null)
//			{
//				//Maybe destroy everything here to be safe? 
//				return;
//			}
			ObjectBenchUtility.setControlValue(c, Array.get(array, x));
		//	FormData cData = new FormData(30, SWT.DEFAULT); 
		//	cData.top = new FormAttachment(number, 0, SWT.TOP);
		//	cData.left = new FormAttachment(number, 40, SWT.LEFT); 
			GridData cData = new GridData();
			cData.grabExcessHorizontalSpace = true; 
			cData.horizontalAlignment = GridData.FILL;
			c.setLayoutData(cData);
			c.addMouseListener(new MouseListener(){

				@Override
				public void mouseDoubleClick(MouseEvent e) {}

				@Override
				public void mouseDown(MouseEvent e) {
					Array.set(array,index,ObjectBenchUtility.getControlValue(c));	
				}

				@Override
				public void mouseUp(MouseEvent e) {
					Array.set(array,index,ObjectBenchUtility.getControlValue(c));	
				}
				
			});
			c.addFocusListener(new FocusListener(){

				@Override
				public void focusGained(FocusEvent e) {
					Array.set(array,index,ObjectBenchUtility.getControlValue(c));	
					
				}

				@Override
				public void focusLost(FocusEvent e) {
					Array.set(array,index,ObjectBenchUtility.getControlValue(c));	
					
				}
				
			});
			previousLabel = number; 
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose()
	{
		super.dispose();
	}

}
