package com.groupproject.workbench.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.groupproject.workbench.JavaModelHelper;

public class NewClassDialog extends Dialog {

	
	Composite container;				//The container object that represents the dialog. 
	Text classNameField;				//The field that a class name is entered into.
	Button[] radioButtons;				//A collection of radio buttons.
	String pName; 						//The name of the package to add the class to. 
	
	/*
	 * Constructor 
	 */
	public NewClassDialog(Shell parentShell, String packageName) {
		super(parentShell);
		pName = packageName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent){
		Composite area = (Composite) super.createDialogArea(parent);
		container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1,false); 
		layout.marginBottom = 10;
		//container.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true, true));
		container.setLayout(layout);
		drawGUI();
		return container;
	}
	
	/*
	 * Draw GUI - Draws the Dialog GUI. 
	 */
	void drawGUI()
	{
		container.getShell().setText("New Class");
		Label newClassLabel = new Label(container,SWT.BOLD);
		newClassLabel.setText("New Class");
		Font labelFont = new Font(container.getDisplay(),"Ariel", 12, SWT.BOLD);
		newClassLabel.setFont(labelFont);
		
		classNameField = new Text(container, SWT.SINGLE|SWT.BORDER);
		classNameField.setText("MyNewClass");
		classNameField.setEditable(true);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.minimumWidth = 100;
		classNameField.setLayoutData(data);
		radioButtons = new Button[6];
		
		radioButtons[0] = new Button(container, SWT.RADIO);
		radioButtons[0].setSelection(true);
		radioButtons[0].setText("Class");
		
		radioButtons[1] = new Button(container, SWT.RADIO);
		radioButtons[1].setText("Abstract Class");

		radioButtons[2] = new Button(container, SWT.RADIO);
		radioButtons[2].setText("Interface");
		
		radioButtons[3] = new Button(container, SWT.RADIO);
		radioButtons[3].setText("Applet");
		
		radioButtons[4] = new Button(container, SWT.RADIO);
		radioButtons[4].setText("Unit Test");
		
		radioButtons[5] = new Button(container, SWT.RADIO);
		radioButtons[5].setText("Enum");
		
		
		for(Button b:radioButtons)
		{
			GridData bData = new GridData(SWT.FILL,SWT.FILL,true,true);
			bData.verticalIndent = 2;
			b.setLayoutData(bData);
		}
		container.layout();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		try {
			String className = classNameField.getText().replace(" ", "");
			if(!JavaModelHelper.newClass(className, getRadioButton(),pName))
			{
				MessageDialog.openError(container.getShell(), "Error", "No duplicate classes, remove existing class first!");
			}
		} catch (Exception e) {
			MessageDialog.openError(container.getShell(), "Error", "Class names cannot contain spaces and certain special characters!");
			e.printStackTrace();
		} 
		super.okPressed();
	}
	
	/*
	 * Get Radio Button - gets the int value of the radio button, used to determine the template to be used.
	 */
	int getRadioButton()
	{
		for(int i = 0; i< radioButtons.length;i++)
		{
			if(radioButtons[i].getSelection())
			{
				return i;
			}
		}
		return 0;
	}
}
