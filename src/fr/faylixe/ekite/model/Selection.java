package fr.faylixe.ekite.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents text selection.
 * 
 * @author fv
 */
public final class Selection {

	/** Selection start index. **/
	@SerializedName("start")
	private final int start;

	/** Selection end index. **/
	@SerializedName("end")
	private final int end;

	/** Pre computed selection hash code. **/
	private final transient int hashcode;

	/**
	 * Default constructor.
	 * 
	 * @param start Start point.
	 * @param end End point.
	 */
	public Selection(final int start, final int end) {
		this.start = start;
		this.end = end;
		this.hashcode = 31 * (start ^ (start >>> 32)) + (end ^(end >>> 32));
	}

	/** {@inheritDoc} **/
	@Override
	public int hashCode() {
		return hashcode;
	}

}
