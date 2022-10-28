attribute vec4 m_position;
uniform mat4 m_viewMatrix;
uniform mat4 m_totalMatrix;
uniform mat4 m_modelMatrix;
uniform mat4 m_projectMatrix;

void main()
{

    gl_Position = m_projectMatrix * m_viewMatrix * m_modelMatrix * m_position;
    gl_PointSize = 5.0;
}