package net.pridi.oliang.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by pridi on 29-Mar-17.
 */

public class CatItemDao implements Parcelable{
    @SerializedName("id") private int id;
    @SerializedName("name") private String name;
    public CatItemDao(){

    }



    protected CatItemDao(Parcel in) {
        id = in.readInt();
        name = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CatItemDao> CREATOR = new Creator<CatItemDao>() {
        @Override
        public CatItemDao createFromParcel(Parcel in) {
            return new CatItemDao(in);
        }

        @Override
        public CatItemDao[] newArray(int size) {
            return new CatItemDao[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
