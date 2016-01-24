package warpath.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.util.ArrayList;
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
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.RPGDriver;
import warpath.core.Utils;
import warpath.templates.AccessoryTemplate;
import warpath.templates.TemplateFactory;
import warpath.templates.UnitTemplate;

/** The menu for creating a new character.
 * TODO support saving female characters
 * TODO remove slot dialog in favor of just items
 */
public class CharacterCreator extends JPanel implements ActionListener, ChangeListener {

  private static final long serialVersionUID = 1L;

  private final static String NEW_SAVE_TEXT = "(New file)";
  private final static String BASE_MODEL = "Base model";
  private final static String NO_ITEM = "None";
  
  private final static String[] MODEL_NAMES = {"player", "female", "zombie"};
  
  // Top-level panels
  private final JPanel leftPanel;
  private final JPanel middlePanel;
  private final JPanel rightPanel;
  
  // Left panel components
  private final SurfacePanel unitPanel;
  private final JList<String> savesList;
  private final JButton modelSelector;
  private final JScrollPane listPanel;
  private final JPanel loadSavePanel;
  private final JButton loadButton;
  private final JButton saveButton;
  private final JButton deleteButton;
  
  // Middle panel components
  private final JLabel slotLabel; 
  private final JPanel slotLabelPanel;
  private final JComboBox<String> slotComboBox;
  
  private final JLabel chooseItemLabel;
  private final JPanel chooseItemLabelPanel;
  private final JComboBox<String> itemComboBox;
  
  private final JLabel chooseColorLabel;
  private final JPanel chooseColorLabelPanel;
  private final JComboBox<String> colorComboBox;
  
  private final JButton startButton;
  private final JPanel startButtonPanel;
  
  // Right panel components
  private final JPanel RPanel;
  private final JPanel GPanel;
  private final JPanel BPanel;
  private final JSlider RSlider;
  private final JSlider GSlider;
  private final JSlider BSlider;
  private final JLabel RLabel;
  private final JLabel GLabel;
  private final JLabel BLabel;
  
  private final JPanel currentColorPanel;
  private final JPanel savedColorPanel;
  private final JButton copyButton;
  private final JButton pasteButton;
  private final JPanel copyPasteColorPanel;
  
  private final JPanel copyButtonPanel;
  private final JPanel savedColorPanelContainer;
  private final JPanel currentColorPanelContainer;
  private final JLabel currentColorLabel; 
  private final JPanel saveColorContainerPanel;
  
  private final Vector<String> filenames;

  private Surface unitSurface;
  private Surface unitSurfaceBase;
  private UnitTemplate template;
  private AccessoryTemplate currentItemTemplate; // Unused
  
  private int modelIndex;

  
  /**
   * TODO: I really don't like passing the Driver as an argument, figure out
   * how to avoid this.
   */
  public CharacterCreator(RPGDriver driver, JFrame window, int width, int height) {
    setSize(width, height);
    setPreferredSize(new Dimension(width, height));
    setVisible(true);
    
    setDoubleBuffered(true);
    setBackground(Color.BLACK);
    setLayout(new GridLayout(1,3));
    setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Set up the unit template.
    modelIndex = 0;
    template = (UnitTemplate)TemplateFactory.getTemplate(MODEL_NAMES[modelIndex]);
    
    // Set up the panels.
    leftPanel = new JPanel();
    add(leftPanel);
    middlePanel = new JPanel();
    add(middlePanel);
    rightPanel = new JPanel();
    add(rightPanel);
    
    // =====================
    // Set up the left panel
    // =====================
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    leftPanel.setBorder(BorderFactory.createEmptyBorder(Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING));
    
    // Add the unit graphic at the top of the left panel.
    unitSurfaceBase = new Surface(String.format("%s_standing_E_1.%s", template.getAnimName(), Constants.IMAGE_FORMAT));
    
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel = new SurfacePanel(unitSurface);
    //unitPanel.setBackground(Color.RED);
    leftPanel.add(unitPanel);
    
    leftPanel.add(Box.createVerticalStrut(Constants.MENU_PADDING));

    modelSelector = new JButton("Switch Model");
    modelSelector.setAlignmentX(CENTER_ALIGNMENT);
    modelSelector.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        changeModel();
      }
    });
    leftPanel.add(modelSelector);
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
    loadButton.addActionListener(this);
    saveButton = new JButton("Save");
    saveButton.addActionListener(this);
    deleteButton = new JButton("Delete");
    deleteButton.addActionListener(this);
    
    loadSavePanel = new JPanel(new BorderLayout());
    loadSavePanel.setOpaque(false);
    loadSavePanel.setLayout(new GridLayout(1, 3, Constants.MENU_PADDING, Constants.MENU_PADDING));
    loadSavePanel.add(loadButton);
    loadSavePanel.add(saveButton);
    loadSavePanel.add(deleteButton);
    
    leftPanel.add(loadSavePanel);
    
    // =======================
    // Set up the middle panel
    // =======================
    
    middlePanel.setBorder(BorderFactory.createEmptyBorder(Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING));
    //middlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.MENU_PADDING));
    middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
    
    slotLabel = new JLabel("Choose slot");
    slotLabel.setHorizontalAlignment(SwingConstants.CENTER);
    // Swing sucks
    slotLabelPanel = new JPanel(new BorderLayout());
    slotLabelPanel.setOpaque(false);
    slotLabelPanel.add(slotLabel, BorderLayout.CENTER);
    middlePanel.add(slotLabelPanel);

    //middlePanel.add(Box.createVerticalStrut(Constants.MENU_PADDING));
    
    // Set up the slot chooser and item chooser

    slotComboBox = new JComboBox<String>();
    refreshSlotComboBox();
    slotComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        refreshItemComboBox();
        refreshColorComboBox();
      }
    });
    
    middlePanel.add(slotComboBox);
    
    middlePanel.add(Box.createVerticalStrut(Constants.MENU_PADDING));
    
    chooseItemLabel = new JLabel("Choose item");
    chooseItemLabel.setHorizontalAlignment(SwingConstants.CENTER);
    // Swing sucks
    chooseItemLabelPanel = new JPanel(new BorderLayout());
    chooseItemLabelPanel.setOpaque(false);
    chooseItemLabelPanel.add(chooseItemLabel, BorderLayout.CENTER);
    middlePanel.add(chooseItemLabelPanel);
    itemComboBox = new JComboBox<String>();
    
    // NB: This fires even when contents are updated as part of
    // slotComboBox#itemStateChanged().
    // TODO set currentItemTemplate
    itemComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        String slot = (String)(slotComboBox.getSelectedItem());
        String item = (String)(itemComboBox.getSelectedItem());
        // TODO better validation
        if (slot != null && !slot.equals(BASE_MODEL)) {
          if (item == null) {
            // why?
            //System.out.println("fuux");
          } else if (item.equals(NO_ITEM)) {
            template.removeItem(slot);
            updateUnitSurface();
          } else {
            equipItem();
          }
          refreshColorComboBox();
          updateSliders();
        }
      }
    });
    
    middlePanel.add(itemComboBox);
    
    middlePanel.add(Box.createVerticalStrut(Constants.MENU_PADDING));
    
    chooseColorLabel = new JLabel("Choose color");
    chooseColorLabel.setHorizontalAlignment(SwingConstants.CENTER);
    // Swing sucks
    chooseColorLabelPanel = new JPanel(new BorderLayout());
    chooseColorLabelPanel.add(chooseColorLabel, BorderLayout.CENTER);
    middlePanel.add(chooseColorLabelPanel);
    
    colorComboBox = new JComboBox<String>();
    colorComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent arg0) {
        updateSliders();
      }
    });
    middlePanel.add(colorComboBox);
    
    middlePanel.add(Box.createVerticalStrut(400));
    
    startButton = new JButton("Start Game");
    startButton.addActionListener(driver);
    startButtonPanel = new JPanel(new BorderLayout());
    startButtonPanel.add(startButton, BorderLayout.CENTER);
    middlePanel.add(startButtonPanel);
    
    // ======================
    // Set up the right panel
    // ======================
    
    rightPanel.setBorder(BorderFactory.createEmptyBorder(Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING));
    //middlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.MENU_PADDING));
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

    RSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
    RSlider.addChangeListener(this);
    GSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
    GSlider.addChangeListener(this);
    BSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
    BSlider.addChangeListener(this);
    RLabel = new JLabel("Red");
    RLabel.setHorizontalAlignment(SwingConstants.CENTER);
    GLabel = new JLabel("Green");
    GLabel.setHorizontalAlignment(SwingConstants.CENTER);
    BLabel = new JLabel("Blue");
    BLabel.setHorizontalAlignment(SwingConstants.CENTER);
    RPanel = new JPanel(new BorderLayout());
    GPanel = new JPanel(new BorderLayout());
    BPanel = new JPanel(new BorderLayout());
    RPanel.add(RSlider, BorderLayout.CENTER);
    RPanel.add(RLabel, BorderLayout.SOUTH);
    GPanel.add(GSlider, BorderLayout.CENTER);
    GPanel.add(GLabel, BorderLayout.SOUTH);
    BPanel.add(BSlider, BorderLayout.CENTER);
    BPanel.add(BLabel, BorderLayout.SOUTH);
    rightPanel.add(RPanel);
    rightPanel.add(GPanel);
    rightPanel.add(BPanel);
    rightPanel.add(Box.createVerticalStrut(10));
    
    saveColorContainerPanel = new JPanel();
    saveColorContainerPanel.setLayout(new GridLayout(1,3));
    currentColorPanel = new JPanel();
    currentColorPanel.setBackground(Color.RED);
    currentColorLabel = new JLabel("Current color");
    currentColorLabel.setHorizontalAlignment(SwingConstants.CENTER);
    currentColorPanelContainer = new JPanel(new BorderLayout());
    currentColorPanelContainer.add(currentColorPanel, BorderLayout.CENTER);
    currentColorPanelContainer.add(currentColorLabel, BorderLayout.SOUTH);
    saveColorContainerPanel.add(currentColorPanelContainer);
    copyPasteColorPanel = new JPanel(new GridLayout(2,1));
    copyButton = new JButton("-->");
    copyButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        copyColor();
      }
    });
    copyButtonPanel = new JPanel(new BorderLayout());
    copyButtonPanel.add(copyButton, BorderLayout.CENTER);
    copyButtonPanel.add(new JLabel("Current color"), BorderLayout.SOUTH);
    pasteButton = new JButton("<--");
    pasteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        pasteColor();
      }
    });
    copyPasteColorPanel.add(copyButton);
    copyPasteColorPanel.add(pasteButton);
    saveColorContainerPanel.add(copyPasteColorPanel);
    savedColorPanel = new JPanel();
    savedColorPanel.setBackground(Color.BLUE);

    JLabel savedColorLabel = new JLabel("Saved color");
    savedColorLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
    savedColorPanelContainer = new JPanel(new BorderLayout());
    savedColorPanelContainer.add(savedColorPanel, BorderLayout.CENTER);
    savedColorPanelContainer.add(savedColorLabel, BorderLayout.SOUTH);
    saveColorContainerPanel.add(savedColorPanelContainer);
    rightPanel.add(saveColorContainerPanel);
    rightPanel.add(Box.createVerticalStrut(400));
    
    // Logic
    refreshItemComboBox();
    refreshColorComboBox();
    revalidate();
  }

  /**
   * I think this needs to be protected so that anonymous classes can call it.
   * TODO add automatic sprite name fetching to combo boxes
   */
  private void equipItem() {
    String slot = (String)(slotComboBox.getSelectedItem());
    String animName = getAnimName();
    if (animName != null && !animName.equals(template.getAnimName())) {
      template.addItem(slot, (AccessoryTemplate)TemplateFactory.getTemplate(animName));
      updateUnitSurface();
    } else {
      System.out.println("fox");
    }
  }

  // TODO: OOP
  private void refreshColorComboBox() {
    String animName = getAnimName();
    if (animName != null) {
      // TODO store current item template somewhere
      colorComboBox.removeAllItems();
      for (String colorName : TemplateFactory.getTemplate(animName).getColorList()) {
        colorComboBox.addItem(colorName);
      }
    } else {
      colorComboBox.removeAllItems();
      colorComboBox.addItem(NO_ITEM);
    }
  }

  /**
   * Returns the animName of the active model/item.
   * "Intelligently" identifies the item or model.
   */
  private String getAnimName() {
    String slot = (String)(slotComboBox.getSelectedItem());
    String item = (String)(itemComboBox.getSelectedItem());
    if (slot == null) {
      return null;
    } else if (slot.equals("Base model")) {
      return "player";
    } else if (slot != null && item != null) {
      switch(item) {
      case "Sword":
        return "sword";
      case "Shield":
        return "shield2";
      default:
        return null;
      }
    } else {
      // e.g. NO_ITEM
      return null;
    }
  }

  private void updateSliders() {
    //String slot = (String)(slotComboBox.getSelectedItem());
    //String itemName = (String)(itemComboBox.getSelectedItem());
    String colorName = (String)(colorComboBox.getSelectedItem());
    String animName = getAnimName();
    if (animName != null) {
      HashMap<String, Color> colorMap = TemplateFactory.getTemplate(animName).getColorMap();
      Color c = colorMap.get(colorName);
      // TODO fine-tune this validation
      if (c == null) {
        RSlider.setEnabled(false);
        GSlider.setEnabled(false);
        BSlider.setEnabled(false);
        RSlider.setValue(0);
        GSlider.setValue(0);
        BSlider.setValue(0);
      } else {
        RSlider.setEnabled(true);
        GSlider.setEnabled(true);
        BSlider.setEnabled(true);
        RSlider.setValue(c.getRed());
        GSlider.setValue(c.getGreen());
        BSlider.setValue(c.getBlue());
        currentColorPanel.setBackground(c);
      }
    }
  }

  /**
   * Called when a button is pressed.
   * Handles events from any of the buttons on the page.
   * TODO: maybe move these to anonymous handlers?
   * @param e - the ActionEvent from the button press
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    String selection = savesList.getSelectedValue();
    
    switch (e.getActionCommand()) {
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
            refreshSlotComboBox();
            refreshItemComboBox();
            refreshColorComboBox();
            slotComboBox.setSelectedIndex(0);
            itemComboBox.setSelectedIndex(0);
            colorComboBox.setSelectedIndex(0);
            updateSliders();
          } catch (IOException e1) {
            System.out.println("IOException (Load): "+filename);
          } catch (ClassNotFoundException e2) {
            System.out.println("ClassNotFoundException (Load): "+filename);
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
            updateSaveFilenames();
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
          updateSaveFilenames();
        }
      }
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
  private void copyColor() {
    savedColorPanel.setBackground(currentColorPanel.getBackground());
  }
  
  /**
   * Sets the specified color sliders to the stored value, if it exists.
   * @param name - the name of the color (e.g. "Shirt 1") 
   */
  private void pasteColor() {
    Color c = savedColorPanel.getBackground();
    RSlider.setValue(c.getRed());
    GSlider.setValue(c.getGreen());
    BSlider.setValue(c.getBlue());
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
    String slot = (String)(slotComboBox.getSelectedItem());
    String itemName = (String)(itemComboBox.getSelectedItem());
    String colorName = (String)(colorComboBox.getSelectedItem());
    String animName = getAnimName();
    HashMap<String, Color> colorMap = TemplateFactory.getTemplate(animName).getColorMap();
    Color c = colorMap.get(colorName);
    if (c != null) {
      int r = RSlider.getValue();
      int g = GSlider.getValue();
      int b = BSlider.getValue();
      Color dest = new Color(r,g,b);
      
      // double validation :(
      if (!slot.equals(BASE_MODEL) && (itemName != null) & (!itemName.equals(NO_ITEM))) {
        AccessoryTemplate item = template.getItem(slot);
        item.getPaletteSwaps().put(c, dest);
      } else {
        template.getPaletteSwaps().put(c, dest);
      }
      //colorLabels.get(label).setBackground(dest);
      RLabel.setText(Integer.toString(r));
      GLabel.setText(Integer.toString(g));
      BLabel.setText(Integer.toString(b));
      updateUnitSurface();
      currentColorPanel.setBackground(dest);
    }
  }
  
  /**
   * Swap between male and female creation.
   * TODO extend this to making zombies, other sprites
   **/
  private void changeModel() {
    modelIndex = (modelIndex+1) % MODEL_NAMES.length;
    String modelName = MODEL_NAMES[modelIndex];
    template = (UnitTemplate)TemplateFactory.getTemplate(modelName);
    updateUnitSurface();
    refreshSlotComboBox();
    refreshItemComboBox();
    refreshColorComboBox();
  }
  
  private void refreshSlotComboBox() {
    String modelName = MODEL_NAMES[modelIndex];
    ArrayList<String> slots = new ArrayList<String>();
    slots.add(BASE_MODEL);
    
    switch (modelName) {
    case "player":
      for (String slot : Constants.SLOT_LIST) {
        slots.add(slot);
      }
    case "female":
      break;
    case "zombie":
      break;
    default:
      break;
    }
    slotComboBox.removeAllItems();
    for (String item : slots) {
      slotComboBox.addItem(item);
    }
  }

  public UnitTemplate exportTemplate() {
    return template;
  }

  public void loadTemplate(UnitTemplate template) {
    this.template = template;
    updateUnitSurface();
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
      oos.writeObject(template);
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
  private void updateSaveFilenames() {
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

  private void refreshItemComboBox() {
    String slot = (String)(slotComboBox.getSelectedItem());
    String[] items = {};
    // TODO why do we need this validation?
    if (slot != null) {
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
      default:
        break;
      }
    }
    itemComboBox.removeAllItems();
    itemComboBox.addItem(NO_ITEM);
    for (String item : items) {
      itemComboBox.addItem(item);
    }
    if (template.getItem(slot) != null) {
      
      // TODO this sux
      String animName = template.getItem(slot).getAnimName();
      String displayName;
      switch (animName) {
      case "sword":
        displayName = "Sword";
        break;
      case "shield2":
        displayName = "Shield";
        break;
      default:
        displayName = null;
        break;
      }
      if (displayName != null) {
        itemComboBox.setSelectedItem(displayName);
      }
    }
  }
  
  /**
   * TODO handle nonstandard naming (e.g. wizard)
   * TODO correct overlap
   */
  private void updateUnitSurface() {
    unitSurfaceBase = new Surface(String.format("%s_standing_E_1.%s", template.getAnimName(), Constants.IMAGE_FORMAT));
    
    unitSurfaceBase.setPaletteSwaps(template.getPaletteSwaps());
    unitSurfaceBase.applyPaletteSwaps();
    
    for (AccessoryTemplate item : template.getEquipment().values()) {
      Surface itemSurface = getItemSurface(item);
      if (itemSurface != null) {
        itemSurface.setColorkey(Color.WHITE);
        itemSurface.setPaletteSwaps(item.getPaletteSwaps());
        itemSurface.applyPaletteSwaps();
        // itemSurface.applyColorkey();
        unitSurfaceBase.blit(itemSurface);
      }
    }
    
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel.setSurface(unitSurface);
    unitPanel.repaint();
  }

  private Surface getItemSurface(AccessoryTemplate item) {
    String imageFilename = String.format("%s_standing_E_1", item.getAnimName());
    Surface itemSurface = null;
    if (Utils.imageExists(imageFilename)) {
      itemSurface = new Surface(String.format("%s.%s", imageFilename, Constants.IMAGE_FORMAT));
    } else {
      String behindFilename = String.format("%s_standing_E_1_B", item.getAnimName());
      if (Utils.imageExists(behindFilename)) {
        itemSurface = new Surface(String.format("%s.%s", behindFilename, Constants.IMAGE_FORMAT));
      } else {
        System.out.println("fux the itemz");
      }
    }
    return itemSurface;
  }
}
