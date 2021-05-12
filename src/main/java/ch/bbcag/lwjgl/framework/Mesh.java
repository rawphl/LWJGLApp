package ch.bbcag.lwjgl.framework;

public class Mesh extends Object3D {
    public final VertexArrayObject vao;
    public Material material;

    public Mesh(VertexArrayObject vao, Material material) {
        this.vao = vao;
        this.material = material;
    }
}