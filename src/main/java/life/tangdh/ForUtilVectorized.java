package life.tangdh;

import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.io.IOException;

public class ForUtilVectorized {

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
        VectorSpecies<Long> SPECIES = LongVector.SPECIES_PREFERRED;
        int loopBound = 16 / SPECIES.length();
        for (int i = 0; i < loopBound; i++) {
            LongVector l = LongVector.fromArray(SPECIES, arr, i * SPECIES.length());

            LongVector shifted0 = l.lanewise(VectorOperators.LSHR, (long) 0 * 8).and(0xFFL);
            shifted0.intoArray(arr, (7 - 0) * 16 + i * SPECIES.length());

            LongVector shifted1 = l.lanewise(VectorOperators.LSHR, (long) 1 * 8).and(0xFFL);
            shifted1.intoArray(arr, (7 - 1) * 16 + i * SPECIES.length());

            LongVector shifted2 = l.lanewise(VectorOperators.LSHR, (long) 2 * 8).and(0xFFL);
            shifted2.intoArray(arr, (7 - 2) * 16 + i * SPECIES.length());

            LongVector shifted3 = l.lanewise(VectorOperators.LSHR, (long) 3 * 8).and(0xFFL);
            shifted3.intoArray(arr, (7 - 3) * 16 + i * SPECIES.length());

            LongVector shifted4 = l.lanewise(VectorOperators.LSHR, (long) 4 * 8).and(0xFFL);
            shifted4.intoArray(arr, (7 - 4) * 16 + i * SPECIES.length());

            LongVector shifted5 = l.lanewise(VectorOperators.LSHR, (long) 5 * 8).and(0xFFL);
            shifted5.intoArray(arr, (7 - 5) * 16 + i * SPECIES.length());

            LongVector shifted6 = l.lanewise(VectorOperators.LSHR, (long) 6 * 8).and(0xFFL);
            shifted6.intoArray(arr, (7 - 6) * 16 + i * SPECIES.length());

            LongVector shifted7 = l.lanewise(VectorOperators.LSHR, (long) 7 * 8).and(0xFFL);
            shifted7.intoArray(arr, (7 - 7) * 16 + i * SPECIES.length());
        }
    }

    public static void expand(long[] arr, int bpv) {
        VectorSpecies<Long> SPECIES = LongVector.SPECIES_PREFERRED;
        int loopBound = (2 * bpv) / SPECIES.length();
        long mask = (1L << bpv) - 1;
        for (int i = 0; i < loopBound; i++) {
            LongVector l = LongVector.fromArray(SPECIES, arr, i * SPECIES.length());

            for (int shift = 0 ; shift * bpv < 64 ; shift++) {
                LongVector shifted = l.lanewise(VectorOperators.LSHR, (long) shift * bpv).and(mask);
                shifted.intoArray(arr, ((64 / bpv) - shift - 1) * (2 * bpv) + i * SPECIES.length());
            }
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
        VectorSpecies<Long> SPECIES = LongVector.SPECIES_PREFERRED;
        int loopBound = 16 - (16 % SPECIES.length());

        for (int i = 0; i < loopBound; i += SPECIES.length()) {
            LongVector result = LongVector.zero(SPECIES);

            LongVector shifted56 = LongVector.fromArray(SPECIES, arr, i).lanewise(VectorOperators.LSHL, 56);
            LongVector shifted48 = LongVector.fromArray(SPECIES, arr, i + 16).lanewise(VectorOperators.LSHL, 48);
            LongVector shifted40 = LongVector.fromArray(SPECIES, arr, i + 32).lanewise(VectorOperators.LSHL, 40);
            LongVector shifted32 = LongVector.fromArray(SPECIES, arr, i + 48).lanewise(VectorOperators.LSHL, 32);
            LongVector shifted24 = LongVector.fromArray(SPECIES, arr, i + 64).lanewise(VectorOperators.LSHL, 24);
            LongVector shifted16 = LongVector.fromArray(SPECIES, arr, i + 80).lanewise(VectorOperators.LSHL, 16);
            LongVector shifted8  = LongVector.fromArray(SPECIES, arr, i + 96).lanewise(VectorOperators.LSHL, 8);
            LongVector shifted0  = LongVector.fromArray(SPECIES, arr, i + 112);
            result = result.or(shifted56)
                    .or(shifted48)
                    .or(shifted40)
                    .or(shifted32)
                    .or(shifted24)
                    .or(shifted16)
                    .or(shifted8)
                    .or(shifted0);

            result.intoArray(arr, i);
        }

        // Handle remaining elements using scalar operations
        for (int i = loopBound; i < 16; ++i) {
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

//        for (int i = 0; i < 8; i++) {
//            long SHIFT =  (64 - (long) (i + 1) * 8);
//            for (int j = 0; j < loopBound; j++) {
//                int offset = i * 16 + j * SPECIES.length();
//                LongVector l = LongVector.fromArray(SPECIES, arr, offset);
//                LongVector shifted = l.lanewise(VectorOperators.LSHL, SHIFT);
//                if (i == 0) {
//                    shifted.intoArray(arr, offset % (16));
//                } else {
//                    LongVector result = shifted.or(LongVector.fromArray(SPECIES, arr, offset % (16)));
//                    result.intoArray(arr, offset % (16));
//                }
//            }
//        }
    }

    private static void collapse(long[] arr, int bpv) {
       VectorSpecies<Long> SPECIES = LongVector.SPECIES_PREFERRED;
       int loopBound =  2 * bpv / SPECIES.length();

         for (int i = 0; i < 64 / bpv; i++) {
            long SHIFT =  (64 - (long) (i + 1) * bpv);
            for (int j = 0; j < loopBound; j++) {
                int offset = i * (2 * bpv) + j * SPECIES.length();
                LongVector l = LongVector.fromArray(SPECIES, arr, offset);
                LongVector shifted = l.lanewise(VectorOperators.LSHL, SHIFT);
                if (i == 0) {
                    shifted.intoArray(arr, offset % (2 * bpv));
                } else {
                    LongVector result = shifted.or(LongVector.fromArray(SPECIES, arr, offset % (2 * bpv)));
                    result.intoArray(arr, offset % (2 * bpv));
                }
            }
         }
    }

    public static void expand16(long[] arr) {
        expand(arr, 16);
    }

    private static void expand16To32(long[] arr) {
        for (int i = 0; i < 32; ++i) {
            long l = arr[i];
            arr[i] = (l >>> 16) & 0x0000FFFF0000FFFFL;
            arr[32 + i] = l & 0x0000FFFF0000FFFFL;
        }
    }

    public static void collapse16(long[] arr) {
        VectorSpecies<Long> SPECIES = LongVector.SPECIES_PREFERRED;
        int loopBound = 32 - (32 % SPECIES.length());

        for (int i = 0; i < loopBound; i += SPECIES.length()) {
            LongVector result = LongVector.zero(SPECIES);

            LongVector shifted48 = LongVector.fromArray(SPECIES, arr, i).lanewise(VectorOperators.LSHL, 48);
            LongVector shifted32 = LongVector.fromArray(SPECIES, arr, i + 32).lanewise(VectorOperators.LSHL, 32);
            LongVector shifted16 = LongVector.fromArray(SPECIES, arr, i + 64).lanewise(VectorOperators.LSHL, 16);
            LongVector shifted0  = LongVector.fromArray(SPECIES, arr, i + 96);
            result = result
                    .or(shifted48)
                    .or(shifted32)
                    .or(shifted16)
                    .or(shifted0);

            result.intoArray(arr, i);
        }

        // Handle remaining elements using scalar operations
        for (int i = loopBound; i < 16; ++i) {
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

    public static void expand32(long[] arr) {
        expand(arr, 32);
    }

    public static void collapse32(long[] arr) {
        VectorSpecies<Long> SPECIES = LongVector.SPECIES_PREFERRED;
        int loopBound = 64 - (64 % SPECIES.length());

        for (int i = 0; i < loopBound; i += SPECIES.length()) {
            LongVector result = LongVector.zero(SPECIES);

            LongVector shifted32= LongVector.fromArray(SPECIES, arr, i).lanewise(VectorOperators.LSHL, 32);
            LongVector shifted0  = LongVector.fromArray(SPECIES, arr, i + 64);
            result = result
                    .or(shifted32)
                    .or(shifted0);

            result.intoArray(arr, i);
        }

        // Handle remaining elements using scalar operations
        for (int i = loopBound; i < 16; ++i) {
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

    private final long[] tmp = new long[BLOCK_SIZE / 2];

    /**
     * Encode 128 integers from {@code longs} into {@code out}.
     */
    public void encode(long[] longs, int bitsPerValue, long[] out) throws IOException {
        final int nextPrimitive;
        final int numLongs;
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

        VectorSpecies<Long> SPECIES = LongVector.SPECIES_PREFERRED;

        final int numLongsPerShift = bitsPerValue * 2;
        int idx = 0;
        int shift = nextPrimitive - bitsPerValue;

        int i = 0;
        for (i = 0; i < numLongsPerShift / SPECIES.length() ; ++i) {
            LongVector l = LongVector.fromArray(SPECIES, longs, i * SPECIES.length());
            LongVector shifted = l.lanewise(VectorOperators.LSHL,  shift);
            shifted.intoArray(tmp, i * SPECIES.length());
            idx += SPECIES.length();
        }
        i = i * SPECIES.length();
        for(; i  < numLongsPerShift; ++i) {
            tmp[i] = longs[idx++] << shift;
        }

        for (shift = shift - bitsPerValue; shift >= 0; shift -= bitsPerValue) {
            for (i = 0; i < numLongsPerShift; ++i) {
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
            // can't set more value in it
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

        for (i = 0; i < numLongsPerShift; ++i) {
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
//        in.readLongs(tmp, 0, numLongs);
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
//        in.readLongs(tmp, 0, 2);
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
//        in.readLongs(tmp, 0, 4);
        shiftLongs(tmp, 4, longs, 0, 6, MASK8_2);
        shiftLongs(tmp, 4, longs, 4, 4, MASK8_2);
        shiftLongs(tmp, 4, longs, 8, 2, MASK8_2);
        shiftLongs(tmp, 4, longs, 12, 0, MASK8_2);
    }

    private static void decode3(long[] in, long[] tmp, long[] longs) throws IOException {
//        in.readLongs(tmp, 0, 6);
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
//        in.readLongs(tmp, 0, 8);
        shiftLongs(tmp, 8, longs, 0, 4, MASK8_4);
        shiftLongs(tmp, 8, longs, 8, 0, MASK8_4);
    }

    private static void decode5(long[] in, long[] tmp, long[] longs) throws IOException {
//        in.readLongs(tmp, 0, 10);
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
//        in.readLongs(tmp, 0, 12);
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
//        in.readLongs(tmp, 0, 14);
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
//        in.readLongs(longs, 0, 16);
    }

    private static void decode9(long[] in, long[] tmp, long[] longs) throws IOException {
//        in.readLongs(tmp, 0, 18);
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
//        in.readLongs(tmp, 0, 20);
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
//        in.readLongs(tmp, 0, 22);
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
//        in.readLongs(tmp, 0, 24);
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
//        in.readLongs(tmp, 0, 26);
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
//        in.readLongs(tmp, 0, 28);
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
//        in.readLongs(tmp, 0, 30);
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
//        in.readLongs(longs, 0, 32);
    }

    private static void decode17(long[] in, long[] tmp, long[] longs) throws IOException {
//        in.readLongs(tmp, 0, 34);
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
//        in.readLongs(tmp, 0, 36);
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
//        in.readLongs(tmp, 0, 38);
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
//        in.readLongs(tmp, 0, 40);
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
//        in.readLongs(tmp, 0, 42);
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
//        in.readLongs(tmp, 0, 44);
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
//        in.readLongs(tmp, 0, 46);
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
//        in.readLongs(tmp, 0, 48);
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

