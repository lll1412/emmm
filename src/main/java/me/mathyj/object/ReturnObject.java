package me.mathyj.object;

public class ReturnObject implements Object {
    public final Object retObj;

    public ReturnObject(Object retObj) {
        this.retObj = retObj;
    }

    @Override
    public ObjectType type() {
        return ObjectType.RETURN;
    }

    @Override
    public String value() {
        return retObj.value();
    }

    @Override
    public String toString() {
        return retObj.toString();
    }
}
