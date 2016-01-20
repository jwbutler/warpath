package warpath.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import warpath.core.Constants;
import warpath.core.Utils;
import warpath.units.UnitTemplate;

public class LoadSaveCharacterDialog extends JDialog implements ActionListener {
  private final static String NEW_SAVE_TEXT = "(New file)";
  
  private final CharacterCreatorNew cc;
  private final JScrollPane listPanel;
  private final JPanel buttonPanel;
  private final Vector<String> filenames;
  private final JList<String> fileList;
  private final JButton loadButton;
  private final JButton saveButton;
  private final JButton deleteButton;
  private final JButton cancelButton;
  private final UnitTemplate template;

  
  public LoadSaveCharacterDialog(CharacterCreatorNew cc, UnitTemplate template) {
    super();
    this.setPreferredSize(new Dimension(300,600));
    this.getRootPane().setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    pack();
    setLayout(null);

    File dir = new File(Constants.CHARACTER_SAVE_FOLDER);
    filenames = new Vector<String>();
    if (dir.exists() && dir.isDirectory()) {
      for (String s : dir.list()) {
        if (Utils.isSaveFile(s)) {
          filenames.add(s.substring(0, s.length()-4));
        }
      }
    } else {
      // error stuff
    }
    filenames.add(NEW_SAVE_TEXT);
    fileList = new JList<String>(filenames);
    fileList.setBackground(Color.WHITE);
    fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    Container cp = getContentPane();
    listPanel = new JScrollPane(fileList);
    listPanel.setBounds(0, 0, cp.getWidth(), (int)(cp.getHeight()*0.8));
    listPanel.setBackground(Color.RED);
    this.add(listPanel);
    
    buttonPanel = new JPanel();
    buttonPanel.setBounds(0, (int)(cp.getHeight()*0.8), cp.getWidth(), (int)(cp.getHeight()*0.2));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING));
    buttonPanel.setLayout(new GridLayout(2,2,Constants.MENU_PADDING,Constants.MENU_PADDING));
    buttonPanel.validate();
    
    
    loadButton = new JButton("Load");
    loadButton.addActionListener(this);
    buttonPanel.add(loadButton);
    
    saveButton = new JButton("Save");
    saveButton.addActionListener(this);
    buttonPanel.add(saveButton);
    
    deleteButton = new JButton("Delete");
    deleteButton.addActionListener(this);
    buttonPanel.add(deleteButton);
    
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    buttonPanel.add(cancelButton);
    
    this.add(buttonPanel);
    pack();
    
    this.cc = cc;
    this.template = template;
    
  }
  
  /**
   * Handler for the four buttons:
   * Load,
   * Save,
   * Delete,
   * Cancel.
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    String selection = fileList.getSelectedValue();
    switch (e.getActionCommand()) {
    case "Load":
      if ((selection != null) && !selection.isEmpty() && !selection.equals(NEW_SAVE_TEXT)) { 
        String filename = String.format("%s%s%s.%s", Constants.CHARACTER_SAVE_FOLDER, File.separator, fileList.getSelectedValue(), Constants.CHARACTER_SAVE_FORMAT);
        File f = new File(filename);
        if (f.exists()) {
          try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            UnitTemplate template = (UnitTemplate)ois.readObject();
            ois.close();
            fis.close();
            cc.loadTemplate(template);
            this.setVisible(false);
          } catch (IOException e1) {
            System.out.println("IOException: "+filename);
          } catch (ClassNotFoundException e2) {
            System.out.println("ClassNotFoundException: "+filename);
          }
        } else {
          System.out.println("fux!"+filename);
        }
      } else if (selection.equals(NEW_SAVE_TEXT)) {
        JOptionPane.showMessageDialog(this, "Cannot load empty file");
      }
      break;
    case "Save":
      if (selection != null) {
        if (!selection.isEmpty() && !selection.equals(NEW_SAVE_TEXT)) { 
          int choice = JOptionPane.showConfirmDialog(
            this,
            "Do you want to overwrite?",
            "Overwrite?",
            JOptionPane.YES_NO_OPTION
          );
          if (choice == 0) {
            deleteCharacterFile(selection);
            String saveName = JOptionPane.showInputDialog(this, "Enter a name for your save file:");
            
            // If a file with this name already exists (other than the one
            // we're currently overwriting), cancel the save.
            boolean fileExists = false;
            if (!saveName.equals(selection)) {
              for (int i=0; i<filenames.size()-1; i++) {
                if (i != fileList.getSelectedIndex()) {
                  String s = filenames.get(i);
                  if (s.equals(saveName)) {
                    JOptionPane.showMessageDialog(this, "Error: File exists");
                  }
                }
              }
            }

            if (!fileExists && doSave(saveName)) {
              filenames.set(fileList.getSelectedIndex(), saveName);
              fileList.setListData(filenames);
            }
          }
        } else if (selection.equals(NEW_SAVE_TEXT)) {
          String saveName = JOptionPane.showInputDialog(this, "Enter a name for your save file:");
          for (int i=0; i<filenames.size()-1; i++) {
            if (i != fileList.getSelectedIndex()) {
              String s = filenames.get(i);
              if (s.equals(saveName)) {
                JOptionPane.showMessageDialog(this, "Error: File exists");
              }
            }
          }
          if (doSave(saveName)) {
            updateFilenames();
          }
        }
      }
      break;
    case "Delete":
      if (!selection.equals(NEW_SAVE_TEXT)) {
        int choice = JOptionPane.showConfirmDialog(
          this,
          "Are you sure you want to delete?",
          "Delete?",
          JOptionPane.YES_NO_OPTION
        );
        if (choice == 0) {
          deleteCharacterFile(selection);
          updateFilenames();
        }
      }
      break;
    case "Cancel":
      setVisible(false);
      break;
    }
  }
  
  private void deleteCharacterFile(String saveName) {
    String filename = String.format(
        "%s%s%s.%s",
        Constants.CHARACTER_SAVE_FOLDER,
        File.separatorChar,
        saveName,
        Constants.CHARACTER_SAVE_FORMAT
      );
      File f = new File(filename);
      f.delete();
  }
  
  private boolean doSave(String saveName) {
    String filename = String.format(
      "%s%s%s.%s",
      Constants.CHARACTER_SAVE_FOLDER,
      File.separatorChar,
      saveName,
      Constants.CHARACTER_SAVE_FORMAT
    );
    File f = new File(filename);
    try {
      FileOutputStream fos = new FileOutputStream(f);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject((Object)template);
      oos.close();
      fos.close();
      return true;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
  }
  
  private void updateFilenames() {
    File dir = new File(Constants.CHARACTER_SAVE_FOLDER);
    if (dir.exists() && dir.isDirectory()) {
      filenames.clear();
      for (String s : dir.list()) {
        if (Utils.isSaveFile(s)) {
          filenames.add(s.substring(0, s.length()-4));
        }
      }
    } else {
      // error stuff
    }
    filenames.add(NEW_SAVE_TEXT);
    fileList.setListData(filenames);
  }
}