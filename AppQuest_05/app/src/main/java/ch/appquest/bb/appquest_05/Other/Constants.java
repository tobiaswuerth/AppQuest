package ch.appquest.bb.appquest_05.Other;

import android.graphics.Color;

import java.util.Arrays;
import java.util.List;

import ch.appquest.bb.appquest_05.Drawing.DrawingMode;

public class Constants {

    public static final List<Integer> DEFAULT_COLORS = Arrays.asList(
            Color.parseColor("#1364B7"), Color.parseColor("#13B717"), Color.parseColor("#FFEA00"), Color.BLACK, Color.WHITE,
            Color.parseColor("#330000"),
            Color.parseColor("#660000"), Color.parseColor("#990000"), Color.parseColor("#CC0000"), Color.parseColor("#FF0000"),
            Color.parseColor("#FF3333"), Color.parseColor("#FF6666"), Color.parseColor("#FF9999"), Color.parseColor("#FFCCCC"),
            Color.parseColor("#331900"), Color.parseColor("#663300"), Color.parseColor("#994C00"), Color.parseColor("#CC6600"),
            Color.parseColor("#FF8000"), Color.parseColor("#FF9933"), Color.parseColor("#FFB266"), Color.parseColor("#FFCC99"),
            Color.parseColor("#FFE5CC"), Color.parseColor("#333300"), Color.parseColor("#666600"), Color.parseColor("#999900"),
            Color.parseColor("#CCCC00"), Color.parseColor("#FFFF00"), Color.parseColor("#FFFF33"), Color.parseColor("#FFFF66"),
            Color.parseColor("#FFFF99"), Color.parseColor("#FFFFCC"), Color.parseColor("#193300"), Color.parseColor("#336600"),
            Color.parseColor("#4C9900"), Color.parseColor("#66CC00"), Color.parseColor("#80FF00"), Color.parseColor("#99FF33"),
            Color.parseColor("#B2FF66"), Color.parseColor("#CCFF99"), Color.parseColor("#E5FFCC"), Color.parseColor("#003300"),
            Color.parseColor("#006600"), Color.parseColor("#009900"), Color.parseColor("#00CC00"), Color.parseColor("#00FF00"),
            Color.parseColor("#33FF33"), Color.parseColor("#66FF66"), Color.parseColor("#99FF99"), Color.parseColor("#CCFFCC"),
            Color.parseColor("#003319"), Color.parseColor("#006633"), Color.parseColor("#00994C"), Color.parseColor("#00CC66"),
            Color.parseColor("#00FF80"), Color.parseColor("#33FF99"), Color.parseColor("#66FFB2"), Color.parseColor("#99FFCC"),
            Color.parseColor("#CCFFE5"), Color.parseColor("#003333"), Color.parseColor("#006666"), Color.parseColor("#009999"),
            Color.parseColor("#00CCCC"), Color.parseColor("#00FFFF"), Color.parseColor("#33FFFF"), Color.parseColor("#66FFFF"),
            Color.parseColor("#99FFFF"), Color.parseColor("#CCFFFF"), Color.parseColor("#001933"), Color.parseColor("#003366"),
            Color.parseColor("#004C99"), Color.parseColor("#0066CC"), Color.parseColor("#0080FF"), Color.parseColor("#3399FF"),
            Color.parseColor("#66B2FF"), Color.parseColor("#99CCFF"), Color.parseColor("#CCE5FF"), Color.parseColor("#000033"),
            Color.parseColor("#000066"), Color.parseColor("#000099"), Color.parseColor("#0000CC"), Color.parseColor("#0000FF"),
            Color.parseColor("#3333FF"), Color.parseColor("#6666FF"), Color.parseColor("#9999FF"), Color.parseColor("#CCCCFF"),
            Color.parseColor("#190033"), Color.parseColor("#330066"), Color.parseColor("#4C0099"), Color.parseColor("#6600CC"),
            Color.parseColor("#7F00FF"), Color.parseColor("#9933FF"), Color.parseColor("#B266FF"), Color.parseColor("#CC99FF"),
            Color.parseColor("#E5CCFF"), Color.parseColor("#330033"), Color.parseColor("#660066"), Color.parseColor("#990099"),
            Color.parseColor("#CC00CC"), Color.parseColor("#FF00FF"), Color.parseColor("#FF33FF"), Color.parseColor("#FF66FF"),
            Color.parseColor("#FF99FF"), Color.parseColor("#FFCCFF"), Color.parseColor("#330019"), Color.parseColor("#660033"),
            Color.parseColor("#99004C"), Color.parseColor("#CC0066"), Color.parseColor("#FF007F"), Color.parseColor("#FF3399"),
            Color.parseColor("#FF66B2"), Color.parseColor("#FF99CC"), Color.parseColor("#FFCCE5"), Color.parseColor("#000000"),
            Color.parseColor("#202020"), Color.parseColor("#404040"), Color.parseColor("#606060"), Color.parseColor("#808080"),
            Color.parseColor("#A0A0A0"), Color.parseColor("#C0C0C0"), Color.parseColor("#E0E0E0"), Color.parseColor("#FFFFFF")
    );

    public static final int COLOR_SELECTOR_DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    public static final int DRAWING_PITCH_CELL_DEFAULT_COLOR = Color.WHITE;
    public static final int KEY_LOAD_IMAGE = 0;
    public static final DrawingMode INITIAL_DRAWING_MODE = DrawingMode.PEN;
    public static final int COLOR_SELECTOR_SIZE = 175;
    public static final int INITIAL_DRAWING_PITCH_SIZE = 11;
    public static final int DRAWING_PITCH_SIZE_MAX = 100;
    public static final int DRAWING_PITCH_SIZE_MIN = 2;
    public static final String KEY_CURRENT_VALUE = "CurrentValue";
    public static final int MAX_SAVED_UNDO_IN_CACHE = 5;

}
