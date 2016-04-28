package fr.faylixe.ekite.internal;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import fr.faylixe.ekite.EKitePlugin;

/**
 * 
 * @author fv
 */
public final class PartListener implements IPartListener2 {

	/** **/
	private final EventSender sender;
	
	/** **/
	private final DocumentListener documentListener;

	/** **/
	private final SelectionListener selectionListener;

	/**
	 * 
	 * @param sender
	 */
	public PartListener(final EventSender sender) {
		this.sender = sender;
		this.documentListener = new DocumentListener(sender);
		this.selectionListener = new SelectionListener(sender);
	}

	/** {@inheritDoc} **/
	@Override
	public void partActivated(final IWorkbenchPartReference partRef) {
		configure(partRef);
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
			final ISelectionProvider selectionProvider = editor.getSelectionProvider();
			final IDocumentProvider documentProvider = editor.getDocumentProvider();
			final IDocument document = documentProvider.getDocument(editor.getEditorInput());
			final IEditorInput input = editor.getEditorInput();
			if (input != null) {
				configureCurrentFile(input, document);
			}
			selectionProvider.addSelectionChangedListener(selectionListener);
			document.addDocumentListener(documentListener);
			final StyledText styledText = getStyledText(editor);
			if (styledText != null) {
				styledText.addCaretListener(selectionListener);
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
			final IDocumentProvider documentProvider = editor.getDocumentProvider();
			final IDocument document = documentProvider.getDocument(editor.getEditorInput());
			selectionProvider.removeSelectionChangedListener(selectionListener);
			document.removeDocumentListener(documentListener);
			final StyledText styledText = getStyledText(editor);
			if (styledText != null) {
				styledText.removeCaretListener(selectionListener);
			}
		}
	}

	/**
	 * 
	 * @param editor
	 * @return
	 */
	private StyledText getStyledText(final ITextEditor editor) {
		if (editor instanceof AbstractTextEditor) {
			final AbstractTextEditor textEditor = (AbstractTextEditor) editor;
			final Object adapter = textEditor.getAdapter(Control.class);
			if (adapter != null && adapter instanceof StyledText) {
				return (StyledText) adapter;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param input
	 * @param document
	 */
	private void configureCurrentFile(final IEditorInput input, final IDocument document) {
		final Object adapter = input.getAdapter(IFile.class);
		if (adapter != null) {
			final IFile file = (IFile) adapter;
			try {
				final String path = file
					.getRawLocation()
					.toFile()
					.getCanonicalPath();
				EKitePlugin.log("Focus on document : " + path);
				sender.setCurrentFilename(path);
				sender.setCurrentDocument(document);
				sender.sendFocus();
			}
			catch (final IOException | IllegalStateException e) {
				EKitePlugin.log(e);
			}
		}
	}

}
