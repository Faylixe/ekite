package fr.faylixe.ekite.internal;

import java.io.IOException;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;

import fr.faylixe.ekite.EKitePlugin;

/**
 * {@link SelectionListener} is in charge of monitoring
 * ``selection`` event and to dispatch them to Kite.
 * Such event are triggered by a cursor move, or user selection.
 * 
 * TODO	Consider using dedicated queue
 * 		for sending event in a separated
 * 		thread in order to avoid blocking
 * 
 * @author fv
 */
public final class SelectionListener implements ISelectionChangedListener, CaretListener {

	/** Event sender used by this listener to send event notification. **/
	private final EventSender sender;

	/**
	 * Default constructor.
	 * 
	 * @param sender Event sender instance to use.
	 */
	public SelectionListener(final EventSender sender) {
		this.sender = sender;
	}

	/** {@inheritDoc} **/
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final ISelection selection = event.getSelection();
		if (selection instanceof ITextSelection) {
			final ITextSelection textSelection = (ITextSelection) selection;
			try {
				sender.sendSelection(
						textSelection.getOffset(),
						textSelection.getOffset() + textSelection.getLength());
			}
			catch (final IOException | IllegalStateException e) {
				EKitePlugin.log(e);
			}
		}
	}

	/** {@inheritDoc }**/
	@Override
	public void caretMoved(final CaretEvent event) {
		try {
			sender.sendSelection(event.caretOffset, event.caretOffset);
			EKitePlugin.log("Caret moved to " + event.caretOffset);
		}
		catch (final IOException | IllegalStateException e) {
			EKitePlugin.log(e);
		}
	}

}
