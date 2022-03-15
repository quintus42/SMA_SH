/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.polytech.sma.TP2;

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

    // <editor-fold defaultstate="collapsed" desc="Variables">
    private int N;
    private int M;
    private int nbObjA;
    private int nbObjB;
    private int nbObjC;
    private int nbAgents;
    private ArrayList<Agent> agents;
    private int agentMemorySize;
    private int I;
    
    private double kPlus;
    private double kMoins;
    private double e;

    private int dS;
    private double iS = 1;

    boolean printGridIter;
    // </editor-fold>
    
    private Case[][] grid;

    public Grid(int n, int m, int nbA, int nbB, int nbC, int nbAgents, int memoSize,
            int I, double kPlus, double kMoins, double e, int dS, boolean printGridIter) {
        this.N = n;
        this.M = m;
        this.nbObjA = nbA;
        this.nbObjB = nbB;
        this.nbObjC = nbC;
        this.nbAgents = nbAgents;
        this.agentMemorySize = memoSize;
        this.I = I;
        this.kPlus = kPlus;
        this.kMoins = kMoins;
        this.e = e;
        this.dS = dS;
        this.printGridIter = printGridIter;
        
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

        //Initialisation des objets C;
        int nbCPlaced = 0;
        while(nbCPlaced < nbObjC){
            Random rand = new Random();
            int randN = rand.nextInt(N);
            int randM = rand.nextInt(M);
            if(grid[randN][randM].getType() == TypeObjet.EMPTY){
                grid[randN][randM].setType(TypeObjet.C);
                nbCPlaced++;
            }
        }

        System.out.println("Début de l'exécution :");
        printGrid();
        
        sortGrid();

        updateCaseAgentFromGrid();//temporaire pour cause de bug ?
        System.out.println("Fin de l'exécution :");
        printGrid();
    }
    
    public void sortGrid(){
        int iter = 0;
        while(true && iter<5000000){
            iter++;

            //On choisit aléatoirement un robot
            Agent r = nextRobot(false);

            //Perception du robot
            r.perception();

            //Action du robot
            r.action();

            if(iter%20000==0){
                if(printGridIter){
                    System.out.println("Itération : " + iter);
                    printGrid();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Grid.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    System.out.println("Itération : " + iter);
                }

            }else{
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="Gestion des Agents">
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
            if(nextX < 0 || nextX >= N || nextY < 0 || nextY >= M){
                System.out.println("Error");
                return;
            }
            Case newPos = grid[nextX][nextY];
            if(aThis.getObjet() == TypeObjet.C){
                Agent otherRobot = aThis.getPosition().getOtherRobot(aThis);
                if(otherRobot != null){
                    newPos.setRobot(otherRobot);
                    currentPosition.removeRobot(otherRobot);
                    otherRobot.setPosition(newPos);
                }else{
                    aThis.increaseCptWaitingOnC();
                    //On calcul la proba d'abandonner l'objet avec le Logn(x)
                    //car log(1) = 0 et log(10) = 1
                    //donc plus le nombre de tour tend vers 10 plus la proba d'abandonner est proche de 1
                    double proba = Math.log10(aThis.getCptWaitingOnC());
                    Random rand = new Random();
                    if (rand.nextDouble() < proba){
                        clearSignal(aThis);
                        aThis.setObjet(TypeObjet.EMPTY);
                        setRandomDirection(aThis);
                        moveRobot(aThis, aThis.getDirection());
                        aThis.resetCptWaitingOnC();
                    }else{
                        //On veut que plus l'agent attend depuis longtemps moins il ait de chance de relancer le
                        // signal en remettant à 0 l'attente
                        //1-log(x) permet de varier entre 1 et 0 en se rapprochant de 10
                        //Mais 1-log(1) = 1 ce qui fait que l'agent bouclerait et relancerait en permanence l'appel
                        //donc on utilise 0.5-log(x) pour que lors du premier tour la proba de relancer ne soit pas de 1
                        // et on utilise la fonction max pour que dès que 0.5-log(x) < 0 (à savoir entre x=3 et x=4)
                        // la proba de relancer l'appel soit de 0
                        proba = Math.max(0.5*Math.log10(aThis.getCptWaitingOnC()), 0);
                        if (rand.nextDouble() < proba){
                            spreadSignal(aThis);
                            aThis.resetCptWaitingOnC();
                        }else{
                            //Et sinon on decrease le signal sur les cases
                            decreaseSignal(aThis);
                        }
                    }
                    return;
                }
            }
            newPos.setRobot(aThis);
            currentPosition.removeRobot(aThis);
            aThis.setPosition(newPos);
            aThis.setDirection(null);
        }
    }

    void localePerception(Agent aThis) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if(grid[i][j].getRobot() == aThis || grid[i][j].getRobot2() == aThis){
                    Case currentCase = grid[i][j];

                    //On ajoute la case courante à la mémoire du robot
                    aThis.addToMemory(currentCase.getType());

                    //On calcule les variables en fonction de son environnement
                    aThis.setFa(calculFrequency(aThis, TypeObjet.A));
                    aThis.setFb(calculFrequency(aThis, TypeObjet.B));
                    aThis.setFc(calculFrequency(aThis, TypeObjet.C));

                    aThis.setPosition(currentCase);

                    if(aThis.getPosition().getSignal() > 0 && aThis.getObjet() == TypeObjet.EMPTY){
                        //System.out.println("Direction signal");
                        setDirectionSignal(aThis);
                    }else{
                        //System.out.println("Direction random");
                        setRandomDirection(aThis);
                    }

                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestion des directions">
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
        for (int x = currentPosition.getX() - 1; x <= currentPosition.getX() + 1; x++) {
            for (int y = currentPosition.getY() - 1; y <= currentPosition.getY() + 1; y++) {
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
            Direction dir = getDirectionOfCase(aThis.getPosition(), maxSignal);
            if(dir==null){
                //c'est la case en cours on force donc l'agent à prendre l'objet
                aThis.setForcedToTake(true);
            }
            aThis.setDirection(dir);
        }else{
            //On tire une direction au hasard
            setRandomDirection(aThis);
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
                    if(isDirectionInBoundAndFree(currentX, currentY - I)){
                        freeDirections.add(direction);
                    }
                    break;
                case NORD_EST:
                    if(isDirectionInBoundAndFree(currentX + I, currentY - I)){
                        freeDirections.add(direction);
                    }
                    break;
                case EST:
                    if(isDirectionInBoundAndFree(currentX + I, currentY)){
                        freeDirections.add(direction);
                    }
                    break;
                case SUD_EST:
                    if(isDirectionInBoundAndFree(currentX + I, currentY + I)){
                        freeDirections.add(direction);
                    }
                    break;
                case SUD:
                    nextY += I;
                    if(isDirectionInBoundAndFree(currentX, currentY + I)){
                        freeDirections.add(direction);
                    }
                    break;
                case SUD_OUEST:
                    if(isDirectionInBoundAndFree(currentX - I, currentY + I)){
                        freeDirections.add(direction);
                    }
                    break;
                case OUEST:
                    if(isDirectionInBoundAndFree(currentX - I, currentY)){
                        freeDirections.add(direction);
                    }
                    break;
                case NORD_OUEST:
                    if(isDirectionInBoundAndFree(currentX - I, currentY - I)){
                        freeDirections.add(direction);
                    }
                    break;
            }
        }
        return freeDirections;
    }

    private boolean isDirectionInBoundAndFree(int x, int y){
        if(0 <= x && x < N
                && 0 <= y && y < M){
            //Position existante dans la grid, on vérifie sa dispo
            if(grid[x][y].getRobot()==null){
                //On ajoute la direction dans la liste
                return true;
            }
        }
        return false;
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
                //C'est la case courante
                return null;
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestion des actions (prendre/déposer)">
    private double probaTake(double f){
        return Math.pow((kPlus / (kPlus + f)), 2);
    }
    void tryTakeObject(Agent aThis, boolean forcedToTake) {
        Random rand = new Random();
        double proba = 0;
        if(aThis.getPosition().getType() == TypeObjet.A){
            proba = probaTake(aThis.getFa());
        }else if(aThis.getPosition().getType() == TypeObjet.B){
            proba = probaTake(aThis.getFb());
        }else if(aThis.getPosition().getType() == TypeObjet.C){
            proba = probaTake(aThis.getFc());
            if(rand.nextDouble()<= proba || forcedToTake){
                //on regarde si il y'a deja un autre robot
                Agent otherRobot = aThis.getPosition().getOtherRobot(aThis);
                if(otherRobot != null){
                    aThis.setObjet(aThis.getPosition().getType());
                    aThis.getPosition().setType(TypeObjet.EMPTY);
                    //On coupe le signal du robot en attente
                    clearSignal(otherRobot);
                }else{
                    aThis.setObjet(aThis.getPosition().getType());
                    //On propage un signal de demande d'aide
                    spreadSignal(aThis);
                }
            }
            return;
        }
        if(rand.nextDouble()<= proba || forcedToTake){
            //On prend l'objet
            aThis.setObjet(aThis.getPosition().getType());
            aThis.getPosition().setType(TypeObjet.EMPTY);
        }
    }

    private double probaDrop(double f){
        return Math.pow((f / (kMoins + f)), 2);
    }
    void tryDropObject(Agent aThis) {
        Random rand = new Random();
        double proba = 0;
        if(aThis.getObjet() == TypeObjet.A){
            proba = probaDrop(aThis.getFa());
        }else if(aThis.getObjet() == TypeObjet.B){
            proba = probaDrop(aThis.getFb());
        }else if(aThis.getObjet() == TypeObjet.C){
            proba = probaDrop(aThis.getFc());
            if(rand.nextDouble()<= proba){
                Agent otherRobot = aThis.getPosition().getOtherRobot(aThis);
                if(otherRobot != null){
                    //On pose l'objet pour les deux robots
                    aThis.getPosition().setType(aThis.getObjet());
                    aThis.setObjet(TypeObjet.EMPTY);
                    otherRobot.setObjet(TypeObjet.EMPTY);
                }
            }
            return;
        }
        if(rand.nextDouble()<= proba){
            //On pose l'objet
            aThis.getPosition().setType(aThis.getObjet());
            aThis.setObjet(TypeObjet.EMPTY);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestion du signal">
    private void updateCaseAgentFromGrid(){
        for (Agent agent:agents) {
            if(!(agent.getPosition() == null)){
                int x = agent.getPosition().getX();
                int y = agent.getPosition().getY();
                if(!(grid[x][y].getRobot() == agent || grid[x][y].getRobot2() == agent)){
                    grid[x][y].setRobot(agent);
                }
            }
        }
    }

    private void spreadSignal(Agent aThis){
        Case currentPosition = aThis.getPosition();
        for (int x = currentPosition.getX() - dS; x <= currentPosition.getX() + dS; x++) {
            for (int y = currentPosition.getY() - dS; y <= currentPosition.getY() + dS; y++) {
                if(0 <= x && x < N
                        && 0 <= y && y < M){
                    int dist = Math.max(Math.abs(x-currentPosition.getX()), Math.abs(y-currentPosition.getY()));
                    double rS = 1; //reduction du signal
                    for (int i = 1; i <= dist; i++) {
                        rS = rS - iS/i;
                    }
                    grid[x][y].setSignal(iS+rS);
                }
            }
        }
    }

    private void decreaseSignal(Agent aThis){
        Case currentPosition = aThis.getPosition();
        for (int x = currentPosition.getX() - dS; x <= currentPosition.getX() + dS; x++) {
            for (int y = currentPosition.getY() - dS; y <= currentPosition.getY() + dS; y++) {
                if(0 <= x && x < N
                        && 0 <= y && y < M){
                    grid[x][y].deprecateSignal(0.1);
                }
            }
        }
    }

    private void clearSignal(Agent aThis){
        Case currentPosition = aThis.getPosition();
        for (int x = currentPosition.getX() - dS; x <= currentPosition.getX() + dS; x++) {
            for (int y = currentPosition.getY() - dS; y <= currentPosition.getY() + dS; y++) {
                if(0 <= x && x < N
                        && 0 <= y && y < M){
                    grid[x][y].setSignal(0);
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestion de l'affichage"
    public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_BLUE = "\u001B[34m";
    private void printGrid(){

        int cptA = 0;
        int cptB = 0;
        int cptC = 0;
        System.out.println("Grille :");
        for (int y = 0; y < M; y++) {
            System.out.print("|");
                for (int x = 0; x < N; x++) {
                if(grid[x][y].getRobot() != null || grid[x][y].getRobot2() != null){
                    System.out.print(TEXT_BLACK);
                    System.out.print("R");
                    System.out.print(TEXT_RESET);
                    System.out.print("|"); 
                }else{
                    switch(grid[x][y].getType()){
                        case A:
                            cptA++;
                            System.out.print(TEXT_GREEN);
                            break;
                        case B:
                            cptB++;
                            System.out.print(TEXT_BLUE);
                            break;
                        case C:
                            cptC++;
                            System.out.print(TEXT_RED);
                            break;
                        case EMPTY:
                            if(grid[x][y].getSignal() > 0)
                                System.out.print(TEXT_YELLOW);
                    }
                    System.out.print(grid[x][y].getType());
                    System.out.print(TEXT_RESET);
                    System.out.print("|");  
                }
            }
            System.out.print("\n");
        }
        System.out.println("");

        System.out.println("A : " + cptA);
        System.out.println("B : " + cptB);
        System.out.println("C : " + cptC);
    }
    // </editor-fold>
    
}
