package net.pridi.oliang.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.inthecheesefactory.thecheeselibrary.view.BaseCustomViewGroup;
import com.inthecheesefactory.thecheeselibrary.view.state.BundleSavedState;

import net.pridi.oliang.R;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PostListItem extends BaseCustomViewGroup {
    TextView tvTitle;
    TextView tvDetail;
    ImageView ivImg;
    TextView tvCatName;
    ImageView ivUnread;

    public PostListItem(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public PostListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public PostListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public PostListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.list_item_post, this);
    }

    private void initInstances() {
        // findViewById here
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDetail = (TextView) findViewById(R.id.tvDetail);
        ivImg=(ImageView) findViewById(R.id.ivImg);
        tvCatName=(TextView) findViewById(R.id.tvCategoryName);
        ivUnread=(ImageView) findViewById(R.id.ivUnread);

    }

    public void setTitleText(String text) {
        tvTitle.setText(text);
    }
    public void setDetailText(String text) {
        tvDetail.setText(text);
    }
    public void setCatName(String text) {tvCatName.setText(text);}
    public void setImgUrl(String text) {
        Glide.with(Contextor.getInstance().getContext())
                .load(text)
                .crossFade()
                .placeholder(R.drawable.dummy)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

                .into(ivImg);

    }
    public void setImgUnread(int text){
        if(text==0){
            ivUnread.setVisibility(GONE);

        }
    }


    private void initWithAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        /*
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StyleableName,
                defStyleAttr, defStyleRes);

        try {

        } finally {
            a.recycle();
        }
        */
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        BundleSavedState savedState = new BundleSavedState(superState);
        // Save Instance State(s) here to the 'savedState.getBundle()'
        // for example,
        // savedState.getBundle().putString("key", value);

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        Bundle bundle = ss.getBundle();
        // Restore State from bundle here
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize((widthMeasureSpec));
        int height=width*9/16;
        //child
        int newHeightMeasureSpec=MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
        //self
        setMeasuredDimension(width,height);
    }
}
