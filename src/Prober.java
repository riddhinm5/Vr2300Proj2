import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

class docList {
	
	String subCategory;
	String mainCategory;
	ArrayList<String> results; // contains URL specific to each category 
	ArrayList<String> freqList; // contains Word list of each URL pointed by results
	
	docList()
	{
		subCategory = new String();
		mainCategory = new String();
		results = new ArrayList<String>();
		freqList = new ArrayList<String>();
	}
	
	docList(String m, String s, ArrayList<String> a)
	{
		subCategory = new String();
		mainCategory = new String();
		results = new ArrayList<String>();
		freqList = new ArrayList<String>();
		subCategory = s;
		mainCategory = m;
		results = a;	
	}
	
	docList(String m, String s, ArrayList<String> a, ArrayList<String> f)
	{
		subCategory = new String();
		mainCategory = new String();
		results = new ArrayList<String>();
		freqList = new ArrayList<String>();
		subCategory = s;
		mainCategory = m;
		results = a;	
		freqList = f;
	}
}


class Prober{
	static Map<Integer, String> heirarchy;
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
	static ArrayList<docList> docs = new ArrayList<docList>();
	static Map<String, Integer> wordFreq;
	static docList docL = new docList();
	
	public static void main(String [] args) throws Exception{
		initProber();
		setQueries();
		RootQuery();

		//classificationStr = "Root";
		//ReturnCoverage();
		//for(String key : categoryCoverage.keySet())
		for( int i = 2 ; i <= heirarchy.size() ; i++ )
			if(categoryCoverage.get(heirarchy.get(i)) >= coverageTh){
				if(heirarchy.get(i) == "Computers") {
					Thread.sleep(6000);
					ComputerQuery1();
					ReturnSpecificity();
				}
				else if(heirarchy.get(i) == "Sports") {
					Thread.sleep(6000);
					SportQuery1();
					ReturnSpecificity();
				}
				else if(heirarchy.get(i) == "Health") {
					Thread.sleep(6000);
					HealthQuery1();
					ReturnSpecificity();
				}
			}
		
		classificationStr = "/";
		boolean flag = true;
		//for(String key : categoryCoverage.keySet())
		for( int i = 1 ; i <= heirarchy.size(); i++ ){
			if(categoryCoverage.get(heirarchy.get(i)) >= coverageTh){
				if(categorySpecificity.get(heirarchy.get(i)) >= specificityTh){
					classificationStr += heirarchy.get(i)+" /";
				}
			}
			else flag = false;
		}
		
		if( flag )
			classificationStr = "/Root";
				
		DisplayResult();
		
		//get content summary for matching categories
				if(classificationStr.contains("Root"))
			    getContentSummary("Root");
				if(classificationStr.contains("Health"))
				    getContentSummary("Health");
				if(classificationStr.contains("Computers"))
				    getContentSummary("Computers");
				if(classificationStr.contains("Sports"))
				    getContentSummary("Sports");
				
				if(classificationStr.contains("Health"))
					outputTopicContentSummary("Health");
				if(classificationStr.contains("Computers"))
					outputTopicContentSummary("Computers");
				if(classificationStr.contains("Sports"))
					outputTopicContentSummary("Sports");
				
				if(classificationStr.contains("Root"))
					outputTopicContentSummary("Root");
			   
				printSample();
			    
			    
	}
	
	// get word List for specified Category 
		public static void getContentSummary(String cat)
		{
			Set s  = new TreeSet() ;
			ArrayList<String> f ;
			for(int i =0; i < docs.size() ; i++)
			{
			  f = new ArrayList<String>();
			  f.add("0");
			  docL = docs.get(i);
			  if(cat.equals(docL.mainCategory))
			  {
				  if(Integer.parseInt(docL.results.get(0)) >= 4) 
				  {
					
				     for(int j =1; j < docL.results.size() ; j++)
				     {
				        s = getWordsLynx.runLynx(docL.results.get(j));	
				        f.add(s.toString());
				     }
				     
				     docs.set(i, new docList(docL.mainCategory, docL.subCategory, docL.results, f));
				  }   
			  }
			}
			
		}
	
		public static String calculateContentSummary(String cat)
		{
			String buf = new String();
			wordFreq = new TreeMap<String,Integer>();	
			ArrayList<String> category = new ArrayList<String>();
			
			if(cat.equals("Root"))
			{
				category.add("Root");
				if(classificationStr.contains("Computers"))
					category.add("Computers");
				if(classificationStr.contains("Health"))
					category.add("Health");
				if(classificationStr.contains("Sports"))
					category.add("Sports");			
			}
			else category.add(cat);
			
			for(int i =0 ; i < docs.size() ; i++)
		    {
		    	docL = docs.get(i);
		    	if(category.contains(docL.mainCategory))
		    	{
		    		for(int j =0; j < docL.freqList.size() ; j++)
		    		{
		    			String s = new String();
		    			s= docL.freqList.get(j);
		    			StringTokenizer st = new StringTokenizer(s, "[,]");
		    			while(st.hasMoreTokens())
		    			{
		    				String word = st.nextToken();
		    				if(wordFreq.containsKey(word))
		    				{
		    					int count = wordFreq.get(word);
		    					count +=1;
		    					wordFreq.remove(word);
		    					wordFreq.put(word, count);
		    					
		    				}
		    				else
		    				{
		    					wordFreq.put(word, 1);
		    				}
		    			}
		    			
		    		}
		    	}
		    }
			
			 Iterator<String> it = wordFreq.keySet().iterator();
			 while (it.hasNext())
			 {
	             String key = (String) it.next();
	             Integer value = wordFreq.get(key);
	             buf += key + "    " + value + "\n";
			 }
			  
			return buf;
		}
		
		public static void outputTopicContentSummary(String cat) 
		{
			String docName = cat + "-" + site + ".txt";
			String buf = new String();
			buf = calculateContentSummary(cat);
			try
			{
			   RandomAccessFile randomAccessFile  = new RandomAccessFile("/home/vr2300/workspace/"+docName ,"rw");
			   randomAccessFile.writeBytes(buf.toString());
			   randomAccessFile.close();
			}
			catch(Exception e) {}
			
			
		}
		//Prints  URL and List of Words of the Samples where the database belongs
		public static void printSample() 
		{
			System.out.println("Press Enter to Continue:");
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			
			for(int i =0; i < docs.size() ; i++)
		    {
		    	docL = docs.get(i);
		    	if(classificationStr.contains(docL.mainCategory))
		    	{	
		    	   System.out.println("Main:" + docL.mainCategory);
		           System.out.println("Sub:" + docL.subCategory );
		        
		          if(docL.mainCategory.length() > 0)
		          {	
		             System.out.println("Results Count " + docL.results.get(0));
		             for(int j =0; j< docL.results.size(); j++)
		             {
		                System.out.println( docL.results.get(j));
		                if(j < docL.freqList.size())
		                System.out.println( docL.freqList.get(j));
		              
		             }
		          }   
		       }
		    }
		}

		
	public static void initProber() {
		
		RootQueries = new ArrayList<String>();
		ComputerQueries = new ArrayList<String>();
		SportQueries = new ArrayList<String>();
		HealthQueries = new ArrayList<String>();
		
		heirarchy = new HashMap<Integer, String>();
		heirarchy.put(1, "Root");
		heirarchy.put(2, "Computers");
		heirarchy.put(3, "Hardware");
		heirarchy.put(4, "Programming");
		heirarchy.put(5, "Health");
		heirarchy.put(6, "Diseases");
		heirarchy.put(7, "Fitness");
		heirarchy.put(8, "Sports");
		heirarchy.put(9, "Soccer");
		heirarchy.put(10, "Basketball");
		
		categoryCoverage = new HashMap<String, Integer>();
		categoryCoverage.put("Root", 0);
		categoryCoverage.put("Computers", 0);
		categoryCoverage.put("Hardware", 0);
		categoryCoverage.put("Programming", 0);
		categoryCoverage.put("Health", 0);
		categoryCoverage.put("Diseases", 0);
		categoryCoverage.put("Fitness", 0);
		categoryCoverage.put("Sports", 0);
		categoryCoverage.put("Basketball", 0);
		categoryCoverage.put("Soccer", 0);
		
		categorySpecificity = new HashMap<String, Double>();
		for(String key : categoryCoverage.keySet())
			categorySpecificity.put(key, 0.0);
		
		gr = new GetResults();
		
		System.out.println("enter site to be classified:");
		Scanner sc = new Scanner(System.in);
		site = sc.nextLine();
		
		System.out.println("enter the coverage threshold:");
		coverageTh = sc.nextInt();
		
		System.out.println("enter the specificity threshold:");
		specificityTh = sc.nextDouble();
		
		String nameOfSite = "";
		for(int i = 0 ; i < site.length() ; i++ )
			if(site.charAt(i) != '.')
				nameOfSite += site.charAt(i);
			else break;
		
		FirstQuery = new ArrayList<String>();
		FirstQuery.add("a");
		FirstQuery.add("the");
		FirstQuery.add("");
		FirstQuery.add(nameOfSite);
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
		int total =0;
		for( int i = 0 ; i < FirstQuery.size() ; i++ )
		if(numDocsIntotal <= (total = gr.get(site, FirstQuery.get(i))))
			numDocsIntotal = total;
		
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
			if(numInRoot >= numDocsIntotal)
				numDocsIntotal = numInRoot;
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
	
	public static void ReturnCoverage() {
		for(String keys : categoryCoverage.keySet())
			categoryCoverage.put(keys, (categoryCoverage.get(keys)/categoryCoverage.get("Root")));
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