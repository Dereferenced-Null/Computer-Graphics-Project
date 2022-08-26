package SimulationEngine.ProjectEntities;

import SimulationEngine.Tools.Keyboard;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class ViewFrustrum {

    private Vector3f location = new Vector3f(0,10,10);
    private float pitch;
    private float yaw;
    private float roll;

    //default constructor
    public ViewFrustrum(long window){
        Keyboard keyboard = new Keyboard();
        GLFW.glfwSetKeyCallback(window, keyboard::invoke);
    };

    public void move(){
        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_W)){
            location.z -= 0.2f;
        }
        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_A)){
            location.x -= 0.2f;
        }
        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_S)){
            location.z += 0.2f;
        }
        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_D)){
            location.x += 0.2f;
        }
        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)){
            location.y -=  0.2f;
        }
        if(Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE)){
            location.y +=  0.2f;
        }
    }

    //getters
    public Vector3f getLocation() {
        return location;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
