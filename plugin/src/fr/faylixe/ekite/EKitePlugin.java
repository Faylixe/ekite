// Contents of this plugin will be reset by Kite on start. Changes you make are not guaranteed to persist.
package fr.faylixe.ekite;

import java.io.IOException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
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
public class EKitePlugin extends AbstractUIPlugin implements IWindowListener, IPageListener, FocusListener, IPropertyChangeListener {

	/** Plugin id. **/
	public static final String PLUGIN_ID = "fr.faylixe.ekite"; //$NON-NLS-1$

	/** Boolean flag that indiciates if the debug ode is active or not. **/
	public static final boolean DEBUG = Boolean.valueOf(System.getProperty("fr.faylixe.ekite.debug"));

	/** Error title for eKite error dialog. **/
	private static final String ERROR_TITLE = "eKite error";

	/** Plugin instance. **/
	private static EKitePlugin plugin;

	/** Part listener instance used. **/
	private PartListener listener;

	/** Event receiver for this Eclipse instance. **/
	private EventReceiver receiver;

	/** Event sender for this Eclipse instance. **/
	private EventSender sender;

	/** Indicates if eKite is currently active or not. **/
	private boolean active;

	/** {@inheritDoc} **/
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		final EKitePreference preference = EKitePreference.getInstance();
		preference.load(getPreferenceStore(), this);
	}

	/** {@inheritDoc} **/
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		final EKitePreference preferences = EKitePreference.getInstance();
		final String property = event.getProperty();
		final Object value = event.getNewValue();
		if (EKitePreference.HOSTNAME_PROPERTY.equals(property)) {
			final String hostname = value.toString();
			if (sender != null) {
				sender.setHostname(hostname);
			}
			preferences.setHostname(hostname);
		}
		else if (EKitePreference.PORT_PROPERTY.equals(property)) {
			final int port = Integer.valueOf(value.toString());
			if (sender != null) {
				sender.setPort(port);
			}
			preferences.setPort(port);
		}
		else if (EKitePreference.SHOW_HIGHLIGHT_PROPERTY.equals(property)) {
			final boolean showHighlight = Boolean.valueOf(value.toString());
			if (receiver != null) {
				receiver.setShowHighlight(showHighlight); 
			}
			preferences.setShowHighlight(showHighlight);
		}
	}
	
	/**
	 * Activates eKite.
	 */
	public void activate() {
		if (DEBUG) {
			log("Activating eKite");
		}
		final EKitePreference preference = EKitePreference.getInstance();
		BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
			@Override
			public void run() {
				try {
					receiver = EventReceiver.create(Display.getDefault(), preference.shouldShowHighlight());
					receiver.start();
					sender = EventSender.create(receiver, preference.getHostname(), preference.getPort());
					listener = new PartListener(sender);
					registerListeners();
					active = true;
				}
				catch (final Throwable e) {
					EKitePlugin.log(e);
					showError("An error occurs while activating eKite : " + e.getMessage());
				}
			}
		});
	}
	
	/**
	 * Registers all listeners (window, focus, document, etc ...)
	 */
	private void registerListeners() {
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
						page.addPartListener(listener);
						for (final IEditorReference reference : page.getEditorReferences()) {
							final IWorkbenchPart part = reference.getPart(false);
							listener.configure(part);
						}
					}
				}
			}
		});
	}

	/**
	 * Indicates if eKite is active.
	 * 
	 * @return <tt>true</tt> if eKite is active, <tt>false</tt> otherwise.
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Desactivate eKite.
	 */
	public void desactivate() {
		if (DEBUG) {
			log("Desactivating eKite");
		}
		BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
			@Override
			public void run() {
				try {
					sender.sendLostFocus();
					unregisterListeners();
					receiver.shutdown();
					receiver = null;
					sender = null;
					listener = null;
					active = false;
				}
				catch (final IOException e) {
					EKitePlugin.log(e);
					showError("An error occurs while desactivating eKite : " + e.getMessage());
				}
			}
		});
	}
	
	/**
	 * Unregisters all listeners (window, focus, document, etc ...)
	 */
	private void unregisterListeners() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.removeWindowListener(this);
		workbench.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if (window != null) {
					window.getShell().removeFocusListener(EKitePlugin.this);
					window.removePageListener(EKitePlugin.this);
					final IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						page.removePartListener(listener);
						for (final IEditorReference reference : page.getEditorReferences()) {
							final IWorkbenchPart part = reference.getPart(false);
							listener.unconfigure(part);
						}
					}
				}
			}
		});
	}

	/** {@inheritDoc} **/
	@Override
	public void stop(final BundleContext context) throws Exception {
		if (isActive()) {
			this.sender.sendLostFocus();
			this.receiver.shutdown();
		}
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
	 * Show an error dialog with the given <tt>message</tt>.
	 * 
	 * @param message Error message.
	 */
	private static void showError(final String message) {
		final Display display = Display.getDefault();
		if (display != null) {
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openError(display.getActiveShell(), ERROR_TITLE, message);
				}	
			});
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
		final EKitePlugin instance = getDefault();
		if (instance != null) {
			final ILog logger = instance.getLog();
			logger.log(status);
		}
	}

}
