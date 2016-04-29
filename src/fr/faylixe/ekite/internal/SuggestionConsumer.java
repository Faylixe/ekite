package fr.faylixe.ekite.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;

import fr.faylixe.ekite.EKitePlugin;
import fr.faylixe.ekite.model.Diff;
import fr.faylixe.ekite.model.Suggestion;

/**
 * Consumer instance that is in charge of processing
 * {@link Suggestion} using a delayed queue.
 * 
 * @author fv
 */
public final class SuggestionConsumer implements Runnable {

	/** Custom marker id. **/
	private static final String MARKER_ID = "fr.faylixe.ekite.marker";
	
	/** 2 seconds delay between empty processing. **/
	private static final long TIMEOUT = 5000;

	/** Default capacity for the queue. **/
	private static final int QUEUE_CAPACITY = 100;

	/** Pending suggestion queue. **/
	private final BlockingQueue<Suggestion> queue;

	/** List of markers that are currently active. **/
	private final List<IMarker> markers;

	/** Display instance to use for live operation. **/
	private final Display display;
	
	/** Internal lock for synchronization. **/
	private final Object lock;

	/** Boolean flag that indicates if the receiver is running or not. **/
	private volatile boolean running;

	/** Boolean flag that indicates if the highlight should be shown or not. **/
	private volatile boolean showHighlight;

	/** Currently edited document model. **/
	private IDocument currentDocument;

	/** Currently edited file. **/
	private IFile currentFile;

	/**
	 * Default constructor. 
	 *
	 * @param display Display instance to use for live operation.
	 * @param showHighlight Indicates if the highlight should be shown or not.
	 */
	public SuggestionConsumer(final Display display, final boolean showHighlight) {
		this.display = display;
		this.lock = new Object();
		this.running = true;
		this.queue = new ArrayBlockingQueue<Suggestion>(QUEUE_CAPACITY);
		this.markers = new ArrayList<IMarker>();
		this.showHighlight = showHighlight;
	}

	/**
	 * Current file setter.
	 * 
	 * @param file Current file to set.
	 */
	protected void setCurrentFile(final IFile file) {
		this.currentFile = file;
	}

	/**
	 * Current document setter.
	 * 
	 * @param document Currently edited document.
	 */
	protected void setCurrentDocument(final IDocument document) {
		this.currentDocument = document;
	}
	
	/**
	 * Show highlight flag setter.
	 * 
	 * @param showHighlight <tt>true</tt> if highlight should be shown, <tt>false</tt> otherwise.
	 */
	protected void setShowHighlight(final boolean showHighlight) {
		this.showHighlight = showHighlight;
	}

	/**
	 * Adds the given <tt>suggestion</tt>
	 * to the processing queue.
	 * 
	 * @param suggestion Suggestion to process.
	 */
	public void accept(final Suggestion suggestion) {
		try {
			queue.put(suggestion);
			synchronized (lock) {
				lock.notify();				
			}
		}
		catch (final InterruptedException e) {
			EKitePlugin.log(e);
		}
	}

	/**
	 * Starts a new Thread over this {@link Runnable}.
	 */
	public void start() {
		final Thread thread = new Thread(this);
		thread.start();
	}
 
	/**
	 * Stops the currently running thread
	 * by setting the running flag to <tt>false</tt>.
	 */
	public void shutdown() {
		running = false;
	}

	/** {@inheritDoc} **/
	@Override
	public void run() {
		while (running) {
			try {
				synchronized (lock) {
					lock.wait(TIMEOUT);					
				}
			}
			catch (final InterruptedException e) {
				EKitePlugin.log(e);
			}
			final Suggestion suggestion = queue.poll();
			if (suggestion != null) {
				if (currentDocument == null) {
					EKitePlugin.log("Suggest to null document, abort");
					return;
				}
				if (suggestion.isApply()) {
					apply(suggestion);
				}
				else if (suggestion.isHighlight()) {
					highlight(suggestion);
				}
				else if (suggestion.isClear()) {
					clear();
				}
			}
		}
		if (EKitePlugin.DEBUG) {
			EKitePlugin.log("Suggestion consumer stopped");
		}
	}

	/**
	 * Applies all diffs of the given <tt>suggestion</tt>
	 * to the currently edited document.
	 * 
	 * @param suggestion Suggestion to apply.
	 */
	private void apply(final Suggestion suggestion) {
		if (EKitePlugin.DEBUG) {
			EKitePlugin.log("Handle apply suggestion.");
		}
		final AtomicInteger padding = new AtomicInteger(0);
		for (final Diff diff : suggestion.getDiffs()) {
			if (EKitePlugin.DEBUG) {
				EKitePlugin.log("Apply diff : " + diff.getDestination());
			}
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						currentDocument.replace(diff.getBegin() + padding.get(), diff.getLength() + padding.get(), diff.getDestination());
					}
					catch (final BadLocationException e) {
						EKitePlugin.log(e);
					}
				}
			}); 
			padding.set(padding.get() + (diff.getDestination().length() - diff.getSource().length()));
		}
		clear();
	}

	/**
	 * Highlight all diffs of the given <tt>suggestion</tt>
	 * to the currently edited document.
	 * 
	 * @param suggestion Suggestion to apply.
	 */
	private void highlight(final Suggestion suggestion) {
		if (!showHighlight) {
			return;
		}
		if (currentFile == null) {
			EKitePlugin.log("Highlight through null file, abort.");
			return;
		}
		if (EKitePlugin.DEBUG) {
			EKitePlugin.log("Performing highlight");
		}
		clear();
		for (final Diff diff : suggestion.getDiffs()) {
			try {
				final IMarker marker = currentFile.createMarker(MARKER_ID);
				marker.setAttribute(IMarker.MESSAGE, diff.getType());
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
				marker.setAttribute(IMarker.LINE_NUMBER, diff.getLineNumber());
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				markers.add(marker);
			}
			catch (final CoreException e) {
				EKitePlugin.log(e);
			}
		}
	}


	/**
	 * Clear all hightlight diffs of the given <tt>suggestion</tt>
	 * to the currently edited document.
	 */
	private void clear() {
		try {
			currentFile.deleteMarkers(MARKER_ID, true, IResource.DEPTH_INFINITE);
		}
		catch (final CoreException e) {
			EKitePlugin.log(e);
		}
	}

}
