package life.tangdh;


import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class ForUtilVectorizedTest {

    @Test
    public void expand8() {
        Random random = new Random();
        random.nextLong();
        for(int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong();
            }

            long[] inputCopy = Arrays.copyOfRange(input, 0, 128);

            ForUtil.expand8(input);

            ForUtilVectorized.expand8(inputCopy);

            Assert.assertArrayEquals(input, inputCopy);
        }
    }

    @Test
    public void expand16() {
        Random random = new Random();
        random.nextLong();
        for(int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong();
            }

            long[] inputCopy = Arrays.copyOfRange(input, 0, 128);

            ForUtil.expand16(input);

            ForUtilVectorized.expand16(inputCopy);

            Assert.assertArrayEquals(input, inputCopy);
        }
    }

    @Test
    public void expand32() {
        Random random = new Random();
        random.nextLong();
        for(int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong();
            }

            long[] inputCopy = Arrays.copyOfRange(input, 0, 128);

            ForUtil.expand32(input);

            ForUtilVectorized.expand32(inputCopy);

            Assert.assertArrayEquals(input, inputCopy);
        }
    }


    @Test
    public void collapse8() {
        Random random = new Random();
        for(int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 8);
            }

            long[] inputCopy = Arrays.copyOfRange(input, 0, 128);

            ForUtil.collapse8(input);

            ForUtilVectorized.collapse8(inputCopy);

            Assert.assertArrayEquals(Arrays.copyOfRange(input, 0, 16),
                    Arrays.copyOfRange(inputCopy, 0, 16));
        }
    }

    @Test
    public void collapse16() {
        Random random = new Random();
        for(int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 16);
            }

            long[] inputCopy = Arrays.copyOfRange(input, 0, 128);

            ForUtil.collapse16(input);

            ForUtilVectorized.collapse16(inputCopy);

            Assert.assertArrayEquals(Arrays.copyOfRange(input, 0, 32),
                    Arrays.copyOfRange(inputCopy, 0, 32));
        }
    }

    @Test
    public void collapse32() {
        Random random = new Random();
        for(int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1L << 32);
            }

            long[] inputCopy = Arrays.copyOfRange(input, 0, 128);

            ForUtil.collapse32(input);

            ForUtilVectorized.collapse32(inputCopy);

            Assert.assertArrayEquals(Arrays.copyOfRange(input, 0, 64),
                    Arrays.copyOfRange(inputCopy, 0, 64));
        }
    }

    @Test
    public void encode() throws IOException {
        long[]  arr = new long[128];
        for(int i = 1; i < 128; i++) {
           arr[i-1] = i;
        }
        arr[127] = 127;
        arr[0] = 127;
//        ForUtilVectorized forUtilVectorized = new ForUtilVectorized();
//        forUtilVectorized.encode(arr, 8, arr);
        ForUtil forUtil = new ForUtil();
        long[] out1 = new long[128];
        long[] out2 = new long[128];
        forUtil.encode(arr, 7, out1);
        forUtil.decode(7, out1, out2);
    }



}