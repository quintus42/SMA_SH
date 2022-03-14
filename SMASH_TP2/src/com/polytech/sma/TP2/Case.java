/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.polytech.sma.TP2;

/**
 *
 * @author Epulapp
 */
public class Case {
    private TypeObjet type;
    private Agent robot;
    private Agent robot2;
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

    public Agent getRobot2() {
        return robot2;
    }

    public void removeRobot(Agent robot){
        if(robot == this.robot){
            this.robot = null;
        }else if(robot == this.robot2){
            this.robot2 = null;
        }
    }

    public void setRobot(Agent robot) {
        //Dans cette fonction on gère le premier robot
        // mais aussi le robot2 pour les objets C
        if(robot == null){
            this.robot = null;
            this.robot2 = null;
        }else{
            //On essaie de remplir robot ou robot2
            if(this.robot == null){
                this.robot = robot;
            }else{
                this.robot2 = robot;
            }
        }
    }

//    public void setRobot2(Agent robot2) {
//        this.robot2 = robot2;
//    }

    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return "{" + x +
                ", " + y +
                '}';
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

    public Agent getOtherRobot(Agent robot){
        if (this.robot == robot){
            return this.robot2;
        }else if(this.robot2 == robot){
            return this.robot;
        }else{
            return null;
        }
    }
}
