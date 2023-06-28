package life.tangdh;

import java.io.IOException;
import java.util.Arrays;

public class ForUtil {
    static final int BLOCK_SIZE = 128;
    private static final int BLOCK_SIZE_LOG2 = 7;

    private static long expandMask32(long mask32) {
        return mask32 | (mask32 << 32);
    }

    private static long expandMask16(long mask16) {
        return expandMask32(mask16 | (mask16 << 16));
    }

    private static long expandMask8(long mask8) {
        return expandMask16(mask8 | (mask8 << 8));
    }

    private static long mask32(int bitsPerValue) {
        return expandMask32((1L << bitsPerValue) - 1);
    }

    private static long mask16(int bitsPerValue) {
        return expandMask16((1L << bitsPerValue) - 1);
    }

    private static long mask8(int bitsPerValue) {
        return expandMask8((1L << bitsPerValue) - 1);
    }

    public static void expand8(long[] arr) {
        for (int i = 0; i < 16; ++i) {
            long l = arr[i];
            arr[i] = (l >>> 56) & 0xFFL;
            arr[16 + i] = (l >>> 48) & 0xFFL;
            arr[32 + i] = (l >>> 40) & 0xFFL;
            arr[48 + i] = (l >>> 32) & 0xFFL;
            arr[64 + i] = (l >>> 24) & 0xFFL;
            arr[80 + i] = (l >>> 16) & 0xFFL;
            arr[96 + i] = (l >>> 8) & 0xFFL;
            arr[112 + i] = l & 0xFFL;
        }
    }

    private static void expand8To32(long[] arr) {
        for (int i = 0; i < 16; ++i) {
            long l = arr[i];
            arr[i] = (l >>> 24) & 0x000000FF000000FFL;
            arr[16 + i] = (l >>> 16) & 0x000000FF000000FFL;
            arr[32 + i] = (l >>> 8) & 0x000000FF000000FFL;
            arr[48 + i] = l & 0x000000FF000000FFL;
        }
    }

    public static void collapse8(long[] arr) {
        for (int i = 0; i < 16; ++i) {
            arr[i] =
                    (arr[i] << 56)
                            | (arr[16 + i] << 48)
                            | (arr[32 + i] << 40)
                            | (arr[48 + i] << 32)
                            | (arr[64 + i] << 24)
                            | (arr[80 + i] << 16)
                            | (arr[96 + i] << 8)
                            | arr[112 + i];
        }
    }

    public static void expand16(long[] arr) {
        for (int i = 0; i < 32; ++i) {
            long l = arr[i];
            arr[i] = (l >>> 48) & 0xFFFFL;
            arr[32 + i] = (l >>> 32) & 0xFFFFL;
            arr[64 + i] = (l >>> 16) & 0xFFFFL;
            arr[96 + i] = l & 0xFFFFL;
        }
    }

    private static void expand16To32(long[] arr) {
        for (int i = 0; i < 32; ++i) {
            long l = arr[i];
            arr[i] = (l >>> 16) & 0x0000FFFF0000FFFFL;
            arr[32 + i] = l & 0x0000FFFF0000FFFFL;
        }
    }

    public static void collapse16(long[] arr) {
        for (int i = 0; i < 32; ++i) {
            arr[i] = (arr[i] << 48) | (arr[32 + i] << 32) | (arr[64 + i] << 16) | arr[96 + i];
        }
    }

    static void expand32(long[] arr) {
        for (int i = 0; i < 64; ++i) {
            long l = arr[i];
            arr[i] = l >>> 32;
            arr[64 + i] = l & 0xFFFFFFFFL;
        }
    }

    public static void collapse32(long[] arr) {
        for (int i = 0; i < 64; ++i) {
            arr[i] = (arr[i] << 32) | arr[64 + i];
        }
    }

    private final long[] tmp = new long[BLOCK_SIZE / 2];

    private void encode5(long[] input, int bitsPerValue, long[] out) {
        out[0] =
                (input[0] << 59)

                        |
                        (input[16] << 51)

                        |
                        (input[32] << 43)

                        |
                        (input[48] << 35)

                        |
                        (input[64] << 27)

                        |
                        (input[80] << 19)

                        |
                        (input[96] << 11)

                        |
                        (input[112] << 3)

        ;out[1] =
                (input[1] << 59)

                        |
                        (input[17] << 51)

                        |
                        (input[33] << 43)

                        |
                        (input[49] << 35)

                        |
                        (input[65] << 27)

                        |
                        (input[81] << 19)

                        |
                        (input[97] << 11)

                        |
                        (input[113] << 3)

        ;out[2] =
                (input[2] << 59)

                        |
                        (input[18] << 51)

                        |
                        (input[34] << 43)

                        |
                        (input[50] << 35)

                        |
                        (input[66] << 27)

                        |
                        (input[82] << 19)

                        |
                        (input[98] << 11)

                        |
                        (input[114] << 3)

        ;out[3] =
                (input[3] << 59)

                        |
                        (input[19] << 51)

                        |
                        (input[35] << 43)

                        |
                        (input[51] << 35)

                        |
                        (input[67] << 27)

                        |
                        (input[83] << 19)

                        |
                        (input[99] << 11)

                        |
                        (input[115] << 3)

        ;out[4] =
                (input[4] << 59)

                        |
                        (input[20] << 51)

                        |
                        (input[36] << 43)

                        |
                        (input[52] << 35)

                        |
                        (input[68] << 27)

                        |
                        (input[84] << 19)

                        |
                        (input[100] << 11)

                        |
                        (input[116] << 3)

        ;out[5] =
                (input[5] << 59)

                        |
                        (input[21] << 51)

                        |
                        (input[37] << 43)

                        |
                        (input[53] << 35)

                        |
                        (input[69] << 27)

                        |
                        (input[85] << 19)

                        |
                        (input[101] << 11)

                        |
                        (input[117] << 3)

        ;out[6] =
                (input[6] << 59)

                        |
                        (input[22] << 51)

                        |
                        (input[38] << 43)

                        |
                        (input[54] << 35)

                        |
                        (input[70] << 27)

                        |
                        (input[86] << 19)

                        |
                        (input[102] << 11)

                        |
                        (input[118] << 3)

        ;out[7] =
                (input[7] << 59)

                        |
                        (input[23] << 51)

                        |
                        (input[39] << 43)

                        |
                        (input[55] << 35)

                        |
                        (input[71] << 27)

                        |
                        (input[87] << 19)

                        |
                        (input[103] << 11)

                        |
                        (input[119] << 3)

        ;out[8] =
                (input[8] << 59)

                        |
                        (input[24] << 51)

                        |
                        (input[40] << 43)

                        |
                        (input[56] << 35)

                        |
                        (input[72] << 27)

                        |
                        (input[88] << 19)

                        |
                        (input[104] << 11)

                        |
                        (input[120] << 3)

        ;out[9] =
                (input[9] << 59)

                        |
                        (input[25] << 51)

                        |
                        (input[41] << 43)

                        |
                        (input[57] << 35)

                        |
                        (input[73] << 27)

                        |
                        (input[89] << 19)

                        |
                        (input[105] << 11)

                        |
                        (input[121] << 3)

        ;tmp[10] =

                (input[10] << 56)

                        |
                        (input[26] << 48)

                        |
                        (input[42] << 40)

                        |
                        (input[58] << 32)

                        |
                        (input[74] << 24)

                        |
                        (input[90] << 16)

                        |
                        (input[106] << 8)

                        |
                        (input[122] << 0)

        ;
        tmp[11] =

                (input[11] << 56)

                        |
                        (input[27] << 48)

                        |
                        (input[43] << 40)

                        |
                        (input[59] << 32)

                        |
                        (input[75] << 24)

                        |
                        (input[91] << 16)

                        |
                        (input[107] << 8)

                        |
                        (input[123] << 0)

        ;
        tmp[12] =

                (input[12] << 56)

                        |
                        (input[28] << 48)

                        |
                        (input[44] << 40)

                        |
                        (input[60] << 32)

                        |
                        (input[76] << 24)

                        |
                        (input[92] << 16)

                        |
                        (input[108] << 8)

                        |
                        (input[124] << 0)

        ;
        tmp[13] =

                (input[13] << 56)

                        |
                        (input[29] << 48)

                        |
                        (input[45] << 40)

                        |
                        (input[61] << 32)

                        |
                        (input[77] << 24)

                        |
                        (input[93] << 16)

                        |
                        (input[109] << 8)

                        |
                        (input[125] << 0)

        ;
        tmp[14] =

                (input[14] << 56)

                        |
                        (input[30] << 48)

                        |
                        (input[46] << 40)

                        |
                        (input[62] << 32)

                        |
                        (input[78] << 24)

                        |
                        (input[94] << 16)

                        |
                        (input[110] << 8)

                        |
                        (input[126] << 0)

        ;
        tmp[15] =

                (input[15] << 56)

                        |
                        (input[31] << 48)

                        |
                        (input[47] << 40)

                        |
                        (input[63] << 32)

                        |
                        (input[79] << 24)

                        |
                        (input[95] << 16)

                        |
                        (input[111] << 8)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 3;
        final long maskRemainingBitsPerLong = MASKS8[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 10;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 16) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS8[remainingBitsPerValue];
                mask2 = MASKS8[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }

    }
    /**
     * Encode 128 integers from {@code longs} into {@code out}.
     */
    void encode(long[] longs, int bitsPerValue, long[] out) throws IOException {
        final int nextPrimitive;
        final int numLongs;
//        if (bitsPerValue == 5) {
//            encode5(longs, bitsPerValue, out);
//            return;
//        }
        if (bitsPerValue <= 8) {
            nextPrimitive = 8;
            numLongs = BLOCK_SIZE / 8;
            collapse8(longs);
        } else if (bitsPerValue <= 16) {
            nextPrimitive = 16;
            numLongs = BLOCK_SIZE / 4;
            collapse16(longs);
        } else {
            nextPrimitive = 32;
            numLongs = BLOCK_SIZE / 2;
            collapse32(longs);
        }

        final int numLongsPerShift = bitsPerValue * 2;
        int idx = 0;
        int shift = nextPrimitive - bitsPerValue;
        // push to the head of byte
        for (int i = 0; i < numLongsPerShift; ++i) {
            tmp[i] = longs[idx++] << shift;
        }
        // can we insert more data in the same byte?
        for (shift = shift - bitsPerValue; shift >= 0; shift -= bitsPerValue) {
            for (int i = 0; i < numLongsPerShift; ++i) {
                tmp[i] |= longs[idx++] << shift;
            }
        }

        final int remainingBitsPerLong = shift + bitsPerValue;
        final long maskRemainingBitsPerLong;
        if (nextPrimitive == 8) {
            maskRemainingBitsPerLong = MASKS8[remainingBitsPerLong];
        } else if (nextPrimitive == 16) {
            maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];
        } else {
            maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];
        }

        int tmpIdx = 0;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < numLongs) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                tmp[tmpIdx++] |= (longs[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;
                if (nextPrimitive == 8) {
                    mask1 = MASKS8[remainingBitsPerValue];
                    mask2 = MASKS8[remainingBitsPerLong - remainingBitsPerValue];
                } else if (nextPrimitive == 16) {
                    mask1 = MASKS16[remainingBitsPerValue];
                    mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
                } else {
                    mask1 = MASKS32[remainingBitsPerValue];
                    mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                }
                tmp[tmpIdx] |= (longs[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                tmp[tmpIdx++] |= (longs[idx] >>> remainingBitsPerValue) & mask2;
            }
        }

        for (int i = 0; i < numLongsPerShift; ++i) {
            out[i] = tmp[i];
        }
    }

    /**
     * Number of bytes required to encode 128 integers of {@code bitsPerValue} bits per value.
     */
    int numBytes(int bitsPerValue) {
        return bitsPerValue << (BLOCK_SIZE_LOG2 - 3);
    }

    private static void decodeSlow(int bitsPerValue, long[] in, long[] tmp, long[] longs)
            throws IOException {
        final int numLongs = bitsPerValue << 1;
        tmp = in;
        final long mask = MASKS32[bitsPerValue];
        int longsIdx = 0;
        int shift = 32 - bitsPerValue;
        for (; shift >= 0; shift -= bitsPerValue) {
            shiftLongs(tmp, numLongs, longs, longsIdx, shift, mask);
            longsIdx += numLongs;
        }
        final int remainingBitsPerLong = shift + bitsPerValue;
        final long mask32RemainingBitsPerLong = MASKS32[remainingBitsPerLong];
        int tmpIdx = 0;
        int remainingBits = remainingBitsPerLong;
        for (; longsIdx < BLOCK_SIZE / 2; ++longsIdx) {
            int b = bitsPerValue - remainingBits;
            long l = (tmp[tmpIdx++] & MASKS32[remainingBits]) << b;
            while (b >= remainingBitsPerLong) {
                b -= remainingBitsPerLong;
                l |= (tmp[tmpIdx++] & mask32RemainingBitsPerLong) << b;
            }
            if (b > 0) {
                l |= (tmp[tmpIdx] >>> (remainingBitsPerLong - b)) & MASKS32[b];
                remainingBits = remainingBitsPerLong - b;
            } else {
                remainingBits = remainingBitsPerLong;
            }
            longs[longsIdx] = l;
        }
    }

    /**
     * The pattern that this shiftLongs method applies is recognized by the C2 compiler, which
     * generates SIMD instructions for it in order to shift multiple longs at once.
     */
    private static void shiftLongs(long[] a, int count, long[] b, int bi, int shift, long mask) {
        for (int i = 0; i < count; ++i) {
            b[bi + i] = (a[i] >>> shift) & mask;
        }
    }

    private static final long[] MASKS8 = new long[8];
    private static final long[] MASKS16 = new long[16];
    private static final long[] MASKS32 = new long[32];

    static {
        for (int i = 0; i < 8; ++i) {
            MASKS8[i] = mask8(i);
        }
        for (int i = 0; i < 16; ++i) {
            MASKS16[i] = mask16(i);
        }
        for (int i = 0; i < 32; ++i) {
            MASKS32[i] = mask32(i);
        }
    }

    // mark values in array as final longs to avoid the cost of reading array, arrays should only be
    // used when the idx is a variable
    private static final long MASK8_1 = MASKS8[1];
    private static final long MASK8_2 = MASKS8[2];
    private static final long MASK8_3 = MASKS8[3];
    private static final long MASK8_4 = MASKS8[4];
    private static final long MASK8_5 = MASKS8[5];
    private static final long MASK8_6 = MASKS8[6];
    private static final long MASK8_7 = MASKS8[7];
    private static final long MASK16_1 = MASKS16[1];
    private static final long MASK16_2 = MASKS16[2];
    private static final long MASK16_3 = MASKS16[3];
    private static final long MASK16_4 = MASKS16[4];
    private static final long MASK16_5 = MASKS16[5];
    private static final long MASK16_6 = MASKS16[6];
    private static final long MASK16_7 = MASKS16[7];
    private static final long MASK16_9 = MASKS16[9];
    private static final long MASK16_10 = MASKS16[10];
    private static final long MASK16_11 = MASKS16[11];
    private static final long MASK16_12 = MASKS16[12];
    private static final long MASK16_13 = MASKS16[13];
    private static final long MASK16_14 = MASKS16[14];
    private static final long MASK16_15 = MASKS16[15];
    private static final long MASK32_1 = MASKS32[1];
    private static final long MASK32_2 = MASKS32[2];
    private static final long MASK32_3 = MASKS32[3];
    private static final long MASK32_4 = MASKS32[4];
    private static final long MASK32_5 = MASKS32[5];
    private static final long MASK32_6 = MASKS32[6];
    private static final long MASK32_7 = MASKS32[7];
    private static final long MASK32_8 = MASKS32[8];
    private static final long MASK32_9 = MASKS32[9];
    private static final long MASK32_10 = MASKS32[10];
    private static final long MASK32_11 = MASKS32[11];
    private static final long MASK32_12 = MASKS32[12];
    private static final long MASK32_13 = MASKS32[13];
    private static final long MASK32_14 = MASKS32[14];
    private static final long MASK32_15 = MASKS32[15];
    private static final long MASK32_17 = MASKS32[17];
    private static final long MASK32_18 = MASKS32[18];
    private static final long MASK32_19 = MASKS32[19];
    private static final long MASK32_20 = MASKS32[20];
    private static final long MASK32_21 = MASKS32[21];
    private static final long MASK32_22 = MASKS32[22];
    private static final long MASK32_23 = MASKS32[23];
    private static final long MASK32_24 = MASKS32[24];

    /**
     * Decode 128 integers into {@code longs}.
     */
    void decode(int bitsPerValue, long[] in, long[] longs) throws IOException {
        switch (bitsPerValue) {
            case 1:
                decode1(in, tmp, longs);
                expand8(longs);
                break;
            case 2:
                decode2(in, tmp, longs);
                expand8(longs);
                break;
            case 3:
                decode3(in, tmp, longs);
                expand8(longs);
                break;
            case 4:
                decode4(in, tmp, longs);
                expand8(longs);
                break;
            case 5:
                decode5(in, tmp, longs);
                expand8(longs);
                break;
            case 6:
                decode6(in, tmp, longs);
                expand8(longs);
                break;
            case 7:
                decode7(in, tmp, longs);
                expand8(longs);
                break;
            case 8:
                decode8(in, tmp, longs);
                expand8(longs);
                break;
            case 9:
                decode9(in, tmp, longs);
                expand16(longs);
                break;
            case 10:
                decode10(in, tmp, longs);
                expand16(longs);
                break;
            case 11:
                decode11(in, tmp, longs);
                expand16(longs);
                break;
            case 12:
                decode12(in, tmp, longs);
                expand16(longs);
                break;
            case 13:
                decode13(in, tmp, longs);
                expand16(longs);
                break;
            case 14:
                decode14(in, tmp, longs);
                expand16(longs);
                break;
            case 15:
                decode15(in, tmp, longs);
                expand16(longs);
                break;
            case 16:
                decode16(in, tmp, longs);
                expand16(longs);
                break;
            case 17:
                decode17(in, tmp, longs);
                expand32(longs);
                break;
            case 18:
                decode18(in, tmp, longs);
                expand32(longs);
                break;
            case 19:
                decode19(in, tmp, longs);
                expand32(longs);
                break;
            case 20:
                decode20(in, tmp, longs);
                expand32(longs);
                break;
            case 21:
                decode21(in, tmp, longs);
                expand32(longs);
                break;
            case 22:
                decode22(in, tmp, longs);
                expand32(longs);
                break;
            case 23:
                decode23(in, tmp, longs);
                expand32(longs);
                break;
            case 24:
                decode24(in, tmp, longs);
                expand32(longs);
                break;
            default:
                decodeSlow(bitsPerValue, in, tmp, longs);
                expand32(longs);
                break;
        }
    }

    /**
     * Decodes 128 integers into 64 {@code longs} such that each long contains two values, each
     * represented with 32 bits. Values [0..63] are encoded in the high-order bits of {@code longs}
     * [0..63], and values [64..127] are encoded in the low-order bits of {@code longs} [0..63]. This
     * representation may allow subsequent operations to be performed on two values at a time.
     */
    void decodeTo32(int bitsPerValue, long[] in, long[] longs) throws IOException {
        switch (bitsPerValue) {
            case 1:
                decode1(in, tmp, longs);
                expand8To32(longs);
                break;
            case 2:
                decode2(in, tmp, longs);
                expand8To32(longs);
                break;
            case 3:
                decode3(in, tmp, longs);
                expand8To32(longs);
                break;
            case 4:
                decode4(in, tmp, longs);
                expand8To32(longs);
                break;
            case 5:
                decode5(in, tmp, longs);
                expand8To32(longs);
                break;
            case 6:
                decode6(in, tmp, longs);
                expand8To32(longs);
                break;
            case 7:
                decode7(in, tmp, longs);
                expand8To32(longs);
                break;
            case 8:
                decode8(in, tmp, longs);
                expand8To32(longs);
                break;
            case 9:
                decode9(in, tmp, longs);
                expand16To32(longs);
                break;
            case 10:
                decode10(in, tmp, longs);
                expand16To32(longs);
                break;
            case 11:
                decode11(in, tmp, longs);
                expand16To32(longs);
                break;
            case 12:
                decode12(in, tmp, longs);
                expand16To32(longs);
                break;
            case 13:
                decode13(in, tmp, longs);
                expand16To32(longs);
                break;
            case 14:
                decode14(in, tmp, longs);
                expand16To32(longs);
                break;
            case 15:
                decode15(in, tmp, longs);
                expand16To32(longs);
                break;
            case 16:
                decode16(in, tmp, longs);
                expand16To32(longs);
                break;
            case 17:
                decode17(in, tmp, longs);
                break;
            case 18:
                decode18(in, tmp, longs);
                break;
            case 19:
                decode19(in, tmp, longs);
                break;
            case 20:
                decode20(in, tmp, longs);
                break;
            case 21:
                decode21(in, tmp, longs);
                break;
            case 22:
                decode22(in, tmp, longs);
                break;
            case 23:
                decode23(in, tmp, longs);
                break;
            case 24:
                decode24(in, tmp, longs);
                break;
            default:
                decodeSlow(bitsPerValue, in, tmp, longs);
                break;
        }
    }

    private static void decode1(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 2, longs, 0, 7, MASK8_1);
        shiftLongs(tmp, 2, longs, 2, 6, MASK8_1);
        shiftLongs(tmp, 2, longs, 4, 5, MASK8_1);
        shiftLongs(tmp, 2, longs, 6, 4, MASK8_1);
        shiftLongs(tmp, 2, longs, 8, 3, MASK8_1);
        shiftLongs(tmp, 2, longs, 10, 2, MASK8_1);
        shiftLongs(tmp, 2, longs, 12, 1, MASK8_1);
        shiftLongs(tmp, 2, longs, 14, 0, MASK8_1);
    }

    private static void decode2(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 4, longs, 0, 6, MASK8_2);
        shiftLongs(tmp, 4, longs, 4, 4, MASK8_2);
        shiftLongs(tmp, 4, longs, 8, 2, MASK8_2);
        shiftLongs(tmp, 4, longs, 12, 0, MASK8_2);
    }

    private static void decode3(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 6, longs, 0, 5, MASK8_3);
        shiftLongs(tmp, 6, longs, 6, 2, MASK8_3);
        for (int iter = 0, tmpIdx = 0, longsIdx = 12; iter < 2; ++iter, tmpIdx += 3, longsIdx += 2) {
            long l0 = (tmp[tmpIdx + 0] & MASK8_2) << 1;
            l0 |= (tmp[tmpIdx + 1] >>> 1) & MASK8_1;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 1] & MASK8_1) << 2;
            l1 |= (tmp[tmpIdx + 2] & MASK8_2) << 0;
            longs[longsIdx + 1] = l1;
        }
    }

    private static void decode4(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 8, longs, 0, 4, MASK8_4);
        shiftLongs(tmp, 8, longs, 8, 0, MASK8_4);
    }

    private static void decode5(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 10, longs, 0, 3, MASK8_5);
        for (int iter = 0, tmpIdx = 0, longsIdx = 10; iter < 2; ++iter, tmpIdx += 5, longsIdx += 3) {
            long l0 = (tmp[tmpIdx + 0] & MASK8_3) << 2;
            l0 |= (tmp[tmpIdx + 1] >>> 1) & MASK8_2;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 1] & MASK8_1) << 4;
            l1 |= (tmp[tmpIdx + 2] & MASK8_3) << 1;
            l1 |= (tmp[tmpIdx + 3] >>> 2) & MASK8_1;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 3] & MASK8_2) << 3;
            l2 |= (tmp[tmpIdx + 4] & MASK8_3) << 0;
            longs[longsIdx + 2] = l2;
        }
    }

    private static void decode6(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 12, longs, 0, 2, MASK8_6);
        shiftLongs(tmp, 12, tmp, 0, 0, MASK8_2);
        for (int iter = 0, tmpIdx = 0, longsIdx = 12; iter < 4; ++iter, tmpIdx += 3, longsIdx += 1) {
            long l0 = tmp[tmpIdx + 0] << 4;
            l0 |= tmp[tmpIdx + 1] << 2;
            l0 |= tmp[tmpIdx + 2] << 0;
            longs[longsIdx + 0] = l0;
        }
    }

    private static void decode7(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 14, longs, 0, 1, MASK8_7);
        shiftLongs(tmp, 14, tmp, 0, 0, MASK8_1);
        for (int iter = 0, tmpIdx = 0, longsIdx = 14; iter < 2; ++iter, tmpIdx += 7, longsIdx += 1) {
            long l0 = tmp[tmpIdx + 0] << 6;
            l0 |= tmp[tmpIdx + 1] << 5;
            l0 |= tmp[tmpIdx + 2] << 4;
            l0 |= tmp[tmpIdx + 3] << 3;
            l0 |= tmp[tmpIdx + 4] << 2;
            l0 |= tmp[tmpIdx + 5] << 1;
            l0 |= tmp[tmpIdx + 6] << 0;
            longs[longsIdx + 0] = l0;
        }
    }

    private static void decode8(long[] in, long[] tmp, long[] longs) throws IOException {
        System.arraycopy(in, 0, longs, 0, 16);
    }

    private static void decode9(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 18, longs, 0, 7, MASK16_9);
        for (int iter = 0, tmpIdx = 0, longsIdx = 18; iter < 2; ++iter, tmpIdx += 9, longsIdx += 7) {
            long l0 = (tmp[tmpIdx + 0] & MASK16_7) << 2;
            l0 |= (tmp[tmpIdx + 1] >>> 5) & MASK16_2;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 1] & MASK16_5) << 4;
            l1 |= (tmp[tmpIdx + 2] >>> 3) & MASK16_4;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 2] & MASK16_3) << 6;
            l2 |= (tmp[tmpIdx + 3] >>> 1) & MASK16_6;
            longs[longsIdx + 2] = l2;
            long l3 = (tmp[tmpIdx + 3] & MASK16_1) << 8;
            l3 |= (tmp[tmpIdx + 4] & MASK16_7) << 1;
            l3 |= (tmp[tmpIdx + 5] >>> 6) & MASK16_1;
            longs[longsIdx + 3] = l3;
            long l4 = (tmp[tmpIdx + 5] & MASK16_6) << 3;
            l4 |= (tmp[tmpIdx + 6] >>> 4) & MASK16_3;
            longs[longsIdx + 4] = l4;
            long l5 = (tmp[tmpIdx + 6] & MASK16_4) << 5;
            l5 |= (tmp[tmpIdx + 7] >>> 2) & MASK16_5;
            longs[longsIdx + 5] = l5;
            long l6 = (tmp[tmpIdx + 7] & MASK16_2) << 7;
            l6 |= (tmp[tmpIdx + 8] & MASK16_7) << 0;
            longs[longsIdx + 6] = l6;
        }
    }

    private static void decode10(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 20, longs, 0, 6, MASK16_10);
        for (int iter = 0, tmpIdx = 0, longsIdx = 20; iter < 4; ++iter, tmpIdx += 5, longsIdx += 3) {
            long l0 = (tmp[tmpIdx + 0] & MASK16_6) << 4;
            l0 |= (tmp[tmpIdx + 1] >>> 2) & MASK16_4;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 1] & MASK16_2) << 8;
            l1 |= (tmp[tmpIdx + 2] & MASK16_6) << 2;
            l1 |= (tmp[tmpIdx + 3] >>> 4) & MASK16_2;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 3] & MASK16_4) << 6;
            l2 |= (tmp[tmpIdx + 4] & MASK16_6) << 0;
            longs[longsIdx + 2] = l2;
        }
    }

    private static void decode11(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 22, longs, 0, 5, MASK16_11);
        for (int iter = 0, tmpIdx = 0, longsIdx = 22; iter < 2; ++iter, tmpIdx += 11, longsIdx += 5) {
            long l0 = (tmp[tmpIdx + 0] & MASK16_5) << 6;
            l0 |= (tmp[tmpIdx + 1] & MASK16_5) << 1;
            l0 |= (tmp[tmpIdx + 2] >>> 4) & MASK16_1;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 2] & MASK16_4) << 7;
            l1 |= (tmp[tmpIdx + 3] & MASK16_5) << 2;
            l1 |= (tmp[tmpIdx + 4] >>> 3) & MASK16_2;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 4] & MASK16_3) << 8;
            l2 |= (tmp[tmpIdx + 5] & MASK16_5) << 3;
            l2 |= (tmp[tmpIdx + 6] >>> 2) & MASK16_3;
            longs[longsIdx + 2] = l2;
            long l3 = (tmp[tmpIdx + 6] & MASK16_2) << 9;
            l3 |= (tmp[tmpIdx + 7] & MASK16_5) << 4;
            l3 |= (tmp[tmpIdx + 8] >>> 1) & MASK16_4;
            longs[longsIdx + 3] = l3;
            long l4 = (tmp[tmpIdx + 8] & MASK16_1) << 10;
            l4 |= (tmp[tmpIdx + 9] & MASK16_5) << 5;
            l4 |= (tmp[tmpIdx + 10] & MASK16_5) << 0;
            longs[longsIdx + 4] = l4;
        }
    }

    private static void decode12(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 24, longs, 0, 4, MASK16_12);
        shiftLongs(tmp, 24, tmp, 0, 0, MASK16_4);
        for (int iter = 0, tmpIdx = 0, longsIdx = 24; iter < 8; ++iter, tmpIdx += 3, longsIdx += 1) {
            long l0 = tmp[tmpIdx + 0] << 8;
            l0 |= tmp[tmpIdx + 1] << 4;
            l0 |= tmp[tmpIdx + 2] << 0;
            longs[longsIdx + 0] = l0;
        }
    }

    private static void decode13(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 26, longs, 0, 3, MASK16_13);
        for (int iter = 0, tmpIdx = 0, longsIdx = 26; iter < 2; ++iter, tmpIdx += 13, longsIdx += 3) {
            long l0 = (tmp[tmpIdx + 0] & MASK16_3) << 10;
            l0 |= (tmp[tmpIdx + 1] & MASK16_3) << 7;
            l0 |= (tmp[tmpIdx + 2] & MASK16_3) << 4;
            l0 |= (tmp[tmpIdx + 3] & MASK16_3) << 1;
            l0 |= (tmp[tmpIdx + 4] >>> 2) & MASK16_1;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 4] & MASK16_2) << 11;
            l1 |= (tmp[tmpIdx + 5] & MASK16_3) << 8;
            l1 |= (tmp[tmpIdx + 6] & MASK16_3) << 5;
            l1 |= (tmp[tmpIdx + 7] & MASK16_3) << 2;
            l1 |= (tmp[tmpIdx + 8] >>> 1) & MASK16_2;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 8] & MASK16_1) << 12;
            l2 |= (tmp[tmpIdx + 9] & MASK16_3) << 9;
            l2 |= (tmp[tmpIdx + 10] & MASK16_3) << 6;
            l2 |= (tmp[tmpIdx + 11] & MASK16_3) << 3;
            l2 |= (tmp[tmpIdx + 12] & MASK16_3) << 0;
            longs[longsIdx + 2] = l2;
        }
    }

    private static void decode14(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 28, longs, 0, 2, MASK16_14);
        shiftLongs(tmp, 28, tmp, 0, 0, MASK16_2);
        for (int iter = 0, tmpIdx = 0, longsIdx = 28; iter < 4; ++iter, tmpIdx += 7, longsIdx += 1) {
            long l0 = tmp[tmpIdx + 0] << 12;
            l0 |= tmp[tmpIdx + 1] << 10;
            l0 |= tmp[tmpIdx + 2] << 8;
            l0 |= tmp[tmpIdx + 3] << 6;
            l0 |= tmp[tmpIdx + 4] << 4;
            l0 |= tmp[tmpIdx + 5] << 2;
            l0 |= tmp[tmpIdx + 6] << 0;
            longs[longsIdx + 0] = l0;
        }
    }

    private static void decode15(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 30, longs, 0, 1, MASK16_15);
        shiftLongs(tmp, 30, tmp, 0, 0, MASK16_1);
        for (int iter = 0, tmpIdx = 0, longsIdx = 30; iter < 2; ++iter, tmpIdx += 15, longsIdx += 1) {
            long l0 = tmp[tmpIdx + 0] << 14;
            l0 |= tmp[tmpIdx + 1] << 13;
            l0 |= tmp[tmpIdx + 2] << 12;
            l0 |= tmp[tmpIdx + 3] << 11;
            l0 |= tmp[tmpIdx + 4] << 10;
            l0 |= tmp[tmpIdx + 5] << 9;
            l0 |= tmp[tmpIdx + 6] << 8;
            l0 |= tmp[tmpIdx + 7] << 7;
            l0 |= tmp[tmpIdx + 8] << 6;
            l0 |= tmp[tmpIdx + 9] << 5;
            l0 |= tmp[tmpIdx + 10] << 4;
            l0 |= tmp[tmpIdx + 11] << 3;
            l0 |= tmp[tmpIdx + 12] << 2;
            l0 |= tmp[tmpIdx + 13] << 1;
            l0 |= tmp[tmpIdx + 14] << 0;
            longs[longsIdx + 0] = l0;
        }
    }

    private static void decode16(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
    }

    private static void decode17(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 34, longs, 0, 15, MASK32_17);
        for (int iter = 0, tmpIdx = 0, longsIdx = 34; iter < 2; ++iter, tmpIdx += 17, longsIdx += 15) {
            long l0 = (tmp[tmpIdx + 0] & MASK32_15) << 2;
            l0 |= (tmp[tmpIdx + 1] >>> 13) & MASK32_2;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 1] & MASK32_13) << 4;
            l1 |= (tmp[tmpIdx + 2] >>> 11) & MASK32_4;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 2] & MASK32_11) << 6;
            l2 |= (tmp[tmpIdx + 3] >>> 9) & MASK32_6;
            longs[longsIdx + 2] = l2;
            long l3 = (tmp[tmpIdx + 3] & MASK32_9) << 8;
            l3 |= (tmp[tmpIdx + 4] >>> 7) & MASK32_8;
            longs[longsIdx + 3] = l3;
            long l4 = (tmp[tmpIdx + 4] & MASK32_7) << 10;
            l4 |= (tmp[tmpIdx + 5] >>> 5) & MASK32_10;
            longs[longsIdx + 4] = l4;
            long l5 = (tmp[tmpIdx + 5] & MASK32_5) << 12;
            l5 |= (tmp[tmpIdx + 6] >>> 3) & MASK32_12;
            longs[longsIdx + 5] = l5;
            long l6 = (tmp[tmpIdx + 6] & MASK32_3) << 14;
            l6 |= (tmp[tmpIdx + 7] >>> 1) & MASK32_14;
            longs[longsIdx + 6] = l6;
            long l7 = (tmp[tmpIdx + 7] & MASK32_1) << 16;
            l7 |= (tmp[tmpIdx + 8] & MASK32_15) << 1;
            l7 |= (tmp[tmpIdx + 9] >>> 14) & MASK32_1;
            longs[longsIdx + 7] = l7;
            long l8 = (tmp[tmpIdx + 9] & MASK32_14) << 3;
            l8 |= (tmp[tmpIdx + 10] >>> 12) & MASK32_3;
            longs[longsIdx + 8] = l8;
            long l9 = (tmp[tmpIdx + 10] & MASK32_12) << 5;
            l9 |= (tmp[tmpIdx + 11] >>> 10) & MASK32_5;
            longs[longsIdx + 9] = l9;
            long l10 = (tmp[tmpIdx + 11] & MASK32_10) << 7;
            l10 |= (tmp[tmpIdx + 12] >>> 8) & MASK32_7;
            longs[longsIdx + 10] = l10;
            long l11 = (tmp[tmpIdx + 12] & MASK32_8) << 9;
            l11 |= (tmp[tmpIdx + 13] >>> 6) & MASK32_9;
            longs[longsIdx + 11] = l11;
            long l12 = (tmp[tmpIdx + 13] & MASK32_6) << 11;
            l12 |= (tmp[tmpIdx + 14] >>> 4) & MASK32_11;
            longs[longsIdx + 12] = l12;
            long l13 = (tmp[tmpIdx + 14] & MASK32_4) << 13;
            l13 |= (tmp[tmpIdx + 15] >>> 2) & MASK32_13;
            longs[longsIdx + 13] = l13;
            long l14 = (tmp[tmpIdx + 15] & MASK32_2) << 15;
            l14 |= (tmp[tmpIdx + 16] & MASK32_15) << 0;
            longs[longsIdx + 14] = l14;
        }
    }

    private static void decode18(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 36, longs, 0, 14, MASK32_18);
        for (int iter = 0, tmpIdx = 0, longsIdx = 36; iter < 4; ++iter, tmpIdx += 9, longsIdx += 7) {
            long l0 = (tmp[tmpIdx + 0] & MASK32_14) << 4;
            l0 |= (tmp[tmpIdx + 1] >>> 10) & MASK32_4;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 1] & MASK32_10) << 8;
            l1 |= (tmp[tmpIdx + 2] >>> 6) & MASK32_8;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 2] & MASK32_6) << 12;
            l2 |= (tmp[tmpIdx + 3] >>> 2) & MASK32_12;
            longs[longsIdx + 2] = l2;
            long l3 = (tmp[tmpIdx + 3] & MASK32_2) << 16;
            l3 |= (tmp[tmpIdx + 4] & MASK32_14) << 2;
            l3 |= (tmp[tmpIdx + 5] >>> 12) & MASK32_2;
            longs[longsIdx + 3] = l3;
            long l4 = (tmp[tmpIdx + 5] & MASK32_12) << 6;
            l4 |= (tmp[tmpIdx + 6] >>> 8) & MASK32_6;
            longs[longsIdx + 4] = l4;
            long l5 = (tmp[tmpIdx + 6] & MASK32_8) << 10;
            l5 |= (tmp[tmpIdx + 7] >>> 4) & MASK32_10;
            longs[longsIdx + 5] = l5;
            long l6 = (tmp[tmpIdx + 7] & MASK32_4) << 14;
            l6 |= (tmp[tmpIdx + 8] & MASK32_14) << 0;
            longs[longsIdx + 6] = l6;
        }
    }

    private static void decode19(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 38, longs, 0, 13, MASK32_19);
        for (int iter = 0, tmpIdx = 0, longsIdx = 38; iter < 2; ++iter, tmpIdx += 19, longsIdx += 13) {
            long l0 = (tmp[tmpIdx + 0] & MASK32_13) << 6;
            l0 |= (tmp[tmpIdx + 1] >>> 7) & MASK32_6;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 1] & MASK32_7) << 12;
            l1 |= (tmp[tmpIdx + 2] >>> 1) & MASK32_12;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 2] & MASK32_1) << 18;
            l2 |= (tmp[tmpIdx + 3] & MASK32_13) << 5;
            l2 |= (tmp[tmpIdx + 4] >>> 8) & MASK32_5;
            longs[longsIdx + 2] = l2;
            long l3 = (tmp[tmpIdx + 4] & MASK32_8) << 11;
            l3 |= (tmp[tmpIdx + 5] >>> 2) & MASK32_11;
            longs[longsIdx + 3] = l3;
            long l4 = (tmp[tmpIdx + 5] & MASK32_2) << 17;
            l4 |= (tmp[tmpIdx + 6] & MASK32_13) << 4;
            l4 |= (tmp[tmpIdx + 7] >>> 9) & MASK32_4;
            longs[longsIdx + 4] = l4;
            long l5 = (tmp[tmpIdx + 7] & MASK32_9) << 10;
            l5 |= (tmp[tmpIdx + 8] >>> 3) & MASK32_10;
            longs[longsIdx + 5] = l5;
            long l6 = (tmp[tmpIdx + 8] & MASK32_3) << 16;
            l6 |= (tmp[tmpIdx + 9] & MASK32_13) << 3;
            l6 |= (tmp[tmpIdx + 10] >>> 10) & MASK32_3;
            longs[longsIdx + 6] = l6;
            long l7 = (tmp[tmpIdx + 10] & MASK32_10) << 9;
            l7 |= (tmp[tmpIdx + 11] >>> 4) & MASK32_9;
            longs[longsIdx + 7] = l7;
            long l8 = (tmp[tmpIdx + 11] & MASK32_4) << 15;
            l8 |= (tmp[tmpIdx + 12] & MASK32_13) << 2;
            l8 |= (tmp[tmpIdx + 13] >>> 11) & MASK32_2;
            longs[longsIdx + 8] = l8;
            long l9 = (tmp[tmpIdx + 13] & MASK32_11) << 8;
            l9 |= (tmp[tmpIdx + 14] >>> 5) & MASK32_8;
            longs[longsIdx + 9] = l9;
            long l10 = (tmp[tmpIdx + 14] & MASK32_5) << 14;
            l10 |= (tmp[tmpIdx + 15] & MASK32_13) << 1;
            l10 |= (tmp[tmpIdx + 16] >>> 12) & MASK32_1;
            longs[longsIdx + 10] = l10;
            long l11 = (tmp[tmpIdx + 16] & MASK32_12) << 7;
            l11 |= (tmp[tmpIdx + 17] >>> 6) & MASK32_7;
            longs[longsIdx + 11] = l11;
            long l12 = (tmp[tmpIdx + 17] & MASK32_6) << 13;
            l12 |= (tmp[tmpIdx + 18] & MASK32_13) << 0;
            longs[longsIdx + 12] = l12;
        }
    }

    private static void decode20(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 40, longs, 0, 12, MASK32_20);
        for (int iter = 0, tmpIdx = 0, longsIdx = 40; iter < 8; ++iter, tmpIdx += 5, longsIdx += 3) {
            long l0 = (tmp[tmpIdx + 0] & MASK32_12) << 8;
            l0 |= (tmp[tmpIdx + 1] >>> 4) & MASK32_8;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 1] & MASK32_4) << 16;
            l1 |= (tmp[tmpIdx + 2] & MASK32_12) << 4;
            l1 |= (tmp[tmpIdx + 3] >>> 8) & MASK32_4;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 3] & MASK32_8) << 12;
            l2 |= (tmp[tmpIdx + 4] & MASK32_12) << 0;
            longs[longsIdx + 2] = l2;
        }
    }

    private static void decode21(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 42, longs, 0, 11, MASK32_21);
        for (int iter = 0, tmpIdx = 0, longsIdx = 42; iter < 2; ++iter, tmpIdx += 21, longsIdx += 11) {
            long l0 = (tmp[tmpIdx + 0] & MASK32_11) << 10;
            l0 |= (tmp[tmpIdx + 1] >>> 1) & MASK32_10;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 1] & MASK32_1) << 20;
            l1 |= (tmp[tmpIdx + 2] & MASK32_11) << 9;
            l1 |= (tmp[tmpIdx + 3] >>> 2) & MASK32_9;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 3] & MASK32_2) << 19;
            l2 |= (tmp[tmpIdx + 4] & MASK32_11) << 8;
            l2 |= (tmp[tmpIdx + 5] >>> 3) & MASK32_8;
            longs[longsIdx + 2] = l2;
            long l3 = (tmp[tmpIdx + 5] & MASK32_3) << 18;
            l3 |= (tmp[tmpIdx + 6] & MASK32_11) << 7;
            l3 |= (tmp[tmpIdx + 7] >>> 4) & MASK32_7;
            longs[longsIdx + 3] = l3;
            long l4 = (tmp[tmpIdx + 7] & MASK32_4) << 17;
            l4 |= (tmp[tmpIdx + 8] & MASK32_11) << 6;
            l4 |= (tmp[tmpIdx + 9] >>> 5) & MASK32_6;
            longs[longsIdx + 4] = l4;
            long l5 = (tmp[tmpIdx + 9] & MASK32_5) << 16;
            l5 |= (tmp[tmpIdx + 10] & MASK32_11) << 5;
            l5 |= (tmp[tmpIdx + 11] >>> 6) & MASK32_5;
            longs[longsIdx + 5] = l5;
            long l6 = (tmp[tmpIdx + 11] & MASK32_6) << 15;
            l6 |= (tmp[tmpIdx + 12] & MASK32_11) << 4;
            l6 |= (tmp[tmpIdx + 13] >>> 7) & MASK32_4;
            longs[longsIdx + 6] = l6;
            long l7 = (tmp[tmpIdx + 13] & MASK32_7) << 14;
            l7 |= (tmp[tmpIdx + 14] & MASK32_11) << 3;
            l7 |= (tmp[tmpIdx + 15] >>> 8) & MASK32_3;
            longs[longsIdx + 7] = l7;
            long l8 = (tmp[tmpIdx + 15] & MASK32_8) << 13;
            l8 |= (tmp[tmpIdx + 16] & MASK32_11) << 2;
            l8 |= (tmp[tmpIdx + 17] >>> 9) & MASK32_2;
            longs[longsIdx + 8] = l8;
            long l9 = (tmp[tmpIdx + 17] & MASK32_9) << 12;
            l9 |= (tmp[tmpIdx + 18] & MASK32_11) << 1;
            l9 |= (tmp[tmpIdx + 19] >>> 10) & MASK32_1;
            longs[longsIdx + 9] = l9;
            long l10 = (tmp[tmpIdx + 19] & MASK32_10) << 11;
            l10 |= (tmp[tmpIdx + 20] & MASK32_11) << 0;
            longs[longsIdx + 10] = l10;
        }
    }

    private static void decode22(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 44, longs, 0, 10, MASK32_22);
        for (int iter = 0, tmpIdx = 0, longsIdx = 44; iter < 4; ++iter, tmpIdx += 11, longsIdx += 5) {
            long l0 = (tmp[tmpIdx + 0] & MASK32_10) << 12;
            l0 |= (tmp[tmpIdx + 1] & MASK32_10) << 2;
            l0 |= (tmp[tmpIdx + 2] >>> 8) & MASK32_2;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 2] & MASK32_8) << 14;
            l1 |= (tmp[tmpIdx + 3] & MASK32_10) << 4;
            l1 |= (tmp[tmpIdx + 4] >>> 6) & MASK32_4;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 4] & MASK32_6) << 16;
            l2 |= (tmp[tmpIdx + 5] & MASK32_10) << 6;
            l2 |= (tmp[tmpIdx + 6] >>> 4) & MASK32_6;
            longs[longsIdx + 2] = l2;
            long l3 = (tmp[tmpIdx + 6] & MASK32_4) << 18;
            l3 |= (tmp[tmpIdx + 7] & MASK32_10) << 8;
            l3 |= (tmp[tmpIdx + 8] >>> 2) & MASK32_8;
            longs[longsIdx + 3] = l3;
            long l4 = (tmp[tmpIdx + 8] & MASK32_2) << 20;
            l4 |= (tmp[tmpIdx + 9] & MASK32_10) << 10;
            l4 |= (tmp[tmpIdx + 10] & MASK32_10) << 0;
            longs[longsIdx + 4] = l4;
        }
    }

    private static void decode23(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 46, longs, 0, 9, MASK32_23);
        for (int iter = 0, tmpIdx = 0, longsIdx = 46; iter < 2; ++iter, tmpIdx += 23, longsIdx += 9) {
            long l0 = (tmp[tmpIdx + 0] & MASK32_9) << 14;
            l0 |= (tmp[tmpIdx + 1] & MASK32_9) << 5;
            l0 |= (tmp[tmpIdx + 2] >>> 4) & MASK32_5;
            longs[longsIdx + 0] = l0;
            long l1 = (tmp[tmpIdx + 2] & MASK32_4) << 19;
            l1 |= (tmp[tmpIdx + 3] & MASK32_9) << 10;
            l1 |= (tmp[tmpIdx + 4] & MASK32_9) << 1;
            l1 |= (tmp[tmpIdx + 5] >>> 8) & MASK32_1;
            longs[longsIdx + 1] = l1;
            long l2 = (tmp[tmpIdx + 5] & MASK32_8) << 15;
            l2 |= (tmp[tmpIdx + 6] & MASK32_9) << 6;
            l2 |= (tmp[tmpIdx + 7] >>> 3) & MASK32_6;
            longs[longsIdx + 2] = l2;
            long l3 = (tmp[tmpIdx + 7] & MASK32_3) << 20;
            l3 |= (tmp[tmpIdx + 8] & MASK32_9) << 11;
            l3 |= (tmp[tmpIdx + 9] & MASK32_9) << 2;
            l3 |= (tmp[tmpIdx + 10] >>> 7) & MASK32_2;
            longs[longsIdx + 3] = l3;
            long l4 = (tmp[tmpIdx + 10] & MASK32_7) << 16;
            l4 |= (tmp[tmpIdx + 11] & MASK32_9) << 7;
            l4 |= (tmp[tmpIdx + 12] >>> 2) & MASK32_7;
            longs[longsIdx + 4] = l4;
            long l5 = (tmp[tmpIdx + 12] & MASK32_2) << 21;
            l5 |= (tmp[tmpIdx + 13] & MASK32_9) << 12;
            l5 |= (tmp[tmpIdx + 14] & MASK32_9) << 3;
            l5 |= (tmp[tmpIdx + 15] >>> 6) & MASK32_3;
            longs[longsIdx + 5] = l5;
            long l6 = (tmp[tmpIdx + 15] & MASK32_6) << 17;
            l6 |= (tmp[tmpIdx + 16] & MASK32_9) << 8;
            l6 |= (tmp[tmpIdx + 17] >>> 1) & MASK32_8;
            longs[longsIdx + 6] = l6;
            long l7 = (tmp[tmpIdx + 17] & MASK32_1) << 22;
            l7 |= (tmp[tmpIdx + 18] & MASK32_9) << 13;
            l7 |= (tmp[tmpIdx + 19] & MASK32_9) << 4;
            l7 |= (tmp[tmpIdx + 20] >>> 5) & MASK32_4;
            longs[longsIdx + 7] = l7;
            long l8 = (tmp[tmpIdx + 20] & MASK32_5) << 18;
            l8 |= (tmp[tmpIdx + 21] & MASK32_9) << 9;
            l8 |= (tmp[tmpIdx + 22] & MASK32_9) << 0;
            longs[longsIdx + 8] = l8;
        }
    }

    private static void decode24(long[] in, long[] tmp, long[] longs) throws IOException {
        tmp = in;
        shiftLongs(tmp, 48, longs, 0, 8, MASK32_24);
        shiftLongs(tmp, 48, tmp, 0, 0, MASK32_8);
        for (int iter = 0, tmpIdx = 0, longsIdx = 48; iter < 16; ++iter, tmpIdx += 3, longsIdx += 1) {
            long l0 = tmp[tmpIdx + 0] << 16;
            l0 |= tmp[tmpIdx + 1] << 8;
            l0 |= tmp[tmpIdx + 2] << 0;
            longs[longsIdx + 0] = l0;
        }
    }
}
