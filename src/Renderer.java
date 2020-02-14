
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

class Renderer implements GLEventListener {
	
	OptionsPanel gui;
	int renderingProgram;
	int vao[] = new int[1]; //vertex attribute object
	int vbo[] = new int[3]; //vertex buffer object
	int ebo[] = new int[1]; //element buffer object
	int texo; 		//texture buffer object
	float vertices[];
	float normals[];
	float uvs[];
	int faces[];
	int numVerts = 0;
	int numFaceIndex = 0;
	float rot;
	FloatBuffer vBuf;
	FloatBuffer nBuf;
	FloatBuffer tBuf;
	IntBuffer fBuf;
	GLAutoDrawable glAutoDrawable;
	
	public Renderer(OptionsPanel gui) {
		
		this.gui = gui;
	}
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		
		this.glAutoDrawable = glAutoDrawable;
		
		GL3 gl = glAutoDrawable.getGL().getGL3();
		
		//loadModel("test.obj");
		this.loadMesh("mario.ply");
		
		rot = 0;
		
		Texture tex = null;

		try {

			tex = TextureIO.newTexture(new File("dr.mario_di.png"), false);
			texo = tex.getTextureObject();
		
		} catch (Exception e) { 
			
			e.printStackTrace(); 
		}
		
		gl.glPointSize(5.0f);
		gl.glEnable(GL3.GL_DEPTH_TEST);  
		//gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);
		
		//set background colour
		gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
		
		//generate OPENGL buffers
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glGenBuffers(vbo.length, vbo, 0);	//generate vertex AND normal buffer AND texture buffer
		gl.glGenBuffers(ebo.length, ebo, 0);	//generate the EBO buffer to hold vert ID's for faces
		
		//VAO
		gl.glBindVertexArray(vao[0]);	//make vertex attribute object 0 active 
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make normal buffer active 
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, nBuf.limit() * Buffers.SIZEOF_FLOAT, nBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[2]);								//make tex buffer active 
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, tBuf.limit() * Buffers.SIZEOF_FLOAT, tBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[2] 
		
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0]);								//make faces buffer active 
		gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, fBuf.limit() * Buffers.SIZEOF_INT, fBuf, GL3.GL_STATIC_DRAW);	//copy fance indexs to EBO[0]
		
		//load/compile shaders from file
		renderingProgram = createShaders();
	}
		
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {

		GL3 gl = glAutoDrawable.getGL().getGL3();
		
		//use compiled shaders
		gl.glUseProgram(renderingProgram);
		
		//camera variables
		float np = (float) gui.nearPlaneSpinner.getValue();
		float fp = (float) gui.farPlaneSpinner.getValue();
		float scale = (float) gui.scaleSpinner.getValue();
		
		//object location
		float ox = (float) gui.oxSpinner.getValue();
		float oy = (float) gui.oySpinner.getValue();
		float oz = (float) gui.ozSpinner.getValue();
		
		//light location
		float lx = (float) gui.lxSpinner.getValue();
		float ly = (float) gui.lySpinner.getValue();
		float lz = (float) gui.lzSpinner.getValue();
		
		//shader uniform variables
		int rotx = gl.glGetUniformLocation(renderingProgram, "rotX");
		gl.glUniformMatrix4fv(rotx, 1, false, Buffers.newDirectFloatBuffer(Matrix.rot4D(rot, Matrix.X)));
		
		int roty = gl.glGetUniformLocation(renderingProgram, "rotY");
		gl.glUniformMatrix4fv(roty, 1, false, Buffers.newDirectFloatBuffer(Matrix.rot4D(rot, Matrix.Y)));
		
		int lightPos = gl.glGetUniformLocation(renderingProgram, "lightPosition");
		gl.glUniform3f(lightPos, lx, ly, lz);
		
		int modelColour = gl.glGetUniformLocation(renderingProgram, "modelColour");
		gl.glUniform3f(modelColour, 0.8f, 0.8f, 0.8f);

		int offset = gl.glGetUniformLocation(renderingProgram, "offset");
		gl.glUniform3f(offset, oy, ox, oz);
		
		rot += 0.01;
		
		//shader uniform variables
		int persp = gl.glGetUniformLocation(renderingProgram, "perspective");
		gl.glUniformMatrix4fv(persp, 1, false, Buffers.newDirectFloatBuffer(Matrix.perspective(scale, np, fp)));

		//vert position
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);		///make vert buffer active
		gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 3, 0);
		gl.glEnableVertexAttribArray(0);
		
		//normal position
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);		//make normal buffer active
		gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 3, 0);
		gl.glEnableVertexAttribArray(1);
		
//		// activate texture unit #0 and bind it to the brick texture object
		gl.glActiveTexture(GL3.GL_TEXTURE0);
		gl.glBindTexture(GL3.GL_TEXTURE_2D, texo);
		
//		//tex position
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[2]);		//make normal buffer active
		gl.glVertexAttribPointer(2, 2, GL3.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 2, 0);
		gl.glEnableVertexAttribArray(2);
		
		//faces
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0]);	//make face buffer active
		
		//set background colour and clear the screen
		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
		
		//DRAW
		//gl.glDrawArrays(GL3.GL_TRIANGLES, 0, numVerts);
		//gl.glDrawArrays(GL3.GL_POINTS, 0, numVerts);
		gl.glDrawElements(GL3.GL_TRIANGLES, numFaceIndex, GL3.GL_UNSIGNED_INT, 0);
	}
	
	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		
		
	}
	
	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		
		GL3 gl = glAutoDrawable.getGL().getGL3();
		
		System.out.println("W = " + width + " H = " + height);
		
		int panelResolution = gl.glGetUniformLocation(renderingProgram, "panelResolution");
		gl.glUniform2f(panelResolution, glAutoDrawable.getSurfaceWidth(), glAutoDrawable.getSurfaceHeight());
	}
	
	private String[] readShaderSource(String path) {
		
		ArrayList<String> vertexList = new  ArrayList<>();

		try (BufferedReader in = new BufferedReader(new FileReader(path))) {
			
			String line = in.readLine();

			while (line != null) {

				vertexList.add(line);
				vertexList.add("\n");
				line = in.readLine();
			}

		} catch (Exception ex) {
			
			System.out.println(ex.toString());
		}

		return vertexList.toArray(new String[0]);
	}

	public int createShaders() {
			
		GL3 gl = (GL3) GLContext.getCurrentGL();

		String vshaderSource[] = readShaderSource("vertex.glsl");
		String fshaderSource[] = readShaderSource("fragment.glsl");
		
		int vShader = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);
		
		int fShader = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);
		
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		
		return vfprogram;
	}
	
	public void loadMesh(String fileName) {
		
		PlyParser object = new PlyParser();
		
		try {
			
			object.parseFile(fileName);
			this.vertices = object.getVertArray();
			this.normals = object.getNormArray();
			this.faces = object.getFacesArray();
			this.uvs = object.getUVArray();
			this.numVerts = object.numElements("vertex");
			this.numFaceIndex = this.faces.length;
			
			this.vBuf = Buffers.newDirectFloatBuffer(vertices);
			this.nBuf = Buffers.newDirectFloatBuffer(normals);
			this.tBuf = Buffers.newDirectFloatBuffer(uvs);
			this.fBuf = Buffers.newDirectIntBuffer(faces);
		
		} catch (Exception ex) {
			
			System.err.println("Could not load PLY file");
			ex.printStackTrace();
		}		
	}
	
	//store in arrays, the verts and faces of the first model in the OBJ file
	public ObjParser loadModel(String name) {
		
		ObjParser obj = new ObjParser();
		
		try {
			
			obj.parseFile(name);
			this.vertices = obj.model.get(0).vertsToArray();
			this.normals = obj.model.get(0).normalsToArray();
			this.faces = obj.model.get(0).facesToArray();
			this.uvs = obj.model.get(0).uvsToArray();
			this.numVerts = vertices.length / 3;
			this.numFaceIndex = faces.length;
			
			this.vBuf = Buffers.newDirectFloatBuffer(vertices);
			this.nBuf = Buffers.newDirectFloatBuffer(normals);
			this.tBuf = Buffers.newDirectFloatBuffer(uvs);
			this.fBuf = Buffers.newDirectIntBuffer(faces);
			
		} catch (Exception ex) {
			
			System.err.println("Could not load OBJ file.");
		}
		
		return obj;
	}
}
