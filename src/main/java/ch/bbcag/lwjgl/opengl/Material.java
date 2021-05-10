package ch.bbcag.lwjgl.opengl;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL20.*;

public class Material extends Shader {
    public Texture diffuse;
    public Texture normal;
    public Texture roughness;
    public Texture displacement;
    public Texture metalness;
    public Vector2f offsetRepeat = new Vector2f(5, 2);


    public Material(String name, String diffusePath, String normalPath,String roughnessPath, String displacementPath, String metalnessPath) throws IOException, URISyntaxException {
        super(name);
        diffuse = new Texture(diffusePath);
        normal = new Texture(normalPath);
        roughness = new Texture(roughnessPath);
        displacement = new Texture(displacementPath);
        metalness = new Texture(metalnessPath);
    }

    private FloatBuffer buffer = BufferUtils.createFloatBuffer(2);

    public void bindTextures() {
        use();
        diffuse.bind(0);
        glUniform1i(glGetUniformLocation(programHandle, "diffuseTexture"), 0);

        normal.bind(1);
        glUniform1i(glGetUniformLocation(programHandle, "normalTexture"), 1);

        roughness.bind(2);
        glUniform1i(glGetUniformLocation(programHandle, "roughnessTexture"), 2);

        displacement.bind(3);
        glUniform1i(glGetUniformLocation(programHandle, "displacementTexture"), 3);

        metalness.bind(4);
        glUniform1i(glGetUniformLocation(programHandle, "metalnessTexture"), 4);

        setUniform("offsetRepeat", offsetRepeat);
    }
}