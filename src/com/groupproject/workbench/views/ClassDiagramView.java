package com.groupproject.workbench.views;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.buttons.ClassButton;
import com.groupproject.workbench.buttons.PackageButton;
import com.groupproject.workbench.buttons.SquareButton;
import com.groupproject.workbench.dialogs.ConstructorDialog;
import com.groupproject.workbench.dialogs.NewClassDialog;
import com.groupproject.workbench.dialogs.NewPackageDialog;
import com.groupproject.workbench.perspectives.ObjectBenchPerspective;
import com.groupproject.workbench.utility.ObjectBenchUtility;
import com.groupproject.workbench.utility.StringHelper;

/*
 * Class Diagram View - This is the class that holds the Class Diagram view. 
 * 
 */
public class ClassDiagramView extends ViewPart implements ISelectionListener{

	private Composite mainViewArea;						//A reference to the scrollable main view area
	private Label viewHeader; 							//A label showing the active package/project
	private List<PackageButton> packageButtons;			//A list of package buttons used to display packages
	private List<ClassButton> classButtons; 			//List of class buttons used to display classes
	private String activeProjectName;					//The active project name
	private String activePackageName; 					//The active package name
	private SquareButton upButton; 						//The reference to the "up" button. 
	private SquareButton newClassButton;				//The reference to the "new class" button.
	private SquareButton newPackageButton;				//The reference to the "new package" button.
	private int state = 0; 								//The state that the view is in Package Viewing = 0 | Class Viewing = 1 
	
	/*
	 * Default Constructor 
	 */
	public ClassDiagramView() {	}

	/*
	 * Create Part Control - Sets up the view. 
	 */
	@Override
	public void createPartControl(final Composite parent) {
		packageButtons = new ArrayList<PackageButton>();
		classButtons = new ArrayList<ClassButton>();
		try {
			JavaModelHelper.Initialise();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		mainViewArea.setMenu(buildContextMenu());
		getViewSite().getPage().addSelectionListener(this); //Listens to the project resource explorer
		ObjectBenchPerspective.hideEditor();
		
		
	}
	
	/*
	 * Build Context Menu - Builds the context menu for the class view. 
	 */
	Menu buildContextMenu()
	{
		if(activeProjectName == null)
		{
			return null;
		}
		Menu menu = new Menu(mainViewArea);
		MenuItem newClassItem = new MenuItem(menu, SWT.CASCADE);
		MenuItem newPackageItem = new MenuItem(menu, SWT.NONE);
		newClassItem.setText("New Class");
		newPackageItem.setText("New Package");
		
		newClassItem.addSelectionListener(new SelectionListener(){

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {addClass();}
			
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		newPackageItem.addSelectionListener(new SelectionListener(){
			
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {addPackage();}
			
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		
		return menu; 
	}
	
	/*
	 * Add Class - Displays the dialog to add a class to the active project. 
	 */
	void addClass()
	{
		NewClassDialog dialog = new NewClassDialog(mainViewArea.getShell(), activePackageName);
		if(dialog.open() == Window.OK){}
		try {
			JavaModelHelper.buildProject(activeProjectName);
			fullRefresh();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	/*
	 * Add Package - Displays the dialog to add a package to the active project. 
	 */
	void addPackage()
	{
		NewPackageDialog dialog = new NewPackageDialog(mainViewArea.getShell());
		if(dialog.open() == Window.OK){}
		try {
			JavaModelHelper.buildProject(activeProjectName);
			fullRefresh();
		} catch (Exception e) {e.printStackTrace();}
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
				mainViewArea.setMenu(buildContextMenu());
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
		if(activeProjectName != null && JavaModelHelper.isProjectOpen(activeProjectName))
		{
			updateHeader();
			createPackageButtons(parent);
			disposeButtons(true);
			createClassButtons(parent, true);
			return;
		}
		else
		{
			updateHeader();
			activePackageName = "";
			disposeButtons(false);
		}
	}
	
	/*
	 * Display Class View - This displays the classes in a selected package. 
	 */
	public void displayClassView(Composite parent) throws MalformedURLException, Exception
	{
		//System.out.println("Displaying Classes");
		JavaModelHelper.Initialise(); //Initialises the Java Model
		disposeButtons(false);
		//activeProjectName = JavaModelHelper.getActiveProjectName(); 
		checkProjectName();
		if(activeProjectName != null)
		{
				updateHeader();
				createClassButtons(parent,false);
		}
		else
		{
			//Use for debugging
			//System.out.println("No Active Project...");
		}
		
	}
	
	/*
	 * Update Header - Updates the header
	 */
	private void updateHeader() throws MalformedURLException, Exception 
	{
		if(state == 0)
		{
			viewHeader.setSize(1000,30);
			//activeProjectName = JavaModelHelper.getActiveProjectName(); 
			checkProjectName();
			if(activeProjectName != null)
			{
				if(JavaModelHelper.isProjectOpen(activeProjectName))
				{
					viewHeader.setText("Current Project: " + activeProjectName);
				}
				else
				{
					viewHeader.setText("Current Project: " + activeProjectName + " (Closed)");
				}
				
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

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				try {
					viewPackages();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
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
	 * Add New Class Button - Adds the "New Class" button to the view. 
	 */
	private void addNewClassButton(Composite parent)
	{
		newClassButton = new SquareButton(parent, SWT.NONE);
		FormData newClassData = new FormData(75,40);
		newClassData.top = new FormAttachment(7);
		if(upButton != null || !upButton.isDisposed())
		{
			newClassData.left = new FormAttachment(upButton, 50, SWT.LEFT);
		}
		else
		{
			newClassData.left = new FormAttachment(2); 
		}
		newClassButton.setLayoutData(newClassData);
		newClassButton.setText("New Class");
		newClassButton.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {}

			@Override
			public void mouseDown(MouseEvent e) {}
			@Override
			public void mouseUp(MouseEvent e) {addClass();}
			
		});
		refresh();
		
	}
	
	/*
	 * Add New Package Button - Adds the "New Package" button to the view. 
	 */
	private void addNewPackageButton(Composite parent)
	{
		newPackageButton = new SquareButton(parent, SWT.NONE);
		FormData newPackageData = new FormData(85,40);
		newPackageData.top = new FormAttachment(7);
		newPackageData.left = new FormAttachment(newClassButton, 85, SWT.LEFT);
		newPackageButton.setLayoutData(newPackageData);
		newPackageButton.setText("New Package");
		newPackageButton.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {}

			@Override
			public void mouseDown(MouseEvent e) {}
			@Override
			public void mouseUp(MouseEvent e) {addPackage();}
			
		});
	}
	
	/*
	 * Create Class Buttons - This is the method that draws the class icons. 
	 */
	public void createClassButtons(final Composite parent, Boolean pushDown) throws MalformedURLException, Exception
	{
		if(!JavaModelHelper.isProjectOpen(activeProjectName))
		{
			disposeButtons(false);
			return;
		}
		if(upButton == null || upButton.isDisposed() && !activePackageName.equals(""))
			{
				addUpButton(parent);

			}
		addNewClassButton(parent);
		addNewPackageButton(parent);
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
					classButtons.get(x).setText(StringHelper.stripExtension(classes[i]));
					classButtons.get(x).className = classes[i];
					classButtons.get(x).packageName = activePackageName;
					classButtons.get(x).setMenu(buildMenuForClass(classes[i], classButtons.get(x)));
					classButtons.get(x).addMouseListener(new MouseListener(){

						@Override
						public void mouseDoubleClick(MouseEvent e) {
							ObjectBenchPerspective.showEditor();
							ObjectBenchUtility.openClassInEditor(activePackageName,classes[currentClassId]);
						}

						@Override
						public void mouseDown(MouseEvent e) {}
						@Override
						public void mouseUp(MouseEvent e) {}
					});
					classButtons.get(x).getColor();//hack to fix a bug
					break;
				}
			}
			if(createNew)
			{
				//Create a new class button. 

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
					buttonData.top =  new FormAttachment(classButtons.get(i-1),0, SWT.TOP);
				}
				if(i == 0)
				{
					buttonData.left = new FormAttachment(5);
					buttonData.top = pushDown ?  new FormAttachment(25):new FormAttachment(15);
				}	
				
				//Probably move these somewhere more appropriate 
				final Point[] offset = new Point[1];
				final ClassButton currentClassButton = classButtons.get(i);
				
				//Set up the listeners 
				//TODO - Fix glitchiness here & repositioning of buttons  
				classButtons.get(i).addMouseListener(new MouseListener(){

					@Override
					public void mouseDoubleClick(MouseEvent e) {
						ObjectBenchUtility.openClassInEditor(activePackageName,classes[currentClassId]);
					}

					@Override
					public void mouseDown(MouseEvent e) {
						try {
							System.out.println("Mouse down event for: " + currentClassButton.getMyClass());
							Point pt1 = currentClassButton.toDisplay(0, 0);
					       // Point pt2 = parent.getShell().toDisplay(e.x, e.y);
					       // offset[0] = new Point(pt2.x - pt1.x, pt2.y - pt1.y);
							offset[0] = new Point(pt1.x, pt1.y);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					
					@Override
					public void mouseUp(MouseEvent e) {
						System.out.println("Mouse up event for: " + classes[currentClassId]);
						 offset[0] = null;
					}
				});
				
				classButtons.get(i).addMouseMoveListener(new MouseMoveListener(){

					@Override
					public void mouseMove(MouseEvent e) {
						if (offset[0] != null) {
							System.out.println("Mouse move event for: " + classes[currentClassId]);
				            Point pt = offset[0];
				            currentClassButton.setLocation(e.x - pt.x, e.y - pt.y);
					}
					}					
				});
				
				classButtons.get(i).setLayoutData(buttonData);
				classButtons.get(i).getColor();//hack to fix a bug
			}
		}
		parent.layout();
		parent.getShell().layout();
		
		for(int j = 0; j < classes.length;j++) 
		{
			checkSuperClass(classButtons.get(j),parent);	
			checkDependencies(classButtons.get(j),parent);
		}
		
		parent.addListener(SWT.Paint, new Listener() { // paint listener to redraw links
		      public void handleEvent(Event e) {
		    	  for(int j = 0; j < classes.length;j++) 
		  		{
		  			try {
						checkSuperClass(classButtons.get(j),mainViewArea);
					} catch (Exception e1) {
						//e1.printStackTrace();
					}	
		  		}
		        }
		});		
	}
	
	/*
	 * Check Super Class - Checks if the class is inherited. 
	 */
	void checkSuperClass(ClassButton b, Composite parent) throws Exception
	{
		Class<?> superClass = JavaModelHelper.getSuperclass(b.getMyClass()); 
		if(superClass != null)
		{
			for(ClassButton a:classButtons)
			{
				if(a.getMyClass().equals(superClass))		
				{
					drawInheritanceLink(b,a,parent); //Maybe 
				}
			}
		}
	}
	
	/*
	 * Check Dependencies - This method will check to see if a given class depends on any other class in the diagram. 
	 */
	void checkDependencies(ClassButton b, Composite parent) throws JavaModelException
	{
		String[] fieldTypes = JavaModelHelper.getFieldTypes(b.packageName,b.className);

		List<ClassButton> dependencies = new ArrayList<ClassButton>();
		//System.out.println("Fields\n ------");
		//debugAddToDependencies(fieldTypes,dependencies,"Fields\n-----------\n");
		addToDependencies(fieldTypes,dependencies);
		String[][] constructorParameters = JavaModelHelper.getConstructorParameters(b.packageName, b.className);
		for(String[] t:constructorParameters)
		{
			//System.out.println("Constructors\n -----------");
			//debugAddToDependencies(t,dependencies,"Constructors\n-----------\n");
			addToDependencies(t,dependencies);
		}
		String[][] methodParameters = JavaModelHelper.getClassMethodParameterTypes(b.packageName, b.className);
		for(String[] m:methodParameters)
		{
			//System.out.println("Methods\n -------");
			//debugAddToDependencies(m,dependencies,"Methods\n-------\n");
			addToDependencies(m,dependencies);
		}
		
		for(ClassButton a:dependencies)
		{
			drawDependenceLink(b,a,parent);
		}
		
	}
	
	/*
	 * Add To Dependencies - Adds dependencies to a list based on a specified set of types represented by strings. 
	 */
	void addToDependencies(String[] strings, List<ClassButton> list)
	{
		for(String s:strings)
		{
			for(ClassButton a:classButtons)
			{
				if(StringHelper.stripExtension(a.className).equals(StringHelper.fixType(s)))
				{
					//System.out.println("Depending on: " + StringHelper.fixType(s) + "\n");
					if(!list.contains(a))
					{
						
						list.add(a);					
					}
				}
			}
			
			//System.out.println(b.className + " contains: " + StringHelper.fixType(s));
		}
	}
	
	/*
	 * Debug Add To Dependencies - Used to check dependencies. 
	 */
	void debugAddToDependencies(String[] strings, List<ClassButton> list, String type)
	{
		System.out.println(type);
		addToDependencies(strings,list);
	}
	
	/*
	 * Draw Inheritance Link - Draws an arrow tipped line between two given classes. 
	 */
	void drawInheritanceLink(ClassButton b, ClassButton a, Composite parent) throws Exception
	{
		GC gc = new GC(parent);
		gc.drawLine(a.getLocation().x, a.getLocation().y+40, b.getLocation().x, b.getLocation().y+40);
		
		//gc.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE)); 
		//gc.drawPolygon(new int[] { a.getLocation().x,a.getLocation().y+40,a.getLocation().x-10,a.getLocation().y+47,a.getLocation().x-10,a.getLocation().y+33 });
		//gc.fillPolygon(new int[] { a.getLocation().x-1,a.getLocation().y+40,a.getLocation().x-9,a.getLocation().y+46,a.getLocation().x-9,a.getLocation().y+34 });
	}
	
	/*
	 * Draw Dependence Link - Draws a dashed? line between two given classes that are dependent on each other. 
	 */
	void drawDependenceLink(ClassButton a, ClassButton b, Composite parent)
	{
		//TODO - Implement this. 
	}
	
	/*
	 * Build Menu for Class - This method builds the right click menu for a class.
	 * Here we get all of the constructor information.
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
			String addString = "new " + constructorNames[i] + "(";
			
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
								Class<?> myClass = ObjectBenchUtility.isKnown(s) ? 
												   ObjectBenchUtility.getClassFromType(s):ObjectBenchUtility.getClassFromType(StringHelper.getQualifiedName(StringHelper.fixType(s), activePackageName));
								if(myClass == null)
								{
									System.out.println("Class-Not-Found: " + s);
									continue;
								}
								classes.add(myClass);
							}
							System.out.println(classes.toString());
							ConstructorDialog dialog = new ConstructorDialog(mainViewArea.getShell(),JavaModelHelper.getClassFromLoader((StringHelper.getQualifiedName(selectedClass, activePackageName))).getDeclaredConstructor(classes.toArray(new Class<?>[classes.size()])));
							if(dialog.open() == Window.OK)
							{
								if(dialog.getReturnCode() != Window.CANCEL)
								{
									object = dialog.getInstance();
									ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName, object);
								}
							}
						}
						else
						{
							object = JavaModelHelper.getClassFromLoader((StringHelper.getQualifiedName(selectedClass, activePackageName))).newInstance();
							ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName, object);
						}
						
					} catch (Exception e1) {
						e1.printStackTrace();
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
		
		MenuItem renameClassItem = new MenuItem(popupMenu, SWT.CASCADE);
		renameClassItem.setText("Rename Class");
		renameClassItem.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		MenuItem deleteClassItem = new MenuItem(popupMenu, SWT.CASCADE);
		deleteClassItem.setText("Delete Class");
		deleteClassItem.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ObjectBenchUtility.DeleteFile(JavaModelHelper.getClassFile(bn.packageName, bn.className));
					ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE,null);
					fullRefresh();
					refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		return popupMenu;
		
	}
	
	/*
	 * Create Package Menu - Creates a menu for package buttons. 
	 */
	public Menu createPackageMenu(final PackageButton b)
	{
		Menu popupMenu = new Menu(b);
		MenuItem renameClassItem = new MenuItem(popupMenu, SWT.CASCADE);
		renameClassItem.setText("Rename Package");
		renameClassItem.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		MenuItem deleteClassItem = new MenuItem(popupMenu, SWT.CASCADE);
		deleteClassItem.setText("Delete Package");
		deleteClassItem.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ObjectBenchUtility.deleteFolder(JavaModelHelper.getPackageFolderFile(b.packageValue));//ObjectBenchUtility.DeleteFile(JavaModelHelper.getClassFile(bn.packageName, bn.className));
					ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE,null);
					fullRefresh();
					refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		return popupMenu;
	}
	
	/*
	 * Create Package Buttons - Creates the buttons to represent packages
	 */
	public void createPackageButtons(Composite parent) throws MalformedURLException, Exception
	{
		if(!JavaModelHelper.isProjectOpen(activeProjectName))
		{
			disposeButtons(false);
			return;
		}
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
					packageButtons.get(i).setMenu(createPackageMenu(packageButtons.get(i)));
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
						buttonData.top = new FormAttachment(15);
					}
					packageButtons.get(i).addMouseListener(new MouseListener(){

						@Override
						public void mouseDoubleClick(MouseEvent e) {
							try {
								viewClasses(packages[currentNumber]);
							} catch (Exception e1) {
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
			e.printStackTrace();
		} 
		parent.layout();
		parent.getShell().layout();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {ObjectBenchPerspective.hideEditor();}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
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
	@Override
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
	 * Full Refresh - Performs a full refresh of the screen, good for checking state changes. 
	 */
	public void fullRefresh() throws MalformedURLException, Exception
	{
		if(!JavaModelHelper.isProjectOpen(activeProjectName))
		{
			disposeButtons(false);
		}
		if(state == 0)
		{
			viewPackages();
			displayPackageView(mainViewArea);	
		}
		if(state == 1)
		{
			displayClassView(mainViewArea);
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
		disposeButtons(false);
	}
	
	/*
	 * Dispose Buttons - Disposes the buttons as needed. UPDATE - Now takes a boolean field to skip clearing package buttons. 
	 */
	private void disposeButtons(Boolean skip)
	{
		if(!skip)
		{
			for(PackageButton button:packageButtons)
			{
				button.dispose();
			}
			packageButtons = new ArrayList<PackageButton>();
		}
		for(ClassButton button:classButtons)
		{
			button.dispose();
		}
			classButtons = new ArrayList<ClassButton>();
			if(upButton != null)
			{
					if(!upButton.isDisposed())
					{
						upButton.dispose();
					}
			}
			if(newClassButton != null)
			{
				if(!newClassButton.isDisposed())
				{
					newClassButton.dispose();
				}
			}
			if(newPackageButton != null)
			{
				if(!newPackageButton.isDisposed())
				{
					newPackageButton.dispose();
				}
			}
	}
	
	private void disposeAll()
	{
	    for (Control control : mainViewArea.getChildren()) {
	        control.dispose();
	    }
	}
	
}

