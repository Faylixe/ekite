// Contents of this plugin will be reset by Kite on start. Changes you make are not guaranteed to persist.
package fr.faylixe.ekite;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import fr.faylixe.ekite.internal.PartListener;

/**
 * TODO : Plugin documentation.
 */
public class EKitePlugin extends AbstractUIPlugin {

	/** Plugin id. **/
	public static final String PLUGIN_ID = "fr.faylixe.ekite"; //$NON-NLS-1$

	/** Plugin instance. **/
	private static EKitePlugin plugin;

	/** **/
	private static final String SOURCE = "eclipse";

	/**
	 * Default constructor.
	 */
	public EKitePlugin() {
	}

	/** {@inheritDoc} **/
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		final IWorkbenchPage page = window.getActivePage();
		page.addPartListener(new PartListener());
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
