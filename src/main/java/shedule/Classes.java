package shedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Classes {
    CLASS_8("8 класс", 0, Arrays.asList("8 кл")),
    CLASS_9("9 класс", 0, Arrays.asList("9 кл")),
    EM_10("10 ЭКМАТ", 1,  Arrays.asList("10 МАТ-1", "10 МАТ-2")),
    ENJ_10("10 ИНЖ", 2, Arrays.asList("10 ИНЖ-1", "10 ИНЖ-2")),
    EN_10("10 ЕН", 3, Arrays.asList("10 ЕН-1", "10 ЕН-2")),
    IF_10("10 ИФ", 4, Arrays.asList("10 ИФ-1", "10 ИФ-2")),
    SP_10("10 СП", 5, Arrays.asList("10 СП-1", "10 СП-2", "10 СП-3")),

    EM_11("11 ЭКМАТ", 1,  Arrays.asList("11 ЭМ-1", "11 ЭМ-2")),
    ENJ_11("11 ИНЖ", 2, Arrays.asList("11 ИНЖ-1", "11 ИНЖ-2")),
    EN_11("11 ЕН", 3, Arrays.asList("11 ЕН-1", "11 ЕН-2")),
    IF_11("11 ИФ", 4, Arrays.asList("11 ИФ-1", "11 ИФ-2")),
    SP_11("11 СП", 5, Arrays.asList("11 СП-1", "11 СП-3")),
    ALL_10("10 ALL", 0, new ArrayList<String>()),
    ALL_11("11 ALL", 0, new ArrayList<String>())

    ;

    private static List<Classes> class_10 = Arrays.asList(EM_10, ENJ_10, EN_10, IF_10, SP_10);
    private static List<Classes> class_11 = Arrays.asList(EM_11, ENJ_11, EN_11, IF_11, SP_11);
    private String general;
    private Short index;
    private List<String> aliases;
    Classes(String general, int index, List<String> aliases) {
        this.aliases = aliases;
        this.general = general;
        this.index = (short)index;
    }

    public static Classes getByAlias(String alias) throws Exception {
        for (Classes clazz : Classes.values()) {
            if (clazz.aliases.contains(alias)) {
                return clazz;
            }
        }
        throw new Exception("Not found class Enum");
    }

    public static Boolean checkAllIn(List<String> list) {
        for (Classes clazz : Classes.values()) {
            if (clazz.aliases.contains(list.get(0))) {
                return list.containsAll(clazz.aliases);
            }
        }
        return false;
    }

    public String getGeneral() {
        return general;
    }

    public static Boolean is10(String className) {
        for (Classes clazz : Classes.values()) {
            if (clazz.general.equals(className)) {
                return class_10.contains(clazz);
            }
        }
        return false;
    }

    public static Boolean is11(String className) {
        for (Classes clazz : Classes.values()) {
            if (clazz.general.equals(className)) {
                return class_11.contains(clazz);
            }
        }
        return false;
    }

    public Short getIndex() {
        return index;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
