package AI;

import java.util.*;

public class Prj3Part2_MRV {

	private int iteration = 0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Prj3Part2_MRV test = new Prj3Part2_MRV();
		test.playGame();
		
	}

	public void playGame() {
		//table looks like this:
		//House	        1	2	3	4	5
		//Color	        Yellow	Blue	Red	Ivory	Green
		//Nationality	Norwegian	Ukrainian	Englishman	Spaniard	Japanese
		//Drink	        Water	Tea	Milk	Orange juice	Coffee
		//eat	        Kools	Chesterfield	Old Gold	Lucky Strike	Parliament
		//Pet	        Fox	Horse	Snails	Dog	Zebra
		String[][] table = new String[5][5];
		for(String[] row:table){
			for(int i=0;i<row.length;i++){
				row[i] = "";
			}
		}
		
		HashSet<String> Colors = new HashSet<String>(Arrays.asList(new String[]{"Yellow","Blue","Red","Ivory","Green"}));
		HashSet<String> Nationality = new HashSet<String>(Arrays.asList(new String[]{"Norwegian","Ukrainian","Englishman","Spaniard","Japanese"}));
		HashSet<String> Drink = new HashSet<String>(Arrays.asList(new String[]{"Water","Tea","Milk","Orange Juice","Coffee"}));
		HashSet<String> Eat = new HashSet<String>(Arrays.asList(new String[]{"Hershey bars","Kits Kats","Smarties","Snickers","Milky Ways"}));
		HashSet<String> Pet = new HashSet<String>(Arrays.asList(new String[]{"Fox","Horse","Snails","Dog","Zebra"}));
		
		HashSet<String>[] domains = new HashSet[5];
		domains[0] = Colors;
		domains[1] = Nationality;
		domains[2] = Drink;
		domains[3] = Eat;
		domains[4] = Pet;

		if (backtracking2(new boolean[25], 0,table, domains, 0)){
			System.out.println("iterations: "+iteration);
			for(int i=0;i<table.length;i++){
				for(int j=0;j<table[0].length;j++){
					System.out.print(table[i][j]+" ");
				}
				System.out.println();
			}
		}
		else
			System.out.println("Fail to find solution!!!");
		
	}
	
	private boolean backtracking2(boolean[] occupied, int position, String[][] table, HashSet<String>[] domainsTable, int layer) {
		//iteration++;
		
		boolean allOccupied = true;
		for(boolean ele:occupied){
			if(!ele){
				allOccupied = false;
				break;
			}
		}
		if(allOccupied)
			return true;
		
		int row = position/5;
		int col = position%5;
		HashSet<String> domains = domainsTable[row];
		if(domains.isEmpty()){
			System.out.println("table is empty");
			return false;
		}
		HashSet<String> nextDomains = new HashSet<String>(domains);
		
		for(String val:nextDomains){
			iteration++;
			domains.remove(val);
			table[row][col] = val;
			
			if(consistency_check_job(position, table)){
				
				//choose the position that has the MRV property: minimum minimum remaining value
				occupied[position] = true;
				
				int nextpositon = findMRV(occupied, table, domainsTable);
				//System.out.println(nextpositon);
				if(backtracking2(occupied, nextpositon, table, domainsTable, layer+1)){
					//System.out.println("SUCCESS");
					return true;
				}
				occupied[position] = false;
				
			}
			domains.add(val);
			table[row][col] = "";
		}
		return false;
	}
	
	private int findMRV(boolean[] occupied, String[][] table, HashSet<String>[] domainsTable ){
		int result = 0;
		int minCount = Integer.MAX_VALUE;
		/*System.out.println("-----------------");
		for(int i=0;i<occupied.length;i++){
			if(!occupied[i])
				System.out.println(i);
		}
		System.out.println("-----------------");*/
		for(int i=0;i<occupied.length;i++){
			if(!occupied[i]){
				int count = 0;
				//for i, we need to calcualte the size of availabe variable could be assigned to it
				int row = i/5;
				int col = i%5;
				for(String val:domainsTable[row]){
					String org = table[row][col];
					table[row][col] = val;
					if(consistency_check_job(i,table))
						count++;
					table[row][col] = org;
				}
				//System.out.println("result:"+result);
				if(minCount>count){
					minCount = count;
					result = i;
				}
			}
		}
		//System.out.println("result:"+result);
		return result;
	}
	
	private void helper(int position, String[][] table){
		System.out.println("------------Start------------");
		System.out.println("position is: "+position);
		int tmp = 0;
		while(tmp<=position){
			int row = tmp/5;
			int col = tmp%5;
			System.out.print(table[row][col]+" ");
			if(col==4)
				System.out.println();
			tmp++;
		}
		System.out.println("-----------End-------------");
	}

	public boolean consistency_check_job(int position, String[][] table) {

		int row = position/5;
		int col = position%5; 
		
		if(col==2){
			if(table[2][col]!="" && table[2][col]!="Milk")
				return false;
		}
		
		if(col==0){
			if(table[1][col]!="" && table[1][col]!="Norwegian")
				return false;
		}
		
		switch(row){
			case 0://color
				switch(table[row][col]){
					case "Red"://make sure that englishman lives in it
						if(table[1][col]!="" && table[1][col]!="Englishman"){
							helper(position, table);
							return false;
						}
						break;
					case "Green":
						//The green house is immediately to the right of the ivory house
						if(col==0)
							return false;
						if(table[0][col-1]!="" && table[0][col-1]!="Ivory")
							return false;
						//Coffee is drunk in the green house
						if(table[2][col]!="" && table[2][col]!="Coffee")
							return false;
						break;
					case "Ivory":
						if(col==4)
							return false;
						if(table[0][col+1]!="" && table[0][col+1]!="Green")
							return false;
						break;
					case "Yellow":
						if(table[3][col]!="" && table[3][col]!="Kits Kats")
							return false;
						break;
					case "Blue":
						if(col!=1)
							return false;
						break;
					
				}
				break;
			case 1://nationality
				switch(table[row][col]){
					case "Spaniard"://make sure that englishman lives in it
						if(table[4][col]!="" && table[4][col]!="Dog")
							return false;
						break;
					case "Norwegian":
						if(col!=0)
							return false;
						//check blue house
						if(table[0][col+1]!="" && table[0][col+1]!="Blue")
							return false;
						break;
					case "Ukranian":
						if(table[2][col]!="" && table[2][col]!="Tea")
							return false;
						break;
					case "Japanese":
						if(table[3][col]!="" && table[3][col]!="Milky Ways")
							return false;
						break;
					case "Englishman":
						if(table[0][col]!="" && table[0][col]!="Red")
							return false;
						break;
					
				}
				break;
			case 2://drink
				switch(table[row][col]){
					case "Tea":
						if(table[1][col]!="" && table[1][col]!="Ukrainian"){
							//System.out.println("???????????????????????????????????????????????");
							return false;
						}
						break;
					case "Orange Juice":
						if(table[3][col]!="" && table[3][col]!="Snickers")
							return false;
						break;
					case "Milk":
						if(col!=2)
							return false;
						break;	
					case "Coffee":
						if(table[0][col]!="" && table[0][col]!="Green")
							return false;
						break;	
				}
				break;
			case 3://eat
				switch(table[row][col]){
					case "Smarties":
						if(table[4][col]!="" && table[4][col]!="Snails")
							return false;
						break;
					case "Snickers":
						if(table[2][col]!="" && table[2][col]!="Orange Juice")
							return false;
						break;
					case "Milky Ways":
						if(table[1][col]!="" && table[1][col]!="Japanese")
							return false;
						break;
					case "Kits Kats":
						if(col==0){
							if(table[4][col+1]!="" && table[4][col+1]!="Horse")
								return false;
						}else if(col==4){
							if(table[4][col-1]!="" && table[4][col-1]!="Horse")
								return false;
						}else{
							if((table[4][col-1]!="" && table[4][col-1]!="Horse") && (table[4][col+1]!="" && table[4][col+1]!="Horse"))
								return false;
						}
						if(table[0][col]!="" && table[0][col]!="Yellow")
							return false;
						break;
					case "Hershey bars":
						if(col==0){
							if(table[4][col+1]!="" && table[4][col+1]!="Fox")
								return false;
						}else if(col==4){
							if(table[4][col-1]!="" && table[4][col-1]!="Fox")
								return false;
						}else{
							if((table[4][col-1]!="" && table[4][col-1]!="Fox") && (table[4][col+1]!="" && table[4][col+1]!="Fox"))
								return false;
						}
						break;
				}
				break;
			case 4://pet
				switch(table[row][col]){
					case "Snails":
						if(table[3][col]!="" && table[3][col]!="Smarties")
							return false;
						break;
					case "Dog":
						if(table[1][col]!="" && table[1][col]!="Spaniard")
							return false;
						break;
					case "Horse":
						if(col==0){
							if(table[3][col+1]!="" && table[3][col+1]!="Kits Kats")
								return false;
						}else if(col==4){
							if(table[3][col-1]!="" && table[3][col-1]!="Kits Kats")
								return false;
						}else{
							if((table[3][col-1]!="" && table[3][col-1]!="Kits Kats") && (table[3][col+1]!="" && table[3][col+1]!="Kits Kats")){
								return false;
							}
						}
						break;
					case "Fox":
						if(col==0){
							if(table[3][col+1]!="" && table[3][col+1]!="Hershey bars")
								return false;
						}else if(col==4){
							if(table[3][col-1]!="" && table[3][col-1]!="Hershey bars")
								return false;
						}else{
							if((table[3][col-1]!="" && table[3][col-1]!="Hershey bars") && (table[3][col+1]!="" && table[3][col+1]!="Hershey bars"))
								return false;
						}
						break;
				
				}
				break;
			default:
				System.out.println("Wrong");
				break;
			
		}
		return true;
	}
	
	
}
