package data;

import java.util.ArrayList;


import exceptions.StatusUnavailableException;

public class Passenger extends User   {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7622300935345582710L;
	private String identityID; // XXX this should not be changed?
	protected ArrayList<Order> orderList;

	
	public Passenger(String identityID2, String realName, String password) {
		
	}

	public String getIdentityID() {
		return identityID;
	}
	
	
	
	public void reserveFlight(Flight flight) throws StatusUnavailableException {
		if (flight.getPassagers().containsKey(this)) {
			throw new StatusUnavailableException("has already reserved");			
		}
		Order order = new Order(this, flight);
		this.addOrder(order);
	}
	
	public void addOrder(Order order) {
		orderList.add(order);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Order> getOrderList() {
		return (ArrayList<Order>) orderList.clone();
	}
	
	public boolean removeOrder(Order order){
		return orderList.remove(order);
	}

}
