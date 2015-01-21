package com.groupproject.workbench.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import com.groupproject.workbench.BenchInstance;
import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.utility.ObjectBenchUtility;

public class InspectorView extends ViewPart {

	Composite mainViewArea;
	Label header; 
	List<Label> fieldNameLabels;
	List<Label> fieldTypeLabels;
	List<Label> fieldValueLabels; 
	
	
	public InspectorView() {
		// TODO Auto-generated constructor stub
	}

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

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public void update() throws JavaModelException
	{
		//disposeLabels(); 
		BenchInstance instance = ObjectBenchUtility.getActiveInstance();
		System.out.println(instance.packageName + ":" + instance.className);
		String[] fieldNames = JavaModelHelper.getFieldNames(instance.packageName, instance.className);
		header.setText(instance.className + "(Instance)");
		for(int i = 0; i< fieldNames.length;i++)
		{
			//FormData labelData = new FormData(100, SWT.DEFAULT);
			FormData labelData = new FormData(85 + (fieldNames.length * 4), SWT.DEFAULT);
			fieldNameLabels.add(new Label(mainViewArea, SWT.NONE));
			fieldNameLabels.get(i).setText(fieldNames[i]);
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
	
	public void disposeLabels()
	{
		for(Label l:fieldNameLabels)
		{
			if(l != null || l.isDisposed() == false)
			{
				l.dispose();
			}

		}
		fieldNameLabels = new ArrayList<Label>();
	}
	

}
