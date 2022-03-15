package com.polytech.sma.TP2;


import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) {
	    //Grid g = new Grid(10, 10, 0, 0, 10, 5, 5, 1, 0.1, 0.3, 0, 2);

		int N = 50;
		int M = 50;
		int nbObjA = 100;
		int nbObjB = 100;
		int nbObjC = 200;
		int nbAgents = 50;
		int agentMemorySize = 10;
		int I = 1;
		double kPlus = 0.1;
		double kMoins = 0.3;
		double e = 0;
		int distanceSignal = 3;
		boolean printIterationGrid = false;

		Grid g = new Grid(N, M, nbObjA, nbObjB, nbObjC, nbAgents,
				agentMemorySize, I, kPlus, kMoins, e, distanceSignal, printIterationGrid);
	}
        
}