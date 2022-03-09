/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.polytech.sma.sma.sh_tp2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Epulapp
 */
public class Grid {
    private int N;
    private int M;
    private int nbObjA;
    private int nbObjB;
    private int nbAgents;
    private ArrayList<Agent> agents;
    private int agentMemorySize;
    private int I;
    
    private double kPlus;
    private double kMoins;
    
    private Case[][] grid;

    public Grid(int n, int m, int nbA, int nbB, int nbAgents, int memoSize, 
            double kPlus, double kMoins, int I) {
        this.N = n;
        this.M = m;
        this.nbObjA = nbA;
        this.nbObjB = nbB;
        this.nbAgents = nbAgents;
        this.agentMemorySize = memoSize;
        this.kPlus = kPlus;
        this.kMoins = kMoins;
        this.I = I;
        
        grid = new Case[N][M];
        for (int i = 0; i < N; i++) {
            grid[i] = new Case[M];
            for (int j = 0; j < M; j++) {
                grid[i][j] = new Case();
            }
        }
        
        //Vérification préalables
        if((nbObjA + nbObjB + nbAgents) > N*M){
            //On a trop d'objets à placer ==> ERREUR
            return;
        }
        
        //Initialisation des robots
        for (int i = 0; i < this.nbAgents; i++) {
            agents.add(new Agent(this, agentMemorySize));
        }
        int nbRPlaced = 0;
        while(nbRPlaced < this.nbAgents){
            Random rand = new Random();
            int randN = rand.nextInt(N);
            int randM = rand.nextInt(M);
            if(grid[randN][randM].getType() == TypeObjet.EMPTY){
                grid[randN][randM].setRobot(agents.get(nbRPlaced));
                nbRPlaced++;
            }
        }
        
        //Initialisation des objets A;
        int nbAPlaced = 0;
        while(nbAPlaced < nbObjA){
            Random rand = new Random();
            int randN = rand.nextInt(N);
            int randM = rand.nextInt(M);
            if(grid[randN][randM].getType() == TypeObjet.EMPTY){
                grid[randN][randM].setType(TypeObjet.A);
                nbAPlaced++;
            }
        }
        
        //Initialisation des objets B;
        int nbBPlaced = 0;
        while(nbBPlaced < nbObjB){
            Random rand = new Random();
            int randN = rand.nextInt(N);
            int randM = rand.nextInt(M);
            if(grid[randN][randM].getType() == TypeObjet.EMPTY){
                grid[randN][randM].setType(TypeObjet.B);
                nbBPlaced++;
            }
        }
        
        //printGrid();
    }
    
    public void sortGrid(){
        int iter = 0;
        while(true && iter < 10000){
            //On choisit aléatoirement un robot
            Agent r = nextRobot(true);
            
            //Perception du robot
            r.perception();
            
            //Action du robot
            r.action();
            
        }
    }

    private int currentRobotIndex;
    private Agent nextRobot(boolean alea) {
        if(alea){
            Random r = new Random();
            int rand = r.nextInt(agents.size());
            return agents.get(rand);
        }else{
            currentRobotIndex++;
            currentRobotIndex = currentRobotIndex%agents.size();
            return agents.get(currentRobotIndex);
        }
    }

    void moveRobot(Agent aThis, Direction direction) {
    }

    void localePerception(Agent aThis) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if(grid[i][j].getRobot() == aThis){
                    Case currentCase = grid[i][j];                    
                    
                    //On ajoute la case courante à la mémoire du robot
                    aThis.addToMemory(currentCase.getType());
                    
                    //On calcule les variables en fonction de son environnement 
                    aThis.setFa(Collections.frequency(aThis.getMemory(),
                            TypeObjet.A)/agentMemorySize);
                    aThis.setFb(Collections.frequency(aThis.getMemory(),
                            TypeObjet.B)/agentMemorySize);
                    
                    aThis.setPosition(currentCase);
                    
                    //On récupère les positions dispo et on en tire une au hasard
                    List<Direction> possibleDirs = getFreeDirection();
                    aThis.setDirection(Direction.randomDirection());
                }
            }
        }
    }

    void tryTakeObject(Agent aThis) {
        double proba = 0;
        if(aThis.getPosition().getType() == TypeObjet.A){            
            proba = Math.pow((kPlus / (kPlus + aThis.getFa())), 2);
        }else if(aThis.getPosition().getType() == TypeObjet.A){            
            proba = Math.pow((kPlus / (kPlus + aThis.getFb())), 2);
        }
        Random rand = new Random();
        if(rand.nextDouble()<= proba){
            //On prend l'objet
            aThis.setObjet(aThis.getPosition().getType());
            aThis.getPosition().setType(TypeObjet.EMPTY);
        }
    }

    void tryDropObject(Agent aThis) {        double proba = 0;
        if(aThis.getPosition().getType() == TypeObjet.A){            
            proba = Math.pow((aThis.getFa() / (kPlus + aThis.getFa())), 2);
        }else if(aThis.getPosition().getType() == TypeObjet.A){            
            proba = Math.pow((aThis.getFb() / (kPlus + aThis.getFb())), 2);
        }
        Random rand = new Random();
        if(rand.nextDouble()<= proba){
            //On pose l'objet
            aThis.getPosition().setType(aThis.getObjet());
            aThis.setObjet(TypeObjet.EMPTY);
        }
    }
    
    private void printGrid(){
                System.out.println("Grille :");
        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < M; j++) {
                if(grid[i][j].getRobot() != null){
                    System.out.print(" R |"); 
                }else{
                    System.out.print(" " + grid[i][j].getType() + " |");  
                }    
            }
            System.out.print("\n");
        }
        System.out.println("");
    }

    private List<Direction> getFreeDirection() {
        List<Direction> directions = Arrays.asList(Direction.values());
        for (Direction direction : directions) {
            
        }
        return null;
    }
    
    
}
