/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.polytech.sma.TP2;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Epulapp
 */
public class ShortTermMemory<T> extends LinkedList<T>{
    private int size;

    public ShortTermMemory(int size) {
        this.size = size;
    }

    @Override
    public boolean add(T e) {
        boolean added = super.add(e);
        if (added && super.size() > size){
            super.remove();
        }
        return added;
    }
    
    
    
}
