package com.groupproject.workbench;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
/*
 * Static class for helping gain access to the Java Model. 
 */
import org.eclipse.core.runtime.IAdaptable;

public final class JavaModelHelper {	
	
	private static IWorkspace workspace;
	private static IWorkspaceRoot root; 
	private static IProject[] projects; 
	
	
	private static IProject activeProject;
	private static IPackageFragment activePackage;
	
	private JavaModelHelper()
	{
		Initialise();
	}
	
	
	public static void Initialise()
	{
		workspace = ResourcesPlugin.getWorkspace(); 
		root = workspace.getRoot();
		projects = root.getProjects();
	}
	
	public static IProject getProjectByName(String name)  throws JavaModelException
	{
		return root.getProject(name);
	}
	
	public static ICompilationUnit[] getClasses(IProject project)  throws JavaModelException
	{
		return null; 
	}
	
	public static ICompilationUnit[] getClasses(IPackageFragment myPackage) throws JavaModelException
	{
		return myPackage.getCompilationUnits();
		
	}
	
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
	
	public static File getClassFile(String myPackage, String myClassName) throws JavaModelException
	{
		IFile iFile = getClassIFile(myPackage,myClassName);
		return iFile.getRawLocation().makeAbsolute().toFile();
	}
	
	public static String getClassFilePath(String myPackage, String myClassName) throws JavaModelException
	{
		IFile myFile = getClassIFile(myPackage,myClassName);
		return myFile.getRawLocation().toString();
	}
	
	private static ICompilationUnit getClass(String mypackage, String name) throws JavaModelException
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
	
	public static String[][] getConstructorParameters(String myPackage,String myClass) throws JavaModelException
	{
		ICompilationUnit unit = getClass(myPackage,myClass);
		return getConstructorParameters(unit);
	}
	
	public static String[][] getConstructorParamaterNames(String myPackage, String myClass) throws JavaModelException
	{
		ICompilationUnit unit = getClass(myPackage,myClass);
		return getConstructorParameterNames(unit);
	}
	
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
	
	private static IField[] getFields(ICompilationUnit unit) throws JavaModelException
	{
		List<IField> fields = new ArrayList<IField>();
		for(IType type:unit.getTypes())
		{
			for(IField field:type.getFields())
			{
				fields.add(field);
			}
		}
		return fields.toArray(new IField[fields.size()]);
	}
	
	public static IField[] getClassFields(String myPacakge, String myClass)
	{
		return null;
		
	}
	
	public static String[] getClassNames(String myPackage) throws JavaModelException
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
		}
		return strings;
		
	}
	
	
	public static IPackageFragment[] getPackages(IJavaProject project) throws JavaModelException
	{
		return project.getPackageFragments();
	}
	
	public static IPackageFragment[] getPackages(String projectName) throws JavaModelException
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
		return (IPackageFragment[]) fragments.toArray(new IPackageFragment[fragments.size()]);
	}
	
	public static String[] getPackageNames(String projectName) throws JavaModelException
	{
		IPackageFragment[] packages = getPackages(projectName);
		String[] returnString = new String[packages.length]; 
		for(int i = 0; i<packages.length;i++)
		{
			returnString[i] = packages[i].getElementName();
		}
		return returnString;
	}
	
	public static int getNumberOfClassesFromPackage(IPackageFragment myPackage)
	{
		try {
			return 	myPackage.getCompilationUnits().length;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int getNumberOfClassesFromPackage(String name) throws JavaModelException
	{
		return getNumberOfClassesFromPackage(getPackage(name));
	}
	
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
	

	
	private static IProject getProject(String name)
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
	
	private static IJavaProject getJavaProject(IProject project)
	{
		return JavaCore.create(project);
	}
	
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
	
	public static String getActiveProjectName()
	{
		return (getActiveProject() != null) ? getActiveProject().getName():null;
	}
	
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
}
