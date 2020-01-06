
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.awt.event.*;
import java.lang.Math;

class Renderer implements GLEventListener {
	
	int renderingProgram;
	int vao[] = new int[1]; //vertex attribute object
	int vbo[] = new int[2]; //vertex buffer object
	int ebo[] = new int[1]; //element buffer object
	float vertices[];
	float normals[];
	int faces[];
	int numVerts = 0;
	int numFaceIndex = 0;
	float rot;
	FloatBuffer vBuf;
	FloatBuffer nBuf;
	IntBuffer fBuf;
	ObjParser obj;
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		
		GL3 gl = glAutoDrawable.getGL().getGL3();
		
		this.loadModel(obj);	

		vBuf = Buffers.newDirectFloatBuffer(vertices);
		nBuf = Buffers.newDirectFloatBuffer(normals);
		fBuf = Buffers.newDirectIntBuffer(faces);
		
		rot = 0;	//set initial rotation to 0
		
		gl.glPointSize(5.0f);
		gl.glEnable(GL3.GL_DEPTH_TEST);  
		//gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_LINE);
		
		//set background colour
		gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
		
		//generate OPENGL buffers
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glGenBuffers(vbo.length, vbo, 0);	//generate vertex AND normal buffer
		gl.glGenBuffers(ebo.length, ebo, 0);	//generate the EBO buffer to hold vert ID's for faces
		
		//VAO
		gl.glBindVertexArray(vao[0]);
		
		//verts
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);								//make vert buffer active 
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vBuf.limit() * Buffers.SIZEOF_FLOAT, vBuf, GL3.GL_STATIC_DRAW);	//copy verts to VBO[0] 
		
		//normals
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);								//make normal buffer active 
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, nBuf.limit() * Buffers.SIZEOF_FLOAT, nBuf, GL3.GL_STATIC_DRAW);	//copy normals to VBO[1] 
		
		//faces
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
		
		float light[] = {1.0f, 0.0f, -2.0f};
		
		//shader uniform variables
		int rotx = gl.glGetUniformLocation(renderingProgram, "rotX");
		gl.glUniformMatrix3fv(rotx, 1, false, Buffers.newDirectFloatBuffer(Matrix.rot3D(0, Matrix.X)));
		
		int roty = gl.glGetUniformLocation(renderingProgram, "rotY");
		gl.glUniformMatrix3fv(roty, 1, false, Buffers.newDirectFloatBuffer(Matrix.rot3D(rot, Matrix.Y)));
		
		int modelColour = gl.glGetUniformLocation(renderingProgram, "modelColour");
		gl.glUniform3f(modelColour, 1.0f, 0.5f, 0.31f);
		
		int lightPos = gl.glGetUniformLocation(renderingProgram, "lightPosition");
		gl.glUniform3f(lightPos, light[0], light[1], light[2]);
		
		//update rotation amount every frame
		rot += 0.005;

		//vert position
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);		///make vert buffer active
		gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 3, 0);
		gl.glEnableVertexAttribArray(0);
		
		//normal position
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]);		//make normal buffer active
		gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 3, 0);
		gl.glEnableVertexAttribArray(1);
		
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
		
	}
	
	private String[] readShaderSource(String path) {
		
		ArrayList<String> vertexList = new  ArrayList<String>();

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
	
	//store in arrays, the verts and faces of the first model in the OBJ file
	public void loadModel(ObjParser obj) {
		
		obj = new ObjParser();
		
		try {
			
			obj.parseFile("test.obj");
			this.vertices = obj.model.get(0).vertsToArray();
			this.normals = obj.model.get(0).normalsToArray();
			this.faces = obj.model.get(0).facesToArray();
			this.numVerts = vertices.length / 3;
			this.numFaceIndex = faces.length;
			
		} catch (Exception ex) {
			
			System.err.println("Could not load OBJ file.");
		}
	}
}
