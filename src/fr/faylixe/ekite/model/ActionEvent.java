package fr.faylixe.ekite.model;

import com.google.gson.annotations.SerializedName;

/**
 * TODO : Javadoc
 * 
 * @author fv
 */
public class ActionEvent extends BaseEvent {

	/** Name of the ``focus`` action.**/
	private static final String FOCUS = "focus";
	
	/** Name of the ``lost_focus`` action.**/
	private static final String LOST_FOCUS = "lost_focus";

	/** Name of the ``error`` action.**/
	private static final String ERROR = "error";
		
	/** Source editor that trigger this event. **/
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

	/** Shortcut for the ``focus`` action. **/
	public static final class FocusEvent extends ActionEvent {	
		
		/**
		 * Default constructor. 
		 * 
		 * @param pluginId Plugin identifier that send this event.
		 * @param filename Name of the currently edited file.
		 */
		public FocusEvent(
				final String pluginId,
				final String filename) {
			super(pluginId, filename, FOCUS, FOCUS);
		}
	
	}

	/** Shortcut for the ``lost_focus`` action. **/
	public static final class LostFocusEvent extends NotificationEvent {	
		
		/**
		 * Default constructor. 
		 * 
		 * @param pluginId Plugin identifier that send this event.
		 * @param filename Name of the currently edited file.
		 */
		public LostFocusEvent(
				final String pluginId,
				final String filename) {
			super(pluginId, filename, LOST_FOCUS, LOST_FOCUS);
		}
	
	}

	/** Shortcut for the ``error`` action. **/
	public static final class ErrorEvent extends NotificationEvent {	
		
		/**
		 * Default constructor. 
		 * 
		 * @param pluginId Plugin identifier that send this event.
		 * @param filename Name of the currently edited file.
		 * @param text Buffer content of the currently edited file.
		 */
		public ErrorEvent(
				final String pluginId,
				final String filename,
				final String text) {
			super(pluginId, filename, ERROR, text);
		}
	
	}
}
