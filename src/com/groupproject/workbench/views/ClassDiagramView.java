package com.groupproject.workbench.views;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;

import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.buttons.ClassButton;
import com.groupproject.workbench.buttons.PackageButton;
import com.groupproject.workbench.buttons.SquareButton;
import com.groupproject.workbench.helpers.StringHelper;
import com.groupproject.workbench.utility.ObjectBenchUtility;

public class ClassDiagramView extends ViewPart implements ISelectionListener{

	private Rectangle clientArea; 
	private GC gc; 
	private Composite mainViewArea;
	private Label viewHeader; 

	
	private List<PackageButton> packageButtons;
	private List<ClassButton> classButtons; 
	private String activeProjectName;
	private String activePackageName; 
	
	private SquareButton upButton; 
	
	private int state = 0; 
	
	public ClassDiagramView() {

	}

	@Override
	public void createPartControl(final Composite parent) {
		packageButtons = new ArrayList<PackageButton>();
		classButtons = new ArrayList<ClassButton>();
		activeProjectName = JavaModelHelper.getActiveProjectName();
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		mainViewArea = new Composite(sc, SWT.NONE);
		mainViewArea.setLayout(new FormLayout());
		mainViewArea.setSize(400,400);
		sc.setContent(mainViewArea);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(mainViewArea.computeSize(1000, 1000));
		viewHeader = new Label(mainViewArea,0);
		getViewSite().getPage().addSelectionListener(this);
	}

	private void checkProjectName()
	{
		if(JavaModelHelper.getActiveProjectName() == null || JavaModelHelper.getActiveProjectName().isEmpty())
		{
			return; 
		}
		else
		{
			if(!JavaModelHelper.getActiveProjectName().equals(activeProjectName))
			{
				activeProjectName = JavaModelHelper.getActiveProjectName();
			}
		}
	}

	public void displayPackageView(Composite parent) throws JavaModelException
	{
		JavaModelHelper.Initialise();
		//activeProjectName = JavaModelHelper.getActiveProjectName(); 
		checkProjectName();
		if(activeProjectName != null)
		{
			updateHeader();
			createPackageButtons(parent);
		}
	}
	
	public void displayClassView(Composite parent) throws JavaModelException
	{
		//System.out.println("Displaying Classes");
		JavaModelHelper.Initialise();
		//activeProjectName = JavaModelHelper.getActiveProjectName(); 
		checkProjectName();
		if(activeProjectName != null)
		{
				updateHeader();
				createClassButtons(parent);
		}
		else
		{
			System.out.println("No Active Project...");
		}
		
	}
	
	private void updateHeader() 
	{
		if(state == 0)
		{
			//activeProjectName = JavaModelHelper.getActiveProjectName(); 
			checkProjectName();
			if(activeProjectName != null)
			{
				viewHeader.setText("Current Project: " + activeProjectName);
			}
			else
			{
				viewHeader.setText("Please Select a Project");
				return; 
			}
		}
		if(state == 1)
		{
			if(activePackageName != null || !activePackageName.isEmpty())
			{
				viewHeader.setText("Current Package: " + activePackageName);
			}
		}
			

	}
	
	
	private void addUpButton(Composite parent)
	{
		upButton = new SquareButton(parent,SWT.NONE);
		FormData upData = new FormData(40,40);
		upData.top = new FormAttachment(7);
		upData.left = new FormAttachment(2);
		upButton.setLayoutData(upData);
		upButton.setText("UP");
		upButton.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				try {
					viewPackages();
				} catch (JavaModelException e1) {
					e1.printStackTrace();
				}
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		refresh();
	}
	public void createClassButtons(Composite parent) throws JavaModelException
	{
		if(upButton == null || upButton.isDisposed())
			{
				addUpButton(parent);
			}
		if(JavaModelHelper.getClassNames(activePackageName) == null)
		{
			return;
		}
			final String[] classes = JavaModelHelper.getClassNames(activePackageName);
		

		for(int i = 0; i < classes.length;i++)
		{
			String entryString = classes[i];
			entryString = entryString.substring(0,entryString.lastIndexOf('.'));
			final int currentClassId = i; 
			Boolean createNew = true; 
			for(int x = 0; x<classButtons.size();x++)
			{
				if(classButtons.get(x).classId == i)
				{
					createNew = false; 
					classButtons.get(x).setText(classes[i]);
					classButtons.get(x).setMenu(buildMenuForClass(classes[i], classButtons.get(x)));
					break;
				}
			}
			if(createNew)
			{
				classButtons.add(new ClassButton(parent,SWT.NONE,classes[i],i,activePackageName));
				classButtons.get(i).setText(entryString);
				classButtons.get(i).setMenu(buildMenuForClass(classes[i], classButtons.get(i)));
				FormData buttonData = new FormData(90+(entryString.length() * 3),80);
				if(i-1 >= 0)
				{
					buttonData.left = new FormAttachment(classButtons.get(i-1), 50, SWT.RIGHT);
					buttonData.bottom = new FormAttachment(classButtons.get(i-1), 0, SWT.BOTTOM);
					buttonData.top = new FormAttachment(classButtons.get(i-1),0, SWT.TOP);
				}
				if(i == 0)
				{
					buttonData.left = new FormAttachment(5);
					buttonData.top = new FormAttachment(20);
				}	
				classButtons.get(i).addMouseListener(new MouseListener(){

					@Override
					public void mouseDoubleClick(MouseEvent e) {
						ObjectBenchUtility.openClassInEditor(activePackageName,classes[currentClassId]);
					}

					@Override
					public void mouseDown(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void mouseUp(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

				});
				classButtons.get(i).setLayoutData(buttonData);
			}
		}
		parent.layout();
		parent.getShell().layout();
	}
	
	private Menu buildMenuForClass(final String selectedClass, final ClassButton bn) throws JavaModelException
	{
		Menu popupMenu = new Menu(bn);

		String[] constructorNames= JavaModelHelper.getConstructorNames(bn.packageName, bn.className);
		String[][] constructorParamaterTypes = JavaModelHelper.getConstructorParameters(bn.packageName, bn.className);
		String[][] constructorParamaterNames = JavaModelHelper.getConstructorParamaterNames(bn.packageName, bn.className);
		for(int i = 0; i < constructorNames.length;i++)
		{
			MenuItem instantiateItem = new MenuItem(popupMenu, SWT.CASCADE);
			String addString = "new" + constructorNames[i] + "(";
			for(int x = 0; x < constructorParamaterTypes[i].length;x++)
			{
				addString += StringHelper.fixType(constructorParamaterTypes[i][x]) + " ";
				addString += constructorParamaterNames[i][x] + " ";
				
			}
			addString += ") ";
			instantiateItem.setText(addString);
			instantiateItem.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					//System.out.println("INSTANTIATING...");
					try {
						ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName);
					} catch (JavaModelException e1) {
						e1.printStackTrace();
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});

			
		}
		new MenuItem(popupMenu, SWT.SEPARATOR);
		MenuItem openEditorItem = new MenuItem(popupMenu, SWT.CASCADE);
		openEditorItem.setText("Open Editor");
		openEditorItem.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				ObjectBenchUtility.openClassInEditor(bn.packageName,bn.className);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});


		return popupMenu;
		
	}
	
	public void createPackageButtons(Composite parent)
	{
		try {
			final String[] packages = JavaModelHelper.getPackageNames(activeProjectName);
			for(int i = 0; i < packages.length;i++)
			{
				final int currentNumber = i; 
				Boolean createNew = true; 
				for(int x = 0; x<packageButtons.size();x++)
				{
					if(packageButtons.get(x).packageId == i)
					{
						createNew = false; 
						packageButtons.get(x).setText(packages[i] +"\n Classes: " + JavaModelHelper.getNumberOfClassesFromPackage(packages[i]));
						break;
					}

				}
				if(createNew)
				{
					packageButtons.add(new PackageButton(parent,SWT.NONE,packages[i],i,(JavaModelHelper.getNumberOfClassesFromPackage(packages[i]) <= 0)));
					packageButtons.get(i).setText(packages[i] +"\n"
							+ "\n Classes: " + JavaModelHelper.getNumberOfClassesFromPackage(packages[i]) );
					//System.out.println("Button Made: " + packages[i]);
					FormData buttonData = new FormData(90+(packages[i].length()*3),80);
					if(i-1 >= 0)
					{
						buttonData.left = new FormAttachment(packageButtons.get(i-1), 50, SWT.RIGHT);
						buttonData.bottom = new FormAttachment(packageButtons.get(i-1), 0, SWT.BOTTOM);
						buttonData.top = new FormAttachment(packageButtons.get(i-1),0, SWT.TOP);
					}
					if(i == 0)
					{
						buttonData.left = new FormAttachment(5);
						buttonData.top = new FormAttachment(20);
					}
					packageButtons.get(i).addMouseListener(new MouseListener(){

						@Override
						public void mouseDoubleClick(MouseEvent e) {
							try {
								viewClasses(packages[currentNumber]);
							} catch (JavaModelException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

						@Override
						public void mouseDown(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
						@Override
						public void mouseUp(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}

					});
					packageButtons.get(i).setLayoutData(buttonData);
				}
			}
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		parent.layout();
		parent.getShell().layout();
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose()
	{
		clear();
		super.dispose();

	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
	{
		//clear();
		//refresh();
		try{
			String checkString = "[P/" + activeProjectName + "]";
			if(state == 0)
			{
				if(!selection.toString().equals(checkString))
				{
					viewPackages();
				}
				displayPackageView(mainViewArea);	
			}
			if(state == 1)
			{
				if(!selection.toString().equals(checkString))
				{
					viewPackages();
					//displayPackageView(mainViewArea);
					return;
				}
				displayClassView(mainViewArea);
			}
		}
		catch(JavaModelException e)
		{
			e.printStackTrace();
		}

		
	}
	
	private void viewClasses(String mypackage) throws JavaModelException
	{
		clear();
		activePackageName = mypackage; 
		state = 1;
		getViewSite().getPage().addSelectionListener(this);
		displayClassView(mainViewArea);
	}
	
	private void viewPackages() throws JavaModelException
	{
		clear();
		activePackageName = ""; 
		state = 0;
		getViewSite().getPage().addSelectionListener(this);
		displayPackageView(mainViewArea);
	}
	void refresh()
	{
		mainViewArea.layout();
		mainViewArea.getShell().layout();
	}
	
	private void clear()
	{
		//viewHeader.dispose();
		getViewSite().getPage().removeSelectionListener(this);
		disposeButtons();
	}
	
	private void disposeButtons()
	{
		if(state == 0)
		{
			for(PackageButton button:packageButtons)
			{
				button.dispose();
			}
			packageButtons = new ArrayList<PackageButton>();
		}
		if(state == 1)
		{
			for(ClassButton button:classButtons)
			{
				button.dispose();
			}
			classButtons = new ArrayList<ClassButton>();
			if(upButton != null)
			{
					if(!upButton.isDisposed())
					{
						//upButton.setVisible(false);
						//System.out.println("Disposing up button");
						upButton.dispose();
					}
			}
		}

	}
	


}

