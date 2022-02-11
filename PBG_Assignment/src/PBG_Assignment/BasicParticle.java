package PBG_Assignment;

import java.awt.*;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class BasicParticle {
    /* Author: Michael Fairbank
     * Creation Date: 2016-02-05 (JBox2d version)
     * Significant changes applied:
     */
    public final int SCREEN_RADIUS;

    private final float rollingFriction, mass;

    protected final Body body;
    private final int num;


    public BasicParticle(float sx, float sy, float vx, float vy, float radius, float mass, float rollingFriction, int num) {
        this.num = num;
        World w = Main.world; // a Box2D object
        BodyDef bodyDef = new BodyDef();  // a Box2D object
        bodyDef.type = BodyType.DYNAMIC;
        // this says the physics engine is to move it automatically
        bodyDef.position.set(sx, sy);
        bodyDef.linearVelocity.set(vx, vy);
        this.body = w.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();// This class is from Box2D
        circleShape.m_radius = radius;
        FixtureDef fixtureDef = new FixtureDef();// This class is from Box2D
        fixtureDef.shape = circleShape;
        fixtureDef.density = (float) (mass / (Math.PI * radius * radius));
        fixtureDef.friction = 0.4f;// this is surface friction;
        fixtureDef.restitution = 0.999f;
        body.createFixture(fixtureDef);
        body.setUserData(this);
        body.getContactList();
        this.rollingFriction = rollingFriction;
        this.mass = mass;
        this.SCREEN_RADIUS = (int) Math.max(Main.convertWorldLengthToScreenLength(radius), 1);
    }

    public void draw(Graphics2D g) {
        int x = Main.convertWorldXtoScreenX(body.getPosition().x);
        int y = Main.convertWorldYtoScreenY(body.getPosition().y);
        Color col = null;

        if (num == 1 || num == 9) {
            col = Color.YELLOW;
        }
        if (num == 2 || num == 10) {
            col = Color.BLUE;
        }
        if (num == 3 || num == 11) {
            col = Color.ORANGE;
        }
        if (num == 4 || num == 12) {
            col = new Color(128, 0, 128);
        }
        if (num == 5 || num == 13) {
            col = new Color(238, 115, 79);
        }
        if (num == 6 || num == 14) {
            col = new Color(0, 100, 0);
        }
        if (num == 7 || num == 15) {
            col = new Color(128, 0, 0);
        }
        if (num == 0) {
            col = Color.WHITE;
        }
        if (num == 8) {
            col = new Color(0, 0, 0);
        }

        g.setFont(new Font("Arial", Font.BOLD, 10));

        if (num > 0 && num <= 8) {

            g.setColor(col);
            g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);

            g.setColor(Color.WHITE);
            g.fillOval(x - 6, y - 6, SCREEN_RADIUS, SCREEN_RADIUS);

            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(num), x - 3, y + 4);
        }

        if (num > 8 && num <= 15) {
            if (num == 9) {
                g.setColor(Color.WHITE);
                g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);

                g.setColor(col);
                g.fillRoundRect((int) (x - 11.75), y - 6, (int) (SCREEN_RADIUS + 12.5), (int) (SCREEN_RADIUS + 1.5), 15, 7);

                g.setColor(Color.WHITE);
                g.fillOval(x - 6, y - 6, SCREEN_RADIUS, SCREEN_RADIUS);

                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(num), x - 3, y + 4);
            } else {
                //Stripes
                g.setColor(Color.WHITE);
                g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);

                g.setColor(col);
                g.fillRoundRect((int) (x - 11.75), y - 6, (int) (SCREEN_RADIUS + 12.5), (int) (SCREEN_RADIUS + 1.5), 15, 7);

                g.setColor(Color.WHITE);
                g.fillOval(x - 6, y - 6, SCREEN_RADIUS, SCREEN_RADIUS);

                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(num), (float) (x - 6), y + 4);
            }
        }

        if (num == 0) {
            g.setColor(col);
            g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);

            g.setColor(Color.BLACK);
            g.fillOval(x - 1, y - 1, 5, 5);
        }
    }

    public void notificationOfNewTimestep() {
        if (rollingFriction > 0) {
            Vec2 rollingFrictionForce = new Vec2(body.getLinearVelocity());
            rollingFrictionForce = rollingFrictionForce.mul(-rollingFriction * mass);
            body.applyForceToCenter(rollingFrictionForce);
        }
    }


}
