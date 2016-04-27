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
	private String userBuffer;
	
	/** **/
	@SerializedName("user_md5")
	private String userMd5;
	
	/** **/
	@SerializedName("expected_md5")
	private String expectedMd5;
	
	/** **/
	@SerializedName("expected_buffer")
	private String expectedBuffer;
	
	/** **/
	@SerializedName("suggestion")
	private Suggestion suggestion;

}
