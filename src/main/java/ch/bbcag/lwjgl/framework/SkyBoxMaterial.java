package ch.bbcag.lwjgl.framework;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class SkyBoxMaterial extends Shader {
    public int cubeMapId;

    public SkyBoxMaterial(String[] paths) throws IOException, URISyntaxException {
        super("skybox");
        cubeMapId = AssetLoader.loadCubeMapTextures(paths);
    }

    public void bind(int unit) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapId);
        glUniform1i(glGetUniformLocation(programHandle, "cubeMap"), 0);
    }
}
