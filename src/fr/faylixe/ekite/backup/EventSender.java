package fr.faylixe.ekite.backup;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * An {@link EventSender} allows to send
 * given {@link Event} to the target Kite receiver
 * socket using JSON format.
 * 
 * @author fv
 */
public final class EventSender implements Closeable {

	/** Kite server hostname. **/
	private static final String HOSTNAME = "127.0.0.1";
  
	/** Kite server port. **/
	private static final int PORT = 46625;
   
	/** 2 MB buffer size. **/
	private static final int BUFFER_SIZE = 2<<20;

	/** Target socket to send data to. **/
	private final DatagramSocket socket;

	/**
	 * Default constructor.
	 * 
	 * @param socket Target socket to send data to.
	 */
	private EventSender(final DatagramSocket socket) {
		this.socket = socket;
	}

	/**
	 * Sends the given ``event`` to the target
	 * socket.
	 * 
	 * @param event Event to send.
	 * @throws IOException If any error occurs while sending to kite.
	 */
	public void send(final Event event) throws IOException {
		try {
			final String json = event.toJSON();
			final byte [] bytes = json.getBytes();
			final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(HOSTNAME), PORT);
			socket.send(packet);
		}
		catch (final IllegalStateException e) {
			throw new IOException(e);
		}
	}

	/** {@inheritDoc} **/
	@Override
	public void close() {
		socket.close();
	}
	
	/** Unique instance. **/
	private static EventSender instance;

	/**
	 * Creates and returns a {@link EventSender} instance.
	 * 
	 * @return Created instance.
	 * @throws IOException If any error occurs while creating associated socket.
	 */
	public static EventSender get() throws IOException {
		synchronized (EventSender.class) {
			if (instance == null) {
				final DatagramSocket socket = new DatagramSocket();
				socket.setSendBufferSize(BUFFER_SIZE);
				instance = new EventSender(socket);
			}
		}
		return instance;
	}

}
