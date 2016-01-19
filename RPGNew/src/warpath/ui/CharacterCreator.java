package warpath.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.RPGDriver;
import warpath.units.UnitTemplate;

/** The menu for creating a new character.
 * TODO support saving female characters
 */
public class CharacterCreator extends JPanel implements ActionListener, ChangeListener {
  private final SurfacePanel unitPanel;
  private final JPanel sliderPanel;
  private final JPanel buttonPanel;
  private final JFrame window;
  private final Timer frameTimer;
  private Surface unitSurface;
  private Surface unitSurfaceBase;
  private Color savedColor;
  
  private final String[] colorNames;
  private final HashMap<String, Color> baseColors;
  private final HashMap<String, JLabel> colorLabels;
  private final HashMap<String, JSlider[]> colorSliders;
  private final HashMap<String, JLabel[]> sliderLabels;
  private final HashMap<String, JButton> copyButtons;
  private final HashMap<String, JButton> pasteButtons;
  private final JButton genderSelector;
  private final JButton continueButton;
  private final JButton loadSaveButton;

  private HashMap<Color, Color> paletteSwaps;
  /**
   * TODO: I really don't like passing the Driver as an argument, figure out
   * how to avoid this.
   */
  public CharacterCreator(RPGDriver driver, JFrame window, int width, int height) {
    copyButtons = new HashMap<String,JButton>();
    pasteButtons = new HashMap<String,JButton>();
    paletteSwaps = new HashMap<Color,Color>();
    this.window = window;
    
    setSize(width, height);
    setPreferredSize(new Dimension(width, height));
    setVisible(true);
    //gameWindow.add(gamePanel);
    
    setDoubleBuffered(true);
    setBackground(Color.BLACK);
    setLayout(null);
    colorNames = new String[] {
      "Hair", "Face", "Eyes", "Mouth", "Shirt 1", "Shirt 2", "Shirt 3",
      "Hands", "Belt", "Skirt", "Legs", "Boots 1", "Boots 2"
    };
    baseColors = new HashMap<String, Color>();
    colorSliders = new HashMap<String, JSlider[]>();
    sliderLabels = new HashMap<String, JLabel[]>();
    
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
    colorLabels = new HashMap<String,JLabel>();
    unitSurfaceBase = new Surface("player_standing_E_1.png");
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    
    savedColor = null;
    unitSurface = unitSurface.scale2x().scale2x();
    
    unitPanel = new SurfacePanel(unitSurface);

    unitPanel.setLayout(null);
    int w = unitSurface.getWidth();
    int h = unitSurface.getHeight();
    int l = (int)(getWidth()*0.30 - unitSurface.getWidth())/2;
    int t = (int)(getHeight()*0.10);
    unitPanel.setBounds(l, t, w, h);
    add(unitPanel);
    
    buttonPanel = new JPanel();
    buttonPanel.setBounds((int)0, (int)(getHeight()*0.5), (int)(getWidth()*0.30), (int)(getHeight()*0.5));
    buttonPanel.setLayout(new GridLayout(3, 0, Constants.MENU_PADDING, Constants.MENU_PADDING));
    buttonPanel.setAlignmentX(0.5f);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    this.add(buttonPanel);
    
    genderSelector = new JButton("Switch to Female");
    genderSelector.setAlignmentX(CENTER_ALIGNMENT);
    genderSelector.addActionListener(this);
    buttonPanel.add(genderSelector);

    
    loadSaveButton = new JButton("Load/Save");
    loadSaveButton.setAlignmentX(CENTER_ALIGNMENT);
    loadSaveButton.addActionListener(this);
    buttonPanel.add(loadSaveButton);
    
    // When the continue button is pressed, export color swaps and move to game panel
    continueButton = new JButton("Continue");
    continueButton.setAlignmentX(CENTER_ALIGNMENT);
    continueButton.addActionListener(driver);
    buttonPanel.add(continueButton);
    
    // add the surface
    sliderPanel = new JPanel();
    sliderPanel.setLayout(new GridLayout(colorNames.length, 5, 5, 5));
    sliderPanel.setBounds((int)(getWidth()*0.30), 0, (int)(getWidth()*0.70), (int)(getHeight()*1));
    sliderPanel.setBorder(new EmptyBorder(Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING, Constants.MENU_PADDING));
    
    populateSliderPanel();
    
    add(sliderPanel);
    frameTimer = new Timer(50, this);
    frameTimer.start();
  }
  
  /**
   * For each color, make an R/G/B slider with number labels, plus copy/paste
   * buttons.
   */
  private void populateSliderPanel() {
    for (String name : colorNames) {
      Color c = baseColors.get(name);
      paletteSwaps.put(c, c);
      JLabel colorLabel = createColorLabel(name, c);
      colorLabels.put(name, colorLabel);
      sliderPanel.add(colorLabel);
      JSlider RSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, c.getRed());
      JSlider GSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, c.getGreen());
      JSlider BSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, c.getBlue());
      JSlider sliders[] = new JSlider[3];
      sliders[0] = RSlider;
      sliders[1] = GSlider;
      sliders[2] = BSlider;
      colorSliders.put(name, sliders);
      RSlider.setOpaque(false);
      GSlider.setOpaque(false);
      BSlider.setOpaque(false);
      
      RSlider.addChangeListener(this);
      GSlider.addChangeListener(this);
      BSlider.addChangeListener(this);
      JLabel RLabel = new JLabel();
      RLabel.setText(Integer.toString(RSlider.getValue()));
      RLabel.setHorizontalAlignment(SwingConstants.CENTER);
      JLabel GLabel = new JLabel();
      GLabel.setText(Integer.toString(GSlider.getValue()));
      GLabel.setHorizontalAlignment(SwingConstants.CENTER);
      JLabel BLabel = new JLabel();
      BLabel.setText(Integer.toString(BSlider.getValue()));
      BLabel.setHorizontalAlignment(SwingConstants.CENTER);
      JLabel[] labels = {RLabel, GLabel, BLabel};
      sliderLabels.put(name, labels);
      JPanel rp = new JPanel();
      JPanel gp = new JPanel();
      JPanel bp = new JPanel();
      rp.setLayout(new BorderLayout());
      gp.setLayout(new BorderLayout());
      bp.setLayout(new BorderLayout());
      rp.add(RSlider, BorderLayout.NORTH);
      rp.add(RLabel, BorderLayout.SOUTH);
      gp.add(GSlider, BorderLayout.NORTH);
      gp.add(GLabel, BorderLayout.SOUTH);
      bp.add(BSlider, BorderLayout.NORTH);
      bp.add(BLabel, BorderLayout.SOUTH);
      sliderPanel.add(rp);
      sliderPanel.add(gp);
      sliderPanel.add(bp);
      
      JButton copyButton = new JButton("Copy");
      copyButton.setActionCommand("Copy_"+name);
      copyButton.addActionListener(this);
      copyButtons.put(name, copyButton);
      sliderPanel.add(copyButton);
      copyButton.setFont(new Font("Arial",Font.PLAIN, 9));
      JButton pasteButton = new JButton("Paste");
      pasteButton.setActionCommand("Paste_"+name);
      pasteButton.addActionListener(this);
      pasteButtons.put(name, pasteButton);
      sliderPanel.add(pasteButton);
      pasteButton.setFont(new Font("Arial",Font.PLAIN, 9));
    }
  }

  private JLabel createColorLabel(String name, Color c) {
    JLabel colorLabel = new JLabel();
    colorLabel.setForeground(Color.WHITE);
    colorLabel.setBackground(c);
    colorLabel.setText(name);
    colorLabel.setOpaque(true);
    colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
    colorLabel.setFont(new Font("Arial",Font.PLAIN, 9));
    return colorLabel;
  }

  /**
   * Called when a button is pressed.
   * @param e - the ActionEvent from the button press
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand() != null) {
      if (e.getActionCommand().equals("Switch to Male")) {
        doGender();
      } else if (e.getActionCommand().equals("Switch to Female")) {
        doGender();
      } else if (e.getActionCommand().startsWith("Copy")) {
        String name = e.getActionCommand().substring(5, e.getActionCommand().length());
        copyColor(name);
      } else if (e.getActionCommand().startsWith("Paste")) {
        String name = e.getActionCommand().substring(6, e.getActionCommand().length());
        pasteColor(name);
      } else if (e.getActionCommand().equals("Load/Save")) {
        showLoadSaveDialog();
      }
    } else {
      repaint();
    }
  }
  
  private void showLoadSaveDialog() {
    UnitTemplate template = new UnitTemplate("player", paletteSwaps);
    //JDialog loadSaveDialog = new LoadSaveCharacterDialog(window, template);
    //loadSaveDialog.setVisible(true);
    LoadSaveCharacterDialog loadSaveDialog = new LoadSaveCharacterDialog(this, template);
    loadSaveDialog.setVisible(true);
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
    if (savedColor != null) {
      JSlider RSlider = colorSliders.get(name)[0];
      RSlider.setValue(savedColor.getRed());
      JSlider GSlider = colorSliders.get(name)[1];
      GSlider.setValue(savedColor.getGreen());
      JSlider BSlider = colorSliders.get(name)[2];
      BSlider.setValue(savedColor.getBlue());
    }
    updateColors();
  }
  
  /**
   * Perform palette swaps for each of the color sliders.
   */
  public void updateColors() {
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
    }
    
    
    /* KLUDGE, TELL WILL TO FIX ALL THESE */
    /*if (genderSelector.getText().equals("Switch to Male")) {
      HashMap<Color,Color> tmpSwaps = new HashMap<Color,Color>();
      tmpSwaps.put(new Color(79,39,0), new Color(128,64,0));
      unitSurface.setPaletteSwaps(tmpSwaps);
      unitSurface.applyPaletteSwaps();
    }*/
    /*END KLUDGE */
    
    unitSurface.setPaletteSwaps(paletteSwaps);
    unitSurface.applyPaletteSwaps();
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel.setSurface(unitSurface);
  }
  
  /**
   * Swap between male and female creation.
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
    for (String label : colorNames) {
      Color src = baseColors.get(label);
      Color dest = paletteSwaps.get(src);
      int r = dest.getRed();
      int g = dest.getGreen();
      int b = dest.getBlue();
      colorLabels.get(label).setBackground(dest);
      sliderLabels.get(label)[0].setText(Integer.toString(r));
      sliderLabels.get(label)[1].setText(Integer.toString(g));
      sliderLabels.get(label)[2].setText(Integer.toString(b));
    }
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    
    unitSurface.setPaletteSwaps(paletteSwaps);
    unitSurface.applyPaletteSwaps();
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel.setSurface(unitSurface);
  }
}
