package fr.faylixe.ekite.internal;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.ITextEditor;

import fr.faylixe.ekite.backup.Event;
import fr.faylixe.ekite.backup.EventSender;

/**
 * 
 * @author fv
 */
public final class PartListener implements IPartListener2 {

	/** **/
	private final Event source;

	/** **/
	private SelectionListener currentSelectionListener;

	/** **/
	private IEditorInput currentEditorInput;

	/**
	 * 
	 * @param source
	 */
	public PartListener(final Event source) {
		this.source = source;
	}

	/**
	 * 
	 * @return
	 */
	public String getCurrentFilename() {
		final IFile file = (IFile) currentEditorInput.getAdapter(IFile.class);
		if (file == null) {
			
		}
		return null;
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
		configure(partRef);
	}

	/** {@inheritDoc} **/
	@Override
	public void partHidden(final IWorkbenchPartReference partRef) {
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void partVisible(final IWorkbenchPartReference partRef) {
		configure(partRef);
	}

	/** {@inheritDoc} **/
	@Override
	public void partInputChanged(final IWorkbenchPartReference partRef) {
		configure(partRef);
	}

	/**
	 * 
	 * @param partRef
	 */
	private void configure(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);
		if (part != null && part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) part;
			final IEditorInput input = editor.getEditorInput();
			if (input != null) {
				currentEditorInput = input;
				final Event fileEvent = source.forFile(input.getName());
				try {
					EventSender.get().send(fileEvent.toFocusEvent());
				}
				catch (final IOException e) {
					e.printStackTrace();
				}
				configureSelectionListener(editor, fileEvent);
	//			final IDocumentProvider documentProvider = editor.getDocumentProvider();
	//			final IDocument document = documentProvider.getDocument(editor.getEditorInput());
	//			document.addDocumentListener(null);
			}
		}
	}

	/**
	 * 
	 * @param editor
	 * @param event
	 */
	private void configureSelectionListener(final ITextEditor editor, final Event event) {
		final ISelectionProvider provider = editor.getSelectionProvider();
		if (currentSelectionListener != null) {
			provider.removeSelectionChangedListener(currentSelectionListener);
		}
		currentSelectionListener = new SelectionListener(event);
		provider.addSelectionChangedListener(currentSelectionListener);
		
	}
}
