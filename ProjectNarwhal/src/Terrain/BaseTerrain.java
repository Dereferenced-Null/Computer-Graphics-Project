package Terrain;

import SimulationEngine.Loaders.ModelLoader;
import SimulationEngine.Models.Material;
import SimulationEngine.Models.Model;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BaseTerrain {

    private static final float SIZE = 4096;
    private static final float MAX_HEIGHT = 40;
    private static final float MAX_PIXEL_COLOUR = 256*256*256;

    private float x;
    private float z;
    private Model model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;
    private String heightMap;

    public BaseTerrain(float gridX, float gridZ, ModelLoader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap){
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.heightMap = heightMap;
        this.model = generateTerrain(loader, heightMap);
    }

    private Model generateTerrain(ModelLoader loader, String heightMap){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("ProjectResources/" + heightMap + ".png"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
        int VERTEX_COUNT = image.getHeight();

        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = ((float)j/((float)VERTEX_COUNT - 1) * SIZE) + 2048;
                vertices[vertexPointer*3+1] = getHeight(j,i,image);
                vertices[vertexPointer*3+2] = ((float)i/((float)VERTEX_COUNT - 1) * SIZE) + 2048;
                Vector3f normal = calculateNormal(j,i,image);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public Model getModel() {
        return model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public float getHeight(int x, int z, BufferedImage image){
        if(x< 0 || x>= image.getHeight() || z<0 || z>=image.getHeight()){
            return 0;
        }
        float height = image.getRGB(x,z);
        height += MAX_PIXEL_COLOUR / 2f;
        height /= MAX_PIXEL_COLOUR/ 2f;
        height *= MAX_HEIGHT;
        return height;
    }

    public BufferedImage getHeightMap(){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("ProjectResources/" + heightMap + ".png"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }

    private Vector3f calculateNormal(int x, int y, BufferedImage image){
        float heightL = getHeight(x-1,y,image);
        float heightR = getHeight(x+1,y,image);
        float heightU = getHeight(x,y+1,image);
        float heightD = getHeight(x,y-1,image);
        Vector3f normal = new Vector3f(heightL-heightR, 2f , heightD-heightU);
        normal.normalize();
        return normal;
    }
}
