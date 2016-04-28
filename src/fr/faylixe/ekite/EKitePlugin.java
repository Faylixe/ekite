// Contents of this plugin will be reset by Kite on start. Changes you make are not guaranteed to persist.
package fr.faylixe.ekite;

import java.io.IOException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
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

import fr.faylixe.ekite.internal.EventSender;
import fr.faylixe.ekite.internal.EventReceiver;
import fr.faylixe.ekite.internal.PartListener;

/**
 * TODO : Plugin documentation.
 */
public class EKitePlugin extends AbstractUIPlugin implements IWindowListener, IPageListener, FocusListener, IStartup {

	/** Plugin id. **/
	public static final String PLUGIN_ID = "fr.faylixe.ekite"; //$NON-NLS-1$

	/** Boolean flag that indiciates if the debug ode is active or not. **/
	public static final boolean DEBUG = true;

	/** Plugin instance. **/
	private static EKitePlugin plugin;

	/** Part listener instance used. **/
	private PartListener listener;

	/** Event receiver for this Eclipse instance. **/
	private EventReceiver receiver;

	/** Event sender for this Eclipse instance. **/
	private EventSender sender;

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
		window.addPageListener(this);
	}

	/** {@inheritDoc} **/
	@Override
	public void windowDeactivated(final IWorkbenchWindow window) {
		window.removePageListener(this);
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
		page.addPartListener(listener);
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

	/**
	 * Creates all components of this plugins.
	 */
	private void initialize() {
		try {
			this.receiver = EventReceiver.create();
			this.sender = EventSender.create(receiver);
			this.listener = new PartListener(sender);
		}
		catch (final IOException e) {
			log("An error occurs while initializes Kite support", e);
		}
	}

	/** {@inheritDoc} **/
	@Override
	public void earlyStartup() {
		initialize();
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.addWindowListener(this);
		workbench.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if (window != null) {
					window.getShell().addFocusListener(EKitePlugin.this);
					window.addPageListener(EKitePlugin.this);
					final IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						// TODO : Check for restored.
						page.addPartListener(listener);
					}
				}
			}
		});
	}

	/** {@inheritDoc} **/
	@Override
	public void focusGained(final FocusEvent event) {
		try {
			sender.sendFocus();
		}
		catch (final IOException e) {
			log(e);
		}
	}

	/** {@inheritDoc} **/
	@Override
	public void focusLost(final FocusEvent event) {
		try {
			sender.sendLostFocus();
		}
		catch (final IOException e) {
			log(e);
		}
	}
	
	/**
	 * Shortcut for logging the given <tt>message</tt>
	 * through this plugin logger.
	 * 
	 * @param message Message to log.
	 */
	public static void log(final String message) {
		log(message, null);
	}

	/**
	 * Shortcut for logging the given <tt>exception</tt>
	 * through this plugin logger.
	 * 
	 * @param exception Exception to log (could be <tt>null</tt>).
	 */
	public static void log(final Throwable exception) {
		log(exception.getMessage(), exception);
	}

	/**
	 * Shortcut for logging the given <tt>message</tt>
	 * and <tt>exception</tt> through this plugin logger.
	 * 
	 * @param message Message to log.
	 * @param exception Exception to log (could be <tt>null</tt>).
	 */
	public static void log(final String message, final Throwable exception) {
		final Status status;
		if (exception == null) {
			status = new Status(Status.INFO, PLUGIN_ID, message);
		}
		else {
			status = new Status(Status.ERROR, PLUGIN_ID, message, exception);			
		}
		final ILog logger = getDefault().getLog();
		logger.log(status);
	}

}
