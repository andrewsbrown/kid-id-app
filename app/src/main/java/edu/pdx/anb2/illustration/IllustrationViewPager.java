package edu.pdx.anb2.illustration;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import edu.pdx.anb2.R;
import edu.pdx.anb2.bluetooth.BluetoothApplicationState;
import edu.pdx.anb2.bluetooth.BluetoothMessages;
import edu.pdx.anb2.bluetooth.BluetoothService;

public class IllustrationViewPager extends ViewPager {

    private static final String LOG_TAG = IllustrationViewPager.class.getSimpleName();
    private volatile boolean allowPaging = false;
    private BluetoothService bluetooth;
    private Toast lastToast;
    private IllustrationImageAdapter adapter;

    public IllustrationViewPager(Context context) {
        super(context);
        setup(context);
    }

    public IllustrationViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    void setup(Context context) {
        adapter = new IllustrationImageAdapter(context);
        setAdapter(adapter);
        addOnPageChangeListener(new DisableOnScroll());

        // setup message handler
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BluetoothMessages.SYNC_TAG:
                        onReceivedSync((BluetoothApplicationState) msg.obj);
                        break;
                    case BluetoothMessages.TOAST_TAG:
                        toast((String) msg.obj);
                        break;
                    default:
                        Log.w(LOG_TAG, "Unknown message type to handle");
                }
            }
        };

        // setup bluetooth
        bluetooth = BluetoothService.getInstance(mHandler);
    }

    private void toast(String text) {
        if (lastToast != null) lastToast.cancel();
        lastToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        lastToast.show();
    }

    public void onReceivedSync(BluetoothApplicationState state) {
        setIllustration(state.illustration);
        setPaging(state.success);
    }

    public BluetoothApplicationState currentState() {
        return new BluetoothApplicationState(Illustration.ALL[getCurrentItem()].image, allowPaging);
    }

    public boolean matches(String text) {
        return Illustration.ALL[getCurrentItem()].name.equals(text);
    }

    public void enablePaging() {
        setPaging(true);
        bluetooth.sendSync(currentState());
    }

    public void disablePaging() {
        setPaging(false);
        bluetooth.sendSync(currentState());
    }

    private void setPaging(boolean enabled) {
        Log.d(LOG_TAG, "Set paging enabled: " + enabled);
        allowPaging = enabled;
        setBackgroundColor(getResources().getColor((enabled) ? R.color.pagingEnabled : R.color.pagingDisabled));
    }

    private void setIllustration(int illustration) {
        int index = Illustration.find(illustration);
        assert index != -1;
        Object o = adapter.instantiateItem(this, index);
        adapter.setPrimaryItem(this, index, o);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return allowPaging && super.onTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return allowPaging && super.onTouchEvent(event);
    }

    class DisableOnScroll implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_SETTLING) {
                setPaging(false);
            } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                bluetooth.sendSync(currentState());
            }
        }
    }

    class IllustrationImageAdapter extends PagerAdapter {
        Context mContext;

        IllustrationImageAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return Illustration.ALL.length;
        }

        @Override
        public boolean isViewFromObject(View v, Object obj) {
            return v == ((ImageView) obj);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int i) {
            ImageView mImageView = new ImageView(mContext);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mImageView.setImageResource(Illustration.ALL[i].image);
            ((ViewPager) container).addView(mImageView, 0);
            return mImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int i, Object obj) {
            ((ViewPager) container).removeView((ImageView) obj);
        }
    }
}
