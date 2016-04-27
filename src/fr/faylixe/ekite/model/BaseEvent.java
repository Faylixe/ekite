package fr.faylixe.ekite.model;

import com.google.gson.annotations.SerializedName;

/**
 * {@link BaseEvent} is a simple POJO that
 * owns basic field for a event which are :
 * 
 * * The plugin identifier.
 * * The currently edited file name.
 * @author fv
 */
public class BaseEvent {

	/** Plugin identifier that send this event. **/
	@SerializedName("plugin_id")
	private final String pluginId;

	/** Name of the currently edited file. **/
	@SerializedName("filename")
	private final String filename;

	/**
	 * Default constructor.
	 * 
	 * @param pluginId Plugin identifier that send this event.
	 * @param filename Name of the currently edited file.
	 */
	protected BaseEvent(final String pluginId, final String filename) {
		this.pluginId = pluginId;
		this.filename = filename;
	}

}
