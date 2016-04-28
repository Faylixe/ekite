package fr.faylixe.ekite.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.gson.Gson;

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
	 */
	private EventReceiver(final DatagramSocket socket) {
		this.socket = socket;		
		this.gson = new Gson();
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
				socket.receive(packet);
				final InputStream stream = new ByteArrayInputStream(
						packet.getData(),
						packet.getOffset(),
						packet.getLength());
				try (final InputStreamReader reader = new InputStreamReader(stream)) {
					gson.fromJson(reader, null);					
				}
				// TODO : Consume suggestion.
			}
			catch (final IOException e) {
				
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
