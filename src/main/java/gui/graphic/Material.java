package gui.graphic;

import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2020/12/26 17:47
 * @Version 1.0
 **/
public class Material {

    private final Logger logger = LoggerFactory.getLogger(Material.class);

    private static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    //环境光
    private Vector4f ambient;
    //散射光
    private Vector4f diffuse;
    //镜面光
    private Vector4f specular;
    //反射率
    private float reflectance;

    private Texture texture;

    public Material() {
        this(0);
    }

    public Material(float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, reflectance, null);
    }
    public Material(float reflectance, Texture texture) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, reflectance, texture);
    }
    public Material(Texture texture) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, 0, texture);
    }

    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, float reflectance, Texture texture) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.reflectance = reflectance;
        this.texture = texture;
    }

    public Vector4f getAmbient() {
        return ambient;
    }

    public void setAmbient(Vector4f ambient) {
        this.ambient = ambient;
    }

    public Vector4f getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Vector4f diffuse) {
        this.diffuse = diffuse;
    }

    public Vector4f getSpecular() {
        return specular;
    }

    public void setSpecular(Vector4f specular) {
        this.specular = specular;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean isTextured() {
        return this.texture != null;
    }
}
