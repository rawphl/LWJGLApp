package ch.bbcag.lwjgl;

import ch.bbcag.lwjgl.framework.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<Mesh> rover;
    public Camera camera;
    public SkyBox skybox;

    @Override
    public void onInit() throws Exception {
        var skyboxMaterial = new SkyBoxMaterial(new String[]{
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
        var plasticMaterial = new Material("/pbr/plastic");
        var wallMaterial = new Material("/pbr/wall");
        var goldMaterial = new Material("/pbr/curved-wet-cobble");
        rover = AssetLoader.loadObj("/meshes/Rover3.obj").stream().map(vao -> new Mesh(vao, plasticMaterial)).collect(Collectors.toList());

        var suzanne = AssetLoader.loadSingleObj("/meshes/suzanne.obj");
        var sphere = AssetLoader.loadSingleObj("/meshes/sphere.obj");
        var pplane = AssetLoader.loadSingleObj("/meshes/scene1.obj");



        mesh = new Mesh(sphere, plasticMaterial);
        plane = new Mesh(pplane, plasticMaterial);
        suzanneMesh = new Mesh(suzanne, goldMaterial);
        plane.position.y -= 2;
        suzanneMesh.position.z += 2;


        wallMaterial.offsetRepeat.set(2);

        camera = new Camera((float) Math.toRadians(10), (float) width / height, 0.01f, 1000.0f);
        camera.position.set(0, 0, 25);

        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glViewport(0, 0, width, height);

        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            camera.updateProjectionMatrix((float) Math.toRadians(10), (float) w / h, 0.01f, 1000.0f);
            glViewport(0, 0, w, h);
        });

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_RIGHT) {
                camera.yaw += 2f;
            }

            if (key == GLFW_KEY_LEFT) {
                camera.yaw -= 2f;
            }
/*
            if(key == GLFW_KEY_UP) {
                camera.pitch += 2f;
            }

            if(key == GLFW_KEY_DOWN) {
                camera.pitch -= 2f;
            }
*/
            if (key == GLFW_KEY_S) {
                camera.target.x += 1.0f;
                camera.target.z += 1.0f;
            }

            if (key == GLFW_KEY_W) {
                camera.target.x -= 1.0f;
                camera.target.z -= 1.0f;
            }

            if (key == GLFW_KEY_D) {
                camera.target.x += 1.0f;
                camera.target.z -= 1.0f;
            }

            if (key == GLFW_KEY_A) {
                camera.target.x -= 1.0f;
                camera.target.z += 1.0f;
            }
        });

        glfwSetScrollCallback(window, (window, ox, oy) -> {
            camera.zoom += 3.0f * oy;
        });

    }

    @Override
    public void onUpdate(float t, float dt) {
        camera.update(t, dt);
        suzanneMesh.rotation.rotateLocalY(1.0f * dt);
        suzanneMesh.needsUpdate = true;
        setTitle("Fps: " + timer.getFPS());
    }

    @Override
    public void onRender() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        renderer.renderSkyBox(skybox, camera);
        /*
        for(var m : rover) {
            renderer.renderMesh(m, camera);
        }*/
        renderer.renderMesh(mesh, camera);
        //renderer.renderMesh(suzanneMesh, camera);
        renderer.renderMesh(plane, camera);
    }
}
