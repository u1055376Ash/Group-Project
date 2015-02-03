package com.groupproject.workbench.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;

import com.groupproject.workbench.JavaModelHelper;
import com.groupproject.workbench.buttons.ObjectBenchButton;
import com.groupproject.workbench.dialogs.MethodDialog;
import com.groupproject.workbench.helpers.StringHelper;
import com.groupproject.workbench.utility.ObjectBenchUtility;

/*
 * Object Bench View - This is the view for the object bench. This class controls all instances of user classes and stores them in ObjectBenchButton.
 */
public class ObjectBenchView extends ViewPart {

	private List<ObjectBenchButton> objectBenchButtons; 
	private Composite mainViewArea;
	
	/*
	 * Default Constructor
	 */
	public ObjectBenchView() {	}

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
		ObjectBenchUtility.registerObjectBench(this); //Register this with the ObjectBenchUtility so other classes can access this instance
	}

	@Override
	public void setFocus() {}

	/*
	 * Add Object - This method adds an instance to the object bench. The object is created from its name, package and an object representing the instance. 
	 */
	public void addObject(String className, String packageName, Object instance) throws JavaModelException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException
	{
		//System.out.println("Instantiating: " + className); //Uncomment to debug
		if(objectBenchButtons == null)
		{
			objectBenchButtons = new ArrayList<ObjectBenchButton>(); 
		}
		String entryString = className;
		entryString = entryString.substring(0,entryString.lastIndexOf('.')); //strip extension TODO - use the strip extension found in StringHelper class
		entryString += " (Instance) ";
		//Create an ObjectBenchButton to represent instance. 
		ObjectBenchButton newButton = new ObjectBenchButton(mainViewArea,SWT.NONE,className,objectBenchButtons.size(), packageName, instance);
		newButton.setText(entryString);
		objectBenchButtons.add(newButton);
		//Position button object. 
		FormData buttonData = new FormData(90+(entryString.length() * 3),80);
		final int i = objectBenchButtons.size()-1; //dirty hack for button listener
		if(i == 0)
		{
			//System.out.println("First");
			buttonData.left = new FormAttachment(2);
			buttonData.top = new FormAttachment(2);
		}
		else
		{
			if(i > 0)
			{
				//System.out.println("Another");
				buttonData.left = new FormAttachment(objectBenchButtons.get(i-1),15,SWT.RIGHT);
				buttonData.bottom = new FormAttachment(objectBenchButtons.get(i-1),0,SWT.BOTTOM);
				buttonData.top = new FormAttachment(objectBenchButtons.get(i-1),0,SWT.TOP);
			}
		}
		objectBenchButtons.get(i).setLayoutData(buttonData);
		//Set up the listener for the buttons. 
		objectBenchButtons.get(i).addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {}

			@Override
			public void mouseDown(MouseEvent e) {}
			@Override
			public void mouseUp(MouseEvent e) {
				try {
					ObjectBenchUtility.setActiveInstance(objectBenchButtons.get(i).getInstance());
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
			
			}

		});
		
		objectBenchButtons.get(i).setMenu(buildMenuForClass(objectBenchButtons.get(i)));
		
		//TODO - in final build remove this code.
//		Class<?> myClass = ObjectBenchButtons.get(i).getMyClass();
//
//		if(parameters.length == 0)
//		{
//			//ObjectBenchButtons.get(i).setInstance(myClass.newInstance());
//		}
//		else
//		{
//			Constructor<?> myConstructor = myClass.getConstructor(parameters);
//			//ObjectBenchButtons.get(i).setInstance((Object)myConstructor.newInstance());//do fuck all for now.
//		}

		
		mainViewArea.layout();
		mainViewArea.getShell().open();
	}
	
	/*
	 * Build Menu For Class - This method builds the right-click menu for an instance. 
	 * This method reads the methods of a class. 
	 * TODO - This method still needs to ascertain the parameters of a method and implement a means to call the method on click. 
	 */
	private Menu buildMenuForClass(final ObjectBenchButton bn) throws JavaModelException
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
						//TODO - Invoke method, if there are parameters display a dialog to take parameters then run the method. 
						Class<?>[] parameters = ObjectBenchUtility.getParameterTypes(methodTypes[index]);
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
										MessageDialog msg = new MessageDialog(mainViewArea.getShell(), "Return Value", Window.getDefaultImage(), 
												"Return Value: " + bn.getInstance().callMethod(method,objects).toString() + " (" + StringHelper.fixType(returnTypes[index]) + ") ", 
												MessageDialog.INFORMATION, new String[] {"OK"}, 0);
										if(msg.open() == Window.OK)
										{
											//System.out.println("IM IN");
											//ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName, object);
										}
									}
									//System.out.println("IM IN");
									//ObjectBenchUtility.getObjectBench().addObject(selectedClass,activePackageName, object);
								}
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
						}
						else
						{
							bn.getInstance().callMethod(methodNames[index]);
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
	void removeObject(ObjectBenchButton bn) throws JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		objectBenchButtons.remove(bn);
		bn.dispose();
		ObjectBenchUtility.setActiveInstance(null);
		mainViewArea.layout();
	}
}
