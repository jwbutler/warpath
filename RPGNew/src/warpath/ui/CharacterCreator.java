package warpath.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jwbgl.*;
import warpath.core.RPGDriver;

/** The menu for creating a new character.
 * TODO support saving female characters
 */
public class CharacterCreator extends JPanel implements ActionListener, ChangeListener {
  private Timer frameTimer;
  private Surface unitSurface;
  private Surface unitSurfaceBase;
  private SurfacePanel unitPanel;
  private JPanel sliderPanel;
  private Color savedColor;
  private RPGDriver driver;
  
  private ArrayList<String> colorNames;
  private Hashtable<String, Color> baseColors;
  private Hashtable<Color, Color> paletteSwaps;
  private Hashtable<String, JLabel> colorLabels;
  private Hashtable<String, JSlider[]> colorSliders;
  private Hashtable<String, JLabel[]> sliderLabels;
  private Hashtable<String, JButton> copyButtons;
  private Hashtable<String, JButton> pasteButtons;
  private JButton genderSelector;
  private JButton contButton;
  
  /**
   * TODO: I really don't like passing the Driver as an argument, figure out
   * how to avoid this.
   */
  public CharacterCreator(RPGDriver theDriver, int width, int height) {
	driver = theDriver;
    setSize(width, height);
    setPreferredSize(new Dimension(width, height));
    setVisible(true);
    //gameWindow.add(gamePanel);
    
    setDoubleBuffered(true);
    setBackground(Color.BLACK);
    setLayout(null);
    
    colorNames = new ArrayList<String>();
    colorNames.add("Hair");
    colorNames.add("Face");
    colorNames.add("Eyes");
    colorNames.add("Mouth");
    colorNames.add("Shirt 1");
    colorNames.add("Shirt 2");
    colorNames.add("Shirt 3");
    colorNames.add("Hands");
    colorNames.add("Belt");
    colorNames.add("Skirt");
    colorNames.add("Legs");
    colorNames.add("Boots 1");
    colorNames.add("Boots 2");
    
    baseColors = new Hashtable<String, Color>();
    colorSliders = new Hashtable<String, JSlider[]>();
    sliderLabels = new Hashtable<String, JLabel[]>();
    
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
    colorLabels = new Hashtable<String,JLabel>();
    unitSurfaceBase = new Surface("player_standing_E_1.png");
    try {
      unitSurface = unitSurfaceBase.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    
    savedColor = null;
    copyButtons = new Hashtable<String,JButton>();
    pasteButtons = new Hashtable<String,JButton>();

    unitSurface = unitSurface.scale2x().scale2x();
    paletteSwaps = new Hashtable<Color,Color>();
    
    unitPanel = new SurfacePanel(unitSurface);

    unitPanel.setLayout(null);
    int w = unitSurface.getWidth();
    int h = unitSurface.getHeight();
    int l = (int)(getWidth()*0.30 - unitSurface.getWidth())/2;
    int t = (int)(getHeight()*0.30);
    unitPanel.setBounds(l, t, w, h);
    add(unitPanel);
    
    genderSelector = new JButton("Switch to Female");
    
    genderSelector.addActionListener(this);
    w = (int)(getWidth()*.24); h = (int)(getHeight()*.09);
    l = (int)(getWidth()*0.30 - w)/2;
    t = (int)(getHeight()*0.65);
    genderSelector.setBounds(l,t,w,h);
    add(genderSelector);
    
    //Dealing with transition from CC to Game
    contButton = new JButton("Continue...");
    w = (int)(getWidth()*.24); h = (int)(getHeight()*.09);
    l = (int)(getWidth()*0.30 - w)/2;
    t = (int)(getHeight()*.80);
    contButton.setBounds(l,t,w,h);
    this.add(contButton);
    
    // When the continue button is pressed, export color swaps and move to game panel
    contButton.addActionListener(driver);
    
    
    // add the surface
    sliderPanel = new JPanel();
    sliderPanel.setLayout(new GridLayout(colorNames.size(), 5, 5, 5));
    sliderPanel.setBounds((int)(getWidth()*0.30), 0, (int)(getWidth()*0.70), (int)(getHeight()*1));
    sliderPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    int i = 0;
    for (String name : colorNames) {
      Color c = baseColors.get(name);
      paletteSwaps.put(c, c);
      JLabel colorLabel = new JLabel();
      /*
      l = (int)(sliderPanel.getWidth()*0.01);
      t = 35*i + 20;
      w = (int)(sliderPanel.getWidth()*0.12);
      h = 25;
      colorLabel.setBounds(l,t,w,h);*/
      colorLabel.setForeground(Color.WHITE);
      colorLabel.setBackground(c);
      colorLabel.setText(name);
      colorLabel.setOpaque(true);
      colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
      colorLabel.setFont(new Font("Arial",Font.PLAIN, 9));
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
      /*t = 35*i + 20;
      h = 25;
      w = (int)(sliderPanel.getWidth()*0.19);
      l = (int)(sliderPanel.getWidth()*0.14);
      RSlider.setBounds(l,t,w,h);
      l = (int)(sliderPanel.getWidth()*0.34);
      GSlider.setBounds(l,t,w,h);
      l = (int)(sliderPanel.getWidth()*0.54);
      BSlider.setBounds(l,t,w,h);*/
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
      /*t = 35*i + 35;
      h = 15;
      w = (int)(sliderPanel.getWidth()*0.19);
      l = (int)(sliderPanel.getWidth()*0.14);
      RLabel.setBounds(l,t,w,h);
      l = (int)(sliderPanel.getWidth()*0.34);
      GLabel.setBounds(l,t,w,h);
      l = (int)(sliderPanel.getWidth()*0.54);
      BLabel.setBounds(l,t,w,h);*/
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
      /*t = 35*i + 20;
      h = 25;
      w = (int)(sliderPanel.getWidth()*0.11);
      l = (int)(sliderPanel.getWidth()*0.74);
      copyButton.setBounds(l,t,w,h);*/
      copyButton.setFont(new Font("Arial",Font.PLAIN, 9));
      JButton pasteButton = new JButton("Paste");
      pasteButton.setActionCommand("Paste_"+name);
      pasteButton.addActionListener(this);
      pasteButtons.put(name, pasteButton);
      sliderPanel.add(pasteButton);
      /*l = (int)(sliderPanel.getWidth()*0.86);
      pasteButton.setBounds(l,t,w,h);*/
      pasteButton.setFont(new Font("Arial",Font.PLAIN, 9));
      i++;
    }
    add(sliderPanel);
    frameTimer = new Timer(50, this);
    frameTimer.start();
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
        doCopy(name);
      } else if (e.getActionCommand().startsWith("Paste")) {
        String name = e.getActionCommand().substring(6, e.getActionCommand().length());
        doPaste(name);
      }
    } else {
      repaint();
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
  public void doCopy(String name) {
    Color c = paletteSwaps.get(baseColors.get(name));
    savedColor = new Color(c.getRGB());
  }
  
  /**
   * Sets the specified color sliders to the stored value, if it exists.
   * @param name - the name of the color (e.g. "Shirt 1") 
   */
  public void doPaste(String name) {
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
      Hashtable<Color,Color> tmpSwaps = new Hashtable<Color,Color>();
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
  
  /** Swap between male and female creation. */
  public void doGender() {
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
      Hashtable<Color,Color> tmpSwaps = new Hashtable<Color,Color>();
      tmpSwaps.put(new Color(79,39,0), new Color(128,64,0));
      unitSurface.setPaletteSwaps(tmpSwaps);
      unitSurface.applyPaletteSwaps();
    }*/
    /* END KLUDGE */
    
    unitSurface.setPaletteSwaps(paletteSwaps);
    unitSurface.applyPaletteSwaps();
    unitSurface = unitSurface.scale2x().scale2x();
    unitPanel.setSurface(unitSurface);
    /*
    int w = unitSurface.getWidth();
    int h = unitSurface.getHeight();
    int l = (int)(ccPanel.getWidth()*0.40 - unitSurface.getWidth())/2;
    int t = (int)(ccPanel.getHeight()*0.40);
    unitPanel.setBounds(l, t, w, h);*/
  }
  
  /** Returns the set of palette swaps that the user has created. */
  public Hashtable<Color, Color> exportPaletteSwaps() {
    return paletteSwaps;
  }
}
