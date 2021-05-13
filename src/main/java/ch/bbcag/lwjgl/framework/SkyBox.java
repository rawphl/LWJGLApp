package ch.bbcag.lwjgl.framework;

public class SkyBox extends Object3D {
    public VertexArrayObject vao;
    public SkyBoxMaterial material;

    public SkyBox(SkyBoxMaterial material) throws Exception {
        vao = AssetLoader.loadSingleObj("/meshes/cube.obj");
        this.material = material;
    }
}
