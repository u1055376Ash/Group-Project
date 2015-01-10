package com.groupproject.workbench.views;

import java.util.ArrayList;
import java.util.List;









import org.eclipse.ui.part.ViewPart;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.*; 
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.buttons.ClassButton;
import com.groupproject.workbench.buttons.PackageButton;
import com.groupproject.workbench.utility.ObjectBenchUtility;

public class ObjectBenchView extends ViewPart {

	private List<ClassButton> classButtons; 
	private Composite mainViewArea;
	
	public ObjectBenchView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		mainViewArea = new Composite(sc, SWT.NONE);
		mainViewArea.setLayout(new FormLayout());
		mainViewArea.setSize(600,100);
		sc.setContent(mainViewArea);
		sc.setExpandHorizontal(true);
		sc.setMinSize(mainViewArea.computeSize(1000, 100));
		sc.setMinHeight(110);
		ObjectBenchUtility.registerObjectBench(this);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	public void addObject(String className, String packageName) throws JavaModelException
	{
		System.out.println("Instantiating: " + className);
		if(classButtons == null)
		{
			classButtons = new ArrayList<ClassButton>();
		}
		String entryString = className;
		entryString = entryString.substring(0,entryString.lastIndexOf('.'));
		entryString += " (Instance) ";
		ClassButton newButton = new ClassButton(mainViewArea,SWT.NONE,className,classButtons.size(), packageName);
		newButton.setText(entryString);
		classButtons.add(newButton);
		FormData buttonData = new FormData(90+(entryString.length() * 3),80);
		int i = classButtons.size()-1;
		if(i == 0)
		{
			System.out.println("First");
			buttonData.left = new FormAttachment(2);
			buttonData.top = new FormAttachment(2);
			//buttonData.bottom = new FormAttachment(50);
		}
		else
		{
			if(i > 0)
			{
				System.out.println("Another");
				buttonData.left = new FormAttachment(classButtons.get(i-1),15,SWT.RIGHT);
				buttonData.bottom = new FormAttachment(classButtons.get(i-1),0,SWT.BOTTOM);
				buttonData.top = new FormAttachment(classButtons.get(i-1),0,SWT.TOP);
			}
		}
		classButtons.get(i).setLayoutData(buttonData);
		classButtons.get(i).setMenu(buildMenuForClass(classButtons.get(i)));
		mainViewArea.layout();
		mainViewArea.getShell().open();
	}
	
	private Menu buildMenuForClass(ClassButton bn) throws JavaModelException
	{
		Menu popupMenu = new Menu(bn);
		if(JavaModelHelper.getClassMethodNames(bn.packageName, bn.className) != null)
		{
			String[] methods = JavaModelHelper.getClassMethodNames(bn.packageName, bn.className);
			for(String s:methods)
			{
				MenuItem methodItem = new MenuItem(popupMenu, SWT.CASCADE);
				methodItem.setText(s);
				methodItem.addSelectionListener(new SelectionListener(){

					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
		
		}
		return popupMenu;
		
	}
}
