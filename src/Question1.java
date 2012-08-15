public class Question1 extends QuestionBase {
	public Question1(float z) {
		final Object3D obj = new Plane();
		final Shader shader = new Shader("resource/simple.vert", "resource/q1.frag");
		super.setObject(obj);
		super.setShader(shader);
		super.setBackgroundColor(.9f, .8f, 1f, 1f);
		super.setTranslation(0f, 0f, z);
		super.setRotation(0f, .4f, 0f, 0f, 1f);

		// for snapshot
//		super.setRotation(0f, 0f);
//		super.setRotation(25f, 0f);	// 4
//		super.setRotation(30f, 0f);	// 4
//		super.setRotation(40f, 0f);	// 6,8
//		super.setRotation(45f, 0f);	// 4,6,8
//		super.setRotation(75f, 0f);	// 6
	}

	public static void main(String[] args) {
		final float[] samples = {-2f, -4f, -6f, -8f};

		for (int i = 0; i < samples.length; i++) {
			final Question1 t = new Question1(samples[i]);
			t.start(t.getClass().getName() + " z:" + String.valueOf(samples[i]));	
			t.getFrame().setLocation(t.SCREENW * (i%2), t.SCREENH * (i/2));
		}
	}
}
