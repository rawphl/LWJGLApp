package ch.bbcag.lwjgl;


public class App {
    /*
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


            if ( key == GLFW_KEY_R) {
                camera.update(0.01f, 0);
            }
            if ( key == GLFW_KEY_DOWN ) {
                camera.target.x += 1;
                camera.target.z += 1;
                camera.position.set(camera.target).add(camera.offset);
                camera.updateMatrices();
            }

            if ( key == GLFW_KEY_UP) {
                camera.target.x -= 1;
                camera.target.z -= 1;
                camera.position.set(camera.target).add(camera.offset);
                camera.updateMatrices();
            }

            if ( key == GLFW_KEY_LEFT ) {
                camera.target.x -= 1;
                camera.target.z += 1;
                camera.position.set(camera.target).add(camera.offset);
                camera.updateMatrices();
            }

            if ( key == GLFW_KEY_RIGHT) {
                camera.target.x += 1;
                camera.target.z -= 1;
                camera.position.set(camera.target).add(camera.offset);
                camera.updateMatrices();
            }
        });

        glfwSetScrollCallback(window, (window, ox, oy) -> {
            dist += oy* 3;
            if(oy > 0) {
                camera.zoom += oy;
                camera.position.set(camera.target).add(camera.offset.add(camera.zoom, camera.zoom, camera.zoom));

            } else {
                camera.zoom -= oy;
                camera.position.set(camera.target).add(camera.offset.sub(camera.zoom, camera.zoom, camera.zoom));

            }

            camera.updateMatrices();
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
        var scene = Mesh.fromObj("src/main/resources/meshes/scene1.obj", grassMaterial);


        var suzanne = Mesh.fromObj("src/main/resources/meshes/suzanne.obj", plasticMaterial).get(0);
        var rock = Mesh.fromObj("src/main/resources/meshes/rock.obj", rustedIronMaterial).get(0);
        suzanne.position.y = 1;
        suzanne.position.z = 2;
        suzanne.updateMatrices();

        pos[0] = suzanne.position.x;
        pos[1] = suzanne.position.y;
        pos[2] = suzanne.position.z;
        camera = new Camera((float) Math.toRadians(10), (float) width / height, 0.01f, 1000.0f);
        camera.update(0, 0);
        camera.updateMatrices();
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
            suzanne.rotation.rotateLocalY(0.01f);
            suzanne.rotation.rotateLocalX(0.01f);
            suzanne.updateMatrices();
            suzanne.draw(camera);
            t += 0.01;

            rock.rotation.rotateLocalY(0.01f);
            rock.updateMatrices();
            rock.draw(camera);
            pos[0] = suzanne.position.x;
            pos[1] = suzanne.position.y;
            pos[2] = suzanne.position.z;

            ImGui.text("Suzanne position:");

            if(ImGui.inputFloat3("pos", pos)) {

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
    */
}
