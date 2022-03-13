/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.polytech.sma.sma.sh_tp2;

/**
 *
 * @author Epulapp
 */
public class Case {
    private TypeObjet type;
    private Agent robot;
    private int x;
    private int y;
    private double signal;

    public Case() {
        this.type = TypeObjet.EMPTY;
        this.signal = 0;
    }
    
    public Case(TypeObjet type) {
        this.type = type;
        this.signal = 0;
    }   
    
    public Case(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = TypeObjet.EMPTY;
        this.signal = 0;
    }     

    public TypeObjet getType() {
        return type;
    }

    public void setType(TypeObjet type) {
        this.type = type;
    }

    public Agent getRobot() {
        return robot;
    }

    public void setRobot(Agent robot) {
        this.robot = robot;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getSignal() {
        return signal;
    }

    public void setSignal(double signal) {
        this.signal = signal;
    }

    public void deprecateSignal(double r){
        signal = (1-r)*signal;
    }


}
