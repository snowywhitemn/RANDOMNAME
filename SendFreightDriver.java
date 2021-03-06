package edu.metrostate.ics372.snowywhitemn;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.metrostate.ics372.snowywhitemn.Shipment.ShipmentMethod;

/**
 * Using j simple
 * 
 * @author vwhite
 *
 */
public class SendFreightDriver {

	public static void main(String[] args) {

		List<Shipment> shipments = new ArrayList<>();

		Scanner reader = new Scanner(System.in);
		System.out.println("Enter new shipments manually or from a file? (manually / file)");
		String answer = reader.nextLine();

		if (answer.toLowerCase().equals("manually")) {
			// prompts to create shipment by setting shipment variables // will have to add
			// to specified warehouse list
		} else if (answer.toLowerCase().equals("file")) {

			// prompt to enter file path of file to read in
			System.out.println("Enter file path containing shipments: ");
			String filePath = reader.nextLine();
			// check if valid file path
			// will have to shipments to specified warehouses
			// will be asking for local file path from user
			shipments = readInShipments(filePath);

		}
		// can make method for this as well where we pass in a list or can add it to the
		// read in shipments method
		for (Shipment shipment : shipments) {

			// get the warehouse
			Warehouse warehouse = WarehouseRepository.getInstance().getWarehouse(shipment.getWarehouseId());
			// add the shipment to the warehouse
			warehouse.addIncomingShipment(shipment);
		}

	}

	/**
	 * @param filePath
	 * @return
	 */
	public static List<Shipment> readInShipments(String filePath) {
		List<Shipment> shipments = new ArrayList<>();

		// https://www.javaguides.net/2019/07/jsonsimple-tutorial-read-and-write-json-in-java.html

		// JSON parser object to parse read file
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(filePath)) {

			try {
				// read JSON file
				Object obj = jsonParser.parse(reader);

				// convert object to JSONObject
				JSONObject jsonObject = (JSONObject) obj;

				JSONArray arrayOfShipments = (JSONArray) jsonObject.get("warehouse_contents");

				// check size if more than 0, there is objects inside the array
				System.out.println(arrayOfShipments.size());

				// create shipment objects
				Iterator i = arrayOfShipments.iterator();
				while (i.hasNext()) {
					// reading object in array from json file
					JSONObject innerObject = (JSONObject) i.next();

					// getting attributes of the object
					String warehouseId = innerObject.get("warehouse_id").toString();
					ShipmentMethod shipmentMethod = ShipmentMethod.valueOf((String) innerObject.get("shipment_method"));
					String shipmentId = (String) innerObject.get("shipment_id");
					var tempWeight = innerObject.get("weight");
					Long tempReceiptDate = (Long) innerObject.get("receipt_date");

					// convert receipt date from long to Date
					Date receiptDate = new Date(tempReceiptDate);

					// convert weight to Long
					Long weight = (long) 0.00;
					if (tempWeight instanceof Double) {
						weight = ((Double) tempWeight).longValue();
					} else if (tempWeight instanceof Long) {
						weight = (Long) tempWeight;
					}

					// create a new shipment object
					Shipment shipment = new Shipment(warehouseId, shipmentMethod, shipmentId, weight);
					// add receipt date to shipment
					shipment.setReceiptDate(receiptDate);

					// use this as a check to make sure it is a valid shipment
					if (shipment.getReceiptDate() != null) {
						// add shipment to List
						shipments.add(shipment);
					}

					System.out.println(shipment);

				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("File was not found.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return shipments;

	}

}
