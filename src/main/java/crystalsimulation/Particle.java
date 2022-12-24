package crystalsimulation;

import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PVector;


public class Particle {
	
	public enum Molecule {
		CL,
		NA,
		GAS
	}
	
	
	public static float initVelScale = 10;
	public static float stableDist = 30;
	public static int s = Main.handler.s;
	public static float friction = (float) 0.9995;
	public static float heat = (float) 6;
	public static PVector gravity = new PVector(0, 0, (float) -0.2);
	public static float stirSpeed = (float) 0.00003;
	public static boolean falling = false, stirring = true;
	
	public PVector pos, vel, acc;
	
	public float radius = 8;
	
	public float humpness = 6;
	public Molecule molecule;
	
	public int[] tag;
	public boolean movable = true;
	
	
	
	
	public Particle(Molecule molecule) {
		this.molecule = molecule;
		this.pos = new PVector((float) (Math.random()-0.5)*Main.boxDims, (float) (Math.random()-0.5)*Main.boxDims, (float) (Math.random()-0.5)*Main.boxDims);
		this.vel = new PVector((float) (Math.random()-0.5)*initVelScale, (float) (Math.random()-0.5)*initVelScale, (float) (Math.random()-0.5)*initVelScale);
		this.acc = new PVector(0, 0, 0);
		this.tag = newTag();
	}
	
	
	public Particle(PVector pos, Molecule molecule) {
		this.molecule = molecule;
		this.pos = pos;
		this.vel = new PVector(0, 0, 0);
		this.acc = new PVector(0, 0, 0);
		this.tag = newTag();	
	}
	
	public Particle(PVector pos, Molecule molecule, float radius, boolean movable) {
		this.movable = movable;
		this.molecule = molecule;
		this.pos = pos;
		this.vel = new PVector(0, 0, 0);
		this.acc = new PVector(0, 0, 0);
		this.tag = newTag();	
	}
	
	public void draw(PApplet p) {
		p.pushMatrix();
		p.translate(pos.x, pos.y, pos.z);	
		p.noStroke();
		if (molecule == Molecule.NA) p.fill(30, 250, 33);
		if (molecule == Molecule.CL) p.fill(240, 23, 20);
		if (molecule == Molecule.GAS) p.fill(23, 20, 200);
		p.sphere(radius);
		p.popMatrix();
	}
	
	
	public float calcForces(Particle particle) {
		
		float coef = 1;
		if (particle.molecule == this.molecule) coef = -1;
		
		
		float dist = particle.pos.dist(this.pos);
		float res = (float) (Math.pow(particle.stableDist/dist, 2*humpness) - coef * Math.pow(particle.stableDist/dist, humpness));
		//System.out.println(res);
		return Math.min((float)10, res*4);
	}
	
	public void advanceForces() {
		acc = new PVector(0, 0, 0);
//		System.out.println("ASd " + Arrays.toString(tag));
//		for (int a : Main.handler.regions.keySet()) System.out.println(a);
		
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
//						System.out.println(hash(new int[] {tag[0]+x, tag[1]+y, tag[2]+z}));
						try {
						     advanceRegionalForces(new int[] {tag[0]+x, tag[1]+y, tag[2]+z});
						} catch (NullPointerException e) {}
				} 
			}
		} 
	}
	
	public void advanceRegionalForces(int[] regionalTag) {
		for (Particle particle : (Main.handler.regions.get(hash(regionalTag)))) {
//			System.out.println(Main.handler.regions.get(0).size());
			if (this == particle) continue;
			PVector unit = pos.copy().sub(particle.pos).normalize();
			acc = acc.add(unit.mult(calcForces(particle)));
		}
		//System.out.println(acc);
	}
	
	public int[] newTag() {
		return new int[] {(int) Math.floor(pos.x/Main.regionDims),
			              (int) Math.floor(pos.y/Main.regionDims),
				          (int) Math.floor(pos.z/Main.regionDims),};
	}
	
	
	public static int hash(int[] tag) {
		return tag[0]*(s+2)*(s+2) + tag[1]*(s+2) + tag[2];
	}
	
	public void setRegion() {
		tag = newTag();
		Main.handler.regions.get(hash(tag)).add(this);	
	}
	
	public void advance(int steps) {
		if (movable) {
			if (falling) acc.add(gravity);
			if (stirring) {
				acc.add(new PVector(pos.y, -pos.x, 0).mult(stirSpeed));
				acc.add(new PVector(-pos.x, -pos.y, 0).mult((float) (stirSpeed*0.5)));
			}
			acc.add(PVector.random3D().mult(heat));
			vel.mult(friction);
			vel = vel.add(acc.copy().div(steps));
		}
		pos = pos.add(vel.copy().div(steps));
		
		if (Math.abs(pos.x)+radius > Main.boxDims/2) {
			vel = new PVector(-vel.x, vel.y, vel.z);
			pos = new PVector(((Main.boxDims)/2 - radius) * Math.signum(pos.x), pos.y, pos.z);
		}
		if (Math.abs(pos.y)+radius > Main.boxDims/2) {
			vel = new PVector(vel.x, -vel.y, vel.z);
			pos = new PVector(pos.x, ((Main.boxDims)/2 - radius) * Math.signum(pos.y), pos.z);
		}
		if (Math.abs(pos.z)+radius > Main.boxDims/2) {
			vel = new PVector(vel.x, vel.y, -vel.z);
			pos = new PVector(pos.x, pos.y, ((Main.boxDims)/2 - radius) * Math.signum(pos.z));
		}
		
	}

}
