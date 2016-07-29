package com.example.chat_v1;

import android.graphics.Bitmap;

public class Message {

	private String message;
	private String author;
	private Bitmap image;
	private Side side;
	private String time;
	private Bitmap picture_profile;

	public enum Side {
		LEFT,
		RIGHT
	}

	public Message() {
		// TODO Auto-generated constructor stub
	}

	public Message(String message, Bitmap image, String author, Side side, String time, Bitmap picture_profile) {
		// TODO Auto-generated constructor stub
		this.message = message;
		this.image = image;
		this.author = author;
		this.side = side;
		this.time = time;
		this.picture_profile = picture_profile;
	}

	public String getMessage() {
		// TODO Auto-generated method stub
		return this.message;
	}

	public String getAuthor() {
		// TODO Auto-generated method stub
		return this.author;
	}

	public Side getSide() {
		// TODO Auto-generated method stub
		return this.side;
	}

	public Bitmap getImage() {
		// TODO Auto-generated method stub
		return this.image;
	}

	public String getTime() {
		// TODO Auto-generated method stub
		return this.time;
	}

	public Bitmap getPictureProfile() {
		// TODO Auto-generated method stub
		return this.picture_profile;
	}

	public void setPictureProfile(Bitmap picture_profile) {
		// TODO Auto-generated method stub
		this.picture_profile = picture_profile;
	}
}
