package gui.graphic.light;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2020/12/26 17:47
 * @Version 1.0
 **/
public class PointLight {

    private final Logger logger = LoggerFactory.getLogger(PointLight.class);

    private Vector3f colour;
    private Vector3f position;
    private float intensity;
    private Attenuation att;

    public PointLight(PointLight pointLight) {
        this(new Vector3f(pointLight.getColour()), new Vector3f(pointLight.getPosition()),
                pointLight.getIntensity(), pointLight.getAtt());
    }

    public PointLight(Vector3f colour, Vector3f position, float intensity, Attenuation att) {
        this.colour = colour;
        this.position = position;
        this.intensity = intensity;
        this.att = att;
    }

    public PointLight(Vector3f colour, Vector3f position, float intensity) {
        this.colour = colour;
        this.position = position;
        this.intensity = intensity;
        //光照不会随距离损失
        this.att = new Attenuation(1,0,0);
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Attenuation getAtt() {
        return att;
    }

    public void setAtt(Attenuation att) {
        this.att = att;
    }

    public static class Attenuation{
        private float constant;
        private float linear;
        private float exponent;

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        public float getConstant() {
            return constant;
        }

        public void setConstant(float constant) {
            this.constant = constant;
        }

        public float getLinear() {
            return linear;
        }

        public void setLinear(float linear) {
            this.linear = linear;
        }

        public float getExponent() {
            return exponent;
        }

        public void setExponent(float exponent) {
            this.exponent = exponent;
        }
    }
}
