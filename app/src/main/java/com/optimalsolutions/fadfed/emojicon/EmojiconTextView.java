package com.optimalsolutions.fadfed.emojicon;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;


import com.optimalsolutions.fadfed.R;

import org.apache.commons.lang3.StringEscapeUtils;


public class EmojiconTextView extends android.support.v7.widget.AppCompatTextView {

    private int mEmojiconSize;
    private int mTextStart = 0;
    private int mTextLength = -1;

    public EmojiconTextView(Context context) {
        super(context);
        init(null);
    }

    public EmojiconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        if (attrs == null) {
            mEmojiconSize = (int) getTextSize();

        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
            mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
            mTextStart = a.getInteger(R.styleable.Emojicon_emojiconTextStart, 0);
            mTextLength = a.getInteger(R.styleable.Emojicon_emojiconTextLength, -1);
            a.recycle();
        }
        setText(getText());
    }


    public void setText(String text) {
        try {

            String parsedtext = StringEscapeUtils.unescapeJava(text.trim());
            super.setText(parsedtext);

        }catch(Exception e){
//            super.setText(text);
            e.printStackTrace();
        }


//        super.setText(StringEscapeUtils.unescapeJava(text));


    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        EmojiconHandler.addEmojis(getContext(), builder, mEmojiconSize, mTextStart, mTextLength);
        super.setText(builder, type);
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }
}
