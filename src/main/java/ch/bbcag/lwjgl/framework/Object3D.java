package ch.bbcag.lwjgl.framework;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Object3D {
    protected static Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
    public final Matrix4f modelMatrix = new Matrix4f().identity();
    public final Matrix3f normalMatrix = new Matrix3f().identity();
    public final Quaternionf rotation = new Quaternionf().identity();
    public final Vector3f position = new Vector3f();
    public final Vector3f scale = new Vector3f(1, 1, 1);
    public Object3D parent;
    public List<Object3D> children = new ArrayList<Object3D>();
    public boolean needsUpdate = true;

    public void traverse(Function<Object3D, Void> fn) {
        fn.apply(this);
        children.forEach(fn::apply);
    }

    public void updateMatrices() {
        if(!needsUpdate) return;
        modelMatrix.identity();

        if(parent != null) {
            parent.modelMatrix.mul(modelMatrix);
        }

        modelMatrix.translationRotateScale(position, rotation, scale);
        normalMatrix.set(modelMatrix);
        normalMatrix.invert().transpose();
        needsUpdate = false;
    }
}
