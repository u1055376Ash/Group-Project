package com.groupproject.workbench.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.groupproject.workbench.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class OBPreferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public OBPreferences() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("A demonstration of a preference page implementation");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_BOOLEAN_SHOWEMPTY, "Display Empty Packages", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_ONE, "Package Color Top", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_TWO, "Package Color Bottom", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_THREE, "Class Color Top", getFieldEditorParent()));
		addField(new ColorFieldEditor(PreferenceConstants.P_COLOR_FOUR, "Class Color Bottom", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}
	
}