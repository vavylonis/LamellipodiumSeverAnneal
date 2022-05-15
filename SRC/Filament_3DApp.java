package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import main.Filament_3D;
import main.MHelp;
import main.SpeckleArrayList.Speckles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

/**
 * Manages filaments and simulation controls
 * @author Aaron Hall, Danielle Holz
 * Note coordinates are labeled differnt than the paper:  
 * 0 or z in from the LE to the center of the cell
 * 1 or y is the top and bottom 
 * 2 or x is the periodic boundary direction

 */

public class Filament_3DApp { 

	// determine if Angle order parameter should be saved and at what time -- DMH	
	public static boolean saveOrdPTime = false;
	public static int ordPSaveTime = 5;
	public static int ordPSaveStart = 50;
	
	//determine bounds for near leading edge and away from leading edge regions -- DMH
	double nearLE_zi = 0;
	double nearLE_zf = 1;
	double awayLE_zi = 3; //away LE bin
	double awayLE_zf = 4;
	
	static boolean oligIndex = false; //used in speckle indexing -- DMH
//	static boolean isOlig = false;  //true if olig, false if filament -- DMH
	
	//booleans to write headers to data files (true = need to write a header)
	public static boolean spec_header; 
	
	// ** global
	public static int nOfFil; // Number of filaments
	public static double time = 0; //time since simulation began
	public static NetworkList network_list = new NetworkList(); //creates, tracks NetworkMembers
	
	public static double saveTime = 10;  // save every x seconds -- DMH
	
	static ArrayList<Filament_3D> sFilament = new ArrayList<Filament_3D>(); //container of all filaments
	static ArrayList<Filament_3D> branchableFil; //list of only filaments that are capable of ever branching again
	static ArrayList<Filament_3D> currBranchableFil; //list of only filaments that are currently capable of branching (BE in branching zone) -- DMH
	static ArrayList<Filament_3D> oligomers; // list of filaments that become diffuse (less than critical length, no children)

	static ArrayList<Integer> parentList = new ArrayList<Integer>(); //list of all the parent id's

	// Used to save with buttons in the GUI verson, now it saves on completion of simulation -- DMH
	static ArrayList<String> concList = new ArrayList<String>(); // data for concentration plot -- DMH
	static ArrayList<String> EndsList = new ArrayList<String>(); // Ends data  -- DMH
	static ArrayList<String> BEPEList = new ArrayList<String>(); // Barbed and pointed ends -- DMH
	static ArrayList<String> branchFilList = new ArrayList<String>(); // branch and filament number -- DMH
	static ArrayList<String> LengthList = new ArrayList<String>(); // filament length data -- DMH
	static ArrayList<String> branchBarbList = new ArrayList<String>(); // branch and barbed end data  -- DMH
	static ArrayList<String> concOrigList = new ArrayList<String>(); // data for concentration of the origin plot -- DMH
	
	static List<Filament_3D> toRemoveOligomer = new ArrayList<Filament_3D>(); //list of oligomers that need to be removed

	// ** control vars
	static double denovoSize;//default filament monomer size
	static double l_crit;//filaments less than this in monomers can become diffuse, maximum oligomer size
	static double steptime,fullDuration; //time between doStep in sim time, time at which sim auto ends
	static double p_nucleation; // desired nucleation density at nucleation site
	static double nucleationArea; //area of nucleation site
	static double depolyBE; //will the barbed end depolymerize after uncapping?  0 no BE depolymerization, 1 BE depolymerization after uncapping, 2 uncapping and polymerization/depolymerization -- DMH
	static double severType; //how will severing occur?  0 sever at any monomer, 9 sever with uniform and end severin, all other types are commented out -- DMH
	static double z_axis_max; // tracks the back of the lamellipodium 
	static double p_destab; // probability of BE polymerizing or depolymerizing after uncapping, need depolyBE == 2
	
	//comments in Filament_3D
	static double k_polyRate; // BE polymerization rate
	static double p_spec; //probality of a monomer becomes a speckle -- DMH
	static double k_depolyRate; //PE depolymerization rate
	static double k_depolyBERate; //BE depolymerization rate -- DMH
	static double k_capRate; //initial capping rate
	static double k_uncapRate; // uncapping rate 
	static double k_branchRate; // branching rate
	static double k_debranchRate; // debranching rate
	static double k_severRate; // severing rate
	static double k_severRate_u; // uniform severing rate, only necessary when using two severing rates 
	static double k_severAge; //not used in paper
	static double k_oligomerAnneal; // oligomer annealing rate
	static double severCap; // do newly created BE from severing cap or polymerize? -- I updated this but it should be checked!!! if they actually polymerize
	static double z_branch; // branching region in z (from LE into the cell)
	static double dt; // time step
	static double retFlow;  //constant retrograde flow
	static double nucSite_x; // how wide the lamellipodium is, PBC direction 
	static double nucSite_y; //height of the lamellipodium, (typically 0.2 um)
	static double l_mon; // length addition of an actin monomer increases the filament length
	static double fil_diffusion; // diffusion coefficient 
	static double filament_kink; // should filaments stop polymerizing or polymerize along it when it hits a y boundary
	static double k_polyUncap; //polymerization rate after uncapping
	
	static double outPlaneAng; //maximum out of plane branching variation
	
	//** data writing
	static boolean data_out = false; //write/update a data file each doStep if true
	static DataWriter data_out_writer;// used for writing text files
			
//	public static ArrayList<double[]> debugArray = new ArrayList<double[]>();
/**
* Constructor 
*/
public Filament_3DApp() {
		
	initialize();
	while (Filament_3DApp.time < Filament_3DApp.fullDuration) { //step until fullDuration is met
		doStep();
	}	
	save_state(); //save lamellipodium state
	save_data();
}
/**
* Main class to run simulation -- DMH
*/
/*never called?
public void run() {
	initialize();
	double endLoop = Filament_3DApp.fullDuration/Filament_3DApp.dt;
	int nsteps = 0;
	while (Filament_3DApp.time < Filament_3DApp.fullDuration) { // stop after full duration 
		doStep();
		nsteps ++;
		if(endLoop > (nsteps + 10)) break; // make sure the loop stops, just in case
	}	
	save_state(); //save lamellipodium state
	save_data();
}
*/
/**
* Run when initializing new simulation
* read in parameters
*/	
public void initialize() {	
		
	//set values 
	nOfFil = 0;
	branchableFil = new ArrayList<Filament_3D>();
	currBranchableFil = new ArrayList<Filament_3D>(); // DMH
	oligomers = new ArrayList<Filament_3D>();
	sFilament.clear();
	network_list.clear();
	time = 0;
	SpeckleArrayList.spec_list.clear();
	
	//get parameters from text file
	importParam.import_params("./filParams.txt","./filParams.txt");
	
	//** assign local variables from file
	fullDuration = importParam.fullDuration;
	p_nucleation = importParam.p_nucleation;
	nucSite_x = importParam.nucSite_x;
	nucSite_y = importParam.nucSite_y;
	k_polyRate = importParam.k_polyRate;
	p_spec = importParam.p_spec;
	k_depolyRate = importParam.k_depolyRate;
	k_depolyBERate = importParam.k_depolyBERate;
	k_capRate = importParam.k_capRate;
	k_uncapRate = importParam.k_uncapRate;
	k_branchRate = importParam.k_branchRate;
	k_debranchRate = importParam.k_debranchRate;
	k_severRate = importParam.k_severRate;
	k_severRate_u = importParam.k_severRate_u;
	severCap = importParam.severCap;
	k_oligomerAnneal = importParam.k_oligomerAnneal;
	z_branch = importParam.z_branch;
	retFlow = importParam.retFlow;
	l_mon = importParam.l_mon;
	denovoSize = importParam.denovoSize;
	l_crit = importParam.l_crit;
	fil_diffusion = importParam.fil_diffusion;
	dt = importParam.dt;
	steptime = importParam.steptime;
	outPlaneAng = importParam.outPlaneAng;
	filament_kink = importParam.filament_kink;
	depolyBE = importParam.depolyBE;
	severType = importParam.severType;
	p_destab = importParam.p_destab; 
				
	k_oligomerAnneal = k_oligomerAnneal/(602.2*nucSite_y); //annealing rate is input in /uM/s/s.u. but simulation uses um*um/s.u./s units --DMH
		
	//create first filaments
	nucleationArea = nucSite_x * nucSite_y;
	int initialNoFil = (int) (Math.round(nucleationArea * p_nucleation));
	for (int i = 0; i < initialNoFil; i++) {
		createDeNovo(); //create filaments at any orientation 
	}
		
	spec_header = true;
}

/**
* Output loop
* Steps in time steptime, 
* although real time between steps is as fast as computation
*/
public void doStep() {
	time += steptime;
	
	System.out.println("Time: " + time);
	
	//clear these lists since the data has been written --DMH
	if (MHelp.round(time,1)%0.5 == 0 && p_spec > 0.0 ) {
		SpeckleArrayList.Speckles.save_speckles();
	}
		
	if(sFilament.size()==0){
		System.out.println("Simulation Stopped: No more filaments");
	}
	for (int dv = 0; dv < (steptime < dt ? 1 : steptime) / dt; dv++)
		doStep2();
		
	if(time>=Filament_3DApp.fullDuration-50.0 && (10*MHelp.round(time,1))%2==0) save_state(); //save lamellipodium state 
	
	if(saveOrdPTime && time>ordPSaveStart && (MHelp.round(time,1)%ordPSaveTime==0)) { //caluclate the average every second -- DMH
		//Calculates the average order parameter between a 2-filament branched state (+- 35 degree pattern) and a 3-filament branch state (0/+-70 degree patter)
		double OrdPTime1um = OrdParam_Time(0, 1, 1); // Angle order parameter within the first micrometer
		double OrdPTime1umC = OrdParam_Time(0,1,2); //use different bins 
		double OrdPTime1umCBack = OrdParam_Time(0,1,3); //consider backward branching
		String AngleOrdP = "AngleOrdP";
		writeTimeData(OrdPTime1um, OrdPTime1umC, OrdPTime1umCBack, AngleOrdP);
		if (MHelp.round(time,1)/(ordPSaveTime+ordPSaveStart)==1 || MHelp.round(time,1)%(ordPSaveTime*20)==0) writeAngleData(0, z_branch);
	}
}
/**
 * Computational loop
 * Steps in time dt
 */
public void doStep2() {
			
	// ** removing filaments
	List<Integer> toRemove = new ArrayList<Integer>();
	double monomer_size_total = 0.0;
	
	//run oligomer steps
	// check if oligomers should be removed because they are to small or large otherwise update them
	if(k_oligomerAnneal > 0.0){
		int index_count = -1; //for a speckle check --DMH
		
		for(Filament_3D oligomer : oligomers){
			index_count = index_count + 1;
			//oligomer.step();
			if (oligomer.network_member.size < 2) { // oligomers smaller than 2 monomers should be removed
				toRemoveOligomer.add(oligomer);
			} else if (oligomer.network_member.size > l_crit){ // if its larger than the critical length, make it a filament again -- DMH
				toRemoveOligomer.add(oligomer);
				sFilament.add(oligomer); // position remains the same from when it became an oligomer
				oligomer.oligomer_time = 0.0;
				oligomer.is_olig = false;
			} else if((oligomer.fil_age- oligomer.oligomer_time) > 20.00 ){ // remove if the oligomer has been around to long
				toRemoveOligomer.add(oligomer); //NOTE speckles are not updated here
			}else{
				count_speckles_o(oligomer);
				oligomer.step(); // calls Filament_3D to update 
			}
		}
		//remove depolymerized or annealed oligomers
		for(Filament_3D oligomer : toRemoveOligomer){
			oligomers.remove(oligomer);
		}
	}
	
	//run filament steps
	// check if filaments are still filaments otherwise updates them 
	for (int j = 0; j < sFilament.size(); j++) {
		int index_count = -1; //for a speckle check --DMH
		index_count = index_count + 1;
		Filament_3D thisFil = sFilament.get(j);
		//become diffuse oligomer if too small
		if(k_oligomerAnneal > 0.0 && thisFil.oligomer_transport && // is an oligomer
			thisFil.network_member.size <= l_crit && 
			thisFil.network_member.size > 1 && 
			thisFil.network_member.children.size()<1 &&
			thisFil.network_member.get_parent_id() == 0){
			thisFil.is_olig = true;
			count_speckles(j);
			oligomers.add(thisFil);
			thisFil.oligomer_start();
			toRemove.add(j);	
			}
		monomer_size_total += thisFil.network_member.size;
		if(sFilament.size()==0)continue;
		//remove if fully depolymerized 
		if (thisFil.network_member.size < 1 || (thisFil.network_member.size < 2 && thisFil.network_member.get_parent_id() == 0)) {
			toRemove.add(j);
			} else { // update filament
			thisFil.step(); // calls Filament_3D to update
			thisFil.position[2] += (retFlow * thisFil.dt);//retrograde flow
			double z_loc = thisFil.position[2];
			double z_bloc = thisFil.get_barbed_loc()[2];
			//keep running track of furthest a filament has traveled in z 
			if (z_loc > z_axis_max || z_bloc > z_axis_max) {
				z_axis_max = z_loc > z_bloc ? z_loc : z_bloc;
				}
			}
			
	}
	
	/**
	 * sFilament clean-up
	 * toRemove is list of indexes
	 * its sorted in reverse to avoid exceptions
	 */
	Collections.sort(toRemove);
	Collections.reverse(toRemove);
	for (int k = 0; k < toRemove.size(); k++) {
		int n = toRemove.get(k);
		branchableFil.remove(sFilament.get(n));
		sFilament.remove(n);

	}
		

	/**
	 * Zero-order Branching
	 */
	
	List<Filament_3D> toRemoveBranchable = new ArrayList<Filament_3D>();
	List<Integer> toRemoveFilament = new ArrayList<Integer>();		
	if (MHelp.rand.nextDouble() <= k_branchRate * dt) {
		List<Filament_3D> branchable = new ArrayList<Filament_3D>();
		for (int i = 0; i < branchableFil.size(); i++) {
			if(branchableFil.size()==0) continue;//nothing can branch
			Filament_3D this_fil = branchableFil.get(i);
			int branchSites = this_fil.count_branch_sites();
			//find filaments that can branch
			if (branchSites > 0) {
				branchable.add(this_fil);//can branch
			}else{
				// check if this can possibly ever branch again
				double[] bloc = this_fil.get_barbed_loc();
				if (this_fil.filamentlength > 0.0) {
					if (bloc[2] <= z_branch || this_fil.position[2] <= z_branch) continue;
					if (this_fil.direction[2] < 0 && (this_fil.barbedCapped == 0 || this_fil.barbedCapped == 2)) {
						//can branch
						continue;
					}
				}else{
					//this should probably never happen, as they are removed in sFilament clean-up
					toRemoveFilament.add(sFilament.indexOf(this_fil));
				}
				toRemoveBranchable.add(this_fil);
				}
		}
		//toRemoveBranchable is list of objects, any loop is fine
		for (int k = 0; k < toRemoveBranchable.size(); k++) {
			branchableFil.remove(toRemoveBranchable.get(k));
		}
		//toRemoveFilament list of indexes, order reversed to avoid exceptions
		Collections.sort(toRemoveFilament);
		Collections.reverse(toRemoveFilament);
		for (int k = 0; k < toRemoveFilament.size(); k++) {
			int n = toRemoveFilament.get(k);
			branchableFil.remove(sFilament.get(n));
			sFilament.remove(n);
		}
		toRemoveFilament.clear();
		toRemoveBranchable.clear();
		
		currBranchableFil.clear();
		int totBranchMono = 0; 
		//select filaments out of the branchable list that can branch this step -- DMH
		for(int k = 0; k < branchableFil.size(); k++) { 
			Filament_3D can_branch_fil = branchableFil.get(k);
			if (can_branch_fil.count_branch_sites() < 1) continue; 
			double[] bloc = can_branch_fil.get_barbed_loc();	
			if (bloc[2] <= z_branch) {	
				currBranchableFil.add(can_branch_fil);
				totBranchMono = totBranchMono + can_branch_fil.count_branch_sites();  //sum the number of branchable sites
				}
		}
		
		if (currBranchableFil.size() > 0) {// DMH
			//branching weighted by monomers that can branch
			boolean did_branch = false;
			int break_inf_loop = 0;
			 while(!did_branch || break_inf_loop>1000){
				int n = MHelp.rand.nextInt(totBranchMono); // choose random monomer
				int currSum = 0;
				for(int i=0; i<currBranchableFil.size(); i++){
					Filament_3D can_branch_fil = currBranchableFil.get(i); 
					
					int branch_sites = can_branch_fil.count_branch_sites();
					currSum += branch_sites; //sum of branch sites -- DMH 
					if(currSum >= n ){	//when total sum is greater than or equal to the random number we know which filament the monomer is on 
						int m = can_branch_fil.network_member.size - (currSum - n); // calculate the monomer that should branch // --DMH
						did_branch = currBranchableFil.get(i).branch(m); //--DMH						
						break; 
					}
				}
					break_inf_loop++;
		}
		} else {
			// nothing is branchable
		}
		branchable.clear();
	}
	}
	
	
public static Filament_3D initFilament(double[] loc, double[] dir, int parent, int parent_mono) {
	return initFilament(loc, dir, parent, parent_mono, (int)(denovoSize));
}
/**
* Creates a new filament
* @param loc (double[]) where filament is initialized
* @param dir (double[]) normalized direction vector
* @param parent (int) id of parent filament or -1
* @param parent_mono (int) monomer root on parent filament or -1
* @param numMono (int) monomeric length
*/
public static Filament_3D initFilament(double[] loc, double[] dir, int parent, int parent_mono, int numMono) {
	NetworkMember network_member = network_list.add_member(parent,parent_mono);
	network_member.change_size(numMono);
	return initFilament(loc, dir, network_member);
}
/**
 * Creates a new filament
 * @param loc (double[]) where filament is initialized
 * @param dir (double[]) normalized direction vector
 * @param network_member (NetworkMember) handles network relationships
 * @return
 */
public static Filament_3D initFilament(double[] loc, double[] dir, NetworkMember network_member){
	Filament_3D new_fil = new Filament_3D();
	sFilament.add(new_fil);
	double[] initialpos = loc;
	double[] initialdir = dir;
	int n = sFilament.size() - 1;
	nOfFil += 1;
	new_fil.start(initialpos, initialdir, network_member);
	branchableFil.add(new_fil);
	return sFilament.get(n);
}
/**
* Creates a new filament with random orientation at the leading edge of the lamellipodium
*/
public void createDeNovo() {
	//rand pos on nucleation zone
	double[] initialpos = new double[] { MHelp.rand.nextDouble() * (nucSite_x - 2 * l_mon * denovoSize) + l_mon * denovoSize,
			MHelp.rand.nextDouble() * (nucSite_y - 2 * l_mon * denovoSize) + l_mon * denovoSize, 0.0 };
	//rand dir
	double[] initialdir = new double[] { 2 * MHelp.rand.nextDouble() - 1, 2 * MHelp.rand.nextDouble() - 1,
			2 * MHelp.rand.nextDouble() - 1 };
	if(initialdir[2]>0) initialdir[2] = -initialdir[2];
	initialdir = MHelp.vecNorm(initialdir);
	//make sure filament isn't intersecting leading edge
	if(initialdir[2]<0) initialpos = MHelp.vecDiff(initialpos, MHelp.vecProd(initialdir, l_mon * denovoSize));
	Filament_3D thisFil = initFilament(initialpos, initialdir, -1, -1);
	thisFil.turn_off_oligomer_transport();
	thisFil.DeNovo = true;
}

/**
 * Loops sFilament for filament with id n, and returns it
 * @param n (int)
 * @return Filament_3D filament with id n or null
 */
public static Filament_3D getFilament(int n){
	for(int i=0;i<sFilament.size();i++){
		if(sFilament.get(i).network_member.id == n) return sFilament.get(i);
	}
	return null;
	}

/** Count how many speckles are on a filament that will become an oligomer
 * DMH
*/

public static void count_speckles(int index) {
	Filament_3D thisFil = sFilament.get(index);
	int speckCount = 0;
	for(int j=0; j<SpeckleArrayList.spec_list.size(); j++){ // loop through all the speckles
		SpeckleArrayList.spec_constructor curr_spec = SpeckleArrayList.spec_list.get(j);
		SpeckleArrayList severed_speck = new SpeckleArrayList();
		//if(thisFil.network_member.id == curr_spec.spec_fil_id){ // when the speckle filament id matches the network id, the speckle is on the current filament
		if(thisFil.network_member == curr_spec.network_member) {
			speckCount = speckCount + 1; // count speckles that will disappear
			//eliminate the speckles 
			severed_speck.spec_severed(j,thisFil.network_member.id, curr_spec.mono_from_PE, thisFil.is_olig, thisFil.position, thisFil.network_member);
		}
	}
	thisFil.nSpeckOlig = speckCount;
	
	// remove speckles that disappeared 
	Collections.sort(SpeckleArrayList.remove_spec);
	Collections.reverse(SpeckleArrayList.remove_spec);
	for(int k = 0; k < SpeckleArrayList.remove_spec.size(); k++){
		int remove = SpeckleArrayList.remove_spec.get(k);
		SpeckleArrayList.spec_list.remove(remove);
	} 					

	SpeckleArrayList.remove_spec.clear();
}

/** Count how many speckles are on an oligomer
 * DMH
*/

//public static void count_speckles_o(int index) {
public static void count_speckles_o(Filament_3D thisOlig) {
	//Filament_3D thisOlig = oligomers.get(index);
	int speckCount = 0;
	for(int j=0; j<SpeckleArrayList.spec_list.size(); j++){ // loop through all the speckles
		SpeckleArrayList.spec_constructor curr_spec = SpeckleArrayList.spec_list.get(j);
		SpeckleArrayList severed_speck = new SpeckleArrayList();
		//if(thisOlig.network_member.id == curr_spec.spec_fil_id){ // when the speckle filament id matches the network id, the speckle is on the current filament
		if(thisOlig.network_member == curr_spec.network_member) {
			speckCount = speckCount + 1;
			severed_speck.spec_severed(j,thisOlig.network_member.id, curr_spec.mono_from_PE, thisOlig.is_olig, thisOlig.position, thisOlig.network_member);
		}
	}
	thisOlig.nSpeckOlig += speckCount;
	
	// remove speckles that disappeared 
	Collections.sort(SpeckleArrayList.remove_spec);
	Collections.reverse(SpeckleArrayList.remove_spec);
	for(int k = 0; k < SpeckleArrayList.remove_spec.size(); k++){
		int remove = SpeckleArrayList.remove_spec.get(k);
		SpeckleArrayList.spec_list.remove(remove);
	} 					

	SpeckleArrayList.remove_spec.clear();
}

/** Calculate the angle order parameter (2 or 3 filament state) at the current time and then write it to file with writeTimeData -- DMH
 * @return OrdP_Avg (double) 
*/	
public static double OrdParam_Time(double min_z, double max_z, int bins){
	double deg0Cnt = 0;
	double deg70Cnt = 0;
	double deg35Cnt = 0;
	double deg0CntC = 0;
	double deg70CntC = 0;
	double deg35CntC = 0;
		for (int j = 0; j < sFilament.size(); j++) {
			if (sFilament.get(j).filamentlength < 0.0) continue;
			double thisFil_zi = sFilament.get(j).position[2];
			double thisFil_zf = sFilament.get(j).get_barbed_loc()[2];
			if((thisFil_zi >= min_z && thisFil_zi < max_z)||(thisFil_zf >= min_z && thisFil_zf < max_z)||(thisFil_zi <= min_z && thisFil_zf > max_z)||(thisFil_zi <= max_z && thisFil_zf > min_z)){
				double[] projVec = sFilament.get(j).direction.clone();
				projVec[1] = 0.0;
				double sign = projVec[0]<0?-1:1;
				double cos_rad = MHelp.vecProd(MHelp.vecNorm(projVec), new double[] { 0, 0, -1 });
				double deg = Math.toDegrees(Math.acos(cos_rad))*sign;					
			
				
				if (bins == 1){
					if(Math.abs(deg) >= 0.0 && Math.abs(deg) <= 20.0){ //  
						deg0Cnt ++;
					} else if (Math.abs(deg) >= 30.0 && Math.abs(deg) <= 50.0){
						deg35Cnt ++;
					} else if (Math.abs(deg) >= 60.0 && Math.abs(deg) <= 80.0){	
						deg70Cnt ++;
					}
				}else if (bins ==2) {
					if(Math.abs(deg) >= 0.0 && Math.abs(deg) <= 20.0){ // bins are centered 
						deg0CntC ++;
					} else if (Math.abs(deg) >= 25.0 && Math.abs(deg) <= 45.0){
						deg35CntC ++;
					} else if (Math.abs(deg) >= 60.0 && Math.abs(deg) <= 80.0){	
						deg70CntC ++;
					}
				}else if (bins ==3) {
					if(Math.abs(deg) >= 0.0 && Math.abs(deg) <= 20.0){ // account for backward branching 
						deg0CntC ++;
					} else if (Math.abs(deg) >= 25.0 && Math.abs(deg) <= 45.0){
						deg35CntC ++;
					} else if (Math.abs(deg) >= 95.0 && Math.abs(deg) <= 115.0){
						deg35CntC ++;
					} else if (Math.abs(deg) >= 60.0 && Math.abs(deg) <= 80.0){	
						deg70CntC ++;
					} else if (Math.abs(deg) >= 130.0 && Math.abs(deg) <= 150.0){	
						deg70CntC ++;
					}
				}
		}
	}
	double OrdP_Time = (deg0Cnt + deg70Cnt - deg35Cnt)/(deg0Cnt + deg70Cnt + deg35Cnt);
	if (bins == 2 || bins == 3) OrdP_Time = (((deg0CntC + deg70CntC)/2) - deg35CntC)/(((deg0CntC + deg70CntC)/2) + deg35CntC);
	return OrdP_Time;	

}
	
public void writeAngleData(double min_z, double max_z){
	String timeS = String.valueOf(MHelp.round(time,1));
	String fname = ("AnglePerpLE" + "_"+"vr_"+retFlow+ "Poly_" + k_polyRate + "BrDist_" + z_branch +  "Restriction_" + outPlaneAng + "time" + timeS + ".csv");
		
	for (int j = 0; j < sFilament.size(); j++) {
		if (sFilament.get(j).filamentlength < 0.0) continue;
		double thisFil_zi = sFilament.get(j).position[2];
		double thisFil_zf = sFilament.get(j).get_barbed_loc()[2];
		if((thisFil_zi >= min_z && thisFil_zi < max_z)||(thisFil_zf >= min_z && thisFil_zf < max_z)||(thisFil_zi <= min_z && thisFil_zf > max_z)||(thisFil_zi <= max_z && thisFil_zf > min_z)){
			double[] projVec = sFilament.get(j).direction.clone();
			projVec[1] = 0.0;
			double sign = projVec[0]<0?-1:1;
			double cos_rad = MHelp.vecProd(MHelp.vecNorm(projVec), new double[] { 0, 0, -1 });
			double deg = Math.toDegrees(Math.acos(cos_rad))*sign;	
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true));
				bw.write(deg+ "," + sFilament.get(j).count_branch_sites() + ",");
				bw.write("\n");
				bw.close();	
			}catch(Exception ex){}
		}				
	}
}
//Write angle order parameter data every time this is called -- DMH
public void writeTimeData(double Data, double OrdPTimeBin1, double OrdPTimeBin2, String name){
	String timeS = String.valueOf(MHelp.round(time,1));
	String fname = (name + "_"+"vr_"+retFlow+ "Poly_" + k_polyRate + "BrDist_" + z_branch + ".csv");
	try {		
		BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true));
		bw.write(timeS+ ",");
		bw.write(Data+",");
		bw.write(OrdPTimeBin1+",");
		bw.write(OrdPTimeBin2+",");
		bw.write("\n");
			
		bw.close();		
	}catch(Exception ex){}
}
/**
 * Writes state data of all filaments to .csv file
 */
public void save_state(){
	DataWriter writer = new DataWriter();
	//	write_params_and_headers(writer);
	writer.write_data("@header");
	writer.write_data("time,n,k,k mono,l size,x,y,z,xdir,ydir,zdir,capped,age,dendral,DeNovo,Annealed");
	writer.write_data("@filaments");
	write_filaments(writer);
	writer.close();
}
/**
 * write graph data to .csv files
 */
public void save_data() {
	save_concentration();
	save_branchBarb();
	save_branchFil();
	save_pointBarb();
	save_ends();
	save_length();
	save_origin_concentration();
}

public void save_concentration() {
	double fil_conc_total = 0; // total filament concentration -- DMH
	double olig_conc_total = 0; // total oligomer concentration -- DMH
	double z_delta = 0.25; //bin size 
	for (int k = 0; k < z_axis_max / z_delta; k++) {
		double mono_count = 0.0;
		double mono_count_olig = 0.0;
		double zf = z_delta + z_delta * k;
		double zi = z_delta * k;
		for (int l = 0; l < sFilament.size(); l++) {
			Filament_3D thisFil = sFilament.get(l);
			mono_count += thisFil.count_mono(zi, zf);
			}	
		for (int m = 0; m < oligomers.size(); m++) {
			Filament_3D thisOlig = oligomers.get(m);
			mono_count_olig += thisOlig.count_mono(zi, zf);						
		}
		double fil_conc = mono_count / (nucleationArea * z_delta * MHelp.avaNum);
		double olig_conc = mono_count_olig / (nucleationArea * z_delta * MHelp.avaNum);
		fil_conc_total = fil_conc_total + fil_conc;
		olig_conc_total = olig_conc_total + olig_conc;
			concList.add("1,"+zf+","+fil_conc);
			concList.add("2,"+zf+","+olig_conc);
	}
	
	String fname = ("Concentration.csv");
	
	try {		
		BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true));	
		bw.write("set," + "z," + "conc" + "\n");
		for(int i=0;i<Filament_3DApp.concList.size(); i++){	
			bw.write(Filament_3DApp.concList.get(i)+"\n");
		}
		bw.close();		
		}catch(Exception ex){}
	
}


public void save_origin_concentration() {
	double z_delta = 0.25; //bin size 
	double total_poly = 0; 
	double total_olig = 0;
	for (int k = 0; k < z_axis_max / z_delta; k++) {
		double mono_count_poly = 0.0; // monomers from polymerization --DMH
		double mono_count_olig = 0.0; // monomers from oligomer annealing -- DMH
		double zf = z_delta + z_delta * k;
		double zi = z_delta * k;
		for(int j=0; j<SpeckleArrayList.spec_list.size(); j++){ // loop through all the speckles
			SpeckleArrayList.spec_constructor curr_spec = SpeckleArrayList.spec_list.get(j);
			//SpeckleArrayList spec_z_loc = new SpeckleArrayList();
			//System.out.println(sFilament.size() + " " + curr_spec.spec_id + " " + j);
			//double speck_z = SpeckleArrayList.spec_z_loc(curr_spec.spec_id);
			
			double dist_from_init = retFlow*(time - curr_spec.spec_init_time); // in um 
			
			double speck_z = dist_from_init + curr_spec.spec_position_z; // in um 
			//double speck_z = curr_spec.spec_position_z; //initial position of the speckle
			
			if(speck_z >= zi && speck_z < zf) {
				if(curr_spec.origin == 0){ // origin is from polymerziation 
					mono_count_poly = mono_count_poly + 1;
				}else if (curr_spec.origin == 1){ // origin is from oligomer annealing
					mono_count_olig = mono_count_olig + 1;
				}
				//System.out.println(mono_count_poly + " " + mono_count_olig + " " + curr_spec.origin + " " + speck_z + " " + zi + " " + curr_spec.spec_id);
			total_poly = mono_count_poly + total_poly;
			total_olig = mono_count_olig + total_olig;
			}
		}
		double fil_conc = mono_count_poly / (nucleationArea * z_delta * MHelp.avaNum);
		double olig_conc = mono_count_olig / (nucleationArea * z_delta * MHelp.avaNum);
		concOrigList.add("1,"+zf+","+fil_conc);
		concOrigList.add("2,"+zf+","+olig_conc);
		
				
	}
	
	String fname = ("Concentration_origin_" + MHelp.round(Filament_3DApp.time,1) + ".csv");
	
	try {		
		BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true));	
		bw.write("set," + "z," + "conc" + "\n");
		for(int i=0;i<Filament_3DApp.concOrigList.size(); i++){	
			bw.write(Filament_3DApp.concOrigList.get(i)+"\n");
		}
		bw.close();		
		}catch(Exception ex){}
	
	concOrigList.clear();
	
}


/**
 * counts values of ends
 */
public void save_ends() {
	double z_delta = 0.25;
	for(int i=0;i<z_axis_max/z_delta;i++){
		double barbedCnt = 0;
		double branchCnt = 0;
		double barbedUC = 0; //Only count the number uncapped BE -- DMH
		double pointedUC = 0; //Only count the number uncapped PE -- DMH
		double BE_UC_olig = 0; //uncapped BE on oligomers -- DMH
		double PE_UC_olig = 0; //uncapped PE on oligomers -- DMH
		double BE_olig = 0; //BE on oligomers -- DMH
		double PE_olig = 0; //branched PE of oligomers -- DMH - should be zero based on how oligomers are defined
		double BE_poly = 0; //repolymerizing BE --DMH
		double BE_depoly = 0; //depolymerizing BE --DMH
		double BE_NC = 0; //BE never capped, polymerizing --DMH
		double BE_CUC = 0; //BE capped then uncapped --DMH
		double zi = i*z_delta;
		double zf = i*z_delta + z_delta;
		for (int j = 0; j < sFilament.size(); j++) {
			Filament_3D thisFil = sFilament.get(j);
			if(thisFil.filamentlength<=0.0) continue;
			if(thisFil.position[2] >= zi && thisFil.position[2] < zf){
				if(!thisFil.network_member.root) branchCnt += 1;
				if(thisFil.network_member.root) pointedUC += 1;
			}
			double[] barbed_loc = thisFil.get_barbed_loc();
			if((barbed_loc[2] >= zi && barbed_loc[2] < zf)) {//&&
					//(barbed_loc[0] > 0.0 && barbed_loc[0] < nucSite_x)){
				barbedCnt += 1;
				if ((barbed_loc[2] < thisFil.position[2]) && (thisFil.barbedCapped == 0 || thisFil.barbedCapped == 2)){
					barbedUC += 1;
				}else if ((barbed_loc[2] < thisFil.position[2]) && (thisFil.barbedCapped == 3)){
					BE_poly += 1;
				}else if ((barbed_loc[2] < thisFil.position[2]) && (thisFil.barbedCapped == 4)){
					BE_depoly += 1;
				}else if ((barbed_loc[2] < thisFil.position[2]) && (thisFil.barbedCapped == 0)){
					BE_NC += 1;
				}else if ((barbed_loc[2] < thisFil.position[2]) && (thisFil.barbedCapped == 2)){
					BE_CUC += 1;
				}
				
			}
		}
		for (int j = 0; j < oligomers.size(); j++) {
			Filament_3D thisOlig = oligomers.get(j);
			if(thisOlig.filamentlength<=0.0) continue;
			if(thisOlig.position[2] >= zi && thisOlig.position[2] < zf){
				if(!thisOlig.network_member.root) PE_olig += 1;
				if(thisOlig.network_member.root) PE_UC_olig += 1;
			}
			double[] barbed_loc = thisOlig.get_barbed_loc();
			if((barbed_loc[2] >= zi && barbed_loc[2] < zf)) {
				BE_olig += 1;
				if ((barbed_loc[2] < thisOlig.position[2]) && (thisOlig.barbedCapped == 0 || thisOlig.barbedCapped == 2)){
					BE_UC_olig += 1;
				}
			}
		}
		EndsList.add("1,"+zf+","+branchCnt);
		EndsList.add("2,"+zf+","+barbedCnt);
		EndsList.add("3,"+zf+","+barbedUC);
		EndsList.add("4,"+zf+","+pointedUC);
		EndsList.add("5,"+zf+","+BE_UC_olig);
		EndsList.add("6,"+zf+","+PE_UC_olig);
		EndsList.add("7,"+zf+","+BE_olig);
		EndsList.add("8,"+zf+","+PE_olig);
		EndsList.add("9,"+zf+","+BE_poly);
		EndsList.add("10,"+zf+","+BE_depoly);
		EndsList.add("11,"+zf+","+BE_NC);
		EndsList.add("12,"+zf+","+BE_CUC);

	}
	String fname = ("Ends.csv");
	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true));
		bw.write("set," + "z," + "num" + "\n");
		for(int i=0;i<Filament_3DApp.EndsList.size(); i++){	
			bw.write(Filament_3DApp.EndsList.get(i)+"\n");
		}
		bw.close();		
		}catch(Exception ex){}
}

/**plots count of branch junctions to filaments in intervals on z. 
 * filaments counted if they intersect the middle of the interval
 */
public void save_branchBarb() {
	double z_delta = 0.25;
	for (int k = 0; k < z_axis_max / z_delta; k++) {
		double branchCnt = 0;
		double barbedCnt = 0;
		double barbedUC = 0; //Only count the number uncapped BE -- DMH
		double zf = z_delta + z_delta * k;
		double zi = z_delta * k;	
		for (int j = 0; j < sFilament.size(); j++) {
			Filament_3D thisFil = sFilament.get(j);
			if(thisFil.filamentlength<=0.0) continue;
			if(thisFil.position[2] >= zi && thisFil.position[2] < zf){
				if(!thisFil.network_member.root) branchCnt += 1;
			}
			double[] barbed_loc = thisFil.get_barbed_loc();
			if((barbed_loc[2] >= zi && barbed_loc[2] < zf)) {
				barbedCnt += 1;
				if ((barbed_loc[2] < thisFil.position[2]) && (thisFil.barbedCapped == 0 || thisFil.barbedCapped == 2)){
					barbedUC += 1;
				}
			}
		}
		branchBarbList.add("1,"+zf+","+branchCnt);
		branchBarbList.add("2,"+zf+","+barbedCnt);
		branchBarbList.add("3,"+zf+","+barbedUC);
	}
	
	String fname = ("BrBE.csv");

	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true));
		bw.write("set," + "z," + "num" + "\n");
		for(int i=0;i<Filament_3DApp.branchBarbList.size(); i++){	
			bw.write(Filament_3DApp.branchBarbList.get(i)+"\n");
		}
		bw.close();		
	}catch(Exception ex){}

};

public void save_branchFil() {
	double z_delta = 0.25; 
	for (int k = 0; k < z_axis_max / z_delta; k++) {
		double zf = z_delta + z_delta * k;
		double zi = z_delta * k;
		double z_plane = (zf+zi)/2;
		double branchCnt = 0;
		int filCnt = 0;
		int FilFwdCnt = 0;
		for (int l = 0; l < sFilament.size(); l++) {
			Filament_3D thisFil = sFilament.get(l);
			double thisFil_zi = thisFil.position[2];
			double thisFil_zf = thisFil.get_barbed_loc()[2];
			if((thisFil_zi<z_plane && thisFil_zf>z_plane)||(thisFil_zi>z_plane && thisFil_zf<z_plane)){
				filCnt += 1;
				if (thisFil_zf < thisFil_zi){
					FilFwdCnt += 1;
				}
			}
			if(thisFil_zi >= zi && thisFil_zi < zf){
				if(!thisFil.network_member.root) branchCnt += 1;
			}
		}
		branchFilList.add("1,"+zf+","+branchCnt);
		branchFilList.add("2,"+zf+","+filCnt);
		branchFilList.add("3,"+zf+","+FilFwdCnt);
	}
	
	String fname = ("BrFil.csv");

	try {

		BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true));

		bw.write("set," + "z," + "num" + "\n");

		for(int i=0;i<Filament_3DApp.branchFilList.size(); i++){	

			bw.write(Filament_3DApp.branchFilList.get(i)+"\n");

		}
		bw.close();		
	}catch(Exception ex){}

}

/**
 * plots count of barbed ends, pointed ends and filaments over the z length -- DMH
 * filaments counted if they intersect the middle of the interval
 */
public void save_pointBarb() {
	double z_delta_m = 0.106; // in um, bin size in Mueller et al
	for (int k = 0; k < z_axis_max / z_delta_m; k++) {
		double zf = z_delta_m + z_delta_m * k;
		double zi = z_delta_m * k;
		double z_plane = (zf+zi)/2;
		double filCnt = 0;
		double BarbCnt = 0;
		double PointCnt = 0;
		for (int l = 0; l < sFilament.size(); l++) {
			Filament_3D thisFil = sFilament.get(l);
			double thisFil_zi = thisFil.position[2];
			double thisFil_zf = thisFil.get_barbed_loc()[2];
			if((thisFil_zi >= zi && thisFil_zi < zf)){
				PointCnt += 1;
			}
			if((thisFil_zf >= zi && thisFil_zf < zf)){
				BarbCnt += 1;
			}
			if((thisFil_zi<z_plane && thisFil_zf>z_plane)||(thisFil_zi>z_plane && thisFil_zf<z_plane)){
				filCnt += 1;
			}
		}
			
		BEPEList.add("1,"+zf+","+BarbCnt/(z_delta_m*nucSite_x));  //output in ends /um^2
		BEPEList.add("2,"+zf+","+PointCnt/(z_delta_m*nucSite_x));
		BEPEList.add("3,"+zf+","+filCnt);
	}

	String fname = ("BEPEFilN.csv");

	try {

		BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true));

		bw.write("set," + "z," + "num" + "\n");

		for(int i=0;i<Filament_3DApp.BEPEList.size(); i++){	

			bw.write(Filament_3DApp.BEPEList.get(i)+"\n");

		}
		bw.close();		
	}catch(Exception ex){}
		
};


/** 
 * finds the length of filaments in 0-1 um region and 3-4 um 
 */
public void save_length() {
	int count = 0;
	int countLE = 0;
	int countAw = 0;
	int countAw2 = 0; //2nd bin closer to LE for XTC parameters, hardcoded as 2-3 um from LE
	int awayLE_zi2 = 2;
	int awayLE_zf2 = 3;
	int j;
	Filament_3D thisFil; 
	for (int i = 0; i < (sFilament.size() + oligomers.size()); i++) {
		if (i<sFilament.size()){
			thisFil = sFilament.get(i);
		}else {
			j = i-sFilament.size();
			thisFil = oligomers.get(j);
		}
		double[] barbed_loc_ = thisFil.get_barbed_loc();
		double barb_loc = barbed_loc_[2];
		double point_loc = thisFil.position[2];
		if(thisFil.filamentlength<=0.0) continue;
		if(thisFil.network_member.parent != null){
			if(thisFil.position[2] >= 0 && thisFil.position[2] < 1){
				count ++;
				double mono_count = thisFil.count_mono(thisFil.position[2], barb_loc, 2); // count_mono counts the size of filament from point/branch point to barbed end
							}	
		}
			if((point_loc >= nearLE_zi && point_loc < nearLE_zf) && (barb_loc >= nearLE_zi && barb_loc < nearLE_zf)){ //Entire filament inside bin
			countLE ++;
			double mono_countLE = thisFil.count_mono(barb_loc, point_loc, 2); // count_mono counts the number of monomers contained in the bin
			LengthList.add("2,"+count+","+mono_countLE*l_mon*1000);
			} else if (point_loc >= nearLE_zi && point_loc < nearLE_zf){ // pointed end in bin
			countLE ++;
			double mono_countLE = thisFil.count_mono(thisFil.position[2], nearLE_zi, 2); 
			LengthList.add("2,"+count+","+mono_countLE*l_mon*1000);
		} else if (barb_loc >= nearLE_zi && barb_loc < nearLE_zf){ //barbed end is in the bin 
			countLE ++;
			double mono_countLE = thisFil.count_mono(nearLE_zf, barb_loc, 2); 
			LengthList.add("2,"+count+","+mono_countLE*l_mon*1000);
		} else if (barb_loc <= nearLE_zi && point_loc > nearLE_zf){ // filament extends outside the bin in both directions
			countLE ++;
			double mono_countLE = thisFil.count_mono(nearLE_zi, nearLE_zf, 2); 
			LengthList.add("2,"+count+","+mono_countLE*l_mon*1000);
		}
		
		if((point_loc >= awayLE_zi && point_loc < awayLE_zf) && (barb_loc >= awayLE_zi && barb_loc < awayLE_zf)){ //Entire filament inside bin
			countAw ++;
			double mono_countAw = thisFil.count_mono(barb_loc, point_loc, 2); // count_mono counts the number of monomers contained in the bin
			LengthList.add("3,"+count+","+mono_countAw*l_mon*1000);
		} else if (point_loc >= awayLE_zi && point_loc < awayLE_zf){ // pointed end in bin
			countAw ++;
			double mono_countAw = thisFil.count_mono(thisFil.position[2], awayLE_zi, 2); 
			LengthList.add("3,"+count+","+mono_countAw*l_mon*1000);
		} else if (barb_loc >= awayLE_zi && barb_loc < awayLE_zf){ //barbed end is in the bin 
			countAw ++;
			double mono_countAw = thisFil.count_mono(awayLE_zf, barb_loc, 2); 
			LengthList.add("3,"+count+","+mono_countAw*l_mon*1000);
		} else if (barb_loc <= awayLE_zi && point_loc > awayLE_zf){ // filament extends outside the bin in both directions
			countAw ++;
			double mono_countAw = thisFil.count_mono(awayLE_zi, awayLE_zf, 2); 
			LengthList.add("3,"+count+","+mono_countAw*l_mon*1000);
		}
		
		if((point_loc >= awayLE_zi2 && point_loc < awayLE_zf2) && (barb_loc >= awayLE_zi2 && barb_loc < awayLE_zf2)){ //Entire filament inside bin
			countAw ++;
			double mono_countAw = thisFil.count_mono(barb_loc, point_loc, 2); // count_mono counts the number of monomers contained in the bin
			LengthList.add("4,"+count+","+mono_countAw*l_mon*1000);
		} else if (point_loc >= awayLE_zi2 && point_loc < awayLE_zf2){ // pointed end in bin
			countAw ++;
			double mono_countAw = thisFil.count_mono(thisFil.position[2], awayLE_zi2, 2); 
			LengthList.add("4,"+count+","+mono_countAw*l_mon*1000);
		} else if (barb_loc >= awayLE_zi2 && barb_loc < awayLE_zf2){ //barbed end is in the bin 
			countAw ++;
			double mono_countAw = thisFil.count_mono(awayLE_zf2, barb_loc, 2); 
			LengthList.add("4,"+count+","+mono_countAw*l_mon*1000);
		} else if (barb_loc <= awayLE_zi2 && point_loc > awayLE_zf2){ // filament extends outside the bin in both directions
			countAw ++;
			double mono_countAw = thisFil.count_mono(awayLE_zi2, awayLE_zf2, 2); 
			LengthList.add("4,"+count+","+mono_countAw*l_mon*1000);
		}

		
	}

		String fname = ("Length_3-4.csv");
		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(fname, true));

			bw.write("set," + "int," + "length" + "\n");

			for(int i=0;i<Filament_3DApp.LengthList.size(); i++){	

				bw.write(Filament_3DApp.LengthList.get(i)+"\n");

			}
			bw.close();		
		}catch(Exception ex){}
	
}

/**
 * Write current state variables of all filaments
 */
public void write_filaments(DataWriter writer){
	for(int i=0;i<sFilament.size();i++){
		Filament_3D thisFil = sFilament.get(i);
		String thisline = String.valueOf(time)+",";
		thisline+=String.valueOf(thisFil.network_member.id)+",";
		thisline+=String.valueOf(thisFil.network_member.get_parent_id())+",";
		thisline+=String.valueOf(thisFil.network_member.location)+",";
		thisline+=String.valueOf(thisFil.network_member.size)+",";
		thisline+=String.valueOf(thisFil.position[0])+",";
		thisline+=String.valueOf(thisFil.position[1])+",";
		thisline+=String.valueOf(thisFil.position[2])+",";
		thisline+=String.valueOf(thisFil.direction[0])+",";
		thisline+=String.valueOf(thisFil.direction[1])+",";
		thisline+=String.valueOf(thisFil.direction[2])+",";
		thisline+=String.valueOf(thisFil.barbedCapped)+",";
		thisline+=String.valueOf(thisFil.fil_age)+",";			
		thisline+=String.valueOf(thisFil.network_member.id_branch)+",";
		thisline+=String.valueOf(thisFil.DeNovo)+","; //DMH
		if (k_oligomerAnneal >0.0) thisline+=String.valueOf(thisFil.annealed); //DMH
			writer.write_data(thisline);
		}
		writer.write_data("@oligomers"); //DMH
		
		for(int i=0;i<oligomers.size();i++){ //save the oligomers too --DMH
			Filament_3D thisOlig = oligomers.get(i);
			String thisline = String.valueOf(time)+",";
			thisline+=String.valueOf(thisOlig.network_member.id)+",";
			thisline+=String.valueOf(thisOlig.network_member.get_parent_id())+",";
			thisline+=String.valueOf(thisOlig.network_member.location)+",";
			thisline+=String.valueOf(thisOlig.network_member.size)+",";
			thisline+=String.valueOf(thisOlig.position[0])+",";
			thisline+=String.valueOf(thisOlig.position[1])+",";
			thisline+=String.valueOf(thisOlig.position[2])+",";
			thisline+=String.valueOf(thisOlig.direction[0])+",";
			thisline+=String.valueOf(thisOlig.direction[1])+",";
			thisline+=String.valueOf(thisOlig.direction[2])+",";
			thisline+=String.valueOf(thisOlig.barbedCapped)+",";
			thisline+=String.valueOf(thisOlig.fil_age)+",";			
			thisline+=String.valueOf(thisOlig.network_member.id_branch)+",";
			thisline+=String.valueOf(thisOlig.DeNovo)+","; //DMH
			if (k_oligomerAnneal >0.0) thisline+=String.valueOf(thisOlig.annealed); //DMH

			writer.write_data(thisline);
	}
		writer.write_data("@speckles"); //AH
		for(int j=0; j<SpeckleArrayList.spec_list.size(); j++){ // loop through all the speckles
			SpeckleArrayList.spec_constructor curr_spec = SpeckleArrayList.spec_list.get(j);
			double dist_from_init = retFlow*(time - curr_spec.spec_init_time); // in um
			double speck_z = dist_from_init + curr_spec.spec_position_z; // in um 
			boolean on_filament = false;
			for(int i=0;i<sFilament.size();i++){ //AH check filament exists
				Filament_3D thisFil = sFilament.get(i);
				if (thisFil.network_member.id == curr_spec.spec_fil_id) on_filament = true;
			}
			boolean on_oligomer = false;
			for(int i=0;i<oligomers.size();i++){ //AH check oligomers too
				Filament_3D thisOlig = oligomers.get(i);
				if (thisOlig.network_member.id == curr_spec.spec_fil_id) on_oligomer = true;
			}
			boolean was_annealed = curr_spec.origin == 1;
			String thisline = String.valueOf(time)+",";
			thisline+=String.valueOf(curr_spec.spec_position_x)+",";
			thisline+=String.valueOf(curr_spec.spec_position_y)+",";
			thisline+=String.valueOf(speck_z)+",";
			thisline+=String.valueOf(was_annealed)+",";
			thisline+=String.valueOf(on_filament)+",";
			thisline+=String.valueOf(on_oligomer);
			writer.write_data(thisline);
		}
}

}
