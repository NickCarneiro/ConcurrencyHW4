package server;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Thread safe representation of a movie theatre
 */
public class Theater {
	//ArrayList of seat numbers
	//All pushes and pops are synchronized
	//behaves like the ticket distributor in bakery.
	private ArrayList<Integer> seats;

	//map names -> seat numbers
	//O(1) search to find seats reserved by given names
	//reads and writes don't need to be in synchronized blocks because
	//every process is guaranteed a unique seat via synchronization 
	//of access to the ArrayList and it is never modified structurally.
	private HashMap<String, Integer> names;

	public Theater(Integer seat_count){
		this.seats = new ArrayList<Integer>(seat_count);
		this.names = new HashMap<String, Integer>(seat_count);
		//for this simple program we'll make seat numbers integers,
		//but there's no reason this couldn't be an ArrayList of Strings
		//like "Section 27 Row 10 Seat 3"
		for(int i = 0; i < seat_count; i++){
			seats.add(i);
		}
	}

	/*
	 * returns positive integer seat number if available
	 * if no seats available, return -1
	 * O(1)
	 */

	private synchronized Integer getSeat(){
		if(seats.size() > 0){
			return seats.remove(0);
		} else {
			return -1;
		}
	}

	/*
	 * return seat_number if it is available,
	 * otherwise return -1
	 * O(n) search for seat. Most arenas don't have more than 100k seats.
	 */
	private synchronized Integer getSeat(Integer seat_number){
		//See if requested seat is available
		for(int i = 0; i < seats.size(); i++){
			if(seats.get(i).equals(seat_number)){
				return seats.remove(i);
			}
		}

		return -1;
	}

	/*
	 * attempts to reserve a seat for
	 */
	public String reserveSeat(String name){

			if(names.get(name) != null){
				return "Seat already booked against the name provided.";
			} else {
				int my_seat = getSeat();
				if(my_seat != -1){
					names.put(name, my_seat);
					return "Seat assigned to you is " + my_seat;
				} else {
					return "Sold out - No seat available.";
				}
			}
	}

	public String reserveSeat(String name, Integer seat_number){
		if(names.get(name) != null){
			return "Seat already booked against the name provided.";
		} else {
			int my_seat = getSeat(seat_number);
			if(my_seat != -1){
				names.put(name, my_seat);
				return "Seat assigned to you is " + my_seat;
			} else {
				return seat_number + " is not available";
			}
		}
	}

	/*
	 * O(n) lookup in names HashMap
	 */
	public String searchName(String name){
		Integer seat = names.get(name);

		if(seat != null){
			return seat.toString();
		} else {
			return "No reservation found for " + name;
		}
	}

	public String deleteReservation(String name){
		Integer seat = names.get(name);
		if(seat == null){
			return "No reservation found for " + name;
		} else {
			removeSeat(name, seat);
			return seat.toString();
		}
	}

	private synchronized void removeSeat(String name, Integer seat_number){
		//first delete the name -> seat hash map entry
		names.remove(name);
		//then add the seat number back into our array
		seats.add(seat_number);
	}
}

