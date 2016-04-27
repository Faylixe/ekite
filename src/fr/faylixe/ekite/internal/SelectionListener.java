package fr.faylixe.ekite.internal;

import java.io.IOException;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import fr.faylixe.ekite.backup.Event;
import fr.faylixe.ekite.backup.EventSender;
import fr.faylixe.ekite.model.Selection;

/**
 * 
 * @author fv
 */
public final class SelectionListener implements ISelectionChangedListener {

	/** **/
	private final Event fileEvent;

	/**
	 * 
	 * @param fileEvent
	 */
	public SelectionListener(final Event fileEvent) {
		this.fileEvent = fileEvent;
	}

	/** {@inheritDoc} **/
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final ISelection selection = event.getSelection();
		if (selection instanceof ITextSelection) {
			final ITextSelection textSelection = (ITextSelection) selection;
			try {
				final String text = textSelection.getText();
				System.out.println("Selection is text : " + text);
				final Event selectionEvent = fileEvent
						.toSelectionEvent()
						.withText(text)
						.withSelection(new Selection(0, text.length()));
				EventSender.get().send(selectionEvent);
				System.out.println("Selection event sent : " + selectionEvent.toJSON());
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

}
