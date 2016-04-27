package fr.faylixe.ekite.internal;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 
 * @author fv
 */
public final class PartListener implements IPartListener2 {

	/** **/
	private final EventSender sender;

	/** **/
	private SelectionListener currentSelectionListener;

	/**
	 * 
	 * @param source
	 */
	public PartListener(final EventSender sender) {
		this.sender = sender;
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
		unconfigure(partRef);
	}

	/** {@inheritDoc} **/
	@Override
	public void partDeactivated(final IWorkbenchPartReference partRef) {
		unconfigure(partRef);
	}

	/** {@inheritDoc} **/
	@Override
	public void partOpened(final IWorkbenchPartReference partRef) {
		configure(partRef);
	}

	/** {@inheritDoc} **/
	@Override
	public void partHidden(final IWorkbenchPartReference partRef) {
		unconfigure(partRef);
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
				configureCurrentFile(input);
				configureSelectionListener(editor);
	//			final IDocumentProvider documentProvider = editor.getDocumentProvider();
	//			final IDocument document = documentProvider.getDocument(editor.getEditorInput());
	//			document.addDocumentListener(null);
			}
		}
	}
	
	/**
	 * 
	 * @param partRef
	 */
	private void unconfigure(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);
		if (part != null && part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) part;
			final ISelectionProvider selectionProvider = editor.getSelectionProvider();
			selectionProvider.removeSelectionChangedListener(currentSelectionListener);
			currentSelectionListener = null;
		}
	}

	/**
	 * 
	 * @param input
	 */
	private void configureCurrentFile(final IEditorInput input) {
		final Object adapter = input.getAdapter(IFile.class);
		if (adapter != null) {
			final IFile file = (IFile) adapter;
			try {
				final String path = file
					.getRawLocation()
					.toFile()
					.getCanonicalPath();
				sender.setCurrentFilename(path);
				sender.sendFocus();
			}
			catch (final IOException e) {
				// TODO : Handle error properly.
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param editor
	 * @param event
	 */
	private void configureSelectionListener(final ITextEditor editor) {
		final ISelectionProvider provider = editor.getSelectionProvider();
		if (currentSelectionListener != null) {
			provider.removeSelectionChangedListener(currentSelectionListener);
		}
		currentSelectionListener = new SelectionListener(sender);
		provider.addSelectionChangedListener(currentSelectionListener);
		
	}
}
