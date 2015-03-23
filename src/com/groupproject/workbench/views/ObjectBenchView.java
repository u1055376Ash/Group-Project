package com.groupproject.workbench.views;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;

import com.groupproject.workbench.BenchInstance;
import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.buttons.ObjectBenchButton;
import com.groupproject.workbench.dialogs.MethodDialog;
import com.groupproject.workbench.utility.ObjectBenchUtility;
import com.groupproject.workbench.utility.StringHelper;

/*
 * Object Bench View - This is the view for the object bench. This class controls all instances of user classes and stores them in ObjectBenchButton.
 */
public class ObjectBenchView extends ViewPart  {

	private List<ObjectBenchButton> objectBenchButtons; 					//The list of buttons on the object bench.
	private Composite mainViewArea;											//A reference to the main view area.
	private Action deleteAction; 											//An action to clear the object bench.
	/*
	 * Default Constructor
	 */
	public ObjectBenchView() {}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		mainViewArea = new Composite(sc, SWT.NONE);
		//mainViewArea = new Composite(sc, SWT.NONE);
		mainViewArea.setLayout(new FormLayout());
		mainViewArea.setSize(600,100);
		mainViewArea.setMenu(buildContextMenu());
		sc.setContent(mainViewArea);
		sc.setExpandHorizontal(true);
		sc.setMinSize(mainViewArea.computeSize(1000, 100));
		sc.setMinHeight(110);
		ObjectBenchUtility.registerObjectBench(this); //Register this with the ObjectBenchUtility so other classes can access this instance
		createActions();
		createToolbar();
		
	}
	
	/*
	 * Create Actions - Creates actions that can be assigned to a variety of controls. 
	 */
	void createActions()
	{
		deleteAction = new Action("Clear Bench"){
			public void run(){
				clearBench();
			}
		};
		deleteAction.setImageDescriptor(ObjectBenchUtility.getImageDescriptor("delete.png"));
	}
	
	/*
	 * Create Toolbar - Creates the toolbar buttons. 
	 */
	void createToolbar()
	{
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(deleteAction);
	}
	/*
	 * Build Context Menu - Builds the context menu for the class view. 
	 */
	Menu buildContextMenu()
	{
		
		Menu menu = new Menu(mainViewArea);
		MenuItem clearItem = new MenuItem(menu, SWT.CASCADE);
		clearItem.setText("Clear Object Bench");

		
		clearItem.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {clearBench();}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		
		return menu; 
	}
	
	/*
	 * Clear Bench - Clears the Object Bench of Instances
	 */
	public void clearBench()
	{
		if(objectBenchButtons != null)
		{
			for(ObjectBenchButton b:objectBenchButtons){
				try {
					removeObject(b,false);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			objectBenchButtons.removeAll(objectBenchButtons);
			objectBenchButtons = new ArrayList<ObjectBenchButton>();
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {}

	/*
	 * Layout Buttons - Layout the buttons on the bench
	 */
	public void layoutButtons()
	{
		for(int i = 0; i<objectBenchButtons.size();i++)
		{
			if(objectBenchButtons.get(i).isDisposed())
			{
				continue;
			}
			FormData buttonData = new FormData(90+(objectBenchButtons.get(i).getText().length() *3),80);
			if(i == 0)
			{
				buttonData.left = new FormAttachment(2);
				buttonData.top = new FormAttachment(2);
			}
			else
			{
				if(i > 0)
				{
					buttonData.left = new FormAttachment(objectBenchButtons.get(i-1),15,SWT.RIGHT);
					buttonData.bottom = new FormAttachment(objectBenchButtons.get(i-1),0,SWT.BOTTOM);
					buttonData.top = new FormAttachment(objectBenchButtons.get(i-1),0,SWT.TOP);
				}
			}
			objectBenchButtons.get(i).setLayoutData(buttonData);
		}
		mainViewArea.layout();
		mainViewArea.getShell().open();
	}
	/*
	 * Add Object - This method adds an instance to the object bench. The object is created from its name, package and an object representing the instance. 
	 */
	public void addObject(String className, String packageName, Object instance) throws Exception
	{
		//System.out.println("Instantiating: " + className); //Uncomment to debug
		if(objectBenchButtons == null)
		{
			objectBenchButtons = new ArrayList<ObjectBenchButton>(); 
		}
		String entryString = className;
		entryString = StringHelper.stripExtension(entryString);
		//entryString = entryString.substring(0,entryString.lastIndexOf('.')); 
		entryString += " (Instance) ";
		//Create an ObjectBenchButton to represent instance. 
		final ObjectBenchButton newButton = new ObjectBenchButton(mainViewArea,SWT.NONE,className,objectBenchButtons.size(), packageName, instance);
		newButton.setText(entryString);
		//Set up the listener for the buttons. 
		newButton.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {}

			@Override
			public void mouseDown(MouseEvent e) {}
			@Override
			public void mouseUp(MouseEvent e) {
				try {
					clearSelection(false);
					ObjectBenchUtility.setActiveInstance(newButton.getInstance());
					newButton.setSelected(true);
					//clearSelection();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
			}
		});
		newButton.setMenu(buildMenuForClass(newButton));
		objectBenchButtons.add(newButton);
		layoutButtons();
	}
	
	/*
	 * Clear Selection - Clears the current selection. 
	 */
	public void clearSelection(boolean clearingAll)
	{
		for(ObjectBenchButton b:objectBenchButtons)
		{
			b.setSelected(false);
		}
		if(!clearingAll)
		{
			layoutButtons();
		}

	}
	
	/*
	 * Build Menu For Class - This method builds the right-click menu for an instance. 
	 * This method reads the methods of a class. 
	 */
	private Menu buildMenuForClass(final ObjectBenchButton bn) throws JavaModelException, Exception
	{
		Menu popupMenu = new Menu(bn);
		if(JavaModelHelper.getClassMethodNames(bn.packageName, bn.className) != null)
		{
			String[] methods = JavaModelHelper.getMethodSignatures(bn.packageName, bn.className);
			final String[] methodNames = JavaModelHelper.getClassMethodNames(bn.packageName, bn.className);
			final String[] returnTypes = JavaModelHelper.getClassMethodReturnTypes(bn.packageName, bn.className);
			final String[][] methodTypes = JavaModelHelper.getClassMethodParameterTypes(bn.packageName, bn.className);
			for(int i = 0; i < methods.length; i++)
			{
				final int index = i; //nasty hack to fix minor issue. 
				MenuItem methodItem = new MenuItem(popupMenu, SWT.NONE); 
				methodItem.setText(methods[i]);
				methodItem.addSelectionListener(new SelectionListener(){

					@Override
					public void widgetSelected(SelectionEvent e) {
						Class<?>[] parameters = null;
						try {
							ObjectBenchUtility.setActivePackage(bn.packageName);
							parameters = ObjectBenchUtility.getParameterTypes(methodTypes[index]);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						
						if(parameters.length > 0)
						{
							try {
								Method method = bn.getInstance().getMethod(methodNames[index],parameters);
								MethodDialog dialog = new MethodDialog(mainViewArea.getShell(), method);
								if(dialog.open() == Window.OK)
								{
									if(dialog.getReturnCode() != Window.CANCEL)
									{
										Object[] objects = dialog.getParameters(); 
										Object o = (o = bn.getInstance().callMethod(method,objects)) != null ? o:null;
										String str = o!= null ? o.toString():"null";
										//System.out.println(returnTypes[index]);
										if(!returnTypes[index].equals("V"))
										{
											MessageDialog msg = new MessageDialog(mainViewArea.getShell(), "Return Value", Window.getDefaultImage(), 
													"Return Value: " + str + " (" + StringHelper.fixType(returnTypes[index]) + ") ", 
													MessageDialog.INFORMATION, new String[] {"OK"}, 0);
											if(msg.open() == Window.OK)
											{
												//System.out.println("IM IN");
												//ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName, object);
											}
										}

									}
									//System.out.println("IM IN");
									//ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName, object);
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							} 
						}
						else
						{
							Object s = (s = bn.getInstance().callMethod(methodNames[index]))!= null ?  s:null;
							String str = s != null ? s.toString():"null";
							MessageDialog msg = new MessageDialog(mainViewArea.getShell(), "Return Value", Window.getDefaultImage(), 
									"Return Value: " + str + " (" + StringHelper.fixType(returnTypes[index]) + ") ", 
									MessageDialog.INFORMATION, new String[] {"OK"}, 0);
							if(msg.open() == Window.OK)
							{
								//System.out.println("IM IN");
								//ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName, object);
							}
						}
					
					try {
						ObjectBenchUtility.setActiveInstance(bn.getInstance());
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
						
					
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
					
				});
			}
		
		}
		new MenuItem(popupMenu, SWT.SEPARATOR);
		MenuItem removeItem = new MenuItem(popupMenu, SWT.NONE);
		removeItem.setText("Remove");
		removeItem.addSelectionListener(new SelectionListener(){

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					removeObject(bn);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		return popupMenu;
	}
	
	/*
	 * Remove Object - Removes an object from the object bench. 
	 */
	void removeObject(ObjectBenchButton bn) throws JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, NoSuchMethodException, ClassNotFoundException, MalformedURLException
	{
		objectBenchButtons.remove(bn);
		bn.dispose();
		ObjectBenchUtility.setActiveInstance(null);
		layoutButtons();
	}
	
	/*
	 * Remove Object(Dirty) - This removes an object from the bench without removing it from the collection.
	 */
	void removeObject(final ObjectBenchButton bn, boolean quick) throws ArrayIndexOutOfBoundsException, JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, MalformedURLException
	{
		if(quick)
		{
			removeObject(bn);
		}
		else
		{
			Display display = bn.getDisplay(); 
		    display.asyncExec(new Runnable() {
		        @Override
		        public void run() {
		          if (!bn.isDisposed()) {
		        	  bn.dispose();

		          }
		        }
		      });
			ObjectBenchUtility.setActiveInstance(null);
			mainViewArea.layout();
		}
		
	}
	/*
	 * Remove Instance - Removes an instance from the object bench.
	 */
	public void removeInstance(BenchInstance b) throws ArrayIndexOutOfBoundsException, JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, MalformedURLException
	{
		ObjectBenchButton button = null; 
		for(ObjectBenchButton o:objectBenchButtons)
		{
			if(o.getInstance().equals(b))
			{
				button = o;
			}
		}
		if(button != null)
		{
			removeObject(button);
		}
	}
	
	/*
	 * Get Instances of Type - Gets the instances of a given type.
	 */
	public BenchInstance[] getInstancesOfType(String t)
	{
		//System.out.println("Looking For: " + t);
		if(objectBenchButtons == null)
		{
			return null; 
		}
		List<BenchInstance> instances = new ArrayList<BenchInstance>();
		for(ObjectBenchButton b:objectBenchButtons)
		{
			//System.out.println("MyClassName: " + b.className);
			if(StringHelper.stripExtension(b.className).equals(t))
			{
				instances.add(b.getInstance());
			}
		}
		return instances.toArray(new BenchInstance[instances.size()]);
	}
	
	/*
	 * Get Instances of Type - Gets the instances of a given type.
	 */	
	public BenchInstance[] getInstancesOfType(Class<?> c)
	{
		return getInstancesOfType(c.getName());
	}
	
	/*
	 * Replace Instance - Replaces an instance on the object bench. 
	 */
	public void replaceInstance(Object old, Object theNew)
	{
		for(ObjectBenchButton b:objectBenchButtons)
		{
			if(b.getInstance().myInstance.equals(old))
			{
				b.getInstance().setObject(theNew);
			}
		}
	}

//	@Override
//	      public void resourceChanged(IResourceChangeEvent event) {
//	          IResource res = event.getResource();
//	          switch (event.getType()) {
//	             case IResourceChangeEvent.PRE_CLOSE:
//
//	                break;
//	             case IResourceChangeEvent.PRE_DELETE:
//
//	                break;
//	             case IResourceChangeEvent.POST_CHANGE:
//
//	                break;
//	             case IResourceChangeEvent.PRE_BUILD:
//	            	 
//	                 break;
//	             case IResourceChangeEvent.POST_BUILD:
//	            	 for(ObjectBenchButton b:objectBenchButtons)
//	            	 {
//	            		 try {
//							if(b.getInstance().checkDelete())
//							 {
//								 removeObject(b);
//							 }
//						} catch (Exception e) {
//							e.printStackTrace();
//						} 
//	            	 }
//	            	 break;
//	          }
//	       }
		
	
}
