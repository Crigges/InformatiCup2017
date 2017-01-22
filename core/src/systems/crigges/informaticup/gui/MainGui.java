package systems.crigges.informaticup.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.LayoutStyle.ComponentPlacement;

import systems.crigges.informaticup.crawling.RepositoryCrawler;
import systems.crigges.informaticup.general.ClassifierConfiguration;
import systems.crigges.informaticup.general.RepositoryDescriptor;
import systems.crigges.informaticup.general.RepositoryTyp;
import systems.crigges.informaticup.io.InputFileReader;
import systems.crigges.informaticup.io.OutputFileWriter;
import systems.crigges.informaticup.io.RepoCacher;
import systems.crigges.informaticup.nnetwork.ClassifierNetwork;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class MainGui {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGui window = new MainGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 472, 279);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblNewLabel = new JLabel("Input File:");
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        int returnValue = fileChooser.showOpenDialog(frame);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		          File selectedFile = fileChooser.getSelectedFile();
		          textField.setText(selectedFile.getAbsolutePath());
		        }
			}
		});
		
		JLabel lblOutputfile = new JLabel("Output File");
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		
		JButton button = new JButton("...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        int returnValue = fileChooser.showSaveDialog(frame);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		          File selectedFile = fileChooser.getSelectedFile();
		          textField_1.setText(selectedFile.getAbsolutePath());
		        }
			}
		});
		
		JButton btnClassify = new JButton("Classify");
		btnClassify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textField.getText().equals("")){
					JOptionPane.showMessageDialog(frame, "Select an Input File");
				}else if(textField_1.getText().equals("")){
					JOptionPane.showMessageDialog(frame, "Select an Output File");
				}else{
					try {
						btnClassify.setEnabled(false);
						ClassifierConfiguration config = ClassifierConfiguration.getDefault();
						ClassifierNetwork neuralNetwork = ClassifierNetwork.loadFromFile(config.neuralNetworkLocation);
						InputFileReader reader = new InputFileReader(new File(textField.getText()));
						OutputFileWriter writer = new OutputFileWriter(new File(textField_1.getText()));
						List<RepositoryDescriptor> repos = reader.getRepositorysAndTypes();
						for(RepositoryDescriptor d : repos){
							try{
								RepositoryCrawler c = RepoCacher.get(d.getName());
								d.setType(neuralNetwork.classify(c.getCollectedDataSet()));
							}catch(Exception e1){
								d.setType(null);
							}
							writer.write(d);
						}
						writer.close();
						btnClassify.setEnabled(true);
						JOptionPane.showMessageDialog(frame, "Classification Successful!");
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, "Classification Failed!");
						e1.printStackTrace();
					}
				}
			}
		});
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(textField, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblNewLabel)
						.addComponent(lblOutputfile, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(textField_1, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(button, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnClassify, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addGap(29))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewButton))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblOutputfile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnClassify)
					.addContainerGap(27, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
