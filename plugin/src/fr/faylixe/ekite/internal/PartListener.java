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
 * This listener registers {@link DocumentListener} and {@link SelectionListener}
 * for text editor that are notified.
 * 
 * @author fv
 */
public final class PartListener implements IPartListener2 {

	/** Event sender used by this listener to send event notification. **/
	private final EventSender sender;
	
	/** Document listener instance to use for registered editor. **/
	private final DocumentListener documentListener;

	/** Selection listener instance to use for registered editor. **/
	private final SelectionListener selectionListener;

	/**
	 * Default constructor.
	 * 
	 * @param sender Event sender instance to use.
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
		// Do nothing.
	}

	/** {@inheritDoc} **/
	@Override
	public void partDeactivated(final IWorkbenchPartReference partRef) {
		unconfigure(partRef);
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
		configure(partRef);
	}

	/**
	 * Adds listeners to the editor denoted by the 
	 * given <tt>partRef</tt>.
	 * 
	 * @param partRef Editor reference to register listener to.
	 */
	public void configure(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);
		configure(part);
	}
	
	/**
	 * Adds listeners to the editor denoted by the 
	 * given <tt>partRef</tt>.
	 * 
	 * @param part Editor to register listener to.
	 */
	public void configure(final IWorkbenchPart part) {
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
	 * Removes all previously registered listeners for the target
	 * document of the given <tt>partRef</tt>.
	 * 
	 * @param partRef Editor reference to unregister listener of.
	 */
	public void unconfigure(final IWorkbenchPartReference partRef) {
		final IWorkbenchPart part = partRef.getPart(false);
		unconfigure(part);
	}

	/**
	 * Removes all previously registered listeners for the target
	 * document of the given <tt>partRef</tt>.
	 * 
	 * @param partRef Editor reference to unregister listener of.
	 */
	public void unconfigure(final IWorkbenchPart part) {
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
	 * Retrieves a {@link StyledText} components
	 * from the given <tt>editor</tt>.
	 * 
	 * @param editor Editor to retrieve styled text from.
	 * @return Found instance if any, <tt>null</tt> otherwise.
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
	 * Configures the event sender using the
	 * currently edited file.
	 * 
	 * @param input Current editor input.
	 * @param document Current associated {@link IDocument} instance.
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
				if (EKitePlugin.DEBUG) {
					EKitePlugin.log("Focus on document : " + path);
				}
				sender.setCurrentFile(file); // TODO : Consider merging with filename extraction.
				sender.setCurrentDocument(document);
				sender.setCurrentFilename(path);
				sender.sendFocus();
			}
			catch (final IOException | IllegalStateException e) {
				EKitePlugin.log(e);
			}
		}
	}

}
