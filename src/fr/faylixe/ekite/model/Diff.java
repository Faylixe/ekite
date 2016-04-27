package fr.faylixe.ekite.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author fv
 */
public final class Diff {

	/** **/
	@SerializedName("type")
	private String type;

	/** **/
	@SerializedName("linenum")
	private int linenum;
	
	/** **/
	@SerializedName("begin")
	private int begin;

	/** **/
	@SerializedName("end")
	private int end;
	
	/** **/
	@SerializedName("source")
	private String source;
	
	/** **/
	@SerializedName("destination")
	private String destination;
	
	/** **/
	@SerializedName("line_src")
	private String line_src;
	
	/** **/
	@SerializedName("line_dst")
	private String line_dst;

}
