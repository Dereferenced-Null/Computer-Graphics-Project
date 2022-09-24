#version 400 core

in vec2 TextureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[10];
in vec3 toCameraVector;
in float visibility;
in float NumberOfLights;
in vec4 clipSpace;

uniform sampler2D textureSampler;
uniform vec3 lightColor[10];
uniform vec3 attenuation[10];
uniform float shineDamper;
uniform float reflectance;
uniform vec3 waterColor;
uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D baseTexture;

out vec4 outColor;

void main()
    {
        vec3 unitNormal = normalize(surfaceNormal);
        vec3 totalDiffuse = vec3(0.0);
        vec3 totalSpecular = vec3(0.0);
        vec3 unitVectorToCamera = normalize(toCameraVector);
        for(int i = 0 ; i < NumberOfLights; i++) {
            float distance = max(length(toLightVector[i]), 1.0);
            float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
            if (attFactor == 0) {attFactor = 1;}
            vec3 unitLightVector = normalize(toLightVector[i]);
            float nDot1 = dot(unitNormal, unitLightVector);
            float brightness = max(nDot1, 0.0);
            vec3 lightDirection = -unitLightVector;
            vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
            totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attFactor;
            float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
            specularFactor = max(specularFactor, 0.0);
            float dampedFactor = pow(specularFactor, shineDamper);
            totalSpecular = totalSpecular + (dampedFactor * reflectance * lightColor[i]) / attFactor;
        }
        totalDiffuse = max(totalDiffuse, 0.1);

        vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
        vec4 reflectColor = texture(reflectionTexture, vec2(ndc.x,-ndc.y));
        vec4 refractColor = texture(refractionTexture, vec2(ndc.x,ndc.y));
        vec4 reflectMix = mix(reflectColor, refractColor, 0.5);
        vec4 baseColor = texture(baseTexture, vec2(ndc.x,ndc.y));

        outColor = vec4(totalDiffuse,1.0) + vec4(totalSpecular, 1.0);
        //To see what the base texture looks like overlaid, change 0.01 to 0.5 or any value inbetween
        //Personally, not a fan
        outColor = outColor * mix(reflectMix, baseColor, 0.01);
        outColor = mix(vec4(waterColor, 1.0), outColor, visibility);
        outColor = outColor * vec4(1.0f, 1.0f, 1.0f, 0.5f);
    }
