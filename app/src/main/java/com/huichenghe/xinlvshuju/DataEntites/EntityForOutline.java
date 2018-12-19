package com.huichenghe.xinlvshuju.DataEntites;

/**
 * Created by lixiaoning on 15-12-24.
 */
public class EntityForOutline
{
    private int movementType;
    private String movementName;
    private String movementHRRound;
    private String movementTime;
    private String movementComp;

    public EntityForOutline(int movementType, String movementComp, String movementTime, String movementHRRound, String movementName) {
        this.movementType = movementType;
        this.movementComp = movementComp;
        this.movementTime = movementTime;
        this.movementHRRound = movementHRRound;
        this.movementName = movementName;
    }


    public String getMovementComp() {
        return movementComp;
    }

    public String getMovementTime() {
        return movementTime;
    }

    public String getMovementHRRound() {
        return movementHRRound;
    }

    public String getMovementName() {
        return movementName;
    }

    public int getMovementType() {
        return movementType;
    }

    public void setMovementType(int movementType) {
        this.movementType = movementType;
    }

    public void setMovementName(String movementName) {
        this.movementName = movementName;
    }

    public void setMovementHRRound(String movementHRRound) {
        this.movementHRRound = movementHRRound;
    }

    public void setMovementTime(String movementTime) {
        this.movementTime = movementTime;
    }

    public void setMovementComp(String movementComp) {
        this.movementComp = movementComp;
    }
}
