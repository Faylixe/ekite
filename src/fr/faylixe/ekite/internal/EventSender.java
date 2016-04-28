package fr.faylixe.ekite.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.eclipse.jface.text.IDocument;

import com.google.gson.Gson;

import fr.faylixe.ekite.model.ActionEvent.ErrorEvent;
import fr.faylixe.ekite.model.ActionEvent.FocusEvent;
import fr.faylixe.ekite.model.ActionEvent.LostFocusEvent;
import fr.faylixe.ekite.model.NotificationEvent.EditEvent;
import fr.faylixe.ekite.model.NotificationEvent.SelectionEvent;
import fr.faylixe.ekite.model.ActionEvent;
import fr.faylixe.ekite.model.Selection;

/**
 * 
 * @author fv
 */
public final class EventSender {

	/** **/
	private static final IllegalStateException NO_CURRENT_FILE = new IllegalStateException("");

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

	/** Plugin identifier to use for built event. **/
	private final String pluginId;

	/** Currently edited file name. **/
	private String currentFilename;

	/** **/
	private IDocument currentDocument;

	/**
	 * Default constructor.
	 * 
	 * @param socket
	 * @param pluginId Plugin identifier to use for built event.
	 */
	private EventSender(final DatagramSocket socket, final String pluginId) {
		this.socket = socket;
		this.pluginId = pluginId;
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
	 * 
	 * @param document
	 */
	public void setCurrentDocument(final IDocument document) {
		this.currentDocument = document;
	}

	/**
	 * 
	 * @param start
	 * @param end
	 * @throws IOException 
	 * @throws IllegalStateException
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
	 * 
	 * @param start
	 * @param end
	 * @throws IOException 
	 * @throws IllegalStateException
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
	 * 
	 * @param text
	 * @throws IOException 
	 * @throws IllegalStateException
	 */
	public void sendError(final String text) throws IOException {
		if (currentFilename == null) {
			throw NO_CURRENT_FILE;
		}
		final ErrorEvent event = new ErrorEvent(pluginId, currentFilename, text);
		sendEvent(event);
	}

	/**
	 * 
	 * @throws IOException 
	 * @throws IllegalStateException
	 */
	public void sendFocus() throws IOException {
		if (currentFilename == null) {
			throw NO_CURRENT_FILE;
		}
		final FocusEvent event = new FocusEvent(pluginId, currentFilename);
		sendEvent(event);
	}

	/**
	 * 
	 * @throws IOException 
	 * @throws IllegalStateException
	 */
	public void sendLostFocus() throws IOException {
		if (currentFilename == null) {
			throw NO_CURRENT_FILE;
		}
		final LostFocusEvent event = new LostFocusEvent(pluginId, currentFilename);
		sendEvent(event);
	}

	/**
	 * 
	 * @param json
	 * @throws IOException
	 */
	private void sendEvent(final ActionEvent event) throws IOException {
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
	 * @return Created instance.
	 * @throws IOException If any error occurs while creating associated socket.
	 */
	public static EventSender create(final EventReceiver receiver) throws IOException {
		final DatagramSocket socket = new DatagramSocket();
		socket.setSendBufferSize(BUFFER_SIZE);
		return new EventSender(socket, receiver.getPluginIdentifier());
	}

}
