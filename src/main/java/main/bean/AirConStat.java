package main.bean;

import java.io.Serializable;

/**
 * @author z
 */
public class AirConStat implements Serializable{


    private static final long serialVersionUID = -6986512190123650249L;

    private int stat;
    private int module;
    private float temp;
    private int speed;
    private int shake;
    private int winDir;
    private int timerOn;
    private int timerOff;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
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

    public int getWinDir() {
        return winDir;
    }

    public void setWinDir(int winDir) {
        this.winDir = winDir;
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
}
