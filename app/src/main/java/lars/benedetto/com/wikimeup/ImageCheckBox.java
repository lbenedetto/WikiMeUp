package lars.benedetto.com.wikimeup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class ImageCheckBox extends AppCompatImageView implements OnClickListener {
    private OnClickListener listener;
    boolean isChecked = true;
    Drawable checked;
    Drawable unchecked;

    public ImageCheckBox(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public ImageCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ImageCheckBox,
                0, 0);
        checked = a.getDrawable(R.styleable.ImageCheckBox_checkedDrawable);
        unchecked = a.getDrawable(R.styleable.ImageCheckBox_uncheckedDrawable);
        setImageDrawable(checked);
        setOnClickListener(this);
        bringToFront();
    }

    public void setChecked(boolean c) {
        isChecked = c;
        setImageDrawable(c ? checked : unchecked);
    }

    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            setChecked(!isChecked);
            if (listener != null) {
                listener.onClick(null);
            }
        }
        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {

    }
}