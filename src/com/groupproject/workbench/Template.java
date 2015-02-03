package com.groupproject.workbench;

/*
 * Template - A wrapper for class templates when creating a new class. 
 */
public class Template {
	
	String body; 				//The source file of the template
	int type; 					//The integer number type of the template
	
	/*
	 * Constructors
	 */
	
	public Template(String b)
	{
		body = b; 
	}
	
	public Template(String string, int type2) {
		this(string);
		type = type2;
	}

	
	/*
	 * Methods
	 */
	
	/*
	 * Get Body - returns the body of the template
	 */
	public String getBody()
	{
		return body; 
	}
	
	/*
	 * Set Name - Sets the name in a template. A template should contain a class name written as CLASSNAME and a package name written as PACKAGENAME
	 */
	public void setName(String newName, String packageName)
	{
		if(packageName.equals(""))
		{
			body = body.replaceAll("package", ""); //This accomodates for creating a class in the default package. 
		}
		body = body.replaceAll("PACKAGENAME", packageName);
		body = body.replaceAll("CLASSNAME", newName);
//		String[] strings = body.split("public class");
//
//		strings[0] += "public class ";
//		strings[0] += " " + newName ;
//		strings[0] += strings[1];
//		body = strings[0];
	}
	
	/*
	 * Set Type - Sets the type of template. 
	 */
	public void setType(int i)
	{
		type = i; 
	}
	
	/*
	 * Get Type - Returns the type of the template. 
	 */
	public int getType()
	{
		return type;
	}

}
