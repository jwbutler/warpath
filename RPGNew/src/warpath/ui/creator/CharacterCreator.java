package warpath.ui.creator;
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
import java.util.*;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.RPG;
import warpath.core.Utils;
import warpath.layouts.StackLayout;
import warpath.layouts.StretchLayout;
import warpath.templates.AccessoryTemplate;
import warpath.templates.TemplateFactory;
import warpath.templates.UnitTemplate;
import warpath.ui.GameWindow;

/**
 * The menu for creating a new character.
 * TODO support saving female characters
 */
public class CharacterCreator extends JPanel implements ActionListener, ChangeListener {

  private static final long serialVersionUID = 1L;

  private final static String NEW_SAVE_TEXT = "(New file)";
  private final static String BASE_MODEL = "Base model";
  private final static String NO_ITEM = "None";
  
  private final static List<String> MODEL_NAMES = Arrays.asList("player", "female", "zombie");
  
  // Top-level panels
  private final JPanel leftPanel;
  private final JPanel middlePanel;
  private final JPanel rightPanel;
  
  // Left panel components
  private final SurfacePanel unitPanel;
  private final JList<String> savesList;
  private final JButton modelSelector;
  private final JPanel loadSaveContainerPanel;
  private final JScrollPane listPanel;
  private final JPanel loadSaveButtonPanel;
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
  private final ColorPicker colorPicker;
  
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
  public CharacterCreator(JFrame window, int width, int height) {
    setSize(width, height);
    setPreferredSize(new Dimension(width, height));
    setVisible(true);
    
    setDoubleBuffered(true);
    setBackground(Color.BLACK);
    setLayout(new GridLayout(1,3));
    setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Set up the unit template.
    modelIndex = 0;
    template = (UnitTemplate)TemplateFactory.getTemplate(MODEL_NAMES.get(modelIndex));
    
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
    leftPanel.setLayout(new StackLayout(StackLayout.Y_AXIS));
    leftPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    leftPanel.setBorder(BorderFactory.createEmptyBorder(Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING));
    
    // Add the unit graphic at the top of the left panel.
    unitSurfaceBase = new Surface(String.format("%s_standing_E_1.%s", template.getSpriteName(), Constants.IMAGE_FORMAT));
    
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel = new SurfacePanel(unitSurface);

    modelSelector = new JButton("Switch Model");
    modelSelector.setAlignmentX(CENTER_ALIGNMENT);
    modelSelector.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        changeModel();
      }
    });
    leftPanel.add(unitPanel);
    leftPanel.add(modelSelector);
    
    // Set up the load/save interface.
    filenames = new Vector<String>();
    savesList = new JList<String>(filenames);
    savesList.setBackground(Color.WHITE);
    savesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    updateSaveFilenames();
    listPanel = new JScrollPane(savesList);
    
    loadButton = new JButton("Load");
    loadButton.addActionListener(this);
    saveButton = new JButton("Save");
    saveButton.addActionListener(this);
    deleteButton = new JButton("Delete");
    deleteButton.addActionListener(this);
    
    loadSaveButtonPanel = new JPanel();
    loadSaveButtonPanel.setLayout(new GridLayout(1, 3, Constants.MENU_PADDING, Constants.MENU_PADDING));
    loadSaveButtonPanel.add(loadButton);
    loadSaveButtonPanel.add(saveButton);
    loadSaveButtonPanel.add(deleteButton);
    
    loadSaveContainerPanel = new JPanel(new StretchLayout(StretchLayout.Y_AXIS));
    loadSaveContainerPanel.add(listPanel, 7.0);
    loadSaveContainerPanel.add(loadSaveButtonPanel, 1.0);
    leftPanel.add(loadSaveContainerPanel);
    
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
            System.out.println("fuux");
          } else if (item.equals(NO_ITEM)) {
            // WHY does this proc when we're populating?
            template.removeItem(slot);
            updateUnitSurface();
          } else {
            equipItem();
          }
          refreshColorComboBox();
          updateColorPicker();
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
        updateColorPicker();
      }
    });
    middlePanel.add(colorComboBox);
    
    middlePanel.add(Box.createVerticalStrut(400));
    
    startButton = new JButton("Start Game");
    startButton.addActionListener(this);
    startButtonPanel = new JPanel(new BorderLayout());
    startButtonPanel.add(startButton, BorderLayout.CENTER);
    middlePanel.add(startButtonPanel);
    
    // ======================
    // Set up the right panel
    // ======================
    
    rightPanel.setBorder(BorderFactory.createEmptyBorder(Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING));
    //middlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.MENU_PADDING));
    rightPanel.setLayout(new StretchLayout(StretchLayout.Y_AXIS));
    colorPicker = new ColorPicker(this);
    rightPanel.add(colorPicker);
    
    
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
    String spriteName = getSpriteName();
    if (spriteName != null && !spriteName.equals(template.getSpriteName())) {
      template.addItem(slot, (AccessoryTemplate)TemplateFactory.getTemplate(spriteName));
      updateUnitSurface();
    } else {
      System.out.println("fox");
    }
  }

  // TODO: OOP
  private void refreshColorComboBox() {
    String spriteName = getSpriteName();
    ItemListener listener = colorComboBox.getItemListeners()[0];
    colorComboBox.removeItemListener(listener);
    if (spriteName != null) {
      // TODO store current item template somewhere
      colorComboBox.removeAllItems();
      for (String colorName : TemplateFactory.getTemplate(spriteName).getColorList()) {
        colorComboBox.addItem(colorName);
      }
    } else {
      colorComboBox.removeAllItems();
      colorComboBox.addItem(NO_ITEM);
    }
    colorComboBox.addItemListener(listener);
  }

  /**
   * Returns the spriteName of the active model/item.
   * "Intelligently" identifies the item or model.
   */
  private String getSpriteName() {
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

  private void updateColorPicker() {
    String slot = (String)(slotComboBox.getSelectedItem());
    //String itemName = (String)(itemComboBox.getSelectedItem());
    String colorName = (String)(colorComboBox.getSelectedItem());
    String spriteName = getSpriteName();
    if (spriteName != null) {
      Map<String, Color> colorMap;
      Color c;
      //HashMap<String, Color> colorMap = TemplateFactory.getTemplate(spriteName).getColorMap();
      if (slot == BASE_MODEL) {
        colorMap = template.getColorMap();
        c = template.getPaletteSwaps().get(colorMap.get(colorName));
      } else {
        colorMap = template.getItem(slot).getColorMap();
        c = template.getItem(slot).getPaletteSwaps().get(colorMap.get(colorName));
      }
      
      // TODO fine-tune this validation
      if (c == null) {
        colorPicker.setVisible(false);
        colorPicker.setColor(Color.BLACK);
      } else {
        colorPicker.setVisible(true);
        colorPicker.setColor(c);
        //currentColorPanel.setBackground(c);
      }
    }
  }

  /**
   * Called when a button is pressed.
   * Handles events from any of the buttons on the page.
   * TODO: maybe moveBy these to anonymous handlers?
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
            updateColorPicker();
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
    case "Start Game":
      GameWindow window = GameWindow.getInstance();
      RPG game = RPG.getInstance();
      if (e.getActionCommand().equals("Start Game")) {
        CharacterCreator cc = window.getCharacterCreator();
        window.setCardLayout("Game");
        game.start(cc.exportTemplate());
      }
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
   */
  private void copyColor() {
    //savedColorPanel.setBackground(currentColorPanel.getBackground());
  }
  
  /**
   * Sets the specified color sliders to the stored value, if it exists.
   */
  private void pasteColor() {
  }
  
  /**
   * Perform palette swaps for each of the color sliders.
   */
  public void updateColors() {
    String slot = (String)(slotComboBox.getSelectedItem());
    String itemName = (String)(itemComboBox.getSelectedItem());
    String colorName = (String)(colorComboBox.getSelectedItem());
    String spriteName = getSpriteName();
    Map<String, Color> colorMap = TemplateFactory.getTemplate(spriteName).getColorMap();
    Color c = colorMap.get(colorName);
    if (c != null) {
      Color dest = colorPicker.getColor();
      
      // double validation :(
      if (!slot.equals(BASE_MODEL) && (itemName != null) & (!itemName.equals(NO_ITEM))) {
        AccessoryTemplate item = template.getItem(slot);
        item.getPaletteSwaps().put(c, dest);
      } else {
        template.getPaletteSwaps().put(c, dest);
      }
      updateUnitSurface();
    }
  }
  
  /**
   * Swap between male and female creation.
   * TODO extend this to making zombies, other sprites
   **/
  private void changeModel() {
    modelIndex = (modelIndex+1) % MODEL_NAMES.size();
    String modelName = MODEL_NAMES.get(modelIndex);
    template = (UnitTemplate)TemplateFactory.getTemplate(modelName);
    updateUnitSurface();
    refreshSlotComboBox();
    refreshItemComboBox();
    refreshColorComboBox();
  }
  
  private void refreshSlotComboBox() {
    String modelName = MODEL_NAMES.get(modelIndex);
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
    ItemListener listener = slotComboBox.getItemListeners()[0];
    slotComboBox.removeItemListener(listener);
    slotComboBox.removeAllItems();
    for (String item : slots) {
      slotComboBox.addItem(item);
    }
    slotComboBox.addItemListener(listener);
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
    // Dumb hack
    ItemListener listener = itemComboBox.getItemListeners()[0];
    itemComboBox.removeItemListener(listener);
    
    itemComboBox.removeAllItems();
    itemComboBox.addItem(NO_ITEM);
    for (String item : items) {
      itemComboBox.addItem(item);
    }
    itemComboBox.addItemListener(listener);
    if (template.getItem(slot) != null) {
      
      // TODO this sux
      String spriteName = template.getItem(slot).getSpriteName();
      String displayName;
      switch (spriteName) {
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
    unitSurfaceBase = new Surface(String.format("%s_standing_E_1.%s", template.getSpriteName(), Constants.IMAGE_FORMAT));
    
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
    String imageFilename = String.format("%s_standing_E_1", item.getSpriteName());
    Surface itemSurface = null;
    if (Utils.imageExists(imageFilename)) {
      itemSurface = new Surface(String.format("%s.%s", imageFilename, Constants.IMAGE_FORMAT));
    } else {
      String behindFilename = String.format("%s_standing_E_1_B", item.getSpriteName());
      if (Utils.imageExists(behindFilename)) {
        itemSurface = new Surface(String.format("%s.%s", behindFilename, Constants.IMAGE_FORMAT));
      } else {
        System.out.println("fux the itemz");
      }
    }
    return itemSurface;
  }
}
