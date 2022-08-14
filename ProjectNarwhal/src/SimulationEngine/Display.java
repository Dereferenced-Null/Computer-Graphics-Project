package SimulationEngine;

import SimulationEngine.Models.Model;
import SimulationEngine.Models.TexturedModel;
import SimulationEngine.ProjectEntities.ModeledEntity;
import SimulationEngine.Shaders.StaticShader;
import SimulationEngine.Textures.ModelTexture;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Display {

    //stores the window handle
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
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(1990, 1080, "Secret Project Narwhal", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
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
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        ModelLoader loader = new ModelLoader();
        StaticShader shader = new StaticShader();
        Renderer render = new Renderer(shader);

        float[] vertices = {
                -0.5f,  0.5f, 0.0f,
                -0.5f, -0.5f, 1.0f,
                0.5f, -0.5f, 1.0f,
                0.5f,  0.5f, 0.0f,
        };

        int[] indicies = {
                0,1,3,
                3,1,2
        };

        //reminder: origin is the top left hand corner, not the bottom left for textures
        float[] textureCoords = {
                0,0,
                0,1,
                1,1,
                1,0
        };

        Model model = loader.loadToVAO(vertices,textureCoords, indicies);
        ModelTexture texture = new ModelTexture(loader.loadTexture("Test"));
        TexturedModel tModel = new TexturedModel(model, texture);
        ModeledEntity entity = new ModeledEntity(tModel, new Vector3f(0,0,-1), 0, 0, 0, 1);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            entity.increasePosition(0,0.001f,-0.002f);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer
            shader.start(); //start the shaders

            //game logic start
            render.render(entity, shader);

            shader.stop(); //stop the shaders
            glfwSwapBuffers(window); // swap the buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

        shader.cleanUp();
    }

    public long getWindow() {
        return window;
    }
}
