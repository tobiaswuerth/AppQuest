package ch.appquest.bb.appquest_05.Fragments.ColorPickerFragment;

import android.content.Context;
import android.graphics.*;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class AmbilWarnaKotak extends View {
    Paint paint;
    Shader luar;
    final float[] color = {1.f, 1.f, 1.f};

    public AmbilWarnaKotak(Context context) {
        this(context, null);

        setSoftwareLayerType();
    }

    public AmbilWarnaKotak(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

        setSoftwareLayerType();
    }

    public AmbilWarnaKotak(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setSoftwareLayerType();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (paint == null) {
            paint = new Paint();
            luar = new LinearGradient(0.f, 0.f, 0.f, this.getMeasuredHeight(), 0xffffffff, 0xff000000, TileMode.CLAMP);
        }
        int rgb = Color.HSVToColor(color);
        Shader dalam = new LinearGradient(0.f, 0.f, this.getMeasuredWidth(), 0.f, 0xffffffff, rgb, TileMode.CLAMP);
        ComposeShader shader = new ComposeShader(luar, dalam, PorterDuff.Mode.MULTIPLY);
        paint.setShader(shader);
        canvas.drawRect(0.f, 0.f, this.getMeasuredWidth(), this.getMeasuredHeight(), paint);
    }

    void setHue(float hue) {
        color[0] = hue;
        invalidate();
    }

    /**
     * For Android 3.0+ software layer type should be set for correct color blending.
     */
    void setSoftwareLayerType() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }
}
