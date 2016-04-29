package fr.faylixe.ekite.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;

import com.google.gson.Gson;

import fr.faylixe.ekite.EKitePlugin;
import fr.faylixe.ekite.model.Suggestion;

/**
 * This class is in charge of receiving and handling
 * event notification sent from Kite.
 * 
 * @author fv
 */
public final class EventReceiver implements Runnable {

	/** Plugin identifier prefix used. **/
	private static final String IDENTIFIER_PREFIX = "udp://127.0.0.1:";

	/** Listened hostname. **/
	private static final String HOSTNAME = "127.0.0.1";

	/** Buffer size used for received packet (10MB). **/
	private static final int BUFFER_SIZE = 10 * 1024 * 1024;

	/** Consumer that processes received suggestion. **/
	private final SuggestionConsumer consumer;

	/** Server socket which listens for Kite event. **/
	private final DatagramSocket socket;

	/** Gson instance for transforming event to JSON. **/
	private final Gson gson;

	/** Boolean flag that indicates if the receiver is running or not. **/
	private volatile boolean running;

	/**
	 * Default constructor.
	 * 
	 * @param socket Server socket that listens for Kite event.
	 * @param display Display instance to use for live operation.
	 */
	private EventReceiver(final DatagramSocket socket, final Display display, final boolean showHighlight) {
		this.socket = socket;
		this.gson = new Gson();
		this.consumer = new SuggestionConsumer(display, showHighlight);
	}

	/**
	 * Current file setter.
	 * 
	 * @param file Current file to set.
	 */
	protected void setCurrentFile(final IFile file) {
		consumer.setCurrentFile(file);
	}

	/**
	 * Current document setter.
	 * 
	 * @param document Currently edited document.
	 */
	protected void setCurrentDocument(final IDocument document) {
		consumer.setCurrentDocument(document);
	}

	/**
	 * Show highlight flag setter.
	 * 
	 * @param showHighlight <tt>true</tt> if highlight should be shown, <tt>false</tt> otherwise.
	 */
	public void setShowHighlight(final boolean showHighlight) {
		consumer.setShowHighlight(showHighlight);
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
		consumer.start();
		final Thread thread = new Thread(this);
		thread.start();
	}
 
	/**
	 * Stops the currently running thread
	 * by setting the running flag to <tt>false</tt>.
	 * 
	 * @throws IOException 
	 */
	public void shutdown() throws IOException {
		running = false;
		final DatagramSocket signalSocket = new DatagramSocket();
		final DatagramPacket shutdownPacket = new DatagramPacket(new byte[0], 0, socket.getLocalAddress(), socket.getLocalPort());
		signalSocket.send(shutdownPacket);
		signalSocket.close();
		if (EKitePlugin.DEBUG) {
			EKitePlugin.log("Shutdown signal sent to local receiver");
		}
		consumer.shutdown();
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
				if (packet.getLength() != 0) {
					final InputStream stream = new ByteArrayInputStream(
							packet.getData(),
							packet.getOffset(),
							packet.getLength());
					try (final InputStreamReader reader = new InputStreamReader(stream)) {
						final Suggestion suggestion = gson.fromJson(reader, Suggestion.class);
						if (EKitePlugin.DEBUG) {
							EKitePlugin.log("Suggestion received : " + gson.toJson(suggestion));
						}
						consumer.accept(suggestion);
					}
				}
			}
			catch (final IOException e) {
				EKitePlugin.log(e);
			}
		}
		socket.close();
		if (EKitePlugin.DEBUG) {
			EKitePlugin.log("Receiver stopped");
		}
	}

	/**
	 * Creates and returns an {@link EventReceiver} instance.
	 * 
	 * @param display Display instance to use for live operation.
	 * @param showHighlight showHighlight <tt>true</tt> if highlight should be shown, <tt>false</tt> otherwise.
	 * @return Created instance.
	 * @throws IOException If any error occurs while creating associated socket.
	 */
	public static final EventReceiver create(final Display display, final boolean showHighlight) throws IOException {
		final DatagramSocket socket = new DatagramSocket(0, InetAddress.getByName(HOSTNAME));
		return new EventReceiver(socket, display, showHighlight);
	}

}
