package fr.faylixe.ekite.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * 
 * @author fv
 */
public final class EventManager {

	/** Kite server hostname. **/
	private static final String HOSTNAME = "127.0.0.1";

	/** Kite server port. **/
	private static final int PORT = 46625;

	/** 2 MB buffer size. **/
	private static final int BUFFER_SIZE = 2 << 20;

	/** Target socket to send data to. **/
	private final DatagramSocket socket;

	/** **/
	private final String pluginId;

	/**
	 * 
	 * @param socket
	 * @param pluginId
	 */
	public EventManager(final DatagramSocket socket, final String pluginId) {
		this.socket = socket;
		this.pluginId = pluginId;
	}

	/**
	 * 
	 * @param action
	 * @param filename
	 * @param text
	 * @param start
	 * @param end
	 * @throws Exception
	 */
	public void sendEvent(
			final String action,
			final String filename,
			final String text,
			final int start,
			final int end) throws Exception {
//		final SelectionEvent event = new SelectionEvent();
//		event.action = action;
//		event.filename = filename;
//		event.text = text;
//		event.pluginId = pluginId;
//
//		final OutboundEventSelectionRange selRange = new OutboundEventSelectionRange();
//		selRange.start = start;
//		selRange.end = end;
//		event.selections = new ArrayList<OutboundEventSelectionRange>();
//		event.selections.add(selRange);
//		if (text.length() > 1024 * 1024) {
//			event.action = "skip";
//			event.text = "file_too_large";
//		}
//		send(new Gson().toJson(event));
	}

	/**
	 * 
	 * @param message
	 * @param filename
	 * @param userBuffer
	 * @param userMD5
	 * @param expectedBuffer
	 * @param expectedMD5
	 * @param suggestion
	 * @throws Exception
	 */
//	public void sendSuggestionError(
//			final String message,
//			final String filename,
//			final String userBuffer,
//			final String userMD5,
//			final String expectedBuffer,
//			final String expectedMD5,
//			final Suggestion suggestion) throws Exception {
//		final OutboundSuggestionErrorDetails text = new OutboundSuggestionErrorDetails();
//		text.message = message;
//		text.user_buffer = userBuffer;
//		text.user_md5 = userMD5;
//		text.expected_md5 = expectedMD5;
//		text.expected_buffer = expectedBuffer;
//		text.suggestion = suggestion;
//		sendError(filename, new Gson().toJson(text));
//	}

	/**
	 * 
	 * @param filename
	 * @param text
	 * @throws Exception
	 */
	public void sendError(final String filename, final String text) throws Exception {
//		final OutboundErrorEvent event = new OutboundErrorEvent();
//		event.action = "error";
//		event.filename = filename;
//		event.text = text;
//		event.pluginId = pluginId;
//		send(new Gson().toJson(event));
	}

	/**
	 * 
	 * @param json
	 * @throws IOException
	 */
	private void send(final String json) throws IOException {
		try {
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
	 * Creates and returns an {@link EventManager} instance.
	 * 
	 * @return Created instance.
	 * @throws IOException If any error occurs while creating associated socket.
	 */
	public static EventManager create(final EventReceiver receiver) throws IOException {
		final DatagramSocket socket = new DatagramSocket();
		socket.setSendBufferSize(BUFFER_SIZE);
		return new EventManager(socket, "");
	}

}
