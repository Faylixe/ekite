package fr.faylixe.ekite.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author fv
 */
public final class SuggestionError {

	/** **/
	@SerializedName("message")
	private String message;

	/** **/
	@SerializedName("user_buffer")
	private String user_buffer;
	
	/** **/
	@SerializedName("user_md5")
	private String user_md5;
	
	/** **/
	@SerializedName("expected_md5")
	private String expected_md5;
	
	/** **/
	@SerializedName("expected_buffer")
	private String expected_buffer;
	
	/** **/
	@SerializedName("suggestion")
	private Suggestion suggestion;

}
