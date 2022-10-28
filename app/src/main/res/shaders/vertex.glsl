attribute vec4 m_position;
uniform mat4 m_matrix;

void main()
{
    gl_Position = m_matrix * m_position;
    gl_PointSize = 5.0;
}