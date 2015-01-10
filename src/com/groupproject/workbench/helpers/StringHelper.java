package com.groupproject.workbench.helpers;

public class StringHelper {

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
		
		return s;
	}
	
}
