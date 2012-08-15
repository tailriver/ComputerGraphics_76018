import java.awt.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import com.jogamp.opengl.util.*;


public abstract class QuestionBase implements GLEventListener {
	JFrame frame;
	GLCanvas canvas;
	protected final int SCREENW=320, SCREENH=320;

	Object3D obj;
	Shader shader;
	final PMVMatrix mats;
	int uniformMat, uniformLight;

	float[] backgroundColor = {0f, 0f, 0f, 0f};
	float[] lightDir = {0f, 10f, -10f};

	float[] translatePosition = {0f, 0f, -2f};
	float rotateAngle = 0f, rotateAnglePerFrame = 0f;
	float[] rotateAxis = {0f, 1f, 0f};
	float[] frustum = {-1f, 1f, -1f, 1f, 1f, 100f};

	public QuestionBase() {
		mats = new PMVMatrix();
	}

	public void start(final String title) {
		try {
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					initGLUI();
					frame.setTitle(title);
				}
			});
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					Animator animator = new Animator(canvas);
					animator.start();
				}
			});
		} catch(java.lang.reflect.InvocationTargetException e) {
			System.out.println(e);
			e.printStackTrace();
		} catch(InterruptedException e) {
			System.out.println(e);
		}
	}

	private void initGLUI() {
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile); 
		capabilities.setSampleBuffers(true); //This is effective if supported
		capabilities.setNumSamples(4);
		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.setPreferredSize(new Dimension(SCREENW, SCREENH));
		canvas.setVisible(true);

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(canvas);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public void setObject(Object3D obj) {
		this.obj = obj;		
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public void setBackgroundColor(float r, float g, float b, float a) {
		backgroundColor[0] = r;
		backgroundColor[1] = g;
		backgroundColor[2] = b;
		backgroundColor[3] = a;
	}

	public void setTranslation(float x, float y, float z) {
		translatePosition[0] = x;
		translatePosition[1] = y;
		translatePosition[2] = z;
	}

	public void setRotation(float offsetDegree, float speedDegree) {
		rotateAngle  = offsetDegree;
		rotateAnglePerFrame = speedDegree;
	}

	public void setRotation(float offsetDegree, float speedDegree,
			float axisX, float axisY, float axisZ) {
		setRotation(offsetDegree, speedDegree);
		rotateAxis[0] = axisX;
		rotateAxis[1] = axisY;
		rotateAxis[2] = axisZ;
	}

	/**
	 * 
	 * @param l left
	 * @param r right
	 * @param b bottom
	 * @param t top
	 * @param n zNear
	 * @param f zFar
	 */
	public void setFrustum(float l, float r, float b, float t, float n, float f) {
		frustum[0] = l;
		frustum[1] = r;
		frustum[2] = b;
		frustum[3] = t;
		frustum[4] = n;
		frustum[5] = f;
	}

	public void setLightDir(float x, float y, float z) {
		lightDir[0] = x;
		lightDir[1] = y;
		lightDir[2] = z;
	}

	public JFrame getFrame() {
		return frame;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		final GL2 gl = drawable.getGL().getGL2();
		gl.glViewport(0, 0, SCREENW, SCREENH);

		// Background color
		gl.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], backgroundColor[3]);
		gl.glClearDepth(1f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT,1);

		// VS setup
		gl.glCreateShader(GL2GL3.GL_VERTEX_SHADER);
		shader.init(gl);
		int programName = shader.getID();

		// set attributes of VS
		gl.glBindAttribLocation(programName, Object3D.VERTEXPOSITION, "inposition");
		gl.glBindAttribLocation(programName, Object3D.VERTEXCOLOR, "incolor");
		gl.glBindAttribLocation(programName, Object3D.VERTEXNORMAL, "innormal");
		gl.glBindAttribLocation(programName, Object3D.VERTEXTEXCOORD0,"intexcoord0");
		shader.link(gl);

		uniformMat = gl.glGetUniformLocation(programName, "mat");
		uniformLight = gl.glGetUniformLocation(programName, "lightdir");
		gl.glUseProgram(programName);
		gl.glUniform3f(uniformLight, lightDir[0], lightDir[1], lightDir[2]);
		obj.init(gl, mats, programName);
		gl.glUseProgram(0);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		mats.glMatrixMode(GL2.GL_MODELVIEW);
		mats.glLoadIdentity();
		mats.glTranslatef(translatePosition[0], translatePosition[1], translatePosition[2]);
		rotateAngle += rotateAnglePerFrame;
		mats.glRotatef(rotateAngle, rotateAxis[0], rotateAxis[1], rotateAxis[2]);
		mats.glMatrixMode(GL2.GL_PROJECTION);
		mats.glLoadIdentity();
		mats.glFrustumf(frustum[0], frustum[1], frustum[2], frustum[3], frustum[4], frustum[5]);
		mats.update();
		gl.glUseProgram(shader.getID());
		gl.glUniformMatrix4fv(uniformMat, 3, false, mats.glGetPMvMviMatrixf());

		obj.display(gl, mats);
		gl.glFlush();
		gl.glUseProgram(0);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
	}
}
