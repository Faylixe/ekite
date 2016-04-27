// Contents of this plugin will be reset by Kite on start. Changes you make are not guaranteed to persist.
package fr.faylixe.ekite;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import fr.faylixe.ekite.backup.Event;
import fr.faylixe.ekite.internal.PartListener;

/**
 * TODO : Plugin documentation.
 */
public class EKitePlugin extends AbstractUIPlugin implements IWindowListener, IPageListener, FocusListener, IStartup {

	/** Plugin id. **/
	public static final String PLUGIN_ID = "fr.faylixe.ekite"; //$NON-NLS-1$

	/** Plugin instance. **/
	private static EKitePlugin plugin;

	/** **/
	private static final String SOURCE = "eclipse";

	/** **/
	private final Event source;

	/** **/
	private final PartListener listener;

	/**
	 * Default constructor.
	 */
	public EKitePlugin() {
		this.source = new Event(SOURCE);
		this.listener = new PartListener(source);
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

	/** {@inheritDoc} **/
	@Override
	public void windowActivated(final IWorkbenchWindow window) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void windowDeactivated(final IWorkbenchWindow window) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void windowClosed(final IWorkbenchWindow window) {
		window.removePageListener(this);
	}

	/** {@inheritDoc} **/
	@Override
	public void windowOpened(final IWorkbenchWindow window) {
		window.addPageListener(this);
	}

	/** {@inheritDoc} **/
	@Override
	public void pageActivated(final IWorkbenchPage page) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void pageClosed(final IWorkbenchPage page) {
		page.removePartListener(listener);
	}

	/** {@inheritDoc} **/
	@Override
	public void pageOpened(final IWorkbenchPage page) {
		page.addPartListener(listener);
	}

	/** {@inheritDoc} **/
	@Override
	public void earlyStartup() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.addWindowListener(this);
		workbench.getDisplay().syncExec(() -> {
			final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window != null) {
				window.addPageListener(this);
				final IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					page.addPartListener(listener);
				}
			}
		});
	}

	/** {@inheritDoc} **/
	@Override
	public void focusGained(final FocusEvent event) {
		source
			.forFile(listener.getCurrentFilename())
			.toFocusEvent()
			.withText();
	}

	/** {@inheritDoc} **/
	@Override
	public void focusLost(final FocusEvent event) {
		// TODO Auto-generated method stub
		
	}

}
