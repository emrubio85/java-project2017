package data;

public class OrderDescuento extends OrderDecorator{
	
	public OrderDescuento(Order order) {
		super(order);
	}
	
	@Override
	public void calculatePrice() {
		super.calculatePrice();
		System.out.print("se añadio el descuento");
	} 
}
