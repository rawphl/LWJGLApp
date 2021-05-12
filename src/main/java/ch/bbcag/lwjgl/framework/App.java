package ch.bbcag.lwjgl.framework;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class App {
    private final static Logger LOGGER = Logger.getLogger(AssetLoader.class.getName());

    protected String title;
    protected int width;
    protected int height;
    protected long window;
    protected Renderer renderer;
    protected float t = 0;
    protected float dt = 0.01f;

    public App(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.title = title;
        this.renderer = new Renderer(width, height);
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        //glfwWindowHint(GLFW_SAMPLES, 16);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            width = pWidth.get(0);
            height = pHeight.get(0);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
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
    }

    private void initInput() {
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });
    }

    public void onInit() throws Exception {
    }

    public void onTerminate() {
    }

    public abstract void onUpdate(float t, float dt);

    public abstract void onRender();

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            t += dt;
            onUpdate(t, dt);
            onRender();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void terminate() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void run() throws Exception {
        LOGGER.info("init()");
        init();
        LOGGER.info("initInput()");
        initInput();
        LOGGER.info("onInit()");
        onInit();
        LOGGER.info("loop()");
        loop();
        onTerminate();
        terminate();
    }
}

