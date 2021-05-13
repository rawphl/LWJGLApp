package ch.bbcag.lwjgl.framework;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15C.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class VertexArrayObject {
    public static final int POSITION_ATTRIBUTE_LOCATION = 0;
    public static final int NORMAL_ATTRIBUTE_LOCATION = 1;
    public static final int UV_ATTRIBUTE_LOCATION = 2;
    public final int handle;
    public String name;
    private final int elementCount;

    public VertexArrayObject(String name, AIMesh mesh) {
        this.name = name;
        handle = glGenVertexArrays();
        glBindVertexArray(handle);

        var vertexArrayBuffer = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexArrayBuffer);
        var vertices = mesh.mVertices();
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, AIVector3D.SIZEOF * vertices.remaining(), vertices.address(), GL_STATIC_DRAW_ARB);
        glVertexAttribPointer(POSITION_ATTRIBUTE_LOCATION, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(POSITION_ATTRIBUTE_LOCATION);

        var normalArrayBuffer = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, normalArrayBuffer);
        var normals = mesh.mNormals();
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, AIVector3D.SIZEOF * normals.remaining(), normals.address(), GL_STATIC_DRAW_ARB);
        glVertexAttribPointer(NORMAL_ATTRIBUTE_LOCATION, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(NORMAL_ATTRIBUTE_LOCATION);

        var uvArrayBuffer = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, uvArrayBuffer);
        var uvs = mesh.mTextureCoords(0);
        if (uvs != null) {
            nglBufferDataARB(GL_ARRAY_BUFFER_ARB, AIVector3D.SIZEOF * uvs.remaining(), uvs.address(), GL_STATIC_DRAW_ARB);
            glVertexAttribPointer(UV_ATTRIBUTE_LOCATION, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(UV_ATTRIBUTE_LOCATION);
        }


        var faceCount = mesh.mNumFaces();
        elementCount = faceCount * 3;
        var elementArrayBufferData = BufferUtils.createIntBuffer(elementCount);
        var facesBuffer = mesh.mFaces();
        for (int i = 0; i < faceCount; ++i) {
            var face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("AIFace.mNumIndices() != 3");
            }
            elementArrayBufferData.put(face.mIndices());
        }
        elementArrayBufferData.flip();
        var elementArrayBuffer = glGenBuffersARB();
        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, elementArrayBuffer);
        glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, elementArrayBufferData, GL_STATIC_DRAW_ARB);

        glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
        glBindVertexArray(0);
    }

    public void draw() {
        glBindVertexArray(handle);
        glDrawElements(GL_TRIANGLES, elementCount, GL_UNSIGNED_INT, 0);
    }
}
