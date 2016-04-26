// Contents of this plugin will be reset by Kite on start. Changes you make are not guaranteed to persist.
package fr.faylixe.ekite;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * TODO : Plugin documentation.
 */
public class EKitePlugin extends AbstractUIPlugin {

	/** Plugin id. **/
	public static final String PLUGIN_ID = "fr.faylixe.ekite"; //$NON-NLS-1$

	/** Plugin instance. **/
	private static EKitePlugin plugin;
	
	/**
	 * Default constructor.
	 */
	public EKitePlugin() {
	}

	/** {@inheritDoc} **/
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/** {@inheritDoc} **/
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EKitePlugin getDefault() {
		return plugin;
	}

}
