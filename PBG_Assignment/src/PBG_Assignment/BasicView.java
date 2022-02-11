package PBG_Assignment;

import java.awt.*;

import javax.swing.JComponent;

public class BasicView extends JComponent {
    /* Author: Michael Fairbank
     * Creation Date: 2016-01-28
     * Significant changes applied:
     */
    // background colour
    public static final Color BG_COLOR = Color.BLACK;

    private static Main game;
    private static BasicMouseListener mouse;

    public BasicView(Main game, BasicMouseListener mouse) {
        this.game = game;
        this.mouse = mouse;
    }

    @Override
    public void paintComponent(Graphics g0) {
        Main game;
        synchronized (this) {
            game = this.game;
        }

        Graphics2D g = (Graphics2D) g0;
        // paint the background
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        //paint the board
        g.setColor(Color.GREEN);
        g.fillRect(game.convertWorldXtoScreenX(1.35f), game.convertWorldYtoScreenY(6.68f), (int) (game.convertWorldLengthToScreenLength(3.85f)), (int) (game.convertWorldLengthToScreenLength(6.2f)));

        int[] xpright = {game.convertWorldXtoScreenX(5.02f), game.convertWorldXtoScreenX(5.48f), game.convertWorldXtoScreenX(5.48f), game.convertWorldXtoScreenX(5.02f)};
        int[] xpbottom = {game.convertWorldXtoScreenX(1.75f), game.convertWorldXtoScreenX(4.77f), game.convertWorldXtoScreenX(5.3f), game.convertWorldXtoScreenX(1.23f)};
        int[] xpLeft = {game.convertWorldXtoScreenX(1.5f), game.convertWorldXtoScreenX(1.02f), game.convertWorldXtoScreenX(1.02f), game.convertWorldXtoScreenX(1.5f)};
        int[] xptop = {game.convertWorldXtoScreenX(1.8f), game.convertWorldXtoScreenX(1.31f), game.convertWorldXtoScreenX(5.34f), game.convertWorldXtoScreenX(4.8f)};
        int[] xpLeftUp = {game.convertWorldXtoScreenX(1.5f), game.convertWorldXtoScreenX(1.02f), game.convertWorldXtoScreenX(1f), game.convertWorldXtoScreenX(1.13f), game.convertWorldXtoScreenX(1.5f)};

        int[] ypR2 = {game.convertWorldYtoScreenY(3.86f), game.convertWorldYtoScreenY(3.26f), game.convertWorldYtoScreenY(6.84f), game.convertWorldYtoScreenY(6.25f)};
        int[] ypR1 = {game.convertWorldYtoScreenY(0.87f), game.convertWorldYtoScreenY(0.23f), game.convertWorldYtoScreenY(3.9f), game.convertWorldYtoScreenY(3.24f)};
        int[] ypbottom = {game.convertWorldYtoScreenY(0.56f), game.convertWorldYtoScreenY(0.56f), game.convertWorldYtoScreenY(0.23f), game.convertWorldYtoScreenY(0.23f)};
        int[] ypL1 = {game.convertWorldYtoScreenY(0.93f), game.convertWorldYtoScreenY(0.25f), game.convertWorldYtoScreenY(3.98f), game.convertWorldYtoScreenY(3.3f)};
        int[] ypL2 = {game.convertWorldYtoScreenY(3.92f), game.convertWorldYtoScreenY(3.4f), game.convertWorldYtoScreenY(6.87f), game.convertWorldYtoScreenY(6.87f), game.convertWorldYtoScreenY(6.3f)};
        int[] yptop = {game.convertWorldYtoScreenY(6.6f), game.convertWorldYtoScreenY(6.93f), game.convertWorldYtoScreenY(6.93f), game.convertWorldYtoScreenY(6.6f)};

        g.setColor(Color.RED);
        g.fillPolygon(xpright, ypR1, 4); //right up cushion
        g.fillPolygon(xpright, ypR2, 4); //right down cushion
        g.fillPolygon(xpbottom, ypbottom, 4); //bottom cushion
        g.fillPolygon(xpLeft, ypL1, 4); //left down cushion
        g.fillPolygon(xpLeftUp, ypL2, 5); //left up cushion
        g.fillPolygon(xptop, yptop, 4); // top cushion

        g.setColor(new Color(69, 29, 2));
        g.fillRoundRect(game.convertWorldXtoScreenX(5.3f), game.convertWorldYtoScreenY(6.95f), (int) game.convertWorldLengthToScreenLength(0.3f), (int) game.convertWorldLengthToScreenLength(6.73f), 10, 10);    //right cushion length
        g.fillRoundRect(game.convertWorldXtoScreenX(1.01f), game.convertWorldYtoScreenY(6.928f), (int) game.convertWorldLengthToScreenLength(0.3f), (int) game.convertWorldLengthToScreenLength(6.71f), 10, 10);    //left cushion length
        g.fillRoundRect(game.convertWorldXtoScreenX(1.013f), game.convertWorldYtoScreenY(6.95f), (int) game.convertWorldLengthToScreenLength(4.47f), (int) game.convertWorldLengthToScreenLength(0.23f), 10, 10);    // top cushion length
        g.fillRoundRect(game.convertWorldXtoScreenX(1.05f), game.convertWorldYtoScreenY(0.45f), (int) game.convertWorldLengthToScreenLength(4.44f), (int) game.convertWorldLengthToScreenLength(0.23f), 10, 10);    // bottom cusion length

        for (AnchoredBarrier b : game.barriers)
            b.draw(g);

        for (AnchoredBarrier b : game.circularbarrier)
            b.draw(g);

        for (BasicParticle p : game.particles) {
            if (p.body.isActive())
                p.draw(g);
        }

        for (BasicPolygon p : game.polygons)
            p.draw(g);

        if (mouse.isMouseButtonPressed() && mouse.canDraw && game.hasAllTheBallsStopped()) {
            game.canShoot = true;
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            g.drawLine(game.convertWorldXtoScreenX(game.particles.get(15).body.getPosition().x), game.convertWorldYtoScreenY(game.particles.get(15).body.getPosition().y), mouse.mouseX, mouse.mouseY);
        }

        game.score(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return Main.FRAME_SIZE;
    }

    public synchronized void updateGame(Main game) {
        this.game = game;
    }
}