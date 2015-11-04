import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


class pair{
	int first, second;
	public pair(int first, int second){
		if(first>second){
			this.first = second;
			this.second = first;
		}else if(first == second){
			try {
				throw new Exception("first and second could not be the same");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			this.first = first;
			this.second = second;
		}
	}

	@Override
	public String toString(){
		return "["+first+", "+second+"]";
	}
}


public class Prj4 {

	public static void main(String[] args) throws IOException {
		Prj4 test = new Prj4();
		test.init();
	}
	
	
	private List<String> clauses;
	private Set<String> visitedClauses;
	private int orginalSize = 0;
	//need to determin whether c1 and c2 are resovable
	private boolean resovable(String c1, String c2){
		String[] elements1 = c1.split(" ");
		Set<String> st = new HashSet<String>();
		for(String ele:elements1){
			if(ele.startsWith("-"))
				st.add(ele.substring(1));
			else
				st.add("-"+ele);
		}
		for(String ele:c2.split(" "))
			if(st.contains(ele))
				return true;
		return false;
	}
	
	public void init() throws IOException{
		//read from txt file to get the CNF clauses
		clauses = new ArrayList<String>();
		visitedClauses = new HashSet<String>();
		File file = new File("NCF_sam.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line = reader.readLine()) != null) {
			//disregard lines starts with "#" and empty line
			if(!line.startsWith("#") && !line.trim().isEmpty()){
				//we need to reorder the elements in each clause
				String[] elements = line.split(" ");
				Arrays.sort(elements, new Comparator<String>(){
					@Override
					public int compare(String s1, String s2){
						if(s1.startsWith("-")){
							if(s2.startsWith("-"))
								return s1.substring(1).compareTo(s2.substring(1));
							else
								return s1.substring(1).compareTo(s2);
						}else if(s2.startsWith("-")){
							return s1.compareTo(s2.substring(1));
						}else
							return s1.compareTo(s2);
					}
				});
				String newLine = "";
				for(String ele:elements){
					newLine+=ele+" ";
				}
				if(visitedClauses.contains(newLine.trim())){
					System.err.println("Duplicated NCF input");
					System.exit(-1);
				}
				clauses.add(newLine.trim());
				visitedClauses.add(newLine.trim());
			}
	    }
		//System.out.println(clauses.size());
		int lineNum = 0;
		for(String each:clauses){
			System.out.println(lineNum+": "+each);
			lineNum++;
		}
		reader.close();
		orginalSize = clauses.size();
		
		//once we get the clauses, we need to get the resolvable clause pairs
		PriorityQueue<pair> qu = new PriorityQueue<pair>(2*clauses.size(),new Comparator<pair>(){
			@Override
			public int compare(pair p1, pair p2){
				int length1 = clauses.get(p1.first).length()+clauses.get(p1.second).length();
				int length2 = clauses.get(p2.first).length()+clauses.get(p2.second).length();
				return length1-length2;
			}
		});
		
		for(int i=0;i<clauses.size();i++){
			for(int j=i;j<clauses.size();j++){
				if(j!=i){
					if(resovable(clauses.get(i),clauses.get(j)))
						qu.add(new pair(i,j));
				}
			}
		}
		//System.out.println(qu);
		//call solve to solve this problem
		solve(qu);
	}
	
	private List<String> resolve(pair curr){
		List<String> result = new ArrayList<String>();
		String[] firstClause = clauses.get(curr.first).split(" ");
		String[] secondClause = clauses.get(curr.second).split(" ");
		
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		for(int i=0;i<firstClause.length;i++){
			if(firstClause[i].startsWith("-"))
				hm.put(firstClause[i].substring(1), i);
			else
				hm.put("-"+firstClause[i], i);
		}
		//System.out.println(hm);
		for(int i=0;i<secondClause.length;i++){
			if(hm.containsKey(secondClause[i])){
				//we could make a new clause here
				
				String generatedClause = merge(firstClause, hm.get(secondClause[i]),secondClause, i);
				System.out.println((clauses.size())+": "+convertClause(generatedClause.equals("")?"\"Empty_String\"":generatedClause)+" generated from "+curr.first+" "+curr.second);
				if(!generatedClause.equals(""))
					result.add(generatedClause);
			}
		}
		return result;
	}
	
	private String merge(String[] firstClause, int fristIndex, String[] secondClause, int secondIndex){
		//List<String> result = new ArrayList<String>();
		//merge two sorted array
		//System.out.println(fristIndex + " "+secondIndex);
		int first = 0, second =0;
		String result = "";
		while(first<firstClause.length && second<secondClause.length){
			if(first==fristIndex){
				first++;
				continue;
			}
			if(second==secondIndex){
				second++;
				continue;
			}
			
			String firstString = firstClause[first].startsWith("-")?firstClause[first].substring(1):firstClause[first];
			String secondString = secondClause[second].startsWith("-")?secondClause[second].substring(1):secondClause[second];
			int compareCode = firstString.compareTo(secondString);
			if(compareCode<0){
				result+= firstClause[first]+" ";
				first++;
			}else if(compareCode==0){
				//the same, we only reserve one
				result+=firstClause[first]+" ";
				first++;
				second++;
			}else{
				result+=secondClause[second]+" ";
				second++;
			}
			//firstClause[first].compareTo(secondClause[second])
		}
		
		
		while(first<firstClause.length){
			if(first!=fristIndex)
				result += firstClause[first]+" ";
			first++;
		}
		while(second<secondClause.length){
			if(second!=secondIndex)
				result += secondClause[second]+" ";
			second++;
		}
		return result.trim();
	}
	
	private String convertClause(String clause){
		String[] eles = clause.split(" ");
		String result ="";
		for(int i=0;i<eles.length-1;i++){
			result += eles[i]+" V ";
			//System.out.print(eles[i]+" V ");
		}
		result +=eles[eles.length-1];
		return result;
	}
	
	public void solve(PriorityQueue<pair> qu){
		HashMap<Integer,pair> hm = new HashMap<Integer,pair>();
		int iteration = 0;
		while(!qu.isEmpty()){
			iteration++;
			//System.out.println("qu is: "+qu);
			pair curr = qu.poll();
			System.out.println("iteration "+iteration +", queue size "+(qu.size()+1)+", "+"resolution on: "+curr.first+" "+curr.second);
			System.out.println("resolving "+convertClause(clauses.get(curr.first))+" and "+convertClause(clauses.get(curr.second)));
			
			//find the opposite literals and then get the merged clauses
			List<String> resolvent = resolve(curr);
			//System.out.println("resolvent: "+resolvent +resolvent.isEmpty()+resolvent.size());
			/*System.out.println("------");
			for(String s:resolvent){
				
				System.out.println(s.equals(""));
				
			}
			System.out.println("------");*/
			if(resolvent.size()==0){
				System.out.println("SUCCESS! empty clause found");
				//print back trace
				hm.put(clauses.size(), curr);
				post_processing(hm,clauses.size(),0);
				return;
			}
			//then need to filter the duplicated clause
			//System.out.println("clauses: "+clauses);
			for(String each:resolvent){
				if(!visitedClauses.contains(each)){
					hm.put(clauses.size(), curr);
					visitedClauses.add(each);
					clauses.add(each);
					
					//then need to resovable clause pairs to qu
					for(int i=0;i<clauses.size()-1;i++){
						if(resovable(clauses.get(i), each)){
							qu.add(new pair(i,clauses.size()-1));
						}
					}
					
				}
			}
		}
	}
	
	public void post_processing(HashMap<Integer,pair> hm, int root, int layer){
		int tmp = layer;
		while(tmp>0){
			System.out.print(" ");
			tmp--;
		}
		//System.out.println(hm);
		if(root==clauses.size()){
			System.out.println(root+": " +" [] "+hm.get(root));
		}else if(root<orginalSize){
			System.out.println(root+": " +convertClause(clauses.get(root))+" [input] ");
			return;
		}
		else
		{
			System.out.println(root+": " +convertClause(clauses.get(root))+" "+hm.get(root));
		}
		pair parent = hm.get(root);
		post_processing(hm,parent.first,layer+1);
		post_processing(hm,parent.second,layer+1);
	}

}
