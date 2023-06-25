package life.tangdh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
//        print(3);
//        print(5);
//        for(int n = 10; n <= 32; n++) {
////            int n = ;
//            System.out.format("private static void encode%d(long[] input, int bitsPerValue, long[] out) {", n);
//            print(n);
//            System.out.format("}");
//        }
//        scalarGen(5);
        for (int i = 9; i <= 32; i++) {
            PrintBench(i);
        }
    }

    static void PrintBench(int n) {
        String bench = """
                @org.openjdk.jmh.annotations.Benchmark
                    public void encode%d(Blackhole bh) throws IOException {
                        forUtil.encode(input, %d, input);
                    }
                                
                    @org.openjdk.jmh.annotations.Benchmark
                    public void vectorizedEncode%d(Blackhole bh) throws IOException {
                        forUtilVectorized.encode(input, %d, input);
                    }
                """;

        System.out.format(bench, n, n, n, n);


    }


    /**
     *
     *
     */
    static void print(int bpv) {
        int runs = 0;
        if (bpv <= 8) {
            runs = 8;
        } else if (bpv <= 16) {
            runs = 16;
        } else {
            runs = 32;
        }

        List<List<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < 2 * runs; i++) {
            List<Integer> list = new ArrayList<>();
            lists.add(list);
        }
        int remaining = runs;
        int idx = 0;
        int round = 0;
        int num = 0;
        while (num < 128) {
           if (remaining >= bpv) {
               lists.get(idx).add(num);
               idx++;
           }

           if (idx == 2 * runs) {
               idx = 0;
           }
           num++;
        }
        remaining -= bpv;


        idx = 0;
        int next_run = 2 * bpv;
        while (remaining > bpv) {
            for(int i = 2 * bpv; i < 2 * runs; i++ ) {
                int pos = 1;
                for(int val : lists.get(i)) {
                    var l = lists.get(idx);
                    l.add(pos, val);
                    pos += 2;
                }
                idx++;
                next_run = i + 1;
                if (idx == 2 * bpv) {
                   break;
                }
            }
            remaining -= bpv;
        }

//        for (List<Integer> list : lists) {
//            System.out.println(list);
//        }

        String program4 = """
                    inputVector = LongVector.fromArray(NORMAL, input, %d).and(mask);
                    outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, %d));\n
                """;

        String InitProgram4 = """
                LongVector outputVector = NORMAL.zero().reinterpretAsLongs();
                long mask = (1L << %d) - 1;
                                
                LongVector inputVector = LongVector.fromArray(NORMAL, input, %d).and(mask);
                outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, %d));          
                """;

        String InitProgram4_1 = """
                outputVector = NORMAL.zero().reinterpretAsLongs();
                                
                inputVector = LongVector.fromArray(NORMAL, input, %d).and(mask);
                outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, %d));          
                """;
        String finalProgram4 = """
                outputVector.intoArray(out, %d);
                """;

        String program2 = """
                   (input[%d] << %d)
                """;

        String InitProgram2 = """
                out[%d] = 
                """;
        String finalProgram2 = """
                ;
                """;

        String tmpprogram4 = """
                    inputVector = LongVector.fromArray(NORMAL, input, %d).and(mask);
                    outputVector = outputVector.or(inputVector.lanewise(VectorOperators.LSHL, %d));\n
                    
                """;

        String tmpInitProgram4 = """
                outputVector = NORMAL.zero().reinterpretAsLongs();
                
                inputVector = LongVector.fromArray(NORMAL, input, %d).and(mask);
                outputVector = outputVector.or( inputVector.lanewise(VectorOperators.LSHL, %d));          
                
                """;
        String tmpfinalProgram4 = """
                outputVector.intoArray(tmp, %d);
                
                """;


        boolean first = true;

        for(int iter = 0; iter + 4 <= 2 * bpv; iter += 4) {
            var l = lists.get(iter);
            int blockremaining = runs - bpv;
            int times = 0;
            if (first) {
                System.out.println(String.format(InitProgram4, bpv, l.get(0), 64 - bpv));
                first = false;
            } else {
                System.out.println(String.format(InitProgram4_1,  l.get(0), 64 - bpv));
            }

            for(int i = 1; i < l.size(); i++) {
                if (blockremaining - bpv > 0) {
                    System.out.println(String.format(program4, l.get(i), 64 - bpv - times * runs - (runs - blockremaining)));
                    blockremaining -= bpv;
                } else {
                    times++;
                    blockremaining = runs - bpv;
                    System.out.println(String.format(program4, l.get(i), 64 - bpv - times * runs));
                }
            }

            System.out.format(finalProgram4, iter);
        }

        for(int iter = (2 * bpv / 4) * 4; iter  < 2 * bpv; iter ++) {
            var l = lists.get(iter);
            int blockremaining = runs ;
            int times = 0;
            System.out.println(String.format(InitProgram2,  iter));
            for(int i = 0; i < l.size(); i++) {
                if (blockremaining - bpv > 0) {
                    System.out.println(String.format(program2, l.get(i), 64 - bpv - times * runs - (runs - blockremaining)));
                    blockremaining -= bpv;
                } else {
                    times++;
                    blockremaining = runs - bpv;
                    System.out.println(String.format(program2, l.get(i), 64 - bpv - times * runs));
                }
                if (i != l.size() - 1) {
                    System.out.println("|");
                }
            }

            System.out.format(finalProgram2);
        }
        int next_run_copy  = next_run;
        for(; next_run + 4 <= 2 * runs; next_run += 4) {
            var l = lists.get(next_run);
            System.out.println(String.format(tmpInitProgram4,  l.get(0),  64 - runs));
            for(int i = 1; i < l.size(); i++) {
                System.out.println(String.format(tmpprogram4, l.get(i), 64 - runs * (i + 1)));
            }
            System.out.format(tmpfinalProgram4, next_run );
        }

        for(; next_run < 2 * runs; next_run ++) {
            printScalar(next_run, lists.get(next_run), runs, runs);
        }

        String end = """
                   final int remainingBitsPerLong = %d;
                   final long maskRemainingBitsPerLong = MASKS%d[remainingBitsPerLong];
                           
                   int tmpIdx = 0;
                   int idx = %d;
                                   int remainingBitsPerValue = bitsPerValue;
                                   while (idx < %d) {
                                       if (remainingBitsPerValue >= remainingBitsPerLong) {
                                           remainingBitsPerValue -= remainingBitsPerLong;
                                           out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                                           if (remainingBitsPerValue == 0) {
                                               idx++;
                                               remainingBitsPerValue = bitsPerValue;
                                           }
                                       } else {
                                           final long mask1, mask2;
                           
                                           mask1 = MASKS%d[remainingBitsPerValue];
                                           mask2 = MASKS%d[remainingBitsPerLong - remainingBitsPerValue];
                                           out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                                           remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                                           out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
                                       }
                                   }     
                """;

        System.out.format(end, runs % bpv, runs,  next_run_copy, 2 * runs , runs, runs);

    }


    static void printScalar(int result, List<Integer> l, int runs, int bpv) {
        String template = """
                tmp[%d] = 
                """;

        String assignment = """
                    (input[%d] << %d)
    """;

        String end = """
                ;
                """;
        int blockremaining = runs;
        int times = 0;
        System.out.println(String.format(template,  result));

        for(int i = 0; i < l.size(); i++) {
            if (blockremaining - bpv >= 0) {
                System.out.println(String.format(assignment, l.get(i), 64 - bpv - times * runs - (runs - blockremaining)));
                blockremaining -= bpv;
            } else {
                times++;
                blockremaining = runs - bpv;
                System.out.println(String.format(assignment, l.get(i), 64 - bpv - times * runs));
            }
            if (i != l.size() - 1) {
                System.out.println("|");
            }
        }

        System.out.format(end);

    }



    static void scalarGen(int bpv) {
        int runs = 0;
        if (bpv <= 8) {
            runs = 8;
        } else if (bpv <= 16) {
            runs = 16;
        } else {
            runs = 32;
        }

        List<List<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < 2 * runs; i++) {
            List<Integer> list = new ArrayList<>();
            lists.add(list);
        }
        int remaining = 8;
        int idx = 0;
        int round = 0;
        int num = 0;
        while (num < 128) {
            if (remaining >= bpv) {
                lists.get(idx).add(num);
                idx++;
            }

            if (idx == 2 * runs) {
                idx = 0;
            }
            num++;
        }
        remaining -= bpv;


        idx = 0;
        int next_run = 2 * bpv;
        while (remaining > bpv) {
            for(int i = 2 * bpv; i < 2 * runs; i++ ) {
                int pos = 1;
                for(int val : lists.get(i)) {
                    var l = lists.get(idx);
                    l.add(pos, val);
                    pos += 2;
                }
                idx++;
                next_run = i + 1;
                if (idx == 2 * bpv) {
                    break;
                }
            }
            remaining -= bpv;
        }

        for (List<Integer> list : lists) {
            System.out.println(list);
        }

        String result_template = "out[%d] = ";

        String assignment_template = """
                    (input[%d] << %d)
    """;
        String end = ";";

        for(int iter = 0; iter < 2 * bpv;  iter++) {
            var l = lists.get(iter);
            int blockremaining = runs ;
            int times = 0;
            System.out.println(String.format(result_template,  iter));
            for(int i = 0; i < l.size(); i++) {
                if (blockremaining - bpv > 0) {
                    System.out.println(String.format(assignment_template, l.get(i), 64 - bpv - times * runs - (runs - blockremaining)));
                    blockremaining -= bpv;
                } else {
                    times++;
                    blockremaining = runs - bpv;
                    System.out.println(String.format(assignment_template, l.get(i), 64 - bpv - times * runs));
                }
                if (i != l.size() - 1) {
                    System.out.println("|");
                }
            }

            System.out.format(end);
        }
        int next_run_copy = next_run;
        for(; next_run < 2 * runs; next_run ++) {
            printScalar(next_run, lists.get(next_run), runs, runs);
        }

        String end_statement = """
                   final int remainingBitsPerLong = %d;
                   final long maskRemainingBitsPerLong = MASKS%d[remainingBitsPerLong];
                           
                   int tmpIdx = 0;
                   int idx = %d;
                                   int remainingBitsPerValue = bitsPerValue;
                                   while (idx < %d) {
                                       if (remainingBitsPerValue >= remainingBitsPerLong) {
                                           remainingBitsPerValue -= remainingBitsPerLong;
                                           out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & maskRemainingBitsPerLong;
                                           if (remainingBitsPerValue == 0) {
                                               idx++;
                                               remainingBitsPerValue = bitsPerValue;
                                           }
                                       } else {
                                           final long mask1, mask2;
                           
                                           mask1 = MASKS%d[remainingBitsPerValue];
                                           mask2 = MASKS%d[remainingBitsPerLong - remainingBitsPerValue];
                                           out[tmpIdx] |= (tmp[idx++] & mask1) << (remainingBitsPerLong - remainingBitsPerValue);
                                           remainingBitsPerValue = bitsPerValue - remainingBitsPerLong + remainingBitsPerValue;
                                           out[tmpIdx++] |= (tmp[idx] >>> remainingBitsPerValue) & mask2;
                                       }
                                   }     
                """;

        System.out.format(end_statement, runs % bpv, runs,  next_run_copy, 2 * runs , runs, runs);
    }
}