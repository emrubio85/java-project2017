package data;

public class OrderIterator implements Iterator<Order> {
	private Order[] orders;
    private int position;
     
    public OrderIterator(Order[] orders)
    {
        this.orders = orders;
        position = 0;
    }
 
  @Override
  public void reset() {
    position = 0;
  }
 
  @Override
  public Order next() { 
    return orders[position++];
  }
 
  @Override
  public Order currentItem() {
    return orders[position];
  }
 
  @Override
  public boolean hasNext() {
    if(position >= orders.length)
            return false;
        return true;
  }


}
