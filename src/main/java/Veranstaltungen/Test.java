package Veranstaltungen;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Arten> kk = new ArrayList<>(Start.getArten());
        for (int i = 0; i < kk.size(); i++) {
            kk.get(i).getName();
        }
    }
}
