package puzzleworld.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.jeff.puzzleworld.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GamePintuLayout extends RelativeLayout implements OnClickListener {
    private int number = 0; //Record the number of wins
    public int code = 0; // 0 Directly into the game ,1 Set the difficulty to enter the game
    //Initialize the image resource
    private int[] textBitmap = {R.drawable.test1, R.drawable.test2, R.drawable.test3, R.drawable.test4, R.drawable.test5, R.drawable.test6, R.drawable.test7,
            R.drawable.test8, R.drawable.test9, R.drawable.test10, R.drawable.test11, R.drawable.test12, R.drawable.test13, R.drawable.test14, R.drawable.test15,
            R.drawable.test16, R.drawable.test17, R.drawable.test18, R.drawable.test19, R.drawable.test20};
    public int mColumn = 3;
    /**
     * The inner margin of the container
     */
    private int mPadding;
    /**
     * The distance between each thumbnail (horizontal, vertical) dp
     */
    private int mMargin = 3;

    private ImageView[] mGamePintuItems;

    private int mItemWidth;

    /**
     * Game of the picture
     */
    private Bitmap mBitmap;

    private List<ImagePiece> mItemBitmaps;

    private boolean once;

    /**
     * The width of the game panel
     */
    private int mWidth;

    private boolean isGameSuccess;
    private boolean isGameOver;

    public interface GamePintuListener {
        void nextLevel(int nextLevel);

        void timechanged(int currentTime);

        void gameover();
    }

    public GamePintuListener mListener;

    /**
     * Sets the interface callback
     *
     * @param mListener
     */
    public void setOnGamePintuListener(GamePintuListener mListener) {
        this.mListener = mListener;
    }

    private int mLevel = 1;
    private static final int TIME_CHANGED = 0x110;
    private static final int NEXT_LEVEL = 0x111;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case TIME_CHANGED:
                    if (isGameSuccess || isGameOver || isPause)
                        return;
                    if (mListener != null) {
                        mListener.timechanged(mTime);
                    }
                    if (mTime == 0) {
                        isGameOver = true;
                        mListener.gameover();
                        return;
                    }
                    mTime--;
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);

                    break;
                case NEXT_LEVEL:
                    mLevel = mLevel + 1;
                    if (mListener != null) {
                        mListener.nextLevel(mLevel);
                    } else {
                        nextLevel();
                    }
                    break;

            }
        }

        ;
    };

    private boolean isTimeEnabled = false;
    private int mTime;

    /**
     * Set whether to turn on time
     *
     * @param isTimeEnabled
     */
    public void setTimeEnabled(boolean isTimeEnabled) {
        this.isTimeEnabled = isTimeEnabled;
    }

    public GamePintuLayout(Context context) {
        this(context, null);
    }

    public GamePintuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GamePintuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                3, getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
                getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Take the small value of width and high school
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

        if (!once) {
            // To cut, and sort
            initBitmap();
            // Sets the width and height properties of the ImageView (Item)
            initItem();
            // To determine whether to open time
            checkTimeEnable();

            once = true;
        }

        setMeasuredDimension(mWidth, mWidth);

    }

    private void checkTimeEnable() {
        if (isTimeEnabled) {
            // Set the time according to the current level
            countTimeBaseLevel();
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }

    }

    private void countTimeBaseLevel() {
        mTime = (int) Math.pow(2, mLevel) * 20;
    }

    /**
     * Cut and sort
     */
    private void initBitmap() {

        Random rand = new Random();
        int i = rand.nextInt(20);
        mBitmap = zoomImage(BitmapFactory.decodeResource(getResources(),
                textBitmap[i]), mWidth, mWidth);

        mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);

        // Use sort to complete our out of order
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece a, ImagePiece b) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });

    }

    /**
     * Sets the width and height properties of the ImageView (Item)
     */
    private void initItem() {
        mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))
                / mColumn;
        mGamePintuItems = new ImageView[mColumn * mColumn];
        // Generate item, set rule
        for (int i = 0; i < mGamePintuItems.length; i++) {
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());

            mGamePintuItems[i] = item;
            item.setId(i + 1);

            // The index is stored in the Item tag
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    mItemWidth, mItemWidth);

            // Set the horizontal gap between items, through rightMargin
            // Not the last column
            if ((i + 1) % mColumn != 0) {
                lp.rightMargin = mMargin;
            }
            // Not the first column
            if (i % mColumn != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF,
                        mGamePintuItems[i - 1].getId());
            }
            // If it is not the first line, set topMargin and rule
            if ((i + 1) > mColumn) {
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW,
                        mGamePintuItems[i - mColumn].getId());
            }
            addView(item, lp);
        }

    }

    public void restart() {
        isGameOver = false;
//        mColumn--;
        nextLevel();
    }

    private boolean isPause;

    public void pause() {
        isPause = true;
        mHandler.removeMessages(TIME_CHANGED);
    }

    public void resume() {
        if (isPause) {
            isPause = false;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    public void nextLevel() {
        this.removeAllViews();
        mAnimLayout = null;
        if (code == 0) {
            number++;
            if (number >= 10) {
                mColumn++;
            }
        } else {

        }


        isGameSuccess = false;
        checkTimeEnable();
        initBitmap();
        initItem();
    }

    /**
     * Gets the minimum value of multiple parameters
     */
    private int min(int... params) {
        int min = params[0];

        for (int param : params) {
            if (param < min)
                min = param;
        }
        return min;
    }

    private ImageView mFirst;
    private ImageView mSecond;

    @Override
    public void onClick(View v) {
        if (isAniming)
            return;

        // Click the same item twice
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }
        if (mFirst == null) {
            mFirst = (ImageView) v;
            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        } else {
            mSecond = (ImageView) v;
            // Exchange Item
            exchangeView();
        }

    }

    /**
     * Animation layer
     */
    private RelativeLayout mAnimLayout;
    private boolean isAniming;

    /**
     * Exchange Item
     */
    private void exchangeView() {
        mFirst.setColorFilter(null);
        // Constructs an animation layer
        setUpAnimLayout();

        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        // Sets the animation
        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft()
                - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation animSecond = new TranslateAnimation(0,
                -mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop()
                + mFirst.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        second.startAnimation(animSecond);

        // Animation listener
        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);

                isAniming = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();
                // Determine whether the user's game is successful
                checkSuccess();
                isAniming = false;
            }
        });

    }

    /**
     * Determine whether the user's game is successful
     */
    private void checkSuccess() {
        boolean isSuccess = true;

        for (int i = 0; i < mGamePintuItems.length; i++) {
            ImageView imageView = mGamePintuItems[i];
            if (getImageIndexByTag((String) imageView.getTag()) != i) {
                isSuccess = false;
            }
        }

        if (isSuccess) {
            isGameSuccess = true;
            mHandler.removeMessages(TIME_CHANGED);

            Toast.makeText(getContext(), "Success ， level up !!!",
                    Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }

    }

    /**
     * Obtain the Id from the tag
     *
     * @param tag
     * @return
     */
    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    public int getImageIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * Constructs an animation layer
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }

    // Zoom the image
    public Bitmap zoomImage(Bitmap bgimage, double newWidth,
                            double newHeight) {
        // Get the width and height of this image
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // Creates a matrix object for manipulating images
        Matrix matrix = new Matrix();
        // Calculate the wide-to-high zoom rate
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // Zoom the picture action
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }


}
