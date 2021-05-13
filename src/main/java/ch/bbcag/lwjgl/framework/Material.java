package ch.bbcag.lwjgl.framework;

import org.joml.Vector2f;

import java.io.IOException;
import java.net.URISyntaxException;

public class Material extends Shader {
    public Texture albedoMap;
    public Texture normalMap;
    public Texture roughnessMap;
    public Texture aoMap;
    public Texture metalnessMap;
    public Vector2f offsetRepeat = new Vector2f(1, 1);

    public Material(String name) throws IOException, URISyntaxException {
        super("physicalmaterial");
        albedoMap = AssetLoader.loadTexture(name + "/albedo.png");
        normalMap = AssetLoader.loadTexture(name + "/normal.png");
        roughnessMap = AssetLoader.loadTexture(name + "/roughness.png");
        aoMap = AssetLoader.loadTexture(name + "/ao.png");
        metalnessMap = AssetLoader.loadTexture(name + "/metallic.png");
    }
}
