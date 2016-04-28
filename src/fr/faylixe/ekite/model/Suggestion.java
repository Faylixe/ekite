package fr.faylixe.ekite.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author fv
 */
public final class Suggestion {

	/** Apply suggestion type. **/
	private static final String APPLY = "apply";

	/** Highlight suggestion type. **/
	private static final String HIGHLIGHT = "highlight";

	/** Clear suggestion type. **/
	private static final String CLEAR = "clear";

	/** Plugin identifier that send this event. **/
	@SerializedName("plugin_id")
	private final String pluginId;

	/** Name of the currently edited file. **/
	@SerializedName("filename")
	private final String filename;

	/** Suggestion type. **/
	@SerializedName("type")
	private final String type;
	
	/** Suggestion score (used by Kite backend). **/
	@SerializedName("score")
	private final double score;

	/**
	 * MD5 of the file as it was when this
	 * suggestion was computed in lowercase
	 * hex string.
	 */
	@SerializedName("file_md5")
	private final String md5;
	
	/**
	 * Base64 encoded version of file when
	 * suggestion was computed.
	 */
	@SerializedName("file_base64")
	private final String base64;

	/** List of diffs this suggestion is proposing. **/
	@SerializedName("diffs")
	private final List<Diff> diffs;

	/**
	 * Default constructor.
	 * 
	 * @param pluginId Plugin identifier that send this event.
	 * @param filename Name of the currently edited file.
	 * @param type Suggestion type.
	 * @param score Suggestion score.
	 * @param md5 MD5 of the file.
	 * @param base64 Base64 encoded version of file.
	 */
	public Suggestion(
			final String pluginId,
			final String filename,
			final String type,
			final double score,
			final String md5,
			final String base64) {
		this.pluginId = pluginId;
		this.filename = filename;
		this.type = type;
		this.score = score;
		this.md5 = md5;
		this.base64 = base64;
		this.diffs = new ArrayList<Diff>();
	}
	
	/**
	 * Indicates if this suggestion is an apply one.
	 * 
	 * @return <tt>true</tt> if this suggestion type is apply, <tt>false</tt> otherwise.
	 */
	public boolean isApply() {
		return APPLY.equals(type);
	}
	
	/**
	 * Indicates if this suggestion is a highlight one.
	 * 
	 * @return <tt>true</tt> if this suggestion type is highlight, <tt>false</tt> otherwise.
	 */
	public boolean isHighlight() {
		return HIGHLIGHT.equals(type);
	}

	/**
	 * Indicates if this suggestion is a clear one.
	 * 
	 * @return <tt>true</tt> if this suggestion type is clear, <tt>false</tt> otherwise.
	 */
	public boolean isClear() {
		return CLEAR.equals(type);
	}

	/**
	 * Diffs getter.
	 * 
	 * @return Unmodifiable view of the internal diffs list.
	 */
	public List<Diff> getDiffs() {
		return Collections.unmodifiableList(diffs);
	}

}
