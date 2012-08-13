import java.nio.ByteBuffer;

public interface TextureImage {
	public int getWidth(); 
	public int getHeight(); 
	public ByteBuffer getByteBuffer();
	public ByteBuffer getByteBufferOfLevel(int level);
}
