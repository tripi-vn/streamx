package vn.vntravel.merging;

public enum TypeJoin {

    RJOIN("rjoin"),
    JOIN("join"),
    LJOIN("ljoin");

    private String type;

    TypeJoin(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TypeJoin fromType(String type) {
        for (TypeJoin t : TypeJoin.values()) {
            if (t.type.equals(type.toLowerCase()))
                return t;
        }
        return null;
    }
}
