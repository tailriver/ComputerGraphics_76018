import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.nio.*;

public class Plane extends Object3D{
  private final float[] VertexData = new float[]{
    -1.0f, -1.0f,  0f,  0.0f, 0.0f,-1.0f,   1f,0f,0f,1f,   0f,0f, //#0
     1.0f, -1.0f,  0f,  0.0f, 0.0f,-1.0f,   1f,1f,0f,1f,   1f,0f, //#1
     1.0f,  1.0f,  0f,  0.0f, 0.0f,-1.0f,   0f,1f,0f,1f,   1f,1f, //#2
    -1.0f,  1.0f,  0f,  0.0f, 0.0f,-1.0f,   0f,0f,1f,1f,   0f,1f, //#3 
  };//position            normal              color          texcoord
  private final int NormalOffset = Float.SIZE/8*3;
  private final int ColorOffset = Float.SIZE/8*6;
  private final int TexCoordOffset = Float.SIZE/8*10;
  private final int VertexCount = VertexData.length/12;
  private final int VertexSize = VertexData.length*Float.SIZE/8;
  private final FloatBuffer FBVertexData = FloatBuffer.wrap(VertexData);

  private final int[] ElementData = new int[]{
    0,1,2, 
    0,2,3 
  };
  private final int PolygonCount = ElementData.length/3;
  private final int ElementCount = ElementData.length;
  private final int ElementSize = ElementCount*Integer.SIZE/8;
  private final IntBuffer IBElementData = IntBuffer.wrap(ElementData);
  private int ElementBufferName;
  private int ArrayBufferName;
  private int TextureName;
  private int uniformTexture;
  
  private TextureImage img;

  public void init(GL2 gl, PMVMatrix mat, int programID){
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

    //img = new ImageLoader("circles.png");
    img = new DotImage(512, 512);
    gl.glGenTextures(1, tmp, 0);
    TextureName = tmp[0];
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glEnable(GL.GL_TEXTURE_2D);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
                       GL.GL_NEAREST);
    //                 GL.GL_LINEAR);

    /* // this configuration is for mip mapping 
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, 
                       GL2.GL_LINEAR_MIPMAP_LINEAR);
    */

    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                       GL.GL_NEAREST);
    //                       GL.GL_LINEAR);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, 
                       GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
                       GL2.GL_CLAMP);

    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL.GL_RGBA8, img.getWidth(),
                    img.getHeight(), 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, 
		    img.getByteBuffer());

    /* // this configuration is for mip mapping 
    int level=0;
    for(int w=img.getWidth();0<w;w = w/2){
      gl.glTexImage2D(GL2.GL_TEXTURE_2D, level, GL.GL_RGBA8, 
                      img.getWidth()>>level, img.getHeight()>>level,
                      0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, 
                      img.getByteBufferOfLevel(level));
      level++;
    }
    */

    uniformTexture = gl.glGetUniformLocation(programID, "texture0");
    gl.glUniform1i(uniformTexture, 0);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
  }

  public void display(GL2 gl, PMVMatrix mats){
    // If this object has own special shader, bind it 
    //    bindProgram(gl, ProgramName);

    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glUniform1i(uniformTexture, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glVertexAttribPointer(VERTEXPOSITION, 3, GL.GL_FLOAT, 
                             false, 48, 0);
    gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, 
			     false, 48, NormalOffset);
    gl.glVertexAttribPointer(VERTEXCOLOR, 4, GL.GL_FLOAT,
			     false, 48, ColorOffset);
    gl.glVertexAttribPointer(VERTEXTEXCOORD0, 2, GL.GL_FLOAT,
                             false, 48, TexCoordOffset);
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
   // Unbind the shader program of this object and restore the shader of the 
   // parent object 
   //  unbindProgram(gl);

    /* // drawing this object without shader 
    gl.glUseProgram(0);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glEnable(GL.GL_TEXTURE_2D);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ElementBufferName);
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
    gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
    gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
    gl.glVertexPointer(3, GL.GL_FLOAT, 48, 0);
    gl.glNormalPointer(GL.GL_FLOAT, 48, NormalOffset);
    gl.glColorPointer(4, GL.GL_FLOAT, 48, ColorOffset);
    gl.glTexCoordPointer(2, GL.GL_FLOAT, 48, TexCoordOffset);
    gl.glDrawElements(GL.GL_TRIANGLES, ElementCount, GL.GL_UNSIGNED_INT, 0);
    //gl.glDrawArrays(GL.GL_TRIANGLES,0,3);
    */

    /* // drawing a polygon by the most traditional way
    gl.glUseProgram(0);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glEnable(GL.GL_TEXTURE_2D);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glBegin(GL2.GL_TRIANGLES);
    gl.glTexCoord2f(0,0);
    gl.glColor3f(1f,0f,1f);
    gl.glVertex3f(-0.5f,-0.5f,-1f);
    gl.glTexCoord2f(0,1);
    gl.glColor3f(1f,1f,0f);
    gl.glVertex3f(0.5f,-0.5f,-1f);
    gl.glTexCoord2f(1,1);
    gl.glColor3f(0f,1f,1f);
    gl.glVertex3f(0.5f,0.5f,-1f);
    gl.glEnd();
    */
  }
}