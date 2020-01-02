/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author karma
 */
public class Vector {
	
	public final int X = 0, Y = 1, Z = 2, W = 3;
	
	public float v[];
	
	public Vector() {
		
		this.v = new float[4];
		this.v[X] = 0;
		this.v[Y] = 0;
		this.v[Z] = 0;
		this.v[W] = 0;
	}
	
	public void add(float n) {
		
		this.v[X] += n;
		this.v[Y] += n;
		this.v[Z] += n;
	}
	
	public void add(Vector in) {
		
		this.v[X] += in.v[X];
		this.v[Y] += in.v[Y];
		this.v[Z] += in.v[Z];
	}
	
	public void sub(float n) {
		
		this.v[X] -= n;
		this.v[Y] -= n;
		this.v[Z] -= n;
	}
	
	public void sub(Vector in) {
		
		this.v[X] -= in.v[X];
		this.v[Y] -= in.v[Y];
		this.v[Z] -= in.v[Z];
	}
	
}
