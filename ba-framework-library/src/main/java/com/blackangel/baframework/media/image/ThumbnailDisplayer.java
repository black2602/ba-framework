package com.blackangel.baframework.media.image;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.media.image.fragment.PreviewsFragment;

import java.lang.ref.WeakReference;

/**
 * Created by KimJeongHun on 2016-07-19.
 */
public class ThumbnailDisplayer extends AsyncTask<PreviewsFragment.PreviewTag, Void, Bitmap> {
    private WeakReference<ImageView> weakReference;
    private ProgressBar loadingProgress;

    public ThumbnailDisplayer(WeakReference<ImageView> weakReference, ProgressBar loadingProgress) {
        this.weakReference = weakReference;
        this.loadingProgress = loadingProgress;
    }

    @Override
    protected void onPreExecute() {
        loadingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(PreviewsFragment.PreviewTag... params) {
        Bitmap bitmap = params[0].getBitmap();
        MyLog.d("KJH", "bitmap=" + bitmap);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            MyLog.d("KJH", "loadingProgress=" + loadingProgress);
            MyLog.d("KJH", "weakReference.get()=" + weakReference.get());
            loadingProgress.setVisibility(View.GONE);
            weakReference.get().setImageBitmap(bitmap);
        }
    }
}
