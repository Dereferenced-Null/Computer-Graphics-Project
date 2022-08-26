package SimulationEngine.DisplayEngine;

import SimulationEngine.ProjectEntities.ModeledEntity;
import SimulationEngine.Shaders.TerrainShader;
import SimulationEngine.Tools.ProjectMaths;
import Terrain.BaseTerrain;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class TerrainRenderer {
    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix){
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(List<BaseTerrain> terrains){
        for(BaseTerrain terrain: terrains){
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTerrain();
        }
    }

    private void prepareTerrain(BaseTerrain terrain){
        GL30.glBindVertexArray(terrain.getModel().getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        shader.loadShineVariables(terrain.getMaterial().getShineDamper(), terrain.getMaterial().getReflectance());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getTexture().getID());
    }

    private void unbindTerrain(){
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(BaseTerrain terrain){
        Matrix4f transformationMatrix = ProjectMaths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()),0,0,0,1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
