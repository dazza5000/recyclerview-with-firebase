package org.emeritus.androidsample.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by daz on 8/17/16.
 */

public interface PostService {
    @GET("/posts")
    Call<List<Post>> getPosts();
}
