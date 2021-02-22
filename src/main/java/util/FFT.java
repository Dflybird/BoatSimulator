package util;

/**
 * @Author Gq
 * @Date 2021/2/17 15:26
 * @Version 1.0
 **/
public class FFT {
    private static final float PI = (float) Math.PI;

    private final int N;
    private final int log2N;
    private final int[] reversed;
    private final Complex[][] T;
    private final Complex[][] c;
    private int which = 0;

    public FFT(int N) {
        this.N = N;
        this.log2N = (int) (Math.log(N) / Math.log(2));
        this.reversed = new int[N];

        for (int i = 0; i < N; i++) {
            reversed[i] = reverse(i);
        }

        int pow2 = 1;
        T = new Complex[log2N][];
        for (int i = 0; i < log2N; i++) {
            T[i] = new Complex[pow2];
            for (int j = 0; j < pow2; j++) {
                T[i][j] = t(j, pow2 * 2);
            }
            pow2 *= 2;
        }

        c = new Complex[2][N];
    }

    public Complex[] fft(Complex[] inputs, Complex[] outputs, int stride, int offset){
        for (int i = 0; i < N; i++) {
            c[which][i] = inputs[reversed[i] * stride + offset];
        }
        int loops = N>>1;
        int size = 1<<1;
        int sizeOver2 = 1;
        int w = 0;
        for (int i = 0; i < log2N; i++) {
            which ^= 1;
            for (int j = 0; j < loops; j++) {
                for (int k = 0; k < sizeOver2; k++) {
                    c[which][size * j + k] =  c[which^1][size * j + k]
                            .plus(c[which^1][size * j + sizeOver2 + k].times(T[w][k]));
                }
                for (int k = sizeOver2; k < size; k++) {
                    c[which][size * j + k] =  c[which^1][size * j - sizeOver2 + k]
                            .minus(c[which^1][size * j + k].times(T[w][k - sizeOver2]));
                }
            }
            loops >>= 1;
            size <<= 1;
            sizeOver2 <<= 1;
            w++;
        }

        for (int i = 0; i < N; i++) {
            outputs[i * stride + offset] = c[which][i];
        }
        return outputs;
    }

    /**
     * 反转i
     * @param i
     * @return
     */
    private int reverse(int i){
        int res = 0;
        for (int j = 0; j < log2N; j++) {
            res = (res << 1) + (i & 1);
            i >>= 1;
        }
        return res;
    }

    private Complex t(int x, int N) {
        float var = 2 * PI * x / N;
        return new Complex(Math.cos(var), Math.sin(var));
    }
}