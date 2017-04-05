package net.pridi.oliang.manager.http;

import net.pridi.oliang.dao.PostItemCollectionDao;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by pridi on 29-Mar-17.
 */

public interface ApiService {
    @POST("posts")
    Call<PostItemCollectionDao> loadPostList();

    @POST("newposts/{id}")
    Call<PostItemCollectionDao> loadNewPostList(@Path("id") int id);
    @POST("oldposts/{id}")
    Call<PostItemCollectionDao> loadOldPostList(@Path("id") int id);
}
