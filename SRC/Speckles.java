package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import main.SpeckleArrayList.Speckles;

/**
 * Manage lifetime, appearance and disappearance locations of randomly added speckles 
 * @author Danielle Holz
 */

class SpeckleArrayList{

	static ArrayList<String> speckle_data = new ArrayList<String>(); // keep track of speckle data to print to a file 
	
	static ArrayList<spec_constructor> speckles = new ArrayList<>();
	
	static ArrayList<spec_constructor> spec_list = new ArrayList<>();
	
	static ArrayList<Integer> remove_spec = new ArrayList<Integer>();
		
	public static int spec_id_count = 0; 
	
		class spec_constructor{ // set up the custom arraylist for the speckles 
		
		public int spec_id; //speckle Id
		public int spec_fil_id; //ID of the filament the speckle is located on 
		public double spec_position_x; //initial position of the speckle in x direction 
		public double spec_position_y;
		public double spec_position_z;
		public double spec_init_time; //time the speckle was created
		public int mono_from_PE; // distance in monomers that the speckle is located from the PE
		public int origin; // if monomer was added through polymerization or through annealing from an oligomer
		public NetworkMember network_member;
		public spec_constructor(int spec_id, int spec_fil_id, double fil_position_x, double fil_position_y, double fil_position_z,
				int mono_from_PE, double spec_init_time, int origin, NetworkMember this_network_member){
		
			this.spec_id = spec_id;
			this.spec_fil_id = spec_fil_id;
			this.spec_position_x = fil_position_x;
			this.spec_position_y = fil_position_y;
			this.spec_position_z = fil_position_z;
			this.mono_from_PE = mono_from_PE; 
			this.spec_init_time = spec_init_time; 
			this.origin = origin;
			this.network_member = this_network_member;
		}	
} 
	
/* add/initialize speckles 
 * Called during polymerization and annealing events 
 */		
	public void add_speckle(int fil_ID, int fil_size, double[] fil_position, double[] fil_direction, int mono_num, double time, int origin, NetworkMember this_network_member) {
		int spec_id;
		int spec_fil_id; 
		double[] spec_position;
		double spec_position_x;
		double spec_position_y;
		double spec_position_z;
		double spec_init_time;
		int mono_from_PE;
		//int origin;
		//double spec_fin_time;
			
		spec_id = spec_id_count;
		spec_id_count = spec_id_count + 1; 
		spec_fil_id = fil_ID; 
		//origin = 0; // 0 for polymerization, 1 for on an oligomer
		
		spec_position = speckle_position(fil_position, fil_direction, mono_num);  //found from PE of the fil to number of monomers away 

		spec_position_x = spec_position[0];  
		spec_position_y = spec_position[1];
		spec_position_z = spec_position[2];
				
		mono_from_PE = mono_num; 
		
		spec_init_time = time;
				
		//add speckle with all its information to the list
		spec_list.add(new spec_constructor(spec_id, spec_fil_id, spec_position_x, spec_position_y, spec_position_z,
				mono_from_PE, spec_init_time, origin, this_network_member));
		
		if(mono_from_PE > fil_size){
			System.out.println("line 86 " + " id " + fil_ID + " size " + fil_size + " spec pos " + mono_from_PE);
			}	
	}
/*
 * Calculate speckle position using PE position, direction and distance from the PE (in monomers)
 */
	public static double[] speckle_position(double[] PE_position, double[] direction, int monoDist) {
		double monoLen = monoDist*Filament_3DApp.l_mon;
		return MHelp.vecSum(PE_position,MHelp.vecProd(direction,monoLen));
	}

	/* 
	 * update speckle data for depolymerization event
	 */
	public static void spec_depolymerize(int spec_index, double[] position, double[] direction) { // speckle disappearance from PE
		spec_constructor curr_spec = spec_list.get(spec_index);
		int dist = curr_spec.mono_from_PE-1; 
		curr_spec.mono_from_PE = dist; // speckle moves 1 monomer closer to the PE due to depolymerization
		if(dist == 0) {
			spec_disappearance(spec_index, 0); // speckle reached the PE, it depolymerized, record speckle data		
		}
	}

	public static void spec_depolymerize_BE(int spec_index, double[] position, double[] direction) { // speckle disappearance from BE
		spec_constructor curr_spec = spec_list.get(spec_index);
		int fil_index = get_fil_index(curr_spec.spec_fil_id);
		Filament_3D thisFil = Filament_3DApp.sFilament.get(fil_index);
		int dist = curr_spec.mono_from_PE;
		if(dist > thisFil.network_member.size) spec_disappearance(spec_index, 1); // speckle depolymerized off filament at BE, record speckle data		
	}

	/*
	 * Speckle disappeared
	 */
	public static void spec_disappearance(int spec_index, int type) {	
		spec_constructor curr_spec = spec_list.get(spec_index);
		int fil_index = get_fil_index(curr_spec.spec_fil_id);
		Filament_3D thisFil = Filament_3DApp.sFilament.get(fil_index);
		double[] speck_pos_final = speckle_position(thisFil.position, thisFil.direction, curr_spec.mono_from_PE); //find final position for speckle
		double timeSim = MHelp.round(Filament_3DApp.time, 6); // current simulation time
		// add speckle information to a list to be printed to a speckle file later
		String specEvent = String.valueOf(timeSim+","+curr_spec.spec_id+","+curr_spec.mono_from_PE+","+curr_spec.spec_init_time+","+thisFil.network_member.id+","+
				curr_spec.spec_position_x+","+curr_spec.spec_position_y+","+curr_spec.spec_position_z+","+
				speck_pos_final[0]+","+speck_pos_final[1]+","+speck_pos_final[2]+","+type+","+curr_spec.origin);			
			speckle_data.add(specEvent);
		//type 0 is disappearance from PE depolymerization
		//type 1 is disappearance from BE depolymerization
		//type 2 is disappearance from severing into an oligomer
			
			remove_spec.add(spec_index); // add the speckles that disappeared to the "remove list" to be removed after all speckles have been checked 
			
	}
	

	/*
	 * Update speckles in a severing event
	 */
	public void spec_severed(int spec_index, int fil_id, int new_pos_num, boolean isolig, double[] fil_PE_position, NetworkMember this_network_member){
		spec_constructor curr_spec = spec_list.get(spec_index);
		double init_time = curr_spec.spec_init_time;
		double init_pos_x = curr_spec.spec_position_x;
		double init_pos_y = curr_spec.spec_position_y;
		double init_pos_z = curr_spec.spec_position_z;
		int spec_id = curr_spec.spec_id;
		int origin = curr_spec.origin;
		
		
		//need to update filament id for the new portion or mono from PE for new portion, 
		//numbers are changed when spec_severed is called
		spec_list.set(spec_index, new spec_constructor(spec_id, fil_id, init_pos_x, 
					init_pos_y, init_pos_z, new_pos_num, init_time, origin, this_network_member));
			
		if(isolig){  // olgiomers disappear  
			spec_disappearance(spec_index,2);
		}
	}
	
	/*
	 * update speckles where the oligomer annealed to the BE
	 */
	
	public void update_ann_spec(int fil_id, int olig_size, NetworkMember this_network_member){
		int countSp = 0;
		for(int j=0; j<spec_list.size(); j++){ // loop through all the speckles
			spec_constructor curr_spec = spec_list.get(j);
			//if(fil_id == curr_spec.spec_fil_id){ // when the speckle filament id matches the network id, the speckle is on the current filament
			if(this_network_member == curr_spec.network_member) {
				double init_time = curr_spec.spec_init_time;
				double init_pos_x = curr_spec.spec_position_x;
				double init_pos_y = curr_spec.spec_position_y;
				double init_pos_z = curr_spec.spec_position_z;
				int spec_id = curr_spec.spec_id;
				int new_pos_num = curr_spec.mono_from_PE + olig_size;
				int origin = 1; // speckle came from an oligomer
				
				
				spec_list.set(j, new spec_constructor(spec_id, fil_id, init_pos_x, 
					init_pos_y, init_pos_z, new_pos_num, init_time, origin, this_network_member));
				
				countSp = countSp + 1; 
			}
		}		
	}
		
		
	public void ann_test(int fil_id, int olig_size){
		// update speckles where the oligomer annealed to the BE
		int countSpBE = 0;
		for(int j=0; j<spec_list.size(); j++){ // loop through all the speckles
			spec_constructor curr_spec = spec_list.get(j);
		//	SpeckleArrayList severed_speck = new SpeckleArrayList();
			if(fil_id == curr_spec.spec_fil_id){ // when the speckle filament id matches the network id, the speckle is on the current filament
			}
		}	
	}
		
	
	/*
	 *  get index for the filament or id with fil_id
	 */
	public static int get_fil_index(int fil_id){
		int index = -1; 
		for(int i = 0; i < Filament_3DApp.sFilament.size(); i++) {  //loop through filaments first
			Filament_3D thisFil = Filament_3DApp.sFilament.get(i);
			int curr_id = thisFil.network_member.id;
			if(curr_id == fil_id) {
				index = i;
				Filament_3DApp.oligIndex = false;
				break;
			} else index = -34; 
		}
		if(index == -34) { // it wasn't a filament so look through the oligomer list
			for(int i = 0; i < Filament_3DApp.oligomers.size(); i++ ) {
				Filament_3D thisOlig = Filament_3DApp.oligomers.get(i);
				int curr_id = thisOlig.network_member.id;
				if(curr_id == fil_id) {
					index = i;
					Filament_3DApp.oligIndex = true;
					break;
				} else index = -35; // gives an error if it can't find the id in either list 
			}
		}
		return index;
	}	
	
	public static class Speckles{
		public static void save_speckles() {
			//write speckle events to a file

			String fnameSpeck = ("Speckles_AnnealRate" + Filament_3DApp.k_oligomerAnneal +  "_Diff" + Filament_3DApp.fil_diffusion +  "_Sev" + Filament_3DApp.k_severRate + "_Debr" + Filament_3DApp.k_debranchRate + ".csv");

				try {
						
					BufferedWriter bw = new BufferedWriter(new FileWriter(fnameSpeck, true));
					
					if (Filament_3DApp.spec_header){
						bw.write("Time,Speckle_ID,dist_from_PE,Init_spec_time,Fil_ID,init_x,init_y,init_z,fin_x,fin_y,fin_z,type,origin,\n");
						Filament_3DApp.spec_header = false; 
					}

					for(int i=0;i<speckle_data.size(); i++){	

					bw.write(speckle_data.get(i)+"\n");

						
				}
					bw.close();	
				}catch(Exception ex){}
				
				speckle_data.clear();
		}
		}

}