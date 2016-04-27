package fr.faylixe.ekite.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author fv
 */
public final class Suggestion extends BaseEvent {

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
		super(pluginId, filename);
		this.type = type;
		this.score = score;
		this.md5 = md5;
		this.base64 = base64;
		this.diffs = new ArrayList<Diff>();
	}
	
	/**
	 * Adds the given ``diff``to the diffs list.
	 * 
	 * @param diff Diff instance to add.
	 * @see List#add(Object)
	 */
	public void addDiff(final Diff diff) {
		diffs.add(diff);
	}

}
