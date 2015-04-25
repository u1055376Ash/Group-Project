package com.groupproject.workbench;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.eclipse.jdt.core.JavaModelException;

import com.groupproject.workbench.utility.JavaModelHelper;
import com.groupproject.workbench.utility.ObjectBenchUtility;
import com.groupproject.workbench.utility.StringHelper;
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
	
	boolean markForDeletion;
	/*
	 * Constructors 
	 */
	public BenchInstance(String c, String p) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		className = c;
		packageName = p; 
		update();
		//myClass = JavaModelHelper.getClassFromLoader(StringHelper.getQualifiedName(c, p));
		Instantiate();
	}
	
	public BenchInstance(String c, String p, Object o) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		className = c;
		packageName = p; 
		update();
		//myClass = JavaModelHelper.getClassFromLoader(StringHelper.getQualifiedName(c, p));
		myInstance = o; 
	}
	
	public BenchInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException
	{
		this("DefaultClass","DefaultPackage");
	}
	
	
	/*
	 * Methods
	 * 
	 */
	
	
	public Method getMethod(String name, Class<?>[] parameterTypes) throws NoSuchMethodException, SecurityException
	{
		return myClass.getDeclaredMethod(name, parameterTypes); 
	}
	/*
	 * Call Method - Calls a method that should be contained in the object instance. 
	 */
	public Object callMethod(String s)
	{
		return callMethod(s,null);
	}
	
	public Object callMethod(String s, Object[] params)
	{
		Method[] allMethods = myClass.getDeclaredMethods();
		for(Method m: allMethods)
		{
			System.out.print(m.getName()+":"+s);
			
			if(m.getName().equals(s))
			{
				System.out.println("Method Found");
				//Type[] pType = m.getGenericParameterTypes();
				try
				{
					m.setAccessible(true);
					if(m.getReturnType() == void.class)
					{
						m.invoke(myInstance, (Object[])null);
						return null; 
					}
					else
					{
						Object o = m.invoke(myInstance, (Object[])null);
						return o;
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		//This method needs to be implemented to call an instances method based on name and parameters. 
		return null;

	}
	
	public Object callMethod(Method m, Object[] params)
	{
		try
		{
			m.setAccessible(true);
			if(m.getReturnType() == void.class)
			{
				m.invoke(myInstance, params);
				return null; 
			}
			else
			{
				Object o = m.invoke(myInstance, params);
				return o;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*
	 * Get Value - Gets the value of a requested field and returns it as a String. 
	 */
	public String getValue(String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ArrayIndexOutOfBoundsException, JavaModelException, NoSuchMethodException, ClassNotFoundException, MalformedURLException
	{
		if(myInstance != null)
		{
			Field field = getMyField(fieldName);
			if(field == null)
			{
				return null; 
			}
			field.setAccessible(true);
			Object obj = field.get(myInstance);//.toString();
			String value = "null";
			if(obj != null)
			{
				value = obj.toString();
			}
			return value;
		}
		else
		{
			return null;
		}

	}
	
	public Class<?> getFieldClass(String fieldName) throws NoSuchFieldException, SecurityException, ClassNotFoundException, MalformedURLException, ArrayIndexOutOfBoundsException, JavaModelException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException
	{
		if(myInstance != null)
		{
			update();
			Field field = getMyField(fieldName);
			if(field == null)
			{
				return null;
			}
			field.setAccessible(true);
			return field.getType();
		}
		return null;
	}
	
	Field getMyField(String name) throws ArrayIndexOutOfBoundsException, JavaModelException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, MalformedURLException
	{
		Field field;
		try {
			field = myClass.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			ObjectBenchUtility.getObjectBench().removeInstance(this);
			return null;
		}
	}
	
	/*
	 * Get Field - This method gets the object associated with a field. 
	 */
	public Object getField(String fieldName) throws SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, MalformedURLException, NoSuchFieldException, ArrayIndexOutOfBoundsException, JavaModelException, NoSuchMethodException
	{
		if(myInstance != null)
		{
			update();
			Field field = getMyField(fieldName);
			if(field == null)
			{
				return null; 
			}
			Object obj = field.get(myInstance);
			return obj;

		}
		return null;
	}
	
	/*
	 * Set Value - Allows a user to set a value contained in an instance.
	 */
	public void setValue(String fieldName, Object value) throws Exception
	{
		Field field = getMyField(fieldName);
		field.setAccessible(true);
		field.set(myInstance, value);
	}
	
	/*
	 * Set Value - Set a value of an instance by passing the original object with one to replace it. 
	 */
	public void setValue(Object o, Object v) throws IllegalArgumentException, IllegalAccessException
	{
		for(Field f:myClass.getDeclaredFields())
		{
			f.setAccessible(true);
			if(f.get(myInstance) != null)
			{
				if(f.get(myInstance).equals(o))
				{
					f.set(myInstance, v);
				}
				
			}

		}
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
	
	 void update() throws ClassNotFoundException, MalformedURLException
	{
		 if(myClass == null)
		 {
			 myClass = JavaModelHelper.getClassFromLoader(StringHelper.getQualifiedName(className, packageName));
			 return;
		 }
		 if(myClass.equals(JavaModelHelper.getClassFromLoader(StringHelper.getQualifiedName(className, packageName))))
		 {
			 myClass = JavaModelHelper.getClassFromLoader(StringHelper.getQualifiedName(className, packageName));
			 markForDeletion = false; 
		 }
		 else
		 {
			 markForDeletion = true;
		 }

	}
	 
	 public boolean checkDelete() throws ClassNotFoundException, MalformedURLException
	 {
		 update();
		 return markForDeletion;
	 }
}
