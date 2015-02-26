package com.groupproject.workbench.views;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.part.ViewPart;

import com.groupproject.workbench.BenchInstance;
import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.utility.ObjectBenchUtility;

/*
 * Inspector View - The class for the inspector view. The inspector is where an instantiated class can be inspected to determine 
 * field values. 
 * TODO - Maybe we can also make the fields editable through the inspector? 
 */
public class InspectorView extends ViewPart {

	Boolean enableEditing; 					//Will allow a user to edit values from within the inspector. 
	Composite myParent; 
	Composite mainViewArea;					//The main view area 
	Label header; 							//The header, usually the name of the instance
	List<Label> fieldNameLabels;			//A list of field names
	List<Label> fieldTypeLabels;			//A list of field types 
	List<Label> fieldValueLabels;			//A list of field values 
	ScrolledComposite sc;					//The scroll control component
	/*
	 * Default Constructor 
	 */
	public InspectorView() {}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		myParent = parent;
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		mainViewArea = new Composite(sc, SWT.NONE);
		mainViewArea.setLayout(new FormLayout());
		sc.setContent(mainViewArea);
		sc.setMinSize(mainViewArea.computeSize(500, 500));
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		ObjectBenchUtility.registerInspectorView(this);
		clear();
	}
	
	public void clear()
	{
	    for (Control control : mainViewArea.getChildren()) {
	        control.dispose();
	    }
		fieldNameLabels = new ArrayList<Label>();
		fieldTypeLabels = new ArrayList<Label>();
		fieldValueLabels = new ArrayList<Label>();
		header = new Label(mainViewArea, SWT.CENTER);
		header.setText("Instance Inspector");
		header.setBounds(mainViewArea.getClientArea());
		sc.layout();
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {}
	
	/*
	 * Update - This method updates the inspector, making it get the active instance. This method reads the active instance and gets all field values
	 */
	public void update() throws JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, NoSuchMethodException, ClassNotFoundException, MalformedURLException
	{
		int yValue = 30; 
		clear();
		//disposeLabels(); 
		final BenchInstance instance = ObjectBenchUtility.getActiveInstance(); //Get instance name 
		if(instance == null) //handles a potential error
		{
			disposeLabels();
			return; 
		}
		//System.out.println(instance.packageName + ":" + instance.className);
		final String[] fieldNames = JavaModelHelper.getFieldNames(instance.packageName, instance.className);
		header.setText(instance.className + "(Instance)");
		Label previousLabel = null;
		//Iterate over field names
		for(int i = 0; i< fieldNames.length;i++)
		{
			//Create Label
			fieldNameLabels.add(new Label(mainViewArea, SWT.NONE));
			fieldNameLabels.get(fieldNameLabels.size()-1).setText(fieldNames[i]);
			//FormData labelData = new FormData(100, SWT.DEFAULT);
			//Position Label
			FormData labelData = new FormData(70 + (fieldNames.length * 4), SWT.DEFAULT);
			if(i == 0)
			{
				labelData.top = new FormAttachment(header, 25);
				labelData.left = new FormAttachment(5);
				yValue += 25;
			}
			if(i-1 >= 0)
			{
				labelData.top = new FormAttachment(previousLabel, 25);
				labelData.left = new FormAttachment(5);
			}
			fieldNameLabels.get(fieldNameLabels.size()-1).setLayoutData(labelData);
			previousLabel = fieldNameLabels.get(fieldNameLabels.size()-1);
			if(instance.getField(fieldNames[i]) != null && instance.getField(fieldNames[i]).getClass().isArray())
			{
				final Object arrayField = instance.getField(fieldNames[i]);
				final int length = Array.getLength(arrayField);

				Label sizeLabel = new Label(mainViewArea, SWT.NONE);
				sizeLabel.setText("Size");
				
				FormData sizeData = new FormData(30, SWT.DEFAULT);
				
				sizeData.top = new FormAttachment(previousLabel, 15);
				sizeData.left = new FormAttachment(8);
				sizeLabel.setLayoutData(sizeData);
				
				final Spinner spinner = new Spinner(mainViewArea,SWT.BORDER);
				spinner.setMinimum(1);
				spinner.setMaximum(Integer.MAX_VALUE);
				//spinner.setSelection(0);
				spinner.setIncrement(1);
				spinner.setPageIncrement(100);
				spinner.setSelection(length);
				spinner.setData("typeKey", "int");
				spinner.addSelectionListener(new SelectionListener(){

					@Override
					public void widgetSelected(SelectionEvent e) {
						if(spinner.getSelection() != length && spinner.getSelection() > 0)
						{
							Object o = Array.newInstance(arrayField.getClass().getComponentType(), spinner.getSelection());
							try {
								
							} catch (Exception e2) {
								e2.printStackTrace();
							}
							try {
								int oldLength = Array.getLength(arrayField) > Array.getLength(o) ? Array.getLength(o):Array.getLength(arrayField);//Array.getLength(arrayField) > length ? Array.getLength(arrayField): Array.getLength(o);
								System.arraycopy(arrayField, 0, o, 0, oldLength);
								// o = Arrays.copyOf((Object[]) arrayField, Array.getLength(o));
								instance.setValue(arrayField, o);
								update();
							} catch (Exception e1) {
								e1.printStackTrace();
							} 
							}
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
					
				});
				FormData spinnerData = new FormData(85, SWT.DEFAULT);
				spinnerData.top  = new FormAttachment(sizeLabel, -15);
				spinnerData.left = new FormAttachment(sizeLabel,50,SWT.LEFT);
				spinner.setLayoutData(spinnerData);
				fieldNameLabels.add(sizeLabel);
				previousLabel = sizeLabel;
				Label number = null;
				for(int x = 0; x < length; x++)
				{
					final int index = x;
					number = new Label(mainViewArea,SWT.NONE);
					FormData lData = new FormData(20,SWT.DEFAULT);
					lData.top = new FormAttachment(previousLabel,25,SWT.TOP);
					lData.left = x == 0 ? new FormAttachment(previousLabel,10,SWT.LEFT):new FormAttachment(previousLabel,0,SWT.LEFT);
					number.setText("[" + x + "]");
					number.setLayoutData(lData);
					yValue += 25;
					//System.out.println(Array.get(arrayField, x).getClass().getName());
					final Control c = ObjectBenchUtility.getControl(mainViewArea, arrayField.getClass().getComponentType().getName());
					if(c == null)
					{
						//TryToGetObjectsFromBench
						
					}
					ObjectBenchUtility.setControlValue(c, Array.get(arrayField, x));
					FormData cData = new FormData(30, SWT.DEFAULT);
					cData.top = new FormAttachment(number, 0, SWT.TOP);
					cData.left = new FormAttachment(number, 40, SWT.LEFT);
					c.setLayoutData(cData);
					c.addMouseListener(new MouseListener(){

						@Override
						public void mouseDoubleClick(MouseEvent e) {}

						@Override
						public void mouseDown(MouseEvent e) {
							Array.set(arrayField, index, ObjectBenchUtility.getControlValue(c));
						}

						@Override
						public void mouseUp(MouseEvent e) {}
						
					});
					previousLabel = number;
				}
				continue;
			}
			//Create value label
			if(instance.getFieldClass(fieldNames[i]) != null)
			{
				final String currentName = fieldNames[i];
				//System.out.println(instance.getFieldClass(fieldNames[i]).getName());
				final Control c = ObjectBenchUtility.getControl(mainViewArea, instance.getFieldClass(fieldNames[i]).getSimpleName());
				//System.out.println("--" + instance.getFieldClass(fieldNames[i]).getName() + "--");
				if(c == null)
				{
					//Do something
				}
				else
				{
					ObjectBenchUtility.setControlValue(c, instance.getField(currentName));
				}

				if(c != null)
				{
					FormData controlData = new FormData(80, SWT.DEFAULT);
					for(Label l :fieldNameLabels)
					{
						if(l.getText().equals(fieldNames[i]))
						{
							controlData.top = new FormAttachment(l, 0, SWT.TOP);
							controlData.left = new FormAttachment(l, 100,SWT.LEFT);
						}
					}

					c.setLayoutData(controlData);
					try {
							c.addFocusListener(new FocusListener(){

								@Override
								public void focusGained(FocusEvent e) {
									try {
										instance.setValue(currentName,ObjectBenchUtility.getControlValue(c));
									} catch (Exception e1) {
										e1.printStackTrace();
									}
									
								}

								@Override
								public void focusLost(FocusEvent e) {
									try {
										instance.setValue(currentName,ObjectBenchUtility.getControlValue(c));
									} catch (Exception e1) {
										e1.printStackTrace();
									}
									
								}
								
							});
							c.addKeyListener(new KeyListener(){

								@Override
								public void keyPressed(KeyEvent e) {
									try {
										instance.setValue(currentName,ObjectBenchUtility.getControlValue(c));
									} catch (Exception e1) {
										e1.printStackTrace();
									}
									
								}

								@Override
								public void keyReleased(KeyEvent e) {
									try {
										instance.setValue(currentName,ObjectBenchUtility.getControlValue(c));
									} catch (Exception e1) {
										e1.printStackTrace();
									}
									
								}
								
							});
					} catch (Exception e1) {
						e1.printStackTrace();
					} 

				}
				else
				{
					fieldValueLabels.add(new Label(mainViewArea, SWT.BORDER));
					String s = instance.getValue(fieldNames[i]) != null ? instance.getValue(fieldNames[i]).toString():"null";
					fieldValueLabels.get(fieldValueLabels.size()-1).setText(s);
					FormData valueData = new FormData(85 + (fieldValueLabels.get(fieldValueLabels.size()-1).getText().length() * 4), SWT.DEFAULT);
					
					if(i == 0)
					{
						valueData.top = new FormAttachment(30); 
						valueData.left = new FormAttachment(fieldNameLabels.get(fieldValueLabels.size()-1),20);
					}
					if(i-1 >= 0)
					{
						if(previousLabel == null)
						{
							previousLabel = fieldNameLabels.get(fieldValueLabels.size()-2); 
						}
						valueData.top = new FormAttachment(previousLabel,-15);
						valueData.left = new FormAttachment(previousLabel,20);
					}
					fieldValueLabels.get(fieldValueLabels.size()-1).setLayoutData(valueData);
				}
			}


		}

		mainViewArea.layout();
		sc.layout();
		sc.setAlwaysShowScrollBars(true);
		sc.setMinSize(mainViewArea.computeSize(500, 500 + yValue));
	}
	
	
	/*
	 * Dispose Labels - Destroys all labels. 
	 *TODO - Refactor this to remove object fields when we implement editing. 
	 */
	public void disposeLabels()
	{
		for(Label l:fieldNameLabels)
		{
			if(l != null)
			{
				if(!l.isDisposed())
				{
					l.dispose();
				}
			}

		}
		for(Label l1:fieldValueLabels)
		{
			if(l1 != null)
			{
				if(!l1.isDisposed())
				{
					l1.dispose();
				}
			}
		}
		fieldNameLabels = new ArrayList<Label>();
		fieldValueLabels = new ArrayList<Label>();
	}
	

}
