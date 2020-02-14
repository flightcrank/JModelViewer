
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author karma
 */

enum Token {
	
	ELEMENT,
	PROPERTY, 
	COMMENT, 
	FLOAT,
	FORMAT,
	END_HEADER,
	UCHAR,
	UINT
}

class PlyToken {

	Token token;
	Object data;
	
	public static class Format {
		
		String type;
		String version;
	}
	
	public static class Element {
		
		String name;
		int number;
	}
	
	public static class Property {
		
		String name;
		boolean list;
		Token value1;
		Token value2;
	}
}

public class PlyParser {
	
	ArrayList<PlyToken> header;
	ArrayList<String> data;
	
	public PlyParser() {
		
		header = new ArrayList<>();
		data = new ArrayList<>();
	}
	
	/**
	 *
	 * @param name of element eg. "vertex" or "vertex_indices"
	 * @return Will return the number of elements held in the plyData member variable in sequential order
	 */
	public int numElements(String name) {
		
		for (PlyToken val: header) {
			
			if (val.token == Token.ELEMENT) {
				
				PlyToken.Element ele = (PlyToken.Element) val.data;
				
				if (ele.name.equals(name)) {
					
					return ele.number;
				}
			}			
		}
		
		return 0;
	}
	
	/**
	 * testing
	 * 
	 * @param the property name that you want to know the type of
	 * @return a Token enum value of the type the property refers to eg a FLOAT if the property consists of "property float x"
	 */
	public Token getPropertyType(String propertyName) {
		
		for (PlyToken val: header) {
			
			if (val.token == Token.PROPERTY) {
				
				PlyToken.Property prop = (PlyToken.Property) val.data;
					
				//float type found
				if (prop.name.equals(propertyName) && prop.value1 == Token.FLOAT) {
					
					System.err.println("property '" + propertyName+ "' " + " is of type FLOAT");
					return Token.FLOAT;
				}
			}
		}
	
		return null;
	}
	
	/**
	 *
	 * @param  properyName: the propery name of the property you are searching for
	 * @param elementName: the element name that the property is found under
	 * @return the int position of where that property occurs in a data string fond in the data member variable array list.
	 * or -1 if no property found
	 */
	public int propertyPosition(String properyName, String elementName) {
		
		int index = -1;
		boolean vertex = false;
		
		//search through the header
		for (int i = 0; i < header.size(); i++) {
			
			PlyToken val = header.get(i);
			
			//look for the element token that has the name "vertex"and set a boolean flag to true 
			//so we know every property after that, is part of that element
			if (val.token == Token.ELEMENT) {
				
				PlyToken.Element ele = (PlyToken.Element) val.data;
				vertex = ele.name.equals(elementName);
				continue;
			}
			
			//count the position of the property under the vertex elemnt
			if (val.token == Token.PROPERTY && vertex == true) {
				
				index += 1;
				PlyToken.Property prop = (PlyToken.Property) val.data;
				
				//Property object with propertName found
				if (prop.name.equals(properyName)) {
					
					return index;
				}		
			}	
		}
		
		return -1; //no property with propertyName found
	}
	
	public int[] getFacesArray() {
		
		int size = this.numElements("face");
		int skip = this.numElements("vertex");
		
		int[] a = new int[size * 3];
		int j = 0;
		
		for (int i = skip; i < data.size(); i++) {
			
			String line = data.get(i);
			String[] vertIndex = line.split(" ");
						
			//vertIndex 0 will be the number or verts that make up a face (3 for a triangulated mesh)
			a[j] = Integer.parseInt(vertIndex[1]);
			a[j + 1] = Integer.parseInt(vertIndex[2]);
			a[j + 2] = Integer.parseInt(vertIndex[3]);
			j += 3;
		}
		
		return a;
	}
	
	public float[] getNormArray() {
		
		int nxIndex = this.propertyPosition("nx", "vertex");
		int nyIndex = this.propertyPosition("ny", "vertex");
		int nzIndex = this.propertyPosition("nz", "vertex");
		
		int size = this.numElements("vertex");
		
		float a[] = new float[size * 3];
		int j = 0;
		
		for (int i = 0; i < size; i++) {
			
			String line = data.get(i);
			String[] vertPos = line.split(" ");
			a[j] = Float.parseFloat(vertPos[nxIndex]);
			a[j + 1] = Float.parseFloat(vertPos[nyIndex]);
			a[j + 2] = Float.parseFloat(vertPos[nzIndex]);
			j += 3;
		}

		return a;
	}
	
	public float[] getVertArray() {
		
		int xIndex = this.propertyPosition("x", "vertex");
		int yIndex = this.propertyPosition("y", "vertex");
		int zIndex = this.propertyPosition("z", "vertex");
		
		int size = this.numElements("vertex");
		
		float a[] = new float[size * 3];
		int j = 0;
		
		for (int i = 0; i < size; i++) {
			
			String line = data.get(i);
			String[] vertPos = line.split(" ");
			a[j] = Float.parseFloat(vertPos[xIndex]);
			a[j + 1] = Float.parseFloat(vertPos[yIndex]);
			a[j + 2] = Float.parseFloat(vertPos[zIndex]);
			j += 3;
		}

		return a;
	}
	
	public float[] getUVArray() {
		
		int u = propertyPosition("s", "vertex");
		int v = propertyPosition("t", "vertex");
		
		int num = this.numElements("vertex");
		float[] a = null;
		
		//if UV data is found
		if (u > 0 && v > 0) {
			
			a = new float[num * 2]; //2 UV values per vertex
			int j = 0;
			
			for (int i = 0; i < num; i++) {

				String s = data.get(i);
	
				a[j] = Float.valueOf(s.split(" ")[u]);
				a[j + 1] = Float.valueOf(s.split(" ")[v]);
				j += 2;
			}
		}
		
		return a;
	}
	
	private PlyToken processProperty(String line) throws Exception {
		
		PlyToken.Property prop = new PlyToken.Property();
		String[] propertyValues = line.trim().split(" ");
		int lastIndex = propertyValues.length -1;

		prop.name = propertyValues[lastIndex];
		
		switch (propertyValues.length) {
			
			case 2:
				prop.list = false;
				
				if (propertyValues[0].equals("float")) {
					
					prop.value1 = Token.FLOAT;
				
				} else {
					
					throw new Exception("Only PLY property's of type float are supported");
				}

				break;
			
			case 4:
				prop.list = propertyValues[0].equals("list");
				
				if (propertyValues[1].equals("uchar")) {
					
					prop.value1 = Token.UCHAR;
				
				} else {
					
					throw new Exception("Only PLY property's of type UCHAR are supported as the second token in a property with a total of 4 values");
				}
				
				if (propertyValues[2].equals("uint")) {
					
					prop.value2 = Token.UINT;
					
				} else {
					
					throw new Exception("Only PLY property's of type UINT are supported as the third token in a property with a total of 4 values");
				}
				
				break;
				
			default:
				
				throw new Exception("Only PLY property values of 2 or 4 values are supported");
		}
		
		PlyToken pToken = new PlyToken();
		pToken.token = Token.PROPERTY;
		pToken.data = prop;
		
		return pToken;
	}
	
	public void parseFile(String fileName) throws Exception {
		
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		Scanner s = new Scanner(in);
		
		//Check header for magic number (first line should just be the string "ply")
		if (s.nextLine().equals("ply") == false) {
			
			throw new Exception("Invalid PLY file");
		}
		
		//if there are still tokens left to process
		while (s.hasNext()) {
		
			String value = s.next();
			PlyToken pToken = new PlyToken();
			
			switch(value) {
				
				case "format":
					
					PlyToken.Format fmt = new PlyToken.Format();
					fmt.type = s.next();
					fmt.version = s.next();
					
					pToken.token = Token.FORMAT;
					pToken.data = fmt;
					
					this.header.add(pToken);

					break;
			
				case "comment":
					
					pToken.token = Token.COMMENT;
					pToken.data = s.nextLine();

					this.header.add(pToken);
					
					break;
					
				case "property":

					String line = s.nextLine();
					pToken = this.processProperty(line);
					
					this.header.add(pToken);
					
					break;
				
				case "element":
					
					PlyToken.Element ele = new PlyToken.Element();
					ele.name = s.next();
					ele.number = s.nextInt();
					
					pToken.token = Token.ELEMENT;
					pToken.data = ele;
					
					this.header.add(pToken);
					
					break;
				
				case "end_header":
					
					pToken.token = Token.END_HEADER;
					this.header.add(pToken);
					s.nextLine();//go to the end of the "end_headerline" to avoid adding a whitespace entry to the data member variable
					
					//the last token in the header has been reached copy all data
					while(s.hasNext()) {
						
						this.data.add(s.nextLine());
					}
					
					break;
					
				default:
					
					throw new Exception("Invalid token "+ value +" found");
			}
		}
	}
}
