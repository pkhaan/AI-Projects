//import sun.awt.geom.AreaOp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import java.io.*;



public class LogisticRegression {

    private int ITERATIONS=200;
    private static int Nwords=1000; //first N words to be used in training
    private double rate=0.0001;
    private double[] weights;

    public static void main (String args[]){

        // list all files
        List<File> fileList =new ArrayList<File>();
        ListFiles("lingspam_public",fileList);

        // split train/test files
        List<File> trainFiles =new ArrayList<File>();
        List<File> testFiles =new ArrayList<File>();
        for (int i=0;i<fileList.size();i++) {
            if (i % 7 != 0) {
                trainFiles.add(fileList.get(i));
            } else {
                testFiles.add(fileList.get(i));
            }
        }

        //TODO      Create hashmap with frequency of words. First store every word of each email in a list, then update frequency map (Dictionary) with words and count number.
        HashMap<String,Integer> Dictionary = new HashMap<>();
        for(int i=0;i<trainFiles.size();i++){
            try{
                if(trainFiles.get(i).getName().contains("spmsg")) {
                    Scanner scanner = new Scanner(new File(String.valueOf(trainFiles.get(i))));
                    List<String> emailcontent = new ArrayList<String>();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String split[];
                        split = line.split(" ");
                        for (int j = 0; j < split.length; j++) {
                            if (!split[j].equals("Subject:")) emailcontent.add(split[j].toLowerCase());
                        }
                    }
                    // System.out.println(emailcontent);
                    for (int k = 0; k < emailcontent.size(); k++) {
                        if (Dictionary.containsKey(emailcontent.get(k))) {
                            Dictionary.put(emailcontent.get(k), Dictionary.get(emailcontent.get(k)) + 1);
                        } else {
                            Dictionary.put(emailcontent.get(k), 1);
                        }
                    }

                }
            }catch(FileNotFoundException e){
                System.err.println("error");
            }
        }

        //TODO store the N words with the highest frequency in an array named words. Will use later to compare to the newcoming emails and update weights.
        Map<String,Integer> DictionaryShorted=SortHashMapByValue(Dictionary);

        ArrayList<String> tempwords = new ArrayList<String>(DictionaryShorted.keySet());
        ArrayList<String> words = new ArrayList<String>();
        for (int i = 0; i < Nwords ; i++){
            words.add(tempwords.get(i));
        }

        List<Instance> TrainInstances = new ArrayList<Instance>();
        List<Instance> TestInstances = new ArrayList<Instance>();
        createListOfInstances(words,trainFiles,TrainInstances);
        createListOfInstances(words,testFiles,TestInstances);

        int d = TrainInstances.get(0).x.length;
        System.out.println(d);
        //TODO     Train
        LogisticRegression logistic = new LogisticRegression(d);
        logistic.train(TrainInstances);

        //TODO     Print results
        System.out.println("\n\nPrinting Results for Train Dataset");
        logistic.showResults(TrainInstances);
        System.out.println("\n\nPrinting Results for Test Dataset");
        logistic.showResults(TestInstances);
    }

    //TODO      Train Files
    public void train(List<Instance> instances){
        int countPositive=0;
        int countNegative=0;
        for(int k=0 ; k<ITERATIONS ; k++){
            double likelihood=0.0;     //likelihood of the training data for this iteration
            for (int i=0 ; i < instances.size() ; i++){
                //keep count of positives and negatives
                if(instances.get(i).label==1){
                    countPositive++;
                }else{
                    countNegative++;
                }
                double predictionProb=helpPredict(instances.get(i).x);
                double error=instances.get(i).label - predictionProb;
                double SigmaWeightsTimeXi=0.0;
                for (int j=0 ; j<weights.length ; j++){
                    weights[j]=weights[j] + (rate*instances.get(i).x[j]*error);
                    SigmaWeightsTimeXi += (weights[j]*instances.get(i).x[j]);
                }
                likelihood+=(instances.get(i).label*SigmaWeightsTimeXi)-Math.log(1 + Math.exp(SigmaWeightsTimeXi));
            }
            System.out.println("Iteration number:  "+(k+1)+". The likelihood is:  "+likelihood);
        }
    }

    public void showResults(List<Instance> instances){
        //TODO:     calculate accuracy for every dataset that i give as input as well as P , R and F1 results.
        double accuracy=0;
        double p_pos=0,p_neg=0,r_pos=0,r_neg=0,f_pos=0,f_neg=0;
        int truepositives = 0,truenegatives=0 ,falsepositives=0 ,falsenegatives=0;
        for(int i=0 ; i<instances.size() ; i++){
            int prediction=predict(instances.get(i).x);
            int actuallabel=instances.get(i).label;
            if(prediction==actuallabel){
                if (prediction == 1) {
                    truepositives++;
                }else{
                    truenegatives++;
                }
            }else{
                if (prediction == 0) {
                    falsepositives++;
                }else{
                    falsenegatives++;
                }
            }
        }

        //TODO:  calculate the results
        accuracy = (double)(truepositives+truenegatives)/instances.size();

        p_pos=(double)(truepositives)/(truepositives+falsepositives);
        p_neg=(double)(truenegatives)/(truenegatives+falsenegatives);

        r_pos=(double)(truepositives)/(truepositives+falsenegatives);
        r_neg=(double)(truenegatives)/(truenegatives+falsepositives);

        f_pos=(2*p_pos*r_pos)/(p_pos+r_pos);
        f_neg=(2*p_neg*r_neg)/(p_neg+r_neg);

        //TODO: print the results
        System.out.println("\n\nAccuracy: "+accuracy+".");
        System.out.println("\nPositive class: ");
        System.out.println("Presicion: "+p_pos+"\nRecall: "+r_pos+" \nF1 Score: "+f_pos);
        System.out.println("\nNegative class: ");
        System.out.println("Presicion: "+p_neg+"\nRecall: "+r_neg+" \nF1 Score: "+f_neg);
    }

    public double helpPredict(double[] x){
        double SigmaWeightsTimeXi = 0.0;
        for (int i=0 ; i<weights.length ; i++){
            SigmaWeightsTimeXi += weights[i]*x[i];
        }
        return sigmoid(SigmaWeightsTimeXi);
    }
    public int predict(double x[]){
        if(helpPredict(x)>=0.5){
            return 1;
        }else{
            return 0;
        }
    }

    public LogisticRegression(int n){
        double tempweights[];
        tempweights=new double[n];
        for (int i=0 ; i<n ; i++){
            tempweights[i]=0.0;
        }
        weights=tempweights;
    }
    public static double sigmoid(double z){
        double sigmoid = 0.0;
        return (1.0/(1.0 + Math.exp((z*(-1)))));
    }





    //TODO  scan every email and compare it to the "words" array
    //TODO  if an email contains the word "x" N times, and the word "x" is on the index 150 on the words array, update this particular email "x" array (features) : x[150]=N
    //TODO  after that, normalize that to [1,0]
    public static void createListOfInstances(ArrayList<String>words,List<File> Files, List <Instance> instances){
        for (int i = 0 ; i<Files.size() ; i++){
            try{
                Scanner scanner = new Scanner(new File(String.valueOf(Files.get(i))));
                String filename=String.valueOf(Files.get(i));
                int filelabel=2; //fake value
                if (filename.contains("spmsg")){
                    filelabel=1;
                }else{
                    filelabel=0;
                }
                List<String> emailwords=new ArrayList<>();
                while (scanner.hasNextLine()){
                    String line=scanner.nextLine();
                    String split[];
                    split = line.split (" ");
                    for (int j = 0; j<split.length;j++){
                        int h;
                        if (!split[j].equals("Subject:")) emailwords.add(split[j].toLowerCase());
                    }
                }
                double[] email= new double[Nwords];
                for (int j = 0 ; j <words.size() ; j++){
                    if (emailwords.contains(words.get(j))){
                        email[j]=1;
                    }else{
                        email[j]=0;
                    }
                }
                //ArrayNormalizer0to1(email);
                Instance instance = new Instance(filename,filelabel,email);
                instances.add(instance);
            }catch(FileNotFoundException e){
                System.err.println("error");
            }
        }
    }

    public static void ListFiles(String directoryName, List<File> files){

        File directory=new File(directoryName);

        File[] fileList=directory.listFiles();
        if (fileList!=null){
            for (File file : fileList){
                if (file.isFile()){
                    files.add(file);
                }else{
                    ListFiles(file.getAbsolutePath(),files);
                }
            }
        }
    }

    public static HashMap<String,Integer> SortHashMapByValue(HashMap<String,Integer> GivenHashMap){

        List<Map.Entry<String,Integer>> list=new LinkedList<Map.Entry<String, Integer>>(GivenHashMap.entrySet());
        Collections.sort(list,new Comparator <Map.Entry<String,Integer>>(){public int compare(Map.Entry<String,Integer> o1 ,Map.Entry<String,Integer> o2 ){return o2.getValue().compareTo(o1.getValue());}});
        HashMap<String,Integer> temp=new LinkedHashMap<String,Integer>();
        for (Map.Entry<String,Integer> aa : list){
            temp.put(aa.getKey(),aa.getValue());
        }
        return temp;
    }

    public static class Instance{
        public String name;//name of file
        public int label;  //ham=0 // spam=1
        public double[] x; //feature vector
        public Instance(String name, int label, double[] x){
            this.name=name;
            this.label=label;
            this.x=x;
        }
    }

    public static double getArrayMaxValue(double[]array){
        double maxvalue=array[0];
        for(int i=0 ; i<array.length ; i++){
            if(array[i]>maxvalue)maxvalue=array[i];
        }
        return maxvalue;
    }

    public static double getArrayMinValue(double[]array){
        double minvalue=array[0];
        for(int i=0 ; i<array.length ; i++){
            if(array[i]<minvalue)minvalue=array[i];
        }
        return minvalue;
    }

    public static void ArrayNormalizer0to1(double[]array){
        double max=getArrayMaxValue(array);
        double min=getArrayMinValue(array);
        for (int i=0 ; i<array.length ; i++){
            if(max-min!=0 && max!=0 && min!=0)array[i]=(array[i]-min)/max-min;
        }
    }

}