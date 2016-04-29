package fr.faylixe.ekite;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Wrapper for preference processing.
 * 
 * @author fv
 */
public final class EKitePreference {

	/** Hostname preference name. **/
	public static final String HOSTNAME_PROPERTY = "hostname";

	/** Port preference name. **/
	public static final String PORT_PROPERTY = "port";

	/** Show highlight preference name. **/
	public static final String SHOW_HIGHLIGHT_PROPERTY = "showHighlight";

	/** Default hostname value. **/
	public static final String DEFAULT_HOSTNAME = "127.0.0.1";

	/** Default port value. **/
	public static final int DEFAULT_PORT = 46625;

	/** Unique instance. **/
	private static EKitePreference instance;

	/** Target preferences. **/
	private final IEclipsePreferences preferences;

	/** Current hostname. **/
	private String hostname;

	/** Current port. **/
	private int port;

	/** Boolean flag that indicates if highlight should be shown. **/
	private boolean showHighlight;

	/**
	 * Default constructor.
	 */
	private EKitePreference() {
		this.port = DEFAULT_PORT;
		this.hostname = DEFAULT_HOSTNAME;
		this.showHighlight = true;
		this.preferences = InstanceScope.INSTANCE.getNode(EKitePlugin.PLUGIN_ID);
	}

	/**
	 * Loads plugin preference from the given <tt>store</tt>.
	 * 
	 * @param store Preference store to load property from.
	 */
	public void load(final IPreferenceStore store, final IPropertyChangeListener listener) {
		final IEclipsePreferences node = InstanceScope.INSTANCE.getNode(EKitePlugin.PLUGIN_ID);
		store.setDefault(HOSTNAME_PROPERTY, DEFAULT_HOSTNAME);
		store.setDefault(PORT_PROPERTY, DEFAULT_PORT);
		store.setDefault(SHOW_HIGHLIGHT_PROPERTY, true);
		store.setValue(HOSTNAME_PROPERTY, node.get(HOSTNAME_PROPERTY, DEFAULT_HOSTNAME));
		store.setValue(PORT_PROPERTY, node.getInt(PORT_PROPERTY, DEFAULT_PORT));
		store.setValue(SHOW_HIGHLIGHT_PROPERTY, node.getBoolean(SHOW_HIGHLIGHT_PROPERTY, true));
		store.addPropertyChangeListener(listener);
	}

	/**
	 * Saves the current preferences.
	 */
	private void save() {
		try {
			preferences.flush();
		}
		catch (final BackingStoreException e) {
			EKitePlugin.log(e);
		}
	}

	/**
	 * Hostname getter.
	 * 
	 * @return Hostname preference.
	 * @see #hostname
	 */
	public String getHostname() {
		return hostname;
	}
	
	/**
	 * Hostname setter.
	 * 
	 * @param hostname Hostname preference.
	 * @see #hostname
	 */
	public void setHostname(final String hostname) {
		this.hostname = hostname;
		preferences.put(HOSTNAME_PROPERTY, hostname);
		save();
	}
	
	/**
	 * Port getter.
	 * 
	 * @return Port preference.
	 * @see #port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Port setter.
	 * 
	 * @param port Port preference.
	 * @see #port
	 */
	public void setPort(final int port) {
		this.port = port;
		preferences.putInt(PORT_PROPERTY, port);
		save();
	}
	
	/**
	 * Show highlight getter.
	 * 
	 * @return Show highlight preference.
	 * @see #showHighlight
	 */
	public boolean shouldShowHighlight() {
		return showHighlight;
	}

	/**
	 * Show highlight getter.
	 * 
	 * @param showHighlight Show highlight preference.
	 * @see #showHighlight
	 */
	public void setShowHighlight(final boolean showHighlight) {
		this.showHighlight = showHighlight;
		preferences.putBoolean(SHOW_HIGHLIGHT_PROPERTY, showHighlight);
		save();
	}

	/**
	 * Unique instance getter. If such instance does
	 * not exist it will be created.
	 * 
	 * @return Unique preference handler instance.
	 */
	public static EKitePreference getInstance() {
		synchronized (EKitePreference.class) {
			if (instance == null) {
				instance = new EKitePreference();
			}
		}
		return instance;
	}

}
