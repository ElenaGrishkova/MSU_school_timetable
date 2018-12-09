package shedule;

public class KeyCell {

    private Integer period;
    private Classes clazz;

    public KeyCell(Integer period, Classes clazz) {
        this.period = period;
        this.clazz = clazz;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Classes getClazz() {
        return clazz;
    }

    public void setClazz(Classes clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyCell keyCell = (KeyCell) o;

        if (!period.equals(keyCell.period)) return false;
        return clazz.equals(keyCell.clazz);
    }

    @Override
    public int hashCode() {
        int result = period.hashCode();
        result = 31 * result + clazz.hashCode();
        return result;
    }
}
