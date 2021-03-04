#version 330

const int MAX_POINT_LIGHT_NUM = 5;

struct Attenuation{
//衰减是一个二次函数f = c + l*x + e*x^2
    float constant;
    float linear;
    float exponent;
};

struct DirectionalLight{
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct PointLight{
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation att;
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

struct Fog {
    int activeFog;
    vec3 colour;
    float density;
    float visibility;
};

in vec2 exTextureCoordinate;
in vec3 exWorldPos;
in vec3 exVertexNormal;

out vec4 fragColor;

uniform DirectionalLight directionalLight;
uniform sampler2D texture_sampler;
uniform PointLight pointLights[MAX_POINT_LIGHT_NUM];
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform Fog fog;

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
    float difuseFactor = max(dot(vertexNormal,toLightNormal),0.0);
    vec4 diffuseColour = diffuseC * vec4(lightColour, 1.0) * lightIntensity * difuseFactor * 0.3;

    //specular light
    vec3 fromLightNormal = -toLightNormal;
    //经过转换，相机位置永远在(0,0,0)
    vec3 toCameraNormal = normalize(-vertexPosition);
    vec3 reflectLightNormal = normalize(reflect(fromLightNormal, vertexNormal));
    float specularFactor = max(dot(toCameraNormal, reflectLightNormal),0.0);
    specularFactor = pow(specularFactor, specularPower);
    vec4 specularColour = speculrC * lightIntensity * vec4(lightColour, 1.0) * material.reflectance * specularFactor;

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

vec4 calcFog(vec3 position, vec4 fragColor, Fog fog) {
    //计算雾时没有考虑环境光照，因此就算环境光为0时 雾依旧是发光的
    //    vec3 fogColor = fog.colour * (ambientLight + directionalLight.colour * directionalLight.intensity);
    vec3 fogColor = fog.colour;
    float distance = length(position) / fog.visibility;
    float fogFactor = 1.0 / exp(distance * distance * fog.density * fog.density);

    //    vec4 resultColor = fragColor * fogFactor + vec4(fogColor, 1.0) * (1.0 - fogFactor);
    vec3 resultColor = mix(fogColor, fragColor.rgb, fogFactor);
    return vec4(resultColor, fragColor.a);
}

void main() {
    setColour(material, exTextureCoordinate);
    vec4 componentColour = calcDirectionalLight(directionalLight, exWorldPos, exVertexNormal);
//    vec4 componentColour = vec4(0,0,0,0);

    for(int i = 0; i < MAX_POINT_LIGHT_NUM; i++) {
        if(pointLights[i].intensity > 0) {
            componentColour += calcPointLight(pointLights[i], exWorldPos, exVertexNormal);
        }
    }

    fragColor = ambientC * vec4(ambientLight, 1) + componentColour;

    if (fog.activeFog == 1) {
        fragColor = calcFog(exWorldPos, fragColor, fog);
    }
    fragColor.a = 0.9;
}
