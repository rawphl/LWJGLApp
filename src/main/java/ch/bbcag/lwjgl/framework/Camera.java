package ch.bbcag.lwjgl.framework;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends Object3D {
    public final Matrix4f projectionMatrix = new Matrix4f();
    public final Matrix4f viewMatrix = new Matrix4f();
    public float zoom = 25;
    public Vector3f offset = new Vector3f(zoom, zoom, zoom);
    public Vector3f target = new Vector3f(0, 0, 0);

    public Vector3f up = new Vector3f(0, 1, 0);
    public Vector3f forward = new Vector3f(0, 0, 1);
    public Vector3f right = new Vector3f(1, 0, 0);
    public float yaw = 0;
    public float pitch = 0;
    public Quaternionf yawQuat = new Quaternionf().identity();
    public Quaternionf pitchQuat = new Quaternionf().identity();

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

    public void update(float t, float dt) {
        yawQuat.setAngleAxis(yaw * dt, 0, 1, 0);
        yawQuat.transform(offset.normalize());
        yawQuat.transform(up);

        forward.set(offset).negate().normalize();
        right.set(up).cross(forward).normalize();

        pitchQuat.setAngleAxis(pitch * dt, right.x, right.y, right.z);
        pitchQuat.transform(offset);
        pitchQuat.transform(up);

        viewMatrix.identity().lookAt(offset.mul(zoom), target, up);
        modelMatrix.set(viewMatrix).invert();
        yaw = 0;
        pitch = 0;
    }
}