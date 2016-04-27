package fr.faylixe.ekite.backup;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import fr.faylixe.ekite.model.Selection;

/**
 * Represents a text buffer event which
 * could be either ``edit`` or ``selection``.
 * 
 * @author fv
 */
public final class Event {

	/** {@link Gson} instance. **/
	private static final Gson GSON = new Gson();

	/** Maximum allowed text length. **/
	private static final int MAX_LENGTH = (int) Math.pow(2, 20);

	/** Name for the EDIT action. **/
	private static final String EDIT = "edit";

	/** Name for the SELECTION action. **/
	private static final String SELECTION = "selection";

	/** Name for the FOCUS action. **/
	private static final String FOCUS = "focus";

	/** Name for the LOST_FOCUS action. **/
	private static final String LOST_FOCUS = "lost_focus";

	/** Name for SKIP action.**/
	private static final String SKIP = "skip";

	/** Text content for file too large event. **/
	private static final String TOO_LARGE = "file_too_large";

	/** Source editor. **/
	@SerializedName("source")
	private final String source;

	/** Event file name. **/
	@SerializedName("filename")
	private final String filename;

	/** Action name. **/
	@SerializedName("action")
	private final String action;

	/** Plugin identifier. **/
	@SerializedName("pluginId")
	private final String pluginId;

	/** Selections. **/
	@SerializedName("selections")
	private final List<Selection> selections;

	/** Text content. **/
	@SerializedName("text")
	private final String text;

	/** Event for too large file. **/
	private final transient Event tooLarge;

	/**
	 * Text less constructor.
	 * 
	 * @param source Source editor.
	 * @param action Action name.
	 */
	public Event(final String source) {
		this(source, null);
	}

	/**
	 * 
	 * @param source
	 * @param filename
	 */
	public Event(final String source, final String filename) {
		this(source, filename, null, null);
	}

	/**
	 * Too large event less constructor.
	 * 
	 * @param source Source editor.
	 * @param filename
	 * @param action Action name.
	 * @param text Text content.
	 */
	private Event(final String source, final String filename, final String action, final String text) {
		this(source, filename, action, text, new Event(source, filename, SKIP, TOO_LARGE, null));
	}
	
	/**
	 * Full constructor.
	 * 
	 * @param source Source editor.
	 * @param filename Name of the target file.
	 * @param action Action name.
	 * @param text Text content.
	 * @param tooLarge Event for too large file.
	 */
	private Event(final String source, final String filename, final String action, final String text, final Event tooLarge) {
		this.source = source;
		this.filename = filename;
		this.action = action;
		this.text = text;
		this.tooLarge = tooLarge;
		this.selections = new ArrayList<Selection>();
		this.pluginId = null;
	}


	/**
	 * Adds the given ``selection`` to this event.
	 * 
	 * @param selection Selection to add.
	 * @return Reference of this event.
	 */
	public Event withSelection(final Selection selection) {
		selections.add(selection);
		return this;
	}
	
	/**
	 * Removes the given ``selection`` of this event.
	 * 
	 * @param selection Selection to remove.
	 */
	public void removeSelection(final Selection selection) {
		selections.remove(selection);
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public Event forFile(final String filename) {
		return new Event(source, filename);
	}

	/**
	 * Created a copy of this event using the given
	 * ``text`` with no selection.
	 * 
	 * @param text New text for this event copy.
	 * @return Created event copy.
	 */
	public Event withText(final String text) {
		return new Event(source, filename, action, text, tooLarge);
	}

	/**
	 * Transforms this event into a ``edit``event.
	 * Created event won't have any selection.
	 * 
	 * @return Transformed event instance.
	 */
	public Event toEditEvent() {
		return new Event(source, filename, EDIT, text, tooLarge);
	}

	/**
	 * Transforms this event into a ``focus``event.
	 * Created event won't have any selection.
	 * 
	 * @return Transformed event instance.
	 */
	public Event toFocusEvent() {
		return new Event(source, filename, FOCUS, text, tooLarge);
	}

	/**
	 * Transforms this event into a ``lost_focus``event.
	 * Created event won't have any selection.
	 * 
	 * @return Transformed event instance.
	 */
	public Event toLostFocusEvent() {
		return new Event(source, filename, LOST_FOCUS, text, tooLarge);
	}
	
	/**
	 * Transforms this event into a ``selection``event.
	 * Created event won't have any selection.
	 * 
	 * @return Transformed event instance.
	 */
	public Event toSelectionEvent() {
		return new Event(source, filename, SELECTION, text, tooLarge);
	}

	/**
	 * Transforms this event in a JSON representation.
	 * If the event text length is too big (more than 2^20),
	 * the {@link #tooLarge} event JSON representation is
	 * returned instead.
	 * 
	 * @return Generated JSON according to **Kite** API.
	 */
	public String toJSON() {
		if (action == null) {
			throw new IllegalStateException("No action provided.");
		}
		if (text == null) {
			return withText("").toJSON();
		}
		if (text.length() > MAX_LENGTH) {
			return tooLarge.toJSON();
		}
		return GSON.toJson(this);
	}

}
