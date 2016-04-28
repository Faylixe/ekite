package fr.faylixe.ekite.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.eclipse.jface.text.IDocument;

import com.google.gson.Gson;

import fr.faylixe.ekite.EKitePlugin;
import fr.faylixe.ekite.model.ActionEvent.ErrorEvent;
import fr.faylixe.ekite.model.ActionEvent.FocusEvent;
import fr.faylixe.ekite.model.ActionEvent.LostFocusEvent;
import fr.faylixe.ekite.model.NotificationEvent.EditEvent;
import fr.faylixe.ekite.model.NotificationEvent.SelectionEvent;
import fr.faylixe.ekite.model.ActionEvent;
import fr.faylixe.ekite.model.Selection;

/**
 * This class is in charge of dispatching event
 * to Kite using UDP socket.
 * 
 * @author fv
 */
public final class EventSender {

	/** Exception thrown when no file name and document is set. **/
	private static final IllegalStateException NO_CURRENT_FILE = new IllegalStateException("No current file / document settled.");

	/** Kite server hostname. **/
	private static final String HOSTNAME = "127.0.0.1";

	/** Kite server port. **/
	private static final int PORT = 46625;

	/** 2 MB buffer size. **/
	private static final int BUFFER_SIZE = 2 << 20;

	/** Gson instance for transforming event to JSON. **/
	private final Gson gson;

	/** Target socket to send data to. **/
	private final DatagramSocket socket;

	/** **/
	private final EventReceiver receiver;

	/** Plugin identifier to use for built event. **/
	private final String pluginId;

	/** Currently edited file name. **/
	private String currentFilename;

	/** Currently edited document model. **/
	private IDocument currentDocument;

	/**
	 * Default constructor.
	 * 
	 * @param socket
	 * @param receiver
	 */
	private EventSender(final DatagramSocket socket, final EventReceiver receiver) {
		this.socket = socket;
		this.receiver = receiver;
		this.pluginId = receiver.getPluginIdentifier();
		if (EKitePlugin.DEBUG) {
			EKitePlugin.log("Plugin identifier : " + pluginId);
		}
		this.gson = new Gson();
	}

	/**
	 * Current filename setter.
	 * 
	 * @param filename Name of the currently edited file.
	 */
	public void setCurrentFilename(final String filename) {
		this.currentFilename = filename;
	}
	
	/**
	 * Current document setter.
	 * 
	 * @param document Currently edited document.
	 */
	public void setCurrentDocument(final IDocument document) {
		this.currentDocument = document;
		receiver.setCurrentDocument(document);
	}

	/**
	 * Sends an <tt>selection</tt> event to Kite.
	 * 
	 * @param start Start position of the current edition.
	 * @param end End position of the current edition.
	 * 
	 * @throws IOException If any error occurs while sending the event.
	 * @throws IllegalStateException If no current file or current document name is settled.
	 */
	public void sendSelection(final int start, final int end) throws IOException {
		if (currentFilename == null || currentDocument == null) {
			throw NO_CURRENT_FILE;
		}
		final String text = currentDocument.get();
		final SelectionEvent event = new SelectionEvent(pluginId, currentFilename, text);
		final Selection selection = new Selection(start, end);
		event.addSelection(selection);
		sendEvent(event);
	}

	/**
	 * Sends an <tt>edit</tt> event to Kite.
	 * 
	 * @param start Start position of the current edition.
	 * @param end End position of the current edition.
	 * 
	 * @throws IOException If any error occurs while sending the event.
	 * @throws IllegalStateException If no current file or current document name is settled.
	 */
	public void sendEdit(final int start, final int end) throws IOException {
		if (currentFilename == null || currentDocument == null) {
			throw NO_CURRENT_FILE;
		}
		final String text = currentDocument.get();
		final EditEvent event = new EditEvent(pluginId, currentFilename, text);
		final Selection selection = new Selection(start, end);
		event.addSelection(selection);
		sendEvent(event);
	}
	
	/**
	 * Sends an <tt>error</tt> event to Kite.
	 * 
	 * @throws IOException If any error occurs while sending the event.
	 * @throws IllegalStateException If no current file name is settled.
	 */
	public void sendError(final String text) throws IOException {
		if (currentFilename == null) {
			throw NO_CURRENT_FILE;
		}
		final ErrorEvent event = new ErrorEvent(pluginId, currentFilename, text);
		sendEvent(event);
	}

	/**
	 * Sends a <tt>focus</tt> event to Kite.
	 * 
	 * @throws IOException If any error occurs while sending the event.
	 * @throws IllegalStateException If no current file name is settled.
	 */
	public void sendFocus() throws IOException {
		if (currentFilename == null) {
			throw NO_CURRENT_FILE;
		}
		final FocusEvent event;
		if (currentDocument == null) {
			event = new FocusEvent(pluginId, currentFilename);
		}
		else {
			event = new FocusEvent(pluginId, currentFilename, currentDocument.get());
		}
		sendEvent(event);
	}

	/**
	 * Sends a <tt>lost_focus</tt> event to Kite.
	 * 
	 * @throws IOException If any error occurs while sending the event.
	 * @throws IllegalStateException If no current file name is settled.
	 */
	public void sendLostFocus() throws IOException {
		if (currentFilename == null) {
			throw NO_CURRENT_FILE;
		}
		final LostFocusEvent event = new LostFocusEvent(pluginId, currentFilename);
		sendEvent(event);
	}

	/**
	 * Sends the given <tt>event</tt> to Kite as
	 * a JSON blob to the target UDP socket.
	 * 
	 * @param event Event to send.
	 * @throws IOException If any error occurs while sending the event.
	 */
	private synchronized void sendEvent(final ActionEvent event) throws IOException {
		try {
			final String json = gson.toJson(event);
			final byte[] bytes = json.getBytes();
			final DatagramPacket packet = new DatagramPacket(bytes,
					bytes.length,
					InetAddress.getByName(HOSTNAME),
					PORT);
			socket.send(packet);
		}
		catch (final IllegalStateException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Creates and returns an {@link EventSender} instance.
	 * 
	 * @param receiver Receiver instance this sender will be bound to.
	 * @return Created instance.
	 * @throws IOException If any error occurs while creating associated socket.
	 */
	public static EventSender create(final EventReceiver receiver) throws IOException {
		final DatagramSocket socket = new DatagramSocket();
		socket.setSendBufferSize(BUFFER_SIZE);
		return new EventSender(socket, receiver);
	}

}
