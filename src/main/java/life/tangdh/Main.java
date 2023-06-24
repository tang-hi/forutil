package life.tangdh;

import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        long[] arr = new long[128];
        for(int i = 0; i < 128; i++) {
            arr[i] = i;
        }

        Random random = new Random();
        random.nextLong();
        for(int i = 0; i < 100; i++) {
            long[] input = new long[128];
            for(int j = 0; j < 128; j++) {
                input[j] = random.nextLong();
            }

            long[] inputCopy = Arrays.copyOfRange(input, 0, 128);

            ForUtil.expand8( input );

            ForUtilVectorized.expand8(inputCopy);

            if (!Arrays.equals(input, inputCopy)) {
                System.out.println("Error at index " + i );
                return;
            }
        }
        System.out.println("SUCCESS");
    }
}