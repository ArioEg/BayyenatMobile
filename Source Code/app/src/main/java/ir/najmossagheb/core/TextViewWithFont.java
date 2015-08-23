package ir.najmossagheb.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import ir.najmossagheb.R;

public class TextViewWithFont extends TextView {
    private int defaultDimension = 0;
    private int TYPE_BOLD = 1;
    private int TYPE_ITALIC = 2;
    private int FONT_MjFlow = 1;
    private int fontType;
    private int fontName;

    public TextViewWithFont(Context context) {
        super(context);
        init(null, 0);
    }
    public TextViewWithFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public TextViewWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.font, defStyle, 0);
        fontName = a.getInt(R.styleable.font_name, defaultDimension);
        fontType = a.getInt(R.styleable.font_type, defaultDimension);
        a.recycle();
        if (fontName == FONT_MjFlow) {
            Typeface MjFlow = Typeface
                    .createFromAsset(getContext().getAssets(), "MjFlow.ttf" );
            setFontType(MjFlow);
        }
    }
    private void setFontType(Typeface font) {
        if (fontType == TYPE_BOLD) {
            setTypeface(font, Typeface.BOLD);
        } else if (fontType == TYPE_ITALIC) {
            setTypeface(font, Typeface.ITALIC);
        } else {
            setTypeface(font);
        }
    }
}