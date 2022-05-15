package main;


import main.Filament_3D;
import main.Filament_3DApp;
import main.MHelp;
import main.NetworkMember;
import main.SpeckleArrayList.Speckles;
import main.SpeckleArrayList.spec_constructor;

import java.awt.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Filament_3D{
	/**
	 * State Variables of a Filament
	 */
	NetworkMember network_member; //information relating this filament to other filaments
	double fil_age = 0; // age of the filament; don't confuse with Filament_3DApp.time
	double[] position; //Pointed end position
	double[] oligomer_position; //Position when became oligomer
	double[] direction; //Unit vector direction
	double[] normal;//pointed end direction, for branching
	double filamentlength; // Filament Length
	int barbedCapped; //Is barbed end capped?, 0 never capped, 1 capped, 2 capped then uncapped, 3 or 4 polymerization or depolymerization
	boolean oligomer_transport = true;
	boolean DeNovo = false; // Was it created from De Novo? false = not from de novo
	boolean annealed = false; // Has this filament annealed? false = not annealed
	boolean is_olig = false; // determine if its an oligomer or not, false = not oligomer
	int nSpeckOlig = 0; // number of speckles on oligomer
	
	/**
	 * Local containers of controls declared in Filament_3DApp
	 */
	double dt;
	double k_polyRate; //Rate of polymerization on barbed end
	double p_spec; //probability a monomer is a speckle -- DMH
	double k_depolyRate; //Rate of depolymerization on pointed end
	double k_depolyBERate; //Rate of depolymerization on barbed end
	double k_capRate; //Rate of barbed end capping
	double k_uncapRate; //Rate of barbed end uncapping, never if 0
	double z_branch; //max distance branching can occur
	double k_branchRate; //rate of branching in branching area
	double k_debranchRate; // rate of uncapping of capped pointed ends
	double k_severRate; // rate of severing per monomer
	double k_severRate_u; //uniform severing rate
	double k_oligomerAnneal; // rate of annealing
	double nucSite_x; // nucleation site width
	double nucSite_y; // nucleation site height
	double l_mon; //size of actin monomer
	double filament_kink; //behavior of filaments that interact with cell membrane
	double fil_diffusion; //constant of diffusion for oligomer transport
	double oligomer_time = 0.0; //the age of the filament when it became an oligomer, 0.0 if never
	double p_destab; //probability of BE polymerizing or depolymerzing after uncapping
	double k_polyUncap; //polymerization rate after uncapping
	
	static double severType; //how will severing occur?  0 sever at any monomer, 1 sever in a monomer within the first 2um, 2 sever within 0.5 um of the BE, 3 combination of 1 and 2, 4 severing everwhere except 2 to 4 um  -- DMH
	
	public boolean endSev;  // true is an end severing event will occur
	public boolean unifSev; //true if a uniform severing event will occur
	
	/**
	 * Constructor
	 * @param initialpos
	 * @param initialdir
	 * @param this_network_member
	 */
	public void start(double[] initialpos,double[] initialdir, NetworkMember this_network_member){
		position = initialpos;
    	direction = MHelp.vecNorm(initialdir);
    	double[] seed = new double[]{2*MHelp.rand.nextDouble()-1,2*MHelp.rand.nextDouble()-1,2*MHelp.rand.nextDouble()-1};//random seed vector
    	normal = MHelp.vecNorm(MHelp.vecXProd(direction.clone(),seed)); //perpendicular to direction
    	network_member = this_network_member;
    	barbedCapped = 0;
    	annealed = false;
    	    	
    	/**
    	 * Assign local copies
    	 */
    	dt = Filament_3DApp.dt;
    	k_polyRate = Filament_3DApp.k_polyRate;
    	p_spec = Filament_3DApp.p_spec;
    	k_depolyRate = Filament_3DApp.k_depolyRate;
    	k_depolyBERate = Filament_3DApp.k_depolyBERate;
    	k_capRate = Filament_3DApp.k_capRate;
    	k_uncapRate = Filament_3DApp.k_uncapRate;
		k_branchRate = Filament_3DApp.k_branchRate;
		k_debranchRate = Filament_3DApp.k_debranchRate;
		k_severRate = Filament_3DApp.k_severRate;
		k_severRate_u = Filament_3DApp.k_severRate_u;
		k_oligomerAnneal = Filament_3DApp.k_oligomerAnneal;
		z_branch = Filament_3DApp.z_branch;
		nucSite_x = Filament_3DApp.nucSite_x;
		nucSite_y = Filament_3DApp.nucSite_y;
		l_mon = Filament_3DApp.l_mon;
		filament_kink = Filament_3DApp.filament_kink;
		fil_diffusion = Filament_3DApp.fil_diffusion;
		severType = Filament_3DApp.severType;
		p_destab = Filament_3DApp.p_destab; //probability of polymerizaiton or depolymerization of BE after uncapping
		k_polyUncap = Filament_3DApp.k_polyUncap;  //polymerization rate after uncapping
		
    	set_mono_size(network_member.size);
    }
    
	/**
	 * Computational Loop
	 */
	public void step(){
		fil_age+=dt;
		double[] barbed_loc = get_barbed_loc();
		if(position[1] < 0.0) position[1] = 0.0;
		if(position[1] > nucSite_y) position[1] = nucSite_y;
							
		/** 
	     * First order rates 
	     **/
		// Polymerization & Capping
		double curr_polyRate;
		if(k_polyUncap > 0.0 && barbedCapped == 3) curr_polyRate = k_polyUncap;
		else curr_polyRate = k_polyRate;
		if((barbedCapped == 0 || (Filament_3DApp.depolyBE == 2 && barbedCapped == 3)) && barbed_loc[2] >= 0.0){
			if(filament_kink == 1 ||(barbed_loc[1]>0.0 && barbed_loc[1]<nucSite_y)){
				//adjust poly rate for z location
				double k_polyAdj =  curr_polyRate*(1-Math.exp(-(barbed_loc[2]-l_mon)/l_mon)); // don't add a filament if there isn't room for it at LE
				if(MHelp.rand.nextDouble() <= k_polyAdj*dt && (barbedCapped == 0 || (barbedCapped == 3 && Filament_3DApp.depolyBE == 2))){  // don't allow filaments that already capped to polymerize 
					if((barbed_loc[2] - 0.0027) <= 0.0) System.out.println(k_polyAdj*dt + " " + (barbed_loc[2] - 0.0027));
						set_mono_size(network_member.size+1);  
						
						//add speckles -- DMH
						if(MHelp.rand.nextDouble() <= p_spec ){
							SpeckleArrayList new_speck = new SpeckleArrayList();
							int mono_num = network_member.size;
							new_speck.add_speckle(network_member.id, network_member.size, position, direction, mono_num, Filament_3DApp.time, 0, network_member);
						}
				}
			}
		//capping 
		double kCapRate = 0;
		kCapRate = k_capRate;
		if((MHelp.rand.nextDouble() <= kCapRate*dt && barbedCapped == 0) || (MHelp.rand.nextDouble() <= kCapRate*dt && barbedCapped == 2)||((MHelp.rand.nextDouble() <= kCapRate*dt && Filament_3DApp.depolyBE == 2 && (barbedCapped == 0 || barbedCapped == 3 || barbedCapped == 4)))){ //determine if filament will cap
			barbedCapped = 1; //capped
		} 
	} 

		// Uncapping
		if(barbedCapped == 1){   //only uncap capped filaments
			double rand = MHelp.rand.nextDouble();
			if(rand <= k_uncapRate*dt && Filament_3DApp.depolyBE != 2){
				barbedCapped = 2;
			} else if (rand <= k_uncapRate*dt && Filament_3DApp.depolyBE == 2){ // barbed end destabilization
				if(MHelp.rand.nextDouble() <= p_destab) barbedCapped = 3; // polymerizing BE 
				else barbedCapped = 4; // depolymerizing BE
			}
		}

		// don't allow if its an oligomer
		if(is_olig == false){
		// Barbed end depolymerization after uncapping -- DMH
		if((Filament_3DApp.depolyBE == 1 && barbedCapped == 2) || (Filament_3DApp.depolyBE == 2 && barbedCapped == 4)) {
			if(MHelp.rand.nextDouble() <= k_depolyBERate*dt) { //should be checked!!!! if used with kinking, may need to be adjusted
				//need to decrease barbed end location by one monomer, length should decrease by one monomer
				set_mono_size(network_member.size-1);
				
				if (SpeckleArrayList.spec_list.size() > 0) { //update speckles if neccesary 
					for(int i=0; i<SpeckleArrayList.spec_list.size();i++) {
						SpeckleArrayList.spec_constructor curr_spec = SpeckleArrayList.spec_list.get(i);
						//if(network_member.id == curr_spec.spec_fil_id) {
						if(network_member == curr_spec.network_member) {
							SpeckleArrayList.spec_depolymerize_BE(i,position,direction);
					}
				}
									
				// remove speckles that disappeared 
				Collections.sort(SpeckleArrayList.remove_spec);
				Collections.reverse(SpeckleArrayList.remove_spec);
				for(int k = 0; k < SpeckleArrayList.remove_spec.size(); k++){
					int remove = SpeckleArrayList.remove_spec.get(k);
					SpeckleArrayList.spec_list.remove(remove);
				} 					

				SpeckleArrayList.remove_spec.clear();
					
				
			}
		}
		}
		
		// PE Depolymerization
		if(network_member.root){
			if(MHelp.rand.nextDouble() <= k_depolyRate*dt){
				//position is at pointed end, needs to be moved 1 monomer length forward
				position = MHelp.vecSum(position,MHelp.vecProd(direction,get_dist_to_mono(1))); //moves PE 
				set_mono_size(network_member.size-1);
			
				// speckles need to update with depolymerization events --DMH
				if (SpeckleArrayList.spec_list.size() > 0) {
					nSpeckOlig = 0;
					for(int i=0; i<SpeckleArrayList.spec_list.size();i++) {
						SpeckleArrayList.spec_constructor curr_spec = SpeckleArrayList.spec_list.get(i);
						//if(network_member.id == curr_spec.spec_fil_id) {
						if(network_member == curr_spec.network_member) {
							SpeckleArrayList.spec_depolymerize(i,position,direction);  // i is the index in array
							}
						}
					}
				
				// remove speckles that disappeared 
				Collections.sort(SpeckleArrayList.remove_spec);
				Collections.reverse(SpeckleArrayList.remove_spec);
				for(int k = 0; k < SpeckleArrayList.remove_spec.size(); k++){
					int remove = SpeckleArrayList.remove_spec.get(k);
					SpeckleArrayList.spec_list.remove(remove);
				} 					

				SpeckleArrayList.remove_spec.clear();
									
			}
		}
			
			//Sever first order 
			//severing types: 0 (uniform only) and 1 (uniform and end severing) 
			double rand = MHelp.rand.nextDouble();
			double sev_length = network_member.size;
			if (network_member.size > Filament_3DApp.l_crit && severType == 1) sev_length = Filament_3DApp.l_crit; 
			if(rand <= k_severRate*dt*sev_length &&  severType != 1){ //includes uniform and end severing
				sever(Filament_3DApp.severCap>0);
			}else if (severType == 1) {
				if(rand <= k_severRate*dt*sev_length) endSev = true; // end severing will happen
				else endSev = false;
				double rand2 = MHelp.rand.nextDouble(); // use a new random number for the 2nd severing rate
				if(rand2 <= k_severRate_u*dt*network_member.size) unifSev = true; //uniform severing will happen
				else unifSev = false;
				if(endSev || unifSev) {
					sever(Filament_3DApp.severCap>0);
				}
				
			}

			// Debranching
			if(!network_member.root && MHelp.rand.nextDouble() <= k_debranchRate*dt){
				//debranch
				network_member.change_location(0);
				
			} else if (k_debranchRate == 0.0 && !network_member.root && network_member.size < 5 && get_barbed_loc()[2] > z_branch){ // debranching if filament is to small				
				//debranch
				network_member.change_location(0);	
			}
		} 
			
		
		//oligomer annealing, not intended to be used with BE destabilization 
		if((barbedCapped == 0 || barbedCapped == 2 || network_member.root)&& k_oligomerAnneal > 0.0 && network_member.size > 1.0 
				&& Filament_3DApp.oligomers.size() > 0){			
			annealed = false;
			for(Filament_3D oligomer : Filament_3DApp.oligomers){
				if(network_member.size < 2) continue; 
				if (oligomer.network_member.size < 2) continue; //exclude oligomers that don't really exist because they don't have any monomers --DMH
				if((oligomer.network_member.size == network_member.size) && (oligomer.position == position) && (oligomer.direction == direction)) continue; // don't let an oligomer anneal to itself --DMH
				//if((oligomer.fil_age - oligomer.oligomer_time) < olig_delay ) continue;  // for oligomer delay uncomment with ~ line 1367 -- DMH
				double del_t;
				double[] barbed_anneal_loc = get_in_bounds_point(get_barbed_loc());
				double barbed_diffusion_prob = oligomer.oligomer_transport(barbed_anneal_loc);
				double barb_delta_t = oligomer.delta_time;
				double[] pointed_anneal_loc = get_in_bounds_point(position);
				double pointed_diffusion_prob = oligomer.oligomer_transport(pointed_anneal_loc);
				double point_delta_t = oligomer.delta_time;
				boolean barbed_annealed = (barbedCapped == 0 || barbedCapped == 2) && 
						MHelp.rand.nextDouble() <= barbed_diffusion_prob*k_oligomerAnneal*dt;
				boolean pointed_annealed = network_member.root && (oligomer.barbedCapped == 0 || oligomer.barbedCapped == 2) &&
						MHelp.rand.nextDouble() <= pointed_diffusion_prob*k_oligomerAnneal*dt;				
				if(pointed_annealed || barbed_annealed){
					if(pointed_annealed && barbed_annealed){ //choose end with a higher probability to use 
						pointed_annealed = pointed_diffusion_prob > barbed_diffusion_prob;
						barbed_annealed = barbed_diffusion_prob >= pointed_diffusion_prob;
						}

					int original_size = network_member.size;
					double[] original_position = position;//--DMH
					int olig_original_size = oligomer.network_member.size;
					double[] olig_original_positon = oligomer.position; //--DMH
					double[] anneal_pos = {0, 0, 0};
					if (barbed_annealed) anneal_pos = get_barbed_loc();
					else if (pointed_annealed) anneal_pos = position;
					//we'll change the size to what it would be after annealing
					//then we'll revert the change if it would pass through a membrane
					if(pointed_annealed){
						position = MHelp.vecSum(position,MHelp.vecProd(MHelp.vecProd(direction.clone(), -1.0),
								get_dist_to_mono(oligomer.network_member.size,MHelp.vecProd(direction.clone(), -1.0))));
					} //position remains the same for BE annealing since position is determined by the PE location
					
					set_mono_size(network_member.size+oligomer.network_member.size);
					int check_leading_edge = count_mono_to_z(0.0);
					int check_membrane1 = count_mono_to_y(0.0);
					int check_membrane2 = count_mono_to_y(nucSite_y);
					if((network_member.size > check_leading_edge && check_leading_edge > 0) || ((
											(network_member.size > check_membrane1 && check_membrane1 > 0) ||
											(network_member.size > check_membrane2 && check_membrane2 > 0)
									) && filament_kink == 0) ){// if true, filament intersects the boundaries, annealing is rejected and everything must go back to its original state 
						if(pointed_annealed){ 
							position = original_position; //--DMH
						} // end if pointed_annealed
						set_mono_size(original_size);//it hit a membrane
						}else{//anneal successful
						oligomer.network_member.change_size(0); //remove from oligomer pool
						if(barbed_annealed) barbedCapped = oligomer.barbedCapped; //transfer capped state
						annealed = true; 
						
						boolean olig2olig = false;
												
						int pointAnn;
						if (pointed_annealed) {
							pointAnn= 1;  // pointed end annealed
							del_t = point_delta_t;
						}
						else {
							pointAnn = 0; // barbed end annealed
							del_t = barb_delta_t;
						}	
						
						if (k_oligomerAnneal > 0.0 && oligomer_transport && // If filament from filament list is an oligomer, we know the olig from olig list is an oligomer
								network_member.size <= Filament_3DApp.l_crit && 
								network_member.size > 1 && 
								network_member.children.size()<1 &&
								network_member.get_parent_id() == 0) {
							olig2olig = true;
						}
					//need to add speckles back to the annealed oligomer	
					if(p_spec > 0){
						int n_spec_add = 0;
						int n_spec_tot = 0;
						int start_mono = 0;  // monomer to start adding speckles to if annealed to the BE
						SpeckleArrayList annealed_speck = new SpeckleArrayList();
						if(pointed_annealed && !is_olig) annealed_speck.update_ann_spec(network_member.id, olig_original_size,network_member); // need to update fil id and speckle mono from PE on filament
						if (is_olig) { //if 'filament' annealed to is also an oligomer consider both
							n_spec_add = olig_original_size + original_size; // size of filament in monomers to check
							n_spec_tot = nSpeckOlig + oligomer.nSpeckOlig; // total speckles to add from oligomer annealing
							start_mono = 0;
							if(n_spec_add < n_spec_tot) System.out.println("line 597: size " + n_spec_add + " add " + n_spec_tot);
						}
						else if(!is_olig) {
							n_spec_add = olig_original_size; // size of filament in monomer to check
							n_spec_tot = oligomer.nSpeckOlig; // total speckles to add from oligomer annealing
							start_mono = original_size;
							if(n_spec_add < n_spec_tot) System.out.println("line 603: size " + n_spec_add + " add " + n_spec_tot);
						}
						if(network_member.size < Filament_3DApp.l_crit && is_olig) nSpeckOlig = n_spec_tot; // if you're still an oligomer just update the number of speckles
						else { // otherwise add the speckles 
								ArrayList<Integer> monoAddSpeck = new ArrayList <Integer>();
								int breakloop = 0;
								while(monoAddSpeck.size() < n_spec_tot) {
									breakloop = breakloop + 1;
									if(monoAddSpeck.size() <= 0) monoAddSpeck.add(MHelp.rand.nextInt(n_spec_add + 1)); // first picked monomer will always work
									else {
										int randSpeck = MHelp.rand.nextInt(n_spec_add + 1); 
											if(!monoAddSpeck.contains(randSpeck)) {
												monoAddSpeck.add(randSpeck); // add the random speckle
												break; // break the loop if you're not double assigning a speckle
											}
									}
									if(breakloop > 500) break;
								}

								if(monoAddSpeck.size() > n_spec_tot) System.out.println("err 620: oligNum " + n_spec_tot + " add at mono size " + monoAddSpeck.size());
								for(int m = 0; m < monoAddSpeck.size(); m++) {// now we know the position, add speckles in that location
									int new_mono_num = 0;
									if(pointed_annealed){	// oligomer attached to PE, need to adjust speckles on current filament and add new speckles for the oligomer
										new_mono_num = monoAddSpeck.get(m);
									}else if (barbed_annealed){ // oligomer added to BE, only need to add speckles for the oligomer
										new_mono_num = monoAddSpeck.get(m) + start_mono;
									}
									annealed_speck.add_speckle(network_member.id, network_member.size, position, direction, new_mono_num, Filament_3DApp.time, 1, network_member);
								}
						nSpeckOlig = 0; // annealed and isn't an oligomer so set the speckles back to zero 
						}						
					}
					Filament_3DApp.toRemoveOligomer.add(oligomer); 
							
					}
				}
			
			if(annealed == true) break;
			
			} //while (annealed = false);
		}

		if(filamentlength < 0.0) filamentlength = 0.0; //no negative
    }
	
		
	/**
	 * Change the monomer length of this filament. Updates filament length and network
	 * @param mono_num (int) new monomer length
	 */
	public void set_mono_size(int mono_num){
		filamentlength = get_dist_to_mono(mono_num);
		network_member.change_size(mono_num);
	}
	
	/**
	 * Gets the distance to a monomer placed mono_num units away from position in direction. 
	 * As portions of filaments that extend beyond the y bounds are projected on the xz plane, this
	 * function ensures the drawn filament size is appropriate for its size in monomers. This means
	 * filamentlength is not true to life, and is longer than mono_num*l_mon when extending 
	 * beyond the y bounds
	 * @param mono_num (int)
	 * @return distance (double)
	 */
	public double get_dist_to_mono(int mono_num){
		return get_dist_to_mono(mono_num, direction.clone());
	}
	/**
	 * Gets the distance to a monomer placed mono_num units away from position in direction. 
	 * As portions of filaments that extend beyond the y bounds are projected on the xz plane, this
	 * function ensures the drawn filament size is appropriate for its size in monomers. This means
	 * filamentlength is not true to life, and is longer than mono_num*l_mon when extending 
	 * beyond the y bounds
	 * @param mono_num (int)
	 * @return distance (double)
	 */
	public double get_dist_to_mono(int mono_num, double[] this_direction){
		double ret = 0.0;
		double barrier_membrane = this_direction[1]>=0?nucSite_y:0.0;
		double dist_membrane;
		if(position[1] > 0.0 && position[1] < nucSite_y) dist_membrane = (barrier_membrane-position[1])/this_direction[1];
		else dist_membrane = 0;
		if(mono_num*l_mon<dist_membrane){
			ret = mono_num*l_mon;
		}else{
			//polymerization will be 'faster' outside of membrane, since only projection in xz plane is seen
			double monos_to_barrier = (dist_membrane/Double.valueOf(l_mon));
			double remainder = Double.valueOf(mono_num)-monos_to_barrier;
			double x_z_mag = remainder*l_mon;
			//trig solution for projecting xz vector mag to xyz vector
			double projected_mag = Math.sqrt(Math.pow(x_z_mag,2)/(Math.pow(this_direction[0], 2)+Math.pow(this_direction[2], 2)));
			ret = dist_membrane + projected_mag;
			
		}
		return ret;
	}
	
	/**
	 * Determines how many monomer lengths a distance on the z-axis is from origin of filament.
	 * If can't get closer than origin in direction of filament, returns zero.
	 * If exceeds size of filament, returns the size of filament.
	 * @param zloc (double)
	 * @return monomer count (int)
	 */
	public int count_mono_to_x(double loc){
		return count_mono_to_var(loc,0);
	}
	public int count_mono_to_y(double loc){
		return count_mono_to_var(loc,1);
	}
	public int count_mono_to_z(double loc){
		return count_mono_to_var(loc,2);
	}
	public int count_mono_to_var(double loc,int x_y_z){
		double last_step = position[x_y_z];
		for(int count=0;count<=network_member.size;count++){
			double step_size = get_dist_to_mono(count+1);
			double this_step =  MHelp.vecSum(position,MHelp.vecProd(direction,step_size))[x_y_z];
			if(Math.abs(this_step-loc)>Math.abs(last_step-loc)){
				return count;
			}else{
				last_step = this_step;
			}
		}
		return network_member.size;
	}
	
	/**
	 * Counts number of monomers close enough to leading edge to branch
	 * @return monomer count (int)
	 */
	public int count_branch_sites(){
		return count_mono(0,z_branch,2);
	}
	/**
	 * Allows you to specify z value
	 * @param where
	 * @return
	 */
	public int count_branch_sites(double where){
		return count_mono(0.0,where);
	}
	
	/**
	 * Creates a branch on a branch site near the leading edge on this filament
	 */
	public boolean branch(){
		int branchSites;
			branchSites = count_branch_sites();
		return branch(true, branchSites);
	}
	
	/**
	 * Creates a branch off of this filament.
	 * @param leading_edge (boolean) If true, branch will be only near the leading edge, otherwise it will only
	 * be away from the leading edge.
	 */
	public boolean branch(boolean leading_edge,int branchSites){
		if(branchSites<1){
			//can't branch
			return false;
		}
		turn_on_oligomer_transport();
		boolean down = direction[2]<0;
		if(!leading_edge) down = !down;
			int i = MHelp.rand.nextInt(branchSites);
			if(down) i = network_member.size-i;
			else i++;
			return branch(i);
	}
	/**
	 * Branch at specific monomer i
	 * @param i
	 * @return
	 */
	public boolean branch(int i){
		double[] initialpos;
		initialpos = MHelp.vecSum(position,MHelp.vecProd(direction,get_dist_to_mono(i)));
		double[] parentdir = direction.clone();
		if(initialpos[1]>nucSite_y || initialpos[1]<0.0){
			parentdir[1] = 0.0;
			parentdir = MHelp.vecNorm(parentdir);
			if(initialpos[1]>nucSite_y)initialpos[1]=nucSite_y;
			if(initialpos[1]<0.0)initialpos[1]=0.0;
		}
		double[] initialdir;
		
			// quaternion rotation
			double[] rotVec;
		    //only forward branching
		    double[] seed = new double[]{2*MHelp.rand.nextDouble()-1,2*MHelp.rand.nextDouble()-1,2*MHelp.rand.nextDouble()-1};//random seed vector
			rotVec = MHelp.vecNorm(MHelp.vecXProd(parentdir,seed)); //random vector perpendicular to direction
			
			double BrAng = Math.toRadians(5*MHelp.rand.nextGaussian() + 70);  //branch angle is a gaussian distribution around 70 with stdev of 5 degrees, rotated in radians, same as weischel and schwarz
			initialdir = MHelp.rotate_vector(parentdir, rotVec, BrAng); 
			double cos_rad = MHelp.vecProd(MHelp.vecNorm(initialdir), new double[] { 0, 0, -1 }); //DMH
			double fil_degm = Math.toDegrees(Math.acos(cos_rad));  //magnitude of the filament angle, zero is perpendicular to LE --DMH
			
			double outPlaneAng = Filament_3DApp.outPlaneAng; // out of plane branching angle allowed -- DMH

			if(outPlaneAng == 0.0){
				while(fil_degm >= 80.0){ // for only branching toward LE and elimination of the 35/35 backward branching peak-- DMH	
						seed = new double[]{2*MHelp.rand.nextDouble()-1,2*MHelp.rand.nextDouble()-1,2*MHelp.rand.nextDouble()-1};//random seed vector
						rotVec = MHelp.vecNorm(MHelp.vecXProd(parentdir,seed)); //random vector perpendicular to direction
						BrAng = Math.toRadians(5*MHelp.rand.nextGaussian() + 70); 
						initialdir = MHelp.rotate_vector(parentdir, rotVec, Math.PI*2.0/5.0); // 2*Pi/5 is a 72 degree angle
						cos_rad = MHelp.vecProd(MHelp.vecNorm(initialdir), new double[] { 0, 0, -1 }); //DMH
						fil_degm = Math.toDegrees(Math.acos(cos_rad));  //magnitude of the filament angle, zero is perpendicular to LE --DMH
					}
			}

			double restAng = (Math.PI/2) - Math.toRadians(outPlaneAng); //angle between initialdir and unit vector y/Mag1 (restricted region) -- DMH
			double angCutOff = Math.cos(restAng); // angles smaller than this are allowed
			if(outPlaneAng>0.0){
				double Mag1 = Math.abs(MHelp.vecNorm(initialdir)[1]);  //Magnitude in y direction, dot product between this direction normalized vector gives ydir magnitude -- DMH
					int break_inf_loop = 0;
					while(Mag1 >= angCutOff  || fil_degm >= 80.0){ //forward branching
						seed = new double[]{2*MHelp.rand.nextDouble()-1,2*MHelp.rand.nextDouble()-1,2*MHelp.rand.nextDouble()-1};//random seed vector
						rotVec = MHelp.vecNorm(MHelp.vecXProd(parentdir,seed)); //random vector perpendicular to direction
						BrAng = Math.toRadians(5*MHelp.rand.nextGaussian() + 70);
						initialdir = MHelp.rotate_vector(parentdir, rotVec, BrAng); 
						cos_rad = MHelp.vecProd(MHelp.vecNorm(initialdir), new double[] { 0, 0, -1 }); //DMH
						fil_degm = Math.toDegrees(Math.acos(cos_rad));  //magnitude of the filament angle, zero is perpendicular to LE --DMH
						Mag1 = Math.abs(MHelp.vecNorm(initialdir)[1]);	//magnitude in the 1 (y, height) direction		
						break_inf_loop ++;
						if (break_inf_loop >= 1000) break;
				} 
				if(Mag1<angCutOff){
					Filament_3DApp.initFilament(initialpos,initialdir,network_member.id,i,1);
					return true;
				}
			}else{
				Filament_3DApp.initFilament(initialpos,initialdir,network_member.id,i,1);
				return true;
			}
			return false;
	}

	/**
	 * Splits filament at random location. This Filament_3D object assumes role of monomers near barbed end.
	 * New Filament_3D created to assume role of monomers near pointed end.
	 * @param severCap (boolean) if true, new barbed end is capped
	 */
	public void sever(boolean severCap){
		if(network_member.size <= 1){
			filamentlength = 0;
			network_member.change_size(0);
			return;
		}
		
		boolean foundMono = false; 
		
		int i = 0;
		int u = 0;
		if(severType == 0){  // sever anywhere
			i = MHelp.rand.nextInt(network_member.size-1);
			foundMono = true;
		}else if(severType == 1){  // sever near BE and anywhere with two different rates 
				if(endSev){  // sever near the end 
					int NearBEN2 = (int) MHelp.round(Filament_3DApp.l_crit, 0); //number of monomers near BE to sever
					if(network_member.size > NearBEN2) i = network_member.size - MHelp.rand.nextInt(NearBEN2-1); 
					else i = MHelp.rand.nextInt(network_member.size-1); // if it's smaller than the critical length sever anywhere on the filament
					foundMono = true;
					double[] barb_loc2 = get_barbed_loc();
					if (barbedCapped == 0 || barb_loc2[2] <= 0.1) foundMono = false; // don't sever anything that is still polymerizing or very close to the leading edge 
				}
				if(unifSev) { //sever anywhere along the filament
					u = MHelp.rand.nextInt(network_member.size-1);
					foundMono = true;
					if(endSev && u>=i) unifSev = false; // don't sever after you already severed into an oligomer
					if(!endSev) i = u;  // if you're not end severing at the end, make sure you're reasssigning where you'll sever 
			}

		} 
		
		//make new filament
		int numSev = 1; //how many severing events occur 
		if(severType == 9 && endSev && unifSev) numSev = 2;
		if(foundMono){
			for(int m=0; m<numSev;m++) { //loop through this twice if you need 2 severing events
				if(m>0) i = u;
			double[] initialdir = direction.clone();
			double[] initialpos = position.clone();
			NetworkMember new_member = network_member.split(i);
			Filament_3D newFil = Filament_3DApp.initFilament(initialpos,initialdir,new_member);
			newFil.fil_age = fil_age;
			if(severCap) {
				newFil.barbedCapped = 1;
			}else newFil.barbedCapped = 2; //if severing doesn't cap, let the filament anneal
			double[] newposition = MHelp.vecSum(position,MHelp.vecProd(direction,get_dist_to_mono(i)));
			position = newposition;
			set_mono_size(network_member.size);
			
			
			// check if speckles disappeared into oligomers otherwise they need updating
			if(SpeckleArrayList.spec_list.size() > 0){  // only check if speckles are being considered
				//nSpeckOlig = 0;
				//newFil.nSpeckOlig = 0;
				int count = 0;
				for(int j=0; j<SpeckleArrayList.spec_list.size(); j++){ // loop through all the speckles
					SpeckleArrayList.spec_constructor curr_spec = SpeckleArrayList.spec_list.get(j);
					SpeckleArrayList severed_speck = new SpeckleArrayList();
					//if(network_member.id == curr_spec.spec_fil_id){ // when the speckle filament id matches the network id, the speckle is on the current filament
					if(network_member == curr_spec.network_member){
						count = count +1;
						if(curr_spec.mono_from_PE <= newFil.network_member.size){  // speckle is on the new portion near the PE
							//severed_speck.spec_severed(j,newFil.network_member.id, curr_spec.mono_from_PE, newFil.is_olig, newFil.position,newFil.network_member);
							severed_speck.spec_severed(j,newFil.network_member.id, curr_spec.mono_from_PE, false, newFil.position,newFil.network_member);
						}else if(curr_spec.mono_from_PE > newFil.network_member.size){ // Speckle is on the old portion near the BE
							int new_mono_from_PE = curr_spec.mono_from_PE - newFil.network_member.size;
							severed_speck.spec_severed(j,network_member.id,new_mono_from_PE, false, position,network_member);
						} 
					}
				}  
				// remove speckles that disappeared 
				Collections.sort(SpeckleArrayList.remove_spec);
				Collections.reverse(SpeckleArrayList.remove_spec);
				for(int k = 0; k < SpeckleArrayList.remove_spec.size(); k++){
					int remove = SpeckleArrayList.remove_spec.get(k);
					SpeckleArrayList.spec_list.remove(remove);
				} 					
				SpeckleArrayList.remove_spec.clear();	
			}
		}
		}
	}
	/**
	 * Counts how many monomers this filament has between the given z values
	 * @param zi (double) First z boundary
	 * @param zf (double) Second z boundary
	 * @return (double) Return may not be whole number; take floor if only looking for contained monomers
	 */
	public int count_mono(double zi, double zf){
		return count_mono(zi,zf,2);
	}
	/**
	 * Count how many monomers between two values of a given axis
	 * @param init
	 * @param fin
	 * @param x_y_z
	 * @return
	 */
	public int count_mono(double init, double fin, int x_y_z){
		int ret = Math.abs(count_mono_to_var(init, x_y_z)-count_mono_to_var(fin ,x_y_z));
		return ret;
	}
	
	/**
	 * Gets the location of the barbed end
	 * @return location (double[])
	 */
	public static boolean test = false;
	public double[] get_barbed_loc(){
		return MHelp.vecSum(position,MHelp.vecProd(direction,filamentlength));
	}
	
	/**
	 * Turns this filament into a diffuse monomer
	 */
	public void oligomer_start(){
		oligomer_time = fil_age;
		oligomer_position = position.clone(); // --DMH
	}
	
	/**
	 * Determine likelihood this diffuse monomer is at anneal_pos
	 * @param anneal_pos
	 * @return probability
	 */
	
	public double delta_time;
	
	public double oligomer_transport(double[] anneal_pos){
		
		delta_time = fil_age - oligomer_time;  // age of oligomer (DMH)
		
		double[] olig_pos_min;
		olig_pos_min = get_in_bounds_point(oligomer_position, anneal_pos); //accounts for periodic boundary conditions in the oligomer based on the annealing position
		
		if(delta_time == 0 || fil_diffusion == 0) return 0.0;
		return (1/(4*Math.PI*fil_diffusion*delta_time))*
				Math.exp(
						-(Math.pow(olig_pos_min[0]-anneal_pos[0],2)+Math.pow(olig_pos_min[2]-anneal_pos[2],2))/
						(4*fil_diffusion*delta_time)  // probability of diffusion 
				);
	}
	/**
	 * Give a point in 3D space, returns point in lamellipodium, subject to
	 * cell membrane barriers and periodic boundaries
	 * @param point
	 * @return
	 */
	public double[] get_in_bounds_point(double[] point){
		double[] ret = new double[]{0,0,0};
		if(point[0]>=0) ret[0] = Math.abs(point[0]) % nucSite_x;
		else ret[0] = nucSite_x - (Math.abs(point[0]) % nucSite_x);
		if(point[1] < nucSite_y && point[1] > 0.0) ret[1] = point[1];
		else ret[1] = point[1]>0?nucSite_y:0.0;
		ret[2] = point[2];
		return ret;
	}

	/**
	 * -- DMH
	 * Give a point in 3D space, returns the position in the 0 direction with periodic
	 * boundary conditions, point is the nearest and not always within the boundaries
	 * finds the closest oligomer point (either PE or BE) to the filament point (PE or BE, 
	 * filament is already within the boundaries) 
	 * @param OligPoint
	 * @param FilPoint 
	 * @return
	 */
	public double[] get_in_bounds_point(double[] oligPoint, double[] filPoint){
		double[] ret = new double[]{0,0,0};
		if((oligPoint[0]<=(filPoint[0]+(nucSite_x/2)))&&(oligPoint[0]>=(filPoint[0]-(nucSite_x/2)))) ret[0] = oligPoint[0];
		else if((oligPoint[0]>(filPoint[0]+(nucSite_x/2))) && ((Math.abs(oligPoint[0]) % nucSite_x) <= nucSite_x) && ((Math.abs(oligPoint[0]) % nucSite_x) > (filPoint[0]+(nucSite_x/2)))) {
			ret[0] = (Math.abs(oligPoint[0]) % nucSite_x) - nucSite_x;
		}else if ((oligPoint[0]>(filPoint[0]+(nucSite_x/2)))){
			ret[0] = Math.abs(oligPoint[0]) % nucSite_x;
		}else if (oligPoint[0]<(filPoint[0]-(nucSite_x/2))&&((Math.abs(oligPoint[0]) % nucSite_x) >= 0) && ((Math.abs(oligPoint[0]) % nucSite_x) < (filPoint[0]-(nucSite_x/2)))) {
			ret[0] = nucSite_x - (Math.abs(oligPoint[0]) % nucSite_x);
		}else if(oligPoint[0]<(filPoint[0]-(nucSite_x/2))) {
			ret[0] = nucSite_x - (Math.abs(oligPoint[0]) % nucSite_x);
		}
		if(oligPoint[1] < nucSite_y && oligPoint[1] > 0.0) ret[1] = oligPoint[1];
		else ret[1] = oligPoint[1]>0?nucSite_y:0.0;
		ret[2] = oligPoint[2];
		return ret;
	}
	/**
	 * Disables oligomer transport, intended for de novo nucleated filaments
	 */
	protected void turn_off_oligomer_transport(){
		oligomer_transport = false;
	}
	/**
	 * Enables oligomer transport 
	 */
	protected void turn_on_oligomer_transport(){
		oligomer_transport = true;
	}
	
}
