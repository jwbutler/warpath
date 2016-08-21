package jwbgl;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

/** This class is used for every image that gets rendered.  It's basically just
 * an extension of the AWT BufferedImage class.  Allows for scaling
 * (especially 2x), palette swaps, transparency, a few other things? */

public class Surface {
  private BufferedImage image;
  private Color colorkey;
  private String imagePath;
  private Rect transparencyRect;
  private Map<Color,Color> paletteSwaps;
  private final static Color DEFAULT_COLOR = Color.BLACK;
  private final static Color TRANSPARENT_WHITE = new Color(0x00FFFFFF, true);

  public Surface(int width, int height) {
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics g = image.getGraphics();
    g.setColor(DEFAULT_COLOR);
    g.fillRect(0, 0, width, height);
    g.dispose();
    colorkey = null;
    this.imagePath = null;
    transparencyRect = new Rect(0, 0, getWidth(), getHeight());
    paletteSwaps = new HashMap<>();
  }
  
  public Surface(BufferedImage image) {
    this.image = image;
    colorkey = null;
    this.imagePath = null;
    transparencyRect = new Rect(0, 0, getWidth(), getHeight());
  }
  
  public Surface(String imagePath) {
    this.imagePath = imagePath;
    try {
      // Copy the source image into a new image with transparency
      BufferedImage tmp = ImageIO.read(new File("png" + File.separator + imagePath));
      int width = tmp.getWidth();
      int height = tmp.getHeight();
      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();
      g.setColor(DEFAULT_COLOR);
      g.fillRect(0, 0, width, height);
      //g.dispose();
      colorkey = null;
      load(imagePath);
      transparencyRect = new Rect(0, 0, getWidth(), getHeight());
      
    } catch (IOException e) {
      // invalid path
      System.out.println("Invalid path");
      System.out.println("&&&&& " + "png" + File.separator + imagePath);
      return;
    }

  }
  
  public boolean load(String imagePath) {
    // TODO: Exceptions etc
    this.imagePath = imagePath;
    BufferedImage tmpImage;
    try {
      // Copy the source image into a new image w/ transparency
      tmpImage = ImageIO.read(new File("png" + File.separator + imagePath));
      //tmp = ImageIO.read(new File("png" + File.separator + imagePath));
    } catch (IOException e) {
      System.out.println("Invalid path");
      System.out.println("##### " + "png" + File.separator + imagePath);
      return false;
    }
    
    // If the width/height don't match predefined values, return an error
    // It might be wiser to just accommodate them, but...
    if (tmpImage.getWidth() != image.getWidth()) {
      System.out.println("Invalid width");
      return false;
    }
    if (tmpImage.getHeight() != image.getHeight()) {
      System.out.println("Invalid height");
      return false;
    }
    Graphics g = image.getGraphics();
    g.drawImage(tmpImage, 0, 0, null);
    g.dispose();
    if (colorkey != null) {
      applyColorkey();
    }
    return true;
  }
  
  public void applyColorkey() {
    // Converts all pixels of the chosen color key to fully transparent
    // (more specifically, transparent white). 
    // Learn the filter shit later
    Color c;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        c = new Color(image.getRGB(x,y),true);
        if ((c.equals(colorkey) || c.getAlpha()==0)) {
          image.setRGB(x, y, TRANSPARENT_WHITE.getRGB());
        }
      }
    }
    findTransparencyRect();
  }
  
  public void draw(Graphics g, int left, int top, float alpha) {
    // this seems really shitty.
    Graphics2D g2 = (Graphics2D) g;
    Composite c = g2.getComposite();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    g.drawImage(image, left, top, null);
    g2.setComposite(c);
  }
  
  public void draw(Graphics g, Posn posn) {
    draw(g, posn.getX(), posn.getY());
  }

    
  public void draw(Graphics g, int left, int top) {
    draw(g, left, top, 1.0f);
  }
  
  public Surface clone() throws CloneNotSupportedException {
    // remember to update this when we allow for palette swaps 
    Surface newSurface = new Surface(image.getWidth(), image.getHeight());
    BufferedImage newImage = new BufferedImage(image.getColorModel(), image.copyData(null),
    image.isAlphaPremultiplied(), null);
    newSurface.setImage(newImage);
    
    //newSurface.setImage((BufferedImage)image.clone());
    newSurface.setColorkey(this.colorkey);
    return newSurface;
  }

  public Surface scale(double tx, double ty) {
    // Scales the surface up by a factor of 2 in each dimension.
    Surface newSurface = new Surface((int)(getWidth() * tx), (int)(getHeight() * ty));
    Graphics g = newSurface.getGraphics();
    g.drawImage(this.image, 0, 0, (int)(getWidth() * tx), (int)(getHeight() * ty), null);
    newSurface.setColorkey(this.colorkey);
    return newSurface;
  }
  
  public Surface scale2x() {
    // Scales the surface up by a factor of 2 in each dimension.
    Surface newSurface = new Surface(getWidth()*2, getHeight()*2);
    Graphics g = newSurface.getGraphics();
    g.drawImage(this.image, 0, 0, getWidth()*2, getHeight()*2, null);
    newSurface.setColorkey(this.colorkey);
    return newSurface;
  }
  
  public void applyPaletteSwaps() {
    BufferedImage swappedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    swappedImg.getGraphics().drawImage(image, 0, 0, null);
    
    //Graphics g = getGraphics();
    int[] pixels = image.getRGB(0,  0, getWidth(), getHeight(), null, 0, getWidth());
    Color color;
    for (int j = 0; j < getHeight(); j++) {  
      for (int i = 0; i < getWidth(); i++) {
        for (Entry<Color, Color> e: paletteSwaps.entrySet()) {
          Color src = e.getKey(), dest = e.getValue();
          color = new Color(pixels[j*getWidth() + i], true);
          if (color.equals(src)) {
            swappedImg.setRGB(i, j, dest.getRGB());
            //Color c = new Color(dest.getRGB());
            //System.out.println(i + " " + j + " " + c);
            //System.out.println(dest.getRGB());
            break;
          }
        }
      }
    }
    image = swappedImg;
  }

  // ===== ACCESSOR METHODS =====

  public Map<Color, Color> getPaletteSwaps() {
    return paletteSwaps;
  }
  
  public Graphics getGraphics() {
    return image.getGraphics();
  }
  
  public int getWidth() {
    return image.getWidth();
  }
  public int getHeight() {
    return image.getHeight();
  }
  
  public int getRGB(int x, int y) {
    return image.getRGB(x, y);
  }

  public Color getColorkey() {
    return colorkey;
  }
  
  /**
   * NB: DOES apply palette swaps
   */
  public void setColorkey(Color colorkey) {
    this.colorkey = colorkey;
    applyColorkey();
  }

  public BufferedImage getImage() {
    return image;
  }
  
  public void setImage(BufferedImage image) {
    this.image = image;
  }
  
  public void findTransparencyRect() {
    // This is meant to find the portion of the image that contains
    // non-transparent pixels. It's good for unit selection
    int left, top, right, bottom;
    left = getWidth();
    top = getHeight();
    right = 0;
    bottom = 0;
    Color c;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        c = new Color(image.getRGB(x,y), true);
        //if (!c.equals(colorkey)) {
        //if (!c.equals(TRANSPARENT_WHITE)) {
        if ((!c.equals(colorkey)) && (!c.equals(TRANSPARENT_WHITE))) {
          if (x < left) {
            left = x;
          } 
          if (y < top) {
            top = y;
          }
          if (x > right) {
            right = x;
          }
          if (y > bottom) {
            bottom = y;
          }
        }
      }
    }
    int width = right-left+1;
    int height = bottom-top+1;
    transparencyRect = new Rect(left,top,width,height);
  }

  public Rect getTransparencyRect() {
    return transparencyRect;
  }
  
  /**
   * NB: Does not apply the palette swaps
   */
  public void setPaletteSwaps(Map<Color,Color> paletteSwaps) {
    this.paletteSwaps = paletteSwaps;
  }
  
  public Surface rotate(double degrees, double anchorX, double anchorY) {
    //double rads = (-degrees+90)*Math.PI/180;
    double rads = degrees*Math.PI/180;
    AffineTransform t = AffineTransform.getRotateInstance(rads, getWidth()*anchorX, getHeight()*anchorY);
    AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);
    Rectangle2D bounds = op.getBounds2D(image);
    int tx = (int)bounds.getX();
    int ty = (int)bounds.getY();
    //System.out.println(tx + " " + ty);
    AffineTransform t2 = AffineTransform.getTranslateInstance(-tx,-ty);
    t.preConcatenate(t2);
    op = new AffineTransformOp(t, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    BufferedImage newImage = op.createCompatibleDestImage(image, image.getColorModel());
    op.filter(image, newImage);
    //System.out.println(op.getBounds2D(image));
    Surface newSurface = new Surface(newImage);
    newSurface.setColorkey(colorkey);
    //newSurface.applyColorkey();
    return newSurface;
  }
  
  public Surface rotate(double degrees) {
    //return rotate(degrees, 0.5, 0.5);
    return rotate(degrees, 0.5, 0.5);
  }
  
  /**
   * TODO test me
   */
  public void blit(Surface other, Posn posn) {
    other.draw(getGraphics(), posn);
  }
  public void blit(Surface other) {
    other.draw(getGraphics(), new Posn(0,0));
  }
  
}
