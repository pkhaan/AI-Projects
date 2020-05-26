import java.util.*;
import java.io.*;

public class Main {
	static double percentage = 0.1;
	
	public static void main(String[] args) {
		Adaboost base = new Adaboost(); //This will be the base that contains all the emails (spam and ham)
		File folder; //The folder that contains 2 other folders (spam and ham)
		File[] filesArray;
		//Open the ham folder
		System.out.println("Reading Mails--parsing folder...");
		folder = new File("mails/ham");
        filesArray = folder.listFiles();
        for(File file : filesArray) { //For each file in "mails/ham/" read it and save it in the TD
			base.add(ReadEmail("mails/ham/"+file.getName(),true));
        }
		//Open the spam folder
		folder = new File("mails/spam");
        filesArray = folder.listFiles();
        for(File file : filesArray) { //For each file in "mails/spam/" read it and save it in the TD
			base.add(ReadEmail("mails/spam/"+file.getName(),false));
        }
		base.update();
		Data[] data = base.run_ada();
		base.test(data);
	}
	
	public static Email ReadEmail(String txt, boolean type) {
		Scanner scan = null;
		try {
		    scan = new Scanner(new File(txt));
		} catch (Exception e) {
            System.err.println("Failed to open file: "+txt);
        }
		Email mail = new Email(type);
		String currentLine; //The current line we are reading
		String[] words; //Words in that line
		//For each line in the text
		while(scan.hasNextLine()) {
			currentLine = scan.nextLine(); //Read the line
			words = currentLine.split(" "); //Split it into strings
			//Add all the strings in the hash set
			for(int i=0;i<words.length;i++) {
				mail.add(words[i].toUpperCase());
			}
		}
		return mail;
	}
}