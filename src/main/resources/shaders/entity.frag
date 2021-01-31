#version 330

struct Attenuation{
    //衰减是一个二次函数f = c + l*x + e*x^2
    float constant;
    float linear;
    float exponent;
};

struct PointLight{
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation att;
};

struct DirectionalLight{
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct Material{
    //环境光
    vec4 ambient;
    //散射光
    vec4 diffuse;
    //镜面光
    vec4 specular;
    int hasTexture;
    //反射率
    float reflectance;
};

in vec2 exTextureCoordinate;
in vec3 exWorldPos;
in vec3 exVertexNormal;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

void setColour(Material material, vec2 textureCoordinate){
    if (material.hasTexture == 1) {
        ambientC = texture(texture_sampler, textureCoordinate);
        diffuseC = ambientC;
        speculrC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        speculrC = material.specular;
    }
}

vec4 calcLightColour(vec3 lightColour, float lightIntensity, vec3 toLightNormal, vec3 vertexPosition, vec3 vertexNormal) {
    //difuse light
    float difuseFactor = max(dot(toLightNormal, vertexNormal),0.0);
    vec4 diffuseColour = diffuseC * vec4(pointLight.colour, 1.0) * pointLight.intensity * difuseFactor;

    //specular light
    vec3 fromLightNormal = -toLightNormal;
    //经过转换，相机位置永远在(0,0,0)
    vec3 toCameraNormal = normalize(-vertexPosition);
    vec3 reflectLightNormal = normalize(reflect(fromLightNormal, vertexNormal));
    float specularFactor = max(dot(toCameraNormal, reflectLightNormal),0.0);
    specularFactor = pow(specularFactor, specularPower);
    vec4 specularColour = speculrC * vec4(pointLight.colour, 1.0) * material.reflectance * specularFactor;

    return diffuseColour + specularColour;
}

vec4 calcPointLight(PointLight pointLight, vec3 vertexPosition, vec3 vertexNormal) {

    vec3 toLightDirection = pointLight.position - vertexPosition;
    vec3 toLightNormal = normalize(toLightDirection);
    vec4 lightColour = calcLightColour(pointLight.colour, pointLight.intensity, toLightNormal, vertexPosition, vertexNormal);

    //atenuation
    float toLightDistance = length(toLightDirection);
    float attenuation = pointLight.att.constant +
    pointLight.att.linear * toLightDistance +
    pointLight.att.exponent * toLightDistance * toLightDistance;
    return lightColour / attenuation;
}

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 vertexPosition, vec3 vertexNormal) {
    return calcLightColour(directionalLight.colour, directionalLight.intensity, normalize(directionalLight.direction), vertexPosition, vertexNormal);
}

void main()
{
    setColour(material, exTextureCoordinate);
    vec4 componentColour = calcPointLight(pointLight, exWorldPos, exVertexNormal);
    componentColour += calcDirectionalLight(directionalLight, exWorldPos, exVertexNormal);
    fragColor = ambientC * vec4(ambientLight, 1) + componentColour;
}