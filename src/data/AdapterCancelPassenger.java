package data;

public class AdapterCancelPassenger extends Passenger implements Cancelar{

	public AdapterCancelPassenger(String identityID, String realName, String password) {
		super(identityID, realName, password);
		
	}
	@Override
    public void cancel(Order order) {
        super.removeOrder(order);
    }
}
