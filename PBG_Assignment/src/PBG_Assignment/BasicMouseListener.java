package PBG_Assignment;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.joints.MouseJoint;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BasicMouseListener extends MouseInputAdapter implements MouseListener {
    /* Author: Michael Fairbank
     * Creation Date: 2016-01-28
     * Significant changes applied: 2016-02-10 added mouseJoint code to allow dragging of bodies
     */
    public static int mouseX, mouseY;
    private static boolean mouseButtonPressed;
    public boolean canDraw;

    private static MouseJoint mouseJoint;

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseButtonPressed = false;
        //System.out.println("Move event: "+getWorldCoordinatesOfMousePointer());
        if (mouseJoint != null) {
            // we're obviously not dragging any more, so drop the current mouseJoint
            linkMouseDragEventToANewMouseJoint(null);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        mouseX = e.getX();
        mouseY = e.getY();
        mouseButtonPressed = true;
        //System.out.println("pressed");


    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseButtonPressed = false;
        //System.out.println("Released");
    }

    public static boolean isMouseButtonPressed() {
        return mouseButtonPressed;
    }

    public Vec2 getWorldCoordinatesOfMousePointer() {
        return new Vec2(Main.convertScreenXtoWorldX(mouseX), Main.convertScreenYtoWorldY(mouseY));
    }

    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseButtonPressed = true;

        canDraw = true;

        //System.out.println("dragged");


    }

    public static void linkMouseDragEventToANewMouseJoint(MouseJoint mj) {
        if (mouseJoint != null) {
            // tidy up and destroy old one
            Main.world.destroyJoint(mouseJoint);
            mouseJoint = null;
        }
        mouseJoint = mj;
    }


    private static final AABB queryAABB = new AABB();  // This is an axis aligned bounding box (AABB)
    private static final TestQueryCallback callback = new TestQueryCallback();


    public static Body findBodyAtWorldCoords(Vec2 worldCoords) {
        // Set up a tiny axis aligned bounding box around the tiny area of screen around the mouse pointer:
        queryAABB.lowerBound.set(worldCoords.x - .001f, worldCoords.y - .001f);
        queryAABB.upperBound.set(worldCoords.x + .001f, worldCoords.y + .001f);
        callback.point.set(worldCoords);
        callback.fixture = null;
        // Now ask the world object which bodies are positioned at the point of the screen we are interested in:
        Main.world.queryAABB(callback, queryAABB);

        if (callback.fixture != null) {
            Body body = callback.fixture.getBody();
            return body;
        } else
            return null;
    }

    private static class TestQueryCallback implements QueryCallback {
        // This is a callback class we need to use when we are querying which objects lie under the point of the screen that we are interested in?
        public final Vec2 point;
        public Fixture fixture;

        public TestQueryCallback() {
            point = new Vec2();
            fixture = null;
        }

        @Override
        public boolean reportFixture(Fixture argFixture) {
            Body body = argFixture.getBody();
            if (body.getType() == BodyType.DYNAMIC) {
                boolean inside = argFixture.testPoint(point);
                if (inside) {
                    fixture = argFixture;

                    return false;
                }
            }

            return true;
        }
    }
}
