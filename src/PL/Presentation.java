package PL;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import BL.B_LOGIC;
import DAL.Data_Layer;

public class Presentation extends JFrame implements ActionListener {
    private B_LOGIC BLO;
    
    private static final long serialVersionUID = 1L;
    private JButton importButton, save, create, update, delete, view, searchButton;
    private JTextArea text;  
    private JTextField searchBar;
    private JComboBox<String> font;
    Integer[] fontsize = {12, 16, 20};
    private JComboBox<Integer> size;
    String[] fontStyles = {"Arial", "Times New Roman", "Courier New"};
    private boolean isContentSaved;
    private String fileContent;
    private String fileName;
    private JFileChooser fileChooser;
    private JTextArea inputText;
    
    public Presentation() {
        BLO = new B_LOGIC(new Data_Layer());


        setTitle("Text File Import");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        text = new JTextArea();  
        importButton = new JButton("Import");
        save = new JButton("Save");
        create = new JButton("Create");
        update = new JButton("Update");
        delete = new JButton("Delete");
        view = new JButton("View");
        searchButton = new JButton("Search");

      
        font = new JComboBox<>(fontStyles);
        size = new JComboBox<>(fontsize);
        searchBar = new JTextField(15); 
        inputText = new JTextArea();
        JScrollPane pane = new JScrollPane(inputText);
        
        JScrollPane scrollPane = new JScrollPane(text);  
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(new JLabel("Font:"));
        topPanel.add(font);
        topPanel.add(new JLabel("Font Size:"));
        topPanel.add(size);

        
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchBar);
        topPanel.add(searchButton);

        topPanel.add(save);
        topPanel.add(delete);
        topPanel.add(importButton);
        topPanel.add(update);
        topPanel.add(create);
        topPanel.add(view);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);  
        add(pane, BorderLayout.CENTER); 
		
        save.addActionListener(this);
        delete.addActionListener(this);
        create.addActionListener(this);
        update.addActionListener(this);
        view.addActionListener(this);
        searchButton.addActionListener(this);

        setVisible(true);

        save.addActionListener(new ActionListener() {
           
        	@Override
			public void actionPerformed(ActionEvent e) {
				if (isContentSaved) {
                    JOptionPane.showMessageDialog(null, "Content has already been saved to the database.");
                    return;
                }

                try {
                    String result = BLO.saveImportedFileToDatabase(fileContent, fileName);
                    JOptionPane.showMessageDialog(null, result);
                    isContentSaved = true;
                    text.setText("");
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                }
			}
        });

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));

        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        fileContent = BLO.importTextFile(selectedFile);  

                        if (fileContent.startsWith("Invalid")) {
                            JOptionPane.showMessageDialog(null, fileContent);
                        } else {
                       
                            text.setText(fileContent);  
                            fileName = selectedFile.getName();  
                            isContentSaved = false;
                            JOptionPane.showMessageDialog(null, "File imported successfully.");
                        }
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "Error reading the file: " + e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }
        });
        font.addActionListener(e -> updateTextAreaFont());
	     size.addActionListener(e -> updateTextAreaFont());
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String searchTerm = searchBar.getText();
            if (!searchTerm.isEmpty()) {
                searchInTextArea(searchTerm);
            }
        }else if(e.getSource() == create){
        	try {
				saveFile(inputText.getText());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }else if(e.getSource() == delete) {
        	delfile();
        }
    }
    
    
    private void updateTextAreaFont() {
	    String selectedFont = (String) font.getSelectedItem();
	    Integer selectedSize = (Integer) size.getSelectedItem();
	    if (selectedFont != null && selectedSize != null) {
	        inputText.setFont(new Font(selectedFont, Font.PLAIN, selectedSize));
	    }
   }

    private void searchInTextArea(String searchTerm) {
        String content = text.getText();
        int index = content.indexOf(searchTerm);

        if (index != -1) {
            text.requestFocus();
            text.select(index, index + searchTerm.length());
        } else {
            JOptionPane.showMessageDialog(this, "Search term not found");
        }
    }

    public void saveFile(String content) throws SQLException {
        String fileName = JOptionPane.showInputDialog(null, "Enter file name:", "Save File", JOptionPane.PLAIN_MESSAGE);
        
        if (fileName == null || fileName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "File not saved. No file name entered.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
        	boolean res= BLO.createfile(fileName, content);	 
        	if(res==true) {
        		 JOptionPane.showMessageDialog(null, "File saved", "Information", JOptionPane.INFORMATION_MESSAGE);
        	}
        	else {
        		JOptionPane.showMessageDialog(null, "Error! File can not be saved", "Information", JOptionPane.INFORMATION_MESSAGE);
        	}
          }
	}
    
    public void delfile() {
		
		String fileName = JOptionPane.showInputDialog(null, "Enter file name:", "Delete File", JOptionPane.PLAIN_MESSAGE);
		if (fileName == null || fileName.trim().isEmpty()) {
			boolean res= BLO.deleteFile(fileName);
			if(res==true) {
				JOptionPane.showMessageDialog(null, "File deleted", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(null, "Error! File can not be deleted", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
   
}
