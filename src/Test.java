import java.util.function.BinaryOperator;

public class Test {

    public static void main(String[] args) {
        BinaryOperator<Integer> plus = Integer::sum; //Reference de methode statique
        System.out.println(plus.apply(5,5));

        byte centVingt = 0b01111000;
        byte ten = 0b00001010;

        System.out.println(ten + centVingt);
    }
}
