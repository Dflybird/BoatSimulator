package environment;

import conf.Constant;
import gui.obj.Model;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Complex;
import util.FFT;

import java.util.Random;

/**
 * @Author Gq
 * @Date 2021/2/21 15:07
 * @Version 1.0
 **/
public class Wave {
    /** 波一个周期时间长度，将连续色散曲线量化，T越大越接近连续色散曲线，单位：秒 */
    private static final float T = 200;
    private static final float PI = (float) Math.PI;
    private static final float SEA_LEVEL = 0f;
    private float Lx;
    private float Lz;
    /** 2的幂 */
    private int N;
    private int M;
    private int NPlus1;
    private int MPlus1;

    /** fft */
    private Complex[] hTildes;
    private Complex[] hTildeSlopeX;
    private Complex[] hTildeSlopeZ;
    private Complex[] hTildeDx;
    private Complex[] hTildeDz;
    private FFT fftM;
    private FFT fftN;

    /** 重力加速度 */
    private float g;
    /** 风 */
    private Wind wind;
    /** 菲利普斯常数,影响波高 */
    private float A;

    /** 点数 */
    private int indicesNum;
    /** 网格数 */
    private int gridsNum;

    /** mesh */
    private int[] indices;
    private float[] vertices;
    private float[] normals;
    private float[] textures;
    private final Model model;

    private float[] originalVertices;
    private float[] hTildeArr;
    private float[] hTildeConjArr;


    public Wave(float Lx, float Lz, int N, int M, Wind wind, float A) {
        this.Lx = Lx;
        this.Lz = Lz;
        this.N = N;
        this.M = M;
        this.wind = wind;
        this.A = A;
        this.g = Constant.g;
        this.NPlus1 = N + 1;
        this.MPlus1 = M + 1;

        this.indicesNum = NPlus1 * MPlus1;
        this.gridsNum = N * M;

        this.indices = new int[gridsNum * 6];
        this.vertices = new float[indicesNum * 3];
        this.normals = new float[indicesNum * 3];
        this.textures = new float[indicesNum * 2];
        this.model = new Model(indices, vertices, textures, normals);

        this.originalVertices = new float[indicesNum * 3];
        this.hTildeArr = new float[indicesNum * 2];
        this.hTildeConjArr = new float[indicesNum * 2];

        this.hTildes = new Complex[gridsNum];
        this.hTildeSlopeX = new Complex[gridsNum];
        this.hTildeSlopeZ = new Complex[gridsNum];
        this.hTildeDx = new Complex[gridsNum];
        this.hTildeDz = new Complex[gridsNum];
        this.fftM = new FFT(N);
        this.fftN = new FFT(M);

        int index;
        Complex hTilde0, hTilde0Conj;
        for (int m = 0; m < MPlus1; m++) {
            for (int n = 0; n < NPlus1; n++) {
                index = m * NPlus1 + n;

                hTilde0 = hTilde0(n, m);
                hTilde0Conj = hTilde0(-n, -m).conjugate();

                hTildeArr[index * 2] = (float) hTilde0.re();
                hTildeArr[index * 2 + 1] = (float) hTilde0.im();

                hTildeConjArr[index * 2] = (float) hTilde0Conj.re();
                hTildeConjArr[index * 2 + 1] = (float) hTilde0Conj.im();

                originalVertices[index * 3] = vertices[index * 3] = (n - N / 2f) * Lx / N;
                originalVertices[index * 3 + 1] = vertices[index * 3 + 1] = SEA_LEVEL;
                originalVertices[index * 3 + 2] = vertices[index * 3 + 2] = (m - M / 2f) * Lz / M;

                normals[index * 3] = 0;
                normals[index * 3 + 1] = 1;
                normals[index * 3 + 2] = 0;
            }
        }

        //先延X轴再延Z轴
        // ┌ ┬ ┐├ ─ ┤└ ┴ ┘ ┼ │
        //
        // m
        // 4 ┌───┬───┬───┬───┐
        //   │12 │13 │14 │15 │
        // 3 ├───┼───┼───┼───┤
        //   │ 8 │ 9 │10 │11 │
        // 2 ├───┼───┼───┼───┤
        //   │ 4 │ 5 │ 6 │ 7 │
        // 1 ├───┼───┼───┼───┤
        //   │ 0 │ 1 │ 2 │ 3 │
        // 0 └───┴───┴───┴───┘
        //   0   1   2   3   4   n
        int count = 0;
        for (int m = 0; m < M; m++) {
            for (int n = 0; n < N; n++) {
                index = m * NPlus1 + n;

                //index+NPlus1 -- index+NPlus1+1
                //    |          /      |
                //    |        /        |
                //    |      /          |
                //    |    /            |
                //  index ----------- index+1
                indices[count++] = index;
                indices[count++] = index + NPlus1;
                indices[count++] = index + NPlus1 + 1;
                indices[count++] = index;
                indices[count++] = index + NPlus1 + 1;
                indices[count++] = index + 1;
            }
        }
    }

    public void evaluateWavesFFT (float t) {
        //坐标x平移方向
        float lambda = -1;
        int indexH, indexV;
        float kx, kz, len;


        for (int m = 0; m < M; m++) {
            kz = PI * (2 * m - M) / Lz;
            for (int n = 0; n < N; n++) {
                kx = PI * (2 * n - N) / Lx;
                len = (float) Math.sqrt(kx * kx + kz * kz);
                indexH = m * N + n;

                hTildes[indexH] = hTilde(t, n, m);
                hTildeSlopeX[indexH] = hTildes[indexH].times(new Complex(0, kx));
                hTildeSlopeZ[indexH] = hTildes[indexH].times(new Complex(0, kz));
                if (len < 0.000001f) {
                    hTildeDx[indexH] = new Complex(0,0);
                    hTildeDz[indexH] = new Complex(0,0);
                } else {
                    hTildeDx[indexH] = hTildes[indexH].times(new Complex(0, -kx/len));
                    hTildeDz[indexH] = hTildes[indexH].times(new Complex(0, -kz/len));
                }
            }
        }

        for (int m = 0; m < M; m++) {
            fftM.fft(hTildes, hTildes, 1, m * N);
            fftM.fft(hTildeSlopeX, hTildeSlopeX, 1, m * N);
            fftM.fft(hTildeSlopeZ, hTildeSlopeZ, 1, m * N);
            fftM.fft(hTildeDx, hTildeDx, 1, m * N);
            fftM.fft(hTildeDz, hTildeDz, 1, m * N);
        }

        for (int n = 0; n < N; n++) {
            fftN.fft(hTildes, hTildes, N, n);
            fftN.fft(hTildeSlopeX, hTildeSlopeX, N, n);
            fftN.fft(hTildeSlopeZ, hTildeSlopeZ, N, n);
            fftN.fft(hTildeDx, hTildeDx, N, n);
            fftN.fft(hTildeDz, hTildeDz, N, n);
        }

        float sign;
        float[] signs = new float[]{1.0f, -1.0f};
        Vector3f normal;
        for (int m = 0; m < M; m++) {
            for (int n = 0; n < N; n++) {
                indexH = m * N + n;         //index of hTildes
                indexV = m * NPlus1 + n;    //index of vertices

                sign = signs[(n + m) & 1];
                hTildes[indexH] = hTildes[indexH].scale(sign);

                //x处海浪高度
                vertices[indexV * 3 + 1] = (float) hTildes[indexH].re();

                //x处海浪水平位移
                hTildeDx[indexH] = hTildeDx[indexH].scale(sign);
                hTildeDz[indexH] = hTildeDz[indexH].scale(sign);
//                vertices[indexV * 3] = originalVertices[indexV * 3] + lambda * (float) hTildeDx[indexH].re();
//                vertices[indexV * 3 + 2] = originalVertices[indexV * 3 + 2] + lambda * (float) hTildeDz[indexH].re();

                //法向量
                hTildeSlopeX[indexH] = hTildeSlopeX[indexH].scale(sign);
                hTildeSlopeZ[indexH] = hTildeSlopeZ[indexH].scale(sign);
                normal = new Vector3f((float) - hTildeSlopeX[indexH].re(), 1f, (float) - hTildeSlopeZ[indexH].re()).normalize();
                normals[indexV * 3] = normal.x;
                normals[indexV * 3 + 1] = normal.y;
                normals[indexV * 3 + 2] = normal.z;

                //将最后一个边界点赋值为当前点，平滑N*M海面范围边界
                if (n == 0 && m == 0) {
                    vertices[(indexV + M * NPlus1 + N) * 3 + 1] = (float) hTildes[indexH].re();
//                    vertices[(indexV + M * NPlus1 + N) * 3] = originalVertices[(indexV + M * NPlus1 + N) * 3] + lambda * (float) hTildeDx[indexH].re();
//                    vertices[(indexV + M * NPlus1 + N) * 3 + 2] = originalVertices[(indexV + M * NPlus1 + N) * 3 + 2] + lambda * (float) hTildeDz[indexH].re();

                    normals[(indexV + M * NPlus1 + N) * 3] = normal.x;
                    normals[(indexV + M * NPlus1 + N) * 3 + 1] = normal.y;
                    normals[(indexV + M * NPlus1 + N) * 3 + 2] = normal.z;
                }
                if (n == 0) {
                    vertices[(indexV + N) * 3 + 1] = (float) hTildes[indexH].re();
//                    vertices[(indexV + N) * 3] = originalVertices[(indexV + N) * 3] + lambda * (float) hTildeDx[indexH].re();
//                    vertices[(indexV + N) * 3 + 2] = originalVertices[(indexV + N) * 3 + 2] + lambda * (float) hTildeDz[indexH].re();

                    normals[(indexV + N) * 3] = normal.x;
                    normals[(indexV + N) * 3 + 1] = normal.y;
                    normals[(indexV + N) * 3 + 2] = normal.z;
                }
                if (m == 0) {
                    vertices[(indexV + M * NPlus1) * 3 + 1] = (float) hTildes[indexH].re();
//                    vertices[(indexV + M * NPlus1) * 3] = originalVertices[(indexV + M * NPlus1) * 3] + lambda * (float) hTildeDx[indexH].re();
//                    vertices[(indexV + M * NPlus1) * 3 + 2] = originalVertices[(indexV + M * NPlus1) * 3 + 2] + lambda * (float) hTildeDz[indexH].re();

                    normals[(indexV + M * NPlus1) * 3] = normal.x;
                    normals[(indexV + M * NPlus1) * 3 + 1] = normal.y;
                    normals[(indexV + M * NPlus1) * 3 + 2] = normal.z;
                }
            }
        }
    }

    /**
     * 计算色散频率
     * @param n
     * @param m
     * @return
     */
    private float dispersion(int n, int m) {
        float w0 = 2 * PI / T;
        float kx = PI * (2 * n - N) / Lx;
        float kz = PI * (2 * m - M) / Lz;
        float k = (float) Math.sqrt(kx * kx + kz * kz);
        float w = (float) Math.sqrt(g * k);
        return (float) (Math.floor(w / w0) * w0);
    }

    /**
     * 计算菲利普斯波谱
     * @return
     */
    private float phillips(int n, int m) {
        float kx = PI * (2 * n - N) / Lx;
        float kz = PI * (2 * m - M) / Lz;
        Vector2f k = new Vector2f(kx, kz);
        float kL = k.length();
        if (kL < 0.000001) {
            return 0;
        }

        float k2 = kL * kL;
        float k4 = k2 * k2;
        float kw = k.normalize().dot(wind.getDirection());
        float kw2 = kw * kw;

        float L = wind.getVelocity() * wind.getVelocity() / g;
        float L2 = L * L;

        //衰减
        float damping = 0.001f;
        float l2 = L2 * damping * damping;

        return (float) (A* Math.exp(-1f / (k2 * L2)) / k4 * kw2 * Math.exp(-k2 * l2));
    }

    private Complex hTilde0(int n, int m) {
        Random random = new Random();
        double re = random.nextGaussian();
        double im = random.nextGaussian();
        Complex complex = new Complex(re, im);
        return complex.scale(Math.sqrt(phillips(n, m) / 2));
    }

    /**
     *
     * @param t 时间，单位：秒
     * @param n
     * @param m
     * @return
     */
    private Complex hTilde(float t, int n, int m) {
        //计算具体的点
        int index = m * NPlus1 + n;

        Complex hTilde0 = new Complex(hTildeArr[index * 2], hTildeArr[index * 2 +1]);
        Complex hTilde0Conj = new Complex(hTildeConjArr[index * 2], hTildeConjArr[index * 2 +1]);

        //TODO 简化计算？
        float omega = dispersion(n, m) * t;

        float cosOmega = (float) Math.cos(omega);
        float sinOmega = (float) Math.sin(omega);

        //$e^{ix}=\cos x+i\sin x$
        Complex e0 = new Complex(cosOmega, sinOmega);
        Complex e1 = new Complex(cosOmega, -sinOmega);

        Complex res0 = hTilde0.times(e0);
        Complex res1 = hTilde0Conj.times(e1);
        return res0.plus(res1);
    }

    public int getIndicesNum() {
        return indicesNum;
    }

    public int getGridsNum() {
        return gridsNum;
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getNormals() {
        return normals;
    }

    public float[] getTextures() {
        return textures;
    }

    public float[] getOriginalVertices() {
        return originalVertices;
    }

    public Model getModel() {
        return model;
    }
}
