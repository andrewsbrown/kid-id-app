package edu.pdx.anb2;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class IllustrationViewPager extends ViewPager {

    private Illustration[] IMAGES = new Illustration[]{
            new Illustration(R.drawable.bear, "bear"),
            new Illustration(R.drawable.dog, "dog"),
            new Illustration(R.drawable.elephant, "elephant"),
            new Illustration(R.drawable.giraffe, "giraffe"),
            new Illustration(R.drawable.horse, "horse"),
            new Illustration(R.drawable.kangaroo, "kangaroo"),
            new Illustration(R.drawable.lion, "lion"),
            new Illustration(R.drawable.peacock, "peacock"),
            new Illustration(R.drawable.rhino, "rhino"),
            new Illustration(R.drawable.tiger, "tiger")
    };

    public IllustrationViewPager(Context context) {
        super(context);
        setAdapter(new IllustrationImageAdapter(context));
    }

    public IllustrationViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAdapter(new IllustrationImageAdapter(context));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    class Illustration {
        public final int image;
        public final String name;

        public Illustration(int image, String name) {
            this.image = image;
            this.name = name;
        }
    }

    class IllustrationImageAdapter extends PagerAdapter {
        Context mContext;

        IllustrationImageAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return IMAGES.length;
        }

        @Override
        public boolean isViewFromObject(View v, Object obj) {
            return v == ((ImageView) obj);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int i) {
            ImageView mImageView = new ImageView(mContext);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mImageView.setImageResource(IMAGES[i].image);
            ((ViewPager) container).addView(mImageView, 0);
            return mImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int i, Object obj) {
            ((ViewPager) container).removeView((ImageView) obj);
        }
    }
}
