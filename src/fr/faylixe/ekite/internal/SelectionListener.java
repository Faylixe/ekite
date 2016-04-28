package fr.faylixe.ekite.internal;

import java.io.IOException;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * 
 * @author fv
 */
public final class SelectionListener implements ISelectionChangedListener {

	/** **/
	private final EventSender sender;

	/**
	 * 
	 * @param fileEvent
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
						sender.getCurrentDocument().get(),
						textSelection.getOffset(),
						textSelection.getOffset() + textSelection.getLength());
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

}
