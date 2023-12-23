package data;



import exceptions.StatusUnavailableException;

public class Aerolinea {

    private Flight flightSchedule;
    private Passenger passengerReservation;

    public Aerolinea() {
        flightSchedule = new Flight();
        passengerReservation = new Passenger(null, null, null);
    }

    public void addPassenger(Passenger passenger,int seat) throws StatusUnavailableException {
        flightSchedule.addPassenger(passenger,seat);
    }

    public void reserverFlight(Flight flight) throws StatusUnavailableException {
        passengerReservation.reserveFlight(flight);
    }

    public void removePassenger(Passenger passenger) throws StatusUnavailableException {
        flightSchedule.removePassenger(passenger);
    }

    public int getFlights() {
        return flightSchedule.getFlightID();
    }

    public String getPassengers() {
        return passengerReservation.getIdentityID();
    }
}

    // demas metodos que se puedan manejar directamente de esta clase
