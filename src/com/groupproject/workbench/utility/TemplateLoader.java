package com.groupproject.workbench.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import com.groupproject.workbench.Template;

/*
 * Template Loader - This class is used to load templates. 
 */
public class TemplateLoader {
	
	/*
	 * Get Template - Returns a template that can be used to create a class. 
	 */
	public static Template getTemplate(int type) throws IOException
	{
		String content = ""; 
		switch(type)
		{
		case 0:
			content = getFile("SampleTemplate.sample");
			return new Template(content,type);
		case 1:
			content = getFile("AbstractTemplate.sample");
			return new Template(content,type);
		case 2:
			content = getFile("InterfaceTemplate.sample");
			return new Template(content,type);
		case 3:
			content = getFile("AppletTemplate.sample");
			return new Template(content,type);
		case 4: 
			content = getFile("UnitTestTemplate.sample");
			return new Template(content,type);
		case 5: 
			content = getFile("EnumTemplate.sample");
			return new Template(content,type);
		}

		return null;
	}

	/*
	 * Get File - Returns a string representation of a given file. 
	 */
	private static String getFile(String fileName) {
		  
			StringBuilder result = new StringBuilder("");
			
			//Get file from resources folder
			URL url;
			try{
				url = new URL("platform:/plugin/com.groupproject.workbench/resources/" + fileName);
				InputStream inputStream = url.openConnection().getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
				String inputLine; 
				
				while((inputLine = in.readLine()) != null)
				{
					result.append(inputLine).append("\n");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return result.toString();
		 
		  }




}