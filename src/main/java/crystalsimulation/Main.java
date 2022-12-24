package crystalsimulation;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

public class Main extends PApplet {

	public static final int WIDTH = 1200, HEIGHT = 800;
	public static final float FRAMERATE = 60;
	
	public static final float boxDims = 800;
	public static final float regionDims = 50;
	
	public static int t = 0;
	
	public static Handler handler = new Handler();
	
	public static boolean running = true;
	
	public static PVector camVec;
	public static boolean[] camMoving = new boolean[6];
	public static float az=0, ze=0, mag=(float) (boxDims * 1.4);
	
	public static long startTime = System.currentTimeMillis();
	
	public static void main(String[] args) {
		PApplet.main("crystalsimulation.Main");
	}
	
	public void settings() {
		System.out.println("---SETTINGS---");
		size(WIDTH, HEIGHT, PApplet.P3D);
	}
	
	public void setup() {
		System.out.println("---SETUP---");
		//frameRate(FRAMERATE);
		sphereDetail(5, 5);
		updateCam();
		System.out.println("---DRAW---");
	}
	
	public void draw() {
		if (running) {
			handler.advance(30);
			if (Particle.heat > 1) Particle.heat -= 0.008;
		}
		
		camera(camVec.x, camVec.y, camVec.z, 0, 0, 0, 0, 0, -1);
		background(200, 180, 190);
		drawWall();
		
//		if (System.currentTimeMillis() - startTime > 1000 * 20) {
//			for (Particle p : Main.handler.particles) p.falling = true;
//			startTime = Long.MAX_VALUE;
//		}
		
		
		
	
		System.out.println(this.frameRate);
		
		
		handler.draw(this);
		
	}
	
	public void keyPressed(KeyEvent e) {
		switch(key) {
			case 'a': camMoving[0] = true; break;
			case 'd': camMoving[1] = true; break;
			case 's': camMoving[2] = true; break;
			case 'w': camMoving[3] = true; break;
			case 'z': camMoving[4] = true; break;
			case 'x': camMoving[5] = true; break;
			case ' ': running = !running;
		}
		
		
	}
	
	public void keyReleased(KeyEvent e) {
		switch(key) {
			case 'a': camMoving[0] = false; break;
			case 'd': camMoving[1] = false; break;
			case 's': camMoving[2] = false; break;
			case 'w': camMoving[3] = false; break;
			case 'z': camMoving[4] = false; break;
			case 'x': camMoving[5] = false; break;
		}
	}
	
	private void moveCam() {
		if (camMoving[0]) az += 0.03;
		if (camMoving[1]) az -= 0.03;
		if (camMoving[2]) ze -= 0.03;
		if (camMoving[3]) ze += 0.03;
		if (camMoving[4]) mag -= 10;
		if (camMoving[5]) mag += 10;
		ze = (float) Math.min(ze, Math.PI/2-0.01);
		ze = (float) Math.max(ze, -Math.PI/2+0.01);
		mag = (float) Math.max(mag, 10);
		
		updateCam();
	}
	
	private void updateCam() {
		float x = (float) (Math.cos(az)*Math.cos(ze));
		float y = (float) (Math.sin(az)*Math.cos(ze));
		float z = (float) (Math.sin(ze));
		camVec = new PVector(x,y,z).mult(mag);
		
	}
	
	
	private void drawWall() {
		moveCam();
		strokeWeight(10);
		stroke(0);
		//point(0,0);
		noFill();
		box(boxDims);
	}
	
}