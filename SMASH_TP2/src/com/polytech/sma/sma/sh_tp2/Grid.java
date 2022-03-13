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
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private double e;

    private int dS;
    private double iS;
    
    private Case[][] grid;

    public Grid(int n, int m, int nbA, int nbB, int nbAgents, int memoSize,
            int I, double kPlus, double kMoins, double e, int dS) {
        this.N = n;
        this.M = m;
        this.nbObjA = nbA;
        this.nbObjB = nbB;
        this.nbAgents = nbAgents;
        this.agentMemorySize = memoSize;
        this.I = I;
        this.kPlus = kPlus;
        this.kMoins = kMoins;
        this.e = e;
        this.dS = dS;
        
        grid = new Case[N][M];
        for (int i = 0; i < N; i++) {
            grid[i] = new Case[M];
            for (int j = 0; j < M; j++) {
                grid[i][j] = new Case(i, j);
            }
        }
        
        //Vérification préalables
        if((nbObjA + nbObjB + nbAgents) > N*M){
            //On a trop d'objets à placer ==> ERREUR
            return;
        }
        
        //Initialisation des robots
        agents = new ArrayList<>();
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
        
        printGrid();
        
        sortGrid();
    }
    
    public void sortGrid(){
        int iter = 0;
        while(true){
            iter++;

            //On choisit aléatoirement un robot
            Agent r = nextRobot(true);

            //Perception du robot
            r.perception();

            //Action du robot
            r.action();

            if(iter%10000==0){
                System.out.println("Itération : " + iter);
                printGrid();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Grid.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
    }

    private void spreadSignal(Agent aThis){
        Case currentPosition = aThis.getPosition();
        for (int x = currentPosition.getX() - dS; x < currentPosition.getX() + dS; x++) {
            for (int y = currentPosition.getY() - dS; y < currentPosition.getY() + dS; y++) {
                if(0 <= x && x < N
                        && 0 <= y && y < M){
                    int dist = Math.max(Math.abs(x), Math.abs(y));
                    double rS = 1; //reduction du signal
                    for (int i = 1; i <= dist; i++) {
                        rS += rS - iS/i;
                    }
                    grid[x][y].setSignal(iS+rS);
                }
            }
        }
    }

    private void clearSignal(Agent aThis){
        Case currentPosition = aThis.getPosition();
        for (int x = currentPosition.getX() - dS; x < currentPosition.getX() + dS; x++) {
            for (int y = currentPosition.getY() - dS; y < currentPosition.getY() + dS; y++) {
                if(0 <= x && x < N
                        && 0 <= y && y < M){
                    grid[x][y].setSignal(0);
                }
            }
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
        if (direction == null) {
            //On ne fait rien
        }else{
            Case currentPosition = aThis.getPosition();
            int nextX = currentPosition.getX();
            int nextY = currentPosition.getY();
            switch (direction) {
                case NORD -> nextY -= I;
                case NORD_EST -> {
                    nextY -= I;
                    nextX += I;
                }
                case EST -> nextX += I;
                case SUD_EST -> {
                    nextY += I;
                    nextX += I;
                }
                case SUD -> nextY += I;
                case SUD_OUEST -> {
                    nextY += I;
                    nextX -= I;
                }
                case OUEST -> nextX -= I;
                case NORD_OUEST -> {
                    nextY -= I;
                    nextX -= I;
                }
            }
            currentPosition.setRobot(null);
            Case newPos = grid[nextX][nextY];
            newPos.setRobot(aThis);
            aThis.setPosition(newPos);
        }
    }

    void localePerception(Agent aThis) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if(grid[i][j].getRobot() == aThis){
                    Case currentCase = grid[i][j];

                    //On ajoute la case courante à la mémoire du robot
                    aThis.addToMemory(currentCase.getType());

                    //On calcule les variables en fonction de son environnement
                    aThis.setFa(calculFrequency(aThis, TypeObjet.A));
                    aThis.setFb(calculFrequency(aThis, TypeObjet.B));

                    aThis.setPosition(currentCase);

                    if(aThis.getPosition().getSignal() > 0){
                        setDirectionSignal(aThis);
                    }else{
                        setRandomDirection(aThis);
                    }

                }
            }
        }
    }

    private void setRandomDirection(Agent aThis){
        List<Direction> possibleDirs = getFreeDirection(aThis);
        if (possibleDirs.size() > 0) {
            Random rand = new Random();
            aThis.setDirection(possibleDirs.get(rand.nextInt(possibleDirs.size())));
        }else{
            aThis.setDirection(null);
        }
    }

    private void setDirectionSignal(Agent aThis){
        Case currentPosition = aThis.getPosition();
        Case maxSignal = new Case();
        for (int x = currentPosition.getX() - 1; x < currentPosition.getX() + 1; x++) {
            for (int y = currentPosition.getY() - 1; y < currentPosition.getY() + 1; y++) {
                if(0 <= x && x < N
                        && 0 <= y && y < M){
                    Case cS = grid[x][y];
                    if (cS.getSignal() > maxSignal.getSignal()) {
                        maxSignal = cS;
                    }
                }
            }
        }

        Random rand = new Random();
        if(rand.nextDouble() <= maxSignal.getSignal()){
            //On suit la direction du signal
            aThis.setDirection(getDirectionOfCase(aThis.getPosition(), maxSignal));
        }else{
            //On tire une direction au hasard
            setRandomDirection(aThis);
        }
    }

    private Direction getDirectionOfCase(Case current, Case target){
        int curX = current.getX();
        int curY = current.getY();
        int tarX = target.getX();
        int tarY = target.getY();
        if(tarX > curX){
            if(tarY > curY){
                return Direction.SUD_EST;
            }else if(tarY < curY){
                return Direction.NORD_EST;
            }else{
                return Direction.EST;
            }
        }else if(tarX < curX){
            if(tarY > curY){
                return Direction.SUD_OUEST;
            }else if(tarY < curY){
                return Direction.NORD_OUEST;
            }else{
                return Direction.OUEST;
            }
        }else{
            if(tarY > curY){
                return Direction.SUD;
            }else if(tarY < curY){
                return Direction.NORD;
            }else{
                return null; //C'est la case current
            }
        }
    }

    private double calculFrequency(Agent aThis, TypeObjet type){
        int nbType = Collections.frequency(aThis.getMemory(), type);

        double nbOtherType = 0;
        TypeObjet.class.getEnumConstants();
        for (TypeObjet t : TypeObjet.class.getEnumConstants()) {
            if (t != type && t != TypeObjet.EMPTY){
                nbOtherType += Collections.frequency(aThis.getMemory(), t);
            }
        }
        double error = (nbOtherType*e);
        double f = ((nbType+error)/(double)agentMemorySize);

        return f;
    }


    private double probaTake(double f){
        return Math.pow((kPlus / (kPlus + f)), 2);
    }
    void tryTakeObject(Agent aThis) {
        double proba = 0;
        if(aThis.getPosition().getType() == TypeObjet.A){
            proba = probaTake(aThis.getFa());
        }else if(aThis.getPosition().getType() == TypeObjet.B){
            proba = probaTake(aThis.getFb());
        }
        Random rand = new Random();
        if(rand.nextDouble()<= proba){
            //On prend l'objet
            aThis.setObjet(aThis.getPosition().getType());
            aThis.getPosition().setType(TypeObjet.EMPTY);
        }
    }


    private double probaDrop(double f){
        return Math.pow((f / (kMoins + f)), 2);
    }
    void tryDropObject(Agent aThis) {
        double proba = 0;
        if(aThis.getObjet() == TypeObjet.A){
            proba = probaDrop(aThis.getFa());
        }else if(aThis.getObjet() == TypeObjet.B){
            proba = probaDrop(aThis.getFb());
        }
        Random rand = new Random();
        if(rand.nextDouble()<= proba){
            //On pose l'objet
            aThis.getPosition().setType(aThis.getObjet());
            aThis.setObjet(TypeObjet.EMPTY);
        }
    }

    private List<Direction> getFreeDirection(Agent agent) {
        List<Direction> freeDirections = new ArrayList<>();
        List<Direction> directions = Arrays.asList(Direction.values());

        Case currentPosition = agent.getPosition();
        int currentX = currentPosition.getX();
        int currentY = currentPosition.getY();

        for (Direction direction : directions) {
            int nextX = currentX;
            int nextY = currentY;
            switch(direction){
                case NORD:
                    nextY -= I;
                    break;
                case NORD_EST:
                    nextY -= I;
                    nextX += I;
                    break;
                case EST:
                    nextX += I;
                    break;
                case SUD_EST:
                    nextY += I;
                    nextX += I;
                    break;
                case SUD:
                    nextY += I;
                    break;
                case SUD_OUEST:
                    nextY += I;
                    nextX -= I;
                    break;
                case OUEST:
                    nextX -= I;
                    break;
                case NORD_OUEST:
                    nextY -= I;
                    nextX -= I;
                    break;
            }
            if(0 <= nextX && nextX < N
                    && 0 <= nextY && nextY < M){
                //Position existante dans la grid, on vérifie sa dispo
                if(grid[nextX][nextY].getRobot()==null){
                    //On ajoute la direction dans la liste
                    freeDirections.add(direction);
                }
            }
        }
        return freeDirections;
    }


    public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_WHITE = "\u001B[37m";
    private void printGrid(){
        System.out.println("Grille :");
        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < M; j++) {
                if(grid[i][j].getRobot() != null){
                    System.out.print(TEXT_RED);
                    System.out.print("R");
                    System.out.print(TEXT_RESET);
                    System.out.print("|"); 
                }else{
                    switch(grid[i][j].getType()){
                        case A:
                            System.out.print(TEXT_GREEN);
                            break;
                        case B:
                            System.out.print(TEXT_BLUE);
                            break;
                    }
                    System.out.print(grid[i][j].getType());
                    System.out.print(TEXT_RESET);
                    System.out.print("|");  
                }
            }
            System.out.print("\n");
        }
        System.out.println("");
    }
    
}
