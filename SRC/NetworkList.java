package main;

import java.util.ArrayList;
/**
 * Tracks and Creates NetworkMembers
 * @author Aaron Hall
 */
public class NetworkList {
	public int id;
	public static int network_count = 0;
	public ArrayList<NetworkMember> member_list;
	public int member_ids;
	
	public NetworkList(){
		network_count++;
		id = network_count;
		member_list = new ArrayList<NetworkMember>();
		member_ids = 0;
	}
	
	/**
	 * Create and return a NetworkMember with default parameters
	 * @return
	 */
	public NetworkMember add_member(){
		return add_member(0,0);
	}
	/**
	 * Create and return a NetworkMember, specifying a parent id and location on parent
	 * @param parent
	 * @param location
	 * @return
	 */
	public NetworkMember add_member(int parent, int location){
		return add_member(parent,location,new_id());
	}
	/**
	 * Create and return a NetworkMember, specifying parent id, location on parent, and the new member's id
	 * @param parent
	 * @param location
	 * @param this_id
	 * @return
	 */
	public NetworkMember add_member(int parent, int location, int this_id){
		NetworkMember new_member = new NetworkMember(parent, location, this_id, this);
		member_list.add(new_member);
		member_ids = this_id;
		return new_member;
	}
	/**
	 * Get a NetworkMember with specified id, n. Otherwise returns null
	 * @param n
	 * @return
	 */
	public NetworkMember get_member(int n){
		for(NetworkMember m: member_list){
			if(m.id == n) return m;
		}
		return null;
	}
	/**
	 * Generate a new id for a NetworkMember, will increment previous largest id
	 * @return
	 */
	 public int new_id(){
		 member_ids++;
		 return Integer.valueOf(member_ids);
	 }
	 /**
	  * Clear members, reset id counter
	  */
	 public void clear(){
		 member_list.clear();
		 member_ids = 0;
	 }
	 
	 /**
	 * Unit Testing
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkList list = new NetworkList();
		NetworkMember mem1 = list.add_member();
		mem1.change_size(420);
		NetworkMember mem2 = list.add_member(1,64);
		mem2.change_size(405);
		NetworkMember mem3 = list.add_member(1,154);
		mem3.change_size(267);
		NetworkMember mem4 = list.add_member(1,160);
		mem4.change_size(278);
		NetworkMember mem5 = list.add_member(1,183);
		mem5.change_size(230);
		NetworkMember mem6 = list.add_member(1,196);
		mem6.change_size(209);
		NetworkMember mem7 = list.add_member(1,205);
		mem7.change_size(220);
		NetworkMember mem8 = list.add_member(3,50);
		mem8.change_size(159);
		NetworkMember mem9 = list.add_member(3,150);
		mem9.change_size(136);
		NetworkMember mem10 = list.add_member(3,200);
		mem10.change_size(178);
		mem3.split(153);
		//mem1.change_size(216);
		//mem1.change_size(500);
		NetworkMember mem11 = list.add_member(10,10);
		mem11.change_size(100);
		for(NetworkMember member: list.member_list){
			System.out.print("id ");System.out.println(member.id);
			System.out.print("id_branch ");System.out.println(member.id_branch);
			System.out.print("size ");System.out.println(member.size);
			if(member.parent != null){
				System.out.print("parent id ");System.out.println(member.parent.id);
				System.out.print("location ");System.out.println(member.location);
				System.out.print("root ");System.out.println(member.root);
			}
			System.out.println("children ids: ");
			for(NetworkMember child: member.children){
				System.out.println(child.id);
			}
			System.out.println("---");
		}
	}
}
