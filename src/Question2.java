public class Question2 extends QuestionBase {
	public Question2() {
		final Object3D obj = new Icosahedron();
		final Shader shader = new Shader("resource/simple.vert", "resource/q2.frag");
		super.setObject(obj);
		super.setShader(shader);
		super.setBackgroundColor(.9f, .8f, 1f, 1f);
		super.setFrustum(-.7f, .7f, -.7f, .7f, 1f, 100f);
		super.setRotation(0f, .4f);

		// for snapshot
		//super.setRotateAngle(18f, 0f);
	}

	public static void main(String[] args) {
		final Question2 t = new Question2();
		t.start(t.getClass().getName());
	}
}
