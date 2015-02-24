package com.groupproject.workbench.helpers;

/*
 * String Helper - A collection of methods to assist in building strings used throughout the plugin. 
 */
public final class StringHelper {

	/*
	 * Fix Type - this returns a string that represents an objects type in an easier to read manner. 
	 */
	public static String fixType(String s)
	{
		s = s.replace(";", "");
		if(s.startsWith("Q"))
		{
			s = s.substring(1);
		}
		
		if(s.equals("I"))
		{
			s = "Integer";
		}
		if(s.equals("D"))
		{
			s = "Double";
		}
		if(s.equals("F"))
		{
			s = "Float";
		}
		if(s.equals("B"))
		{
			s = "Byte";
		}
		if(s.equals("S"))
		{
			s = "Short";
		}
		if(s.equals("J"))
		{
			s = "Long";
		}
		if(s.equals("D"))
		{
			s = "Double";
		}
		if(s.equals("Z"))
		{
			s = "Boolean";
		}
		if(s.equals("C"))
		{
			s = "Char";
		}
		if(s.equals("V"))
		{
			s = "void";
		}
		
		return s;
	}
	
	/*
	 * Strip Extension - Strips the extension from a class name. 
	 */
	public static String stripExtension(String s)
	{
		return s.substring(0,s.lastIndexOf('.'));
	}
	
	/*
	 * Get Qualified Name - Returns a full, qualified name of a class based on its class and package name.
	 */
	public static String getQualifiedName(String className,String packageName)
	{
		if(packageName.equals(""))
		{
			return stripExtension(className);
		}
		if(className.isEmpty() || packageName.isEmpty() || className == null || packageName == null || className.length() <= 0 || packageName.length() <= 0)
		{
			//System.out.println(className + ":::" + packageName);
			return "";
		}
		if(className.contains("."))
		{
			String cleanName = className.substring(0, className.lastIndexOf('.'));
			String qualifiedName = packageName + "." + cleanName;
			return qualifiedName;
		}
		return packageName + "."  + className; 

	}
}
