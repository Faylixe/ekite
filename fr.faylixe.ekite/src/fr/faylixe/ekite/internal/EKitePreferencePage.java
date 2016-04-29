package fr.faylixe.ekite.internal;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import fr.faylixe.ekite.EKitePlugin;
import fr.faylixe.ekite.EKitePreference;

/**
 * Custom preference page for this plugin.
 * 
 * @author fv 
 */
public final class EKitePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/** Hostname preference label. **/
	private static final String HOSTNAME = "Kite hostname";

	/** Port preference label. **/
	private static final String PORT = "Kite port";

	/** Show highlight preference label. **/
	private static final String SHOW_HIGHLIGHT = "Show highlight(s)";

	/** {@inheritDoc} **/
	@Override
	public void init(final IWorkbench workbench) {
		final EKitePlugin plugin = EKitePlugin.getDefault();
		final IPreferenceStore store = plugin.getPreferenceStore();
		setPreferenceStore(store);
	}

	/** {@inheritDoc} **/
	@Override
	protected void createFieldEditors() {
		final StringFieldEditor hostname = new StringFieldEditor(EKitePreference.HOSTNAME_PROPERTY, HOSTNAME, getFieldEditorParent());
		hostname.setEmptyStringAllowed(false);
		addField(hostname);addField(new IntegerFieldEditor(EKitePreference.PORT_PROPERTY, PORT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(EKitePreference.SHOW_HIGHLIGHT_PROPERTY, SHOW_HIGHLIGHT, getFieldEditorParent()));
	}

}
