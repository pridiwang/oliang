package net.pridi.oliang.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by pridi on 29-Mar-17.
 */

public class PostNewDao implements Parcelable{

    @SerializedName("title") private String title;
    @SerializedName("content") private String content;
    @SerializedName("cat_id") private int catId;
    @SerializedName("img") private String img;
    @SerializedName("vdo") private String vdo;
    public PostNewDao(){

    }



    protected PostNewDao(Parcel in) {

        title = in.readString();
        content = in.readString();
        catId = in.readInt();
        img = in.readString();
        vdo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(content);
        dest.writeInt(catId);
        dest.writeString(img);
        dest.writeString(vdo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostNewDao> CREATOR = new Creator<PostNewDao>() {
        @Override
        public PostNewDao createFromParcel(Parcel in) {
            return new PostNewDao(in);
        }

        @Override
        public PostNewDao[] newArray(int size) {
            return new PostNewDao[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVdo() {
        return vdo;
    }

    public void setVdo(String vdo) {
        this.vdo = vdo;
    }
}
