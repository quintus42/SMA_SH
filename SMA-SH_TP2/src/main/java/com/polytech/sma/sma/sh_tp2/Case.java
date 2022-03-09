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

    public Case() {
        this.type = TypeObjet.EMPTY;
    }
    
    public Case(TypeObjet type) {
        this.type = type;
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
}
