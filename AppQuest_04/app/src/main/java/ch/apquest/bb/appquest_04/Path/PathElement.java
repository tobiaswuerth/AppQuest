package ch.apquest.bb.appquest_04.Path;

import ch.apquest.bb.appquest_04.Other.Enums;

public class PathElement {

    //region Fields

    private Enums.ORIENTATION orientation;

    private int totalStepsTodo;
    private int stepsDone = 0;

    private boolean isDone = false;
    private boolean isCurrent = false;

    //endregion

    //region  General Methods

    public boolean isCurrent() {
        return isCurrent;
    }

    public PathElement setIsCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
        return this;
    }

    public boolean isDone() {
        return isDone;
    }

    public PathElement setDone(boolean done) {
        this.isDone = done;
        return this;
    }

    public Enums.ORIENTATION getOrientation() {
        return orientation;
    }

    public PathElement setOrientation(Enums.ORIENTATION orientation) {
        this.orientation = orientation;
        return this;
    }

    public int getTotalStepsTodo() {
        return totalStepsTodo;
    }

    public PathElement setTotalStepsTodo(int totalStepsTodo) {
        this.totalStepsTodo = totalStepsTodo;
        return this;
    }

    public int getStepsDone() {
        return stepsDone;
    }

    public PathElement setStepsDone(int stepsDone) {
        this.stepsDone = stepsDone;
        return this;
    }

    // endregion

}
