package main;

import java.util.ArrayList;
/**
 * Maintains relationship between members of filament network
 * @author Aaron Hall
 */
public class NetworkMember {
	public int id; //this filament id
	public int id_branch; //id of the branch/dendral this is connected to, or 0 if none
	public int size; //size of this filament in monomers
	public int location; //monomer on parent this filament is attached to
	public boolean root; //is this filament the root filament of a branch/dendral?
	public NetworkList this_list; //the NetworkList this filament is a member of
	public NetworkMember parent; //this filament's parent, or null if none
	public ArrayList<NetworkMember> children; //All filaments attached to this filament
	
	public NetworkMember(NetworkList list){
		constructor(0,0,list.new_id(),list);
	}
	
	public NetworkMember(int parent_id, int loc, NetworkList list){
		constructor(parent_id, loc, list.new_id(), list);
	}
	
	public NetworkMember(int parent_id, int loc, int this_id, NetworkList list){
		constructor(parent_id, loc, this_id, list);
	}
	public void constructor(int parent_id, int loc, int this_id, NetworkList list){
		children = new ArrayList<NetworkMember>();
		this_list = list;
		id = this_id;
		set_parent(parent_id, loc);
	}
	/**
	 * Sets parent, checking variety of conditions
	 * Updates network based on change
	 * @param parent_id
	 * @param loc
	 * @return
	 */
	public boolean set_parent(int parent_id, int loc){
		int new_id_branch = id_branch;
		if(parent_id > 0){
			parent = this_list.get_member(parent_id);
			if(parent != null && parent.size >= loc && loc > 0){
				parent.children.add(this);
				root = false;
				location = loc;
				new_id_branch = parent.id_branch;
				if(new_id_branch != id_branch) set_id_branch(new_id_branch);
				return true;
			}
		}
		parent = null;
		root = true;
		location = 0;
		new_id_branch = id;
		if(new_id_branch != id_branch) set_id_branch(new_id_branch);
		return false;
	}
	/**
	 * Changes monomer size of filament
	 * Updates network based on change
	 * @param new_size
	 */
	public void change_size(int new_size){
		int delta_size = size - new_size;
		size = new_size;
		children.clear();
		for(NetworkMember member: this_list.member_list){
			if(member.parent == this){
				member.parent = null;
				if(delta_size > 0){
					member.location -= delta_size;
				}
				member.set_parent(id, member.location);
			}
		}
	}
	/**
	 * Sets id_branch, and propagates change to children
	 * @param new_id_branch
	 */
	public void set_id_branch(int new_id_branch){
		id_branch = new_id_branch;
		for(NetworkMember child: children){
			if(child == this){
				System.out.print(id);System.out.println(" is a child of itself");
			}
			child.set_id_branch(new_id_branch);
		}
	}
	/**
	 * Splits a NetworkMember into two at a location, for severing
	 * Updates network based on change
	 * @param split_loc
	 * @return
	 */
	public NetworkMember split(int split_loc){
		int this_parent_id = get_parent_id();
		NetworkMember new_member = this_list.add_member(this_parent_id,location);
		new_member.change_size(split_loc);
		for(NetworkMember child: children){
			if(child.location < split_loc){
				child.set_parent(new_member.id, child.location);
			}
		}
		set_parent(0,0);
		change_size(size - split_loc);
		return new_member;
	}
	/**
	 * Gets parent id if exists, otherwise 0
	 * @return
	 */
	public int get_parent_id(){
		if(parent != null){
			return parent.id;
		}
		return 0;
	}
	/**
	 * Updates location on parent
	 * Updates network based on change
	 * @param new_location
	 */
	public void change_location(int new_location){
		location = new_location;
		set_parent(get_parent_id(),location);
	}
}
