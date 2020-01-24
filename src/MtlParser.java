
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author karma
 */

class Materal {
	
	String name;
	float[] ambiant;
	float[] diffuse;
	float[] specular;
	float transparency;
	float specularExponent;
	String diffuseTexture;
	
	public Materal() {
		
		ambiant = new float[3];
		diffuse = new float[3];
		specular = new float[3];
	}
}

class MtlParser {
	
	ArrayList<Materal> materials;
	
	public MtlParser() {
		
		materials = new ArrayList<>();
	}
	
	public void parseFile(String fileName) throws Exception {

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		Scanner s = new Scanner(in);			
		Materal currentMaterial = null;
		
		//there are still tokens left to process
		while (s.hasNext()) {
			
			if (s.hasNext("newmtl")) { 
				
				currentMaterial = new Materal();
				s.next();						//discard the 'newmtl' token
				currentMaterial.name = s.next();
				materials.add(currentMaterial);	//add material to the list of materials
				
			} else if (s.hasNext("Ns") && currentMaterial != null) { 
				
				s.next();						//discard the 'Ns' token
				currentMaterial.specularExponent = s.nextFloat();
				
			} else if (s.hasNext("Ka") && currentMaterial != null) { 
				
				s.next();						//discard the 'Ka' token
				currentMaterial.ambiant[0] = s.nextFloat();
				currentMaterial.ambiant[1] = s.nextFloat();
				currentMaterial.ambiant[2] = s.nextFloat();
			
			} else if (s.hasNext("Kd") && currentMaterial != null) { 
				
				s.next();						//discard the 'Kd' token
				currentMaterial.diffuse[0] = s.nextFloat();
				currentMaterial.diffuse[1] = s.nextFloat();
				currentMaterial.diffuse[2] = s.nextFloat();
			
			} else if (s.hasNext("Ks") && currentMaterial != null) { 
				
				s.next();						//discard the 'Ks' token
				currentMaterial.specular[0] = s.nextFloat();
				currentMaterial.specular[1] = s.nextFloat();
				currentMaterial.specular[2] = s.nextFloat();
			
			} else if (s.hasNext("d") && currentMaterial != null) {
				
				s.next();						//discard the 'd' token
				currentMaterial.transparency = s.nextFloat();
			
			} else if (s.hasNext("map_Kd") && currentMaterial != null) {
				
				s.next();						//discard the 'map_Kd' token
				currentMaterial.diffuseTexture = s.next();
				
			} else {
			
				s.nextLine();
			}
		}
	}
}
