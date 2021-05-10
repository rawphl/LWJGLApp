package ch.bbcag.lwjgl;

import ch.bbcag.lwjgl.opengl.Material;
import ch.bbcag.lwjgl.opengl.Shader;
import ch.bbcag.lwjgl.opengl.VertexArrayObject;

import java.util.List;
import java.util.stream.Collectors;

public class Mesh extends Object3D {
    public static List<Mesh> fromObj(String path, Material material) throws Exception {
        return VertexArrayObject.fromObj(path).stream().map(vao -> new Mesh(vao, material)).collect(Collectors.toList());
    }

    private final VertexArrayObject vao;
    private Material material;

    public Mesh(VertexArrayObject vao) {
        this.vao = vao;
    }

    public Mesh(VertexArrayObject vao, Material material) {
        this.vao = vao;
        this.material = material;
    }

    public void draw(Camera camera) {
        material.bindTextures();
        material.setUniform("normalMatrix", normalMatrix);
        material.setUniform("modelMatrix", modelMatrix);
        material.setUniform("viewMatrix", camera.viewMatrix);
        material.setUniform("projectionMatrix", camera.projectionMatrix);
        material.setUniform("cameraPosition", camera.position);
        vao.draw();
    }
}
