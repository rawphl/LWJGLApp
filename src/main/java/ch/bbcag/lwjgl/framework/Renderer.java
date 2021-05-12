package ch.bbcag.lwjgl.framework;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class Renderer {
    private boolean depthTestEnabled = false;
    private int width;
    private int height;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setDepthTest(boolean depthTestEnabled) {
        this.depthTestEnabled = depthTestEnabled;
        if(depthTestEnabled) glEnable(GL_DEPTH_TEST);
        if(!depthTestEnabled) glDisable(GL_DEPTH_TEST);
    }

    public void renderMesh(Mesh mesh, Camera camera) {
        mesh.updateMatrices();
        var material = mesh.material;
        material.use();
        material.albedoMap.bind(0);
        glUniform1i(glGetUniformLocation(material.programHandle, "albedoMap"), 0);

        material.normalMap.bind(1);
        glUniform1i(glGetUniformLocation(material.programHandle, "normalMap"), 1);

        material.roughnessMap.bind(2);
        glUniform1i(glGetUniformLocation(material.programHandle, "roughnessMap"), 2);

        material.aoMap.bind(3);
        glUniform1i(glGetUniformLocation(material.programHandle, "aoMap"), 3);

        material.metalnessMap.bind(4);
        glUniform1i(glGetUniformLocation(material.programHandle, "metalnessMap"), 4);

        material.setUniform("offsetRepeat", material.offsetRepeat);
        material.setUniform("normalMatrix", mesh.normalMatrix);
        material.setUniform("modelMatrix", mesh.modelMatrix);
        material.setUniform("viewMatrix", camera.viewMatrix);
        material.setUniform("projectionMatrix", camera.projectionMatrix);
        material.setUniform("cameraPosition", camera.position);
        mesh.vao.draw();
    }

    public void renderSkyBox(SkyBox skybox, Camera camera) {
        skybox.updateMatrices();
        var material = skybox.material;
        material.use();
        material.bind(0);
        material.setUniform("normalMatrix", skybox.normalMatrix);
        material.setUniform("modelMatrix", skybox.modelMatrix);
        material.setUniform("viewMatrix", camera.viewMatrix);
        material.setUniform("projectionMatrix", camera.projectionMatrix);
        material.setUniform("cameraPosition", camera.position);
        glDepthMask(false);
        skybox.vao.draw();
        glDepthMask(true);
    }
}
