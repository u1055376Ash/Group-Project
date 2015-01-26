package com.groupproject.workbench.views;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.jface.window.Window;
import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.buttons.ClassButton;
import com.groupproject.workbench.buttons.PackageButton;
import com.groupproject.workbench.buttons.SquareButton;
import com.groupproject.workbench.dialogs.ConstructorDialog;
import com.groupproject.workbench.helpers.StringHelper;
import com.groupproject.workbench.utility.ObjectBenchUtility;

/*
 * Class Diagram View - This is the class taht holds the Class Diagram view. 
 * 
 *TODO - This class needs quite a bit of clean-up, possibly splitting some of it into re-usable sub-classes. 
 */
public class ClassDiagramView extends ViewPart implements ISelectionListener{

	private Composite mainViewArea;						//A reference to the scrollable main view area
	private Label viewHeader; 							//A label showing the active package/project
	private List<PackageButton> packageButtons;			//A list of package buttons used to display packages
	private List<ClassButton> classButtons; 			//List of class buttons used to display classes
	private String activeProjectName;					//The active project name
	private String activePackageName; 					//The active package name
	private SquareButton upButton; 						//The reference to the "up" button. 
	private int state = 0; 								//The state that the view is in Package Viewing = 0 | Class Viewing = 1 
	//TODO - Maybe remove state and add a simple boolean switch? 
	/*
	 * Default Constructor 
	 */
	public ClassDiagramView() {

	}

	@Override
	public void createPartControl(final Composite parent) {
		packageButtons = new ArrayList<PackageButton>();
		classButtons = new ArrayList<ClassButton>();
		activeProjectName = JavaModelHelper.getActiveProjectName();
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER); //Set up scrollable
		mainViewArea = new Composite(sc, SWT.NONE);
		mainViewArea.setLayout(new FormLayout());
		mainViewArea.setSize(400,400);
		sc.setContent(mainViewArea);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(mainViewArea.computeSize(1000, 1000));
		viewHeader = new Label(mainViewArea,0);
		getViewSite().getPage().addSelectionListener(this); //Listens to the project resource explorer
	}

	/*
	 * Check Project Name - This checks whether the project has changed. 
	 */
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

	/*
	 * Display Package View - This displays the packages in a given project. 
	 */
	public void displayPackageView(Composite parent) throws MalformedURLException, Exception
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
	
	/*
	 * Display Class View - This displays the classes in a selected package. 
	 */
	public void displayClassView(Composite parent) throws MalformedURLException, Exception
	{
		//System.out.println("Displaying Classes");
		JavaModelHelper.Initialise(); //Initialises the Java Model
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
	
	/*
	 * Update Header - Updates the header
	 */
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
	
	/*
	 * Add "Up" Button - Adds the up button when required. 
	 */
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
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}

			@Override
			public void mouseDown(MouseEvent e) {}
			@Override
			public void mouseUp(MouseEvent e) {}
			
		});
		refresh();
	}
	
	/*
	 * Create Class Buttons - This is the method that draws the class icons. 
	 *TODO - Add dependencies here! 
	 */
	public void createClassButtons(Composite parent) throws JavaModelException, ClassNotFoundException
	{
		if(upButton == null || upButton.isDisposed())
			{
				addUpButton(parent);
			}
		if(JavaModelHelper.getClassNames(activePackageName) == null)
		{
			return; //If a package is empty then return 
		}
			final String[] classes = JavaModelHelper.getClassNames(activePackageName); //Get the class names 
		
		for(int i = 0; i < classes.length;i++) 
		{
			String entryString = classes[i];
			entryString = entryString.substring(0,entryString.lastIndexOf('.'));
			final int currentClassId = i; 
			Boolean createNew = true; 
			for(int x = 0; x<classButtons.size();x++)
			{
				if(classButtons.get(x).classId == i) //if the button has already been made, update it
				{
					createNew = false; 
					classButtons.get(x).setText(classes[i]);
					classButtons.get(x).setMenu(buildMenuForClass(classes[i], classButtons.get(x)));
					break;
				}
			}
			if(createNew)
			{
				//Create a new class button. 
				try {
					JavaModelHelper.addToClassPath(classes[i], activePackageName); //This makes sure that the class is added to the class path
					//TODO This may cause problems later on, may need to have a method that does this before drawing the buttons.
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				//Add the button
				classButtons.add(new ClassButton(parent,SWT.NONE,classes[i],i,activePackageName));
				classButtons.get(i).setText(entryString);
				classButtons.get(i).setMenu(buildMenuForClass(classes[i], classButtons.get(i)));
				
				//Position the button
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
				
				//Set up the listeners 
				classButtons.get(i).addMouseListener(new MouseListener(){

					@Override
					public void mouseDoubleClick(MouseEvent e) {
						ObjectBenchUtility.openClassInEditor(activePackageName,classes[currentClassId]);
					}

					@Override
					public void mouseDown(MouseEvent e) {}
					@Override
					public void mouseUp(MouseEvent e) {}
				});
				classButtons.get(i).setLayoutData(buttonData);
				classButtons.get(i).getColor();//hack to fix a bug
			}
		}
		parent.layout();
		parent.getShell().layout();
	}
	
	/*
	 * Build Menu for Class - This method builds the right click menu for a class.
	 * Here we get all of the constructor information.
	 * TODO - This method needs cleaning and maybe moving into the utility class. 
	 */
	private Menu buildMenuForClass(final String selectedClass, final ClassButton bn) throws JavaModelException
	{
		Menu popupMenu = new Menu(bn);
		//Get String values
		String[] constructorNames= JavaModelHelper.getConstructorNames(bn.packageName, bn.className);
		final String[][] constructorParamaterTypes = JavaModelHelper.getConstructorParameters(bn.packageName, bn.className);
		final String[][] constructorParamaterNames = JavaModelHelper.getConstructorParamaterNames(bn.packageName, bn.className);
		
		for(int i = 0; i < constructorNames.length;i++)
		{
			final int index = i; // horrible hack to pass the constructor parameter types 
			MenuItem instantiateItem = new MenuItem(popupMenu, SWT.CASCADE);
			//Build the string for constructor
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
				public void widgetSelected(SelectionEvent e){
					//System.out.println("INSTANTIATING...");
					try {
						Object object = null; 
						if(constructorParamaterTypes[index].length > 0)
						{
							List<Class<?>> classes = new ArrayList<Class<?>>();
							for(String s:constructorParamaterTypes[index])
							{
								//System.out.println(s);
								Class<?> myClass = ObjectBenchUtility.getClassFromType(s);
								//TODO This whole section needs expanding to account for non-native Java types. 
								if(myClass == null)
								{
									//Need to add method to the JavaModelHelper to check if a classname exists. 
									//myClass = JavaModelHelper.getClassFromLoader(StringHelper.getQualifiedName(s, activePackageName));
								}
								if(myClass == null)
								{
									//myClass = Class.forName(s); //This is going to cause more trouble than its worth. 
								}
								classes.add(myClass);
							}
							ConstructorDialog dialog = new ConstructorDialog(mainViewArea.getShell(),JavaModelHelper.getClassFromLoader((StringHelper.getQualifiedName(selectedClass, activePackageName))).getDeclaredConstructor(classes.toArray(new Class<?>[classes.size()])));
							if(dialog.open() == Window.OK)
							{
								object = dialog.getInstance();
								//System.out.println("IM IN");
								//ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName, object);
							}
						}
						ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName, object);
					} catch (Exception e1) {
						e1.printStackTrace();
						//I'll confess there were about 10 catch clauses here before.
					} 
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}
		
		//Create the secondary menu underneath the constructors 
		new MenuItem(popupMenu, SWT.SEPARATOR);
		MenuItem openEditorItem = new MenuItem(popupMenu, SWT.CASCADE);
		openEditorItem.setText("Open Editor");
		openEditorItem.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				ObjectBenchUtility.openClassInEditor(bn.packageName,bn.className);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		return popupMenu;
		
	}
	
	/*
	 * Create Package Buttons - Creates the buttons to represent packages
	 */
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
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
						}

						@Override
						public void mouseDown(MouseEvent e) {}
						@Override
						public void mouseUp(MouseEvent e) {}

					});
					
					packageButtons.get(i).setLayoutData(buttonData);
				}
				//Get number of classes
				if(JavaModelHelper.getNumberOfClassesFromPackage(packages[i]) <= 0)
				{
					if(ObjectBenchUtility.showEmptyPackages() == false)
					{
						packageButtons.get(i).setVisible(false);
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		parent.layout();
		parent.getShell().layout();
	}
	
	@Override
	public void setFocus() {}
	
	@Override
	public void dispose()
	{
		clear();
		super.dispose();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 * Selection Changed - This controls the event of a user changing projects in the resource view. 
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
	{
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
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		

		
	}
	
	/*
	 * View Classes - Switches the system over to view classes 
	 */
	private void viewClasses(String mypackage) throws MalformedURLException, Exception
	{
		clear();
		activePackageName = mypackage; 
		state = 1;
		getViewSite().getPage().addSelectionListener(this);
		displayClassView(mainViewArea);
	}
	
	/*
	 * View Packages - Switches the system over to view packages
	 */
	private void viewPackages() throws MalformedURLException, Exception
	{
		clear();
		activePackageName = ""; 
		state = 0;
		getViewSite().getPage().addSelectionListener(this);
		displayPackageView(mainViewArea);
	}
	
	/*
	 * Refresh - Refreshes the view
	 */
	void refresh()
	{
		mainViewArea.layout();
		mainViewArea.getShell().layout();
	}
	
	/*
	 * Clear - Clears the view
	 */
	private void clear()
	{
		//viewHeader.dispose();
		getViewSite().getPage().removeSelectionListener(this);
		disposeButtons();
	}
	
	/*
	 * Dispose Buttons - Disposes the buttons as needed.
	 */
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

