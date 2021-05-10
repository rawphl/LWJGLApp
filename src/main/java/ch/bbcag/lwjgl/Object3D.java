package ch.bbcag.lwjgl;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Object3D {
    protected static Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
    public final Matrix4f modelMatrix = new Matrix4f().identity();
    public final Matrix3f normalMatrix = new Matrix3f().identity();
    public final Quaternionf rotation = new Quaternionf().identity();
    public final Vector3f position = new Vector3f();
    public final Vector3f scale = new Vector3f(1, 1, 1);

    public void updateMatrices() {
        modelMatrix.identity().translationRotateScale(position, rotation, scale);
        normalMatrix.set(modelMatrix);
        normalMatrix.invert().transpose();
    }

}
