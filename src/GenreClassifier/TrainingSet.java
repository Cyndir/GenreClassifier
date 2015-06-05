package GenreClassifier;

import java.util.*;

//This class contains the training data and answers
class TrainingSet {
	double[][] sets;
	double[][] answers;
	int current;	
	//Initialize on a number of sets
	public TrainingSet(int nSets,int numAnswers){
		sets = new double[nSets][];
		answers = new double[nSets][numAnswers];
		current =0;
	}
	//Add a training set and its answer to the data
	public void addSet(double[] data, double[] answer){
		if (current <answers.length){
			sets[current] = data;
			answers[current] = answer;
			current++;
		}
	}
	//shuffle the order of the training sets
	public void shuffle(){
		int index;
		double[] temp;
		double[] aTemp;
		Random random = new Random();	
		for (int i = 0 ; i < answers.length ;i++){
			index = random.nextInt(i+1);
			temp = new double[sets[i].length];
			for (int j = 0 ; j < sets[i].length ; j ++){
				temp[j] = sets[i][j];
				sets[i][j] = sets[index][j];
				sets[index][j] = temp[j];
			}
			aTemp = answers[i];
			answers[i] = answers[index];
			answers[index]=aTemp;						
		}
	}
}
