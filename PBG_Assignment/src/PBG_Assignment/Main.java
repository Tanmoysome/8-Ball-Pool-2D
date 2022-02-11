package PBG_Assignment;

import java.awt.*;
import java.util.*;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.MouseJoint;


public class Main {
    /* Author: Michael Fairbank
     * Creation Date: 2016-02-05 (JBox2d version)
     * Significant changes applied:
     */

    // frame dimensions
    public static final int SCREEN_HEIGHT = 650;
    public static final int SCREEN_WIDTH = 900;
    public static final Dimension FRAME_SIZE = new Dimension(
            SCREEN_WIDTH, SCREEN_HEIGHT);
    public static final float WORLD_WIDTH = 10;//metres
    public static final float WORLD_HEIGHT = SCREEN_HEIGHT * (WORLD_WIDTH / SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
    public static final float GRAVITY = 0;
    public static final boolean ALLOW_MOUSE_POINTER_TO_DRAG_BODIES_ON_SCREEN = false;// There's a load of code in basic mouse listener to process this, if you set it to true

    public static World world; // Box2D container for all bodies and barriers

    // sleep time between two drawn frames in milliseconds
    public static final int DELAY = 15;
    public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH = 30;
    // estimate for time between two frames in seconds
    public static final float DELTA_T = DELAY / 1000.0f;

    public static float Radius = .14f;
    int count = 0;
    public static boolean canShoot = false;
    public static boolean increase;
    BasicMouseListener mouselistener = new BasicMouseListener();

    private boolean collisionActive = false;
    private int ballcount = 0;
    private boolean isInvalidPot;


    public static int convertWorldXtoScreenX(float worldX) {
        return (int) (worldX / WORLD_WIDTH * SCREEN_WIDTH);
    }

    public static int convertWorldYtoScreenY(float worldY) {
        // minus sign in here is because screen coordinates are upside down.
        return (int) (SCREEN_HEIGHT - (worldY / WORLD_HEIGHT * SCREEN_HEIGHT));
    }

    public static float convertWorldLengthToScreenLength(float worldLength) {
        return (worldLength / WORLD_WIDTH * SCREEN_WIDTH);
    }

    public static float convertScreenXtoWorldX(int screenX) {
        return screenX * WORLD_WIDTH / SCREEN_WIDTH;
    }

    public static float convertScreenYtoWorldY(int screenY) {
        return (SCREEN_HEIGHT - screenY) * WORLD_HEIGHT / SCREEN_HEIGHT;
    }

    public List<BasicParticle> particles;
    public List<BasicPolygon> polygons;
    public List<AnchoredBarrier> barriers;

    public List<AnchoredBarrier> circularbarrier;
    public static MouseJoint mouseJointDef;

    public static enum LayoutMode {RECTANGLE, SNOOKER_TABLE}

    ;

    public Main() {
        world = new World(new Vec2(0, -GRAVITY));// create Box2D container for everything
        world.setContinuousPhysics(true);

        particles = new ArrayList<BasicParticle>();
        polygons = new ArrayList<BasicPolygon>();
        barriers = new ArrayList<AnchoredBarrier>();

        circularbarrier = new ArrayList<AnchoredBarrier>();

        LayoutMode layout = LayoutMode.SNOOKER_TABLE;

        float rollingFriction = .02f;

        float s = 1.2f;
        //particles.add(new BasicParticle(WORLD_WIDTH/2-2,WORLD_HEIGHT/2-2.2f,1.5f*s,1.2f*s, r,Color.GREEN, 1, 0.99f));

        barriers = new ArrayList<AnchoredBarrier>();

        switch (layout) {
            case RECTANGLE: {
                // rectangle walls:
                // anticlockwise listing
                // These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints.
                barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
                barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
                break;
            }


            case SNOOKER_TABLE: {
                // These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints.
                float snookerTableHeight = WORLD_HEIGHT;
                float pocketSize = 0.4f;
                float cushionDepth = 0.3f;
                float cushionLength = snookerTableHeight / 2 - pocketSize - cushionDepth;
                float snookerTableWidth = cushionLength + cushionDepth * 2 + pocketSize * 2;

                Color pocketcolor = new Color(255, 255, 255);

                circularbarrier.add(new AnchoredBarrier_Curve((snookerTableWidth - cushionDepth / 2) + 0.95f, snookerTableHeight - 6.7f, 0.2f, 0, 360, pocketcolor));
                createCushion(barriers, snookerTableWidth - cushionDepth / 2 + 1f, snookerTableHeight * 0.25f + 0.25f, 0, cushionLength - 0.55f, cushionDepth);
                circularbarrier.add(new AnchoredBarrier_Curve((snookerTableWidth - cushionDepth / 2) + 1.05f, snookerTableHeight - 3.67f, 0.2f, 0, 360, pocketcolor));
                createCushion(barriers, snookerTableWidth - cushionDepth / 2 + 1f, snookerTableHeight * 0.75f - 0.35f, 0, cushionLength - 0.55f, cushionDepth);
                circularbarrier.add(new AnchoredBarrier_Curve(snookerTableWidth - cushionDepth / 2 + 1f, snookerTableHeight - 0.62f, 0.2f, 0, 360, pocketcolor));
                createTopCushion(barriers, snookerTableWidth / 2 + 1.15f, snookerTableHeight - cushionDepth / 2 - 0.33f, Math.PI / 2, cushionLength + 0.1f, cushionDepth); // top barrier cushion


                circularbarrier.add(new AnchoredBarrier_Curve(snookerTableWidth - cushionDepth / 2 - 2.7f, snookerTableHeight - 0.55f, 0.2f, 0, 360, pocketcolor));
                createLeftCushion(barriers, cushionDepth / 2 + 1.2f, snookerTableHeight * 0.25f + 0.29f, Math.PI, cushionLength - 0.55f, cushionDepth);
                circularbarrier.add(new AnchoredBarrier_Curve(snookerTableWidth - cushionDepth / 2 - 2.867f, snookerTableHeight - 3.62f, 0.2f, 0, 360, pocketcolor));
                createLeftCushion(barriers, cushionDepth / 2 + 1.2f, snookerTableHeight * 0.75f - 0.29f, Math.PI, cushionLength - 0.55f, cushionDepth);
                circularbarrier.add(new AnchoredBarrier_Curve(snookerTableWidth - cushionDepth / 2 - 2.76f, snookerTableHeight - 6.68f, 0.2f, 0, 360, pocketcolor));
                createBottomCushion(barriers, snookerTableWidth / 2 + 1.1f, cushionDepth / 2 + 0.27f, Math.PI * 3 / 2, cushionLength + 0.1f, cushionDepth);

                InitBall(2.81f, 5.9f);

                BasicParticle cueBall = new BasicParticle(snookerTableWidth - 1f, 1.844f, 0, 0, 0.14f, 4, 0.9999f, 0);
                particles.add(cueBall);

                break;
            }
        }
    }

    public void InitBall(float x, float y) {
        float updatedX = x;
        float offsetX = Radius * 2;
        float offsetY = Radius + 0.1f;
        int num = 0;
        int[] ranNum = new int[]{5, 10, 11, 1, 14, 3, 13, 6, 15, 7, 8, 2, 9, 4, 12, 7};

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < i; j++) {
                x += Radius;
            }
            for (int k = 0; k < 5 - i; k++) {
                particles.add(new BasicParticle(x, y, 0, 0, Radius, 2, 0.99f, ranNum[num]));
                x += offsetX;
                num++;
            }
            x = updatedX;
            y -= offsetY;
        }
    }

    public boolean hasAllTheBallsStopped() {
        boolean hasStopped = true;
        for (int i = 0; i < particles.size(); i++) {
            if (particles.get(i).body.getLinearVelocity().length() < 0.002f) {
                hasStopped = true;
            } else
                hasStopped = false;
        }
        return hasStopped;
    }

    private void createCushion(List<AnchoredBarrier> barriers, float centrex, float centrey, double orientation, float cushionLength, float cushionDepth) {
        // on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
        Color col = Color.RED;
        Vec2 p1 = new Vec2(cushionDepth / 2, -cushionLength / 2 - cushionDepth / 2);
        Vec2 p2 = new Vec2(-cushionDepth / 2, -cushionLength / 2);
        Vec2 p3 = new Vec2(-cushionDepth / 2, +cushionLength / 2);
        Vec2 p4 = new Vec2(cushionDepth / 2, cushionLength / 2 + cushionDepth / 2);
        p1 = rotateVec(p1, orientation);
        p2 = rotateVec(p2, orientation);
        p3 = rotateVec(p3, orientation);
        p4 = rotateVec(p4, orientation);

        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p1.x) - 0.2f, (float) (centrey + p1.y), (float) (centrex + p2.x) + 0.009f, (float) (centrey + p2.y), col));
        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p2.x), (float) (centrey + p2.y), (float) (centrex + p3.x), (float) (centrey + p3.y), col));
        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p3.x) + 0.009f, (float) (centrey + p3.y), (float) (centrex + p4.x) - 0.2f, (float) (centrey + p4.y), col));

    }

    private void createTopCushion(List<AnchoredBarrier> barriers, float centrex, float centrey, double orientation, float cushionLength, float cushionDepth) {
        // on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
        Color col = Color.RED;
        Vec2 p1 = new Vec2(cushionDepth / 2, -cushionLength / 2 - cushionDepth / 2);
        Vec2 p2 = new Vec2(-cushionDepth / 2, -cushionLength / 2);
        Vec2 p3 = new Vec2(-cushionDepth / 2, +cushionLength / 2);
        Vec2 p4 = new Vec2(cushionDepth / 2, cushionLength / 2 + cushionDepth / 2);
        p1 = rotateVec(p1, orientation);
        p2 = rotateVec(p2, orientation);
        p3 = rotateVec(p3, orientation);
        p4 = rotateVec(p4, orientation);


        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p1.x), (float) (centrey + p1.y) - 0.2f, (float) (centrex + p2.x), (float) (centrey + p2.y), col));
        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p2.x), (float) (centrey + p2.y), (float) (centrex + p3.x), (float) (centrey + p3.y), col));
        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p3.x), (float) (centrey + p3.y), (float) (centrex + p4.x), (float) (centrey + p4.y) - 0.2f, col));

    }

    private void createBottomCushion(List<AnchoredBarrier> barriers, float centrex, float centrey, double orientation, float cushionLength, float cushionDepth) {
        // on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
        Color col = Color.RED;
        Vec2 p1 = new Vec2(cushionDepth / 2, -cushionLength / 2 - cushionDepth / 2);
        Vec2 p2 = new Vec2(-cushionDepth / 2, -cushionLength / 2);
        Vec2 p3 = new Vec2(-cushionDepth / 2, +cushionLength / 2);
        Vec2 p4 = new Vec2(cushionDepth / 2, cushionLength / 2 + cushionDepth / 2);
        p1 = rotateVec(p1, orientation);
        p2 = rotateVec(p2, orientation);
        p3 = rotateVec(p3, orientation);
        p4 = rotateVec(p4, orientation);


        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p1.x), (float) (centrey + p1.y) + 0.2f, (float) (centrex + p2.x), (float) (centrey + p2.y), col));
        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p2.x), (float) (centrey + p2.y), (float) (centrex + p3.x), (float) (centrey + p3.y), col));
        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p3.x), (float) (centrey + p3.y), (float) (centrex + p4.x), (float) (centrey + p4.y) + 0.2f, col));

    }

    private void createLeftCushion(List<AnchoredBarrier> barriers, float centrex, float centrey, double orientation, float cushionLength, float cushionDepth) {
        // on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
        Color col = Color.RED;
        Vec2 p1 = new Vec2(cushionDepth / 2, -cushionLength / 2 - cushionDepth / 2);
        Vec2 p2 = new Vec2(-cushionDepth / 2, -cushionLength / 2);
        Vec2 p3 = new Vec2(-cushionDepth / 2, +cushionLength / 2);
        Vec2 p4 = new Vec2(cushionDepth / 2, cushionLength / 2 + cushionDepth / 2);
        p1 = rotateVec(p1, orientation);
        p2 = rotateVec(p2, orientation);
        p3 = rotateVec(p3, orientation);
        p4 = rotateVec(p4, orientation);


        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p1.x) + 0.2f, (float) (centrey + p1.y), (float) (centrex + p2.x), (float) (centrey + p2.y), col));
        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p2.x), (float) (centrey + p2.y), (float) (centrex + p3.x), (float) (centrey + p3.y), col));
        barriers.add(new AnchoredBarrier_StraightLine((float) (centrex + p3.x), (float) (centrey + p3.y), (float) (centrex + p4.x) + 0.2f, (float) (centrey + p4.y), col));

    }

    public void pocketBall() {
		/*Vec2 topRight = new Vec2(4.98f,6.5f);
		Vec2 middleRight = new Vec2(5.05f,3.52f);
		Vec2 botRight = new Vec2(4.97f,0.64f);
		Vec2 topLeft = new Vec2(1.57f,6.52f);
		Vec2 middleLeft = new Vec2(1.44f,3.63f);
		Vec2 bottomLeft = new Vec2(1.53f,0.67f);*/


        float[] rightpocketXCoordinates = new float[]{5.16f, 5.25f, 5.15f};
        float[] leftpocketXCoordinates = new float[]{1.45f, 1.27f, 1.41f};


        boolean cueBallPotted = false;

        for (int i = 0; i < particles.size() - 1; i++) {
            for (int j = 0; j < rightpocketXCoordinates.length; j++) {
                if (particles.get(i).body.isActive()) {
                    if (particles.get(10).body.getPosition().x + Radius > rightpocketXCoordinates[j] || particles.get(10).body.getPosition().x - Radius < leftpocketXCoordinates[j]) {
                        isInvalidPot = true;
                        particles.get(10).body.setActive(false);
                    }

                    if (particles.get(i).body.getPosition().x + Radius > rightpocketXCoordinates[j] || particles.get(i).body.getPosition().x - Radius < leftpocketXCoordinates[j]) {
                        increase = true;

                        particles.get(i).body.setActive(false);
                    }


                    if (particles.get(15).body.getPosition().x + Radius > rightpocketXCoordinates[j] || particles.get(15).body.getPosition().x - Radius < leftpocketXCoordinates[j]) {
                        cueBallPotted = true;
                        resetCueBall();
                    }
                }
            }
        }

        if (increase) {
            count++;
            ballcount++;
            increase = false;
            System.out.println(count);
        }

        if (cueBallPotted) {
            if (count == 0) {
                count = 0;
            }

            if (count > 0) {
                count--;
            }

        }

    }

    public void resetTable() {
        if (ballcount >= 14 && hasAllTheBallsStopped()) {
            InitBall(2.81f, 5.9f);
            resetCueBall();
            ballcount = 0;
        }
    }

    public void checkIf8BallPottedBeforeOtherBalls() {
        if (ballcount <= 14 && isInvalidPot) {
            System.out.println("You potted the 8 ball");
            System.exit(1);
        }
    }

    public void score(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 32));
        g.drawString(String.valueOf("Score: "), SCREEN_WIDTH - 180, 40);
        g.drawString(String.valueOf(count), SCREEN_WIDTH - 60, 40);
    }

    public void resetCueBall() {
        particles.get(15).body.setTransform(new Vec2(3.3f, 1.845f), 0);
        particles.get(15).body.setLinearVelocity(new Vec2(0, 0));

        hasAllTheBallsStopped();
        addPower();
    }

    private static Vec2 rotateVec(Vec2 v, double angle) {
        // I couldn't find a rotate function in Vec2 so had to write own temporary one here, just for the sake of
        // cushion rotation for snooker table...
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float nx = v.x * cos - v.y * sin;
        float ny = v.x * sin + v.y * cos;
        return new Vec2(nx, ny);
    }

    public static void main(String[] args) throws Exception {
        final Main game = new Main();
        final BasicView view = new BasicView(game, game.mouselistener);
        JEasyFrame frame = new JEasyFrame(view, "8 Ball Pool");
        view.addMouseMotionListener(game.mouselistener);
        view.addMouseListener(game.mouselistener);
        game.startThread(view);
    }

    private void startThread(final BasicView view) throws InterruptedException {
        final Main game = this;
        while (true) {
            game.update();
            view.repaint();
            Toolkit.getDefaultToolkit().sync();
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
            }
        }
    }

    public void addPower() {
        if (hasAllTheBallsStopped()) {
            if (canShoot && !mouselistener.isMouseButtonPressed()) {
                Vec2 vel = particles.get(15).body.getPosition().sub(mouselistener.getWorldCoordinatesOfMousePointer());
                particles.get(15).body.setLinearVelocity(vel.mul(5f));
                canShoot = false;
            }
        }
    }

	/*public void collisionEffect()
	{

		ContactListener contactListener = new ContactListener() {
			@Override
			public void beginContact(Contact contact) {

				Fixture f1 = contact.getFixtureA();
				Fixture f2 = contact.getFixtureB();

				Body b1 = f1.getBody();
				Body b2 = f2.getBody();

				Object o1 = particles.get(15).body.getUserData();
				Object o2 = b2.getUserData();

				//for(int i=0; i< particles.size(); i++)
				{
					//Object o2 = particles.get(i).body.getUserData();

					//System.out.println(o1+" "+o2);

					if(o1.getClass() == BasicParticle.class && o2.getClass() == BasicParticle.class)
					{
						collisionActive = true;
					}
				}
			}

			@Override
			public void endContact(Contact contact) {

				Fixture f1 = contact.getFixtureA();
				Fixture f2 = contact.getFixtureB();

				Body b1 = f1.getBody();
				Body b2 = f2.getBody();

				Object o1 = particles.get(15).body.getUserData();
				Object o2 = b2.getUserData();




					//System.out.println(o1+" "+o2);

					if(o1.getClass() == BasicParticle.class && o2.getClass() == BasicParticle.class)
					{
						collisionActive = false;
					}

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		};

		particles.get(15).body.m_world.setContactListener(contactListener);

	}*/


    public void update() {
        int VELOCITY_ITERATIONS = NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
        int POSITION_ITERATIONS = NUM_EULER_UPDATES_PER_SCREEN_REFRESH;

        checkIf8BallPottedBeforeOtherBalls();
        resetTable();
        addPower();
        pocketBall();

        //collisionEffect();

        for (BasicParticle p : particles) {
            // give the objects an opportunity to add any bespoke forces, e.g. rolling friction
            p.notificationOfNewTimestep();
        }
        for (BasicPolygon p : polygons) {
            // give the objects an opportunity to add any bespoke forces, e.g. rolling friction
            p.notificationOfNewTimestep();
        }

        world.step(DELTA_T, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }


}


