package com.optimalsolutions.fadfed.emojicon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.optimalsolutions.fadfed.R;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@SuppressLint("AppCompatCustomView")
public class EmojiconEditText extends EditText {
    private int mEmojiconSize;

    public EmojiconEditText(Context context) {
        super(context);
        mEmojiconSize = (int) getTextSize();

    }

    public EmojiconEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiconEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
        mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
        a.recycle();
        setText(getText());
    }

    public String getURLString() throws UnsupportedEncodingException {

        return  URLEncoder.encode(StringEscapeUtils.escapeJava(getText().toString()), "utf-8");

    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        EmojiconHandler.addEmojis(getContext(), getText(), mEmojiconSize);
    }


    public void setText(String text) {
        super.setText(StringEscapeUtils.unescapeJava(text));
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }
}
