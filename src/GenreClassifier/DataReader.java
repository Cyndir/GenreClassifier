
package GenreClassifier;

import javax.swing.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/*This class reads the data from the txt file
 */
public class DataReader extends JPanel{

	JFileChooser fc;
	File data;
	Scanner s;
	LineNumberReader lnr;
	int lines;
	String description;
	DecimalFormat df = new DecimalFormat("#.######");



	//This constructor will read a data file from the given name and normalize the data
	public DataReader() {

	}
	double [][] readAnswersFromTxt(File in,int numAnswers) throws Exception{
		lnr = new LineNumberReader(new FileReader(in));
		s = new Scanner(in);
		while (lnr.readLine() != null);
		double [][] answers = new double[lnr.getLineNumber()][numAnswers];
		for (int i = 0 ; i < answers.length ; i++){
			for(int j = 0 ; j < answers[i].length ; j++){
				answers[i][j] = s.nextInt();
			}
		}
		return answers;
	}
	double[][] readInputFromTxt(File in)throws Exception{
		lnr = new LineNumberReader(new FileReader(in));
		s = new Scanner(in);
		while (lnr.readLine() != null);
		double[][] inputData = new double[lnr.getLineNumber()][400];
		for (int i = 0 ; i < inputData.length ; i++){
			for(int j = 0 ; j < inputData[i].length ; j++){
				inputData[i][j] = s.nextDouble();
			}
		}
		return inputData;
	}
	/* This method takes a random 1 minute sample from a given WavFile and runs a Discrete Fourier Transform
	 using 100 harmonics on it. It then returns sin and cos amplitudes as well as the phase shift and amplitude of the
	  harmonic */
	double[][] dft(WavFile wave, int numHarmonics)throws Exception{
		double a, b;
		int[] samps = new int[(int)wave.getNumFrames()*wave.getNumChannels()];
		double[][] output =new double[numHarmonics][4];
		int duration = (int)wave.getSampleRate()*60;
		wave.readFrames(samps,(int)wave.getNumFrames());
		int start = (int) (Math.random()*(samps.length -duration));

		for (int j = 0 ; j < numHarmonics ; j++){
			a = 0;
			b = 0;
			for (int i = start; i < start + duration; i+=2) {
				samps[i] += samps[i+1];
				samps[i] = samps[i]/2;
				a = a + samps[i]*Math.cos((2*Math.PI*j*i)/duration);
				b = b + samps[i]*Math.sin((2*Math.PI*j*i)/duration);
			}
			output[j][0] = a*2/duration;
			output[j][1] = b*2/duration;
			output[j][2] = Math.pow(Math.pow(output[j][0],2) + Math.pow(output[j][1],2),0.5);
			output[j][3] = Math.atan(output[j][0]/output[j][1]);
		}
		return output;
	}
	/*This method was used to create the training data using all WAV files categorized into folders. This was to
	 prevent having to upload 5 GB of WAV files. This was also done to improve efficiency as running DFTs on so
	 many WAV files took significant time*/
	public void createTrainingText(String folder) throws Exception{
		File answers = new File("trainingAnswers.txt");
		data=new File("training.txt");
		double[][] output;
		PrintWriter pw2 = new PrintWriter(answers);
		WavFile wave;
		PrintWriter pw = new PrintWriter(data);
		for (File fileEntry : new File(folder+ "/Rap").listFiles()) {

			wave = WavFile.openWavFile(fileEntry);

			output = dft(wave,100);//sample array and number of harmonics

			for (int i = 0 ; i < output.length ; i++)
				pw.print(df.format(output[i][0]) + "\t" + df.format(output[i][1]) + "\t" + df.format(output[i][2]) + "\t" + df.format(output[i][3]) + "\t");
			pw.println();
			pw2.println(1 +"\t" + 0 + "\t"+ 0 + "\t"+ 0 + "\t"+ 0 + "\t"+ 0);

		}
		for (File fileEntry : new File(folder+ "/Alternative Rock").listFiles()) {
			wave = WavFile.openWavFile(fileEntry);

			output = dft(wave,100);//sample array and number of harmonics

			for (int i = 0 ; i < output.length ; i++)
				pw.print(df.format(output[i][0]) + "\t" + df.format(output[i][1]) + "\t" + df.format(output[i][2]) + "\t" + df.format(output[i][3]) + "\t");
			pw.println();
			pw2.println(0 +"\t" + 1 + "\t"+ 0 + "\t"+ 0 + "\t"+ 0 + "\t"+ 0);
		}
		for (File fileEntry : new File(folder+ "/Classic Rock").listFiles()) {
			wave = WavFile.openWavFile(fileEntry);

			output = dft(wave,100);//sample array and number of harmonics

			for (int i = 0 ; i < output.length ; i++)
				pw.print(df.format(output[i][0]) + "\t" + df.format(output[i][1]) + "\t" + df.format(output[i][2]) + "\t" + df.format(output[i][3]) + "\t");
			pw.println();
			pw2.println(0 +"\t" + 0 + "\t"+ 1 + "\t"+ 0 + "\t"+ 0 + "\t"+ 0);
		}
		for (File fileEntry : new File(folder+ "/Comedy").listFiles()) {
			wave = WavFile.openWavFile(fileEntry);

			output = dft(wave,100);//sample array and number of harmonics

			for (int i = 0 ; i < output.length ; i++)
				pw.print(df.format(output[i][0]) + "\t" + df.format(output[i][1]) + "\t" + df.format(output[i][2]) + "\t" + df.format(output[i][3]) + "\t");
			pw.println();
			pw2.println(0 +"\t" + 0 + "\t"+ 0 + "\t"+ 1 + "\t"+ 0 + "\t"+ 0);
		}
		for (File fileEntry : new File(folder+ "/Metal").listFiles()) {
			wave = WavFile.openWavFile(fileEntry);

			output = dft(wave,100);//sample array and number of harmonics

			for (int i = 0 ; i < output.length ; i++)
				pw.print(df.format(output[i][0]) + "\t" + df.format(output[i][1]) + "\t" + df.format(output[i][2]) + "\t" + df.format(output[i][3]) + "\t");
			pw.println();
			pw2.println(0 +"\t" + 0 + "\t"+ 0 + "\t"+ 0 + "\t"+ 1 + "\t"+ 0);
		}
		for (File fileEntry : new File(folder+ "/Video Game").listFiles()) {
			wave = WavFile.openWavFile(fileEntry);

			output = dft(wave,100);//sample array and number of harmonics

			for (int i = 0 ; i < output.length ; i++)
				pw.print(df.format(output[i][0]) + "\t" + df.format(output[i][1]) + "\t" + df.format(output[i][2]) + "\t" + df.format(output[i][3]) + "\t");
			pw.println();
			pw2.println(0 +"\t" + 0 + "\t"+ 0 + "\t"+ 0 + "\t"+ 0 + "\t"+ 1);
		}
		pw.close();
		pw2.close();
		System.out.println("Done");
	}
}