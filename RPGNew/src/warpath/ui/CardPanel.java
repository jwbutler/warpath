package warpath.ui;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;

import warpath.core.RPG;
import warpath.ui.components.UnitCard;
import warpath.units.Unit;

/** Holds a set of unit cards.
 * Unused in the current engine.
 */
public class CardPanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private final RPG game;
  private static final int MARGIN = 10;
  private final ArrayList<UnitCard> cards;
  public CardPanel(RPG game, int width, int height) {
    super();
    this.game = game;
    setSize(width, height);
    setBackground(Color.DARK_GRAY);
    setLayout(null);
    cards = new ArrayList<UnitCard>();
    for (Unit u: game.getHumanPlayer().getUnits()) {
      addCard(u);
    }

  }
  
  public void addCard(Unit u) {
    int cardWidth = getWidth()/5 - (MARGIN*5/6);
    int cardHeight = getHeight() - 2*MARGIN;
    int cardX = cardWidth*cards.size() + MARGIN*(cards.size()+1);
    int cardY = MARGIN;
    UnitCard card = new UnitCard(game, u, cardWidth, cardHeight);
    //card.setAlignmentX(cardX);
    //card.setAlignmentY(cardY);
    this.add(card);
    card.setBounds(cardX, cardY, cardWidth, cardHeight);
    cards.add(card);
  }
  
}
