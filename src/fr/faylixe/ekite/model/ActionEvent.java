package fr.faylixe.ekite.model;

import com.google.gson.annotations.SerializedName;

/**
 * An {@link ActionEvent}
 * 
 * @author fv
 */
public class ActionEvent extends BaseEvent {

	/** Name of the ``focus`` action.**/
	private static final String FOCUS = "focus";
	
	/** Name of the ``lost_focus`` action.**/
	private static final String LOST_FOCUS = "lost_focus";
		
	/** **/
	@SerializedName("source")
	private final String source;

	/** Name of the action performed. **/
	@SerializedName("action")
	private final String action;
	
	/** Buffer content of the currently edited file. **/
	@SerializedName("text")
	private final String text;

	/**
	 * Default constructor. 
	 * 
	 * @param pluginId Plugin identifier that send this event.
	 * @param filename Name of the currently edited file.
	 * @param action Name of the action performed.
	 * @param text Buffer content of the currently edited file.
	 */
	protected ActionEvent(
			final String pluginId,
			final String filename,
			final String action,
			final String text) {
		super(pluginId, filename);
		this.source = "eclipse"; // TODO : Externalize.
		this.action = action;
		this.text = text;
	}

	/** **/
	public static final class Focus extends ActionEvent {	
		
		/**
		 * Default constructor. 
		 * 
		 * @param pluginId Plugin identifier that send this event.
		 * @param filename Name of the currently edited file.
		 * @param text Buffer content of the currently edited file.
		 */
		public Focus(
				final String pluginId,
				final String filename,
				final String text) {
			super(pluginId, filename, FOCUS, text);
		}
	
	}

	/** **/
	public static final class LostFocus extends NotificationEvent {	
		
		/**
		 * Default constructor. 
		 * 
		 * @param pluginId Plugin identifier that send this event.
		 * @param filename Name of the currently edited file.
		 * @param text Buffer content of the currently edited file.
		 */
		public LostFocus(
				final String pluginId,
				final String filename,
				final String text) {
			super(pluginId, filename, LOST_FOCUS, text);
		}
	
	}

}
