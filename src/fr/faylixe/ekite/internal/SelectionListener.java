package fr.faylixe.ekite.internal;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * 
 * @author fv
 */
public final class SelectionListener implements ISelectionListener {

	/** {@inheritDoc} **/
	@Override
	public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
		if (selection instanceof ITextSelection) {
			final ITextSelection textSelection = (ITextSelection) selection;
			final int start = textSelection.getOffset();
			final int end = start + textSelection.getLength();
			if (start != -1 && end != -1) {
				// TODO : Add to target event.
				new Selection(start, end);
			}
		}
	}

}
