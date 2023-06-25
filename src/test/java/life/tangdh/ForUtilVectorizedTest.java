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
        for (int i = 0; i < 100; i++) {
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
        for (int i = 0; i < 100; i++) {
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
        for (int i = 0; i < 100; i++) {
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
        for (int i = 0; i < 100; i++) {
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
        for (int i = 0; i < 100; i++) {
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
        for (int i = 0; i < 100; i++) {
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
    public void encodeDecode_1() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(2);
            }
            long[] output = new long[128];
            SimdPack.pack1_128bit(input, output);

            long[] output1 = new long[128];
            SimdPack.unpack1_128bit(output, output1);

            Assert.assertArrayEquals(input, output1);

        }
    }

    @Test
    public void encodeDecode_1_same_format() throws IOException {
        Random random = new Random();
        var forUtilVectorized = new ForUtilVectorized();
        var forUtil  = new ForUtil();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            long[] raw = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(2);
                raw[j] = input[j];
            }
            long[] output = new long[128];
            long[] outs =  new long[128];
            forUtilVectorized.encode(input,1, output);
            forUtil.encode(input, 1, outs);

            Assert.assertArrayEquals(outs, output);
            long[] output1 = new long[128];
            forUtil.decode(1, output, output1);
            Assert.assertArrayEquals(raw, output1);


        }
    }

    @Test
    public void encodeDecode_same_format() throws IOException {
        Random random = new Random();
        var forUtilVectorized = new ForUtilVectorized();
        var forUtil  = new ForUtil();
        for(int bpv = 1; bpv <= 32; bpv++) {
            for (int i = 0; i < 100; i++) {
                long[] input = new long[128];
                long[] raw = new long[128];
                for (int j = 0; j < 128; j++) {
                    input[j] = random.nextLong((1L << bpv) - 1);
                    raw[j] = input[j];
                }
                long[] output = new long[128];
                long[] outs = new long[128];
                forUtilVectorized.encode(input, bpv, output);
                forUtil.encode(input, bpv, outs);

                Assert.assertArrayEquals(outs, output);
//                long[] output1 = new long[128];
//                forUtil.decode(bpv, output, output1);
//                Assert.assertArrayEquals(raw, output1);


            }
        }
    }

    @Test
    public void encodeDecode_2_same_format() throws IOException {
        Random random = new Random();
        var forUtilVectorized = new ForUtilVectorized();
        var forUtil  = new ForUtil();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            long[] raw = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 2);
                raw[j] = input[j];
            }
            long[] output = new long[128];
            long[] outs =  new long[128];
            forUtilVectorized.encode(input,2, output);
            forUtil.encode(input, 2, outs);

            Assert.assertArrayEquals(outs, output);
            long[] output1 = new long[128];
            forUtil.decode(2, output, output1);
            Assert.assertArrayEquals(raw, output1);


        }
    }

    @Test
    public void encodeDecode_3_same_format() throws IOException {
        Random random = new Random();
        var forUtilVectorized = new ForUtilVectorized();
        var forUtil  = new ForUtil();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            long[] raw = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 3);
                raw[j] = input[j];
            }
            long[] output = new long[128];
            long[] outs =  new long[128];
            forUtilVectorized.encode(input,3, output);
            forUtil.encode(input, 3, outs);

            long[] output1 = new long[128];
            forUtil.decode(3, output, output1);
            Assert.assertArrayEquals(raw, output1);


        }
    }

    @Test
    public void encodeDecode_5_same_format() throws IOException {
        Random random = new Random();
        var forUtilVectorized = new ForUtilVectorized();
        var forUtil  = new ForUtil();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            long[] raw = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 5);
                raw[j] = input[j];
            }
            long[] output = new long[128];
            long[] outs =  new long[128];
            forUtilVectorized.encode(input,5, output);
            forUtil.encode(input, 5, outs);

            long[] output1 = new long[128];
            forUtil.decode(5, output, output1);
            Assert.assertArrayEquals(raw, output1);
        }
    }

    @Test
    public void encodeDecode_6_same_format() throws IOException {
        Random random = new Random();
        var forUtilVectorized = new ForUtilVectorized();
        var forUtil  = new ForUtil();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            long[] raw = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 6);
                raw[j] = input[j];
            }
            long[] output = new long[128];
            long[] outs =  new long[128];
            forUtilVectorized.encode(input,6, output);
            forUtil.encode(input, 6, outs);

            long[] output1 = new long[128];
            forUtil.decode(6, output, output1);
            Assert.assertArrayEquals(raw, output1);
        }
    }

    @Test
    public void encodeDecode_7_same_format() throws IOException {
        Random random = new Random();
        var forUtilVectorized = new ForUtilVectorized();
        var forUtil  = new ForUtil();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            long[] raw = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 7);
                raw[j] = input[j];
            }
            long[] output = new long[128];
            long[] outs =  new long[128];
            forUtilVectorized.encode(input,7, output);
            forUtil.encode(input, 7, outs);

            long[] output1 = new long[128];
            forUtil.decode(7, output, output1);
            Assert.assertArrayEquals(raw, output1);
        }
    }


    @Test
    public void encodeDecode_4_same_format() throws IOException {
        Random random = new Random();
        var forUtilVectorized = new ForUtilVectorized();
        var forUtil  = new ForUtil();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            long[] raw = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 4);
                raw[j] = input[j];
            }
            long[] output = new long[128];
            long[] outs =  new long[128];
            forUtilVectorized.encode(input,4, output);
            forUtil.encode(input, 4, outs);

            Assert.assertArrayEquals(outs, output);
            long[] output1 = new long[128];
            forUtil.decode(4, output, output1);
            Assert.assertArrayEquals(raw, output1);


        }
    }

    @Test
    public void encodeDecode_8_same_format() throws IOException {
        Random random = new Random();
        var forUtilVectorized = new ForUtilVectorized();
        var forUtil  = new ForUtil();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            long[] raw = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 8);
                raw[j] = input[j];
            }
            long[] output = new long[128];
            long[] outs =  new long[128];
            forUtilVectorized.encode(input,8, output);
            forUtil.encode(input, 8, outs);

            Assert.assertArrayEquals(outs, output);
            long[] output1 = new long[128];
            forUtil.decode(8, output, output1);
            Assert.assertArrayEquals(raw, output1);


        }
    }


    @Test
    public void encodeDecode_2() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 2);
            }
            long[] output = new long[128];
            SimdPack.pack2_256bit(input, output);

            long[] output1 = new long[128];
            SimdPack.unpack2_256bit(output, output1);

            Assert.assertArrayEquals(input, output1);

        }
    }

    @Test
    public void encodeDecode_3() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 3);
            }
            long[] output = new long[128];
            SimdPack.pack3_256bit(input, output);

            long[] output1 = new long[128];
            SimdPack.unpack3_256bit(output, output1);

            Assert.assertArrayEquals(input, output1);

        }
    }

    @Test
    public void encodeDecode_4() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 4);
            }
            long[] output = new long[128];
            SimdPack.pack4_256bit(input, output);

            long[] output1 = new long[128];
            SimdPack.unpack4_256bit(output, output1);

            Assert.assertArrayEquals(input, output1);

        }
    }

    @Test
    public void encodeDecode_5() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 5);
            }
            long[] output = new long[128];
            SimdPack.pack5_256bit(input, output);

            long[] output1 = new long[128];
            SimdPack.unpack5_256bit(output, output1);

            Assert.assertArrayEquals(input, output1);

        }
    }

    @Test
    public void encodeDecode_6() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 6);
            }
            long[] output = new long[128];
            SimdPack.pack6_256bit(input, output);

            long[] output1 = new long[128];
            SimdPack.unpack6_256bit(output, output1);

            Assert.assertArrayEquals(input, output1);

        }
    }

    @Test
    public void encodeDecode_7() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 7);
            }
            long[] output = new long[128];
            SimdPack.pack7_256bit(input, output);

            long[] output1 = new long[128];
            SimdPack.unpack7_256bit(output, output1);

            Assert.assertArrayEquals(input, output1);

        }
    }

    @Test
    public void encodeDecode_8() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for (int j = 0; j < 128; j++) {
                input[j] = random.nextLong(1 << 8);
            }
            long[] output = new long[128];
            SimdPack.pack8_256bit(input, output);

            long[] output1 = new long[128];
            SimdPack.unpack8_256bit(output, output1);

            Assert.assertArrayEquals(input, output1);

        }
    }

//    @Test
//    public void encode() throws IOException {
//        Random random = new Random();
//        for (int i = 0; i < 100; i++) {
//            long[] arr = new long[128];
//            long[] arr1 = new long[128];
//            for (int j = 0; j < 128; j++) {
//                arr[j] = random.nextLong((1 << 7) - 1);
//                arr1[j] = arr[j];
//            }
//            long[] out1 = new long[128];
//            long[] out2 = new long[128];
//            ForUtilVectorized forUtilVectorized = new ForUtilVectorized();
//            Assert.assertArrayEquals(arr1, arr);
//            forUtilVectorized.encode(arr, 7, out1);
//            ForUtil forUtil = new ForUtil();
//            forUtil.encode(arr1, 7, out2);
//            Assert.assertArrayEquals(Arrays.copyOfRange(out1, 0, 14),
//                    Arrays.copyOfRange(out2, 0, 14));
//        }
//    }


}