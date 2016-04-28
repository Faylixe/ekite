package fr.faylixe.ekite.internal;

import java.io.IOException;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

import fr.faylixe.ekite.EKitePlugin;

/**
 * {@link DocumentListener} is in charge of monitoring
 * <tt>edit</tt> event and to dispatch them to Kite.
 * Such event are triggered by any change in the
 * listened document.
 * 
 * TODO	Consider using dedicated queue
 * 		for sending event in a separated
 * 		thread in order to avoid blocking ?
 * 
 * @author fv
 */
public final class DocumentListener implements IDocumentListener {

	/** Event sender used by this listener to send event notification. **/
	private final EventSender sender;

	/**
	 * Default constructor.
	 * 
	 * @param sender Event sender instance to use.
	 */
	public DocumentListener(final EventSender sender) {
		this.sender = sender;
	}

	/** {@inheritDoc} **/
	@Override
	public void documentAboutToBeChanged(final DocumentEvent event) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void documentChanged(final DocumentEvent event) {
		try {
			sender.sendEdit(event.getOffset(), event.getOffset() + event.getLength());
			sender.sendSelection(event.getOffset(), event.getOffset() + event.getLength());
			if (EKitePlugin.DEBUG) {
				EKitePlugin.log("Edit / Selection for " + event.getOffset() + " to " + event.getLength());
			}
		}
		catch (final IOException | IllegalStateException e) {
			EKitePlugin.log(e);
		}
	}

}
