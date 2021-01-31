package gui;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sim.SimGUI;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @Author: gq
 * @Date: 2021/1/20 15:41
 */
public class Window {

    private final Logger logger = LoggerFactory.getLogger(Window.class);

    private final float FOV = (float) Math.toRadians(60);
    private final float Z_NEAR = 0.01f;
    private final float Z_FAR = 1000f;
    private final Matrix4f projectionMatrix = new Matrix4f();

    private long windowID;

    private final String title;
    private int width;
    private int height;
    private boolean vSync;
    private boolean resize;

    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.resize = false;
    }
    public void init(){
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        windowID = glfwCreateWindow(width, height, title, NULL, NULL);
        if ( windowID == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowID, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resize = true;
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowID);

        if (vSync) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(windowID);

        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.2f, 0.2f, 0.8f, 0.0f);
//        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        //绘制3D对象时，远处的像素比近处的像素先绘制
        glEnable(GL_DEPTH_TEST);

        // Support for transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //不渲染反面
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public void clean(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(){
        glfwSwapBuffers(windowID);
        glfwPollEvents();
    }

    public void cleanup(){
        glfwFreeCallbacks(windowID);
        glfwDestroyWindow(windowID);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public boolean isClosed(){
        return glfwWindowShouldClose(windowID);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindowID() {
        return windowID;
    }

    public Matrix4f updateProjectionMatrix() {
        projectionMatrix.setPerspective(FOV, (float) width / (float)height, Z_NEAR, Z_FAR);
        return projectionMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public boolean isResize(boolean clean) {
        boolean result = resize;
        if (clean) {
            resize = false;
        }
        return result;
    }

    public void updateViewPoint(){
        glViewport(0, 0, width, height);
    }
}
