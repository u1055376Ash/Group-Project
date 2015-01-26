package com.groupproject.workbench;

import java.lang.reflect.Field;
import com.groupproject.workbench.helpers.StringHelper;
/*
 * The class that controls an instance of a users class.
 * Contains methods to access fields of an instantiated class and call methods related to the class. 
 * 
 */
public class BenchInstance {
	
	public String className;									//The name of the class that the instance belongs to.
	public String packageName; 									//The package the class can be found in. 
	
	public Class<?> myClass; 									//Class variable
	public Object myInstance; 									//Reference to the created Object instance.
	
	/*
	 * Constructors 
	 */
	public BenchInstance(String c, String p) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		className = c;
		packageName = p; 
		myClass = JavaModelHelper.getClassFromLoader(StringHelper.getQualifiedName(c, p));
		Instantiate();
	}
	
	public BenchInstance(String c, String p, Object o) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		className = c;
		packageName = p; 
		myClass = JavaModelHelper.getClassFromLoader(StringHelper.getQualifiedName(c, p));
		myInstance = o; 
	}
	
	public BenchInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		this("DefaultClass","DefaultPackage");
	}
	
	
	/*
	 * Methods
	 * 
	 */
	
	/*
	 * Call Method - Calls a method that should be contained in the object instance. 
	 */
	public void callMethod(String m)
	{
		//This method needs to be implemented to call an instances method based on name and parameters. 
	}
	
	
	/*
	 * Get Value - Gets the value of a requested field and returns it as a String. 
	 */
	public String getValue(String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field field = myClass.getDeclaredField(fieldName);
		field.setAccessible(true);
		Object obj = field.get(myInstance);//.toString();
		String value = "null";
		if(obj != null)
		{
			value = obj.toString();
		}
		return value;
	}
	
	/*
	 * Set Value - Allows a user to set a value contained in an instance.
	 */
	public void setValue(String fieldName, Object value) throws Exception
	{
		Field field = myClass.getDeclaredField(fieldName); 
		field.setAccessible(true);
		field.set(myInstance, value);
	}
	
	/*
	 * Instantiate - Instantiates the instance, provided there is a zero-parameter constructor. 
	 */
	void Instantiate() throws InstantiationException, IllegalAccessException
	{
		myInstance = myClass.newInstance();
		//TODO add error checking and ensure that this method can't be called on a class with no blank Constructor.
	}
	
	/*
	 * Set Object - Sets the actual instance. This method is an override for setting an instance. 
	 */
	public void setObject(Object o)
	{
		myInstance = o; 
	}

}
