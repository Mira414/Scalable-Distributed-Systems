import java.util.HashMap;
import java.util.Map;

public class Main {

  public static void main(String[] args) {
    int[] a = {1, 2};
    int[] b = {1, 2};
    Map<Character, Integer> map1 = new HashMap<>();
    map1.put('a', 1);
    map1.put('b', 1);
    Map<Character, Integer> map2 = new HashMap<>();
    map2.put('a', 1);
    map2.put('b', 1);

    System.out.println(a==b);
    System.out.println(map1.equals(map2));

    System.out.println(a);
  }

}
