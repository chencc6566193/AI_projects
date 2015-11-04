package AI;

import java.util.*;

public class Prj3Part1_MRV {

	public static void main(String[] args) {
		Prj3Part1_MRV test = new Prj3Part1_MRV();
		test.playGame();
	}
	private int iteration = 0;

	public void playGame() {

		// Hashtable<String, HashSet<String>> name_domain = new
		// Hashtable<String, HashSet<String>>();
		// initialize
		Hashtable<String, HashSet<String>> name_assignment = new Hashtable<String, HashSet<String>>();

		String[] people = { "Roberta", "Thelma", "Steve", "Pete" };
		String[] jobs = { "chef", "guard", "nurse", "clerk", "police officer", "teacher", "actor", "boxer" };
		HashSet<String> domains = new HashSet<String>(Arrays.asList(jobs));
		for (String name : people) {
			// name_domain.put(name, new HashSet<String>(Arrays.asList(jobs)));
			name_assignment.put(name, new HashSet<String>());
		}
		// System.out.println(name_domain);
		// System.out.println(name_assignment);
		if (backtracking(domains, name_assignment, 0))
			System.out.println(name_assignment);
		else
			System.out.println("Fail to find solution!!!");

	}

	// variables are 4 people, domain values are the jobs that could be assigned
	// to each people
	private boolean backtracking(HashSet<String> domains, Hashtable<String, HashSet<String>> name_assignment,
			int layer) {
		iteration++;
		// get next assignale varible
		String name = "";
		boolean hasNext = false;
		int min = Integer.MAX_VALUE;
		for (String currName : name_assignment.keySet()) {
			if (name_assignment.get(currName).size() < 2){
				hasNext = true;
				int count = getValueNum(currName,domains);
				if(min>count){
					min = count;
					name = currName;
				}
			}
		}
		if (!hasNext) {
			System.out.println("Finish. Total iteration: " + iteration);
			return true;
		}
		// get the name that has the MRV: minimum minimum remaining value
		assert (domains.isEmpty() == false);
		HashSet<String> nextDomains = new HashSet<String>(domains);
		for (String job1 : domains) {
			for (String job2 : domains) {
				if (!job1.equals(job2)) {
					// one possible assignment
					name_assignment.get(name).add(job1);
					name_assignment.get(name).add(job2);
					nextDomains.remove(job1);
					nextDomains.remove(job2);
					if (consistency_check_job(name, job1, job2)) {
						// valid assignment, try to assign another
						// people
						if (backtracking(nextDomains, name_assignment, layer + 1))
							return true;
					}
					nextDomains.add(job1);
					nextDomains.add(job2);
					name_assignment.get(name).remove(job1);
					name_assignment.get(name).remove(job2);
				}
			}
		}

		// could not find assignment for current people
		// just return false
		return false;	
	}

	private int getValueNum(String name,HashSet<String> domains){
		int count = 0;
		for(String val:domains){
			if(check(name,val))
				count++;
		}
		return count;
	}
	// check whether current partial assignment is valid
	private boolean consistency_check_job(String name, String job1, String job2) {
		// return true;
		// nurse could only belongs to: Steve, and Pete
		// chef only belongs to: Roberta, Thelma
		// boxer can not belongs to: roberta
		// nurse/teacher/police officer could not belongs to: pete
		// chef can not belongs to: Roberta
		// actor only belongs to: Roberta, Thelma
		// cleck is male, thus, cleck can only belongs to: Steve, and Pete
		
		// each people has two jobs
		// jobs are chef, guard, nurse, clerk, police officer (gender not
		// implied), teacher, actor, and boxer
		if((job1=="chef" && job2=="police officer") || (job2=="chef" && job1=="police officer") )
			return false;
		if(!check(name,job1) || !check(name,job2))
			return false;
		else
			return true;
	}

	private boolean check(String name, String job) {
		switch (job) {
		case "nurse":
			if (name != "Steve" && name != "Pete")
				return false;
			if (name == "Pete")
				return false;
			break;
		case "chef":
			if (name != "Roberta" && name != "Thelma")
				return false;
			if (name == "Roberta")
				return false;
			break;
		case "boxer":
			if (name == "Roberta")
				return false;
			break;
		case "clerk":
			if(name!="Steve" && name!="Pete")
				return false;
			break;
		case "police officer":
			if (name == "Pete" || name=="Roberta")
				return false;
			break;
		case "teacher":
			if (name == "Pete")
				return false;
			break;
		case "actor":
			if (name != "Roberta" && name != "Thelma")
				return false;
			break;
		case "guard":

			break;
		default:
			System.out.println("Wrong");
		}
		return true;
	}

}
