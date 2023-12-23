package data;

public class DirectorFlightBuilder {
	private FlightBuilder builder;
	
	public DirectorFlightBuilder(FlightBuilder builder) {
		this.builder = builder;
	}
	
	public void ContruirFlight() {
		builder.definirFlight();
	}
	
	public Flight getFlight() {
		return builder.getFlight();
	}
}
