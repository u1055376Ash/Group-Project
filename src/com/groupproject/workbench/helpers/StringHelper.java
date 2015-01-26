package com.groupproject.workbench.helpers;

public final class StringHelper {

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
		
		return s;
	}
	
	
	public static String stripExtension(String s)
	{
		return s.substring(0,s.lastIndexOf('.'));
	}
	
	public static String getQualifiedName(String className,String packageName)
	{
		if(className.isEmpty() || packageName.isEmpty() || className == null || packageName == null || className.length() <= 0 || packageName.length() <= 0)
		{
			return "";
		}
		if(className.contains("."))
		{
			String cleanName = className.substring(0, className.lastIndexOf('.'));
			String qualifiedName = packageName + "." + cleanName;
			return qualifiedName;
		}
		return ""; 

	}
}
