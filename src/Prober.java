import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


class Prober{
	
	static ArrayList<String> RootQueries;
	static ArrayList<String> ComputerQueries;
	static ArrayList<String> SportQueries;
	static ArrayList<String> HealthQueries;
	static ArrayList<String> FirstQuery;
	static Map<String, Integer> categoryCoverage;
	static Map<String, Double> categorySpecificity;
	static int numDocsInCat = 0;
	static int numDocsIntotal = 0;
	static GetResults gr;
	static String site;
	static int coverageTh = 0;
	static Double specificityTh = 0.0;
	static String classificationStr;
	
	public static void main(String [] args) throws Exception{
		initProber();
		setQueries();
		RootQuery();

		//classificationStr = "Root";

		for(String key : categoryCoverage.keySet())
			if(categoryCoverage.get(key) >= coverageTh){
				if(key == "Computers") {
					ComputerQuery1();
					ReturnSpecificity();
				}
				else if(key == "Sports") {
					SportQuery1();
					ReturnSpecificity();
				}
				else if(key == "Health") {
					HealthQuery1();
					ReturnSpecificity();
				}
			}
		
		for(String key : categoryCoverage.keySet())
			if(categoryCoverage.get(key) >= coverageTh){
				if(categorySpecificity.get(key) >= specificityTh){
					classificationStr += "/";
					classificationStr += key;
				}
			}
		
		
		DisplayResult();
	}
	
	public static void initProber() {
		
		RootQueries = new ArrayList<String>();
		ComputerQueries = new ArrayList<String>();
		SportQueries = new ArrayList<String>();
		HealthQueries = new ArrayList<String>();
		
		categoryCoverage = new HashMap<String, Integer>();
		categoryCoverage.put("Computers", 0);
		categoryCoverage.put("Health", 0);
		categoryCoverage.put("Sports", 0);
		categoryCoverage.put("Root", 0);
		categoryCoverage.put("Hardware", 0);
		categoryCoverage.put("Programming", 0);
		categoryCoverage.put("Diseases", 0);
		categoryCoverage.put("Fitness", 0);
		categoryCoverage.put("Basketball", 0);
		categoryCoverage.put("Soccer", 0);
		
		categorySpecificity = new HashMap<String, Double>();
		categorySpecificity.put("Computers", 0.0);
		categorySpecificity.put("Health", 0.0);
		categorySpecificity.put("Sports", 0.0);
		categorySpecificity.put("Root", 0.0);
		categorySpecificity.put("Hardware", 0.0);
		categorySpecificity.put("Programming", 0.0);
		categorySpecificity.put("Diseases", 0.0);
		categorySpecificity.put("Fitness", 0.0);
		categorySpecificity.put("Basketball", 0.0);
		categorySpecificity.put("Soccer", 0.0);
		
		FirstQuery = new ArrayList<String>();
		FirstQuery.add("a");
		FirstQuery.add("the");
		
		gr = new GetResults();
		
		System.out.println("enter site to be classified:");
		Scanner sc = new Scanner(System.in);
		site = sc.nextLine();
		
		System.out.println("enter the coverage threshold:");
		coverageTh = sc.nextInt();
		
		System.out.println("enter the specificity threshold:");
		specificityTh = sc.nextDouble();
	}
	
	public static void setQueries()throws Exception {
		String query;
		try{
		
			BufferedReader fr = new BufferedReader(new FileReader("RootQueries.txt"));
			while((query = fr.readLine()) != null)
				RootQueries.add(query);
			
			fr = new BufferedReader(new FileReader("ComputerQueries.txt"));
			while((query = fr.readLine()) != null)
				ComputerQueries.add(query);
			
			fr = new BufferedReader(new FileReader("SportQueries.txt"));
			while((query = fr.readLine()) != null)
				SportQueries.add(query);
			
			fr = new BufferedReader(new FileReader("HealthQueries.txt"));
			while((query = fr.readLine()) != null)
				HealthQueries.add(query);
		
		}
		catch(Exception e){System.out.println(e);}
	}
	
	public static void RootQuery()throws Exception {
		//using generic words like "a" to get total number of results in the hidden database
		int numInRoot = 0;
		if(numDocsIntotal <= gr.get(site, FirstQuery.get(0)))
			numDocsIntotal = gr.get(site, FirstQuery.get(0));
		if(numDocsIntotal <= gr.get(site, FirstQuery.get(1)))
			numDocsIntotal = gr.get(site, FirstQuery.get(1));
		
			//to check the coverage in each category of the root
			for(int i = 0; i < RootQueries.size(); i++) {
				String[] Category = RootQueries.get(i).split(" ");
				numDocsInCat = categoryCoverage.get(Category[0]);
				if(numDocsInCat <= gr.get(site, RootQueries.get(i))) {
					numDocsInCat = gr.get(site, RootQueries.get(i));
					numInRoot += numDocsInCat;
					categoryCoverage.put(Category[0], numDocsInCat);
				}	
			}
			
			categoryCoverage.put("Root", numInRoot);
	}
	
	public static void ComputerQuery(){
		String query = null;
	}
	
	public static void SportQuery(){
		String query = null;
	}
	
	public static void HealthQuery(){
		String query = null;
	}

	public static void ComputerQuery1()throws Exception {
		
		for(int i = 0; i < ComputerQueries.size(); i++) {
			String[] Category = ComputerQueries.get(i).split(" ");
			numDocsInCat = categoryCoverage.get(Category[0]);
			if(numDocsInCat <= gr.get(site, ComputerQueries.get(i))) {
				numDocsInCat = gr.get(site, ComputerQueries.get(i));
				categoryCoverage.put(Category[0], numDocsInCat);
			}	
		}
		
	}
	
	public static void HealthQuery1()throws Exception {
		
		for(int i = 0; i < HealthQueries.size(); i++) {
			String[] Category = HealthQueries.get(i).split(" ");
			numDocsInCat = categoryCoverage.get(Category[0]);
			if(numDocsInCat <= gr.get(site, HealthQueries.get(i))) {
				numDocsInCat = gr.get(site, HealthQueries.get(i));
				categoryCoverage.put(Category[0], numDocsInCat);
			}	
		}
		
	}
	
	public static void SportQuery1()throws Exception {
		
		for(int i = 0; i < SportQueries.size(); i++) {
			String[] Category = SportQueries.get(i).split(" ");
			numDocsInCat = categoryCoverage.get(Category[0]);
			if(numDocsInCat <= gr.get(site, SportQueries.get(i))) {
				numDocsInCat = gr.get(site, SportQueries.get(i));
				categoryCoverage.put(Category[0], numDocsInCat);
			}	
		}
		
	}
	
	public static Double ReturnSpecificity(){
		Double specificity = 0.0;
		for(String keys : categoryCoverage.keySet() )
			categorySpecificity.put(keys,(double)categoryCoverage.get(keys)/(double)numDocsIntotal);
		return specificity;
	}
	
	public static void DisplayResult(){	
		System.out.println("Total Documents in the site:" + numDocsIntotal);
		
		for(String keys : categoryCoverage.keySet() )
			System.out.println("Coverage for "+ keys +" : " + categoryCoverage.get(keys));
		
		for(String keys : categoryCoverage.keySet() )
			System.out.println("Specificity for "+ keys +" : " + categorySpecificity.get(keys));
				
				System.out.println("Classification:"+ classificationStr);
	}
}