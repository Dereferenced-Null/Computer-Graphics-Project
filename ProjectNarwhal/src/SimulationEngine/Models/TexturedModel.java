package SimulationEngine.Models;

import SimulationEngine.Textures.ModelTexture;

public class TexturedModel {

    private Model model;
    private ModelTexture texture;
    private Material material;

    //Constructor, stores model and texture
    public TexturedModel(Model model, ModelTexture texture){
        this.model = model;
        this.texture = texture;
    }

    //getters
    public Model getModel() {
        return model;
    }

    public ModelTexture getTexture() {
        return texture;
    }

    public void setMaterial(Material material){
        this.material = material;
    }

    public Material getMaterial(){
        return material;
    }
}
