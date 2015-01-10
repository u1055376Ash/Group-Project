package com.groupproject.workbench.buttons;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

public class ClassButton extends SquareButton{
	
	public int classId;
	public String className; 
	public String packageName; 
	
	public ClassButton(Composite parent, int style, String cn, int id,String pn) {
		super(parent, style);
		classId = id;
		className = cn;
		packageName = pn; 
		//setText(packageValue);
		//Image image = ImageDescriptor.createFromURL(getClass().getResource("/img/classButton.png")).createImage();
		setRoundedCorners(true);
		backgroundColor = new Color(parent.getDisplay(), 150,255,100);
		backgroundColor2 = new Color(parent.getDisplay(),255,255,255);
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
		

	}
	
	@Override
	public void checkSubclass()
	{
		
	}
}
