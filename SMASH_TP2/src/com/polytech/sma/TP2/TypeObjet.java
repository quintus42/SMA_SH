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
public enum TypeObjet {
    EMPTY('O'), 
    A('A'),
    B('B'),
    C('C');
 
    private Character type;
 
    TypeObjet(Character envUrl) {
        this.type = envUrl;
    }
    
    public Character getType(){
        return type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
 
    
}
