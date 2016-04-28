package fr.faylixe.ekite.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.google.gson.Gson;

import fr.faylixe.ekite.EKitePlugin;
import fr.faylixe.ekite.model.Diff;
import fr.faylixe.ekite.model.Suggestion;

/**
 * This class is in charge of receiving and handling
 * event notification sent from Kite.
 * 
 * @author fv
 */
public final class EventReceiver implements Runnable {

	/** Custom marker id. **/
	private static final String MARKER_ID = "fr.faylixe.ekite.marker";

	/** Plugin identifier prefix used. **/
	private static final String IDENTIFIER_PREFIX = "udp://127.0.0.1:";

	/** Listened hostname. **/
	private static final String HOSTNAME = "127.0.0.1";

	/** Buffer size used for received packet (10MB). **/
	private static final int BUFFER_SIZE = 10 * 1024 * 1024;

	/** List of markers that are currently active. **/
	private final List<IMarker> markers;

	/** Server socket which listens for Kite event. **/
	private final DatagramSocket socket;

	/** Gson instance for transforming event to JSON. **/
	private final Gson gson;

	/** Boolean flag that indicates if the receiver is running or not. **/
	private volatile boolean running;

	/** Currently edited document model. **/
	private IDocument currentDocument;

	/** Currently edited file. **/
	private IFile currentFile;

	/**
	 * Default constructor.
	 * 
	 * @param socket Server socket that listens for Kite event.
	 */
	private EventReceiver(final DatagramSocket socket) {
		this.socket = socket;
		this.markers = new ArrayList<IMarker>();
		this.gson = new Gson();
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
	 * Builds and returns the plugin identifier
	 * for the current server socket.
	 * 
	 * @return Built identifier.
	 */
	public String getPluginIdentifier() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(IDENTIFIER_PREFIX);
		buffer.append(socket.getLocalPort());
		return buffer.toString();
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
		running = true;
		final byte [] buffer = new byte[BUFFER_SIZE];
		while (running) {
			final DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
			try {
				if (EKitePlugin.DEBUG) {
					EKitePlugin.log("Waiting for Kite notification");
				}
				socket.receive(packet);
				if (EKitePlugin.DEBUG) {
					EKitePlugin.log("Received Kite notification : " + packet.getLength());
				}
				final InputStream stream = new ByteArrayInputStream(
						packet.getData(),
						packet.getOffset(),
						packet.getLength());
				try (final InputStreamReader reader = new InputStreamReader(stream)) {
					final Suggestion suggestion = gson.fromJson(reader, Suggestion.class);					
					if (EKitePlugin.DEBUG) {
						EKitePlugin.log("Suggestion received");
					}
					handleSuggestion(suggestion);
				}
			}
			catch (final IOException e) {
				EKitePlugin.log(e);
			}
		}
	}

	/**
	 * Handles the given <tt>suggestion</tt> regarding
	 * of it type.
	 * 
	 * @param suggestion Suggestion to handle.
	 */
	private void handleSuggestion(final Suggestion suggestion) {
		if (currentDocument == null) {
			EKitePlugin.log("Suggest to null document");
			return;
		}
		if (suggestion.isApply()) {
			apply(suggestion);
		}
		else if (suggestion.isHighlight()) {
			highlight(suggestion);
		}
		else if (suggestion.isClear()) {
			clear(suggestion);
		}
	}

	/**
	 * Applies all diffs of the given <tt>suggestion</tt>
	 * to the currently edited document.
	 * 
	 * @param suggestion Suggestion to apply.
	 */
	private void apply(final Suggestion suggestion) {
		for (final Diff diff : suggestion.getDiffs()) {
			try {
				if (EKitePlugin.DEBUG) {
					EKitePlugin.log("Apply diff : " + diff.getDestination());
				}
				currentDocument.replace(diff.getBegin(), diff.getLength(), diff.getDestination());
			}
			catch (final BadLocationException e) {
				EKitePlugin.log(e);
			}
		}
		clear(null);
	}

	/**
	 * Highlight all diffs of the given <tt>suggestion</tt>
	 * to the currently edited document.
	 * 
	 * @param suggestion Suggestion to apply.
	 */
	private void highlight(final Suggestion suggestion) {
		for (final Diff diff : suggestion.getDiffs()) {
			try {
				final IMarker marker = currentFile.createMarker(MARKER_ID);
				marker.setAttribute(IMarker.CHAR_START, diff.getBegin());
				marker.setAttribute(IMarker.CHAR_END, diff.getEnd());
			}
			catch (final CoreException e) {
				EKitePlugin.log(e);
			}
		}
	}

	/**
	 * Clear all hightlight diffs of the given <tt>suggestion</tt>
	 * to the currently edited document.
	 * 
	 * @param suggestion Suggestion to apply.
	 */
	private void clear(final Suggestion suggestion) {
		for (int i = 0; i < markers.size(); i++) {
			final IMarker marker = markers.get(i);
			try {
				marker.delete();
				markers.remove(i);
			}
			catch (final CoreException e) {
				EKitePlugin.log(e);
			}
		}
	}

	/**
	 * Creates and returns an {@link EventReceiver} instance.
	 * 
	 * @return Created instance.
	 * @throws IOException If any error occurs while creating associated socket.
	 */
	public static final EventReceiver create() throws IOException {
		final DatagramSocket socket = new DatagramSocket(0, InetAddress.getByName(HOSTNAME));
		return new EventReceiver(socket);
	}

}
