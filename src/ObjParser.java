
import java.util.*;
import java.io.*;

class ObjParser {

	public ArrayList<Model> model;

	public ObjParser() {
	
		model = new ArrayList<>();
	}

	public void parseFile(String fileName) throws Exception {

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		Scanner s = new Scanner(in);			
		Model currentModel = null;
		
		//there are still tokens left to process
		while (s.hasNext()) {

			//if the next token in the file is the char 'v'	(vertex)
			if (s.hasNext("v") && currentModel != null) {
				
				s.next();					//discard the 'v' token
				currentModel.verts.add((Float) s.nextFloat()); 	//x value
				currentModel.verts.add((Float) s.nextFloat());	//y value
				currentModel.verts.add((Float) s.nextFloat());	//z value	
			
			//if the next token in the file is the char 'vn' (vertex normals)	
			} else if (s.hasNext("vn") && currentModel != null) {
				
				s.next();						//discard the 'vn' token
				currentModel.normals.add((Float) s.nextFloat());	//x value
				currentModel.normals.add((Float) s.nextFloat());	//y value
				currentModel.normals.add((Float) s.nextFloat());	//z value
				
			//if the next token in the file is the char 'o' (object name)	
			} else if (s.hasNext("o")) {
				
				s.next();			//discard the 'o' token
				currentModel = new Model();	//clear the current model incase theres another one in the obj file
				currentModel.name = s.next();
				model.add(currentModel);	//add current model to the list
			
			//OBJ file must store face information in n/n/n format, where n is a number.
			} else if (s.hasNext("f") && currentModel != null) {
				
				s.next();			//discard the 'f' token
				String i = s.next();		//index 1
				String j = s.next();		//index 2
				String k = s.next();		//index 3
				
				String[] iFace = i.split("/");
				String[] jFace = j.split("/");
				String[] kFace = k.split("/");
				Integer tri0 = Integer.valueOf(iFace[0]);
				Integer tri1 = Integer.valueOf(jFace[0]);
				Integer tri2 = Integer.valueOf(kFace[0]);
				
				// minus 1 to line up with the zero indexed array
				currentModel.faces.add(tri0 - 1); 
				currentModel.faces.add(tri1 - 1);
				currentModel.faces.add(tri2 - 1);
				
			//go to the next line
			} else {
			
				s.nextLine();
			}
		}			
	}
}

class Model {

	public String name;			//object's name
	public ArrayList<Float> verts;		//list of vertices
	public ArrayList<Integer> faces;	//list of faces
	public ArrayList<Float> normals;	//list of vertex normals
	
	public Model() {
		
		name = "Unknown Object";	
		verts = new ArrayList<>();
		faces = new ArrayList<>();
		normals = new ArrayList<>();
	}

	public float[] vertsToArray() {
		
		int s = verts.size();
		float[] a = new float[s];

		for (int i = 0; i < s; i++) {
			
			a[i] = verts.get(i);
		}
		
		return a;
	}
	
	public float[] normalsToArray() {
		
		int s = normals.size();
		float[] a = new float[s];

		for (int i = 0; i < s; i++) {
			
			a[i] = normals.get(i);
		}
		
		return a;
	}
	
	public int[] facesToArray() {
		
		int s = faces.size();
		int[] a = new int[s];

		for (int i = 0; i < s; i++) {
			
			a[i] = faces.get(i);
		}
		
		return a;
	}
}

