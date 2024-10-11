package PL;

import BL.B_LOGIC;
import DAL.Data_Layer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Presentation extends JFrame implements ActionListener {

    private B_LOGIC BLO;
    private static final long serialVersionUID = 1L;
    private JButton importButton, save, create, update, delete, view, searchButton;
    private JTextArea text;  // This JTextArea will display the file content
    private JTextField searchBar;
    private JComboBox<String> font;
    Integer[] fontsize = {12, 16, 20};
    private JComboBox<Integer> size;
    String[] fontStyles = {"Arial", "Times New Roman", "Courier New"};
    private boolean isContentSaved;
    private String fileContent;
    private String fileName;
    private JFileChooser fileChooser;

    public Presentation() {
        BLO = new B_LOGIC(new Data_Layer());


        setTitle("Text File Import");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        text = new JTextArea();  // Initialize the JTextArea
        importButton = new JButton("Import");
        save = new JButton("Save");
        create = new JButton("Create");
        update = new JButton("Update");
        delete = new JButton("Delete");
        view = new JButton("View");
        searchButton = new JButton("Search");

        // Initialize font and size JComboBox
        font = new JComboBox<>(fontStyles);
        size = new JComboBox<>(fontsize);
        searchBar = new JTextField(15); // Search bar with 15 columns width

        JScrollPane scrollPane = new JScrollPane(text);  // Add JTextArea to scroll pane
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(new JLabel("Font:"));
        topPanel.add(font);
        topPanel.add(new JLabel("Font Size:"));
        topPanel.add(size);

        // Add the search bar and search button to the top panel
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
        add(scrollPane, BorderLayout.CENTER);  // Add scrollPane to the frame to display text content

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
                        fileContent = BLO.importTextFile(selectedFile);  // Now BLO is properly initialized

                        if (fileContent.startsWith("Invalid")) {
                            JOptionPane.showMessageDialog(null, fileContent);
                        } else {
                        	
                            text.setText(fileContent);  // Display content in JTextArea
                            fileName = selectedFile.getName();  // Store the file name for saving later
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String searchTerm = searchBar.getText();
            if (!searchTerm.isEmpty()) {
                searchInTextArea(searchTerm);
            }
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

    
}
