package life.tangdh;

import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.io.IOException;

public class ForUtilVectorized {

    static final int BLOCK_SIZE = 128;
    private static final int BLOCK_SIZE_LOG2 = 7;

    private static final VectorSpecies<Long> SPECIES = LongVector.SPECIES_128;


    private static final VectorSpecies<Long> NORMAL = LongVector.SPECIES_256;

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

            for (int shift = 0; shift * bpv < 64; shift++) {
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
            LongVector shifted8 = LongVector.fromArray(SPECIES, arr, i + 96).lanewise(VectorOperators.LSHL, 8);
            LongVector shifted0 = LongVector.fromArray(SPECIES, arr, i + 112);
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
        int loopBound = 2 * bpv / SPECIES.length();

        for (int i = 0; i < 64 / bpv; i++) {
            long SHIFT = (64 - (long) (i + 1) * bpv);
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
            LongVector shifted0 = LongVector.fromArray(SPECIES, arr, i + 96);
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

            LongVector shifted32 = LongVector.fromArray(SPECIES, arr, i).lanewise(VectorOperators.LSHL, 32);
            LongVector shifted0 = LongVector.fromArray(SPECIES, arr, i + 64);
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

    private static final long[] tmp = new long[BLOCK_SIZE / 2];

    private static void encode1(long[] input, int bitsPerValue, long[] out) {
        LongVector outputVector = SPECIES.zero().reinterpretAsLongs();
        long mask = (1L << 1) - 1;

        LongVector inputVector = LongVector.fromArray(SPECIES, input, 0).and(mask);
        outputVector = inputVector.lanewise(VectorOperators.LSHL, 63);
        inputVector = LongVector.fromArray(SPECIES, input, 2).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 4).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 61).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 6).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 60).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 8).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 59).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 10).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 58).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 12).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 57).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 14).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 56).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 16).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 55).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 18).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 54).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 20).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 53).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 22).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 52).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 24).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 51).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 26).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 50).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 28).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 49).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 30).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 48).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 32).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 47).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 34).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 46).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 36).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 45).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 38).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 44).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 40).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 43).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 42).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 42).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 44).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 41).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 46).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 40).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 48).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 39).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 50).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 38).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 52).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 37).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 54).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 36).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 56).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 35).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 58).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 34).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 60).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 33).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 62).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 32).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 64).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 31).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 66).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 30).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 68).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 29).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 70).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 28).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 72).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 27).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 74).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 26).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 76).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 25).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 78).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 24).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 80).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 23).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 82).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 22).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 84).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 21).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 86).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 20).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 88).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 19).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 90).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 18).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 92).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 17).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 94).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 16).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 96).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 15).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 98).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 14).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 100).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 13).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 102).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 12).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 104).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 11).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 106).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 10).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 108).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 9).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 110).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 8).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 112).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 7).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 114).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 6).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 116).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 5).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 118).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 4).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 120).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 3).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 122).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 2).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 124).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 1).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 126).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 0).or(outputVector);
        outputVector.intoArray(out, 0);

    }

    private static void encode2(long[] input, int bitsPerValue, long[] out) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 2) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62);
        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 2).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 4).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 6).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 8).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 10).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 12).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 14).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 16).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 18).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 20).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 22).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 24).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 26).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 28).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 30).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 32).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 34).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 36).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 38).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 40).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 42).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 44).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 46).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 48).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 50).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 52).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 54).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 56).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 58).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 60).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62 - 62).or(outputVector);
        outputVector.intoArray(out, 0);
    }

    private static void encode3(long[] input, int bitsPerValue, long[] out) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 3) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 61));

        inputVector = LongVector.fromArray(NORMAL, input, 6).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 58));


        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 53));


        inputVector = LongVector.fromArray(NORMAL, input, 22).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 50));


        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 45));


        inputVector = LongVector.fromArray(NORMAL, input, 38).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 42));


        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 37));


        inputVector = LongVector.fromArray(NORMAL, input, 54).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 29));


        inputVector = LongVector.fromArray(NORMAL, input, 70).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 26));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 21));


        inputVector = LongVector.fromArray(NORMAL, input, 86).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        inputVector = LongVector.fromArray(NORMAL, input, 102).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        inputVector = LongVector.fromArray(NORMAL, input, 118).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 0);
        out[4] =

                (input[4] << 61)

                        |
                        (input[10] << 58)

                        |
                        (input[20] << 53)

                        |
                        (input[26] << 50)

                        |
                        (input[36] << 45)

                        |
                        (input[42] << 42)

                        |
                        (input[52] << 37)

                        |
                        (input[58] << 34)

                        |
                        (input[68] << 29)

                        |
                        (input[74] << 26)

                        |
                        (input[84] << 21)

                        |
                        (input[90] << 18)

                        |
                        (input[100] << 13)

                        |
                        (input[106] << 10)

                        |
                        (input[116] << 5)

                        |
                        (input[122] << 2)

        ;
        out[5] =

                (input[5] << 61)

                        |
                        (input[11] << 58)

                        |
                        (input[21] << 53)

                        |
                        (input[27] << 50)

                        |
                        (input[37] << 45)

                        |
                        (input[43] << 42)

                        |
                        (input[53] << 37)

                        |
                        (input[59] << 34)

                        |
                        (input[69] << 29)

                        |
                        (input[75] << 26)

                        |
                        (input[85] << 21)

                        |
                        (input[91] << 18)

                        |
                        (input[101] << 13)

                        |
                        (input[107] << 10)

                        |
                        (input[117] << 5)

                        |
                        (input[123] << 2)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 56));


        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 48));



        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 40));



        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 24));



        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));



        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 12);

        final int remainingBitsPerLong = 2;
        final long maskRemainingBitsPerLong = MASKS8[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 12;
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


    private static void encode5(long[] input, int bitsPerValue, long[] out) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 5) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 59));

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 51));


        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 43));


        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 35));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 27));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 19));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 59));

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 51));


        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 43));


        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 35));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 27));


        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 19));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 4);
        out[8] =

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

        ;
        out[9] =

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

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 10).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 56));


        inputVector = LongVector.fromArray(NORMAL, input, 26).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 48));



        inputVector = LongVector.fromArray(NORMAL, input, 42).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 40));



        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 74).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 24));



        inputVector = LongVector.fromArray(NORMAL, input, 90).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 106).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));



        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 10);

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



    private static void encode4(long[] input, int bitsPerValue, long[] out) {
        long mask = (1L << 4) - 1;

        for (int i = 0; i < 2; i++) {

            LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
            LongVector inputVector = LongVector.fromArray(NORMAL, input, 0 + i * 4).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60);
            inputVector = LongVector.fromArray(NORMAL, input, 8 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 4).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 16 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 8).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 24 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 12).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 32 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 16).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 40 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 20).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 48 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 24).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 56 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 28).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 64 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 32).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 72 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 36).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 80 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 40).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 88 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 44).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 96 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 48).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 104 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 52).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 112 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 56).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 120 + i * 4).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60 - 60).or(outputVector);
            outputVector.intoArray(out, i * 4);
        }

    }

    public static void encode8(long[] input, int bitsPerValue, long[] out) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 8) - 1;

        for (int i = 0; i < 4; i++) {
            // 0 1 - 4
            LongVector inputVector = LongVector.fromArray(NORMAL, input, 0 + i * 4).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 56);

            // 1 5 - 8
            inputVector = LongVector.fromArray(NORMAL, input, 16 + i * 4).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 48).or(outputVector);

            // 2 9 - 12
            inputVector = LongVector.fromArray(NORMAL, input, 32 + i * 4).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 40).or(outputVector);

            // 3 13
            inputVector = LongVector.fromArray(NORMAL, input, 48 + i * 4).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 32).or(outputVector);

            // 4
            inputVector = LongVector.fromArray(NORMAL, input, 64 + i * 4).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 24).or(outputVector);

            // 5
            inputVector = LongVector.fromArray(NORMAL, input, 80 + i * 4).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 16).or(outputVector);

            // 6
            inputVector = LongVector.fromArray(NORMAL, input, 96 + i * 4).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 8).or(outputVector);

//            // 7
            inputVector = LongVector.fromArray(NORMAL, input, 112 + i * 4).and(mask);
            outputVector = inputVector.or(outputVector);

            outputVector.intoArray(out, i * 4);
        }

    }


    public static void encode6(long[] input, int bitsPerValue, long[] out) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 6) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 58));

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 50));


        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 42));


        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 26));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 58));

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 50));


        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 42));


        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 26));


        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 58));

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 50));


        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 42));


        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 26));


        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 56));


        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 48));



        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 40));



        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 24));



        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));



        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 12);

        final int remainingBitsPerLong = 2;
        final long maskRemainingBitsPerLong = MASKS8[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 12;
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

    public static void encode7(long[] input, int bitsPerValue, long[] out) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 7) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 57));

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 49));


        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 41));


        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 25));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 57));

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 49));


        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 41));


        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 25));


        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 57));

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 49));


        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 41));


        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 25));


        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 8);
        out[12] =

                (input[12] << 57)

                        |
                        (input[28] << 49)

                        |
                        (input[44] << 41)

                        |
                        (input[60] << 33)

                        |
                        (input[76] << 25)

                        |
                        (input[92] << 17)

                        |
                        (input[108] << 9)

                        |
                        (input[124] << 1)

        ;
        out[13] =

                (input[13] << 57)

                        |
                        (input[29] << 49)

                        |
                        (input[45] << 41)

                        |
                        (input[61] << 33)

                        |
                        (input[77] << 25)

                        |
                        (input[93] << 17)

                        |
                        (input[109] << 9)

                        |
                        (input[125] << 1)

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
        final int remainingBitsPerLong = 1;
        final long maskRemainingBitsPerLong = MASKS8[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 14;
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


    private static void encode9(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 9) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 55));

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 39));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 23));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 55));

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 39));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 23));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 55));

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 39));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 23));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 55));

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 39));


        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 23));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 12);
        out[16] =

                (input[16] << 55)

                        |
                        (input[48] << 39)

                        |
                        (input[80] << 23)

                        |
                        (input[112] << 7)

        ;
        out[17] =

                (input[17] << 55)

                        |
                        (input[49] << 39)

                        |
                        (input[81] << 23)

                        |
                        (input[113] << 7)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 18).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 50).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 82).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 114).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 18);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 22).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 54).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 86).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 118).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 22);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 26).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 90).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 26);

        tmp[30] =

                (input[30] << 48)

                        |
                        (input[62] << 32)

                        |
                        (input[94] << 16)

                        |
                        (input[126] << 0)

        ;
        tmp[31] =

                (input[31] << 48)

                        |
                        (input[63] << 32)

                        |
                        (input[95] << 16)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 7;
        final long maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 18;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 32) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS16[remainingBitsPerValue];
                mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }


    private static void encode10(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 10) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 54));

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 38));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 22));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 54));

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 38));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 22));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 54));

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 38));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 22));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 54));

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 38));


        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 22));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 54));

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 38));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 22));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 20);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 24);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 28);

        final int remainingBitsPerLong = 6;
        final long maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 20;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 32) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS16[remainingBitsPerValue];
                mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode11(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 11) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 53));

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 37));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 21));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 53));

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 37));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 21));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 53));

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 37));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 21));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 53));

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 37));


        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 21));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 53));

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 37));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 21));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 16);
        out[20] =

                (input[20] << 53)

                        |
                        (input[52] << 37)

                        |
                        (input[84] << 21)

                        |
                        (input[116] << 5)

        ;
        out[21] =

                (input[21] << 53)

                        |
                        (input[53] << 37)

                        |
                        (input[85] << 21)

                        |
                        (input[117] << 5)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 22).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 54).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 86).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 118).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 22);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 26).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 90).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 26);

        tmp[30] =

                (input[30] << 48)

                        |
                        (input[62] << 32)

                        |
                        (input[94] << 16)

                        |
                        (input[126] << 0)

        ;
        tmp[31] =

                (input[31] << 48)

                        |
                        (input[63] << 32)

                        |
                        (input[95] << 16)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 5;
        final long maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 22;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 32) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS16[remainingBitsPerValue];
                mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode12(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 12) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 52));

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 36));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 20));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 52));

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 36));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 20));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 52));

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 36));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 20));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 52));

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 36));


        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 20));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 52));

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 36));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 20));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 52));

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 36));


        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 20));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 24);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 28);

        final int remainingBitsPerLong = 4;
        final long maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 24;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 32) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS16[remainingBitsPerValue];
                mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode13(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 13) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 51));

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 35));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 19));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 51));

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 35));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 19));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 51));

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 35));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 19));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 51));

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 35));


        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 19));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 51));

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 35));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 19));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 51));

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 35));


        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 19));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 20);
        out[24] =

                (input[24] << 51)

                        |
                        (input[56] << 35)

                        |
                        (input[88] << 19)

                        |
                        (input[120] << 3)

        ;
        out[25] =

                (input[25] << 51)

                        |
                        (input[57] << 35)

                        |
                        (input[89] << 19)

                        |
                        (input[121] << 3)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 26).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 90).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 26);

        tmp[30] =

                (input[30] << 48)

                        |
                        (input[62] << 32)

                        |
                        (input[94] << 16)

                        |
                        (input[126] << 0)

        ;
        tmp[31] =

                (input[31] << 48)

                        |
                        (input[63] << 32)

                        |
                        (input[95] << 16)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 3;
        final long maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 26;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 32) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS16[remainingBitsPerValue];
                mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode14(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 14) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 50));

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 50));

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 50));

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 50));

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 50));

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 50));

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 50));

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 34));


        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 18));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));


        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));



        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));



        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 28);

        final int remainingBitsPerLong = 2;
        final long maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 28;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 32) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS16[remainingBitsPerValue];
                mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode15(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 15) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 49));

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 49));

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 49));

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 49));

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 49));

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 49));

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 49));

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 33));


        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 17));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 24);
        out[28] =

                (input[28] << 49)

                        |
                        (input[60] << 33)

                        |
                        (input[92] << 17)

                        |
                        (input[124] << 1)

        ;
        out[29] =

                (input[29] << 49)

                        |
                        (input[61] << 33)

                        |
                        (input[93] << 17)

                        |
                        (input[125] << 1)

        ;
        tmp[30] =

                (input[30] << 48)

                        |
                        (input[62] << 32)

                        |
                        (input[94] << 16)

                        |
                        (input[126] << 0)

        ;
        tmp[31] =

                (input[31] << 48)

                        |
                        (input[63] << 32)

                        |
                        (input[95] << 16)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 1;
        final long maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 30;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 32) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS16[remainingBitsPerValue];
                mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode16(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 16) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));


        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 48));

        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 16));


        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 28);
        final int remainingBitsPerLong = 0;
        final long maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 32;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 32) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS16[remainingBitsPerValue];
                mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode17(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 17) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 47));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 15));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 47));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 15));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 47));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 15));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 47));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 15));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 47));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 15));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 47));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 15));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 47));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 15));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 47));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 15));


        outputVector.intoArray(out, 28);
        out[32] =

                (input[32] << 47)

                        |
                        (input[96] << 15)

        ;
        out[33] =

                (input[33] << 47)

                        |
                        (input[97] << 15)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 34).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 98).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 34);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 38).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 102).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 38);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 42).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 106).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 42);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 46).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 110).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 46);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 50).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 114).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 50);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 54).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 118).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 54);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 58);

        tmp[62] =

                (input[62] << 32)

                        |
                        (input[126] << 0)

        ;
        tmp[63] =

                (input[63] << 32)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 15;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 34;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode18(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 18) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 46));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 14));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 46));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 14));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 46));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 14));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 46));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 14));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 46));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 14));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 46));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 14));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 46));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 14));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 46));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 14));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 46));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 14));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 36);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 40);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 44);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 48);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 52);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 56);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 60);

        final int remainingBitsPerLong = 14;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 36;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode19(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 19) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 45));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 45));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 45));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 45));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 45));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 45));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 45));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 45));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 45));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 13));


        outputVector.intoArray(out, 32);
        out[36] =

                (input[36] << 45)

                        |
                        (input[100] << 13)

        ;
        out[37] =

                (input[37] << 45)

                        |
                        (input[101] << 13)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 38).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 102).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 38);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 42).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 106).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 42);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 46).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 110).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 46);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 50).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 114).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 50);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 54).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 118).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 54);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 58);

        tmp[62] =

                (input[62] << 32)

                        |
                        (input[126] << 0)

        ;
        tmp[63] =

                (input[63] << 32)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 13;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 38;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode20(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 20) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 44));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 12));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 40);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 44);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 48);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 52);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 56);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 60);

        final int remainingBitsPerLong = 12;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 40;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode21(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 21) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 43));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 11));


        outputVector.intoArray(out, 36);
        out[40] =

                (input[40] << 43)

                        |
                        (input[104] << 11)

        ;
        out[41] =

                (input[41] << 43)

                        |
                        (input[105] << 11)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 42).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 106).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 42);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 46).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 110).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 46);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 50).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 114).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 50);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 54).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 118).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 54);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 58);

        tmp[62] =

                (input[62] << 32)

                        |
                        (input[126] << 0)

        ;
        tmp[63] =

                (input[63] << 32)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 11;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 42;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode22(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 22) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 42));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 10));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 44);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 48);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 52);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 56);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 60);

        final int remainingBitsPerLong = 10;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 44;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode23(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 23) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 41));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 9));


        outputVector.intoArray(out, 40);
        out[44] =

                (input[44] << 41)

                        |
                        (input[108] << 9)

        ;
        out[45] =

                (input[45] << 41)

                        |
                        (input[109] << 9)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 46).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 110).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 46);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 50).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 114).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 50);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 54).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 118).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 54);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 58);

        tmp[62] =

                (input[62] << 32)

                        |
                        (input[126] << 0)

        ;
        tmp[63] =

                (input[63] << 32)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 9;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 46;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode24(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 24) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 40));

        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 8));


        outputVector.intoArray(out, 44);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 48);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 52);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 56);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 60);

        final int remainingBitsPerLong = 8;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 48;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode25(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 25) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 39));

        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 7));


        outputVector.intoArray(out, 44);
        out[48] =

                (input[48] << 39)

                        |
                        (input[112] << 7)

        ;
        out[49] =

                (input[49] << 39)

                        |
                        (input[113] << 7)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 50).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 114).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 50);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 54).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 118).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 54);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 58);

        tmp[62] =

                (input[62] << 32)

                        |
                        (input[126] << 0)

        ;
        tmp[63] =

                (input[63] << 32)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 7;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 50;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode26(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 26) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 44);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 38));

        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 6));


        outputVector.intoArray(out, 48);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 52);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 56);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 60);

        final int remainingBitsPerLong = 6;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 52;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode27(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 27) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 44);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 37));

        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 5));


        outputVector.intoArray(out, 48);
        out[52] =

                (input[52] << 37)

                        |
                        (input[116] << 5)

        ;
        out[53] =

                (input[53] << 37)

                        |
                        (input[117] << 5)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 54).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 118).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 54);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 58);

        tmp[62] =

                (input[62] << 32)

                        |
                        (input[126] << 0)

        ;
        tmp[63] =

                (input[63] << 32)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 5;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 54;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode28(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 28) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 44);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 48);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 36));

        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 4));


        outputVector.intoArray(out, 52);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 56);

        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 60);

        final int remainingBitsPerLong = 4;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 56;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode29(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 29) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 44);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 48);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 35));

        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 3));


        outputVector.intoArray(out, 52);
        out[56] =

                (input[56] << 35)

                        |
                        (input[120] << 3)

        ;
        out[57] =

                (input[57] << 35)

                        |
                        (input[121] << 3)

        ;
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 58).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 122).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 58);

        tmp[62] =

                (input[62] << 32)

                        |
                        (input[126] << 0)

        ;
        tmp[63] =

                (input[63] << 32)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 3;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 58;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode30(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 30) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 44);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 48);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 52);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 34));

        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 2));


        outputVector.intoArray(out, 56);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));


        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));



        outputVector.intoArray(tmp, 60);

        final int remainingBitsPerLong = 2;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 60;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode31(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 31) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 44);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 48);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 52);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 33));

        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 1));


        outputVector.intoArray(out, 56);
        out[60] =

                (input[60] << 33)

                        |
                        (input[124] << 1)

        ;
        out[61] =

                (input[61] << 33)

                        |
                        (input[125] << 1)

        ;
        tmp[62] =

                (input[62] << 32)

                        |
                        (input[126] << 0)

        ;
        tmp[63] =

                (input[63] << 32)

                        |
                        (input[127] << 0)

        ;
        final int remainingBitsPerLong = 1;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 62;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }private static void encode32(long[] input, int bitsPerValue, long[] out) {LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 32) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 0);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 4);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 8);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 12);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 16);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 20);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 24);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 28);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 32);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 36);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 40);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 44);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 48);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 52);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 56);
        outputVector = NORMAL.zero().reinterpretAsLongs();

        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);
        outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, 32));

        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);
        outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, 0));


        outputVector.intoArray(out, 60);
        final int remainingBitsPerLong = 0;
        final long maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];

        int tmpIdx = 0;
        int idx = 64;
        int remainingBitsPerValue = bitsPerValue;
        while (idx < 64) {
            if (remainingBitsPerValue >= remainingBitsPerLong) {
                remainingBitsPerValue -= remainingBitsPerLong;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                if (remainingBitsPerValue == 0) {
                    idx++;
                    remainingBitsPerValue = bitsPerValue;
                }
            } else {
                final long mask1, mask2;

                mask1 = MASKS32[remainingBitsPerValue];
                mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
                out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
            }
        }
    }
    /**
     * Encode 128 integers from {@code longs} into {@code out}.
     */
    public void encode(long[] longs, int bitsPerValue, long[] out) throws IOException {

        switch (bitsPerValue) {
            case 1 -> encode1(longs, bitsPerValue, out);
            case 2 -> encode2(longs, bitsPerValue, out);
            case 3 -> encode3(longs, bitsPerValue, out);
            case 4 -> encode4(longs, bitsPerValue, out);
            case 5 -> encode5(longs, bitsPerValue, out);
            case 6 -> encode6(longs, bitsPerValue, out);
            case 7 -> encode7(longs, bitsPerValue, out);
            case 8 -> encode8(longs, bitsPerValue, out);
            case 9 -> encode9(longs, bitsPerValue, out);
            case 10 -> encode10(longs, bitsPerValue, out);
            case 11 -> encode11(longs, bitsPerValue, out);
            case 12 -> encode12(longs, bitsPerValue, out);
            case 13 -> encode13(longs, bitsPerValue, out);
            case 14 -> encode14(longs, bitsPerValue, out);
            case 15 -> encode15(longs, bitsPerValue, out);
            case 16 -> encode16(longs, bitsPerValue, out);
            case 17 -> encode17(longs, bitsPerValue, out);
            case 18 -> encode18(longs, bitsPerValue, out);
            case 19 -> encode19(longs, bitsPerValue, out);
            case 20 -> encode20(longs, bitsPerValue, out);
            case 21 -> encode21(longs, bitsPerValue, out);
            case 22 -> encode22(longs, bitsPerValue, out);
            case 23 -> encode23(longs, bitsPerValue, out);
            case 24 -> encode24(longs, bitsPerValue, out);
            case 25 -> encode25(longs, bitsPerValue, out);
            case 26 -> encode26(longs, bitsPerValue, out);
            case 27 -> encode27(longs, bitsPerValue, out);
            case 28 -> encode28(longs, bitsPerValue, out);
            case 29 -> encode29(longs, bitsPerValue, out);
            case 30 -> encode30(longs, bitsPerValue, out);
            case 31 -> encode31(longs, bitsPerValue, out);
            case 32 -> encode32(longs, bitsPerValue, out);
//            case 10 -> encode10(longs, bitsPerValue, out);
        }
        return;
//        final int nextPrimitive;
//        final int numLongs;
//        if (bitsPerValue <= 8) {
//            nextPrimitive = 8;
//            numLongs = BLOCK_SIZE / 8;
//            collapse8(longs);
//        } else if (bitsPerValue <= 16) {
//            nextPrimitive = 16;
//            numLongs = BLOCK_SIZE / 4;
//            collapse16(longs);
//        } else {
//            nextPrimitive = 32;
//            numLongs = BLOCK_SIZE / 2;
//            collapse32(longs);
//        }
//
//        final int numLongsPerShift = bitsPerValue * 2;
//        int idx = 0;
//        int shift = nextPrimitive - bitsPerValue;
//        // push to the head of byte
//        for (int i = 0; i < numLongsPerShift; ++i) {
//            tmp[i] = longs[idx++] << shift;
//        }
//        // can we insert more data in the same byte?
//        for (shift = shift - bitsPerValue; shift >= 0; shift -= bitsPerValue) {
//            for (int i = 0; i < numLongsPerShift; ++i) {
//                tmp[i] |= longs[idx++] << shift;
//            }
//        }
//
//        final int remainingBitsPerLong = shift + bitsPerValue;
//        final long maskRemainingBitsPerLong;
//        if (nextPrimitive == 8) {
//            maskRemainingBitsPerLong = MASKS8[remainingBitsPerLong];
//        } else if (nextPrimitive == 16) {
//            maskRemainingBitsPerLong = MASKS16[remainingBitsPerLong];
//        } else {
//            maskRemainingBitsPerLong = MASKS32[remainingBitsPerLong];
//        }
//
//        int tmpIdx = 0;
//        int remainingBitsPerValue = bitsPerValue;
//        while (idx < numLongs) {
//            if (remainingBitsPerValue >= remainingBitsPerLong) {
//                remainingBitsPerValue -= remainingBitsPerLong;
//                tmp[tmpIdx++] |= (longs[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
//                if (remainingBitsPerValue == 0) {
//                    idx++;
//                    remainingBitsPerValue = bitsPerValue;
//                }
//            } else {
//                final long mask1, mask2;
//                if (nextPrimitive == 8) {
//                    mask1 = MASKS8[remainingBitsPerValue];
//                    mask2 = MASKS8[remainingBitsPerLong - remainingBitsPerValue];
//                } else if (nextPrimitive == 16) {
//                    mask1 = MASKS16[remainingBitsPerValue];
//                    mask2 = MASKS16[remainingBitsPerLong - remainingBitsPerValue];
//                } else {
//                    mask1 = MASKS32[remainingBitsPerValue];
//                    mask2 = MASKS32[remainingBitsPerLong - remainingBitsPerValue];
//                }
//                tmp[tmpIdx] |= (longs[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
//                remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
//                tmp[tmpIdx++] |= (longs[idx] >>> remainingBitsPerValue) & mask2;
//            }
//        }
//
//        for (int i = 0; i < numLongsPerShift; ++i) {
//            out[i] = tmp[i];
//        }


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

