
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Adaboost {
	private ArrayList<Email> mails;
	private HashSet<String> listed_words; //Contains all the used words - helps us to find entropy faster
	private HashSet<String> important_words;//Storage of important Words(Temporary)
	public  int repetitions = 500;//the number of how many ID3's we'll create
	public  double percentage = 0.1;//the % of the test data
	public int value=200;//the number if important properties that we pick
	private ArrayList<Email> test_data;
	
	public Adaboost() {
		this.mails = new ArrayList<Email>();
		this.listed_words = new HashSet<String>();
	}
	
	/*This function updates the test data of this algorithm,updates the hash set of the listed words
	 and it initialises the weights of each mail to 1/N. Also it "trains" the algorithms by constructing the
	 important_words hashset,which contains the m most important properties of a mail(which words it contains or not)*/
	public void update() {
		updateTestData();
		updateListedWords();
		for(int i=0;i<mails.size();i++){
			mails.get(i).setWeight(((double)1/((double)mails.size())));
		}
		Data temp=new Data(mails,listed_words);
		important_words=temp.train2(value);
	}
	
	//This will be called only after we've added every mail to our data base
	public void updateListedWords() {
		for(int i=0;i<mails.size();i++) {
			listed_words.addAll(mails.get(i).getHashSet());
		}
	}
	
	public int size() {return mails.size();}
	
	//Picks randomly test data for the adaboost algorithm
	public void updateTestData() {
		test_data=new ArrayList<Email>();
		Random randomGenerator = new Random();
	    int randomInt;
	    int test_size = (int)(mails.size()*(percentage));//Calculates how many emails it will take from the base
		/*It randomly selects an email from the base
		The selected emails will be used as check data to test the efficiency of the algorithm
		The remaining emails in the base will be our traning data
		Based on the tranning data we calculate the chances of a test email to be spam or ham*/
	    for(int i=0;i<test_size;i++) {
	        randomInt = randomGenerator.nextInt(mails.size());
	        test_data.add(mails.get(randomInt));
	        mails.remove(randomInt);
	    }
	}
	
	public void add(Email m) {
		mails.add(m);
	}
	
	/*This function tests our test data.It does this by having each of our hypothesises evaluate the mail individually.
	 * We also have a variable named result.This will be used later in order to see in what type the mail actually belongs
	 * If the miniID3 tree evaluates is as spamm,we substract the miniID3's voting weight from the result,if it's a ham
	 * we add the miniID3's voting weight to the result.In the end (after all our ID3's have evaluated a single mail),
	 * we check the sign of the result,if it's negative then we have spam,if it's positive then we have ham!Of course we do
	 * this for every mail in our test data*/
	public double test(Data[] data) {
		double correct=0;
		double result=0;
		System.out.println("Evaluating...");
		for(int i=0;i<test_data.size();i++) {
			result=0;
			for(int j=0;j<data.length;j++) {
				if(data[j].evaluate(test_data.get(i))==true)result=result +data[j].getVotingWeight();
				else result=result - data[j].getVotingWeight();
			}
			if(result>0) {
				if(test_data.get(i).getType()==true) correct++;
			} else {
				if(test_data.get(i).getType()==false) correct++;
			}
		}
		double accuracy=(Math.round(((correct/test_data.size())*100)*100.0)/100.0);
		System.out.println("Results: "+accuracy+"% accuracy");
		return accuracy;
	}
	
	/*basically this is the algorithm from the slides,what it does is that it initialises each Id3 tree.All trees use the 
	same training data,but with different weights each.Each ID3's voting weight depends on the number of mistakes that it made.
	A bigger number of mistakes results in lower voting weight.*/
	public Data[] run_ada() {
		Data[] hypothesis = new Data[repetitions];//This is the array that contains the hypothesis 
		double[] hypothesis_weight = new double[repetitions];//This contains the weight of each hypothesis
		double error=0;
		for(int i=0;i<repetitions;i++)
		{
			System.out.print("Attempt #"+(i+1)+" ");
			hypothesis[i]=new Data(mails,important_words);
			hypothesis[i].train();
			error=0;
			for(int j=0;j<mails.size();j++)
			{
				if(hypothesis[i].evaluate(mails.get(j))!=mails.get(j).getType()) error=error+mails.get(j).getWeight();
			}
			
			for(int j=0;j<mails.size();j++)
			{
				double temp=(error)/(1.0-error);
				if(hypothesis[i].evaluate(mails.get(j))==mails.get(j).getType()) mails.get(j).multiplyWeight(temp);
			}
			normalize();
			double temp=(1.0-error)/(error);
			hypothesis_weight[i]=Math.log(temp)/Math.log(2);
		}
		for(int i=0;i<hypothesis.length;i++)
		{
			hypothesis[i].setVotingWeight(hypothesis_weight[i]);
		}
		try {
			writeImportantWords(hypothesis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("Ada has finished!");
		return hypothesis;
	}
	

	/*This method normalizes the voting weights of each mail so that their sum will be 1.IT does this by calculating 
	 * the sum of their weights.Then, it divides each mail's weight by this sum and the result we get is a weight-normalized
	 *  training set.Example-> we want our sum to have the value of 1,but our weights are : 1,3,10. 1+3+10=14,which is 
	 *  definitely not 1.So we'll divide each weight by 14 and see the result. 1/14=0.071 , 3/14=0.214 , 10/14 = 0.714.
	 *  Adding all these 3(our new weights) results to 1 */
	public void normalize() {
		double sum=0;
		for(int i=0;i<mails.size();i++)
		{
			sum=sum+mails.get(i).getWeight();
		}
		for(int i=0;i<mails.size();i++)
		{
			mails.get(i).divideWeight(sum);
		}
	}
	
	//debugging purposes
	public void writeImportantWords(Data[] a) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("importantWords.txt", "UTF-8");
		for (int i=0;i<a.length;i++) {
			writer.println((i+1) +"->" +a[i].getVotingWeight() +" | | " +a[i].getID3().getProperty() +" " +a[i].getID3().entropy);
		}
		writer.close();
	}
}