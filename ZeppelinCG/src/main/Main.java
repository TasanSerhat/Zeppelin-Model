// Rebar BAYRAM             220313013
// Serhat Talha TAŞAN      220313035




package main;

import java.io.File;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    // The window handle
    private long window;

    public void run() {
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
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(800, 600, "Zeppeline", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods)
                -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
            if (key == GLFW_KEY_W && action == GLFW_RELEASE) {
                Global.currentCam.Move(new Vector3f(0, 0, 1));
            }
            if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
                Global.currentCam.Move(new Vector3f(0, 0, -1));
            }
            if (key == GLFW_KEY_A && action == GLFW_RELEASE) {
                Global.currentCam.Move(new Vector3f(1, 0, 0));
            }
            if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
                Global.currentCam.Move(new Vector3f(-1, 0, 0));
            }

            if (key == GLFW_KEY_UP && action == GLFW_RELEASE) {
                Global.currentLight.Move(0, 0, -0.5f);
            }
            if (key == GLFW_KEY_DOWN && action == GLFW_RELEASE) {
                Global.currentLight.Move(0, 0, 0.5f);
            }
            if (key == GLFW_KEY_LEFT && action == GLFW_RELEASE) {
                Global.currentLight.Move(0.5f, 0, 0);
            }
            if (key == GLFW_KEY_RIGHT && action == GLFW_RELEASE) {
                Global.currentLight.Move(-0.5f, 0, 0);
            }
        });
        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.5f, 0.8f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        Camera cam = new Camera(Math.toRadians(75), 80, new Vector3f(0, -1, -5));
        Global.currentCam = cam;

        Light l = new Light("light", "", new Vector3f(0, 2, 4));
        Global.currentLight = l;

        Mesh zeplin = new Mesh("zeplin", "zeplin", new Vector3f(5, 0, 0));
        zeplin.SetLight();

        Mesh cloud = new Mesh("cloudv2", "zeplin", new Vector3f(0.5f, 0.4f, -0.7f));
        cloud.SetLight();

        Mesh cloud2 = new Mesh("cloudv3", "zeplin", new Vector3f(-1.0f, 1.2f, 0));
        cloud.SetLight();

        Mesh cloud3 = new Mesh("cloudv2", "zeplin", new Vector3f(-2.7f, 0, -1.5f));
        cloud.SetLight();

        float value = 0.01f;
        Matrix4f rot = new Matrix4f();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            rot.rotateXYZ(0, value, 0);

            l.lightMesh.Draw();

            cloud.Draw();
            cloud2.Draw();
            cloud3.Draw();

            float rotationAngle = 0.0f;  // Başlangıçta 0 derece

            // Render döngüsünde her frame'de
            rotationAngle += 0.1f;  // Her frame'de 0.1 derece dönecek
            if (rotationAngle >= 360.0f) {
                rotationAngle -= 360.0f;  // Dönüşü sınırlamak için
            }

            zeplin.Rotate(0.1f);
            
            zeplin.Draw();

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
