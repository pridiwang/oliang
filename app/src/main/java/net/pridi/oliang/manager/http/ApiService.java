package net.pridi.oliang.manager.http;

import net.pridi.oliang.dao.CatItemCollectionDao;
import net.pridi.oliang.dao.PostItemCollectionDao;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.dao.PostNewDao;
import net.pridi.oliang.datatype.MutableInteger;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by pridi on 29-Mar-17.
 */

public interface ApiService {
    @POST("posts")
    Call<PostItemCollectionDao> loadPostList();

    @POST("catposts/{catid}")
    Call<PostItemCollectionDao> loadCatPostList(@Path("catid") Integer catid);

    @POST("catlist")
    Call<CatItemCollectionDao> loadCatList();

    @POST("newposts/{catid}/{id}")
    Call<PostItemCollectionDao> loadNewPostList(@Path("catid") Integer catid,@Path("id") int id);

    @POST("oldposts/{catid}/{id}")
    Call<PostItemCollectionDao> loadOldPostList(@Path("catid") Integer catid,@Path("id") int id);

    @FormUrlEncoded
    @POST("postnew")
    Call<PostItemDao> postNewPost(@Field("category") int category,
                                  @Field("title") String title,
                                  @Field("content") String content,
                                  @Field("image") String image,
                                  @Field("vdo") String vdo
    );

    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part file
    );


    @POST("regtoken/{token}/{uuid}")
    Call<Object> registerToken(@Path("token") String token,@Path("uuid") String uuid);

}

