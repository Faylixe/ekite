package fr.faylixe.ekite.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author fv
 */
public class NotificationEvent extends ActionEvent {

	/** Name of the ``edit`` action.**/
	private static final String EDIT = "edit";

	/** Name of the ``selection`` action.**/
	private static final String SELECTION = "selection";

	/** **/
	@SerializedName("selections")
	private List<Selection> selections;

	/**
	 * 
	 * @param pluginId
	 * @param filename
	 * @param action
	 * @param text
	 */
	protected NotificationEvent(
			final String pluginId,
			final String filename,
			final String action,
			final String text) {
		super(pluginId, filename, action, text);
		this.selections = new ArrayList<Selection>();
	}

	/**
	 * 
	 * @param selection
	 * @see List#add(Object)
	 */
	public void addSelection(final Selection selection) {
		selections.add(selection);
	}

	/** **/
	public static final class Edit extends NotificationEvent {	
		
		/**
		 * Default constructor. 
		 * 
		 * @param pluginId Plugin identifier that send this event.
		 * @param filename Name of the currently edited file.
		 * @param text Buffer content of the currently edited file.
		 */
		public Edit(
				final String pluginId,
				final String filename,
				final String text) {
			super(pluginId, filename, EDIT, text);
		}
	
	}
	
	/** **/
	public static final class Selection extends NotificationEvent {	
		
		/**
		 * Default constructor. 
		 * 
		 * @param pluginId Plugin identifier that send this event.
		 * @param filename Name of the currently edited file.
		 * @param text Buffer content of the currently edited file.
		 */
		public Selection(
				final String pluginId,
				final String filename,
				final String text) {
			super(pluginId, filename, SELECTION, text);
		}
	
	}

}
