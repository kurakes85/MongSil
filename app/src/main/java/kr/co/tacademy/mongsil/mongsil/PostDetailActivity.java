package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class PostDetailActivity extends BaseActivity {
    private static final String POST_ID= "postid";

    ImageView imgBackground, imgThreeDot, imgWeatherIcon;
    TextView postContent, postLocation, postTime, postName, postReplyCount;

    Post post;
    // 댓글
    RelativeLayout commentBottomSheet;
    BottomSheetBehavior commentBehavior;
    RecyclerView replyRecycler;
    ReplyRecyclerViewAdapter replyAdapter;
    ImageView imgNoneReply;
    TextView noneReply;
    EditText editReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        post = new Post();
        Intent intent = getIntent();
        String postId = intent.getStringExtra(POST_ID);

        imgBackground = (ImageView) findViewById(R.id.img_post_detail_background);
        imgThreeDot = (ImageView) findViewById(R.id.img_threeDot);
        imgWeatherIcon = (ImageView) findViewById(R.id.img_weather_icon);
        postContent = (TextView) findViewById(R.id.text_post_content);
        postLocation = (TextView) findViewById(R.id.text_post_location);
        postTime = (TextView) findViewById(R.id.text_post_time);
        postName = (TextView) findViewById(R.id.text_post_name);
        postReplyCount = (TextView) findViewById(R.id.text_post_comment_count);
        editReply = (EditText) findViewById(R.id.edit_reply);

        replyAdapter = new ReplyRecyclerViewAdapter();
        replyRecycler = (RecyclerView) findViewById(R.id.reply_recycler);
        imgNoneReply = (ImageView) findViewById(R.id.img_none_reply_icon);
        noneReply = (TextView) findViewById(R.id.text_none_reply);
        commentBottomSheet =
                (RelativeLayout) findViewById(R.id.comment_bottom_sheet);
        commentBehavior = BottomSheetBehavior.from(commentBottomSheet);
        commentBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        new AsyncPostDetailReplyJSONList().execute(postId);
        new AsyncPostDetailJSONList().execute(postId);
    }

    private void init() {
        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // background 이미지
        if(!post.bgImg.isEmpty()) {
            Glide.with(this).load(post.bgImg).into(imgBackground);
        } else {
            // TODO : weatherCode에 맞는 테마 배경을 넣어야함
            // WeatherData.imgFromWeatherCode(String.valueOf(post.weatherCode), 1));
            imgBackground.setImageResource(R.drawable.splash_background);
        }

        // 날씨 아이콘
        imgWeatherIcon.setImageResource(
                WeatherData.imgFromWeatherCode(
                        String.valueOf(post.weatherCode), 0));

        // TODO : 아이콘 코드 추가해야함

        // 포스트 내용
        postContent.setText(post.content);

        // 포스트 지역
        postLocation.setText(post.area1);

        // 포스트 시간
        String[] date = post.date.split(" ");
        postTime.setText(TimeData.PostTime(date[1]));

        // 포스트 작성자명
        postName.setText(post.username);

        // 포스트 코멘트 수
        postReplyCount.setText(String.valueOf(post.replyCount));

        // 코멘트 바텀 시트
        replyRecycler.setLayoutManager(
                new LinearLayoutManager(MongSilApplication.getMongSilContext()));
        postReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                // TODO : 댓글 수에 따라 창의 최대 위치가 변경됨
                /*if(commentCount > 3) {

                } else if(commentCount > 0) {

                }*/
                commentBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(View bottomSheet, int newState) {

                    }
                    @Override
                    public void onSlide(View bottomSheet, float slideOffset) {

                    }
                });
                showCommentSheet();
            }
        });
    }

    // TODO : 댓글창 만들기
    private void showCommentSheet() {
        TextView share = (TextView) findViewById(R.id.text_share);
        // 공유하기를 눌렀을 때
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 댓글 리사이클러뷰
        /*if(postReplyCount > 0) {
            RecyclerView commentRecycler =
                    (RecyclerView) rootView.findViewById(R.id.comment_recycler);
            commentRecycler.setVisibility(View.VISIBLE);
            commentRecycler.setLayoutManager(
                    new LinearLayoutManager(getApplicationContext()));
            //commentRecycler.setAdapter( -- );
        }*/
    }

    // 글 상세내용, 댓글목록 가져오기
    public class AsyncPostDetailJSONList extends AsyncTask<String, Integer, Post> {
        @Override
        protected Post doInBackground(String... args) {
            try{
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.SERVER_POST_DETAIL,
                                args[0]))
                        .build();
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();

                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONPostDetailRequestList(
                            new StringBuilder(responseBody.string()));
                }
                responseBody.close();
            }catch (UnknownHostException une) {
                e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("connectionFail", uee.toString());
            } catch (Exception e) {
                e("connectionFail", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Post result) {
            post = result;
            init();
        }
    }

    public class AsyncPostDetailReplyJSONList extends AsyncTask<String, Integer, ArrayList<ReplyData>> {
        @Override
        protected ArrayList<ReplyData> doInBackground(String... args) {
            try{
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.SERVER_POST_DETAIL_REPLY,
                                args[0]))
                        .build();
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();

                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONReplyRequestList(
                            new StringBuilder(responseBody.string()));
                }
                responseBody.close();
            }catch (UnknownHostException une) {
                e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("connectionFail", uee.toString());
            } catch (Exception e) {
                e("connectionFail", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ReplyData> result) {
            if(result != null && result.size() > 0) {
                replyRecycler.setVisibility(View.VISIBLE);
                imgNoneReply.setVisibility(View.GONE);
                noneReply.setVisibility(View.GONE);

                // TODO : 만들어야함
                replyAdapter.add(result);
                replyRecycler.setAdapter(replyAdapter);
            }
        }
    }

    private void closePostDetail() {
        commentBehavior.setPeekHeight(0);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closePostDetail();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closePostDetail();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
