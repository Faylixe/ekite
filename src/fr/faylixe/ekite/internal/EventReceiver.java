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
 * 
 * @author fv
 */
public final class EventReceiver implements Runnable {

	/** **/
	private static final String IDENTIFIER_PREFIX = "udp://127.0.0.1:";

	/** **/
	private static final String HOSTNAME = "127.0.0.1";

	/** **/
	private static final int BUFFER_SIZE = 10 * 1024 * 1024;

	/** **/
	private final DatagramSocket socket;

	/** **/
	private final Gson gson;

	/** **/
	private volatile boolean running;

	/**
	 * 
	 * @param socket
	 */
	private EventReceiver(final DatagramSocket socket) {
		this.socket = socket;		
		this.gson = new Gson();
	}

	/**
	 * 
	 * @return
	 */
	public String getPluginIdentifier() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(IDENTIFIER_PREFIX);
		buffer.append(socket.getLocalPort());
		return buffer.toString();
	}

	/**
	 * 
	 */
	public void start() {
		final Thread thread = new Thread(this);
		thread.start();
	}
 
	/**
	 * 
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
	 * 
	 * @return
	 * @throws IOException
	 */
	public static final EventReceiver create() throws IOException {
		final DatagramSocket socket = new DatagramSocket(0, InetAddress.getByName(HOSTNAME));
		return new EventReceiver(socket);
	}

}
