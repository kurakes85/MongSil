package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ccei on 2016-07-29.
 */
public class SlidingMenuTabFragment extends Fragment {
    public static final String TABINFO = "tabinfo";

    public SlidingMenuTabFragment() { }

    public static SlidingMenuTabFragment newInstance(int tabInfo) {
        SlidingMenuTabFragment fragment = new SlidingMenuTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TABINFO, tabInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle initBundle = getArguments();
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_post, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                MongSilApplication.getMongSilContext()));

        if(initBundle.getInt(TABINFO) == 0) {
            // 나의 이야기 탭
            recyclerView.setAdapter(new PostRecyclerViewAdapter(1));
        } else {
            // 내가 쓴 댓글 탭
            recyclerView.setAdapter(new MyCommentRecyclerViewAdapter());
        }
        return recyclerView;
    }

    // 내가 쓴 댓글 어답터
    public static class MyCommentRecyclerViewAdapter
            extends RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder> {
        private static final int TEMP_COUNT = 5;

        MyCommentRecyclerViewAdapter() { }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final TextView textPostContent, textMyCommentContent, textMyCommentTime;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                textPostContent =
                        (TextView) view.findViewById(R.id.text_post_content);
                textMyCommentContent =
                        (TextView) view.findViewById(R.id.text_my_comment_content);
                textMyCommentTime =
                        (TextView) view.findViewById(R.id.text_my_comment_time);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.layout_my_comment_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textPostContent.setText(String.valueOf("댓글 내용이다" + position));
            int date = 20+position;
            holder.textMyCommentTime.setText(String.valueOf(String.valueOf("7월" + date)));
        }

        @Override
        public int getItemCount() {
            return TEMP_COUNT;
        }
    }
}