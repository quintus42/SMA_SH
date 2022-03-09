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
public class Agent {
    
    private Grid env;
    private TypeObjet carriedObjet;
    private Case position;
    private Direction direction;
    
    private double fa;
    private double fb;

    public Case getPosition() {
        return position;
    }

    public void setPosition(Case position) {
        this.position = position;
    }

    public double getFa() {
        return fa;
    }

    public void setFa(double fa) {
        this.fa = fa;
    }

    public double getFb() {
        return fb;
    }

    public void setFb(double fb) {
        this.fb = fb;
    }
    
    private ShortTermMemory<TypeObjet> memory;

    public Agent(Grid env, int tMemory) {
        this.env = env;
        this.carriedObjet = TypeObjet.EMPTY;
        this.memory = new ShortTermMemory<TypeObjet>(tMemory);
    }
    
    public void perception(){
        //Récupérer les infos depuis l'environnement
        //(voisinage + case libre + direction)
        env.localePerception(this);
    }
    
    public void action(){
        //Déplacement
        env.moveRobot(this, direction);
        
        //Essayer de prendre/déposer
        if(carriedObjet != TypeObjet.EMPTY){
            if(position.getType() == TypeObjet.EMPTY){
                //On essaye de poser l'objet
                env.tryDropObject(this);
            }
            //Sinon on ne fait rien
        }else{
            if(position.getType() != TypeObjet.EMPTY){
                //On essaye de prendre l'objet
                env.tryTakeObject(this);
            }
            //Sinon on ne fait rien
        }
    }
    
    public boolean addToMemory(TypeObjet c){
        return this.memory.add(c);
    }
    
    public ShortTermMemory getMemory(){
        return memory;
    }

    public TypeObjet getObjet() {
        return carriedObjet;
    }

    public void setObjet(TypeObjet objet) {
        this.carriedObjet = objet;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
