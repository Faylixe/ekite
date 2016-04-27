package fr.faylixe.ekite.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author fv
 */
public final class Diff {

	/** Type of this diff. **/
	@SerializedName("type")
	private final String type;

	/** Target line number of this diff. **/
	@SerializedName("linenum")
	private final int lineNumber;
	
	/** Start offset of the diff. **/
	@SerializedName("begin")
	private final int begin;

	/** End offset of this diff. **/
	@SerializedName("end")
	private final int end;
	
	/** Original diff. **/
	@SerializedName("source")
	private final String source;
	
	/** Destination diff. **/
	@SerializedName("destination")
	private final String destination;
	
	/** Original line value. **/
	@SerializedName("line_src")
	private final String lineSource;
	
	/** Diff line value. **/
	@SerializedName("line_dst")
	private final String lineDestination;

	/**
	 * Default constructor.
	 * 
	 * @param type Type of this diff.
	 * @param lineNumber Target line number of this diff.
	 * @param begin Start offset of this diff.
	 * @param end End offset of this diff.
	 * @param source Original diff.
	 * @param destination Destination diff.
	 * @param lineSource Original line value.
	 * @param lineDestination Diff line value.
	 */
	public Diff(
			final String type,
			final int lineNumber,
			final int begin,
			final int end,
			final String source,
			final String destination,
			final String lineSource,
			final String lineDestination) {
		this.type = type;
		this.lineNumber = lineNumber;
		this.begin = begin;
		this.end = end;
		this.source = source;
		this.destination = destination;
		this.lineSource = lineSource;
		this.lineDestination = lineDestination;
	}
	
	/**
	 * Type getter.
	 * 
	 * @return Type of this diff.
	 * @see #type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Line number getter.
	 * 
	 * @return Target line number of this diff.
	 * @see #lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}
	
	/**
	 * Start getter.
	 * 
	 * @return Start offset of this diff.
	 * @see #begin
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * End getter.
	 * 
	 * @return End offset of this diff.
	 * @see #end
	 */
	public int getEnd() {
		return end;
	}
	
	/**
	 * Source getter.
	 * 
	 * @return Original diff.
	 * @see #source
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * Destination getter.
	 * 
	 * @return Destination diff.
	 * @see #destination
	 */
	public String getDestination() {
		return destination;
	}
	
	/**
	 * Line source getter.
	 * 
	 * @return Original line value.
	 * @see #lineSource
	 */
	public String getLineSource() {
		return lineSource;
	}

	/**
	 * Line destination getter.
	 * 
	 * @return Diff line value.
	 * @see #lineDestination
	 */
	public String getLineDestination() {
		return lineDestination;
	}

}
