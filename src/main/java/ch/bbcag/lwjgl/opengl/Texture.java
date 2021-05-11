package ch.bbcag.lwjgl.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture {
    public int id;
    public int unit = 0;
    public int loadTexture(String filename) throws IOException, URISyntaxException {
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        var bytes = Files.readAllBytes(Path.of(Texture.class.getResource(filename).toURI()));
        var buffer = BufferUtils.createByteBuffer(3  * bytes.length);
        buffer.put(bytes);
        buffer.flip();

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);
        ByteBuffer data = stbi_load_from_memory(buffer, width, height, components, 0);
        var w = width.get(0);
        var h = height.get(0);
        //System.out.println(filename + " " + w + " " + h + " " + components.get(0));
        if (components.get(0) == 4) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h,  0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            glGenerateMipmap(GL_TEXTURE_2D);
        } else if (components.get(0) == 3) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, w, h, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, data);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            glGenerateMipmap(GL_TEXTURE_2D);
        } else if (components.get(0) == 1) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RED, w, h, 0, GL_RED, GL_RG32F , data);
        }

        stbi_image_free(data);
        return id;
    }

    public Texture(String path) throws IOException, URISyntaxException {
        try {
            loadTexture(path);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void bind(int unit) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, id);
    }
}
