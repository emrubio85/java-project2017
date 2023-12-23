package data;

public class OrderDecorator implements Iorder{
	protected Order order;
	
	public OrderDecorator(Order order) {
		this.order = order;
	}

	@Override
	public void calculatePrice() {
		this.order.calculatePrice();
		
	}
		
}
