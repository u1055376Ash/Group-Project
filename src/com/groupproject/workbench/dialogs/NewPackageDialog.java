package com.groupproject.workbench.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.groupproject.workbench.JavaModelHelper;

public class NewPackageDialog extends Dialog{

	Composite container;					//The container object that represents the dialog. 
	Text packageNameValue;					//The name to use when creating the package. 
	
	/*
	 * Constructor
	 */
	public NewPackageDialog(Shell parentShell) {
		super(parentShell);
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
		container.getShell().setText("New Package");
		Label newPackageLabel = new Label(container,SWT.BOLD);
		newPackageLabel.setText("New Package");
		Font labelFont = new Font(container.getDisplay(),"Ariel", 12, SWT.BOLD);
		newPackageLabel.setFont(labelFont);

		packageNameValue = new Text(container, SWT.DEFAULT);
		packageNameValue.setText("my.new.package");
		packageNameValue.setEditable(true);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.minimumWidth = 100;
		packageNameValue.setLayoutData(data);

		container.layout();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		try {
			if(!JavaModelHelper.newPackage(packageNameValue.getText()))
			{
				MessageDialog.openError(container.getShell(), "Error", "Package name should not contain '.package'");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		super.okPressed();
	}
}
