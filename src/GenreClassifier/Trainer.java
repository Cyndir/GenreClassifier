package GenreClassifier;

import java.io.*;

/* This class runs the training algorithms on the given neural network
 *Adapted from 
 *http://takinginitiative.wordpress.com/2008/04/23/basic-neural-network-tutorial-c-implementation-and-source-code/
 */
public class Trainer {
	
	double[] hiddenErrorGradients,outputErrorGradients;

	double[][] deltaHiddenInput,hidDelta;
	double[][] deltaHiddenOutput,outDelta;
	double[][] hidPrevGrad, outPrevGrad;
	double l,m,acc;
	int mEpochs,epoch;
	NeuralNet net;
	//PrintWriter p;

	double cAcc;

	//The constructor initializes most of the instance variables
	public Trainer(NeuralNet n){
	//	try{
	//		p = new PrintWriter("output.txt");
	//	}
	//	catch(Exception e){};
		net = n;

		mEpochs = 100;
		hiddenErrorGradients = new double[(int)n.numHid];
		outputErrorGradients = new double[(int)n.numOut];
		deltaHiddenInput = new double[(int)n.numIn+1][(int)n.numHid];
		deltaHiddenOutput=new double[(int)n.numHid+1][(int)n.numOut];
		outDelta = new double[(int)net.numHid+1][(int)net.numOut];
		hidDelta = new double[(int)net.numIn+1][(int)net.numHid];		
	}
	
	//This method allows the training parameters to be changed
	void setTrainingParams(double learn, double momentum){
		l = learn;
		m = momentum;
	}
	//This method allows the parameters determining the end of the
	//training to be changed
	void setEndTraining(int maxEpochs,double accuracy){
		mEpochs=maxEpochs;
		acc= accuracy;
	}
	//This method gives the output error gradient value
	double outputErrorGrad(double desired,double output){
		return output*(1.0-output)*(desired-output);
	}
	//This method gives the hidden error gradient value
	double hiddenErrorGrad(int i){
		double sum = 0;
		for (int j = 0 ; j <net.numOut;j++){
			sum += net.hiddenOutputWeights[i][j]*outputErrorGradients[j];
		}
		return net.hiddenNeurons[i]*(1-net.hiddenNeurons[i])*sum;	
	}
	//This method runs the training algorithm on a given set
	void trainNetwork(TrainingSet t){
		//printHeader();
		while (epoch < mEpochs && acc >cAcc) {
			runEpoch(t.sets,t.answers);
			//print(epoch);
			if(epoch%10==0){
				t.shuffle();
				System.out.println(epoch + "\t" + cAcc);
			}
			epoch++;		
		}		
	}
	//This method closes the printwriter
	/*public void end(){
		p.close();
	}
	//This method prints the result for the current epoch
	 private void print(int i){
    	p.print(i);
    	p.print("\t\t");
    	p.print(cAcc);
    	p.print("\t\t\t\t");
    	p.println(cErr);
    }
    //This method prints the header for the training set
    private void printHeader(){
    	p.println("Epoch\tTraining Accuracy\tTraining Error");
    }*/
    //This method executes a single epoch on the given sets and answers
	void runEpoch(double[][] sets, double[][] answers){
		double fails = 0;

		for(int i = 0 ; i < sets.length;i++){
			net.feedForward(sets[i]);
			backProp(answers[i]);

			int idx = 0;
			double max =0;
			for (int j = 0 ; j < net.outputNeurons.length;j++){

				if (net.outputNeurons[j]>max){
					max = net.outputNeurons[j];
					idx = j;
				}
			}
			if(answers[i][idx] !=1) fails++;
		}
		updateWeights();
		cAcc =100- (fails/sets.length*100);

		}
	//This method runs the back propagation learning algorithm	
	void backProp(double[] answers){		
		//hidden to output
		for (int i = 0 ; i < net.numOut; i++){
			outputErrorGradients[i] = outputErrorGrad(answers[i],net.outputNeurons[i]);
			for (int j = 0 ; j<=net.numHid ; j++){
				deltaHiddenOutput[j][i] = l*net.hiddenNeurons[j]*outputErrorGradients[i]+m*deltaHiddenOutput[j][i];
			}
		}
		//input to hidden
		for (int j = 0 ; j<net.numHid ; j++){
			hiddenErrorGradients[j] = hiddenErrorGrad(j);
			for (int i = 0 ; i <= net.numIn;i++){
				deltaHiddenInput[i][j]= l*net.inputNeurons[i]*hiddenErrorGradients[j];
			}		
		}		
	}


	//This method returns the sign of a value
	double sign(double d){
		if(d >0)return 1;
		else if(d < 0) return -1;
		else return 0;
	}
	//This method updates the weights in the neural network
	void updateWeights(){
		for (int i = 0 ; i <= net.numIn;i++){
			for (int j = 0 ; j<net.numHid ; j++){
				net.hiddenInputWeights[i][j] += deltaHiddenInput[i][j];
			}
		}		
		for (int i = 0 ; i <= net.numHid;i++){
			for (int j = 0 ; j<net.numOut ; j++){
				net.hiddenOutputWeights[i][j] +=deltaHiddenOutput[i][j];
			}
		}		
	}
	//This method prints the test results
	/*void generalPrint(){
		
		p.flush();
		for (int i = 0 ; i < net.outputNeurons.length;i++){
		
		if (net.outputNeurons[i] >0.9)	p.print(1);
		else print(0);
			p.print('\t');
		}
		p.print('\n');
	}*/
		
}

