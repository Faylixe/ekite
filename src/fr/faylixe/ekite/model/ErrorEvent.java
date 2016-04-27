package fr.faylixe.ekite.model;

/**
 * 
 * @author fv
 */
public final class ErrorEvent extends ActionEvent {

	/** Event action name. **/
	private static final String ACTION = "error";

	/**
	 * 
	 * @param pluginId
	 * @param filename
	 * @param text
	 */
	public ErrorEvent(final String pluginId, final String filename, final String text) {
		super(ACTION, filename, text, pluginId);
	}

}
