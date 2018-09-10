package domain;

import java.io.Serializable;

public class Rank implements Comparable, Serializable {
    private String pathDoc;
    private Double value;

    public Rank(String pathDoc, Double value) {
        this.pathDoc = pathDoc;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "pathDoc='" + pathDoc + '\'' +
                ", value=" + value +
                '}';
    }
    @Override
    public int compareTo(Object comparestu) {
        Double tm=((Rank)comparestu).getValue()*100000000;
        Double tm2 = this.value*100000000;
        return tm.intValue()-tm2.intValue();

        /* For Descending order do like this */
        //return compareage-this.studentage;
    }
    public String getPathDoc() {
        return pathDoc;
    }

    public void setPathDoc(String pathDoc) {
        this.pathDoc = pathDoc;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
