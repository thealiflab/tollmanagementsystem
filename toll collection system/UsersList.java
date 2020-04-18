import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.*;  



public class UsersList extends JFrame implements ActionListener {
	private JTextField search;
	private Home home;
	private Frame parent;
	private JTable j;
	private DefaultTableModel tableModel;
	
	public UsersList(Home f) {
		super("Users List");
		
		setLayout(null);
		setSize(500,500);
		setLocation(400,100);
        ImageIcon logo = new ImageIcon("logo.png");
        this.setIconImage(logo.getImage());
		
		home = f;

		search = new JTextField (40);
		search.setHorizontalAlignment(SwingConstants.CENTER);
		search.setBackground(Color.LIGHT_GRAY);
		search.setForeground(Color.BLACK);

		Button searchButton = new Button("Search");
		Button homeButton = new Button("Home");
		Button logoutButton = new Button("Logout");

		searchButton.setBackground(Color.DARK_GRAY);
		homeButton.setBackground(Color.DARK_GRAY);
		logoutButton.setBackground(Color.DARK_GRAY);
		searchButton.setForeground(Color.WHITE);
		homeButton.setForeground(Color.WHITE);
		logoutButton.setForeground(Color.WHITE);
		
		add(search);
		add(searchButton);
		add(homeButton);
		add(logoutButton);
		
		homeButton.setBounds(10, 10, 100, 40);
		logoutButton.setBounds(120, 10, 100, 40);
		search.setBounds(230, 10, 150, 40);
		searchButton.setBounds(390, 10, 90, 40);
		
		searchButton.addActionListener(this);
		homeButton.addActionListener(this);
		logoutButton.addActionListener(this);

		String[] columnNames = { "User Name", "Full name", "Phone Number"};
        tableModel = new DefaultTableModel(columnNames, 0);
		DbConnection da = new DbConnection();
		String q = "SELECT username, fullname, phone from client WHERE admin=0";
		ResultSet rs = null;		
		try {
			rs = da.getData(q);
			while(rs.next()) {
				String a = rs.getString("username");
				String b = rs.getString("fullname");
				String c = rs.getString("phone");
				String[] data = {a, b, c}; // preparing each row
				tableModel.addRow(data); // pushing the row to table
			}
			if (tableModel.getRowCount() == 0) { // checking for empty table and showing no data labels
				String[] data = {"No data yet", "No data yet", "No data yet"} ;
				tableModel.addRow(data);
			}
		} catch(Exception ex){
			JOptionPane.showMessageDialog(this,"DB Error");
		}
        j = new JTable(tableModel); 
        j.setDefaultEditor(Object.class, null);
        j.setAutoCreateRowSorter(true);
        JScrollPane sp = new JScrollPane(j); 
        sp.setBounds(0, 60, 485, 440); 
        add(sp);


	}

	// listens to button clicks
	public void actionPerformed(ActionEvent ae){
		String clickedButton = ae.getActionCommand();
		if(clickedButton.equals("Search")){
			// clearing the current table for new data
			int rowCount = tableModel.getRowCount();
			for (int i = rowCount - 1; i >= 0; i--) {
			    tableModel.removeRow(i);
			}
			DbConnection da = new DbConnection();
			// search query
			String q = "SELECT * from client WHERE username LIKE '%" + search.getText().trim() + "%' OR fullname LIKE '%" + search.getText().trim() + "%' OR phone LIKE '%" + search.getText().trim() + "%'";
			ResultSet rs = null;		
			try {
				rs = da.getData(q);
				while(rs.next()){
					String a = rs.getString("username");
					String b = rs.getString("fullname");
					String c = rs.getString("phone");
					String[] data = {a, b, c}; // preparing each row
					tableModel.addRow(data); // pushing the row to table
				} 
				if (tableModel.getRowCount() == 0) { // checking for empty table and showing no data labels
					String[] data = {"No data found", "No data found", "No data found"} ;
					tableModel.addRow(data);
				}
			} catch(Exception ex){
				JOptionPane.showMessageDialog(this,"DB Error");
			}
		}
		else if(clickedButton.equals("Home")) {
			// admin home page
			home.admin.setVisible(true);
			home.admin.setParent(this);
			this.setVisible(false);
		}
		else if(clickedButton.equals("Logout")) {
			this.setVisible(false);
			home.setVisible(true);
		}
	}
	
	public void setParent(Frame f) {parent=f;}
}