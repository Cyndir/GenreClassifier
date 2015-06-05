package GenreClassifier;
import java.lang.Math.*;
import java.io.*;
import java.util.*;

/*This class is the neural network class. It contains the nodes, weights and feed
 *forward action
 *Adapted from 
 *http://takinginitiative.wordpress.com/2008/04/23/basic-neural-network-tutorial-c-implementation-and-source-code/
 *
 *@author Connor Henderson
 */
public class NeuralNet {
	double[] inputNeurons, hiddenNeurons, outputNeurons;
	double[][] hiddenInputWeights, hiddenOutputWeights;
	double numIn,numHid,numOut;
	
	//The constructor initializes all instance variables and
	//randomly initializes weights
	public NeuralNet(int nIn, int nHid, int nOut){
		numIn = nIn;
		numHid = nHid;
		numOut = nOut;
		inputNeurons = new double[nIn+1];
		hiddenNeurons = new double[nHid+1];
		outputNeurons = new double[nOut];
		
		hiddenInputWeights = new double[nIn+1][nHid];
		hiddenOutputWeights = new double[nHid+1][nOut];
		
		initializeWeights();
	}
	//This method randomizes the initial weights
	void initializeWeights(){

		for (int i = 0 ; i < hiddenInputWeights.length;i++){
			for (int j = 0 ; j<hiddenInputWeights[i].length;j++){
				hiddenInputWeights[i][j] =(double)Math.random()*2-1;
			}
		}
		for (int i = 0 ; i < hiddenOutputWeights.length;i++){
			for (int j = 0 ; j<hiddenOutputWeights[i].length;j++){
				hiddenOutputWeights[i][j] =(double)Math.random()*2-1;
			}
		}
	}
	//This method runs the logistic activation function
	double activate(double x){
		return 1/(1+Math.exp(-x));
	}
	//This method executes the feed forward operation
	void feedForward(double[] data){
		//input layer
		for (int i = 0 ; i < data.length-1 ; i++){
			inputNeurons[i] = data[i];
		}
		inputNeurons[data.length] = 1;
		
		//hidden layer
		for (int i = 0 ; i < hiddenNeurons.length-1 ; i++){
			hiddenNeurons[i]=0;
			
			for (int j = 0 ; j < inputNeurons.length;j++){
				hiddenNeurons[i] +=inputNeurons[j]*hiddenInputWeights[j][i];
			}
			hiddenNeurons[i] = activate(hiddenNeurons[i]);
		}
		
		//output layer
		for (int i = 0 ; i < outputNeurons.length;i++){
			outputNeurons[i]=0;
			
			for (int j = 0; j < hiddenNeurons.length;j++){
				outputNeurons[i] +=hiddenNeurons[j]*hiddenOutputWeights[j][i];
			}
			outputNeurons[i] = activate(outputNeurons[i]);
		}
		
	}

	//save a weights file
	void saveWeights(File f)throws Exception{
		PrintWriter pw = new PrintWriter(f);
		pw.println(hiddenInputWeights.length);
		pw.println(hiddenInputWeights[0].length);
		pw.println(hiddenOutputWeights.length);
		pw.println(hiddenOutputWeights[0].length);
		for (int i = 0 ; i < hiddenInputWeights.length;i++) {
			for (int j = 0; j < hiddenInputWeights[i].length; j++) {
				pw.print(hiddenInputWeights[i][j]);
				pw.print('\t');
			}
			pw.println();
		}
		for (int i = 0 ; i < hiddenOutputWeights.length;i++){
			for (int j = 0 ; j<hiddenOutputWeights[i].length;j++){
				pw.print(hiddenOutputWeights[i][j]);
				pw.print('\t');
			}
			pw.println();

		}
		pw.close();
	}

	//load a weights file
	void loadWeights(File f) throws Exception{
		Scanner s = new Scanner(f);
		hiddenInputWeights = new double[s.nextInt()][s.nextInt()];
		numHid = hiddenInputWeights[0].length;
		hiddenNeurons = new double[(int)numHid+1];
		hiddenOutputWeights = new double[s.nextInt()][s.nextInt()];
		for (int i = 0 ; i < hiddenInputWeights.length;i++) {
			for (int j = 0; j < hiddenInputWeights[i].length; j++) {
				hiddenInputWeights[i][j]=s.nextDouble();
			}
		}
		for (int i = 0 ; i < hiddenOutputWeights.length;i++){
			for (int j = 0 ; j<hiddenOutputWeights[i].length;j++){
				hiddenOutputWeights[i][j] = s.nextDouble();
			}
		}
		s.close();
	}
	double[] getOutput(){
		return outputNeurons;
	}

}

