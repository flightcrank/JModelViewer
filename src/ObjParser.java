
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
				currentModel.normals.add((Float) s.nextFloat() );	//x value
				currentModel.normals.add((Float) s.nextFloat() );	//y value
				currentModel.normals.add((Float) s.nextFloat() );	//z value
				
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
				
				String[] faceIndex1 = i.split("/");
				String[] faceIndex2 = j.split("/");
				String[] faceIndex3 = k.split("/");
				
				//these values represent the Index into the vert arraylist
				Integer vIndex0 = Integer.valueOf(faceIndex1[0]) - 1;	//(minus 1 to line up with the zero indexed array)
				Integer vIndex1 = Integer.valueOf(faceIndex2[0]) - 1;
				Integer vIndex2 = Integer.valueOf(faceIndex3[0]) - 1;
				
				//these values represent the Index into the normals arraylist
				Integer nIndex0 = Integer.valueOf(faceIndex1[2]) - 1;	//(minus 1 to line up with the zero indexed array)
				Integer nIndex1 = Integer.valueOf(faceIndex2[2]) - 1;
				Integer nIndex2 = Integer.valueOf(faceIndex3[2]) - 1;
				
				// Add vertex array index to faces arraylist
				currentModel.faces.add(vIndex0); 
				currentModel.faces.add(vIndex1);
				currentModel.faces.add(vIndex2);
				
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
		
		ArrayList<Float> temp = new ArrayList<>();
		float[] a;
		int s = normalIndex.size();


		for (int i = 0; i < s; i++) {
			
			int index = normalIndex.get(i) * 3; //3 is the span, as the normals array is 1D
			
			//add the 3 values that make up the normal
			temp.add(normals.get(index));
			temp.add(normals.get(index + 1));
			temp.add(normals.get(index + 2));
			
			//System.out.println(normals.get(index) + " " + normals.get(index + 1) + " " + normals.get(index + 2));
		}
		
		a = new float[temp.size()];
		
		for (int i = 0; i < a.length; i++) {
			
			a[i] = temp.get(i);			
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

