package com.blackangel.baframework.ui.dialog.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackangel.baframework.R;


/**
 * Created by KimJeongHun on 2016-03-17.
 */
public class LoadingProgressDialog extends ProgressDialog {
    private Context mContext;
    private View mContentView;
    private ImageView mLoadingImage;
    private TextView mTxtMessage;

    public LoadingProgressDialog(Context context) {
        super(context);
        init(context);
    }

    public LoadingProgressDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context) {
        this.mContext =  context;

        // set background transparent
//        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // protect background behind dark
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // make background behind dark
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setDimAmount(0.5f);

        setIndeterminate(true);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        mContentView = View.inflate(mContext, R.layout.progress_dialog, null);
//        mLoadingImage = (ImageView) mContentView.findViewById(R.id.progress_loading);
//        mTxtMessage = (TextView) mContentView.findViewById(R.id.message);
    }

    @Override
    public void show() {
        super.show();
        startFrameAnimation(mLoadingImage);
        setContentView(mContentView);
    }

    private void startFrameAnimation(ImageView loadingImageView) {
//        loadingImageView.setBackgroundResource(R.drawable.loading_animation);
//        AnimationDrawable anim = (AnimationDrawable) loadingImageView.getBackground();
//        anim.start();
    }

    private void startFadeAnimation(final ImageView view) {
//        final Animation fadeIn = AnimationUtils.loadAnimation(mActivity, R.anim.loading_fade_in);
//        final Animation fadeOut = AnimationUtils.loadAnimation(mActivity, R.anim.loading_fade_out);
//
//        fadeIn.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {}
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                view.startAnimation(fadeOut);
//            }
//        });
//
//        fadeOut.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {}
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                view.startAnimation(fadeIn);
//            }
//        });
//
//        view.startAnimation(fadeIn);
    }

    @Override
    public void setMessage(CharSequence message) {
        mTxtMessage.setText(message);
    }
}
