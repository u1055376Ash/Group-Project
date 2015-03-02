package com.groupproject.workbench;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;

import com.groupproject.workbench.helpers.StringHelper;
import com.groupproject.workbench.helpers.TemplateLoader;

/*
 * JavaModelHelper - This class contains methods to gather information on user defined classes. This class can traverse the Eclipse AST and gather
 * information as required. This class is to be used exclusively for gathering information. 
 */
public final class JavaModelHelper {	
	
	private static IWorkspace workspace;						//The active Eclipse Workspace
	private static IWorkspaceRoot root; 						//The root of the Eclipse Workspace
	private static IProject[] projects; 						//A collection of projects
	
	private static IProject activeProject;						//The active project
//	private static IPackageFragment activePackage;				//The active package
	private static URLClassLoader classLoader;					//The classloader used to load classes
	private static List<URL> myUrls; 							//A list of locations of classes loaded by the class loader
	
	/*
	 * Constructor
	 */
	private JavaModelHelper() throws MalformedURLException, Exception
	{
		Initialise();
	}
	
	/*
	 * Methods 
	 */
	
	/*
	 * Initialise - Initialises the JavaModelHelper
	 */
	public static void Initialise() throws MalformedURLException, Exception
	{
		myUrls = new ArrayList<URL>();
		workspace = ResourcesPlugin.getWorkspace(); 
		root = workspace.getRoot();
		projects = root.getProjects();
		if(projects[0] != null && activeProject == null)
		{
			activeProject = projects[0];
		}
		addToClassPath();
		//myUrls = 
	}
	
	/*
	 * Build Project - Makes a project rebuild.
	 */
	public static void buildProject(String projectName) throws CoreException
	{
		IProject p = root.getProject(projectName);
		IProgressMonitor pm = null;
		p.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, pm);
	}
	
	public static void buildActiveProject() throws CoreException
	{

		IProgressMonitor pm = null;
		activeProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, pm);
	}
	
	/*
	 * Get Project by Name - Returns a project by its name. 
	 */
	public static IProject getProjectByName(String name)  throws JavaModelException
	{
		return root.getProject(name);
	}
	
	/*
	 * New Class - Creates a new class
	 *TODO - Extend this method to include different class types from template.
	 */
	public static void newClass(String className, int classType, String myPackage) throws IOException, JavaModelException
	{
		Template t = TemplateLoader.getTemplate(classType); 
		t.setName(className, myPackage);
		className += ".java";
		IPackageFragment p = getPackage(myPackage);
		if(p == null)
		{
			System.out.println("No Package Found");
			return;
		}
		p.createCompilationUnit(className, t.getBody(), true, null);
		p.save(null, true);
	}
	
	/*
	 * New Package - Creates a new package. 
	 */
	public static Boolean newPackage(String packageName) throws CoreException
	{
		if(packageName.contains(".package"))
		{
			return false; 
		}
		IFolder sourceFolder = activeProject.getFolder("src");
		//sourceFolder.create(false, true, null);
		IJavaProject jProject = getJavaProject(activeProject);
		jProject.getPackageFragmentRoot(sourceFolder).createPackageFragment(packageName, true, null);
		return true; 
	}
	
	/*
	 * Add to Class Path - Adds a users class to the plugins class path. 
	 */
	static void addToClassPath() throws MalformedURLException, Exception
	{
		
		for(IProject p:projects)
		{
			if(p.isOpen())
			{
				for(IResource r:p.members())
				{
					addURL(r.getLocation().toFile().toURI().toURL());
				}
			}

		}
	}
	
	/*
	 * Add To Class Path - Adds a users class to the plugins class path from qualified class name. 
	 */
	public static void addToClassPath(String name, String packageName) throws MalformedURLException, JavaModelException, Exception
	{
		addURL(getClassFile(packageName,name).toURI().toURL());
	}
	
	
	/*
	 * Get Classes - Gets the classes from a given project. 
	 */
	public static ICompilationUnit[] getClasses(IProject project)  throws JavaModelException
	{
		//TODO Implement this method, can use the other version for ease. 
		return null; 
	}
	
	/*
	 * Get Classes - Returns the classes in a package. 
	 */
	public static ICompilationUnit[] getClasses(IPackageFragment myPackage) throws JavaModelException
	{
		return myPackage.getCompilationUnits();
		
	}
	
	
	/*
	 * Get Class IFile - This method gets the actual reference to the file used for a class. 
	 */
	private static IFile getClassIFile(String myPackage, String myClassName) throws JavaModelException
	{
		ICompilationUnit myClass = getClass(myPackage,myClassName);
		IResource resource = myClass.getUnderlyingResource();
		if(resource.getType() == IResource.FILE)
		{
			IFile ifile = (IFile)resource;
			return ifile;
		}
		
		return null; 
	}
	

	/*
	 * Get Class File - This method returns a classes source file. 
	 */
	public static File getClassFile(String myPackage, String myClassName) throws JavaModelException
	{
		IFile iFile = getClassIFile(myPackage,myClassName);
		return iFile.getRawLocation().makeAbsolute().toFile();
	}
	
	/*
	 * Get Class File Path - Returns the absolute path of a given classes file. 
	 */
	public static String getClassFilePath(String myPackage, String myClassName) throws JavaModelException
	{
		IFile myFile = getClassIFile(myPackage,myClassName);
		return myFile.getRawLocation().toString();
	}
	
	/*
	 * Get Package Folder File - Returns a file reference of a package folder. 
	 */
	public static File getPackageFolderFile(String packageName) throws JavaModelException
	{
		IFolder fol = getPackageIFile(packageName);
		return fol.getRawLocation().makeAbsolute().toFile();
	}
	
	/*
	 * Get Package IFile - Returns the underlying resource of a package. 
	 */
	private static IFolder getPackageIFile(String pk) throws JavaModelException
	{
		IPackageFragment p = getPackage(pk);
		if(p != null)
		{
			IResource res = p.getUnderlyingResource();
			if(res.getType() == IResource.FOLDER)
			{
				IFolder fol = (IFolder)res;
				return fol; 
			}
		}
		return null;
	}
	
	/*
	 * Get Class - Returns the compilation unit of the class. 
	 * TODO - Refactor this method to better reflect what is returned. 
	 */
	public static ICompilationUnit getClass(String mypackage, String name) throws JavaModelException
	{
		IPackageFragment localPackage = getPackage(mypackage);
		if(localPackage == null)
		{
			return null;
		}
		for(ICompilationUnit unit:getClasses(localPackage))
		{
			if(unit.getElementName().equals(name))
			{
				
				return unit; 
			}
		}
		return null;
		
	}
	
	/*
	 * Get Constructors - Returns a collection of constructors from a given compilation unit(class file). 
	 */
	private static IMethod[] getConstructors(ICompilationUnit unit) throws JavaModelException
	{
		List<IMethod> methods = new ArrayList<IMethod>();
		for(IType type:unit.getAllTypes())
		{
			for(IMethod method:type.getMethods())
			{
				if(method.isConstructor())
				{
					methods.add(method);
				}
			}
		}
		return methods.toArray(new IMethod[methods.size()]);
	}
	
	
	/*
	 * Get Constructor Names - This method returns the names of all the constructors in a given class from a given package. 
	 */
	public static String[] getConstructorNames(String myPackage, String myClass) throws JavaModelException
	{
		ICompilationUnit desiredClass = getClass(myPackage,myClass);
		IMethod[] methods = getConstructors(desiredClass);
		String[] strings = new String[methods.length];
		
		for(int i = 0; i<methods.length;i++)
		{
			strings[i] = methods[i].getElementName();
		}
		return strings;
	}
	
	
	/*
	 * Get Constructor Parameters - Returns the parameters to be passed to all constructors in a given class. 
	 */
	private static String[][] getConstructorParameters(ICompilationUnit unit) throws JavaModelException
	{
		
		IMethod[] constructors = getConstructors(unit);
		String[][] strings = new String[constructors.length][];
			for(int i =0; i<constructors.length;i++)
			{
				if(constructors[i].isConstructor())
				{
					strings[i] = constructors[i].getParameterTypes();
				}
			}
		
		return strings;
		
	}
	
	/*
	 * Get Constructor Parameter Names - Returns the names of all parameters to be passed to all constructors in a given class. 
	 */
	private static String[][] getConstructorParameterNames(ICompilationUnit unit) throws JavaModelException
	{
		IMethod[] constructors = getConstructors(unit);
		String[][] strings = new String[constructors.length][];
			for(int i =0; i<constructors.length;i++)
			{
				if(constructors[i].isConstructor())
				{
					strings[i] = constructors[i].getParameterNames();
				}
			}
		
		return strings;
	}
	
	/*
	 * Get Field Names - Returns a collection of field names (global variables) from a given class. 
	 */
	private static String[] getFieldNames(ICompilationUnit unit) throws JavaModelException
	{
		if(unit == null)
		{
			return null;
		}
		IType[] fields = unit.getAllTypes();
		List<String> strings = new ArrayList<String>();
		for(int i = 0; i<fields.length;i++)
		{
			for(int x = 0; x<fields[i].getFields().length; x++)
			{
				strings.add(fields[i].getFields()[x].getElementName());
				//strings[i] = fields[i].getElementName();
				
			}
			
		}
		return strings.toArray(new String[strings.size()]);
	}
	
	/*
	 * Get Field Types - Returns a collection of string representations of a fields type.
	 */
	private static String[] getFieldTypes(ICompilationUnit unit) throws JavaModelException
	{
		IType[] fields = unit.getAllTypes();
		List<String> strings = new ArrayList<String>();
		for(int i = 0; i<fields.length;i++)
		{
			for(int x = 0; x<fields[i].getFields().length; x++)
			{
				strings.add(fields[i].getFields()[x].getTypeSignature());
				//strings[i] = fields[i].getElementName();
				
			}
			
		}
		return strings.toArray(new String[strings.size()]);
	}
	
	/*
	 * Get Field Types - Returns a collection of string representations of a fields type from a given class and package. 
	 */
	public static String[] getFieldTypes(String myPackage,String myClass) throws JavaModelException
	{
		return getFieldTypes(getClass(myPackage,myClass));
	}
	
	/*
	 * Get Field Names - Returns the field names of a given class. 
	 */
	public static String[] getFieldNames(String myPackage, String myClass) throws JavaModelException
	{
		return getFieldNames(getClass(myPackage,myClass));
	}
	
	/*
	 * Get Constructor Parameters - Returns the parameter types of a given classes constructors. 
	 */
	public static String[][] getConstructorParameters(String myPackage,String myClass) throws JavaModelException
	{
		ICompilationUnit unit = getClass(myPackage,myClass);
		return getConstructorParameters(unit);
	}
	
	/*
	 * Get Constructor Parameter Names - Returns a collection of parameter names for all constructors of a given class.
	 */
	public static String[][] getConstructorParamaterNames(String myPackage, String myClass) throws JavaModelException
	{
		ICompilationUnit unit = getClass(myPackage,myClass);
		return getConstructorParameterNames(unit);
	}
	
	/*
	 * Get Methods - Returns a collection of methods from a class. 
	 */
	private static IMethod[] getMethods(ICompilationUnit unit) throws JavaModelException
	{
		List<IMethod> methods = new ArrayList<IMethod>();
		for(IType type:unit.getAllTypes())
		{
			//System.out.println(type.getElementName());
			for(IMethod method:type.getMethods())
			{
				if(method.isConstructor())
				{
					//System.out.println("Constructor Found");
				}
				else
				{
					methods.add(method);	
				}
			}
		}
		return methods.toArray(new IMethod[methods.size()]);
	}
	
	/*
	 * Get Class Method Names - Returns a collection of method names from a given class. 
	 */
	public static String[] getClassMethodNames(String myPackage, String myClass) throws JavaModelException
	{
		IPackageFragment localPackage = getPackage(myPackage);
		if(localPackage == null)
		{
			return null;
		}
		ICompilationUnit[] classes = getClasses(localPackage);
		ICompilationUnit desiredClass = null;
		for(ICompilationUnit c:classes)
		{
			if(c.getElementName() == myClass)
			{
				desiredClass = c; 
			}
		}
		IMethod[] methods = getMethods(desiredClass);
		String[] strings = new String[methods.length];
		
		for(int i = 0; i<methods.length;i++)
		{
			strings[i] = methods[i].getElementName();
		}
		return strings;
	}
	
	/*
	 * Get Class Method Return Types - Returns the types that methods in a class return. 
	 */
	public static String[] getClassMethodReturnTypes(String myPackage, String myClass) throws JavaModelException
	{
		
		IPackageFragment localPackage = getPackage(myPackage);
		if(localPackage == null)
		{
			return null;
		}
		ICompilationUnit[] classes = getClasses(localPackage);
		ICompilationUnit desiredClass = null;
		for(ICompilationUnit c:classes)
		{
			if(c.getElementName() == myClass)
			{
				desiredClass = c; 
			}
		}
		IMethod[] methods = getMethods(desiredClass);
		
		String[] strings = new String[methods.length];
		
		for(int i = 0; i<methods.length;i++)
		{
			strings[i] = methods[i].getReturnType();
		}
		return strings;
		
	}
	
	/*
	 * Ge Method Signatures - Gets the full method signatures from a class. 
	 */
	public static String[] getMethodSignatures(String myPackage, String myClass) throws JavaModelException
	{
		IPackageFragment localPackage = getPackage(myPackage);
		if(localPackage == null)
		{
			return null;
		}
		ICompilationUnit[] classes = getClasses(localPackage);
		ICompilationUnit desiredClass = null;
		for(ICompilationUnit c:classes)
		{
			if(c.getElementName() == myClass)
			{
				desiredClass = c; 
			}
		}
		IMethod[] methods = getMethods(desiredClass);
		String[] strings = new String[methods.length];
		
		for(int i = 0; i<methods.length;i++)
		{
			String s = StringHelper.fixType(methods[i].getReturnType()) + " " + methods[i].getElementName() + "(";
			String[] names = methods[i].getParameterNames();
			String[] types = methods[i].getParameterTypes(); 
			for(int x = 0; x<names.length;x++)
			{
				if(x < names.length -1)
				{
					s += StringHelper.fixType(types[x]) + " " + names[x] + ",";
				}
				else
				{
					s += StringHelper.fixType(types[x]) + " " + names[x];
				}
			}
			s += ")";
			strings[i] = s;
		}
		return strings;
	}
	
	/*
	 * Get Class Method Parameter Types - Returns a multi-dimensional array containing all of the parameter types for each method. 
	 * Example:- String[i][x] i = The method x = The parameter type
	 */
	public static String[][] getClassMethodParameterTypes(String myPackage, String myClass) throws JavaModelException
	{
		IPackageFragment localPackage = getPackage(myPackage);
		if(localPackage == null)
		{
			return null;
		}
		ICompilationUnit[] classes = getClasses(localPackage);
		ICompilationUnit desiredClass = null;
		for(ICompilationUnit c:classes)
		{
			if(c.getElementName() == myClass)
			{
				desiredClass = c; 
			}
		}
		IMethod[] methods = getMethods(desiredClass);
		String[][]stringReturn = new String[methods.length][];
		for(int i = 0; i<methods.length;i++)
		{
				stringReturn[i] = methods[i].getParameterTypes();
		}
		return stringReturn;
	
	}
	
	/*
	 * Get Class Method Parameter Names - Gets the names of parameters within all methods. 
	 * Example:- String[i][x] i = the method x = the parameter name
	 */
	public static String[][] getClassMethodParameterNames(String myPackage, String myClass,String methodName, String returnType) throws JavaModelException
	{
		IPackageFragment localPackage = getPackage(myPackage);
		if(localPackage == null)
		{
			return null;
		}
		ICompilationUnit[] classes = getClasses(localPackage);
		ICompilationUnit desiredClass = null;
		for(ICompilationUnit c:classes)
		{
			if(c.getElementName() == myClass)
			{
				desiredClass = c; 
			}
		}
		IMethod[] methods = getMethods(desiredClass);
		String[][]stringReturn = new String[methods.length][];
		for(int i = 0; i<methods.length;i++)
		{
				stringReturn[i] = methods[i].getParameterTypes();
		}
		return stringReturn;
	}
	
//	private static IField[] getFields(ICompilationUnit unit) throws JavaModelException
//	{
//		List<IField> fields = new ArrayList<IField>();
//		for(IType type:unit.getTypes())
//		{
//			for(IField field:type.getFields())
//			{
//				fields.add(field);
//			}
//		}
//		return fields.toArray(new IField[fields.size()]);
//	}
	
	/*
	 * Get Class Fields - Gets a collection of fields from a given class. 
	 */
	private static IField[] getClassFields(String myPacakge, String myClass)
	{
		//TODO - Implement this method. 
		return null;
		
	}
	
	/*
	 * Get Class Names - Returns a collection of class names from a given package. 
	 */
	public static String[] getClassNames(String myPackage) throws MalformedURLException, Exception
	{
		IPackageFragment localPackage = getPackage(myPackage);
		if(localPackage == null)
		{
			return null;
		}
		ICompilationUnit[] classes = getClasses(localPackage);
		String[] strings = new String[classes.length];
		for(int i = 0; i<classes.length;i++)
		{
			strings[i] = classes[i].getElementName();
			addToClassPath(classes[i].getElementName(), myPackage); 
			//addToClassPath(StringHelper.stripExtension(localPackage.getElementName() + "." + strings[i]));
		}
		return strings;
		
	}
	
	/*
	 * Get Packages - Returns a collection of packages from a given Java project. 
	 */
	public static IPackageFragment[] getPackages(IJavaProject project) throws JavaModelException
	{
		return project.getPackageFragments();
	}
	
	/*
	 * Get Packages - Returns a collection of packages from a given project by name. 
	 */
	public static IPackageFragment[] getPackages(String projectName) throws MalformedURLException, Exception
	{
		IJavaProject project = getJavaProject(getProject(projectName));
		List<IPackageFragment> fragments = new ArrayList<IPackageFragment>();
		for(IPackageFragment myPackage:project.getPackageFragments())
		{
			if(myPackage.getKind() == IPackageFragmentRoot.K_SOURCE)
			{
				if(!fragments.contains(myPackage))
				{
					if(!myPackage.getElementName().isEmpty())
					{
						fragments.add(myPackage);	
						
					}
				}
			}
		}
		return fragments.toArray(new IPackageFragment[fragments.size()]);
	}
	
	/*
	 * Add To Class Path - Attemps to add a user class to the class path. 
	 */
	static void addToClassPath(String s) throws ClassNotFoundException
	{
		//TODO - Look at maybe removing this method as it may now be depreciated. 
		ClassLoader.getSystemClassLoader().loadClass(s);
		//System.out.println(s);
		//ResourceBundle.getBundle(s,Locale.getDefault(), ClassLoader.getSystemClassLoader());
	}
	
	/*
	 * Get Class From Loader - This tiny method solves the reflection problem as a result of the dual VM setup in Eclipse. 
	 * This method allows us to get the Class object from a qualified class name. 
	 */
	public static Class<?> getClassFromLoader(String s) throws ClassNotFoundException, MalformedURLException
	{
		try {
			buildActiveProject();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		URL[] urls= null;
//		File dir = new File(System.getProperty("user.dir") + File.separator + "dir" + File.separator);
//		URL url = dir.toURI().toURL();
//		urls = new URL[] {url};
//		ClassLoader cl = new URLClassLoader(urls);
//		return cl.loadClass(s);
		return classLoader.loadClass(s);
		//return ClassLoader.getSystemClassLoader().loadClass(s);
	}
	
	/*
	 * Get Superclass - Gets the superclass of a given class. 
	 */
	public static Class<?> getSuperclass(Class<?> c)
	{
		return c.getSuperclass();
	}
	
	/*
	 * Get Superclass - Gets the superclass of a given class. 
	 */
	public static Class<?> getSuperClass(String s) throws ClassNotFoundException, MalformedURLException
	{
		Class<?> c = getClassFromLoader(s);
		return getSuperclass(c);
	}
	/*
	 * Get Package Names - Returns a collection of package names from a given project. 
	 */
	public static String[] getPackageNames(String projectName) throws MalformedURLException, Exception
	{
		IPackageFragment[] packages = getPackages(projectName);
		String[] returnString = new String[packages.length]; 
		for(int i = 0; i<packages.length;i++)
		{
			returnString[i] = packages[i].getElementName();
		}
		return returnString;
	}
	
	/*
	 * Get Number of Classes From Package - Returns the number of classes in a given package. 
	 */
	private static int getNumberOfClassesFromPackage(IPackageFragment myPackage)
	{
		try {
			return 	myPackage.getCompilationUnits().length;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/*
	 * Get Number of Classes From Package - Returns the number of classes in a given package. 
	 */
	public static int getNumberOfClassesFromPackage(String name) throws JavaModelException
	{
		return getNumberOfClassesFromPackage(getPackage(name));
	}
	
	/*
	 * Get Package - Returns a package found by name.
	 */
	private static IPackageFragment getPackage(String name) throws JavaModelException
	{
		IPackageFragment[] packages =  getPackages(getJavaProject(getActiveProject()));
		for(IPackageFragment myPackage:packages)
		{
			if(myPackage.getElementName().equals(name))
			{
				return myPackage;
			}
		}
		return null;
	}
	
	/*
	 * Get Project - Returns a project requested by its name. 
	 */
	private static IProject getProject(String name) throws MalformedURLException, Exception
	{
		if(projects == null)
		{
			Initialise(); 
		}
		for(IProject project:projects)
		{
			if(project.getName().equals(name))
			{
				return project;
			}
		}
		return null; 
	}
	
	/*
	 * Get Java Project - Returns the Java Project from a given IProject. 
	 */
	private static IJavaProject getJavaProject(IProject project)
	{
		return JavaCore.create(project);
	}
	
	/*
	 * Get Active Project - Returns the active project. 
	 */
	private static IProject getActiveProject()
	{

		ISelectionService selection = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		IResource resource = extractSelection(selection.getSelection());
		if(resource != null)
		{
			activeProject = resource.getProject();
		}
		
		return activeProject; 
	}
	
	/*
	 * Get Active Project Name - Returns the name of an active project. 
	 */
	public static String getActiveProjectName()
	{
		return (getActiveProject() != null) ? getActiveProject().getName():(projects != null && projects.length > 0) ? projects[0].getName():null;
	}
	
	/*
	 * Add URL - Adds a URL to the class path. 
	 */
	public static void addURL(URL url) throws Exception{
//		Class<URLClassLoader> myClass = URLClassLoader.class; 
//		Method method = myClass.getDeclaredMethod("addURL", new Class[]{URL.class});
//		method.setAccessible(true);
//		method.invoke(classLoader, new Object[]{url});
		myUrls.add(url);
		Class<URLClassLoader> myClass = URLClassLoader.class; 
     	URL[] urls = myUrls.toArray(new URL[myUrls.size()]);		
     	classLoader = new URLClassLoader(urls);
		//Method method = myClass.getDeclaredMethod("addURL", new Class[]{URL.class});
		//method.setAccessible(true);
		//method.invoke(classLoader, new Object[]{url});
	//return cl.loadClass(s);
		
	}
	
	/*
	 * Extract Selection - This method takes the active selection from the resource viewer. 
	 */
	 private static IResource extractSelection(ISelection sel) {
	      if (!(sel instanceof IStructuredSelection))
	         return null;
	      IStructuredSelection ss = (IStructuredSelection) sel;
	      Object element = ss.getFirstElement();
	      if (element instanceof IResource)
	         return (IResource) element;
	      if (!(element instanceof IAdaptable))
	         return null;
	      IAdaptable adaptable = (IAdaptable)element;
	      Object adapter = adaptable.getAdapter(IResource.class);
	      return (IResource) adapter;
	   }
	 
	 /*
	  * Is Project Open - Checks whether a specified project is open. 
	  */
	 public static boolean isProjectOpen(String s) throws MalformedURLException, Exception
	 {
		 IProject p = getProject(s);
		 return p.isOpen();
	 }
}
