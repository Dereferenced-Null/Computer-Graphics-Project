import Scene.Scene;
import SimulationEngine.DisplayEngine.Display;
import SimulationEngine.DisplayEngine.RenderController;
import SimulationEngine.Loaders.ModelLoader;
//import SimulationEngine.PostProcessing.Fbo;
import SimulationEngine.PostProcessing.Fbo;
import SimulationEngine.PostProcessing.PostProcessing;
import SimulationEngine.ProjectEntities.ViewFrustrum;
import SimulationEngine.BaseShaders.StaticShader;
import Water.WaterFrameBuffers;
import Water.WaterSurface;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.glfw.GLFW.*;

public class ProjectNarwhal {

    //stores the window handle
    private long window;

    public static void main(String[] args) {
        ProjectNarwhal simulation = new ProjectNarwhal();
        simulation.start();
    }

    public void start(){
        Display disp = new Display();
        window = disp.run();

        //main game loop
        loop();
    }

    private void loop() {
        //Very important following line
        GL.createCapabilities();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        ViewFrustrum camera = new ViewFrustrum(window, new Vector3f(1f,1f,1f));
        ModelLoader loader = new ModelLoader();
        StaticShader shader = new StaticShader();
        WaterFrameBuffers fbos = new WaterFrameBuffers();
        Fbo multisampleFbo = new Fbo();
        Fbo outputFbo = new Fbo(Fbo.DEPTH_RENDER_BUFFER);
        PostProcessing.init(loader);
        RenderController renderer = new RenderController(loader, camera, fbos);

        Scene scene = new Scene(loader, fbos, renderer);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            renderer.renderShadowMap(scene.getEntities(), scene.getSun());
            camera.move();
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            //Reflections
            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getLocation().y - scene.getWaters().get(0).getY());
            camera.getLocation().y -= distance;
            camera.invertPitch();
            renderer.renderScene(scene.getEntities(), scene.getTerrains(), scene.getLights(), camera, new Vector4f(0, -1 , 0 , scene.getWaters().get(0).getY() - 25));
            camera.getLocation().y += distance;
            camera.invertPitch();
            fbos.unbindCurrentFrameBuffer();

            //Refractions
            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(scene.getEntities(), scene.getTerrains(), scene.getLights(), camera, new Vector4f(0, 1 , 0 , -scene.getWaters().get(0).getY() + 5));
            fbos.unbindCurrentFrameBuffer();

            for(WaterSurface water:scene.getWaters()){
                renderer.processWater(water);
            }
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            fbos.unbindCurrentFrameBuffer();

            //Post Processing
            multisampleFbo.bindFrameBuffer();
            renderer.renderScene(scene.getEntities(), scene.getTerrains(), scene.getLights(), camera, new Vector4f(0, -1 , 0 , 130));
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(outputFbo);
            PostProcessing.doPostProcessing(outputFbo.getColourTexture());

            glfwSwapBuffers(window); // swap the buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
        PostProcessing.cleanUp();
        outputFbo.cleanUp();
        multisampleFbo.cleanUp();
        loader.cleanUp();
        fbos.cleanUp();
        renderer.cleanUp();
        shader.cleanUp();
    }

    public long getWindow() {
        return window;
    }

}
