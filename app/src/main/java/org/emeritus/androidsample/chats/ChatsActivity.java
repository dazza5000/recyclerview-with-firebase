package org.emeritus.androidsample.chats;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.emeritus.androidsample.R;
import org.emeritus.androidsample.data.Post;
import org.emeritus.androidsample.data.PostService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatsActivity extends AppCompatActivity {

    private static final String TAG = ChatsActivity.class.getSimpleName();

    private RecyclerView postRecyclerView;

    private FirebaseUser currentUser;
    private DatabaseReference postReference;
    private List<Post> postsFromFirebase = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        postRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final PostsAdapter postsAdapter = new PostsAdapter(postsFromFirebase);
        postRecyclerView.setAdapter(postsAdapter);


        if (null != FirebaseAuth.getInstance().getCurrentUser()) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            postReference = FirebaseDatabase.getInstance()
                    .getReference(currentUser.getUid()).child("posts");
            postReference.keepSynced(true);
            getPosts();

            postReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    postsFromFirebase.clear();
                    for(DataSnapshot postFromSnapshot : dataSnapshot.getChildren()) {
                        Post currentPost = postFromSnapshot.getValue(Post.class);
                        postsFromFirebase.add(currentPost);
                    }
                    postsAdapter.replaceData(postsFromFirebase);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Please relogin to Emeritus", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void getPosts(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        PostService postService = retrofit.create(PostService.class);
        Call<List<Post>> call =
                postService.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postList = response.body();
                Log.d(TAG, "Posts received from API: " + postList.size());
                postReference.setValue(postList);

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });

    }

    private static class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

        private List<Post> posts;

        public PostsAdapter(List<Post> posts) {
            setList(posts);
        }

        @Override
        public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View quoteView = inflater.inflate(R.layout.post_item, parent, false);

            return new PostsAdapter.ViewHolder(quoteView);
        }

        @Override
        public void onBindViewHolder(PostsAdapter.ViewHolder viewHolder, int position) {
            Post post = posts.get(position);

            viewHolder.userId.setText(String.format("User: %s", post.getUserId()));
            viewHolder.title.setText(String.format("Title: %s", post.getTitle()));
            viewHolder.body.setText(post.getBody());

            Log.d(TAG, "onBindViewHolder: for position " +position);

        }

        private void setList(List<Post> quotes) {
            posts = quotes;
        }

        public void replaceData(List<Post> quotes){
            setList(quotes);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        public Post getItem(int position) {
            return posts.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView userId;
            private TextView title;
            private TextView body;



            public ViewHolder(View itemView) {
                super(itemView);

                userId = (TextView) itemView.findViewById(R.id.post_user_id);
                title = (TextView) itemView.findViewById(R.id.post_title);
                body = (TextView) itemView.findViewById(R.id.post_body);
            }

        }

    }
}


