package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import flight.City;
import flight.Flight;
import flight.FlightStatus;
import user.Admin;
import user.Order;
import user.OrderStatus;
import user.Passenger;
import user.User;

/** 
 * This is to provide a access to write file
 * use constructor DataManager(MainServer server) to trace
 * use saveData() to save to file
 */
/* file format:
 * <root>
 * 	<user>
 * 		<admin id=>
 * 			<username></username>
 * 			<passhash></passhash>
 * 		</admin>
 * 		<passenger id=>
 * 			<username></username>
 * 			<passhash></passhash>
 * 			<idnumber></idnumber>
 * 			<order>
 * 				<item>
 * 					<flightid></flightid>
 * 					<seat></seat>
 * 					<date></date>
 * 					<status></status>
 * 				</item>
 * 			</order>
 * 		</passenger>
 * 	</user>
 * 	<flight>
 * 		<item id=>
 *			<flightname></flightname>
 *			<starttime><starttime>
 *			<arrivetime><arrivetime>
 *			<startcity></startcity>
 *			<arrivecity></arrivecity>
 *			<price></price>
 *			<seatcapacity></seatcapacity>
 *			<status></status>
 * 		</item>
 * 	</flight>
 * 	<city>
 * 		<cityname id=></cityname>
 * 	</city>
 * </root>
 */
public class DataManager {
	
	// DONE(Dong) This class is all of my job :(
	public ArrayList<User> users;
	public ArrayList<Flight> flights;
	public ArrayList<City> cities;
	public static final int SYNC_INTERVAL = 120; // unit: (s)
	private final String filename = "data.xml";
	private File file;
	private Document document;
	private int usersHash;
	private int flightsHash;
	private int citiesHash;
	private SaveFileTask save;
	
	class SaveFileTask extends TimerTask {
		
		@Override
		public synchronized void run() {
			try {
				saveData();
			} catch (FileNotFoundException e) {
				try {
					init();
				} catch (IOException e1) {
					System.err.println("Write data error!");
					System.exit(-1);
				}
			}
		}
		
	}
	
	public DataManager() {
		try {
			init();
		} catch (IOException e) {
			System.err.println("Read/write data error!");
			System.exit(-1);
		}
		usersHash = users.hashCode();
		flightsHash = flights.hashCode();
		citiesHash =  cities.hashCode();
		save = new SaveFileTask();
		
	}
	
	public Flight getFlightByID(int flightID) {
		// DONE(Zhu) searchFlightByID
		for (Flight flight : flights) {
			if(flight.getFlightID()==flightID){
				return flight;
			}
		}
		return null;
	}
	
	public User getUserByID(int userID) {
		// DONE(Zhu) searchUserByID
		for (User user : users) {
			if(user.getID()==userID){
				return user;
			}
		}
		return null;
	}
	
	public City getCityByID(int cityID) {
		// DONE(Zhu) searchCityByID
		for (City city : cities) {
			if (city.getCityID()==cityID) {
				return city;
			}
		}
		return null;
	}
	
	public void saveData() throws FileNotFoundException {
		if (usersHash != users.hashCode()) {
			usersHash = users.hashCode();
			//Users
			Element euser = document.createElement("user");
			document.getDocumentElement().replaceChild(euser, document.getElementsByTagName("user").item(0));
			Element adminEgg;
			Element passengerEgg;
			adminEgg = document.createElement("admin");
			adminEgg.appendChild(document.createElement("username"));
			adminEgg.appendChild(document.createElement("passhash"));
			passengerEgg = document.createElement("passenger");
			passengerEgg.appendChild(document.createElement("username"));
			passengerEgg.appendChild(document.createElement("passhash"));
			passengerEgg.appendChild(document.createElement("idnumber"));
			passengerEgg.appendChild(document.createElement("order"));
			for (User user : users) {
				if (user instanceof Admin) {
					Admin admin = (Admin) user;
					Element node = (Element) adminEgg.cloneNode(true);
					node.setAttribute("uid", String.valueOf(admin.getID()));
					NodeList nodes = node.getChildNodes();
					nodes.item(0).appendChild(document.createTextNode(admin.getUserName()));
					nodes.item(1).appendChild(document.createTextNode(admin.getPassHash()));
					euser.appendChild(nodes.item(0).getParentNode());
				} else if (user instanceof Passenger) {
					Passenger passenger = (Passenger) user;
					Element node = (Element) passengerEgg.cloneNode(true);
					node.setAttribute("uid", String.valueOf(passenger.getID()));
					NodeList nodes = node.getChildNodes();
					nodes.item(0).appendChild(document.createTextNode(passenger.getUserName()));
					nodes.item(1).appendChild(document.createTextNode(passenger.getPassHash()));
					nodes.item(2).appendChild(document.createTextNode(passenger.getIdentityID()));
					Element orderEgg = document.createElement("item");
					orderEgg.appendChild(document.createElement("flightid"));
					orderEgg.appendChild(document.createElement("seat"));
					orderEgg.appendChild(document.createElement("date"));
					orderEgg.appendChild(document.createElement("status"));
					for (Order order : passenger.getOrderList()) {
						NodeList item = orderEgg.cloneNode(true).getChildNodes();
						item.item(0)
								.appendChild(document.createTextNode(String.valueOf(order.getFlight().getFlightID())));
						item.item(1).appendChild(document.createTextNode(String.valueOf(order.getSeat())));
						item.item(2)
								.appendChild(document.createTextNode(String.valueOf(order.getCreatDate().getTime())));
						item.item(3).appendChild(document.createTextNode(String.valueOf(order.getStatus().name())));
						nodes.item(4).appendChild(item.item(0).getParentNode());
					}
					euser.appendChild(nodes.item(0).getParentNode());
				}
			}
			adminEgg = null;
			passengerEgg = null;
		}
		if (flightsHash != flights.hashCode()) {
			flightsHash = flights.hashCode();
			//flight
			Element eflight = document.createElement("flight");
			document.getDocumentElement().replaceChild(eflight, document.getElementsByTagName("flight").item(0));
			Element flightEgg;
			flightEgg = document.createElement("item");
			flightEgg.appendChild(document.createElement("flightname"));
			flightEgg.appendChild(document.createElement("starttime"));
			flightEgg.appendChild(document.createElement("arrivetime"));
			flightEgg.appendChild(document.createElement("startcity"));
			flightEgg.appendChild(document.createElement("arrivecity"));
			flightEgg.appendChild(document.createElement("price"));
			flightEgg.appendChild(document.createElement("seatcapacity"));
			flightEgg.appendChild(document.createElement("status"));
			for (Flight f : flights) {
				Element node = (Element) flightEgg.cloneNode(true);
				node.setAttribute("fid", String.valueOf(f.getFlightID()));
				NodeList nodes = node.getChildNodes();
				nodes.item(0).appendChild(document.createTextNode(f.getFlightName()));
				nodes.item(1).appendChild(document.createTextNode(String.valueOf(f.getStartTime().getTime())));
				nodes.item(2).appendChild(document.createTextNode(String.valueOf(f.getArriveTime().getTime())));
				nodes.item(3).appendChild(document.createTextNode(String.valueOf((f.getStartCity().getCityID()))));
				nodes.item(4).appendChild(document.createTextNode(String.valueOf((f.getArriveCity().getCityID()))));
				nodes.item(5).appendChild(document.createTextNode(String.valueOf(f.getPrice())));
				nodes.item(6).appendChild(document.createTextNode(String.valueOf(f.getSeatCapacity())));
				nodes.item(7).appendChild(document.createTextNode(f.getFlightStatus().name()));
				eflight.appendChild(nodes.item(0).getParentNode());
			}
			flightEgg = null;
		}
		if (citiesHash != cities.hashCode()) {
			citiesHash = cities.hashCode();
			//city
			Element ecity = document.createElement("city");
			document.getDocumentElement().replaceChild(ecity, document.getElementsByTagName("city").item(0));
			for (City c : cities) {
				Element city = (Element) ecity.appendChild(document.createElement("item"))
						.appendChild(document.createTextNode(c.getCityName())).getParentNode();
				city.setAttribute("cid", String.valueOf(c.getCityID()));
			}
		}
		saveDocument();
	}

	private void init() throws IOException {
		flights = new ArrayList<>();
		users = new ArrayList<>();
		cities = new ArrayList<>();
		User.ID = 0;
		Flight.ID = 0;
		City.ID = 0;
		try {
			file = new File(filename);
			if (!file.exists()) {	
				file.createNewFile();
				Admin admin = new Admin("Admin", "admin");
				users.add(admin);
				City shenz = new City("Shenzhen");
				City beij = new City("Beijing");
				City zhenz = new City("Zhengzhou");
				cities.add(zhenz);
				cities.add(beij);
				cities.add(shenz);
				Flight flight1 = new Flight("A001",
						Flight.calendar(2017, 4, 1, 9, 30, 0),
						Flight.calendar(2017, 4, 1, 10, 40, 0), shenz, beij, 1200, 120);
				Flight flight2 = new Flight("A002",
						Flight.calendar(2017, 5, 2, 9, 12, 0),
						Flight.calendar(2017, 5, 2, 10, 42, 0), beij, shenz, 1200, 120);
				Flight flight3 = new Flight("A003",
						Flight.calendar(2017, 3, 3, 16, 12, 00),
						Flight.calendar(2017, 3, 3, 16, 52, 00), zhenz, shenz, 1200, 120);
				flights.add(flight1);
				flights.add(flight2);
				flights.add(flight3);
				// TODO(Zhu) add remain
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Element root = document.createElement("root");
				document.appendChild(root);
				Element euser = document.createElement("user");
				Element eflight = document.createElement("flight");
				Element ecity = document.createElement("city");
				root.appendChild(euser);
				root.appendChild(ecity);
				root.appendChild(eflight);
				saveData();
			} else {
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
				readData();
			}
		} catch (FileNotFoundException | SAXException e) {
			file.delete();
			init();
		} catch (ParserConfigurationException e) {
		}
	}

	private void readData() throws SAXException, FileNotFoundException, IOException {
		try {
			Element root = document.getDocumentElement();
			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeType() == Node.ELEMENT_NODE && ((Element)list.item(i)).getTagName().equals("city")) {
					NodeList cityList = ((Element) list.item(i)).getChildNodes();
					int MAX_ID = 0;
					for (int j = 0; j < cityList.getLength(); j++) {
						if (cityList.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) cityList.item(j);
							City.ID = Integer.parseInt(e.getAttribute("cid"));
							MAX_ID = City.ID > MAX_ID ? City.ID : MAX_ID;
							cities.add(new City(e.getTextContent()));
						}
					}
					City.ID = MAX_ID;
				}
			}
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeType() == Node.ELEMENT_NODE && ((Element)list.item(i)).getTagName().equals("flight")) {
					NodeList flightList = ((Element) list.item(i)).getChildNodes();
					int MAX_ID = 0;
					for (int j = 0; j < flightList.getLength(); j++) {
						if (flightList.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) flightList.item(j);
							Flight.ID = Integer.parseInt(e.getAttribute("fid"));
							MAX_ID = Flight.ID > MAX_ID ? Flight.ID : MAX_ID;
							Flight flight;
							flight = new Flight(
								e.getElementsByTagName("flightname").item(0).getTextContent(),
								new Date(Long.parseLong(e.getElementsByTagName("starttime").item(0).getTextContent())),
								new Date(Long.parseLong(e.getElementsByTagName("arrivetime").item(0).getTextContent())),
								getCityByID(Integer.parseInt(e.getElementsByTagName("startcity").item(0).getTextContent())),
								getCityByID(Integer.parseInt(e.getElementsByTagName("arrivecity").item(0).getTextContent())),
								Integer.parseInt(e.getElementsByTagName("price").item(0).getTextContent()),
								Integer.parseInt(e.getElementsByTagName("seatcapacity").item(0).getTextContent()));
							flight.setFlightStatus(FlightStatus.valueOf(e.getElementsByTagName("status").item(0).getTextContent()));
							flights.add(flight);
						}
					}
					Flight.ID = MAX_ID;
				}
			}
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeType() == Node.ELEMENT_NODE && ((Element)list.item(i)).getTagName().equals("user")) {
					Element user = (Element) list.item(i);
					NodeList userlist = user.getChildNodes();
					int MAX_ID = 0;
					for (int j = 0; j < userlist.getLength(); j++) {
						if (userlist.item(j).getNodeType() == Node.ELEMENT_NODE && userlist.item(j).getNodeName() != null) {
							Element u = (Element) userlist.item(j);
							User.ID = Integer.parseInt(u.getAttribute("uid"));
							MAX_ID = User.ID > MAX_ID ? User.ID : MAX_ID;
							if (u.getTagName().equals("admin")) {
								users.add(new Admin(
										u.getElementsByTagName("username").item(0).getTextContent(),
										u.getElementsByTagName("passhash").item(0).getTextContent(),
										true));
							} else if (u.getTagName().equals("passenger")) {
								Passenger p = new Passenger(
										u.getElementsByTagName("idnumber").item(0).getTextContent(),
										u.getElementsByTagName("username").item(0).getTextContent(),
										u.getElementsByTagName("passhash").item(0).getTextContent(),
										true);
								NodeList orders = u.getElementsByTagName("order").item(0).getChildNodes();
								for (int k = 0; k < orders.getLength(); k++) {
									if (orders.item(k).getNodeType() == Node.ELEMENT_NODE && orders.item(j).getNodeName() != null) {
										Element o = (Element) orders.item(k);
										Order order = new Order(p,
												getFlightByID(Integer.parseInt(o.getElementsByTagName("flightid").item(0).getTextContent())),
												Integer.parseInt(o.getElementsByTagName("seat").item(0).getTextContent()));
										order.setStatus(OrderStatus.valueOf(o.getElementsByTagName("status").item(0).getTextContent()));
										order.setCreatDate(new Date(Long.parseLong(o.getElementsByTagName("date").item(0).getTextContent())));
										p.addOrder(order);
									}
								}
								users.add(p);
							}
						}
					}
					User.ID = MAX_ID;
				}
			}
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			file.delete();
			init();
		}
	}
	
	private void saveDocument() throws FileNotFoundException {
        try {
        	Transformer transformer = TransformerFactory.newInstance().newTransformer();
        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        	DOMSource source=new DOMSource();
        	source.setNode(document);
        	StreamResult result=new StreamResult();
        	result.setOutputStream(new FileOutputStream(file));
			transformer.transform(source, result);
		} catch (TransformerException e) {
		}
	}
		
}
