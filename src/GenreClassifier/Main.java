package GenreClassifier;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
public class Main extends JFrame{
    DataReader dr;
    NeuralNet nn;
    Trainer nt;
    TrainingSet ts;
    int hidden,epochs;
    double learn,momentum,acc;
    double[][] input,answers;
    JFileChooser fc;
    int dur = 5;
    public Main()throws Exception{

        initGUI();
        fc = new JFileChooser();
        dr = new DataReader();
        //dr.createTrainingText("WAV Files");
       nn = new NeuralNet(400,hidden,6);
        nt = new Trainer(nn);
        nt.setEndTraining(epochs,acc);
        nt.setTrainingParams(learn,momentum);
        input = dr.readInputFromTxt(new File("training.txt"));
        answers = dr.readAnswersFromTxt(new File("trainingAnswers.txt"),6);
        ts = new TrainingSet(input.length,answers[0].length);
        for (int i = 0 ; i < input.length ; i++){
            ts.addSet(input[i],answers[i]);
        }


}
    //Creates the UI and sets up all the features of it
    void initGUI()throws Exception{
        setTitle("Genre Classifier");
        setSize(700,250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container pane = getContentPane();
        SpringLayout layout = new SpringLayout();
        pane.setLayout(layout);
        JLabel hLabel = new JLabel("Hidden Nodes");
        JLabel dLabel = new JLabel("Duration to use (seconds)");
        JLabel lLabel = new JLabel("Learning Rate");
        JLabel mLabel = new JLabel("Momentum");
        JLabel aLabel = new JLabel("Target Accuracy");
        JLabel eLabel = new JLabel("Number of epochs");
        final JTextField hTextField = new JTextField("200",15);
        final JTextField dTextField = new JTextField("5",5);
        final JTextField lTextField = new JTextField("0.2",15);
        final JTextField mTextField = new JTextField("0.2",15);
        final JTextField aTextField = new JTextField("80",15);
        final JTextField eTextField = new JTextField("600",15);
        JLabel nLabel = new JLabel("Network Accuracy");
        JLabel gLabel = new JLabel("Genre");
       final JTextArea nTextField = new JTextArea(1,15);
       final JTextArea gTextField = new JTextArea(1,15);

        JButton quitButton = new JButton("Quit");//exit program
        quitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        JButton loadButton = new JButton("Load Weights");//load a weight file

        loadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event){
               int val= fc.showOpenDialog(Main.this);
                File f = new File("weights.txt");
                if (val == JFileChooser.APPROVE_OPTION) {
                    f = fc.getSelectedFile();
                }
                try {

                    nn.loadWeights(f);
                    nt = new Trainer(nn);
                    nt.setEndTraining(epochs,acc);
                    nt.setTrainingParams(learn,momentum);
                }
                catch(Exception e){};
            }
        });
        JButton saveButton = new JButton("Save Weights");//save a weight file

        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event){
                int val= fc.showSaveDialog(Main.this);
                File f = new File("weights.txt");
                if (val == JFileChooser.APPROVE_OPTION) {
                    f = fc.getSelectedFile();
                }
                try {
                    nn.saveWeights(f);
                }
                catch(Exception e){};
            }
        });
        JButton classifyButton = new JButton("Classify a Song");//pick a song to test with
        classifyButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event){
                int val= fc.showOpenDialog(Main.this);
                File f = new File("weights.txt");
                if (val == JFileChooser.APPROVE_OPTION) {
                    f = fc.getSelectedFile();
                }
                try {
                    WavFile wav = WavFile.openWavFile(f);
                    dur = Integer.parseInt(dTextField.getText());
                    double[][] input = dft(wav,100);
                    nn.feedForward(convertArray(input));
                    gTextField.setText(classify(nn.getOutput()));
                }
                catch(Exception e){}
            }
        });
        JButton retrainButton = new JButton("Retrain Network");//do a full training cycle
        retrainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event){
                hidden = Integer.parseInt(hTextField.getText());
                learn = Double.parseDouble(lTextField.getText());
                momentum = Double.parseDouble(mTextField.getText());
                acc = Double.parseDouble(aTextField.getText());
                epochs = Integer.parseInt(eTextField.getText());
                nn = new NeuralNet(400,hidden,6);
                nt = new Trainer(nn);
                nt.setEndTraining(epochs,acc);
                nt.setTrainingParams(learn,momentum);
                nn.initializeWeights();
                nt.trainNetwork(ts);
                System.out.println(nt.cAcc);
                nTextField.setText(Double.toString(nt.cAcc));
            }
        });
        JButton testButton = new JButton("Test Accuracy");//run a single epoch

        testButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event){
                nt.runEpoch(ts.sets,ts.answers);
                nTextField.setText(Double.toString(nt.cAcc));
            }
        });

        pane.add(hLabel);//add all field to UI
        pane.add(dLabel);
        pane.add(lLabel);
        pane.add(mLabel);
        pane.add(aLabel);
        pane.add(eLabel);
        pane.add(nLabel);
        pane.add(gLabel);
        pane.add(hTextField);
        pane.add(dTextField);
        pane.add(lTextField);
        pane.add(mTextField);
        pane.add(aTextField);
        pane.add(eTextField);
        pane.add(nTextField);
        pane.add(gTextField);
        pane.add(quitButton);
        pane.add(saveButton);
        pane.add(loadButton);
        pane.add(retrainButton);
        pane.add(classifyButton);
        pane.add(testButton);
        nTextField.setEditable(false);
        gTextField.setEditable(false);

        layout.putConstraint(SpringLayout.WEST,hLabel,5,SpringLayout.WEST,pane);//position UI fields
        layout.putConstraint(SpringLayout.WEST,dLabel,5,SpringLayout.EAST,hTextField);
        layout.putConstraint(SpringLayout.WEST,lLabel,5,SpringLayout.WEST,pane);
        layout.putConstraint(SpringLayout.WEST,mLabel,5,SpringLayout.WEST,pane);
        layout.putConstraint(SpringLayout.WEST,aLabel,5,SpringLayout.WEST,pane);
        layout.putConstraint(SpringLayout.WEST,eLabel,5,SpringLayout.WEST,pane);
        layout.putConstraint(SpringLayout.WEST,nLabel,5,SpringLayout.WEST,pane);
        layout.putConstraint(SpringLayout.WEST,gLabel,5,SpringLayout.WEST,pane);

        layout.putConstraint(SpringLayout.NORTH,hLabel,5,SpringLayout.NORTH,pane);
        layout.putConstraint(SpringLayout.NORTH,dLabel,5,SpringLayout.NORTH,pane);
        layout.putConstraint(SpringLayout.NORTH,lLabel,9,SpringLayout.SOUTH,hLabel);
        layout.putConstraint(SpringLayout.NORTH,mLabel,9,SpringLayout.SOUTH,lLabel);
        layout.putConstraint(SpringLayout.NORTH,aLabel,9,SpringLayout.SOUTH,mLabel);
        layout.putConstraint(SpringLayout.NORTH,eLabel,9,SpringLayout.SOUTH,aLabel);
        layout.putConstraint(SpringLayout.NORTH,nLabel,9,SpringLayout.SOUTH,eLabel);
        layout.putConstraint(SpringLayout.NORTH,gLabel,9,SpringLayout.SOUTH,nLabel);

        layout.putConstraint(SpringLayout.WEST,hTextField,5,SpringLayout.EAST,eLabel);
        layout.putConstraint(SpringLayout.WEST,dTextField,5,SpringLayout.EAST,dLabel);
        layout.putConstraint(SpringLayout.WEST,lTextField,5,SpringLayout.EAST,eLabel);
        layout.putConstraint(SpringLayout.WEST,mTextField,5,SpringLayout.EAST,eLabel);
        layout.putConstraint(SpringLayout.WEST,aTextField,5,SpringLayout.EAST,eLabel);
        layout.putConstraint(SpringLayout.WEST,eTextField,5,SpringLayout.EAST,eLabel);
        layout.putConstraint(SpringLayout.WEST,nTextField,5,SpringLayout.EAST,eLabel);
        layout.putConstraint(SpringLayout.WEST,gTextField,5,SpringLayout.EAST,eLabel);

        layout.putConstraint(SpringLayout.NORTH,hTextField,5,SpringLayout.NORTH,pane);
        layout.putConstraint(SpringLayout.NORTH,dTextField,5,SpringLayout.NORTH,pane);
        layout.putConstraint(SpringLayout.NORTH,lTextField,5,SpringLayout.SOUTH,hTextField);
        layout.putConstraint(SpringLayout.NORTH,mTextField,5,SpringLayout.SOUTH,lTextField);
        layout.putConstraint(SpringLayout.NORTH,aTextField,5,SpringLayout.SOUTH,mTextField);
        layout.putConstraint(SpringLayout.NORTH,eTextField,5,SpringLayout.SOUTH,aTextField);
        layout.putConstraint(SpringLayout.NORTH,nTextField,5,SpringLayout.SOUTH,eTextField);
        layout.putConstraint(SpringLayout.NORTH,gTextField,5,SpringLayout.SOUTH,nTextField);

        layout.putConstraint(SpringLayout.SOUTH,quitButton,-5,SpringLayout.SOUTH,pane);
        layout.putConstraint(SpringLayout.EAST,quitButton,-5,SpringLayout.EAST,pane);

        layout.putConstraint(SpringLayout.SOUTH,saveButton,-5,SpringLayout.SOUTH,pane);
        layout.putConstraint(SpringLayout.EAST,saveButton,-5,SpringLayout.WEST,quitButton);

        layout.putConstraint(SpringLayout.SOUTH,loadButton,-5,SpringLayout.SOUTH,pane);
        layout.putConstraint(SpringLayout.EAST,loadButton,-5,SpringLayout.WEST,saveButton);

        layout.putConstraint(SpringLayout.SOUTH,retrainButton,-5,SpringLayout.SOUTH,pane);
        layout.putConstraint(SpringLayout.EAST,retrainButton,-5,SpringLayout.WEST,loadButton);

        layout.putConstraint(SpringLayout.SOUTH,classifyButton,-5,SpringLayout.SOUTH,pane);
        layout.putConstraint(SpringLayout.EAST,classifyButton,-5,SpringLayout.WEST,retrainButton);

        layout.putConstraint(SpringLayout.SOUTH,testButton,-5,SpringLayout.SOUTH,pane);
        layout.putConstraint(SpringLayout.EAST,testButton,-5,SpringLayout.WEST,classifyButton);
        setVisible(true);
    }
    //run DFT on given wav file
    double[][] dft(WavFile wave, int numHarmonics)throws Exception{
        double a, b;
        int[] samps = new int[(int)wave.getNumFrames()*wave.getNumChannels()];
        double[][] output =new double[numHarmonics][4];
        int duration = (int)wave.getSampleRate()*dur;
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
    //convert 2d array to 1d
    double[] convertArray(double[][] array){
        double[] out = new double[array.length*array[0].length];
        int idx = 0;
        for(int i = 0 ; i < array.length ; i++){
            for (int j = 0 ; j < array[i].length ; j++){
                out[idx] = array[i][j];
                idx++;
            }
        }
        return out;
    }
    //convert result to genre
    String classify(double[] out){
        for (int i = 0 ; i < out.length ; i++){
            System.out.print(out[i]);
            System.out.print("\t");
        }
        System.out.println();
        double max = 0;

        int idx= 6;
        for (int i = 0 ; i < out.length ; i++){
            if(out[i] > max){
                max = out[i];
                idx = i;
            }
        }
        String guess = "";
        if (max < 0.7) guess = "(Uncertain) ";
        if (idx ==0){
            return guess +"Rap";
        }
        if (idx ==1){
            return guess +"Alternative Rock";
        }
        if (idx ==2){
            return guess +"Classic Rock";
        }
        if (idx ==3){
            return guess +"Comedy";
        }
        if (idx ==4){
            return guess +"Metal";
        }
        if (idx ==5){
            return guess +"Video Game";
        }
        return "unknown";
    }
    public static void main(String[] args)throws Exception {
	    Main m = new Main();
    }
}
