package ch.apquest.bb.appquest_04.Other;

import ch.apquest.bb.appquest_04.Activities.PathActivity;

public abstract class Constants {

    // Request Codes
    public static final Integer RC_SCAN_QR_CODE = 0;
    public static final Integer RC_CHECK_TTS_DATA = 1;

    // Tags
    public static final String TAG_PATHACTIVITY = PathActivity.class.getSimpleName();

    // Shared Preferences
    public static final String SP_SETTINGS_SHOWPLOT = TAG_PATHACTIVITY + "_SHOWPLOT";

    // Intent Extras
    public static final String IE_PATH = TAG_PATHACTIVITY + "_PATH";

    // StepController configuration
    public static final Integer SC_STEP_MEASUREMENT_COUNT = 75;
    public static final Float SC_STEP_ACCELERATION_MODIFIER_TOP = 1.25f;
    public static final Float SC_STEP_ACCELERATION_MODIFIER_BOTTOM = 0.75f;
    public static final Float SC_STEP_MAGNITUDE_APLIFIER = 2.0f;

    // TurnController
    public static final Integer TC_BUFFER_SIZE = 10;
}
