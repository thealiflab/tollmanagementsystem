import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.*;

public class AddVehicle extends JFrame implements ActionListener, ItemListener {
	private TextField brandName,  vehicleModel, vehicleNumber;
	private Home home;
	private Frame parent;
	
	Choice vehicleClassChoice = new Choice();
	Choice vehicleTypeChoice = new Choice();
	String vClass;
	String vType;
	
	public AddVehicle(Home f) {
		super("Add Vehicle");
		setLayout(null);
		setSize(500,500);
		setLocation(400,100);

        ImageIcon logo = new ImageIcon("logo.png");
        this.setIconImage(logo.getImage());
		
		home = f;
		Label brandNameLabel = new Label("Brand Name");
		Label vehicleModelLabel = new Label("Vehicle Model");
		Label vehicleNumberLabel = new Label("Vehicle Number");
		Label vehicleTypeLabel = new Label("Vehicle Type");
		
		brandName = new TextField(12);
		vehicleModel = new TextField(12);
		vehicleNumber = new TextField(12);
		
		Button submitButton = new Button("Add Vehicle");
		Button homeButton = new Button("Home");
		
		Font myFont = new Font("Serif",Font.BOLD,14);
		brandNameLabel.setFont(myFont);
		vehicleModelLabel.setFont(myFont);
		vehicleNumberLabel.setFont(myFont);
		vehicleTypeLabel.setFont(myFont);
		
		brandName.setBackground(Color.LIGHT_GRAY);
		brandName.setForeground(Color.BLACK);
		vehicleModel.setBackground(Color.LIGHT_GRAY);
		vehicleModel.setForeground(Color.BLACK);
		vehicleNumber.setBackground(Color.LIGHT_GRAY);
		vehicleNumber.setForeground(Color.BLACK);
		
		submitButton.setBackground(Color.DARK_GRAY);
		homeButton.setBackground(Color.DARK_GRAY);
		submitButton.setForeground(Color.WHITE);
		homeButton.setForeground(Color.WHITE);
		
		add(brandNameLabel);add(brandName);
		add(vehicleModelLabel);add(vehicleModel);
		add(vehicleNumberLabel);add(vehicleNumber);
		add(vehicleTypeLabel);
		add(submitButton);add(homeButton);
		
		brandNameLabel.setBounds(75,42,100,15);
		brandName.setBounds(185,40,215,24);
		vehicleModelLabel.setBounds(75,84,100,15);
		vehicleModel.setBounds(185,80,215,24);
		vehicleNumberLabel.setBounds(75,124,100,15);
		vehicleNumber.setBounds(185,120,215,24);
		vehicleTypeLabel.setBounds(75,164,100,15);
		submitButton.setBounds(300,224,100,40);
		homeButton.setBounds(185,224,100,40);
		
		submitButton.addActionListener(this);
		homeButton.addActionListener(this);

		regenerateVehicleTypeList();
		
		add(vehicleClassChoice);
		add(vehicleTypeChoice);
		
		vehicleClassChoice.setBounds(185,164,105,24);
		vehicleTypeChoice.setBounds(300,164,100,24);
		
		vehicleClassChoice.setBackground(Color.LIGHT_GRAY);
		vehicleClassChoice.setForeground(Color.BLACK);
		vehicleTypeChoice.setBackground(Color.LIGHT_GRAY);
		vehicleTypeChoice.setForeground(Color.BLACK);
		
		vehicleClassChoice.addItemListener(this);
		vehicleTypeChoice.addItemListener(this);
		
		
	}

	public void regenerateVehicleTypeList() {
		// genrating dropdown options for vehicle classes
		vehicleClassChoice.add("Vehicle Class");
		vehicleClassChoice.add("Public service");
		vehicleClassChoice.add("Private service");
		vehicleClassChoice.add("Motorcycle");
		// genrating dropdown options for vehicle types
		vehicleTypeChoice.add("Vehicle Type");
		vehicleTypeChoice.add("minibus");
		vehicleTypeChoice.add("touring coach");
		vehicleTypeChoice.add("school bus");
		vehicleTypeChoice.add("small");
		vehicleTypeChoice.add("up to 50 cc");
	}
	
	public void actionPerformed(ActionEvent ae) {
		String clickedButton = ae.getActionCommand();
		String salutation = home.getUsername();
		String vc = vehicleClassChoice.getSelectedItem();   
		String vt = vehicleTypeChoice.getSelectedItem();
		if(clickedButton.equals("Add Vehicle")) {
			// validation: checks for empty values
			if(brandName.getText().trim().length() == 0 || vehicleModel.getText().trim().length() == 0 || vehicleNumber.getText().trim().length() == 0 || vc == "Vehicle Class" || vt == "Vehicle Type") {
				JOptionPane.showMessageDialog(this, "All the fields are required");
			} else {
				DbConnection da = new DbConnection();
				// query for checking duplicate entry(vehiclenumber is unique)
				String q1 = "SELECT vehiclenumber from vehicle WHERE vehiclenumber = '" + vehicleNumber.getText().trim() + "'";
				ResultSet rs = null;		
				try {
					rs = da.getData(q1);
					if(rs.next()) {						
						JOptionPane.showMessageDialog(this, "Please enter a valid number");
					} else {		
						// all validation done: inserts data into DB				
						String q2 = "INSERT INTO vehicle(brandname, vehiclemodel, vehiclenumber, vehicleclass, vehicletype, username) VALUES ('" + brandName.getText().trim() + "','" + vehicleModel.getText() + "','" + vehicleNumber.getText().trim() + "','" + vClass + "','" + vType + "','"+ salutation +"')";
						da.updateDB(q2);
						JOptionPane.showMessageDialog(this, "Vehicle added successfully");
						
						// clearing form fields and setting them to default values
						brandName.setText("");
						vehicleModel.setText("");
						vehicleNumber.setText("");
						vehicleTypeChoice.removeAll();
						vehicleClassChoice.removeAll();
						regenerateVehicleTypeList();
					}
				}
				catch(Exception ex) {
					JOptionPane.showMessageDialog(this,"DB Error");
				}
			}
		}
		else if(clickedButton.equals("Home")) {
			// home screen for client
			this.setVisible(false);
			home.profile.setVisible(true);
			home.profile.setParent(home);
		}
	}
	
	// listening to dropdown selection changes and readying the values for validation and DB insertion
	public void itemStateChanged(ItemEvent ie) {
		String v = (String)ie.getItem();
		if(ie.getSource() == vehicleTypeChoice){
			try{
				vType = v;
			} catch(Exception ex) {
				vType = "1";
			}
		}
		else if(ie.getSource() == vehicleClassChoice) {
			try {
				vClass = v;
				vehicleTypeChoice.removeAll();
				if(vClass == "Vehicle Class") {
					vehicleTypeChoice.add("Vehicle Type");
					vehicleTypeChoice.add("minibus");
					vehicleTypeChoice.add("touring coach");
					vehicleTypeChoice.add("school bus");
					vehicleTypeChoice.add("small");
					vehicleTypeChoice.add("up to 50 cc");
				} else if(vClass == "Public service") {
					vehicleTypeChoice.add("Vehicle Type");
					vehicleTypeChoice.add("minibus");
					vehicleTypeChoice.add("touring coach");
					vehicleTypeChoice.add("school bus");
				} else if (vClass == "Private service") {
					vehicleTypeChoice.add("Vehicle Type");
					vehicleTypeChoice.add("minibus");
					vehicleTypeChoice.add("touring coach");
					vehicleTypeChoice.add("school bus");
				} else if(vClass == "Motorcycle") {
					vehicleTypeChoice.add("Vehicle Type");
					vehicleTypeChoice.add("small");
					vehicleTypeChoice.add("up to 50 cc");
				}
				vType = "1";
			} catch(Exception ex) {
				vClass = "1";
			}
		}
	}
	
	public void setParent(Frame f) {parent=f;}
}