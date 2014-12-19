import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CharacterCreator extends JFrame implements ActionListener, ChangeListener {
  private JPanel ccPanel;
  private Timer frameTimer;
  private Surface unitSurface;
  private Surface unitSurfaceBase;
  private SurfacePanel unitPanel;
  private JPanel sliderPanel;
  private Color savedColor;
  private RPG game;
  
  private ArrayList<String> colorNames;
  private HashMap<String, Color> baseColors;
  private HashMap<Color, Color> paletteSwaps;
  private HashMap<String, JLabel> colorLabels;
  private HashMap<String, JSlider[]> colorSliders;
  private HashMap<String, JLabel[]> sliderLabels;
  private HashMap<String, JButton> copyButtons;
  private HashMap<String, JButton> pasteButtons;
  private JButton genderSelector;
  
  public CharacterCreator() {
    setSize(RPG.DEFAULT_WIDTH, RPG.DEFAULT_HEIGHT);
    setVisible(true);
    getContentPane().setLayout(null);
    //gamePanel = new GamePanel(this, DEFAULT_WIDTH, DEFAULT_HEIGHT - HUD_PANEL_HEIGHT);
    setSize(RPG.DEFAULT_WIDTH, 2*RPG.DEFAULT_HEIGHT - getContentPane().getHeight());
    //gameWindow.add(gamePanel);
    setResizable(false);
    ccPanel = new JPanel();
    
    ccPanel.setSize(RPG.DEFAULT_WIDTH, RPG.DEFAULT_HEIGHT);
    ccPanel.setVisible(true);
    ccPanel.setDoubleBuffered(true);
    ccPanel.setBackground(Color.BLACK);
    ccPanel.setLayout(null);
    getContentPane().add(ccPanel);
    
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    savedColor = null;
    copyButtons = new HashMap<String,JButton>();
    pasteButtons = new HashMap<String,JButton>();

    unitSurface = unitSurface.scale2x().scale2x();
    paletteSwaps = new HashMap<Color,Color>();
    
    unitPanel = new SurfacePanel(unitSurface);

    unitPanel.setLayout(null);
    int w = unitSurface.getWidth();
    int h = unitSurface.getHeight();
    int l = (int)(ccPanel.getWidth()*0.32 - unitSurface.getWidth())/2;
    int t = (int)(ccPanel.getHeight()*0.40);
    unitPanel.setBounds(l, t, w, h);
    ccPanel.add(unitPanel);
    
    genderSelector = new JButton("Switch to Female");
    
    genderSelector.addActionListener(this);
    l = (int)(ccPanel.getWidth()*0.32 - 150)/2;
    t = (int)(ccPanel.getHeight()*0.80);
    w = 150; h = 30;
    genderSelector.setBounds(l,t,w,h);
    ccPanel.add(genderSelector);
    // add the surface
    sliderPanel = new JPanel();
    sliderPanel.setLayout(null);
    sliderPanel.setBounds((int)(ccPanel.getWidth()*0.32), 0, (int)(ccPanel.getWidth()*0.68),
    ccPanel.getHeight());

    int i = 0;
    for (String name : colorNames) {
      Color c = baseColors.get(name);
      paletteSwaps.put(c, c);
      JLabel colorLabel = new JLabel();
      l = (int)(sliderPanel.getWidth()*0.01);
      t = 35*i + 20;
      w = (int)(sliderPanel.getWidth()*0.12);
      h = 25;
      colorLabel.setBounds(l,t,w,h);
      colorLabel.setForeground(Color.WHITE);
      colorLabel.setBackground(c);
      colorLabel.setText(name);
      colorLabel.setOpaque(true);
      colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
      t = 35*i + 20;
      h = 25;
      w = (int)(sliderPanel.getWidth()*0.19);
      l = (int)(sliderPanel.getWidth()*0.14);
      RSlider.setBounds(l,t,w,h);
      l = (int)(sliderPanel.getWidth()*0.34);
      GSlider.setBounds(l,t,w,h);
      l = (int)(sliderPanel.getWidth()*0.54);
      BSlider.setBounds(l,t,w,h);
      RSlider.setOpaque(false);
      GSlider.setOpaque(false);
      BSlider.setOpaque(false);
      
      RSlider.addChangeListener(this);
      GSlider.addChangeListener(this);
      BSlider.addChangeListener(this);
      sliderPanel.add(RSlider);
      sliderPanel.add(GSlider);
      sliderPanel.add(BSlider);
      JLabel RLabel = new JLabel();
      RLabel.setText(Integer.toString(RSlider.getValue()));
      RLabel.setHorizontalAlignment(SwingConstants.CENTER);
      JLabel GLabel = new JLabel();
      GLabel.setText(Integer.toString(GSlider.getValue()));
      GLabel.setHorizontalAlignment(SwingConstants.CENTER);
      JLabel BLabel = new JLabel();
      BLabel.setText(Integer.toString(BSlider.getValue()));
      BLabel.setHorizontalAlignment(SwingConstants.CENTER);
      t = 35*i + 35;
      h = 15;
      w = (int)(sliderPanel.getWidth()*0.19);
      l = (int)(sliderPanel.getWidth()*0.14);
      RLabel.setBounds(l,t,w,h);
      l = (int)(sliderPanel.getWidth()*0.34);
      GLabel.setBounds(l,t,w,h);
      l = (int)(sliderPanel.getWidth()*0.54);
      BLabel.setBounds(l,t,w,h);
      JLabel[] labels = {RLabel, GLabel, BLabel};
      sliderLabels.put(name, labels);
      
      JButton copyButton = new JButton("Copy");
      copyButton.setActionCommand("Copy_"+name);
      copyButton.addActionListener(this);
      copyButtons.put(name, copyButton);
      sliderPanel.add(copyButton);
      t = 35*i + 20;
      h = 25;
      w = (int)(sliderPanel.getWidth()*0.11);
      l = (int)(sliderPanel.getWidth()*0.74);
      copyButton.setBounds(l,t,w,h);
      Font f = new Font("Arial",Font.PLAIN, 9);
      copyButton.setFont(f);
      JButton pasteButton = new JButton("Paste");
      pasteButton.setActionCommand("Paste_"+name);
      pasteButton.addActionListener(this);
      pasteButtons.put(name, pasteButton);
      sliderPanel.add(pasteButton);
      l = (int)(sliderPanel.getWidth()*0.86);
      pasteButton.setBounds(l,t,w,h);
      pasteButton.setFont(f);
      sliderPanel.add(RLabel);
      sliderPanel.add(GLabel);
      sliderPanel.add(BLabel);
      i++;
    }
    ccPanel.add(sliderPanel);
    frameTimer = new Timer(50, this);
    frameTimer.start();
  }
  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
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
      unitPanel.repaint();
      sliderPanel.repaint();
      ccPanel.repaint();
    }
  }
  @Override
  public void stateChanged(ChangeEvent e) {
    // TODO Auto-generated method stub
    updateColors();
  }
  
  public void doCopy(String name) {
    Color c = paletteSwaps.get(baseColors.get(name));
    savedColor = new Color(c.getRGB());
  }
  
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
      // TODO Auto-generated catch block
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
    /*
    int w = unitSurface.getWidth();
    int h = unitSurface.getHeight();
    int l = (int)(ccPanel.getWidth()*0.40 - unitSurface.getWidth())/2;
    int t = (int)(ccPanel.getHeight()*0.40);
    unitPanel.setBounds(l, t, w, h);*/
  }
  
  public HashMap<Color, Color> exportPaletteSwaps() {
    return paletteSwaps;
  }
}
