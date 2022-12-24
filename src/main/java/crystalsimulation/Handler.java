package crystalsimulation;

import java.util.ArrayList;
import java.util.HashMap;

import crystalsimulation.Particle.Molecule;
import processing.core.PApplet;
import processing.core.PVector;

public class Handler {
	
	ArrayList<Particle> particles = new ArrayList<Particle>();
	HashMap<Integer, ArrayList<Particle>> regions = new HashMap<Integer, ArrayList<Particle>>();
	
	public static int n = 200;
	
	public static int s = (int) ((Main.boxDims)/Main.regionDims);
	
	public Handler() {
		
		for (int i = -s/2; i < s/2; i++) {
			for (int j = -s/2; j < s/2; j++) {
				for (int k = -s/2; k < s/2; k++) {
					regions.put(Particle.hash(new int[] {i,j,k}), new ArrayList<Particle>());
					System.out.println(Particle.hash(new int[] {i,j,k}));
				}
			}
		}
		
		for (int i = 0; i < n; i++) {
			particles.add(new Particle(Molecule.NA));
		}
		for (int i = 0; i < n; i++) {
			particles.add(new Particle(Molecule.CL));
		}
//		for (int i = 0; i < n; i++) {
//			particles.add(new Particle(Molecule.GAS));
//		}
		
//		for (int i = 0; i < 20; i++) {
//			particles.add(new Particle(Molecule.GAS));
//		}
		
//		for (int i = 0; i < 3; i++) {
//			for (int j = 0; j < 3; j++) {
//				for (int k = 0; k < 3; k++) {
//					boolean cation = (i+j+k) % 2 == 1;
//					particles.add(new Particle(new PVector(i, j, k).mult(Particle.stableDist), (cation ? Molecule.NA : Molecule.CL), (float) 8, false));
//				}
//			}
//		}
		

	
		
		
		
	}
	
	public void draw(PApplet p) {
		p.strokeWeight(1);
		
//		for (int i = -s/2; i < s/2; i++) {
//			for (int j = -s/2; j < s/2; j++) {
//				for (int k = -s/2; k < s/2; k++) {
//					if (regions.get(Particle.hash(new int[] {i,j,k})).size() > 0) {
//						for (int x = -1; x <= 1; x++) {
//							for (int y = -1; y <= 1; y++) {
//								for (int z = -1; z <= 1; z++) {
//									try {
//										p.pushMatrix();
//										p.translate((float)(i+0.5+x)*Main.regionDims, (float)(j+0.5+y)*Main.regionDims, (float)(k+0.5+z)*Main.regionDims);
//										p.box(Main.regionDims);
//										p.popMatrix();
//									
//									} catch (Exception e) {
//										
//									}
//								} 
//							}
//						}
//					}
//				}
//			}
//		}
		
		for (Particle particle : particles) {
			particle.draw(p);
		}
		
		p.stroke(255);
		p.strokeWeight(3);
		for (int i = 0; i < particles.size(); i++) {
			for (int j = i+1; j < particles.size(); j++) {
				Particle p1 = particles.get(i);
				Particle p2 = particles.get(j);
				if (p1.pos.dist(p2.pos) < Particle.stableDist + 10 && p1.molecule != p2.molecule) {
					p.line(p1.pos.x, p1.pos.y, p1.pos.z, p2.pos.x, p2.pos.y, p2.pos.z);
				}
			}
		}
	}
	
	public void advance(int steps) {
		for (ArrayList<Particle> list : regions.values()) list.clear();
		for (Particle particle : particles) particle.setRegion();
		for (Particle particle : particles) {
			for (int i = 0; i < steps; i++) {
				particle.advanceForces();
				particle.advance(steps);
			}
		}
	}
	
}
