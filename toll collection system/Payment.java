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
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;

public class Payment extends JFrame implements ActionListener, ItemListener {
	private Home home;
	private Frame parent;
	private String place = "Select";
	private String vehicle = "Select";
	Choice placeName = new Choice();
	Choice vehicleNumber = new Choice();

	public Payment(Home f) {
		super("Payment");
		setLayout(null);
		setSize(500,500);
		setLocation(400,100);

        ImageIcon logo = new ImageIcon("logo.png");
        this.setIconImage(logo.getImage());
		
		home = f;
		Label placeNameLabel = new Label("Place Name");
		Label vehiclenumberLabel = new Label("Vehicle Number");
		
		Button paymentButton = new Button("Pay");
		Button homeButton = new Button("Home");
		
		Font myFont = new Font("Serif",Font.BOLD,14);
		placeNameLabel.setFont(myFont);
		vehiclenumberLabel.setFont(myFont);
		
		placeName.setBackground(Color.LIGHT_GRAY);
		placeName.setForeground(Color.BLACK);
		vehicleNumber.setBackground(Color.LIGHT_GRAY);
		vehicleNumber.setForeground(Color.BLACK);
		paymentButton.setBackground(Color.DARK_GRAY);
		homeButton.setBackground(Color.DARK_GRAY);
		paymentButton.setForeground(Color.WHITE);
		homeButton.setForeground(Color.WHITE);
		
		add(placeNameLabel);add(vehiclenumberLabel);
		add(placeName);add(vehicleNumber);
		add(paymentButton);add(homeButton);
		
		placeNameLabel.setBounds(75,100,100,15);
		placeName.setBounds(185,100,215,24);
		vehiclenumberLabel.setBounds(75,150,100,15);
		vehicleNumber.setBounds(185,150,215,24);
		paymentButton.setBounds(300,224,100,40);
		homeButton.setBounds(185,224,100,40);
		
		paymentButton.addActionListener(this);
		homeButton.addActionListener(this);
		placeName.addItemListener(this);
		vehicleNumber.addItemListener(this);

		placeList();
	}

	// listens for button click
	public void actionPerformed(ActionEvent ae) {
		String clickedButton = ae.getActionCommand();
		String salutation = home.getUsername();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
   		LocalDateTime now = LocalDateTime.now(); 
		if(clickedButton.equals("Pay")) {
			// validation: checking for empty inputs
			if(place == "Select" || vehicle == "Select") {
				JOptionPane.showMessageDialog(this, "All the fields are required");
			} else {
				DbConnection da = new DbConnection();
				// collecting price to put in the transaction table
				String q = "SELECT price from toll WHERE place = '" + place + "'";
				ResultSet rs = null;		
				try {
					rs = da.getData(q);
					if(rs.next()){
						String amount = rs.getString("price");
						String q3 = "INSERT INTO transaction(placeName,vehiclenumber,username, amount, date) VALUES ('" + place + "','" + vehicle + "','" + salutation + "','" + amount + "','" + dtf.format(now) + "')";
						da.updateDB(q3);
						JOptionPane.showMessageDialog(this, "Payment Successful");
						// setting dropsown fields to default values
						vehicleList(home.getUsername());
						placeList();
					}
				} catch(Exception ex){
					JOptionPane.showMessageDialog(this,"DB Error");
				}				
			}
		}
		else if(clickedButton.equals("Home")) {
			// client home
			this.setVisible(false);
			home.profile.setVisible(true);
			home.profile.setParent(home);
		}
	}

	// showing dynamic place lists from DB as a dropdown
	public void placeList(){
		placeName.removeAll();
		placeName.add("Select");
		DbConnection da = new DbConnection();
		String q = "SELECT place from toll";
		ResultSet rs = null;		
		try {
			rs = da.getData(q);
			while(rs.next()){
				placeName.add(rs.getString("place"));
			}
		} catch(Exception ex){
			JOptionPane.showMessageDialog(this,"DB Error");
		}
	}

	// showing list of vehicles as dropdown
	public void vehicleList(String username){
		vehicleNumber.removeAll();
		vehicleNumber.add("Select");
		DbConnection da = new DbConnection();
		String q = "SELECT * from vehicle WHERE username = '" + username + "'";
		ResultSet rs = null;		
		try {
			rs = da.getData(q);
			while(rs.next()){
				vehicleNumber.add(rs.getString("vehiclenumber"));
			}
		} catch(Exception ex){
			JOptionPane.showMessageDialog(this,"DB Error");
		}
	} 

	// listens to dropdown value changes and handles exception by putting a default value
	public void itemStateChanged(ItemEvent ie){
		String v = (String)ie.getItem();
		if (ie.getSource() == placeName){
			try{
				place = v;
			} catch(Exception ex) {
				place = "Select";
			}
		} else if (ie.getSource() == vehicleNumber){
			try{
				vehicle = v;
			} catch(Exception ex) {
				vehicle = "Select";
			}
		}
	}
	
	public void setParent(Frame f) {parent=f;}
}