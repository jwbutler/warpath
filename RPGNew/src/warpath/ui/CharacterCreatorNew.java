package warpath.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.RPGDriver;
import warpath.core.Utils;
import warpath.units.UnitTemplate;

/** The menu for creating a new character.
 * TODO support saving female characters
 */
public class CharacterCreatorNew extends JPanel implements ActionListener, ChangeListener {

  private static final long serialVersionUID = 1L;

  private final static String NEW_SAVE_TEXT = "(New file)";
  
  private final JFrame window;
  private Color savedColor;
  
  private final String[] colorNames;
  private final HashMap<String, Color> baseColors;
  
  // Top-level panels
  private final JPanel leftPanel;
  private final JPanel middlePanel;
  private final JPanel rightPanel;
  
  // Left panel components
  private final SurfacePanel unitPanel;
  private final JList<String> savesList;
  private final JButton genderSelector;
  private final JScrollPane listPanel;
  private final JPanel loadSavePanel;
  private final JButton loadButton;
  private final JButton saveButton;
  
  // Middle panel components
  private final JLabel slotLabel; 
  private final JComboBox<String> midSlotComboBox;
  private final JComboBox<String> midItemComboBox;
  
  private final JButton startButton;
  
  // Right panel components
  
  private final JButton copyButton;
  private final JButton pasteButton;
  
  private final Vector<String> filenames;

  private HashMap<Color, Color> paletteSwaps;
  private Surface unitSurface;
  private Surface unitSurfaceBase;
  private UnitTemplate template;
  
  /**
   * TODO: I really don't like passing the Driver as an argument, figure out
   * how to avoid this.
   */
  public CharacterCreatorNew(RPGDriver driver, JFrame window, int width, int height) {
    this.window = window;
    setSize(width, height);
    setPreferredSize(new Dimension(width, height));
    setVisible(true);
    
    setDoubleBuffered(true);
    setBackground(Color.BLACK);
    setLayout(new GridLayout(1,3));
    setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Set up the panels.
    leftPanel = new JPanel();
    //leftPanel.setBackground(Color.RED);
    add(leftPanel);
    middlePanel = new JPanel();
    middlePanel.setBackground(Color.RED);
    add(middlePanel);
    rightPanel = new JPanel();
    rightPanel.setBackground(Color.BLUE);
    add(rightPanel);
    
    // Set up the left panel.
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.setAlignmentX(0.5f);
    leftPanel.setBorder(BorderFactory.createEmptyBorder(Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING));
    
    // Add the unit graphic at the top of the left panel.
    unitSurfaceBase = new Surface("player_standing_E_1.png");
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel = new SurfacePanel(unitSurface);
    unitPanel.setBackground(Color.RED);
    leftPanel.add(unitPanel);
    
    leftPanel.add(Box.createVerticalStrut(Constants.MENU_PADDING));

    genderSelector = new JButton("Switch to Female");
    genderSelector.setAlignmentX(CENTER_ALIGNMENT);
    genderSelector.addActionListener(this);
    leftPanel.add(genderSelector);

    leftPanel.add(Box.createVerticalStrut(Constants.MENU_PADDING));
    
    // Set up the load/save interface.
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
    savesList = new JList<String>(filenames);
    savesList.setBackground(Color.WHITE);
    savesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listPanel = new JScrollPane(savesList);
    leftPanel.add(listPanel);
    
    leftPanel.add(Box.createVerticalStrut(Constants.MENU_PADDING));
    
    loadButton = new JButton("Load");
    loadButton.setAlignmentX(CENTER_ALIGNMENT);
    loadButton.addActionListener(this);
    
    saveButton = new JButton("Save");
    saveButton.setAlignmentX(CENTER_ALIGNMENT);
    saveButton.addActionListener(this);
    
    loadSavePanel = new JPanel();
    loadSavePanel.setOpaque(false);
    loadSavePanel.setLayout(new BoxLayout(loadSavePanel, BoxLayout.X_AXIS));
    loadSavePanel.add(loadButton);
    loadSavePanel.add(saveButton);
    
    leftPanel.add(loadSavePanel);
    
    // Set up the middle panel
    middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
    middlePanel.setBackground(Color.RED);
    middlePanel.setBorder(BorderFactory.createEmptyBorder(Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING));
    //middlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.MENU_PADDING));
    middlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    slotLabel = new JLabel("Slot");
    slotLabel.setOpaque(true);
    slotLabel.setBackground(Color.GREEN);
    slotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    slotLabel.setHorizontalAlignment(SwingConstants.CENTER);
    //middlePanel.getRootPane().add(slotLabel);
    middlePanel.add(slotLabel);
    // Set up the slot chooser and item chooser
    Vector<String> slotList = new Vector<String>();
    slotList.add("Base model");
    for (String slot : Constants.SLOT_LIST) {
      slotList.add(slot);
    }
    midSlotComboBox = new JComboBox<String>(slotList);
    midSlotComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        String slot = (String)e.getItem();
        refreshMidItemComboBox(slot);
      }
    });
    middlePanel.add(midSlotComboBox);

    middlePanel.add(Box.createVerticalStrut(Constants.MENU_PADDING));
    
    middlePanel.add(new JLabel("Choose item"));
    midItemComboBox = new JComboBox<String>();
    middlePanel.add(midItemComboBox);
    refreshMidItemComboBox((String)midSlotComboBox.getSelectedItem());
    
   // middlePanel.add(Box.createVerticalStrut(400));
    
    middlePanel.add(Box.createVerticalGlue());
    startButton = new JButton("Start Game");
    startButton.addActionListener(this);
    middlePanel.add(startButton);
    middlePanel.revalidate();
    // Set up the right panel
    
    // NYI
    copyButton = new JButton("Copy");
    pasteButton = new JButton("Paste");
    
    // Set up color information.
    paletteSwaps = new HashMap<Color,Color>();
    baseColors = new HashMap<String, Color>();
    colorNames = new String[] {
      "Hair", "Face", "Eyes", "Mouth", "Shirt 1", "Shirt 2", "Shirt 3",
      "Hands", "Belt", "Skirt", "Legs", "Boots 1", "Boots 2"
    };
    populateBaseColors();
    savedColor = null;
    revalidate();
  }

  private void populateBaseColors() {
    baseColors.put("Hair", new Color(128,64,0));
    baseColors.put("Face", new Color(255,128,64));
    baseColors.put("Eyes", new Color(0,64,64));
    baseColors.put("Mouth", new Color(128,0,0));
    baseColors.put("Shirt 1", new Color(128,0,128));
    baseColors.put("Shirt 2", new Color(255,0,255));
    baseColors.put("Shirt 3", new Color(0,0,128));
    baseColors.put("Hands", new Color(0,255,255));
    baseColors.put("Belt", new Color(0,0,0));
    baseColors.put("Skirt", new Color(128,128,128));
    baseColors.put("Legs", new Color(192,192,192));
    baseColors.put("Boots 1", new Color(0,128,0));
    baseColors.put("Boots 2", new Color(0,255,0));
  }

  /**
   * Called when a button is pressed.
   * @param e - the ActionEvent from the button press
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    String selection = savesList.getSelectedValue();
    
    switch (e.getActionCommand()) {
    case "Switch to Male":
      doGender();
      break;
    case "Switch to Female":
      doGender();
      break;
    case "Copy":
      // FIXME
      copyColor(null);
      break;
    case "Paste":
      // FIXME
      pasteColor(null);
      break;
    case "Load":
      if ((selection != null) && !selection.isEmpty() && !selection.equals(NEW_SAVE_TEXT)) { 
        String filename = String.format("%s%s%s.%s",
          Constants.CHARACTER_SAVE_FOLDER,
          File.separator,
          savesList.getSelectedValue(),
          Constants.CHARACTER_SAVE_FORMAT
        );
        File f = new File(filename);
        if (f.exists()) {
          try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            UnitTemplate template = (UnitTemplate)ois.readObject();
            ois.close();
            fis.close();
            loadTemplate(template);
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
                if (i != savesList.getSelectedIndex()) {
                  String s = filenames.get(i);
                  if (s.equals(saveName)) {
                    JOptionPane.showMessageDialog(this, "Error: File exists");
                  }
                }
              }
            }

            if (!fileExists && doSave(saveName)) {
              filenames.set(savesList.getSelectedIndex(), saveName);
              savesList.setListData(filenames);
            }
          }
        } else if (selection.equals(NEW_SAVE_TEXT)) {
          String saveName = JOptionPane.showInputDialog(this, "Enter a name for your save file:");
          for (int i=0; i<filenames.size()-1; i++) {
            if (i != savesList.getSelectedIndex()) {
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
    default:
      // Why?
      repaint();
      break;
    }
  }
  
  /**
   * Called whenever a slider is moved.
   * @param e - the event containing slider information
   */ 
  @Override
  public void stateChanged(ChangeEvent e) {
    updateColors();
  }
  
  /**
   * Copies the given color to the clipboard.
   * @param name - the name of the color (e.g. "Shirt 1")
   */
  private void copyColor(String name) {
    Color c = paletteSwaps.get(baseColors.get(name));
    savedColor = new Color(c.getRGB());
  }
  
  /**
   * Sets the specified color sliders to the stored value, if it exists.
   * @param name - the name of the color (e.g. "Shirt 1") 
   */
  private void pasteColor(String name) {
    /*if (savedColor != null) {
      JSlider RSlider = colorSliders.get(name)[0];
      RSlider.setValue(savedColor.getRed());
      JSlider GSlider = colorSliders.get(name)[1];
      GSlider.setValue(savedColor.getGreen());
      JSlider BSlider = colorSliders.get(name)[2];
      BSlider.setValue(savedColor.getBlue());
    }
    updateColors();*/
  }
  
  /**
   * Perform palette swaps for each of the color sliders.
   */
  public void updateColors() {
    /*
    for (String label : colorNames) {
      Color src = baseColors.get(label);
      int r = colorSliders.get(label)[0].getValue();
      int g = colorSliders.get(label)[1].getValue();
      int b = colorSliders.get(label)[2].getValue();
      Color dest = new Color(r,g,b);
      paletteSwaps.put(src, dest);
      colorLabels.get(label).setBackground(dest);
      sliderLabels.get(label)[0].setText(Integer.toString(r));
      sliderLabels.get(label)[1].setText(Integer.toString(g));
      sliderLabels.get(label)[2].setText(Integer.toString(b));
    }
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }*/
    
    /*unitSurface.setPaletteSwaps(paletteSwaps);
    unitSurface.applyPaletteSwaps();
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel.setSurface(unitSurface);*/
  }
  
  /**
   * Swap between male and female creation.
   * TODO extend this to making zombies, other sprites
   **/
  private void doGender() {
    if (genderSelector.getText().equals("Switch to Female")) {
      genderSelector.setText("Switch to Male");
      unitSurfaceBase = new Surface("female_standing_E_1.png");
    } else if (genderSelector.getText().equals("Switch to Male")) {
      genderSelector.setText("Switch to Female");
      unitSurfaceBase = new Surface("player_standing_E_1.png");
    } else {
      System.out.println("FUck you");
    }
    
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    /* KLUDGE, TELL WILL TO FIX ALL THESE */
    /*
    if (genderSelector.getText().equals("Switch to Male")) {
      HashMap<Color,Color> tmpSwaps = new HashMap<Color,Color>();
      tmpSwaps.put(new Color(79,39,0), new Color(128,64,0));
      unitSurface.setPaletteSwaps(tmpSwaps);
      unitSurface.applyPaletteSwaps();
    }*/
    /* END KLUDGE */
    
    unitSurface.setPaletteSwaps(paletteSwaps);
    unitSurface.applyPaletteSwaps();
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel.setSurface(unitSurface);
  }
  
  /** Returns the set of palette swaps that the user has created. */
  public HashMap<Color, Color> exportPaletteSwaps() {
    return paletteSwaps;
  }

  public void loadTemplate(UnitTemplate template) {
    paletteSwaps = template.getPaletteSwaps();
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    
    unitSurface.setPaletteSwaps(paletteSwaps);
    unitSurface.applyPaletteSwaps();
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel.setSurface(unitSurface);
    repaint();
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
  
  /**
   * Refresh the list of save files.
   * Don't call this when overwriting a file.
   */
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
    savesList.setListData(filenames);
  }

  private void refreshMidItemComboBox(String slot) {
    String[] items = new String[]{};
    switch (slot) {
    case "Head":
      break;
    case "Hair":
      break;
    case "Beard":
      break;
    case "Chest":
      break;
    case "Legs":
      break;
    case "Mainhand":
      items = new String[]{"Sword"};
      break;
    case "Offhand":
      items = new String[]{"Shield"};
      break;
    case "Default":
      System.out.println("fux");
      break;
    }
    midItemComboBox.removeAllItems();
    midItemComboBox.addItem("None");
    for (String item : items) {
      midItemComboBox.addItem(item);
    }
  }
}
