package ch.bbcag.lwjgl.framework;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    public int programHandle;
    private final FloatBuffer tmp = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer tmp2 = BufferUtils.createFloatBuffer(9);
    private final FloatBuffer tmp3 = BufferUtils.createFloatBuffer(3);
    private final FloatBuffer tmp4 = BufferUtils.createFloatBuffer(2);

    public Shader(String name) throws IOException, URISyntaxException {
        var vertexShaderPath = Path.of(App.class.getResource("/shaders/" + name + ".vert").toURI());
        var vertexShaderSource = Files.readString(vertexShaderPath);
        var vertexShaderHandle = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderHandle, vertexShaderSource);
        glCompileShader(vertexShaderHandle);
        int status = glGetShaderi(vertexShaderHandle, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(vertexShaderHandle));
        }

        var fragmentShaderPath = Path.of(App.class.getResource("/shaders/" + name + ".frag").toURI());
        var fragmentShaderSoruce = Files.readString(fragmentShaderPath);
        var fragmentShaderHandle = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderHandle, fragmentShaderSoruce);
        glCompileShader(fragmentShaderHandle);
        status = glGetShaderi(fragmentShaderHandle, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(fragmentShaderHandle));
        }

        programHandle = glCreateProgram();
        glAttachShader(programHandle, vertexShaderHandle);
        glAttachShader(programHandle, fragmentShaderHandle);
        glLinkProgram(programHandle);

        var error = glGetProgramInfoLog(programHandle);

        System.out.println(error);
    }

    public void use() {
        glUseProgram(programHandle);
    }

    public void setUniform(String name, Object value) {
        if (value instanceof Vector4f) {
            glUniform4fv(glGetUniformLocation(programHandle, name), ((Vector4f) value).get(tmp4));
        }
        if (value instanceof Vector2f) {
            glUniform2fv(glGetUniformLocation(programHandle, name), ((Vector2f) value).get(tmp4));
        }
        if (value instanceof Vector3f) {
            glUniform3fv(glGetUniformLocation(programHandle, name), ((Vector3f) value).get(tmp3));
        }

        if (value instanceof Matrix3f) {
            glUniformMatrix3fv(glGetUniformLocation(programHandle, name), false, ((Matrix3f) value).get(tmp2));
        }

        if (value instanceof Matrix4f) {
            glUniformMatrix4fv(glGetUniformLocation(programHandle, name), false, ((Matrix4f) value).get(tmp));
        }
    }

    public void unuse() {
        glUseProgram(0);
    }
}
