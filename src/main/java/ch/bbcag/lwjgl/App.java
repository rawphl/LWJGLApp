package ch.bbcag.lwjgl;

import ch.bbcag.lwjgl.opengl.Material;
import ch.bbcag.lwjgl.opengl.Shader;
import ch.bbcag.lwjgl.opengl.Texture;
import de.matthiasmann.twl.utils.PNGDecoder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBVertexShader.glGetAttribLocationARB;
import static org.lwjgl.opengl.ARBVertexShader.glVertexAttribPointerARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL20.*;

public class App {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    public static int id;
    private long window;
    public int width;
    public int height;
    private Camera camera;
    private int dist = 5;
    private float[] pos = new float[3];
    float yaw = 0;
    float pitch = 0;

    public void run() throws Exception {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 16);

        window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);
            width = pWidth.get(0);
            height = pHeight.get(0);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
        ImGui.createContext();

        imGuiGlfw.init(window, true);
        imGuiGl3.init();


        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            camera.updateProjectionMatrix((float) Math.toRadians(10), (float) w / h, 0.01f, 1000.0f);
            glViewport(0, 0, w, h);
        });

        glfwSetKeyCallback(window,(window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);

            if ( key == GLFW_KEY_RIGHT ) {
                yaw = -0.001f;
            }

            if ( key == GLFW_KEY_LEFT ) {
                yaw = 0.001f;
            }

            if ( key == GLFW_KEY_UP ) {
                pitch = 0.001f;
            }

            if ( key == GLFW_KEY_DOWN ) {
                pitch = -0.001f;
            }
        });

        glfwSetScrollCallback(window, (window, ox, oy) -> {
            dist += oy* 3;
            camera.offset.add(dist, dist, dist);
        });

    }

    private void loop() throws Exception {
        String glVersion = glGetString(GL_VERSION);
        System.out.println(glVersion);

        var wallMaterial = new Material("physicalmaterial",
                "/pbr/wall/albedo.png",
                "/pbr/wall/normal.png",
                "/pbr/wall/roughness.png",
                "/pbr/wall/ao.png",
                "/pbr/wall/metallic.png"
        );

        var grassMaterial = new Material("physicalmaterial",
                "/pbr/grass/albedo.png",
                "/pbr/grass/normal.png",
                "/pbr/grass/roughness.png",
                "/pbr/grass/ao.png",
                "/pbr/grass/metallic.png"
        );

        wallMaterial.offsetRepeat.x = 64;

        var plasticMaterial = new Material("physicalmaterial",
                "/pbr/plastic/albedo.png",
                "/pbr/plastic/normal.png",
                "/pbr/plastic/roughness.png",
                "/pbr/plastic/ao.png",
                "/pbr/plastic/metallic.png"
        );

        var rustedIronMaterial = new Material("physicalmaterial",
                "/pbr/rusted_iron/albedo.png",
                "/pbr/rusted_iron/normal.png",
                "/pbr/rusted_iron/roughness.png",
                "/pbr/rusted_iron/ao.png",
                "/pbr/rusted_iron/metallic.png"
        );
        var scene = Mesh.fromObj("src/main/resources/meshes/scene1.obj", wallMaterial);


        var suzanne = Mesh.fromObj("src/main/resources/meshes/sphere.obj", plasticMaterial).get(0);
        var rock = Mesh.fromObj("src/main/resources/meshes/rock.obj", rustedIronMaterial).get(0);
        suzanne.position.y = 1;
        suzanne.position.z = 2;
        suzanne.updateMatrices();
        camera = new Camera((float) Math.toRadians(10), (float) width / height, 0.01f, 1000.0f);

        camera.offset.set(25, 25, 25);
        camera.update(0, 0);

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        float t = 0.0f;
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            imGuiGlfw.newFrame();
            ImGui.newFrame();

            for(var mesh : scene) {
                mesh.updateMatrices();
                mesh.draw(camera);
            }
            suzanne.position.x = (float)Math.cos(t) * 2.0f;
            suzanne.position.z = (float)Math.sin(t) * 2.0f;
            suzanne.updateMatrices();
            suzanne.draw(camera);
            t += 0.01;

            rock.rotation.rotateLocalY(0.01f);
            rock.updateMatrices();
            rock.draw(camera);
            if(ImGui.inputFloat3("pos", pos)) {
                System.out.println(pos[0]);
            }

            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = GLFW.glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(backupWindowPtr);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }


    public static void main(String[] args) throws Exception {
        var app = new App();
        app.run();
    }
}
