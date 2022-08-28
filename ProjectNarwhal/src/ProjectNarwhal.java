import SimulationEngine.DisplayEngine.Display;
import SimulationEngine.DisplayEngine.RenderController;
import SimulationEngine.Loaders.AssimpLoader;
import SimulationEngine.Loaders.ModelLoader;
import SimulationEngine.Models.ModelTexture;
import SimulationEngine.ProjectEntities.LightSource;
import SimulationEngine.ProjectEntities.ModeledEntity;
import SimulationEngine.ProjectEntities.ViewFrustrum;
import SimulationEngine.Shaders.StaticShader;
import Terrain.BaseTerrain;
import Terrain.TerrainTexture;
import Terrain.TerrainTexturePack;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        ViewFrustrum camera = new ViewFrustrum(window);
        ModelLoader loader = new ModelLoader();
        StaticShader shader = new StaticShader();
        RenderController renderer = new RenderController();

        ModeledEntity[] models = AssimpLoader.loadModel("ProjectResources/Knife/knife.obj", loader, "Knife/Textures/Albedo");

        models[0].setPosition(new Vector3f(0, 10, 0));

        Random rand = new Random();
        List<ModeledEntity> entities = new ArrayList<>();

        for(int i=0; i<100; i++){
            float x = rand.nextFloat()* 100 - 50;
            float y = rand.nextFloat()* 100 - 50;
            float z = rand.nextFloat()* 100 - 50;
            ModeledEntity newEntity = new ModeledEntity(models[0].getModel());
            newEntity.setMaterial(models[0].getMaterial());
            newEntity.setPosition(new Vector3f(x,y,z));
            entities.add(newEntity);
        }

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("seabed"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("coral"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("sand"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("stones"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));


        BaseTerrain terrain = new BaseTerrain(0,0,loader, texturePack, blendMap);
        BaseTerrain terrain2 = new BaseTerrain(0,-1,loader, texturePack, blendMap);
        BaseTerrain terrain3 = new BaseTerrain(-1,-1,loader, texturePack, blendMap);
        BaseTerrain terrain4 = new BaseTerrain(-1,0,loader, texturePack, blendMap);








        LightSource light = new LightSource(new Vector3f(20000,20000,2000), new Vector3f(1.5f,1.5f,1.5f));

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            camera.move();


            for(ModeledEntity model: entities){
                renderer.processEntity(model);
                model.increaseRotation(0f,0.2f,0f);
            }
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            renderer.processTerrain(terrain3);
            renderer.processTerrain(terrain4);

            renderer.render(light, camera);


            glfwSwapBuffers(window); // swap the buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
        renderer.cleanUp();
        shader.cleanUp();
    }

    public long getWindow() {
        return window;
    }

}