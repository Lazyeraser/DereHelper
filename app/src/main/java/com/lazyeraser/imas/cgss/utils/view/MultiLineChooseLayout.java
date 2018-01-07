package com.lazyeraser.imas.cgss.utils.view;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lazyeraser.imas.derehelper.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 自定义flowlayout，显示运营商
 * https://github.com/jackuhan/FlowlayoutTags
 */

public class MultiLineChooseLayout extends ViewGroup {

    /**
     * 默认的文字颜色
     */
    private final int default_text_color = Color.rgb(0x49, 0xC1, 0x20);

    /**
     * 默认的背景颜色
     */
    private final int default_background_color = Color.WHITE;

    /**
     * 默认的选中文字颜色
     */
    private final int default_checked_text_color = Color.WHITE;

    /**
     * 默认的选中背景颜色
     */
    private final int default_checked_background_color = Color.rgb(0x49, 0xC1, 0x20);

    /**
     * 默认的文字大小
     */
    private final float default_text_size;

    /**
     * 默认的水平间距
     */
    private final float default_horizontal_spacing;

    /**
     * 默认的竖直间距
     */
    private final float default_vertical_spacing;

    /**
     * 默认的内部水平间距
     */
    private final float default_horizontal_padding;

    /**
     * 默认的内部竖直间距
     */
    private final float default_vertical_padding;

    private int textColor;

    private int backgroundColor;

    private boolean item_click;

    private int selectedTextColor;

    private int selectedBackgroundColor;

    private float textSize;

    private int horizontalSpacing;

    private int verticalSpacing;

    private int horizontalPadding;

    private int verticalPadding;

    private int itemWidth, itemHeight;

    private int itemMaxEms;

    private boolean multiChooseable, singleLine;

    private boolean animUpdateDrawable = false;

    private float mRadius[] = {0, 0, 0, 0, 0, 0, 0, 0};

    private ColorStateList mStrokeColor, mCheckedStrokeColor;

    private int mStrokeWidth = 0;

    private onItemClickListener mOnItemClickLisener;

    private ItemClicker mInternalTagClickListener = new ItemClicker();

    public MultiLineChooseLayout(Context context) {
        this(context, null);
    }

    //    public MultiLineChooseLayout(Context context, AttributeSet attrs) {
    //        this(context, attrs, R.attr.MultiLineChooseLayoutTagsStyle);
    //    }

    public MultiLineChooseLayout(Context context, AttributeSet attrs/*, int defStyleAttr*/) {
        super(context, attrs/*, defStyleAttr*/);
        default_text_size = sp2px(13.0f);
        default_horizontal_spacing = dp2px(8.0f);
        default_vertical_spacing = dp2px(4.0f);
        default_horizontal_padding = dp2px(0.0f);
        default_vertical_padding = dp2px(0.0f);

        final TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.MultiLineChooseItemTags);
        try {
            textColor = attrsArray.getColor(R.styleable.MultiLineChooseItemTags_item_textColor, default_text_color);
            backgroundColor = attrsArray.getColor(R.styleable.MultiLineChooseItemTags_item_backgroundColor,
                    default_background_color);
            selectedTextColor = attrsArray.getColor(R.styleable.MultiLineChooseItemTags_item_selectedTextColor,
                    default_checked_text_color);
            selectedBackgroundColor = attrsArray.getColor(
                    R.styleable.MultiLineChooseItemTags_item_selectedBackgroundColor,
                    default_checked_background_color);
            textSize = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_item_textSize, default_text_size);
            horizontalSpacing = (int) attrsArray.getDimension(
                    R.styleable.MultiLineChooseItemTags_item_horizontalSpacing,
                    default_horizontal_spacing);
            verticalSpacing = (int) attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_item_verticalSpacing,
                    default_vertical_spacing);
            horizontalPadding = (int) attrsArray.getDimension(
                    R.styleable.MultiLineChooseItemTags_item_horizontalPadding,
                    default_horizontal_padding);
            verticalPadding = (int) attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_item_verticalPadding,
                    default_vertical_padding);
            multiChooseable = attrsArray.getBoolean(R.styleable.MultiLineChooseItemTags_item_multiChooseable, true);
            item_click = attrsArray.getBoolean(R.styleable.MultiLineChooseItemTags_item_click, true);
            singleLine = attrsArray.getBoolean(R.styleable.MultiLineChooseItemTags_item_singleLine, false);
            itemWidth = attrsArray.getInt(R.styleable.MultiLineChooseItemTags_item_width,
                    MultiLineChooseLayout.LayoutParams.WRAP_CONTENT);
            if (itemWidth >= 0) {
                itemWidth = sp2px(itemWidth);
            }
            itemHeight = attrsArray.getInt(R.styleable.MultiLineChooseItemTags_item_height,
                    MultiLineChooseLayout.LayoutParams.WRAP_CONTENT);
            if (itemHeight >= 0) {
                itemHeight = sp2px(itemHeight);
            }

            itemMaxEms = attrsArray.getInt(R.styleable.MultiLineChooseItemTags_item_maxEms, -1);
            if (itemWidth < 0) {
                itemMaxEms = -1;
            }

            float radius = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_item_radius, 0);
            float topLeftRadius = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_item_topLeftRadius, 0);
            float topRightRadius = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_item_topRightRadius, 0);
            float bottomLeftRadius = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_item_bottomLeftRadius,
                    0);
            float bottomRightRadius = attrsArray
                    .getDimension(R.styleable.MultiLineChooseItemTags_item_bottomRightRadius, 0);

            if (topLeftRadius == 0 && topRightRadius == 0 && bottomLeftRadius == 0 && bottomRightRadius == 0) {
                topLeftRadius = topRightRadius = bottomRightRadius = bottomLeftRadius = radius;
            }

            mRadius[0] = mRadius[1] = topLeftRadius;
            mRadius[2] = mRadius[3] = topRightRadius;
            mRadius[4] = mRadius[5] = bottomRightRadius;
            mRadius[6] = mRadius[7] = bottomLeftRadius;

            mStrokeColor = attrsArray.getColorStateList(R.styleable.MultiLineChooseItemTags_item_strokeColor);
            mCheckedStrokeColor = attrsArray
                    .getColorStateList(R.styleable.MultiLineChooseItemTags_item_selectedStrokeColor);
            mStrokeWidth = (int) attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_item_strokeWidth,
                    mStrokeWidth);

        } finally {
            attrsArray.recycle();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int row = 0; // The row counter.
        int rowWidth = 0; // Calc the current row width.
        int rowMaxHeight = 0; // Calc the max tag height, in current row.

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                rowWidth += childWidth;
                if (rowWidth > widthSize) { // Next line.
                    rowWidth = childWidth; // The next row width.
                    height += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = childHeight; // The next row max height.
                    row++;
                } else { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += horizontalSpacing;
            }
        }
        height += rowMaxHeight;

        height += getPaddingTop() + getPaddingBottom();

        if (row == 0) {//只有一行item
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        } else {// If the tags grouped exceed one line, set the width to match the parent.
            width = widthSize;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();

        int childLeft = parentLeft;
        int childTop = parentTop;

        int rowMaxHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                if (childLeft + width > parentRight) { // Next line
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = height;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                childLeft += width + horizontalSpacing;
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.tags = getAllItemText();
        ss.checkedPosition = getSelectedIndex();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setList(ss.tags);
        ItemView checkedTagView = getIndexItem(ss.checkedPosition);
        if (checkedTagView != null) {
            checkedTagView.setItemSelected(true);
        }
    }

    /**
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public String[] getAllItemText() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            final ItemView tagView = getIndexItem(i);
            tagList.add(tagView.getText().toString());
        }

        return tagList.toArray(new String[tagList.size()]);
    }

    /**
     * @see #setList(String...)
     */
    public void setList(List<String> tagList) {
        setList(tagList.toArray(new String[tagList.size()]));
    }

    public void setList(Collection<String> tagList) {
        setList(tagList.toArray(new String[tagList.size()]));
    }

    public void setResList(Collection<Integer> tagList) {
        List<String> strings = new LinkedList<>();
        for (Integer integer : tagList) {
            strings.add(getContext().getString(integer));
        }
        setList(strings);
    }

    /**
     * 设置数据源
     *
     * @param tags
     */
    public void setList(String... tags) {
        removeAllViews();
        for (final String tag : tags) {
            addItem(tag);
        }

    }

    /**
     * 更新某个item的内容
     *
     * @param txt
     * @param position
     */
    public void setIndexItemText(int position, String txt) {

        int index = -1;
        final int count = getChildCount();
        if (position >= count) {
            return;
        }
        ItemView tagView = getIndexItem(position);
        tagView.setText(txt);
    }

    /**
     * 设置默认的位置上选中
     *
     * @param position
     * @return
     */
    public int setIndexItemSelected(int position) {
        return setIndexItemSelected(position, true);
    }

    /**
     * 设置选中状态
     *
     * @param position
     * @return
     */
    public int setIndexItemSelected(int position, boolean flag) {

        int index = -1;
        final int count = getChildCount();
        if (position >= count) {
            return -1;
        }

        ItemView tagView = getIndexItem(position);
        tagView.setItemSelected(flag);
        index = position;
        return index;
    }

    /**
     * 设置选中
     *
     * @param positionList
     * @return
     */
    public void setIndexListItemSelected(List<Integer> positionList) {

        if (positionList == null || positionList.isEmpty() || positionList.size() == 0)
            return;

        final int count = getChildCount();
        if (positionList.size() > count) {
            return;
        }

        for (int i = 0; i < positionList.size(); i++) {
            ItemView tagView = getIndexItem(i);
            tagView.setItemSelected(true);
        }

    }

    /**
     * 返回指定的item
     *
     * @param index
     * @return
     */
    protected ItemView getIndexItem(int index) {
        return null == getChildAt(index) ? null : (ItemView) getChildAt(index);
    }

    /**
     * 返回选中的item
     *
     * @return
     */
    protected ItemView getSelectedItem() {
        final int checkedTagIndex = getSelectedIndex();
        if (checkedTagIndex != -1) {
            return getIndexItem(checkedTagIndex);
        }
        return null;
    }

    /**
     * 获取选中的item文字内容
     *
     * @return
     */
    protected String getSelectedItemText() {
        if (null != getSelectedItem()) {
            return getSelectedItem().getText().toString();
        }
        return null;
    }

    /**
     * 返回所有选中的tag的文字
     *
     * @return String
     */
    public String[] getAllItemSelectedTextWithStringArray() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            final ItemView tagView = getIndexItem(i);
            if (tagView.isChecked) {
                tagList.add(tagView.getText().toString());
            }
        }

        return tagList.toArray(new String[tagList.size()]);
    }

    /**
     * 返回所有选中的tag的文字
     *
     * @return ListArray
     */
    public ArrayList<String> getAllItemSelectedTextWithListArray() {
        final int count = getChildCount();
        final ArrayList<String> tagList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            final ItemView tagView = getIndexItem(i);
            if (tagView.isChecked) {
                tagList.add(tagView.getText().toString());
            }
        }

        return tagList;
    }

    /**
     * 返回选中的item下标
     *
     * @return
     */
    public int getSelectedIndex() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final ItemView tag = getIndexItem(i);
            if (tag.isChecked) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 返回所有选中的item的下标列表集合
     *
     * @return ArrayList
     */
    public ArrayList<Integer> getAllItemSelectedIndex() {
        final int count = getChildCount();
        final ArrayList<Integer> tagList = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            final ItemView tagView = getIndexItem(i);
            if (tagView.isChecked) {
                tagList.add(i);
            }
        }

        return tagList;
    }

    /**
     * 如果某个选中的话，取消选中
     *
     * @param position
     */
    public void cancelSelectedIndex(int position) {
        ItemView tag = getIndexItem(position);
        if (null != tag && tag.isChecked) {
            tag.setItemSelected(false);
        }
    }

    /**
     * 根据某个tag判断是否被选中
     *
     * @param text
     * @return 返回选中的下标
     */
    public int isSelected(String text) {
        int flag = -1;
        List<String> mSelectedAll = getAllItemSelectedTextWithListArray();
        if (mSelectedAll != null && mSelectedAll.contains(text)) {
            flag = mSelectedAll.indexOf(text);
        }
        return flag;
    }

    /**
     * 判断某个位置下面是否被选中
     *
     * @param poisition
     * @return 返回选中的下标
     */
    public boolean isSelected(int poisition) {
        boolean flag = false;
        ItemView tag = getIndexItem(poisition);
        if (null != tag && tag.isChecked) {
            flag = true;
        }
        return flag;
    }

    /**
     * 取消选中状态
     */
    public void cancelAllSelectedItems() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ItemView tag = getIndexItem(i);
            if (null != tag && tag.isChecked) {
                tag.setItemSelected(false);
            }
        }
    }

    public void selectAll(){
        for (int i = 0; i < getChildCount(); i++) {
            setIndexItemSelected(i);
        }
    }

    /**
     * 只展示，不做事件响应
     */
    public void onlyShow() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ItemView tag = getIndexItem(i);
            if (null != tag && tag.isChecked) {
                tag.setItemSelected(false);
            }
            tag.setClickable(false);
        }
    }

    /**
     * 增加item
     *
     * @param tag
     */
    private void addItem(CharSequence tag) {
        final ItemView item = new ItemView(getContext(), tag);
        item.setOnClickListener(mInternalTagClickListener);
        addView(item);
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MultiLineChooseLayout.LayoutParams(getContext(), attrs);
    }

    public void setOnItemClickListener(onItemClickListener l) {
        mOnItemClickLisener = l;
    }

    public interface onItemClickListener {
        void onItemClick(int position, String text);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }

    /**
     * For {@link MultiLineChooseLayout} save and restore state.
     */
    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        int tagCount;

        String[] tags;

        int checkedPosition;

        public SavedState(Parcel source) {
            super(source);
            tagCount = source.readInt();
            tags = new String[tagCount];
            source.readStringArray(tags);
            checkedPosition = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            tagCount = tags.length;
            dest.writeInt(tagCount);
            dest.writeStringArray(tags);
            dest.writeInt(checkedPosition);
        }
    }

    class ItemClicker implements OnClickListener {
        @Override
        public void onClick(View v) {

            //如果可以点击的话
            if (item_click) {
                final ItemView tag = (ItemView) v;
                int position = -1;
                final ItemView checkedTag = getSelectedItem();
                if (!multiChooseable) {
                    //单选
                    if (checkedTag != null) {
                        checkedTag.setItemSelected(false);
                    }

                    tag.setItemSelected(true);
                    position = getSelectedIndex();
                } else {
                    //多选
                    tag.setItemSelected(!tag.isChecked);

                    //寻找最后一次点击的index
                    final int count = getChildCount();
                    for (int i = 0; i < count; i++) {
                        final ItemView tagPre = getIndexItem(i);
                        if (tagPre == tag) {
                            position = i;
                            break;
                        }
                    }
                }

                //外部点击事件
                if (mOnItemClickLisener != null) {
                    mOnItemClickLisener.onItemClick(position, tag.getText().toString());
                }
            }

        }
    }

    class ItemView extends AppCompatTextView {

        private Context mContext;

        private boolean isChecked = false;

        private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private Rect mOutRect = new Rect();

        {
            mBackgroundPaint.setStyle(Paint.Style.FILL);
        }

        public ItemView(Context context, CharSequence text) {
            super(context);
            this.mContext = context;
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
            setLayoutParams(new MultiLineChooseLayout.LayoutParams(itemWidth, itemHeight));

            setGravity(Gravity.CENTER);

            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            setSingleLine(singleLine);
            if (singleLine) {
                if (itemMaxEms >= 0) {
                    setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                    setMaxEms(itemMaxEms);
                }
            }

            setText(text);

            setClickable(true);
            invalidatePaint();

        }

        /**
         * Set whether this tag view is in the checked state.
         *
         * @param select true is checked, false otherwise
         */
        public void setItemSelected(boolean select) {
            isChecked = select;
            invalidatePaint();
        }

        @Override
        protected boolean getDefaultEditable() {
            return false;
        }

        private void invalidatePaint() {

//            animUpdateDrawable = false;

            if (isChecked) {
                mBackgroundPaint.setColor(selectedBackgroundColor);
                setTextColor(selectedTextColor);
            } else {
                mBackgroundPaint.setColor(backgroundColor);
                setTextColor(textColor);
            }
            updateDrawable();
        }

        @Override
        protected void onDraw(Canvas canvas) {
//            if (!animUpdateDrawable) {
//                updateDrawable();
//            }
            super.onDraw(canvas);
        }

        private void updateDrawable() {
            mStrokeColor = mStrokeColor == null ? ColorStateList.valueOf(Color.TRANSPARENT) : mStrokeColor;
            mCheckedStrokeColor = mCheckedStrokeColor == null ? mStrokeColor : mCheckedStrokeColor;
            updateDrawable(!isChecked ? mStrokeColor.getDefaultColor() : mCheckedStrokeColor.getDefaultColor());
        }

        private void updateDrawable(int strokeColor) {

            int mbackgroundColor;
            if (isChecked) {
                mbackgroundColor = selectedBackgroundColor;
            } else {
                mbackgroundColor = backgroundColor;
            }

            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadii(mRadius);
            drawable.setColor(mbackgroundColor);
            drawable.setStroke(mStrokeWidth, strokeColor);

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                this.setBackgroundDrawable(drawable);
            } else {
                this.setBackground(drawable);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    getDrawingRect(mOutRect);
                    invalidatePaint();
                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!mOutRect.contains((int) event.getX(), (int) event.getY())) {
                        invalidatePaint();
                        invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    invalidatePaint();
                    invalidate();
                    break;
                }
            }
            return super.onTouchEvent(event);
        }
    }
}
