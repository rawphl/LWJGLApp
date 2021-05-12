package ch.bbcag.lwjgl.framework;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL30C.GL_RG32F;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class AssetLoader {
    private final static Logger LOGGER = Logger.getLogger(AssetLoader.class.getName());

    public static Texture loadTexture(String filename) throws IOException, URISyntaxException {
        return new Texture(_loadTexture(filename));
    }

    private static int _loadTexture(String filename) throws IOException, URISyntaxException {
        LOGGER.info("loadTexture(" + filename + ")");
        var id = GL11.glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        var bytes = Files.readAllBytes(Path.of(Texture.class.getResource(filename).toURI()));
        var buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes);
        buffer.flip();

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);
        ByteBuffer data = stbi_load_from_memory(buffer, width, height, components, 0);
        var w = width.get(0);
        var h = height.get(0);

        if (components.get(0) == 4) {
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            glGenerateMipmap(GL_TEXTURE_2D);
        } else if (components.get(0) == 3) {
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGB, w, h, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, data);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            glGenerateMipmap(GL_TEXTURE_2D);
        } else if (components.get(0) == 1) {
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, w, h, 0, GL_RED, GL_RG32F, data);
        }

        stbi_image_free(data);
        return id;
    }

    public static int loadCubeMapTexture(String filename, int target, int id) throws IOException, URISyntaxException {
        LOGGER.info("loadCubeMapTexture(" + filename + ")");

        glBindTexture(GL_TEXTURE_CUBE_MAP, id);

        var bytes = Files.readAllBytes(Path.of(Texture.class.getResource(filename).toURI()));
        var buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes);
        buffer.flip();

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);
        ByteBuffer data = stbi_load_from_memory(buffer, width, height, components, 0);
        var w = width.get(0);
        var h = height.get(0);

        if (components.get(0) == 4) {
            GL11.glTexImage2D(target, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
        } else if (components.get(0) == 3) {
            GL11.glTexImage2D(target, 0, GL11.GL_RGB, w, h, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, data);
        } else if (components.get(0) == 1) {
            GL11.glTexImage2D(target, 0, GL_RED, w, h, 0, GL_RED, GL_RG32F, data);
        }

        stbi_image_free(data);
        return id;
    }

    public static int loadCubeMapTextures(String[] paths) throws IOException, URISyntaxException {
        var id = glGenTextures();
        Texture[] textures = new Texture[paths.length];
        for (var i = 0; i < paths.length; i++) {
            loadCubeMapTexture(paths[i], GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, id);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        return id;
    }

    public static List<VertexArrayObject> loadObj(String path) throws Exception {
        LOGGER.info("loadObj(" + path + ")");
        AIScene aiScene = aiImportFile("src/main/resources" + path, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_FlipUVs);
        if (aiScene.mNumMeshes() <= 0) throw new Exception("No meshes fround in file: " + path);
        var list = new ArrayList<VertexArrayObject>();
        for (var i = 0; i < aiScene.mNumMeshes(); i++) {
            var mesh = AIMesh.create(aiScene.mMeshes().get(i));
            var name = mesh.mName();
            var materialName = mesh.mMaterialIndex(0).mName().dataString();
            LOGGER.info(materialName);
            var str = name.dataString();
            LOGGER.info("Mesh(name=" + str + ")");
            var vao = new VertexArrayObject(name.toString(), mesh);
            list.add(vao);
        }
        return list;
    }

    public static VertexArrayObject loadSingleObj(String path) throws Exception {
        LOGGER.info("loadObj(" + path + ")");
        AIScene aiScene = aiImportFile("src/main/resources" + path, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_FlipUVs);
        if (aiScene.mNumMeshes() <= 0) throw new Exception("No meshes fround in file: " + path);
        var mesh = AIMesh.create(aiScene.mMeshes().get(0));
        var name = mesh.mName();
        var materialName = mesh.mMaterialIndex(0).mName().dataString();
        LOGGER.info(materialName);
        var str = name.dataString();
        LOGGER.info("Mesh(name=" + str + ")");
        var vao = new VertexArrayObject(name.toString(), mesh);
        return vao;
    }
}

