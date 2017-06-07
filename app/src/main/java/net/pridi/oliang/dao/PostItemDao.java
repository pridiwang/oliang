package net.pridi.oliang.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by pridi on 29-Mar-17.
 */

public class PostItemDao implements Parcelable{
    @SerializedName("id") private int id;
    @SerializedName("date") private Date date;
    @SerializedName("title") private String title;
    @SerializedName("content") private String content;
    @SerializedName("yrmo") private String yrmo;
    @SerializedName("category") private String category;
    @SerializedName("cat_id") private int catId;
    @SerializedName("img") private String img;
    @SerializedName("eb") private String eb;
    @SerializedName("yt") private String yt;
    @SerializedName("mp4") private String mp4;
    @SerializedName("vdo") private String vdo;
    @SerializedName("unread") private int unread;
    @SerializedName("viewed") private int viewed;
    public PostItemDao(){

    }



    protected PostItemDao(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        yrmo = in.readString();
        category = in.readString();
        catId = in.readInt();
        img = in.readString();
        eb = in.readString();
        yt = in.readString();
        mp4 = in.readString();
        vdo = in.readString();
        unread = in.readInt();
        viewed=in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(yrmo);
        dest.writeString(category);
        dest.writeInt(catId);
        dest.writeString(img);
        dest.writeString(eb);
        dest.writeString(yt);
        dest.writeString(mp4);
        dest.writeString(vdo);
        dest.writeInt(unread);
        dest.writeInt(viewed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostItemDao> CREATOR = new Creator<PostItemDao>() {
        @Override
        public PostItemDao createFromParcel(Parcel in) {
            return new PostItemDao(in);
        }

        @Override
        public PostItemDao[] newArray(int size) {
            return new PostItemDao[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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

    public String getYrmo() {
        return yrmo;
    }

    public void setYrmo(String yrmo) {
        this.yrmo = yrmo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getEb() {
        return eb;
    }

    public void setEb(String eb) {
        this.eb = eb;
    }

    public String getYt() {
        return yt;
    }

    public void setYt(String yt) {
        this.yt = yt;
    }

    public String getMp4() {
        return mp4;
    }
    public int getUnread() {
        return unread;
    }
    public int getViewed() {       return viewed;    }

    public void setMp4(String mp4) {
        this.mp4 = mp4;
    }
    public String getVdo() {
        return vdo;
    }

    public void setVdo(String vdo) {
        this.vdo = vdo;
    }
    public void setUnread(int unread) {
        this.unread = unread;
    }
    public void setViewed(int viewed) {
        this.viewed = viewed;
    }


}
