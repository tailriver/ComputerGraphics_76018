public class Question3 extends QuestionBase {
	public Question3() {
		final Object3D obj = new Icosahedron();
		final Shader shader = new Shader("resource/simple.vert", "resource/q3.frag");
		super.setObject(obj);
		super.setShader(shader);
		super.setLightDir(7f, -7f, 1f);
		super.setFrustum(-.7f, .7f, -.7f, .7f, 1f, 100f);
		super.setRotation(18f, 0f);
	}

	public static void main(String[] args) {
		final Question3 t = new Question3();
		t.start(t.getClass().getName());
	}
}
