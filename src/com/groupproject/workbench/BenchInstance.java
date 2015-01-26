package com.groupproject.workbench;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import com.groupproject.workbench.helpers.StringHelper;

public class BenchInstance {
	
	public String className;
	public String packageName; 
	
	public Class<?> myClass; 
	public Object myInstance; 
	
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
	
	public void callMethod(String m)
	{
		
	}
	
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
	
	public void setValue(String fieldName, Object value) throws Exception
	{
		Field field = myClass.getDeclaredField(fieldName); 
		field.setAccessible(true);
		field.set(myInstance, value);
	}
	
	void Instantiate() throws InstantiationException, IllegalAccessException
	{
		myInstance = myClass.newInstance();
	}
	
	public void setObject(Object o)
	{
		myInstance = o; 
	}

}
