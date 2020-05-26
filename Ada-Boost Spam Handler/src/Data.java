/**
 * 
 */

/**
 * @author anubis2
 *
 */
import java.util.ArrayList;
import java.util.HashSet;

public class Data {
	private ArrayList<Email> emails; //Contains all the emails used during the training
	private ArrayList<wordEnt> words_entropy;//contains the entropy of each word on listed_words
	private HashSet<String> listed_words; //Contains all the used words - helps us to find entropy faster
	private miniID3 miniID3;//this is the miniID3 that will be used in order to evaluate our things
	private double votingWeight;//this thing's voting weight
	
	/*This class uses the IG functions from the previous exercise,the only difference is that instead of adding 1 on the countwhen we find an
	 * element that has the property that we want,it adds the mail's weight to it */
	
	public Data(ArrayList<Email>mails,HashSet<String> listed_words) {
		emails=mails;
		this.listed_words=listed_words;
	}
	
	public miniID3 getID3() {
		return miniID3;
	}
	
	public ArrayList<Email> getEmails() {
		if(emails != null) {
			return emails;
		} else {
			return null;
		}
	}
	
	public void setVotingWeight(double w) {
		votingWeight = w;
	}
	
	public double getVotingWeight() {
		return votingWeight;
	}
	
	/*This is H(C).It counts how many emails are spam and ham,and then divides these by the size of the emails.
	 * THis gives us the chance of a mail being spam and a mail being ham,then we use this chance(let's say x)
	 * and make the following calculation P(x)*log(x),we do this for x=C=0 and x=C=1 and we have sum1 and sum2.
	 * then we just return (-sum1 -sum2)
	 */
	public double Hc() {
		double sumH=0;
		double sumS=0;
		for(int i=0;i<emails.size();i++)
		{
			if(emails.get(i).getType()==true) sumH=sumH+emails.get(i).getWeight();
			else sumS=sumS+emails.get(i).getWeight();
		}
		double sum1=(sumH)*(Math.log(sumH)/Math.log(2));
		double sum2=(sumS)*(Math.log(sumS)/Math.log(2));
		double result=((-1)*sum1)+((-1)*sum2);
		return result;
	}
	
	/*This calculates H(C|X=x).First of all we do this for both classes (spamm and ham)
	 * At the beginning in the first for loop we calculate how many times we can find
	 * X=x^C=0 and X=x^C=1 in our mails.
	 * after we've found this we create chance 1 and chance2 which is this number that we've
	 * found before divided by the chance of the word having this certain value.This can be
	 * written as P(X=x^C=0)/P(X=x) (the same for C=1) which can be written as->P(C=c|X=x)*/
	public double Hc_x(String s,boolean appears,double wordChance3) {
		double sumS=0;
		double sumH=0;
		for(int i=0;i<emails.size();i++)
		{
			if(emails.get(i).getType()==true)
			{
				if(emails.get(i).contains(s)==appears)sumH=sumH+emails.get(i).getWeight();
			}
			else
			{
				if(emails.get(i).contains(s)==appears)sumS=sumS+emails.get(i).getWeight();
			}

		}
		//System.out.println(sumS);
		double chance1=(sumH)/wordChance3;
		double chance2=(sumS)/wordChance3;
		double part1,part2;
		if(chance1!=0) part1=(chance1)*((Math.log(chance1))/Math.log(2));
		else part1=0;
		if(chance2!=0) part2=(chance2)*((Math.log(chance2))/Math.log(2));
		else part2 = 0;
		
		part1 = part1*(-1);
		part2 = part2*(-1);
		double result = ((part1+part2));
		//System.out.println(result);
		return result;
	}
	
	
	/*This is Sum(P(X=x)*H(C|X=x) (for all values of x)
	wordChance3 calculates the chance of the word having a certain value.Hc(x) was explained before*/
	public double SumPxHc_x(String s) {
		double wc1 = wordChance3(s,false);
		double wc2 = wordChance3(s,true);
		double part1 = wc1*Hc_x(s,false,wc1);
		double part2 = wc2*Hc_x(s,true,wc2);
		double result = part1 + part2;
		//System.out.println(result);
		return result;
	}
	
	/*Calculates the chance of the word having a certain value by checking all the emails.
	If on a mail the word has the same value as appears,the counter is increased.This implements P(X=x) */
	public double wordChance3(String word,boolean appears) {
		double count = 0;
		for(int i = 0;i < emails.size();i++)
		{
			if(emails.get(i).contains(word) == appears)count=count+emails.get(i).getWeight();
		}
		return count;
	}
	
	//This is the IG function IG=H(C)-Sum(P(X=x)*H(C|X=x) (for all values of x)
	public double IG(String s,double Hc) {
		double result = (Hc - SumPxHc_x(s));
		return result;
	}
	
	/*This method calculates the entropy of every element in the listed_words hash set.
	 * After it;s done that it picks the element with the most IG of all and it uses this
	 * as the root of our mini ID3 tree*/
	public void train() {
		words_entropy = new ArrayList<wordEnt>();
		double Hc = Hc();
		System.out.println("Training...");
		@SuppressWarnings("unused")
		int count=0;
		for (String s : listed_words) {
			words_entropy.add(new wordEnt(s,IG(s,Hc)));
			count++;
		}
		double max = -1;
		int position = -1;
		for(int j = 0;j <words_entropy.size();j++) {
			if(words_entropy.get(j).getEntropy() > max) {
				max=words_entropy.get(j).getEntropy();
				position = j;
			}
		}
		if(position != -1) {
			String property = words_entropy.get(position).getName();
			miniID3 = new miniID3(property,emails);
			miniID3.entropy = words_entropy.get(position).getEntropy();
		}
		System.out.println("Training has finished!");
	}
		
	public HashSet<String> train2(int value) {
		HashSet<String> important_properties=new HashSet<String>();
		words_entropy=new ArrayList<wordEnt>();
		double Hc=Hc();
		System.out.print("Training");
		int count=0;
		for (String s : listed_words) {
			words_entropy.add(new wordEnt(s,IG(s,Hc)));
			if(count%3000==0)System.out.print(".");
			count++;
		}
		System.out.println();
		for(int i=0;i<(value);i++) {
			double max=-1;
			int position=-1;
			for(int j=0;j<words_entropy.size();j++) {
				if(words_entropy.get(j).getEntropy()>max) {
					max=words_entropy.get(j).getEntropy();
					position=j;
				}
			}
			if(position!=-1) {
				String word=words_entropy.get(position).getName();
				important_properties.add(word);
				words_entropy.remove(position);
			}
		}
		return important_properties;
	}
	
	public boolean evaluate(Email e) {
		return miniID3.evaluate(e);
	}
}