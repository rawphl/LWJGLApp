package ch.bbcag.lwjgl;

import ch.bbcag.lwjgl.framework.*;
import ch.bbcag.lwjgl.framework.App;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TestApp extends App {
    public static void main(String[] args) throws Exception {
        var app = new TestApp();
        app.run();
    }

    public TestApp() {
        super("TestApp", 800, 600);
    }

    public Mesh mesh;
    public Mesh suzanneMesh;
    public Mesh plane;
    public Camera camera;
    public SkyBox skybox;

    @Override
    public void onInit() throws Exception {
        var skyboxMaterial = new SkyBoxMaterial(new String[] {
                "/skybox/right.jpg",
                "/skybox/left.jpg",
                "/skybox/top.jpg",
                "/skybox/bottom.jpg",
                "/skybox/front.jpg",
                "/skybox/back.jpg",
        });
        skybox = new SkyBox(skyboxMaterial);
        skybox.scale.set(100, 100, 100);
        skybox.needsUpdate = true;

        var suzanne = AssetLoader.loadSingleObj("/meshes/suzanne.obj");
        var sphere = AssetLoader.loadSingleObj("/meshes/sphere.obj");
        var pplane = AssetLoader.loadSingleObj("/meshes/scene1.obj");
        var plasticMaterial = new Material("/pbr/plastic");
        var wallMaterial = new Material("/pbr/wall");
        mesh = new Mesh(sphere, plasticMaterial);
        plane = new Mesh(pplane, wallMaterial);
        suzanneMesh = new Mesh(suzanne, plasticMaterial);
        plane.position.y -= 2;
        suzanneMesh.position.x += 2;
        camera = new Camera((float) Math.toRadians(10), (float) width / height, 0.01f, 1000.0f);
        camera.position.set(15, 15, 15);
        camera.lookAt(new Vector3f());

        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glViewport(0, 0, width, height);

        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            camera.updateProjectionMatrix((float) Math.toRadians(10), (float) w / h, 0.01f, 1000.0f);
            glViewport(0, 0, w, h);
        });

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {

        });

        glfwSetScrollCallback(window, (window, ox, oy) -> {
            camera.position.add(new Vector3f((float)oy, (float)oy, (float)oy));
            camera.lookAt(new Vector3f());
        });

    }

    @Override
    public void onUpdate(float t, float dt) {
    }

    @Override
    public void onRender() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderer.renderSkyBox(skybox, camera);
        renderer.renderMesh(mesh, camera);

        suzanneMesh.rotation.rotateLocalY(0.01f);
        suzanneMesh.needsUpdate = true;

        renderer.renderMesh(suzanneMesh, camera);

        renderer.renderMesh(plane, camera);
    }
}
