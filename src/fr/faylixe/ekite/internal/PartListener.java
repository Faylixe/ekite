package fr.faylixe.ekite.internal;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 
 * @author fv
 */
public final class PartListener implements IPartListener2 {

	/** **/
	private final SelectionListener selectionListener;

	/**
	 * 
	 */
	public PartListener() {
		this.selectionListener = new SelectionListener();
	}

	/** {@inheritDoc} **/
	@Override
	public void partActivated(final IWorkbenchPartReference partRef) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void partBroughtToTop(final IWorkbenchPartReference partRef) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void partClosed(final IWorkbenchPartReference partRef) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void partDeactivated(final IWorkbenchPartReference partRef) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void partOpened(final IWorkbenchPartReference partRef) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void partHidden(final IWorkbenchPartReference partRef) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void partVisible(final IWorkbenchPartReference partRef) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void partInputChanged(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);
		if (part != null && part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) part;
			final ISelectionProvider provider = editor.getSelectionProvider();
			provider.addSelectionChangedListener(null); // TODO Add selection listener.
			final IDocumentProvider documentProvider = editor.getDocumentProvider();
			final IDocument document = documentProvider.getDocument(editor.getEditorInput());
			document.addDocumentListener(null);
		}
	}

}
