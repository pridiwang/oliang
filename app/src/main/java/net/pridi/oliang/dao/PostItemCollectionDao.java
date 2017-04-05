package net.pridi.oliang.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pridi on 29-Mar-17.
 */

public class PostItemCollectionDao implements Parcelable{
    @SerializedName("response") private boolean response;
    @SerializedName("data") private List<PostItemDao> data;
    public PostItemCollectionDao(){

    }
    protected PostItemCollectionDao(Parcel in) {
        response = in.readByte() != 0;
        data = in.createTypedArrayList(PostItemDao.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (response ? 1 : 0));
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostItemCollectionDao> CREATOR = new Creator<PostItemCollectionDao>() {
        @Override
        public PostItemCollectionDao createFromParcel(Parcel in) {
            return new PostItemCollectionDao(in);
        }

        @Override
        public PostItemCollectionDao[] newArray(int size) {
            return new PostItemCollectionDao[size];
        }
    };

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public List<PostItemDao> getData() {
        return data;
    }

    public void setData(List<PostItemDao> data) {
        this.data = data;
    }
}
