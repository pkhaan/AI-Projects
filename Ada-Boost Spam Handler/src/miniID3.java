/**
 * 
 */
import java.util.ArrayList;
/**
 * @author anubis2
 *
 */
public class miniID3 {
	/*So this basically is a overly simplified ID3 tree
	We'll be picking a property and we'll be checking it
	leftAction is what the mail will be classified as if it has the value,same for rightAction*/
	private boolean leftAction;
	private boolean rightAction;
	private String property;
	public double entropy;//This is the entropy that we found when se set up the property String
	
	public miniID3(String property,ArrayList<Email> list) {
		this.property = property;
		double hasPropertyIsSpam=0;
		double hasPropertyIsHam=0;
		double doesntHavePropertyIsHam=0;
		double doesntHavePropertyIsSpam=0;
		/*checks how many mails have-don't have the word and what type they are
		and it keeps a count of these numbers in order to initialize
		left and right action accordingly*/
		for(int i=0;i<list.size();i++) {
			if(list.get(i).contains(property)) {
				if(list.get(i).getType()==true) {
					hasPropertyIsHam = hasPropertyIsHam+list.get(i).getWeight();
				} else {
					hasPropertyIsSpam = hasPropertyIsSpam+list.get(i).getWeight();
				}
			} else {
				if(list.get(i).getType()==true) {
					doesntHavePropertyIsHam = doesntHavePropertyIsHam + list.get(i).getWeight();
				} else {
					doesntHavePropertyIsSpam = doesntHavePropertyIsSpam + list.get(i).getWeight();
				}
			}
		}
		/*So for example,if the mails that contain the String are mostly spamm,then if the mail contains
		this word,we'll automatically evaluate it as spamm*/
		if(hasPropertyIsSpam>hasPropertyIsHam) {
			leftAction=false;
		} else {
			leftAction=true;
		}
		if(doesntHavePropertyIsSpam>doesntHavePropertyIsHam) {
			rightAction=false;
		} else {
			rightAction=true;
		}
		/*Down here,we're just checking the case where a branch doesn't have any emails,then the mail will be identified as
		the majority of mail type in the mails that the parent contains.*/
		if(hasPropertyIsSpam==0 && hasPropertyIsHam==0) {
			double spam = 0;
			double ham = 0;
			for(int i=0;i<list.size();i++) {
				if(list.get(i).getType()==true) {
					ham=ham +list.get(i).getWeight();
				} else {
					spam=spam+ list.get(i).getWeight();
				}
			}
			if(ham>spam) {
				leftAction=true;
			} else {
				leftAction=false;
			}
		}
		if(doesntHavePropertyIsSpam==0 && doesntHavePropertyIsHam==0) {
			double spam = 0;
			double ham = 0;
			for(int i=0;i<list.size();i++) {
				if(list.get(i).getType()==true) {
					ham=ham +list.get(i).getWeight();
				} else {
					spam=spam+ list.get(i).getWeight();
				}
			}
			if(ham>spam) {
				rightAction=true;
			} else {
				rightAction=false;
			}
		}
	}
	
	public boolean evaluate(Email e) {//if the mail has the word property we return the leftAction,otherwise the rightAction
		if(e.contains(property))return leftAction;
		else return rightAction;
	}
	
	public String getProperty() {
		return property;
	}

}
