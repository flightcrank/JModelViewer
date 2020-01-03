
import java.util.*;
import java.io.*;

//must export with one normal per vertex (in blender turn on smooth shading, then export)
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
				
				String[] vertIndex1 = i.split("/");
				String[] vertIndex2 = j.split("/");
				String[] vertIndex3 = k.split("/");
				
				//these values represent the Index into the vert arraylist
				Integer vIndex0 = Integer.valueOf(vertIndex1[0]) - 1;	//(minus 1 to line up with the zero indexed array)
				Integer vIndex1 = Integer.valueOf(vertIndex2[0]) - 1;
				Integer vIndex2 = Integer.valueOf(vertIndex3[0]) - 1;
				
				// Add vertex array index to faces arraylist
				currentModel.faces.add(vIndex0); 
				currentModel.faces.add(vIndex1);
				currentModel.faces.add(vIndex2);
				
				//these values represent the index in the normals arraylist
				Integer nIndex0 = Integer.valueOf(vertIndex1[2]) - 1;	//(minus 1 to line up with the zero indexed array)
				Integer nIndex1 = Integer.valueOf(vertIndex2[2]) - 1;
				Integer nIndex2 = Integer.valueOf(vertIndex3[2]) - 1;
				
				//add vert index and normal index to a HashMap
				currentModel.normalIndex.put(vIndex0, nIndex0);
				currentModel.normalIndex.put(vIndex1, nIndex1);
				currentModel.normalIndex.put(vIndex2, nIndex2);
				
			//go to the next line
			} else {
			
				s.nextLine();
			}
		}			
	}
}

class Model {

	public String name;				//object's name
	public ArrayList<Float> verts;			//list of vertices
	public ArrayList<Float> normals;		//list of vertex normals
	public ArrayList<Integer> faces;		//list of indices into the vertex array
	public HashMap<Integer, Integer> normalIndex;	//list of indices into the normals array
	
	public Model() {
		
		name = "Unknown Object";	
		verts = new ArrayList<>();
		faces = new ArrayList<>();
		normals = new ArrayList<>();
		normalIndex = new HashMap<>();
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
		
		int numVerts = verts.size() / 3;	
		ArrayList<Float> n = new ArrayList<>();
		
		//new array list containing just the individual x,y and z normal vectors for each vertices in order
		for (int i = 0; i < numVerts; i++) {

			Integer nIndex = normalIndex.get(i);
			
			//normal vector direction for each axis
			float nVecX = normals.get(nIndex);
			float nVecY = normals.get(nIndex + 1);
			float nVecZ = normals.get(nIndex + 2);
			
			n.add(nVecX);
			n.add(nVecY);
			n.add(nVecZ);
			//System.out.println(nVecX + " " + nVecY + " " + nVecZ);
		}
		
		int s = n.size();
		float[] a = new float[s];

		//convert arraylist to primitve array
		for (int i = 0; i < s; i++) {
			
			a[i] = n.get(i);
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

