package life.tangdh;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.time.temporal.ValueRange;

public class SimdPack {

    private static final VectorSpecies<Long> SPECIES = LongVector.SPECIES_128;

    private static final VectorSpecies<Long> NORMAL = LongVector.SPECIES_256;

    public static void pack(long[] input, int bpv, long[] output) {
        switch (bpv) {
            case 1 -> pack1_128bit(input, output);
            case 2 -> pack2_256bit(input, output);
            case 3 -> pack3_256bit(input, output);
            case 4 -> pack4_256bit(input, output);
            case 5 -> pack5_256bit(input, output);
            case 6 -> pack6_256bit(input, output);
            case 7 -> pack7_256bit(input, output);
            case 8 -> pack8_256bit(input, output);
        }
    }

    public static void unpack(long[] input, int bpv, long[] output) {
        switch (bpv) {
            case 1 -> unpack1_128bit(input, output);
            case 2 -> unpack2_256bit(input, output);
            case 3 -> unpack3_256bit(input, output);
            case 4 -> unpack4_256bit(input, output);
            case 5 -> unpack5_256bit(input, output);
            case 6 -> unpack6_256bit(input, output);
            case 7 -> unpack7_256bit(input, output);
            case 8 -> unpack8_256bit(input, output);
        }
    }

    public static void unpack8_256bit(long[] input, long[] output) {
        long mask = (1L << 8) - 1;
        LongVector raw = LongVector.fromArray(NORMAL, input, 0);
        LongVector inputVector = raw.and(mask);
        inputVector.intoArray(output, 0);

        inputVector = raw.lanewise(VectorOperators.LSHR, 8).and(mask);
        inputVector.intoArray(output, 4);


        inputVector = raw.lanewise(VectorOperators.LSHR, 16).and(mask);
        inputVector.intoArray(output, 8);

        inputVector = raw.lanewise(VectorOperators.LSHR, 24).and(mask);
        inputVector.intoArray(output, 12);

        inputVector = raw.lanewise(VectorOperators.LSHR, 32).and(mask);
        inputVector.intoArray(output, 16);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 20);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 24);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 28);

        raw = LongVector.fromArray(NORMAL, input, 4);

        inputVector = raw.and(mask);
        inputVector.intoArray(output, 32);

        inputVector = raw.lanewise(VectorOperators.LSHR, 8).and(mask);
        inputVector.intoArray(output, 36);

        inputVector = raw.lanewise(VectorOperators.LSHR, 16).and(mask);
        inputVector.intoArray(output, 40);

        inputVector = raw.lanewise(VectorOperators.LSHR, 24).and(mask);
        inputVector.intoArray(output, 44);

        inputVector = raw.lanewise(VectorOperators.LSHR, 32).and(mask);
        inputVector.intoArray(output, 48);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 52);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 56);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 60);

        raw = LongVector.fromArray(NORMAL, input, 8);
        inputVector = raw.and(mask);
        inputVector.intoArray(output, 64);


        inputVector = raw.lanewise(VectorOperators.LSHR, 8).and(mask);
        inputVector.intoArray(output, 68);

        inputVector = raw.lanewise(VectorOperators.LSHR, 16).and(mask);
        inputVector.intoArray(output, 72);

        inputVector = raw.lanewise(VectorOperators.LSHR, 24).and(mask);
        inputVector.intoArray(output, 76);

        inputVector = raw.lanewise(VectorOperators.LSHR, 32).and(mask);
        inputVector.intoArray(output, 80);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 84);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 88);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 92);

        raw = LongVector.fromArray(NORMAL, input, 12);
        inputVector = raw.and(mask);
        inputVector.intoArray(output, 96);

        inputVector = raw.lanewise(VectorOperators.LSHR, 8).and(mask);
        inputVector.intoArray(output, 100);

        inputVector = raw.lanewise(VectorOperators.LSHR, 16).and(mask);
        inputVector.intoArray(output, 104);

        inputVector = raw.lanewise(VectorOperators.LSHR, 24).and(mask);
        inputVector.intoArray(output, 108);

        inputVector = raw.lanewise(VectorOperators.LSHR, 32).and(mask);
        inputVector.intoArray(output, 112);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 116);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 120);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 124);


    }

    public static void unpack7_256bit(long[] input, long[] output) {
        long mask = (1L << 7) - 1;
        LongVector raw = LongVector.fromArray(NORMAL, input, 0);
        LongVector inputVector = raw.and(mask);
        inputVector.intoArray(output, 0);

        inputVector = raw.lanewise(VectorOperators.LSHR, 7).and(mask);
        inputVector.intoArray(output, 4);

        inputVector = raw.lanewise(VectorOperators.LSHR, 14).and(mask);
        inputVector.intoArray(output, 8);

        inputVector = raw.lanewise(VectorOperators.LSHR, 21).and(mask);
        inputVector.intoArray(output, 12);

        inputVector = raw.lanewise(VectorOperators.LSHR, 28).and(mask);
        inputVector.intoArray(output, 16);

        inputVector = raw.lanewise(VectorOperators.LSHR, 35).and(mask);
        inputVector.intoArray(output, 20);

        inputVector = raw.lanewise(VectorOperators.LSHR, 42).and(mask);
        inputVector.intoArray(output, 24);

        inputVector = raw.lanewise(VectorOperators.LSHR, 49).and(mask);
        inputVector.intoArray(output, 28);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 32);

        inputVector = raw.lanewise(VectorOperators.LSHR, 63).and(mask);
        raw = LongVector.fromArray(NORMAL, input, 4);
        inputVector = inputVector.or(raw.lanewise(VectorOperators.LSHL, 1).and(mask));
        inputVector.intoArray(output, 36);

        inputVector = raw.lanewise(VectorOperators.LSHR, 6).and(mask);
        inputVector.intoArray(output, 40);

        inputVector = raw.lanewise(VectorOperators.LSHR, 13).and(mask);
        inputVector.intoArray(output, 44);


        inputVector = raw.lanewise(VectorOperators.LSHR, 20).and(mask);
        inputVector.intoArray(output, 48);

        inputVector = raw.lanewise(VectorOperators.LSHR, 27).and(mask);
        inputVector.intoArray(output, 52);

        inputVector = raw.lanewise(VectorOperators.LSHR, 34).and(mask);
        inputVector.intoArray(output, 56);

        inputVector = raw.lanewise(VectorOperators.LSHR, 41).and(mask);
        inputVector.intoArray(output, 60);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 64);

        inputVector = raw.lanewise(VectorOperators.LSHR, 55).and(mask);
        inputVector.intoArray(output, 68);

        inputVector = raw.lanewise(VectorOperators.LSHR, 62).and(mask);
        raw = LongVector.fromArray(NORMAL, input, 8);
        inputVector = inputVector.or(raw.lanewise(VectorOperators.LSHL, 2).and(mask));
        inputVector.intoArray(output, 72);

        inputVector = raw.lanewise(VectorOperators.LSHR, 5).and(mask);
        inputVector.intoArray(output, 76);

        inputVector = raw.lanewise(VectorOperators.LSHR, 12).and(mask);
        inputVector.intoArray(output, 80);

        inputVector = raw.lanewise(VectorOperators.LSHR, 19).and(mask);
        inputVector.intoArray(output, 84);

        inputVector = raw.lanewise(VectorOperators.LSHR, 26).and(mask);
        inputVector.intoArray(output, 88);

        inputVector = raw.lanewise(VectorOperators.LSHR, 33).and(mask);
        inputVector.intoArray(output, 92);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 96);

        inputVector = raw.lanewise(VectorOperators.LSHR, 47).and(mask);
        inputVector.intoArray(output, 100);

        inputVector = raw.lanewise(VectorOperators.LSHR, 54).and(mask);
        inputVector.intoArray(output, 104);

        inputVector = raw.lanewise(VectorOperators.LSHR, 61).and(mask);
        raw = LongVector.fromArray(NORMAL, input, 12);
        inputVector = inputVector.or(raw.lanewise(VectorOperators.LSHL, 3).and(mask));
        inputVector.intoArray(output, 108);

        inputVector = raw.lanewise(VectorOperators.LSHR, 4).and(mask);
        inputVector.intoArray(output, 112);

        inputVector = raw.lanewise(VectorOperators.LSHR, 11).and(mask);
        inputVector.intoArray(output, 116);

        inputVector = raw.lanewise(VectorOperators.LSHR, 18).and(mask);
        inputVector.intoArray(output, 120);

        inputVector = raw.lanewise(VectorOperators.LSHR, 25).and(mask);
        inputVector.intoArray(output, 124);


    }

    public static void unpack6_256bit(long[] input, long[] output) {
        long mask = (1L << 6) - 1;

        LongVector raw = LongVector.fromArray(NORMAL, input, 0);
        LongVector inputVector = raw.and(mask);
        inputVector.intoArray(output, 0);

        inputVector = raw.lanewise(VectorOperators.LSHR, 6).and(mask);
        inputVector.intoArray(output, 4);


        inputVector = raw.lanewise(VectorOperators.LSHR, 12).and(mask);
        inputVector.intoArray(output, 8);

        inputVector = raw.lanewise(VectorOperators.LSHR, 18).and(mask);
        inputVector.intoArray(output, 12);

        inputVector = raw.lanewise(VectorOperators.LSHR, 24).and(mask);
        inputVector.intoArray(output, 16);

        inputVector = raw.lanewise(VectorOperators.LSHR, 30).and(mask);
        inputVector.intoArray(output, 20);

        inputVector = raw.lanewise(VectorOperators.LSHR, 36).and(mask);
        inputVector.intoArray(output, 24);

        inputVector = raw.lanewise(VectorOperators.LSHR, 42).and(mask);
        inputVector.intoArray(output, 28);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 32);

        inputVector = raw.lanewise(VectorOperators.LSHR, 54).and(mask);
        inputVector.intoArray(output, 36);

        inputVector = raw.lanewise(VectorOperators.LSHR, 60).and(mask);
        raw = LongVector.fromArray(NORMAL, input, 4);
        inputVector = inputVector.or(raw.lanewise(VectorOperators.LSHL, 4).and(mask));
        inputVector.intoArray(output, 40);


        inputVector = raw.lanewise(VectorOperators.LSHR, 2).and(mask);
        inputVector.intoArray(output, 44);

        inputVector = raw.lanewise(VectorOperators.LSHR, 8).and(mask);
        inputVector.intoArray(output, 48);

        inputVector = raw.lanewise(VectorOperators.LSHR, 14).and(mask);
        inputVector.intoArray(output, 52);

        inputVector = raw.lanewise(VectorOperators.LSHR, 20).and(mask);
        inputVector.intoArray(output, 56);

        inputVector = raw.lanewise(VectorOperators.LSHR, 26).and(mask);
        inputVector.intoArray(output, 60);

        inputVector = raw.lanewise(VectorOperators.LSHR, 32).and(mask);
        inputVector.intoArray(output, 64);

        inputVector = raw.lanewise(VectorOperators.LSHR, 38).and(mask);
        inputVector.intoArray(output, 68);

        inputVector = raw.lanewise(VectorOperators.LSHR, 44).and(mask);
        inputVector.intoArray(output, 72);

        inputVector = raw.lanewise(VectorOperators.LSHR, 50).and(mask);
        inputVector.intoArray(output, 76);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 80);

        inputVector = raw.lanewise(VectorOperators.LSHR, 62).and(mask);
        raw = LongVector.fromArray(NORMAL, input, 8);
        inputVector = inputVector.or(raw.lanewise(VectorOperators.LSHL, 2).and(mask));
        inputVector.intoArray(output, 84);


        inputVector = raw.lanewise(VectorOperators.LSHR, 4).and(mask);
        inputVector.intoArray(output, 88);

        inputVector = raw.lanewise(VectorOperators.LSHR, 10).and(mask);
        inputVector.intoArray(output, 92);

        inputVector = raw.lanewise(VectorOperators.LSHR, 16).and(mask);
        inputVector.intoArray(output, 96);


        inputVector = raw.lanewise(VectorOperators.LSHR, 22).and(mask);
        inputVector.intoArray(output, 100);

        inputVector = raw.lanewise(VectorOperators.LSHR, 28).and(mask);
        inputVector.intoArray(output, 104);

        inputVector = raw.lanewise(VectorOperators.LSHR, 34).and(mask);
        inputVector.intoArray(output, 108);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 112);

        inputVector = raw.lanewise(VectorOperators.LSHR, 46).and(mask);
        inputVector.intoArray(output, 116);


        inputVector = raw.lanewise(VectorOperators.LSHR, 52).and(mask);
        inputVector.intoArray(output, 120);

        inputVector = raw.lanewise(VectorOperators.LSHR, 58).and(mask);
        inputVector.intoArray(output, 124);


    }

    public static void unpack5_256bit(long[] input, long[] output) {
        long mask = (1L << 5) - 1;

        LongVector raw = LongVector.fromArray(NORMAL, input, 0);
        LongVector inputVector = raw.and(mask);
        inputVector.intoArray(output, 0);


        inputVector = raw.lanewise(VectorOperators.LSHR, 5).and(mask);
        inputVector.intoArray(output, 4);


        inputVector = raw.lanewise(VectorOperators.LSHR, 10).and(mask);
        inputVector.intoArray(output, 8);

        inputVector = raw.lanewise(VectorOperators.LSHR, 15).and(mask);
        inputVector.intoArray(output, 12);

        inputVector = raw.lanewise(VectorOperators.LSHR, 20).and(mask);
        inputVector.intoArray(output, 16);

        inputVector = raw.lanewise(VectorOperators.LSHR, 25).and(mask);
        inputVector.intoArray(output, 20);

        inputVector = raw.lanewise(VectorOperators.LSHR, 30).and(mask);
        inputVector.intoArray(output, 24);

        inputVector = raw.lanewise(VectorOperators.LSHR, 35).and(mask);
        inputVector.intoArray(output, 28);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 32);

        inputVector = raw.lanewise(VectorOperators.LSHR, 45).and(mask);
        inputVector.intoArray(output, 36);

        inputVector = raw.lanewise(VectorOperators.LSHR, 50).and(mask);
        inputVector.intoArray(output, 40);

        inputVector = raw.lanewise(VectorOperators.LSHR, 55).and(mask);
        inputVector.intoArray(output, 44);

        inputVector = raw.lanewise(VectorOperators.LSHR, 60).and(mask);
        raw = LongVector.fromArray(NORMAL, input, 4);
        inputVector = inputVector.or(raw.lanewise(VectorOperators.LSHL, 4).and(mask));

        inputVector.intoArray(output, 48);

        inputVector = raw.lanewise(VectorOperators.LSHR, 1).and(mask);
        inputVector.intoArray(output, 52);


        inputVector = raw.lanewise(VectorOperators.LSHR, 6).and(mask);
        inputVector.intoArray(output, 56);

        inputVector = raw.lanewise(VectorOperators.LSHR, 11).and(mask);
        inputVector.intoArray(output, 60);

        inputVector = raw.lanewise(VectorOperators.LSHR, 16).and(mask);
        inputVector.intoArray(output, 64);

        inputVector = raw.lanewise(VectorOperators.LSHR, 21).and(mask);
        inputVector.intoArray(output, 68);

        inputVector = raw.lanewise(VectorOperators.LSHR, 26).and(mask);
        inputVector.intoArray(output, 72);

        inputVector = raw.lanewise(VectorOperators.LSHR, 31).and(mask);
        inputVector.intoArray(output, 76);

        inputVector = raw.lanewise(VectorOperators.LSHR, 36).and(mask);
        inputVector.intoArray(output, 80);

        inputVector = raw.lanewise(VectorOperators.LSHR, 41).and(mask);
        inputVector.intoArray(output, 84);

        inputVector = raw.lanewise(VectorOperators.LSHR, 46).and(mask);
        inputVector.intoArray(output, 88);

        inputVector = raw.lanewise(VectorOperators.LSHR, 51).and(mask);
        inputVector.intoArray(output, 92);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 96);

        inputVector = raw.lanewise(VectorOperators.LSHR, 61).and(mask);
        raw = LongVector.fromArray(NORMAL, input, 8);
        inputVector = inputVector.or(raw.lanewise(VectorOperators.LSHL, 3).and(mask));
        inputVector.intoArray(output, 100);

        inputVector = raw.lanewise(VectorOperators.LSHR, 2).and(mask);
        inputVector.intoArray(output, 104);

        inputVector = raw.lanewise(VectorOperators.LSHR, 7).and(mask);
        inputVector.intoArray(output, 108);

        inputVector = raw.lanewise(VectorOperators.LSHR, 12).and(mask);
        inputVector.intoArray(output, 112);


        inputVector = raw.lanewise(VectorOperators.LSHR, 17).and(mask);
        inputVector.intoArray(output, 116);

        inputVector = raw.lanewise(VectorOperators.LSHR, 22).and(mask);
        inputVector.intoArray(output, 120);

        inputVector = raw.lanewise(VectorOperators.LSHR, 27).and(mask);
        inputVector.intoArray(output, 124);

    }

    public static void unpack4_256bit(long[] input, long[] output) {
        long mask = (1L << 4) - 1;
        LongVector raw = LongVector.fromArray(NORMAL, input, 0);
        LongVector inputVector = raw.and(mask);
        inputVector.intoArray(output, 0);

        inputVector = raw.lanewise(VectorOperators.LSHR, 4).and(mask);
        inputVector.intoArray(output, 4);

        inputVector = raw.lanewise(VectorOperators.LSHR, 8).and(mask);
        inputVector.intoArray(output, 8);

        inputVector = raw.lanewise(VectorOperators.LSHR, 12).and(mask);
        inputVector.intoArray(output, 12);

        inputVector = raw.lanewise(VectorOperators.LSHR, 16).and(mask);
        inputVector.intoArray(output, 16);

        inputVector = raw.lanewise(VectorOperators.LSHR, 20).and(mask);
        inputVector.intoArray(output, 20);

        inputVector = raw.lanewise(VectorOperators.LSHR, 24).and(mask);
        inputVector.intoArray(output, 24);

        inputVector = raw.lanewise(VectorOperators.LSHR, 28).and(mask);
        inputVector.intoArray(output, 28);

        inputVector = raw.lanewise(VectorOperators.LSHR, 32).and(mask);
        inputVector.intoArray(output, 32);


        inputVector = raw.lanewise(VectorOperators.LSHR, 36).and(mask);
        inputVector.intoArray(output, 36);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 40);

        inputVector = raw.lanewise(VectorOperators.LSHR, 44).and(mask);
        inputVector.intoArray(output, 44);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 48);

        inputVector = raw.lanewise(VectorOperators.LSHR, 52).and(mask);
        inputVector.intoArray(output, 52);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 56);

        inputVector = raw.lanewise(VectorOperators.LSHR, 60).and(mask);
        inputVector.intoArray(output, 60);

        raw = LongVector.fromArray(NORMAL, input, 4);
        inputVector = raw.and(mask);
        inputVector.intoArray(output, 64);

        inputVector = raw.lanewise(VectorOperators.LSHR, 4).and(mask);
        inputVector.intoArray(output, 68);

        inputVector = raw.lanewise(VectorOperators.LSHR, 8).and(mask);
        inputVector.intoArray(output, 72);

        inputVector = raw.lanewise(VectorOperators.LSHR, 12).and(mask);
        inputVector.intoArray(output, 76);

        inputVector = raw.lanewise(VectorOperators.LSHR, 16).and(mask);
        inputVector.intoArray(output, 80);

        inputVector = raw.lanewise(VectorOperators.LSHR, 20).and(mask);
        inputVector.intoArray(output, 84);

        inputVector = raw.lanewise(VectorOperators.LSHR, 24).and(mask);
        inputVector.intoArray(output, 88);

        inputVector = raw.lanewise(VectorOperators.LSHR, 28).and(mask);
        inputVector.intoArray(output, 92);

        inputVector = raw.lanewise(VectorOperators.LSHR, 32).and(mask);
        inputVector.intoArray(output, 96);

        inputVector = raw.lanewise(VectorOperators.LSHR, 36).and(mask);
        inputVector.intoArray(output, 100);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 104);

        inputVector = raw.lanewise(VectorOperators.LSHR, 44).and(mask);
        inputVector.intoArray(output, 108);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 112);

        inputVector = raw.lanewise(VectorOperators.LSHR, 52).and(mask);
        inputVector.intoArray(output, 116);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 120);

        inputVector = raw.lanewise(VectorOperators.LSHR, 60).and(mask);
        inputVector.intoArray(output, 124);

    }

    public static void unpack3_256bit(long[] input, long[] output) {
        long mask = (1L << 3) - 1;
        LongVector raw = LongVector.fromArray(NORMAL, input, 0);
        LongVector inputVector = raw.and(mask);
        inputVector.intoArray(output, 0);

        inputVector = raw.lanewise(VectorOperators.LSHR, 3).and(mask);
        inputVector.intoArray(output, 4);

        inputVector = raw.lanewise(VectorOperators.LSHR, 6).and(mask);
        inputVector.intoArray(output, 8);

        inputVector = raw.lanewise(VectorOperators.LSHR, 9).and(mask);
        inputVector.intoArray(output, 12);

        inputVector = raw.lanewise(VectorOperators.LSHR, 12).and(mask);
        inputVector.intoArray(output, 16);

        inputVector = raw.lanewise(VectorOperators.LSHR, 15).and(mask);
        inputVector.intoArray(output, 20);

        inputVector = raw.lanewise(VectorOperators.LSHR, 18).and(mask);
        inputVector.intoArray(output, 24);

        inputVector = raw.lanewise(VectorOperators.LSHR, 21).and(mask);
        inputVector.intoArray(output, 28);

        inputVector = raw.lanewise(VectorOperators.LSHR, 24).and(mask);
        inputVector.intoArray(output, 32);

        inputVector = raw.lanewise(VectorOperators.LSHR, 27).and(mask);
        inputVector.intoArray(output, 36);

        inputVector = raw.lanewise(VectorOperators.LSHR, 30).and(mask);
        inputVector.intoArray(output, 40);

        inputVector = raw.lanewise(VectorOperators.LSHR, 33).and(mask);
        inputVector.intoArray(output, 44);

        inputVector = raw.lanewise(VectorOperators.LSHR, 36).and(mask);
        inputVector.intoArray(output, 48);

        inputVector = raw.lanewise(VectorOperators.LSHR, 39).and(mask);
        inputVector.intoArray(output, 52);

        inputVector = raw.lanewise(VectorOperators.LSHR, 42).and(mask);
        inputVector.intoArray(output, 56);

        inputVector = raw.lanewise(VectorOperators.LSHR, 45).and(mask);
        inputVector.intoArray(output, 60);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 64);

        inputVector = raw.lanewise(VectorOperators.LSHR, 51).and(mask);
        inputVector.intoArray(output, 68);

        inputVector = raw.lanewise(VectorOperators.LSHR, 54).and(mask);
        inputVector.intoArray(output, 72);

        inputVector = raw.lanewise(VectorOperators.LSHR, 57).and(mask);
        inputVector.intoArray(output, 76);

        inputVector = raw.lanewise(VectorOperators.LSHR, 60).and(mask);
        inputVector.intoArray(output, 80);

        inputVector = raw.lanewise(VectorOperators.LSHR, 63).and(mask);
        raw = LongVector.fromArray(NORMAL, input, 4);

        inputVector = inputVector.or(raw.lanewise(VectorOperators.LSHL, 1).and(mask));
        inputVector.intoArray(output, 84);

        inputVector = raw.lanewise(VectorOperators.LSHR, 2).and(mask);
        inputVector.intoArray(output, 88);


        inputVector = raw.lanewise(VectorOperators.LSHR, 5).and(mask);
        inputVector.intoArray(output, 92);

        inputVector = raw.lanewise(VectorOperators.LSHR, 8).and(mask);
        inputVector.intoArray(output, 96);

        inputVector = raw.lanewise(VectorOperators.LSHR, 11).and(mask);
        inputVector.intoArray(output, 100);

        inputVector = raw.lanewise(VectorOperators.LSHR, 14).and(mask);
        inputVector.intoArray(output, 104);

        inputVector = raw.lanewise(VectorOperators.LSHR, 17).and(mask);
        inputVector.intoArray(output, 108);

        inputVector = raw.lanewise(VectorOperators.LSHR, 20).and(mask);
        inputVector.intoArray(output, 112);

        inputVector = raw.lanewise(VectorOperators.LSHR, 23).and(mask);
        inputVector.intoArray(output, 116);

        inputVector = raw.lanewise(VectorOperators.LSHR, 26).and(mask);
        inputVector.intoArray(output, 120);

        inputVector = raw.lanewise(VectorOperators.LSHR, 29).and(mask);
        inputVector.intoArray(output, 124);


    }

    public static void unpack2_256bit(long[] input, long[] output) {
        long mask = (1L << 2) - 1;
        LongVector raw = LongVector.fromArray(NORMAL, input, 0);
        LongVector inputVector = raw.and(mask);
        inputVector.intoArray(output, 0);

        inputVector = raw.lanewise(VectorOperators.LSHR, 2).and(mask);
        inputVector.intoArray(output, 4);

        inputVector = raw.lanewise(VectorOperators.LSHR, 4).and(mask);
        inputVector.intoArray(output, 8);

        inputVector = raw.lanewise(VectorOperators.LSHR, 6).and(mask);
        inputVector.intoArray(output, 12);

        inputVector = raw.lanewise(VectorOperators.LSHR, 8).and(mask);
        inputVector.intoArray(output, 16);

        inputVector = raw.lanewise(VectorOperators.LSHR, 10).and(mask);
        inputVector.intoArray(output, 20);

        inputVector = raw.lanewise(VectorOperators.LSHR, 12).and(mask);
        inputVector.intoArray(output, 24);

        inputVector = raw.lanewise(VectorOperators.LSHR, 14).and(mask);
        inputVector.intoArray(output, 28);

        inputVector = raw.lanewise(VectorOperators.LSHR, 16).and(mask);
        inputVector.intoArray(output, 32);

        inputVector = raw.lanewise(VectorOperators.LSHR, 18).and(mask);
        inputVector.intoArray(output, 36);

        inputVector = raw.lanewise(VectorOperators.LSHR, 20).and(mask);
        inputVector.intoArray(output, 40);

        inputVector = raw.lanewise(VectorOperators.LSHR, 22).and(mask);
        inputVector.intoArray(output, 44);

        inputVector = raw.lanewise(VectorOperators.LSHR, 24).and(mask);
        inputVector.intoArray(output, 48);

        inputVector = raw.lanewise(VectorOperators.LSHR, 26).and(mask);
        inputVector.intoArray(output, 52);

        inputVector = raw.lanewise(VectorOperators.LSHR, 28).and(mask);
        inputVector.intoArray(output, 56);

        inputVector = raw.lanewise(VectorOperators.LSHR, 30).and(mask);
        inputVector.intoArray(output, 60);

        inputVector = raw.lanewise(VectorOperators.LSHR, 32).and(mask);
        inputVector.intoArray(output, 64);

        inputVector = raw.lanewise(VectorOperators.LSHR, 34).and(mask);
        inputVector.intoArray(output, 68);

        inputVector = raw.lanewise(VectorOperators.LSHR, 36).and(mask);
        inputVector.intoArray(output, 72);

        inputVector = raw.lanewise(VectorOperators.LSHR, 38).and(mask);
        inputVector.intoArray(output, 76);

        inputVector = raw.lanewise(VectorOperators.LSHR, 40).and(mask);
        inputVector.intoArray(output, 80);

        inputVector = raw.lanewise(VectorOperators.LSHR, 42).and(mask);
        inputVector.intoArray(output, 84);

        inputVector = raw.lanewise(VectorOperators.LSHR, 44).and(mask);
        inputVector.intoArray(output, 88);

        inputVector = raw.lanewise(VectorOperators.LSHR, 46).and(mask);
        inputVector.intoArray(output, 92);

        inputVector = raw.lanewise(VectorOperators.LSHR, 48).and(mask);
        inputVector.intoArray(output, 96);

        inputVector = raw.lanewise(VectorOperators.LSHR, 50).and(mask);
        inputVector.intoArray(output, 100);

        inputVector = raw.lanewise(VectorOperators.LSHR, 52).and(mask);
        inputVector.intoArray(output, 104);

        inputVector = raw.lanewise(VectorOperators.LSHR, 54).and(mask);
        inputVector.intoArray(output, 108);

        inputVector = raw.lanewise(VectorOperators.LSHR, 56).and(mask);
        inputVector.intoArray(output, 112);

        inputVector = raw.lanewise(VectorOperators.LSHR, 58).and(mask);
        inputVector.intoArray(output, 116);

        inputVector = raw.lanewise(VectorOperators.LSHR, 60).and(mask);
        inputVector.intoArray(output, 120);

        inputVector = raw.lanewise(VectorOperators.LSHR, 62).and(mask);
        inputVector.intoArray(output, 124);

    }

    public static void unpack1_128bit(long[] input, long[] output) {
        int shift = 0;
        LongVector inputVector = SPECIES.fromArray(input, 0).reinterpretAsLongs();
        LongVector outputVector1;
        LongVector outputVector2;
        LongVector outputVector3;
        LongVector outputVector4;
        LongVector outputVector5;
        LongVector outputVector6;
        LongVector outputVector7;
        LongVector outputVector8;
        long mask = (1L << 1) - 1;
        for (int i = 0; i < 8; i++) {
            outputVector1 = inputVector.lanewise(VectorOperators.LSHR, shift++).and(mask);
            outputVector2 = inputVector.lanewise(VectorOperators.LSHR, shift++).and(mask);
            outputVector3 = inputVector.lanewise(VectorOperators.LSHR, shift++).and(mask);
            outputVector4 = inputVector.lanewise(VectorOperators.LSHR, shift++).and(mask);
            outputVector5 = inputVector.lanewise(VectorOperators.LSHR, shift++).and(mask);
            outputVector6 = inputVector.lanewise(VectorOperators.LSHR, shift++).and(mask);
            outputVector7 = inputVector.lanewise(VectorOperators.LSHR, shift++).and(mask);
            outputVector8 = inputVector.lanewise(VectorOperators.LSHR, shift++).and(mask);
            outputVector1.intoArray(output, i * 16 + 0);
            outputVector2.intoArray(output, i * 16 + 2);
            outputVector3.intoArray(output, i * 16 + 4);
            outputVector4.intoArray(output, i * 16 + 6);
            outputVector5.intoArray(output, i * 16 + 8);
            outputVector6.intoArray(output, i * 16 + 10);
            outputVector7.intoArray(output, i * 16 + 12);
            outputVector8.intoArray(output, i * 16 + 14);
        }
    }

    public static void pack1_128bit_loop(long[] input, long[] output) {
        LongVector outputVector = SPECIES.zero().reinterpretAsLongs();
        long mask = (1L << 1) - 1;

        LongVector inputVector = LongVector.fromArray(SPECIES, input, 0).and(mask);
        outputVector = inputVector;
        inputVector = LongVector.fromArray(SPECIES, input, 2).and(mask);

        for (int i = 1; i <= 58; i += 4) {
            outputVector = inputVector.lanewise(VectorOperators.LSHL, i).or(outputVector);
            inputVector = LongVector.fromArray(SPECIES, input, i * 2 + 2).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, i + 1).or(outputVector);
            inputVector = LongVector.fromArray(SPECIES, input, (i + 1) * 2 + 2).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, i + 2).or(outputVector);
            inputVector = LongVector.fromArray(SPECIES, input, (i + 2) * 2 + 2).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, i + 3).or(outputVector);
            inputVector = LongVector.fromArray(SPECIES, input, (i + 3) * 2 + 2).and(mask);

        }

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 63).or(outputVector);
        outputVector.intoArray(output, 0);
    }


    public static void pack2_256bit(long[] input, long[] output) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 2) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = inputVector;
        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 2).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 4).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 6).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 8).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 10).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 12).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 14).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 16).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 18).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 20).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 22).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 24).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 26).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 28).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 30).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 32).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 34).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 36).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 38).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 40).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 42).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 44).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 46).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 48).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 50).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 52).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 54).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 56).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 58).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 60).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62).or(outputVector);
        outputVector.intoArray(output, 0);
    }


    public static void pack4_256bit(long[] input, long[] output) {
        long mask = (1L << 4) - 1;

        for (int i = 0; i < 2; i++) {

            LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
            LongVector inputVector = LongVector.fromArray(NORMAL, input, 0 + i * 64).and(mask);
            outputVector = inputVector;
            inputVector = LongVector.fromArray(NORMAL, input, 4 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 4).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 8 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 8).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 12 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 12).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 16 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 16).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 20 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 20).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 24 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 24).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 28 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 28).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 32 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 32).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 36 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 36).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 40 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 40).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 44 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 44).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 48 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 48).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 52 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 52).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 56 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 56).or(outputVector);
            inputVector = LongVector.fromArray(NORMAL, input, 60 + i * 64).and(mask);

            outputVector = inputVector.lanewise(VectorOperators.LSHL, 60).or(outputVector);
            outputVector.intoArray(output, i * 4);
        }


    }

    public static void pack5_256bit(long[] input, long[] output) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 5) - 1;
        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = inputVector;
        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 5).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 10).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 15).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 20).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 25).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 30).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 35).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 40).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 45).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 50).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 55).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 60).or(outputVector);
        outputVector.intoArray(output, 0);

        outputVector = inputVector.lanewise(VectorOperators.LSHR, 4);
        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 1).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 6).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 11).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 16).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 21).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 26).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 31).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 36).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 41).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 46).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 51).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 56).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 61).or(outputVector);
        outputVector.intoArray(output, 4);

        outputVector = inputVector.lanewise(VectorOperators.LSHR, 3);
        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 2).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 7).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 12).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 17).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 22).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 27).or(outputVector);
        outputVector.intoArray(output, 8);
    }

    public static void pack7_256bit(long[] input, long[] output) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 7) - 1;
        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = inputVector;
        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 7).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 14).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 21).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 28).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 35).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 42).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 49).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 56).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 63).or(outputVector);
        outputVector.intoArray(output, 0);

        outputVector = inputVector.lanewise(VectorOperators.LSHR, 1);
        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 6).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 13).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 20).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 27).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 34).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 41).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 48).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 55).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62).or(outputVector);

        outputVector.intoArray(output, 4);
        outputVector = inputVector.lanewise(VectorOperators.LSHR, 2);
        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 5).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 12).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 19).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 26).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 33).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 40).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 47).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 54).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 61).or(outputVector);
        outputVector.intoArray(output, 8);
        outputVector = inputVector.lanewise(VectorOperators.LSHR, 3);

        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 4).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 11).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 18).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 25).or(outputVector);
        outputVector.intoArray(output, 12);

    }

    public static void pack6_256bit(long[] input, long[] output) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 6) - 1;
        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = inputVector;
        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 6).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 12).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 18).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 24).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 30).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 36).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 42).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 48).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 54).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 60).or(outputVector);
        outputVector.intoArray(output, 0);

        outputVector = inputVector.lanewise(VectorOperators.LSHR, 4);
        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 2).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 8).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 14).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 20).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 26).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 32).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 38).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 44).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 50).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 56).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62).or(outputVector);
        outputVector.intoArray(output, 4);

        outputVector = inputVector.lanewise(VectorOperators.LSHR, 2);
        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 4).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 10).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 16).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 22).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 28).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 34).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 40).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 46).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 52).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 58).or(outputVector);
        outputVector.intoArray(output, 8);
    }

    public static void pack3_256bit(long[] input, long[] output) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 3) - 1;

        LongVector inputVector = LongVector.fromArray(NORMAL, input, 0).and(mask);
        outputVector = inputVector;
        inputVector = LongVector.fromArray(NORMAL, input, 4).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 3).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 8).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 6).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 12).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 9).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 16).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 12).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 20).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 15).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 24).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 18).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 28).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 21).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 32).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 24).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 36).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 27).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 40).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 30).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 44).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 33).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 48).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 36).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 52).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 39).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 56).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 42).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 60).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 45).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 64).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 48).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 68).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 51).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 72).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 54).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 76).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 57).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 80).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 60).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 84).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 63).or(outputVector);
        outputVector.intoArray(output, 0);

        outputVector = inputVector.lanewise(VectorOperators.LSHR, 1);
        inputVector = LongVector.fromArray(NORMAL, input, 88).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 2).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 92).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 5).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 96).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 8).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 100).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 11).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 104).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 14).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 108).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 17).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 112).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 20).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 116).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 23).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 120).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 26).or(outputVector);
        inputVector = LongVector.fromArray(NORMAL, input, 124).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 29).or(outputVector);
        outputVector.intoArray(output, 4);
    }

    public static void pack1_128bit(long[] input, long[] output) {
        LongVector outputVector = SPECIES.zero().reinterpretAsLongs();
        long mask = (1L << 1) - 1;

        LongVector inputVector = LongVector.fromArray(SPECIES, input, 0).and(mask);
        outputVector = inputVector;
        inputVector = LongVector.fromArray(SPECIES, input, 2).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 1).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 4).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 2).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 6).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 3).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 8).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 4).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 10).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 5).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 12).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 6).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 14).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 7).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 16).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 8).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 18).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 9).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 20).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 10).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 22).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 11).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 24).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 12).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 26).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 13).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 28).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 14).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 30).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 15).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 32).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 16).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 34).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 17).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 36).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 18).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 38).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 19).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 40).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 20).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 42).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 21).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 44).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 22).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 46).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 23).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 48).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 24).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 50).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 25).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 52).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 26).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 54).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 27).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 56).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 28).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 58).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 29).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 60).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 30).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 62).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 31).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 64).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 32).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 66).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 33).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 68).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 34).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 70).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 35).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 72).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 36).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 74).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 37).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 76).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 38).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 78).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 39).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 80).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 40).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 82).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 41).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 84).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 42).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 86).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 43).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 88).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 44).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 90).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 45).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 92).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 46).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 94).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 47).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 96).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 48).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 98).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 49).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 100).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 50).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 102).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 51).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 104).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 52).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 106).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 53).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 108).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 54).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 110).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 55).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 112).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 56).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 114).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 57).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 116).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 58).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 118).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 59).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 120).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 60).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 122).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 61).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 124).and(mask);


        outputVector = inputVector.lanewise(VectorOperators.LSHL, 62).or(outputVector);
        inputVector = LongVector.fromArray(SPECIES, input, 126).and(mask);

        outputVector = inputVector.lanewise(VectorOperators.LSHL, 63).or(outputVector);
        outputVector.intoArray(output, 0);

    }


    public static void pack8_256bit(long[] input, long[] output) {
        LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
        long mask = (1L << 8) - 1;

        for (int i = 0; i < 4; i++) {
            // 0 1 - 4
            LongVector inputVector = LongVector.fromArray(NORMAL, input, 0 + i * 32).and(mask);
            outputVector = inputVector;

            // 1 5 - 8
            inputVector = LongVector.fromArray(NORMAL, input, 4 + i * 32).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 8).or(outputVector);

            // 2 9 - 12
            inputVector = LongVector.fromArray(NORMAL, input, 8 + i * 32).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 16).or(outputVector);

            // 3 13
            inputVector = LongVector.fromArray(NORMAL, input, 12 + i * 32).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 24).or(outputVector);

            // 4
            inputVector = LongVector.fromArray(NORMAL, input, 16 + i * 32).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 32).or(outputVector);

            // 5
            inputVector = LongVector.fromArray(NORMAL, input, 20 + i * 32).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 40).or(outputVector);

            // 6
            inputVector = LongVector.fromArray(NORMAL, input, 24 + i * 32).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 48).or(outputVector);

//            // 7
            inputVector = LongVector.fromArray(NORMAL, input, 28 + i * 32).and(mask);
            outputVector = inputVector.lanewise(VectorOperators.LSHL, 56).or(outputVector);

            outputVector.intoArray(output, i * 4);
        }

    }
}
