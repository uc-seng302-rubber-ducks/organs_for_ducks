package odms.services;

import java.util.Comparator;

public class TimeRemainingComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        String[] splitStr1 = o1.split(" ");
        String[] splitStr2 = o2.split(" ");

        return Integer.valueOf(splitStr1[0] +
                (splitStr1[2].length() < 2 ? "0" + splitStr1[2] : splitStr1[2]) +
                (splitStr1[4].length() < 2 ? "0" + splitStr1[4] : splitStr1[4]))
                .compareTo(Integer.valueOf(splitStr2[0] +
                        (splitStr2[2].length() < 2 ? "0" + splitStr2[2] : splitStr2[2]) +
                        (splitStr2[4].length() < 2 ? "0" + splitStr2[4] : splitStr2[4])));
    }
}
