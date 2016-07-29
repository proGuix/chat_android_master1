package com.example.chat_v1;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guillaume on 12/04/16.
 */
public class User implements Parcelable {

    private String name;
    private Connection connection;
    private Bitmap picture_profile;
    private String description;

    protected User(Parcel in) {
        name = in.readString();
        connection = (Connection) in.readSerializable();
        picture_profile = in.readParcelable(Bitmap.class.getClassLoader());
        description = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeSerializable(this.connection);
        dest.writeParcelable(this.picture_profile, 0);
        dest.writeString(this.description);
    }

    public enum Connection {
        YES_FOREGROUND,
        YES_BACKGROUND,
        NO
    }

    public User(String name, Connection connection, Bitmap picture_profile, String description) {
        // TODO Auto-generated constructor stub
        this.name = name;
        this.connection = connection;
        this.picture_profile = picture_profile;
        this.description = description;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return this.name;
    }

    public Connection getConnection(){
        return this.connection;
    }

    public Bitmap getPictureProfile(){
        return this.picture_profile;
    }

    public void setPictureProfile(Bitmap picture_profile){
        this.picture_profile = picture_profile;
    }

    public String getDescription(){
        return this.description;
    }
}
