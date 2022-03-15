import java.util.Random;

public class random {

    public static void main(String[] args) {
        Random r = new Random();
        long arr[] = new long[1000];

        for (int i = 0; i < 1000; i++) {
            arr[i] = r.nextLong(10000000);
        }

        for (int i = 0; i < 1000; i++) {
            for (int j = i+1; j < 1000; j++) {
                if (arr[i] == arr[j]){
                    System.out.println(arr[i] +","+ i +","+ j);
                    break;
                } 
                continue;
            }
        }
    }
}
