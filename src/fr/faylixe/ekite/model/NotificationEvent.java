package fr.faylixe.ekite.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * TODO : Javadoc.
 * 
 * @author fv
 */
public class NotificationEvent extends ActionEvent {

	/** Name of the ``edit`` action.**/
	private static final String EDIT = "edit";

	/** Name of the ``selection`` action.**/
	private static final String SELECTION = "selection";

	/** List of the selection over the currently edited file. . **/
	@SerializedName("selections")
	private List<Selection> selections;

	/**
	 * Default constructor.
	 * 
	 * @param pluginId Plugin identifier that send this event.
	 * @param filename Name of the currently edited file.
	 * @param action Name of the action performed.
	 * @param text Buffer content of the currently edited file.
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
	 * Adds the given ``selection`` to the selection list.
	 * 
	 * @param selection Selection to add.
	 * @see List#add(Object)
	 */
	public void addSelection(final Selection selection) {
		selections.add(selection);
	}

	/** Shortcut for the ``edit`` action. **/
	public static final class EditEvent extends NotificationEvent {	
		
		/**
		 * Default constructor. 
		 * 
		 * @param pluginId Plugin identifier that send this event.
		 * @param filename Name of the currently edited file.
		 * @param text Buffer content of the currently edited file.
		 */
		public EditEvent(
				final String pluginId,
				final String filename,
				final String text) {
			super(pluginId, filename, EDIT, text);
		}
	
	}
	
	/** Shortcut for the ``selection`` action. **/
	public static final class SelectionEvent extends NotificationEvent {	
		
		/**
		 * Default constructor. 
		 * 
		 * @param pluginId Plugin identifier that send this event.
		 * @param filename Name of the currently edited file.
		 * @param text Buffer content of the currently edited file.
		 */
		public SelectionEvent(
				final String pluginId,
				final String filename,
				final String text) {
			super(pluginId, filename, SELECTION, text);
		}
	
	}

}
