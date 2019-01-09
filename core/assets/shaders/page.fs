/*
Original fragment shader created by laserdog:
https://www.shadertoy.com/view/ls3cDB

Modified for compatibility with LibGDX by Raymond Buckley.
*/
#define pi 3.14159265359
#define radius .1
#ifdef GL_ES
    precision mediump float;
#endif

uniform sampler2D u_texture1;
uniform sampler2D u_texture2;
varying vec2 v_texCoords;

uniform vec2 u_resolution;
uniform float u_speed;
uniform float u_size;
uniform vec4 u_mouse;

varying float v_time;


void main()
{
    float aspect = u_resolution.x / u_resolution.y;

    vec2 uv = gl_FragCoord.xy * vec2(aspect, 1.) / u_resolution.xy;
    uv.y = 1.0 - uv.y;
    vec2 mouse = u_mouse.xy  * vec2(aspect, 1.) / u_resolution.xy;
    vec2 mouseDir = normalize(abs(u_mouse.zw) - u_mouse.xy);
    vec2 origin = clamp(mouse - mouseDir * mouse.x / mouseDir.x, 0., 1.);
    
    float mouseDist = clamp(length(mouse - origin) 
        + (aspect - (abs(u_mouse.z) / u_resolution.x) * aspect) / mouseDir.x, 0., aspect / mouseDir.x);
    
    if (mouseDir.x < 0.)
    {
        mouseDist = distance(mouse, origin);
    }
  
    float proj = dot(uv - origin, mouseDir);
    float dist = proj - mouseDist;
    
    vec2 linePoint = uv - dist * mouseDir;
    
    if (dist > radius) 
    {
        gl_FragColor = texture2D(u_texture2, uv * vec2(1. / aspect, 1.));
        gl_FragColor.rgb *= pow(clamp(dist - radius, 0., 1.) * 1.5, .2);
    }
    else if (dist >= 0.)
    {
        // map to cylinder point
        float theta = asin(dist / radius);
        vec2 p2 = linePoint + mouseDir * (pi - theta) * radius;
        vec2 p1 = linePoint + mouseDir * theta * radius;
        uv = (p2.x <= aspect && p2.y <= 1. && p2.x > 0. && p2.y > 0.) ? p2 : p1;
        gl_FragColor = texture2D(u_texture1, uv * vec2(1. / aspect, 1.));
        gl_FragColor.rgb *= pow(clamp((radius - dist) / radius, 0., 1.), .2);
    }
    else 
    {
        vec2 p = linePoint + mouseDir * (abs(dist) + pi * radius);
        uv = (p.x <= aspect && p.y <= 1. && p.x > 0. && p.y > 0.) ? p : uv;
        gl_FragColor = texture2D(u_texture1, uv * vec2(1. / aspect, 1.));
    }
}