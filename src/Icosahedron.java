import java.nio.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;

public class Icosahedron extends Object3D {
	private final float L = 0.8f;

	private final float v10 = 1f;
	private final float va0 = (float) (1 / Math.sqrt(5));
	private final float vb0 = (1 - va0) / 2;
	private final float vc0 = (1 + va0) / 2;
	private final float vd0 = (float) Math.sqrt(vb0);
	private final float ve0 = (float) Math.sqrt(vc0);
	private final float ratio = L / ( 2 * vd0 );
	private final float v1 = ratio * v10;
	private final float va = ratio * va0;
	private final float vb = ratio * vb0;
	private final float vc = ratio * vc0;
	private final float vd = ratio * vd0;
	private final float ve = ratio * ve0;

	private final float[] VertexData = new float[]{
			 0f,  v1,    0f,	 0f,  v1,    0f,	1f,0f,1f,1f,	1f,0f,
			 0f,  va,  2*va,	 0f,  va,  2*va,	0f,1f,1f,1f,	0f,1f,
			-ve,  va,    vb,	-ve,  va,    vb,	1f,1f,0f,1f,	1f,1f,
			-vd,  va,   -vc,	-vd,  va,   -vc,	0f,1f,1f,1f,	0f,1f,
			 vd,  va,   -vc,	 vd,  va,   -vc,	1f,1f,1f,1f,	0f,0f,
			 ve,  va,    vb,	 ve,  va,    vb,	1f,1f,0f,1f,	1f,1f,
			-vd, -va,    vc,	-vd, -va,    vc,	1f,0f,1f,1f,	1f,0f,
			-ve, -va,   -vb,	-ve, -va,   -vb,	1f,1f,1f,1f,	0f,0f,
			 0f, -va, -2*va,	 0f, -va, -2*va,	1f,1f,0f,1f,	1f,1f,
			 ve, -va,   -vb,	 ve, -va,   -vb,	1f,0f,1f,1f,	1f,0f,
			 vd, -va,    vc,	 vd, -va,    vc,	1f,1f,1f,1f,	0f,0f,
			 0f, -v1,    0f,	 0f, -v1,    0f,	0f,1f,1f,1f,	0f,1f,
	};	//	position			normal				color			texcoord
	private final int NormalOffset = Float.SIZE/8*3;
	private final int ColorOffset = Float.SIZE/8*6;
	private final int TexCoordOffset = Float.SIZE/8*10;
	private final int VertexSize = VertexData.length*Float.SIZE/8;
	private final FloatBuffer FBVertexData = FloatBuffer.wrap(VertexData);

	private final int[] ElementData = new int[]{
			0,1,2,
			0,2,3,
			0,3,4,
			0,4,5,
			0,5,1,
			1,6,2,
			2,7,3,
			3,8,4,
			4,9,5,
			5,10,1,
			1,10,6,
			2,6,7,
			3,7,8,
			4,8,9,
			5,9,10,
			11,6,10,
			11,7,6,
			11,8,7,
			11,9,8,
			11,10,9,
	};
	private final int ElementCount = ElementData.length;
	private final int ElementSize = ElementCount*Integer.SIZE/8;
	private final IntBuffer IBElementData = IntBuffer.wrap(ElementData);
	private int ElementBufferName;
	private int ArrayBufferName;
	private int TextureName;
	private int uniformTexture;

	private TextureImage img;

	public void init(GL2 gl, PMVMatrix mat, int programID) {
		initCommon(mat, programID);
		int[] tmp = new int[1]; 
		gl.glGenBuffers(1, tmp, 0);
		ArrayBufferName = tmp[0];
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, VertexSize, FBVertexData, 
				GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

		gl.glGenBuffers(1, tmp, 0);
		ElementBufferName = tmp[0];
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ElementBufferName);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, ElementSize, IBElementData, 
				GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);

		// select image
		//img = new ImageLoader("circles.png");
		img = new DotImage(256, 256);

		gl.glGenTextures(1, tmp, 0);
		TextureName = tmp[0];
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST);
		//		GL.GL_LINEAR);

		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
		//		GL.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, 
				GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL2.GL_CLAMP);

		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL.GL_RGBA8, img.getWidth(),
				img.getHeight(), 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, 
				img.getByteBuffer());

		uniformTexture = gl.glGetUniformLocation(programID, "texture0");
		gl.glUniform1i(uniformTexture, 0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}

	public void display(GL2 gl, PMVMatrix mats) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
		gl.glUniform1i(uniformTexture, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
		gl.glVertexAttribPointer(VERTEXPOSITION, 3, GL.GL_FLOAT, false, 48, 0);
		gl.glVertexAttribPointer(VERTEXNORMAL, 3, GL.GL_FLOAT, false, 48, NormalOffset);
		gl.glVertexAttribPointer(VERTEXCOLOR, 4, GL.GL_FLOAT, false, 48, ColorOffset);
		gl.glVertexAttribPointer(VERTEXTEXCOORD0, 2, GL.GL_FLOAT, false, 48, TexCoordOffset);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ElementBufferName);

		gl.glEnableVertexAttribArray(VERTEXPOSITION);
		gl.glEnableVertexAttribArray(VERTEXCOLOR);
		gl.glEnableVertexAttribArray(VERTEXNORMAL);
		gl.glEnableVertexAttribArray(VERTEXTEXCOORD0);

		gl.glDrawElements(GL.GL_TRIANGLES, ElementCount, GL.GL_UNSIGNED_INT, 0);

		gl.glDisableVertexAttribArray(VERTEXPOSITION);
		gl.glDisableVertexAttribArray(VERTEXNORMAL);
		gl.glDisableVertexAttribArray(VERTEXCOLOR);
		gl.glDisableVertexAttribArray(VERTEXTEXCOORD0);

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
}
