package fr.faylixe.ekite.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author fv
 */
public final class Suggestion extends BaseEvent {

	/** **/
	@SerializedName("type")
	private final String type;
	
	/** **/
	@SerializedName("score")
	private final double score;

	/** **/
	@SerializedName("file_md5")
	private final String md5;
	
	/** **/
	@SerializedName("file_base64")
	private final String base64;

	/** **/
	@SerializedName("diffs")
	private final List<Diff> diffs;

	/**
	 * 
	 * @param pluginId
	 * @param filename
	 * @param type
	 * @param score
	 * @param md5
	 * @param base64
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
	 * 
	 * @param diff
	 * @see List#add(Object)
	 */
	public void addDiff(final Diff diff) {
		diffs.add(diff);
	}

}
