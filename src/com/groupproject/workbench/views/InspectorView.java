package com.groupproject.workbench.views;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
	
	Composite mainViewArea;					//The main view area 
	Label header; 							//The header, usually the name of the instance
	List<Label> fieldNameLabels;			//A list of field names
	List<Label> fieldTypeLabels;			//A list of field types 
	List<Label> fieldValueLabels;			//A list of field values 
	
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
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		mainViewArea = new Composite(sc, SWT.NONE);
		mainViewArea.setLayout(new FormLayout());
		mainViewArea.setSize(500,500);
		sc.setContent(mainViewArea);
		sc.setExpandHorizontal(true);
		sc.setMinSize(mainViewArea.computeSize(500, 500));
		sc.setMinHeight(110);
		ObjectBenchUtility.registerInspectorView(this);
		fieldNameLabels = new ArrayList<Label>();
		fieldTypeLabels = new ArrayList<Label>();
		fieldValueLabels = new ArrayList<Label>();
		header = new Label(mainViewArea, SWT.CENTER);
		header.setText("Instance Inspector");
		header.setBounds(mainViewArea.getClientArea());
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
	public void update() throws JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		disposeLabels(); 
		BenchInstance instance = ObjectBenchUtility.getActiveInstance(); //Get instance name 
		if(instance == null) //handles a potential error
		{
			disposeLabels();
			return; 
		}
		//System.out.println(instance.packageName + ":" + instance.className);
		String[] fieldNames = JavaModelHelper.getFieldNames(instance.packageName, instance.className);
		header.setText(instance.className + "(Instance)");
		//Iterate over field names
		for(int i = 0; i< fieldNames.length;i++)
		{
			//Create Label
			fieldNameLabels.add(new Label(mainViewArea, SWT.NONE));
			fieldNameLabels.get(i).setText(fieldNames[i]);
			//FormData labelData = new FormData(100, SWT.DEFAULT);
			//Position Label
			FormData labelData = new FormData(85 + (fieldNames.length * 4), SWT.DEFAULT);
			if( i == 0)
			{
				labelData.top = new FormAttachment(30);
				labelData.left = new FormAttachment(5);
			}
			if(i-1 >= 0)
			{
				labelData.top = new FormAttachment(fieldNameLabels.get(i-1), 25);
				labelData.left = new FormAttachment(5);
			}
			fieldNameLabels.get(i).setLayoutData(labelData);
			
			//Create value label
			fieldValueLabels.add(new Label(mainViewArea, SWT.BORDER));
			String s = instance.getValue(fieldNames[i]) != null ? instance.getValue(fieldNames[i]).toString():"null";
			fieldValueLabels.get(i).setText(s);
			FormData valueData = new FormData(85 + (fieldValueLabels.get(i).getText().length() * 4), SWT.DEFAULT);
			if(i == 0)
			{
				valueData.top = new FormAttachment(30); 
				valueData.left = new FormAttachment(fieldNameLabels.get(i),20);
			}
			if(i-1 >= 0)
			{
				valueData.top = new FormAttachment(fieldNameLabels.get(i),-15);
				valueData.left = new FormAttachment(fieldNameLabels.get(i),20);
			}
			fieldValueLabels.get(i).setLayoutData(valueData);
		}

		mainViewArea.layout();
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
