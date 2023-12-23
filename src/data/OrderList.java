package data;

public class OrderList implements List<Order> {
	private Order[] orders;
	
	public OrderList(Order[] orders) {
		this.orders = orders;
	}
	
	@Override
	public Iterator<Order> iterator(){
		return new OrderIterator(orders);
	}
}
