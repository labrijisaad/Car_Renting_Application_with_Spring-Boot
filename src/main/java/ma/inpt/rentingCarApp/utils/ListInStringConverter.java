package ma.inpt.rentingCarApp.utils;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListInStringConverter {

    public Set<Long> convertListInStringToSetInLong(String listInString) {
        Set<Long> converted = new LinkedHashSet<>();
        if (listInString.length() > 2) {
            String idsInString = listInString.substring(1, listInString.length() - 1);
            String[] idsStringArrayList = idsInString.split(", ");
            for (String id : idsStringArrayList) converted.add(Long.parseLong(id));
        }
        return converted;
    }
}
