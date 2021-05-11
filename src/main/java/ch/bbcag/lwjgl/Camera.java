package ch.bbcag.lwjgl;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends Object3D {
    public final Matrix4f projectionMatrix = new Matrix4f();
    public final Matrix4f viewMatrix = new Matrix4f();
    public float zoom = 15;
    public Vector3f offset = new Vector3f(zoom, zoom, zoom);
    public Vector3f target = new Vector3f(0, 0, 0);

    public Vector3f up = new Vector3f(0, 1, 0);
    public Vector3f forward = new Vector3f(0, 0, 1);
    public Vector3f right = new Vector3f(1, 0, 0);

    public Quaternionf yawQuat = new Quaternionf();
    public Quaternionf pitchQuat = new Quaternionf();



    public Camera(float fov, float aspect, float near, float far) {
        position.set(target).add(offset);
        projectionMatrix.perspective(fov, aspect, near, far);
    }

    public void updateMatrices() {
        super.updateMatrices();
        viewMatrix.set(modelMatrix).invert();
    }

    public void updateProjectionMatrix(float fov, float aspect, float near, float far) {
        projectionMatrix.identity().perspective(fov, aspect, near, far);
    }

    public void update(float yaw, float pitch) {
        viewMatrix.rotateY(yaw).rotateX(pitch).lookAt(position, target, up);
        modelMatrix.set(viewMatrix).invert();
        modelMatrix.getTranslation(position);
        modelMatrix.getNormalizedRotation(rotation.normalize());
        modelMatrix.getScale(scale);
    }

    public void lookAt(Vector3f target) {
        viewMatrix.lookAt(position, target, UP);
        modelMatrix.set(viewMatrix).invert();
        modelMatrix.getTranslation(position);
        modelMatrix.getNormalizedRotation(rotation.normalize());
        modelMatrix.getScale(scale);
    }
}