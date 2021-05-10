package ch.bbcag.lwjgl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera extends Object3D {
    public final Matrix4f projectionMatrix = new Matrix4f();
    public final Matrix4f viewMatrix = new Matrix4f();
    public Vector3f target = new Vector3f();

    public Camera(float fov, float aspect, float near, float far) {
        projectionMatrix.perspective(fov, aspect, near, far);
    }

    public void updateMatrices() {
        super.updateMatrices();
        viewMatrix.set(modelMatrix).invert();
    }

    public void updateProjectionMatrix(float fov, float aspect, float near, float far) {
        projectionMatrix.identity().perspective(fov, aspect, near, far);
    }

    public void lookAt(Vector3f target) {
        viewMatrix.lookAt(position, target, UP);
        modelMatrix.set(viewMatrix).invert();
        modelMatrix.getTranslation(position);
        modelMatrix.getNormalizedRotation(rotation.normalize());
        modelMatrix.getScale(scale);
    }

    public void moveRight() {
        position.x += 0.01;
        target.x += 0.01;
        lookAt(target);
    }
}