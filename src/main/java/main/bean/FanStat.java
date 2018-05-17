package main.bean;

import java.io.Serializable;

public class FanStat implements Serializable{
    private static final long serialVersionUID = -8769466157051603435L;

    private int stat;
    private int speed;
    private int shake;
    private int timerOn;
    private int timerOff;
    private String time;

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getShake() {
        return shake;
    }

    public void setShake(int shake) {
        this.shake = shake;
    }

    public int getTimerOn() {
        return timerOn;
    }

    public void setTimerOn(int timerOn) {
        this.timerOn = timerOn;
    }

    public int getTimerOff() {
        return timerOff;
    }

    public void setTimerOff(int timerOff) {
        this.timerOff = timerOff;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
