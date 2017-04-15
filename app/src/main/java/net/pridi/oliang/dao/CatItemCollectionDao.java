package net.pridi.oliang.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pridi on 15-Apr-17.
 */

public class CatItemCollectionDao implements Parcelable {
    @SerializedName("response") private boolean response;
    @SerializedName("data") private List<CatItemDao> data;
    public CatItemCollectionDao(){

    }
    protected CatItemCollectionDao(Parcel in){
        response = in.readByte() != 0;
        data = in.createTypedArrayList(CatItemDao.CREATOR);
    }

    public static final Creator<CatItemCollectionDao> CREATOR = new Creator<CatItemCollectionDao>() {
        @Override
        public CatItemCollectionDao createFromParcel(Parcel in) {
            return new CatItemCollectionDao(in);
        }

        @Override
        public CatItemCollectionDao[] newArray(int size) {
            return new CatItemCollectionDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (response ? 1 : 0));
        dest.writeTypedList(data);
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public List<CatItemDao> getData() {
        return data;
    }

    public void setData(List<CatItemDao> data) {
        this.data = data;
    }
}
