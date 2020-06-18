package bg.bulsi.egov.eauth.model;

public enum NamePrefixEnum {

    MR("Mr"),
    MS("Ms");

    private String value;

    NamePrefixEnum(String value) {
        this.value = value;
    }

    public static NamePrefixEnum fromValue(String text) {
        for (NamePrefixEnum b : NamePrefixEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @Override

    public String toString() {
        return String.valueOf(value);
    }
}

